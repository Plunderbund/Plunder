package com.plunder.plunder.executors;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;

public class MainThreadExecutor {
  private final Handler handler;

  public MainThreadExecutor() {
    this.handler = new Handler(Looper.getMainLooper());
  }

  public void execute(@NonNull Runnable runnable) {
    Preconditions.checkNotNull(runnable);
    handler.post(runnable);
  }
}
