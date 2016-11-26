package com.plunder.plunder.github;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Date;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

@AutoValue public abstract class GithubAsset implements Parcelable {
  public abstract String name();

  public abstract String label();

  public abstract String state();

  public abstract String contentType();

  public abstract Long size();

  public abstract Long downloadCount();

  public abstract String downloadUrl();

  public static Builder Builder() {
    return new AutoValue_GithubAsset.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract GithubAsset.Builder name(String value);

    public abstract GithubAsset.Builder label(String value);

    public abstract GithubAsset.Builder state(String value);

    public abstract GithubAsset.Builder contentType(String value);

    public abstract GithubAsset.Builder size(Long value);

    public abstract GithubAsset.Builder downloadCount(Long value);

    public abstract GithubAsset.Builder downloadUrl(String value);

    public abstract GithubAsset build();
  }
}