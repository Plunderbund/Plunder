package com.plunder.plunder.ui.playback;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = PlaybackModule.class) public interface PlaybackComponent {
  void inject(PlaybackFragment fragment);
}
