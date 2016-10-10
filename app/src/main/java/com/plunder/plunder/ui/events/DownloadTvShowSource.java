package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.provider.search.SearchResult;

public class DownloadTvShowSource {
  private final TvShow tvShow;
  private final TvSeason tvSeason;
  private final TvEpisode tvEpisode;
  private final SearchResult searchResult;

  public DownloadTvShowSource(TvShow tvShow, TvSeason tvSeason, TvEpisode tvEpisode,
      SearchResult searchResult) {
    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
    this.tvEpisode = tvEpisode;
    this.searchResult = searchResult;
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

  public SearchResult getSearchResult() {
    return searchResult;
  }
}
