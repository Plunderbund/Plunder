package com.plunder.plunder.executors;

import com.plunder.plunder.AppScope;
import dagger.Module;
import dagger.Provides;

@Module public class ExecutorModule {
  @Provides @AppScope ThreadExecutor provideThreadExecutor() {
    return new ThreadExecutor();
  }

  @Provides @AppScope MainThreadExecutor provideMainThreadExecutor() {
    return new MainThreadExecutor();
  }
}
