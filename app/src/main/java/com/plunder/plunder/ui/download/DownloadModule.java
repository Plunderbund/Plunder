package com.plunder.plunder.ui.download;

import android.support.annotation.NonNull;
import com.plunder.plunder.torrents.TorrentManager;
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
      TorrentManager torrentManager) {
    return new DownloadPresenterImpl(view, eventBus, torrentManager);
  }
}
