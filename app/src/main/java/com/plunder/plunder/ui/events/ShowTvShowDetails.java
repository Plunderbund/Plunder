package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.TvShow;

public class ShowTvShowDetails {
  private final TvShow tvShow;

  public ShowTvShowDetails(TvShow tvShow) {
    this.tvShow = tvShow;
  }

  public TvShow getTvShow() {
    return tvShow;
  }
}
