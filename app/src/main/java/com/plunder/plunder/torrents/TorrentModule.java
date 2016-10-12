package com.plunder.plunder.torrents;

import android.app.Application;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.exceptions.NotInitializedException;
import com.plunder.plunder.AppScope;
import com.plunder.plunder.executors.ThreadExecutor;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import javax.inject.Named;
import javax.inject.Provider;

@Module public class TorrentModule {
  @Provides @AppScope @Named("TorrentDirectory") String provideTorrentDirectory(
      Application application) {
    return new File(application.getCacheDir(), "downloads").getAbsolutePath();
  }

  @Provides @AppScope @Named("MaxUploadSpeed") int provideMaximumUploadSpeed() {
    return 100000;
  }

  @Provides @AppScope TorrentOptions provideTorrentOptions(
      @Named("TorrentDirectory") String torrentDirectory,
      @Named("MaxUploadSpeed") int maxUploadSpeed) {
    return new TorrentOptions.Builder().saveLocation(torrentDirectory)
        .removeFilesAfterStop(true)
        .maxUploadSpeed(maxUploadSpeed)
        .build();
  }

  @Provides TorrentStream provideTorrentStream(TorrentOptions options) {
    try {
      return TorrentStream.getInstance();
    } catch (NotInitializedException e) {
      return TorrentStream.init(options);
    }
  }

  @Provides TorrentClient provideDownloadClient(TorrentStream torrentStream) {
    return new TorrentStreamClient(torrentStream);
  }

  @Provides @AppScope TorrentManager provideDownloadManager(
      @Named("TorrentDirectory") String torrentDirectory, Provider<TorrentClient> clientProvider,
      ThreadExecutor threadExecutor) {
    return new TorrentManager(torrentDirectory, clientProvider, threadExecutor);
  }
}
