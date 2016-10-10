package com.plunder.plunder.ui.viewmodels;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.TvShow;
import java.util.List;

public class TvShowViewModel {
  private final TvShow tvShow;

  public TvShowViewModel(@NonNull TvShow tvShow) {
    this.tvShow = tvShow;
  }

  public TvShow tvShow() {
    return tvShow;
  }

  public String name() {
    return tvShow.name();
  }

  @Nullable public Uri posterUri() {
    String path = tvShow.posterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  @Nullable public Uri backdropUri() {
    String path = tvShow.backdropPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  public static List<TvShowViewModel> fromList(@NonNull List<TvShow> items) {
    return Stream.of(items).map(TvShowViewModel::new).collect(Collectors.toList());
  }
}
