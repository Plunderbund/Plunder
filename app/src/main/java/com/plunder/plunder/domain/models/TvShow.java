package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class TvShow implements Parcelable {
  public abstract int id();

  @Nullable public abstract String name();

  @Nullable public abstract String posterPath();

  @Nullable public abstract String largePosterPath();

  @Nullable public abstract String backdropPath();

  public static Builder Builder() {
    return new AutoValue_TvShow.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(int value);

    public abstract Builder name(String value);

    public abstract Builder posterPath(String value);

    public abstract Builder largePosterPath(String value);

    public abstract Builder backdropPath(String value);

    public abstract TvShow build();
  }
}