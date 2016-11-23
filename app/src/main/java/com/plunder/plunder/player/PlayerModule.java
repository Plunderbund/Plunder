package com.plunder.plunder.player;

import android.app.Application;
import com.plunder.plunder.AppScope;
import dagger.Module;
import dagger.Provides;
import org.videolan.libvlc.LibVLC;

@Module public class PlayerModule {
  @Provides @AppScope LibVLC provideLibVLC() {
    return new LibVLC();
  }
}
