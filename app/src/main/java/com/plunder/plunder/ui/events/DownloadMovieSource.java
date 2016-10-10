package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.Movie;
import com.plunder.provider.search.SearchResult;

public class DownloadMovieSource {
  private final Movie movie;
  private final SearchResult searchResult;

  public DownloadMovieSource(Movie movie, SearchResult searchResult) {
    this.movie = movie;
    this.searchResult = searchResult;
  }

  public Movie getMovie() {
    return movie;
  }

  public SearchResult getSearchResult() {
    return searchResult;
  }
}
