package com.plunder.plunder.update;

import com.plunder.plunder.AppScope;
import dagger.Module;
import dagger.Provides;

@Module public class UpdateModule {
  @Provides @AppScope public UpdateManager provideUpdateManager() {
    return new GitHubUpdateManager();
  }
}
