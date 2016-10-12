package com.plunder.plunder;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import com.plunder.plunder.domain.DaggerDomainComponent;
import com.plunder.plunder.domain.DomainComponent;
import com.plunder.plunder.domain.catalog.CatalogModule;
import com.plunder.plunder.torrents.TorrentModule;
import com.plunder.plunder.executors.ExecutorModule;
import com.plunder.plunder.player.PlayerModule;
import com.plunder.plunder.providers.ProviderModule;
import com.plunder.plunder.update.UpdateModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import timber.log.Timber;

public class App extends Application {
  private AppComponent appComponent;
  private RefWatcher refWatcher;

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);

    MultiDex.install(this);
  }

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      refWatcher = LeakCanary.install(this);
      Timber.plant(new Timber.DebugTree());

      new Handler().post(() -> {
        StrictMode.setThreadPolicy(
            new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
      });
    }

    createComponents();
    appComponent.inject(this);
  }

  private void createComponents() {
    DomainComponent domainComponent =
        DaggerDomainComponent.builder().catalogModule(new CatalogModule()).build();

    appComponent = DaggerAppComponent.builder()
        .domainComponent(domainComponent)
        .appModule(new AppModule(this))
        .providerModule(new ProviderModule())
        .executorModule(new ExecutorModule())
        .torrentModule(new TorrentModule())
        .playerModule(new PlayerModule())
        .updateModule(new UpdateModule())
        .build();
  }

  public AppComponent getAppComponent() {
    return appComponent;
  }

  @Nullable public RefWatcher getRefWatcher() {
    return refWatcher;
  }

  @Nullable public static AppComponent getAppComponent(Activity activity) {
    if (activity == null) {
      return null;
    }

    Application application = activity.getApplication();
    return getAppComponent(application);
  }

  @Nullable public static AppComponent getAppComponent(Application application) {
    if (application == null) {
      return null;
    }

    if (application instanceof App) {
      App app = (App) application;
      return app.getAppComponent();
    }

    return null;
  }
}
