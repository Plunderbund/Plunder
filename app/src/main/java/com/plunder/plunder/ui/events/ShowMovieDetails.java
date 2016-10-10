package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.Movie;

public class ShowMovieDetails {
  private final Movie movie;

  public ShowMovieDetails(Movie movie) {
    this.movie = movie;
  }

  public Movie getMovie() {
    return movie;
  }
}
