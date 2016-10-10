package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue public abstract class TvSeason implements Parcelable {
  public abstract int id();

  @Nullable public abstract String name();

  public abstract int seasonNumber();

  @Nullable public abstract String posterPath();

  @Nullable public abstract String largePosterPath();

  public abstract List<TvEpisode> episodes();

  public static Builder Builder() {
    return new AutoValue_TvSeason.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(int value);

    public abstract Builder name(String value);

    public abstract Builder seasonNumber(int value);

    public abstract Builder posterPath(String value);

    public abstract Builder largePosterPath(String value);

    public abstract Builder episodes(List<TvEpisode> value);

    public abstract TvSeason build();
  }
}