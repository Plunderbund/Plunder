package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class Genre implements Parcelable {
  @Nullable public abstract Integer movieId();

  @Nullable public abstract Integer tvId();

  @Nullable public abstract String name();

  public static Builder Builder() {
    return new AutoValue_Genre.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder movieId(Integer value);

    public abstract Builder tvId(Integer value);

    public abstract Builder name(String value);

    public abstract Genre build();
  }
}
