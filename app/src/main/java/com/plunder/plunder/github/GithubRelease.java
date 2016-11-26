package com.plunder.plunder.github;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Date;
import java.util.List;

@AutoValue public abstract class GithubRelease implements Parcelable {
  public abstract String name();

  public abstract String tagName();

  public abstract String body();

  public abstract Boolean isDraft();

  public abstract Boolean isPrerelease();

  public abstract Date publishedAt();

  public abstract List<GithubAsset> assets();

  public static Builder Builder() {
    return new AutoValue_GithubRelease.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder name(String value);

    public abstract Builder tagName(String value);

    public abstract Builder body(String value);

    public abstract Builder isDraft(Boolean value);

    public abstract Builder isPrerelease(Boolean value);

    public abstract Builder publishedAt(Date value);

    public abstract Builder assets(List<GithubAsset> value);

    public abstract GithubRelease build();
  }
}