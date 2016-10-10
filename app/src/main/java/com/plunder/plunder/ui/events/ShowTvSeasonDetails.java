package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;

public class ShowTvSeasonDetails {
  private final TvShow tvShow;
  private final TvSeason tvSeason;

  public ShowTvSeasonDetails(TvShow tvShow, TvSeason tvSeason) {
    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
  }

  public TvShow getTvShow() {
    return tvShow;
  }

  public TvSeason getTvSeason() {
    return tvSeason;
  }
}
