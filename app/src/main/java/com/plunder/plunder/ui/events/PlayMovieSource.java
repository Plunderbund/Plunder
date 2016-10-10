package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.Movie;
import java.util.UUID;

public class PlayMovieSource {
  private final UUID downloadId;
  private final Movie movie;

  public PlayMovieSource(UUID downloadId, Movie movie) {
    this.downloadId = downloadId;
    this.movie = movie;
  }

  public UUID getDownloadId() {
    return downloadId;
  }

  public Movie getMovie() {
    return movie;
  }
}
