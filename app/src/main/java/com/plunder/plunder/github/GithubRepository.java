package com.plunder.plunder.github;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHLicense;
import org.kohsuke.github.GHMilestone;
import org.kohsuke.github.GHUser;

@AutoValue public abstract class GithubRepository implements Parcelable {
  public abstract String name();

  public abstract String description();

  public abstract String fullName();

  public abstract Boolean isFork();

  public static Builder Builder() {
    return new AutoValue_GithubRepository.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder name(String value);

    public abstract Builder description(String value);

    public abstract Builder fullName(String value);

    public abstract Builder isFork(Boolean value);

    public abstract GithubRepository build();
  }
}