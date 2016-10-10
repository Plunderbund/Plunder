package com.plunder.plunder.ui.events;

import com.plunder.plunder.domain.models.Genre;

public class ShowGenre {
  private final Genre genre;

  public ShowGenre(Genre genre) {
    this.genre = genre;
  }

  public Genre getGenre() {
    return genre;
  }
}
