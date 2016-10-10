package com.plunder.plunder.ui.viewmodels;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.TvShowDetails;
import java.util.List;

public class TvShowDetailsViewModel {
  private final TvShowDetails tvShowDetails;
  private final List<TvSeasonViewModel> seasonViewModels;

  public TvShowDetailsViewModel(@NonNull TvShowDetails tvShowDetails) {
    this.tvShowDetails = tvShowDetails;
    seasonViewModels = TvSeasonViewModel.fromList(tvShowDetails.seasons());
  }

  public TvShowDetails tvShowDetails() {
    return tvShowDetails;
  }

  public String name() {
    return tvShowDetails.name();
  }

  @Nullable public Uri posterUri() {
    String path = tvShowDetails.posterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  @Nullable public Uri backdropUri() {
    String path = tvShowDetails.backdropPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  @NonNull public List<TvSeasonViewModel> seasons() {
    return seasonViewModels;
  }

  public static List<TvShowDetailsViewModel> fromList(@NonNull List<TvShowDetails> items) {
    return Stream.of(items).map(TvShowDetailsViewModel::new).collect(Collectors.toList());
  }
}
