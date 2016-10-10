package com.plunder.plunder.ui.search;

import android.support.annotation.NonNull;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;

public interface SearchPresenter extends FragmentPresenter {
  void setSearchQuery(@NonNull String query);

  void selectMovie(@NonNull MovieViewModel viewModel);

  void watchMovie(@NonNull MovieViewModel viewModel);

  void selectTvShow(@NonNull TvShowViewModel viewModel);

  void watchTvShow(@NonNull TvShowViewModel viewModel);
}
