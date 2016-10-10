package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Date;

@AutoValue public abstract class Movie implements Parcelable {
  public abstract int id();

  @Nullable public abstract String name();

  @Nullable public abstract String tagline();

  @Nullable public abstract Date releaseDate();

  @Nullable public abstract String overview();

  @Nullable public abstract String posterPath();

  @Nullable public abstract String largePosterPath();

  @Nullable public abstract String backdropPath();

  @Nullable public abstract String imdbId();

  public static Builder Builder() {
    return new AutoValue_Movie.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(int value);

    public abstract Builder name(String value);

    public abstract Builder tagline(String value);

    public abstract Builder releaseDate(Date value);

    public abstract Builder overview(String value);

    public abstract Builder posterPath(String value);

    public abstract Builder largePosterPath(String value);

    public abstract Builder backdropPath(String value);

    public abstract Builder imdbId(String value);

    public abstract Movie build();
  }
}
