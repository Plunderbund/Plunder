package com.plunder.plunder.ui.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.Collection;

public interface MainView extends FragmentView {
  void addMovies(@NonNull Collection<MovieViewModel> movies);

  void addTvShows(@NonNull Collection<TvShowViewModel> tvShows);

  void addGenres(@NonNull Collection<GenreViewModel> genres);

  void setBackgroundUri(@Nullable Uri uri);

  void provideUpdate(String name);

  void updateStarted();

  void updateComplete(String filePath);
}
