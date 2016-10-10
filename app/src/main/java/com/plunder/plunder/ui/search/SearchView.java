package com.plunder.plunder.ui.search;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.List;

public interface SearchView extends FragmentView {
  void clearResults();

  void setMovieResults(@NonNull List<MovieViewModel> results);

  void setTvShowResults(@NonNull List<TvShowViewModel> results);

  void setBackgroundUri(@Nullable Uri uri);
}
