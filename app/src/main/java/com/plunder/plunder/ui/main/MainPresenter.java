package com.plunder.plunder.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;

public interface MainPresenter extends FragmentPresenter {
  void selectMovie(@NonNull MovieViewModel viewModel);

  void watchMovie(@NonNull MovieViewModel viewModel);

  void selectTvShow(@NonNull TvShowViewModel viewModel);

  void watchTvShow(@NonNull TvShowViewModel viewModel);

  void selectGenre(@NonNull GenreViewModel viewModel);

  void search();

  Uri getBackgroundUri();

  void performUpdate();
}
