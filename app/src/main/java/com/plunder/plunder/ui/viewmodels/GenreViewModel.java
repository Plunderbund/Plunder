package com.plunder.plunder.ui.viewmodels;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.Genre;
import java.util.List;

public class GenreViewModel {
  private final Genre genre;

  public GenreViewModel(@NonNull Genre genre) {
    this.genre = genre;
  }

  public Genre genre() {
    return genre;
  }

  public String name() {
    return genre.name();
  }

  public static List<GenreViewModel> fromList(@NonNull List<Genre> items) {
    return Stream.of(items).map(GenreViewModel::new).collect(Collectors.toList());
  }
}
