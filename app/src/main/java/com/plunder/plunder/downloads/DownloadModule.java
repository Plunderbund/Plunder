package com.plunder.plunder.downloads;

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

@Module public class DownloadModule {
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

  @Provides DownloadClient provideDownloadClient(TorrentStream torrentStream) {
    return new TorrentStreamClient(torrentStream);
  }

  @Provides @AppScope DownloadManager provideDownloadManager(
      @Named("TorrentDirectory") String torrentDirectory, Provider<DownloadClient> clientProvider,
      ThreadExecutor threadExecutor) {
    return new DownloadManager(torrentDirectory, clientProvider, threadExecutor);
  }
}
