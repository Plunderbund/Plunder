package com.plunder.plunder.ui.common;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

public abstract class BasePresenter<V> implements Presenter {
  private final WeakReference<V> viewRef;
  private final Handler handler;

  protected final EventBus eventBus;

  public BasePresenter(@NonNull V view, EventBus eventBus) {
    viewRef = new WeakReference<>(view);
    handler = new Handler(Looper.getMainLooper());
    this.eventBus = eventBus;
  }

  protected @Nullable V getView() {
    return viewRef.get();
  }

  protected void runOnView(Runnable runnable) {
    handler.post(runnable);
  }
}
