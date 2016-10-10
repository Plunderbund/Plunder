package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;

public class ShowTvShowSources {
  private final TvShow tvShow;
  private final TvSeason tvSeason;
  private final TvEpisode tvEpisode;

  public ShowTvShowSources(TvShow tvShow, TvSeason tvSeason, TvEpisode tvEpisode) {
    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
    this.tvEpisode = tvEpisode;
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
