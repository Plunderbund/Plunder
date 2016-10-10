package com.plunder.plunder.ui.details.movies;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.VideoViewModel;
import java.util.List;

public interface MovieDetailsView extends FragmentView {
  void setMovie(@NonNull MovieViewModel viewModel);

  void setTrailer(@NonNull VideoViewModel viewModel);

  void setBackgroundUri(@Nullable Uri uri);

  void setSimilarMovies(@NonNull List<MovieViewModel> movies);
}
