package com.plunder.plunder.providers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.SearchResults;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.executors.MainThreadExecutor;
import com.plunder.plunder.executors.ThreadExecutor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import timber.log.Timber;

public class ProviderClient {
  private final static int TIMEOUT_READY = 10000;
  private final static int TIMEOUT_SEARCH = 10000;

  private final ThreadExecutor threadExecutor;
  private final MainThreadExecutor mainThreadExecutor;
  private final WeakReference<Context> contextRef;
  private List<ProviderConnection> connections;
  private WeakReference<Listener> listenerRef;
  private boolean ready;
  private Future<?> readinessFuture;

  public ProviderClient(Context context, ThreadExecutor threadExecutor,
      MainThreadExecutor mainThreadExecutor) {
    this.threadExecutor = threadExecutor;
    this.mainThreadExecutor = mainThreadExecutor;
    contextRef = new WeakReference<>(context);
    connections = new ArrayList<>();
  }

  private @NonNull List<ComponentName> findProviders() {
    Context context = contextRef.get();

    if (context == null) {
      return new ArrayList<>();
    }

    Intent intent = ProviderConnection.createStartIntent();
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> intentServices =
        packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL);

    if (intentServices == null) {
      return new ArrayList<>();
    }

    return Stream.of(intentServices)
        .map(service -> new ComponentName(service.serviceInfo.applicationInfo.packageName,
            service.serviceInfo.name))
        .collect(Collectors.toList());
  }

  private void createConnections() {
    Context context = contextRef.get();

    if (context == null) {
      return;
    }

    if (hasProviders()) {
      unbind();
    }

    connections = Stream.of(findProviders())
        .map(provider -> new ProviderConnection(context, provider))
        .collect(Collectors.toList());
  }

  private void cancelReadiness() {
    if (readinessFuture != null && !readinessFuture.isDone()) {
      readinessFuture.cancel(true);
      readinessFuture = null;
    }
  }

  private void awaitReadiness() {
    cancelReadiness();
    final long startTime = System.currentTimeMillis();

    readinessFuture = threadExecutor.run(() -> {
      for (; ; ) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - startTime > TIMEOUT_READY) {
          onClientReady();
          break;
        }

        long readyCount = Stream.of(connections).filter(ProviderConnection::isReady).count();

        if (readyCount == connections.size()) {
          onClientReady();
          break;
        }

        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          return;
        }
      }
    });
  }

  private void onClientReady() {
    ready = true;
    Listener listener = listenerRef.get();

    if (listener != null) {
      mainThreadExecutor.execute(listener::onClientReady);
    }
  }

  private void onClientUnbound() {
    cancelReadiness();
    ready = false;
  }

  public void setListener(@Nullable Listener listener) {
    listenerRef = new WeakReference<>(listener);
  }

  public boolean hasProviders() {
    if (connections.size() > 0) {
      return true;
    }

    return false;
  }

  public void bind() {
    createConnections();
    Stream.of(connections).forEach(ProviderConnection::bind);
    awaitReadiness();
  }

  public void unbind() {
    Stream.of(connections).filter(ProviderConnection::isBound).forEach(ProviderConnection::unbind);
    connections.clear();
    onClientUnbound();
  }

  public Observable<SearchResults> search(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);

    if (!ready) {
      return Observable.error(new ProviderClientNotReadyException());
    }

    return Observable.from(connections)
        .concatMap(
            connection -> connection.search(movie).timeout(TIMEOUT_SEARCH, TimeUnit.MILLISECONDS))
        .onErrorResumeNext(err -> {
          Timber.e(err, "Failed to search connection");
          return Observable.empty();
        });
  }

  public Observable<SearchResults> search(@NonNull TvShow tvShow, TvEpisode tvEpisode) {
    Preconditions.checkNotNull(tvShow);
    Preconditions.checkNotNull(tvEpisode);

    if (!ready) {
      return Observable.error(new ProviderClientNotReadyException());
    }

    return Observable.from(connections)
        .concatMap(connection -> connection.search(tvShow, tvEpisode)
            .timeout(TIMEOUT_SEARCH, TimeUnit.MILLISECONDS))
        .onErrorResumeNext(err -> {
          Timber.e(err, "Failed to search connection");
          return Observable.empty();
        });
  }

  public interface Listener {
    void onClientReady();
  }
}
