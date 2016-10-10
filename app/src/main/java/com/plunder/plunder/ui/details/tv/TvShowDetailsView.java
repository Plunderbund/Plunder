package com.plunder.plunder.ui.details.tv;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.TvShowDetailsViewModel;

public interface TvShowDetailsView extends FragmentView {
  void setTvShowDetails(@NonNull TvShowDetailsViewModel viewModel);

  void setBackgroundUri(@Nullable Uri uri);
}
