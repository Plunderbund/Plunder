package com.plunder.plunder.ui.download;

import android.support.annotation.NonNull;
import com.plunder.plunder.downloads.DownloadManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class DownloadModule {
  @NonNull private final WeakReference<DownloadView> viewRef;

  public DownloadModule(DownloadView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public DownloadView provideView() {
    return viewRef.get();
  }

  @Provides public DownloadPresenter providePresenter(DownloadView view, EventBus eventBus,
      DownloadManager downloadManager) {
    return new DownloadPresenterImpl(view, eventBus, downloadManager);
  }
}
