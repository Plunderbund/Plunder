package com.plunder.plunder.ui.viewmodels;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.Movie;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MovieViewModel implements MediaViewModel {
  private final Movie movie;
  private final Calendar releaseDate;

  public MovieViewModel(@NonNull Movie movie) {
    this.movie = movie;

    releaseDate = new GregorianCalendar();

    if (movie.releaseDate() != null) {
      releaseDate.setTime(movie.releaseDate());
    }
  }

  public Movie movie() {
    return movie;
  }

  public String name() {
    return movie.name();
  }

  public String tagline() {
    return movie.tagline();
  }

  public String overview() {
    return movie.overview();
  }

  public int releaseYear() {
    return releaseDate.get(Calendar.YEAR);
  }

  @Nullable public Uri posterUri() {
    String path = movie.posterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  @Nullable public Uri largePosterPath() {
    String path = movie.largePosterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  @Nullable public Uri backdropUri() {
    String path = movie.backdropPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  public static List<MovieViewModel> fromList(@NonNull List<Movie> items) {
    return Stream.of(items).map(MovieViewModel::new).collect(Collectors.toList());
  }
}
