package com.plunder.plunder.ui.details.tv.season;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.TvEpisodeViewModel;

public interface TvSeasonDetailsPresenter extends FragmentPresenter {
  @Nullable TvShow getTvShow();

  void setTvShow(@NonNull TvShow tvShow);

  @Nullable TvSeason getTvSeason();

  void setTvSeason(@NonNull TvSeason tvSeason);

  void watch(@NonNull TvEpisodeViewModel viewModel);
}
