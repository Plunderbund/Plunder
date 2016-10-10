package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Date;

@AutoValue public abstract class TvEpisode implements Parcelable {
  public abstract int id();

  @Nullable public abstract String name();

  @Nullable public abstract String overview();

  @Nullable public abstract Date airDate();

  public abstract int episodeNumber();

  public abstract int seasonNumber();

  @Nullable public abstract String stillUrl();

  public static Builder Builder() {
    return new AutoValue_TvEpisode.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(int value);

    public abstract Builder name(String value);

    public abstract Builder overview(String value);

    public abstract Builder airDate(Date value);

    public abstract Builder episodeNumber(int value);

    public abstract Builder seasonNumber(int value);

    public abstract Builder stillUrl(String value);

    public abstract TvEpisode build();
  }
}