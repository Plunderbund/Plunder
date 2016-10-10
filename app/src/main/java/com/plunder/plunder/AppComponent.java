package com.plunder.plunder;

import android.app.Application;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.DomainComponent;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.downloads.DownloadManager;
import com.plunder.plunder.downloads.DownloadModule;
import com.plunder.plunder.executors.ExecutorModule;
import com.plunder.plunder.executors.MainThreadExecutor;
import com.plunder.plunder.executors.ThreadExecutor;
import com.plunder.plunder.player.PlayerModule;
import com.plunder.plunder.providers.ProviderManager;
import com.plunder.plunder.providers.ProviderModule;
import com.plunder.plunder.ui.common.BaseActivity;
import com.plunder.plunder.update.UpdateManager;
import com.plunder.plunder.update.UpdateModule;
import com.squareup.leakcanary.RefWatcher;
import dagger.Component;
import org.greenrobot.eventbus.EventBus;
import org.videolan.libvlc.LibVLC;

@AppScope @Component(
    dependencies = DomainComponent.class,
    modules = {
        AppModule.class, ProviderModule.class, ExecutorModule.class, DownloadModule.class,
        PlayerModule.class, UpdateModule.class,
    }) public interface AppComponent {
  void inject(App app);

  void inject(BaseActivity activity);

  Application getApplication();

  @Nullable RefWatcher getRefWatcher();

  EventBus getEventBus();

  PackageManager getPackageManager();

  CatalogManager getCatalogManager();

  ProviderManager getProviderManager();

  ThreadExecutor getThreadExecutor();

  MainThreadExecutor getMainThreadExecutor();

  DownloadManager getDownloadManager();

  LibVLC getLibVLC();

  UpdateManager getUpdateManager();
}
