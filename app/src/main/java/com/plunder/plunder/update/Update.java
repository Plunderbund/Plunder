package com.plunder.plunder.update;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;

@AutoValue public abstract class Update implements Parcelable {
  public abstract String name();

  public abstract String downloadUrl();

  public static Update.Builder Builder() {
    return new AutoValue_Update.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Update.Builder name(String value);

    public abstract Update.Builder downloadUrl(String value);

    public abstract Update build();
  }
}
