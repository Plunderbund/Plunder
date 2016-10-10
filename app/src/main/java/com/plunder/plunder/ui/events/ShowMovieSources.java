package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.Movie;

public class ShowMovieSources {
  private final Movie movie;

  public ShowMovieSources(Movie movie) {
    this.movie = movie;
  }

  public Movie getMovie() {
    return movie;
  }
}
