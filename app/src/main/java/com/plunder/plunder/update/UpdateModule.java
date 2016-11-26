package com.plunder.plunder.update;

import android.app.Application;
import android.os.Environment;
import com.plunder.plunder.AppScope;
import com.plunder.plunder.github.GithubManager;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Named;
import okhttp3.OkHttpClient;

@Module public class UpdateModule {
  @Provides @AppScope @Named("UpdateDirectory") String provideUpdateDirectory() {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .getAbsolutePath();
  }

  @Provides @AppScope public UpdateManager provideUpdateManager(OkHttpClient httpClient,
      GithubManager githubManager, @Named("UpdateDirectory") String updateDirectory) {
    return new GitHubUpdateManager(httpClient, githubManager, updateDirectory);
  }
}
