package com.plunder.plunder.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.common.base.Preconditions;
import com.plunder.plunder.executors.MainThreadExecutor;
import com.plunder.plunder.executors.ThreadExecutor;

public class ProviderManager {
  private final ThreadExecutor threadExecutor;
  private final MainThreadExecutor mainThreadExecutor;

  public ProviderManager(ThreadExecutor threadExecutor, MainThreadExecutor mainThreadExecutor) {
    this.threadExecutor = threadExecutor;
    this.mainThreadExecutor = mainThreadExecutor;
  }

  public ProviderClient createClient(@NonNull Context context) {
    Preconditions.checkNotNull(context);

    return new ProviderClient(context, threadExecutor, mainThreadExecutor);
  }
}
