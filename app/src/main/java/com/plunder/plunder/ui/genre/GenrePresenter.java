package com.plunder.plunder.ui.genre;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;

public interface GenrePresenter extends FragmentPresenter {
  @Nullable Genre getGenre();

  void setGenre(@NonNull Genre genre);

  void nextPage(int position);

  void selectMovie(@NonNull MovieViewModel viewModel);

  void watchMovie(@NonNull MovieViewModel viewModel);

  void selectTvShow(@NonNull TvShowViewModel viewModel);

  void watchTvShow(@NonNull TvShowViewModel viewModel);

  void search();

  Uri getBackgroundUri();
}
