package com.plunder.plunder;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.squareup.leakcanary.RefWatcher;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.EventBus;

@Module public class AppModule {
  private final App app;

  public AppModule(@NonNull App app) {
    this.app = app;
  }

  @Provides @AppScope Application provideApplication() {
    return app;
  }

  @Nullable @Provides @AppScope RefWatcher provideRefWatcher() {
    return app.getRefWatcher();
  }

  @Provides @AppScope EventBus provideEventBus() {
    return EventBus.getDefault();
  }

  @Provides @AppScope PackageManager providePackageManager() {
    return app.getPackageManager();
  }

  @Provides @AppScope DownloadManager provideDownloadManager() {
    return (DownloadManager) app.getSystemService(Context.DOWNLOAD_SERVICE);
  }

  @Provides @AppScope OkHttpClient provideOkHttpClient() {
    return new OkHttpClient();
  }
}
