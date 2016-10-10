package com.plunder.plunder.providers;

import com.plunder.plunder.AppScope;
import com.plunder.plunder.executors.MainThreadExecutor;
import com.plunder.plunder.executors.ThreadExecutor;
import dagger.Module;
import dagger.Provides;

@Module public class ProviderModule {
  @Provides @AppScope public ProviderManager provideProviderManager(ThreadExecutor threadExecutor,
      MainThreadExecutor mainThreadExecutor) {
    return new ProviderManager(threadExecutor, mainThreadExecutor);
  }
}
