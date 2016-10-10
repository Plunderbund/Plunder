package com.plunder.plunder.ui.viewmodels;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.TvSeason;
import java.util.List;

public class TvSeasonViewModel {
  private final TvSeason tvSeason;

  public TvSeasonViewModel(@NonNull TvSeason tvSeason) {
    this.tvSeason = tvSeason;
  }

  public TvSeason tvSeason() {
    return tvSeason;
  }

  public String name() {
    String name = tvSeason.name();

    if (TextUtils.isEmpty(name)) {
      name = "Season " + String.valueOf(tvSeason.seasonNumber());
    }

    return name;
  }

  public Uri posterUri() {
    String path = tvSeason.posterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  public Uri posterPath() {
    String path = tvSeason.posterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  public Uri largePosterPath() {
    String path = tvSeason.largePosterPath();

    if (TextUtils.isEmpty(path)) {
      return null;
    }

    return Uri.parse(path);
  }

  public static List<TvSeasonViewModel> fromList(@NonNull List<TvSeason> items) {
    return Stream.of(items).map(TvSeasonViewModel::new).collect(Collectors.toList());
  }
}
