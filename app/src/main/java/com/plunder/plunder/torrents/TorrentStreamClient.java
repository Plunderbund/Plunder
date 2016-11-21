package com.plunder.plunder.torrents;

import android.support.annotation.Nullable;
import com.frostwire.jlibtorrent.Priority;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.alerts.Alert;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentStream;
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import timber.log.Timber;

public class TorrentStreamClient extends TorrentClient implements TorrentListener {
  private final TorrentStream torrentStream;
  private Torrent torrent;
  private StreamStatus streamStatus;

  public TorrentStreamClient(TorrentStream torrentStream) {
    this.torrentStream = torrentStream;
    torrentStream.addListener(this);
  }

  @Override public void start() {
    if (!torrentStream.isStreaming()) {
      torrentStream.startStream(getUrl());
    }
  }

  @Override public void stop() {
    if (torrentStream.isStreaming()) {
      torrentStream.stopStream();
    }
  }

  @Override public float getProgress() {
    if (streamStatus != null) {
      return streamStatus.progress;
    }

    return 0;
  }

  @Override public int getBufferProgress() {
    if (streamStatus != null) {
      return streamStatus.bufferProgress;
    }

    return 0;
  }

  @Override public int getSeeds() {
    if (streamStatus != null) {
      return streamStatus.seeds;
    }

    return 0;
  }

  @Override public float getDownloadSpeed() {
    if (streamStatus != null) {
      return streamStatus.downloadSpeed;
    }

    return 0;
  }

  @Override @Nullable public File getFile() {
    if (torrent != null) {
      return torrent.getVideoFile();
    }

    return null;
  }

  @Override @Nullable public InputStream getInputStream() {
    if (torrent != null) {
      try {
        return torrent.getVideoStream();
      } catch (FileNotFoundException e) {
        Timber.e(e, "Failed to load video stream");
        return null;
      }
    }

    return null;
  }

  @Override public void setDownloadOffset(long bytes) {
    torrent.setInterestedBytes(bytes);
  }

  @Override public void onStreamPrepared(Torrent torrent) {
    this.torrent = torrent;
    onPrepared();

    torrent.startDownload();
  }

  @Override public void onStreamStarted(Torrent torrent) {
    onStarted();
  }

  @Override public void onStreamError(Torrent torrent, Exception e) {
    onError();
  }

  @Override public void onStreamReady(Torrent torrent) {
    onReady();
  }

  @Override public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {
    this.streamStatus = streamStatus;
    onProgress();
  }

  @Override public void onStreamStopped() {
    onStopped();
  }
}
