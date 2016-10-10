package com.plunder.plunder.ui.details.tv.season;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.TvEpisodeViewModel;
import com.plunder.plunder.ui.viewmodels.TvSeasonViewModel;
import java.util.List;

public interface TvSeasonDetailsView extends FragmentView {
  void setTvEpisodes(@NonNull TvSeasonViewModel season, @NonNull List<TvEpisodeViewModel> episodes);

  void setBackgroundUri(@Nullable Uri uri);
}
