package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class Video implements Parcelable {
  public final static String TYPE_TRAILER = "trailer";
  public final static String SITE_YOUTUBE = "youtube";

  public abstract String id();

  public abstract String key();

  public abstract String name();

  public abstract String site();

  public abstract int size();

  public abstract String type();

  public static Builder Builder() {
    return new AutoValue_Video.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder id(String value);

    public abstract Builder key(String value);

    public abstract Builder name(String value);

    public abstract Builder site(String value);

    public abstract Builder size(int value);

    public abstract Builder type(String value);

    public abstract Video build();
  }
}
