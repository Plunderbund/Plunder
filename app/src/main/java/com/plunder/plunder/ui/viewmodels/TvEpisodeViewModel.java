package com.plunder.plunder.ui.viewmodels;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.plunder.domain.models.TvEpisode;
import java.util.List;

public class TvEpisodeViewModel implements MediaViewModel {
  private final TvEpisode tvEpisode;

  public TvEpisodeViewModel(@NonNull TvEpisode tvEpisode) {
    this.tvEpisode = tvEpisode;
  }

  public TvEpisode tvEpisode() {
    return tvEpisode;
  }

  @Override public String name() {
    String name = tvEpisode.name();

    if (TextUtils.isEmpty(name)) {
      name = "Episode " + String.valueOf(tvEpisode.episodeNumber());
    }

    return name;
  }

  public int episodeNumber() {
    return tvEpisode.episodeNumber();
  }

  public int seasonNumber() {
    return tvEpisode.seasonNumber();
  }

  public String overview() {
    return tvEpisode.overview();
  }

  public Uri stillUri() {
    String url = tvEpisode.stillUrl();

    if (TextUtils.isEmpty(url)) {
      return null;
    }

    return Uri.parse(url);
  }

  public static List<TvEpisodeViewModel> fromList(@NonNull List<TvEpisode> items) {
    return Stream.of(items).map(TvEpisodeViewModel::new).collect(Collectors.toList());
  }
}
