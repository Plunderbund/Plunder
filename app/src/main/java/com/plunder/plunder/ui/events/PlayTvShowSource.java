package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import java.util.UUID;

public class PlayTvShowSource {
  private final UUID downloadId;
  private final TvShow tvShow;
  private final TvSeason tvSeason;
  private final TvEpisode tvEpisode;

  public PlayTvShowSource(UUID downloadId, TvShow tvShow, TvSeason tvSeason, TvEpisode tvEpisode) {
    this.downloadId = downloadId;
    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
    this.tvEpisode = tvEpisode;
  }

  public UUID getDownloadId() {
    return downloadId;
  }

  public TvShow getTvShow() {
    return tvShow;
  }

  public TvSeason getTvSeason() {
    return tvSeason;
  }

  public TvEpisode getTvEpisode() {
    return tvEpisode;
  }
}
