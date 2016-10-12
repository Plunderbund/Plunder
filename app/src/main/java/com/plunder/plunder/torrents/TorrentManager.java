package com.plunder.plunder.torrents;

import android.support.annotation.Nullable;
import com.plunder.plunder.executors.ThreadExecutor;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Provider;
import org.apache.commons.io.FileUtils;
import timber.log.Timber;

public class TorrentManager {
  private final String torrentDirectory;
  private final Provider<TorrentClient> clientProvider;
  private final Map<UUID, TorrentClient> clients;
  private final ThreadExecutor threadExecutor;

  public TorrentManager(String torrentDirectory, Provider<TorrentClient> clientProvider,
      ThreadExecutor threadExecutor) {
    this.torrentDirectory = torrentDirectory;
    this.clientProvider = clientProvider;
    this.threadExecutor = threadExecutor;
    clients = new ConcurrentHashMap<>();
    cleanDirectory();
  }

  private void cleanDirectory() {
    File directory = new File(torrentDirectory);

    if (directory.exists() && directory.isDirectory()) {
      threadExecutor.run(() -> {
        try {
          FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
          Timber.w(e, "Failed to clean torrent directory");
        }
      });
    }
  }

  public TorrentClient create(String url) {
    UUID id;

    do {
      id = createId();
    } while (clients.containsKey(id));

    TorrentClient client = clientProvider.get();
    client.setId(id);
    client.setUrl(url);
    clients.put(id, client);

    return client;
  }

  public @Nullable TorrentClient getClientById(UUID id) {
    if (clients.containsKey(id)) {
      return clients.get(id);
    }

    return null;
  }

  private static UUID createId() {
    return UUID.randomUUID();
  }
}
