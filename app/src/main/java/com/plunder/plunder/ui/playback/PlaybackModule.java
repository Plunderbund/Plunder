package com.plunder.plunder.ui.playback;

import android.support.annotation.NonNull;
import com.plunder.plunder.torrents.TorrentManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class PlaybackModule {
  @NonNull private final WeakReference<PlaybackView> viewRef;

  public PlaybackModule(PlaybackView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public PlaybackView provideView() {
    return viewRef.get();
  }

  @Provides public PlaybackPresenter providePresenter(PlaybackView view, EventBus eventBus,
      TorrentManager torrentManager) {
    return new PlaybackPresenterImpl(view, eventBus, torrentManager);
  }
}
