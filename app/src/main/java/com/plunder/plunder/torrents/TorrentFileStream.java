package com.plunder.plunder.torrents;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TorrentFileStream extends FilterInputStream {
  private TorrentClient client;
  private volatile boolean alive;

  public TorrentFileStream(TorrentClient torrentClient, InputStream in) {
    super(in);
    alive = true;
    client = torrentClient;
  }

  @Override protected void finalize() throws Throwable {
    alive = false;
    super.finalize();
  }

  @Override public int read(byte[] b, int off, int len) throws IOException {
    client.setInterested(off, len);

    while (alive) {
      if (client.hasBytes(off, len)) {
        break;
      }
    }

    if (!alive) {
      return -1;
    }

    return super.read(b, off, len);
  }

  @Override public void close() throws IOException {
    alive = false;
    super.close();
  }
}
