package com.plunder.plunder.downloads;

import android.support.annotation.Nullable;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DownloadClient {
  private List<WeakReference<Listener>> listeners;
  private UUID id;
  private String url;

  public DownloadClient() {
    listeners = new ArrayList<>();
  }

  public abstract void start();

  public abstract void stop();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public abstract float getProgress();

  public abstract int getBufferProgress();

  public abstract int getSeeds();

  public abstract float getDownloadSpeed();

  public abstract @Nullable File getFile();

  public void addListener(Listener listener) {
    listeners.add(new WeakReference<>(listener));
  }

  public void removeListener(Listener listener) {
    listeners = Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null && item != listener)
        .map(WeakReference::new)
        .collect(Collectors.toList());
  }

  protected void onPrepared() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onPrepared(this));
  }

  protected void onStarted() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onStarted(this));
  }

  protected void onError() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onError(this));
  }

  protected void onReady() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onReady(this));
  }

  protected void onProgress() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onProgress(this));
  }

  protected void onStopped() {
    Stream.of(listeners)
        .map(WeakReference::get)
        .filter(item -> item != null)
        .forEach(item -> item.onStopped(this));
  }

  public interface Listener {
    void onPrepared(DownloadClient client);

    void onStarted(DownloadClient client);

    void onError(DownloadClient client);

    void onReady(DownloadClient client);

    void onProgress(DownloadClient client);

    void onStopped(DownloadClient client);
  }
}
