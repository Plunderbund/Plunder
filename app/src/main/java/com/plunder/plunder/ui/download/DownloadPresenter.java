package com.plunder.plunder.ui.download;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.FragmentPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.provider.search.SearchResult;

public interface DownloadPresenter extends FragmentPresenter {
  @Nullable Movie getMovie();

  @Nullable MovieViewModel getMovieViewModel();

  void setMovie(@NonNull Movie movie);

  @Nullable TvShow getTvShow();

  @Nullable TvSeason getTvSeason();

  @Nullable TvEpisode getTvEpisode();

  void setTvDetails(@NonNull TvShow tvShow, @NonNull TvSeason tvSeason,
      @NonNull TvEpisode tvEpisode);

  @Nullable SearchResult getSearchResult();

  void setSearchResult(@NonNull SearchResult searchResult);
}
