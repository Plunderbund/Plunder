package com.plunder.plunder.ui.details.movies;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;

public interface MovieDetailsPresenter extends FragmentPresenter {
  @Nullable Movie getMovie();

  void setMovie(@NonNull Movie movie);

  void watchMovie(@NonNull MovieViewModel movie);

  void watch();
}
