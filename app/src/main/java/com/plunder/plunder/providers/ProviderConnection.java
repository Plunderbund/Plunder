package com.plunder.plunder.providers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.SearchResults;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.provider.PlunderConstants;
import com.plunder.provider.ipc.ProviderInterface;
import com.plunder.provider.search.MovieSearchRequest;
import com.plunder.provider.search.SearchResult;
import com.plunder.provider.search.TvSearchRequest;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rx.Observable;
import timber.log.Timber;

public class ProviderConnection implements ServiceConnection {
  private final WeakReference<Context> contextRef;
  private final ComponentName componentName;
  private ProviderInterface providerInterface;
  private boolean bound;

  public ProviderConnection(Context context, ComponentName componentName) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(componentName);

    this.contextRef = new WeakReference<>(context);
    this.componentName = componentName;
  }

  public void bind() {
    if (isBound()) {
      return;
    }

    Context context = contextRef.get();

    if (context != null) {
      Intent intent = createStartIntent();
      intent.setComponent(componentName);

      if (context.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
        bound = true;
      }
    }
  }

  public void unbind() {
    if (!isBound()) {
      return;
    }

    Context context = contextRef.get();

    if (context != null) {
      context.unbindService(this);
      bound = false;
    }
  }

  public boolean isBound() {
    return bound;
  }

  public boolean isConnected() {
    if (providerInterface != null) {
      return true;
    }

    return false;
  }

  public boolean isReady() {
    return isBound() && isConnected();
  }

  @Override public final void onServiceConnected(ComponentName name, IBinder binder) {
    Timber.d("Provider \"%s\" connected", name.toString());
    providerInterface = ProviderInterface.Stub.asInterface(binder);
  }

  @Override public final void onServiceDisconnected(ComponentName name) {
    Timber.d("Provider \"%s\" disconnected", name.toString());
    providerInterface = null;
  }

  public Observable<SearchResults> search(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);

    return Observable.defer(() -> {
      if (!isReady()) {
        Throwable exception = new ProviderNotConnectedException("The provider is not ready");
        return Observable.error(exception);
      }

      MovieSearchRequest request = new MovieSearchRequest.Builder().name(movie.name())
          .year(movie.releaseDate() != null ? movie.releaseDate().getYear() : null)
          .imdbId(movie.imdbId())
          .build();
      List<SearchResult> results;
      String providerName;

      try {
        providerName = providerInterface.getName();
        results = providerInterface.searchMovies(request);
      } catch (RemoteException e) {
        Timber.e(e, "provider '%s' threw an exception when searching",
            componentName.getShortClassName());
        return Observable.error(e);
      }

      if (results == null) {
        results = new ArrayList<>();
      }

      SearchResults providerResults =
          SearchResults.Builder().results(results).provider(providerName).build();
      return Observable.just(providerResults);
    });
  }

  public Observable<SearchResults> search(@NonNull TvShow tvShow, @NonNull TvEpisode tvEpisode) {
    Preconditions.checkNotNull(tvShow);
    Preconditions.checkNotNull(tvEpisode);

    return Observable.defer(() -> {
      if (!isReady()) {
        Throwable exception = new ProviderNotConnectedException("The provider is not ready");
        return Observable.error(exception);
      }

      String query = String.format(Locale.getDefault(), "%s S%02dE%02d", tvShow.name(),
          tvEpisode.seasonNumber(), tvEpisode.episodeNumber());

      TvSearchRequest request = new TvSearchRequest.Builder().name(tvShow.name())
          .season(tvEpisode.seasonNumber())
          .episode(tvEpisode.episodeNumber())
          .query(query)
          .build();

      List<SearchResult> results;
      String providerName;

      try {
        providerName = providerInterface.getName();
        results = providerInterface.searchTvShows(request);
      } catch (RemoteException e) {
        Timber.e(e, "provider '%s' threw an exception when searching",
            componentName.getShortClassName());
        return Observable.error(e);
      }

      if (results == null) {
        results = new ArrayList<>();
      }

      SearchResults providerResults =
          SearchResults.Builder().results(results).provider(providerName).build();
      return Observable.just(providerResults);
    });
  }

  public static Intent createStartIntent() {
    return new Intent(PlunderConstants.ACTION_START_PROVIDER);
  }
}