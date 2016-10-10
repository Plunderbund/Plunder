package com.plunder.plunder.ui.details.tv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.TvSeasonViewModel;

public interface TvShowDetailsPresenter extends FragmentPresenter {
  @Nullable TvShow getTvShow();

  void setTvShow(@NonNull TvShow tvShow);

  void watchSeason(@NonNull TvSeasonViewModel viewModel);
}
