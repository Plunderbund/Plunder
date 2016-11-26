package com.plunder.plunder.github;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import java.util.Date;
import java.util.List;
import org.kohsuke.github.GHRepositorySearchBuilder;

@AutoValue public abstract class GithubSearch implements Parcelable {
  public abstract String query();

  @Nullable public abstract String in();

  @Nullable public abstract String size();

  @Nullable public abstract String forks();

  @Nullable public abstract String created();

  @Nullable public abstract String pushed();

  @Nullable public abstract String user();

  @Nullable public abstract String language();

  @Nullable public abstract String stars();

  public static Builder Builder() {
    return new AutoValue_GithubSearch.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder query(String value);

    public abstract Builder in(String value);

    public abstract Builder size(String value);

    public abstract Builder forks(String value);

    public abstract Builder created(String value);

    public abstract Builder pushed(String value);

    public abstract Builder user(String value);

    public abstract Builder language(String value);

    public abstract Builder stars(String value);

    public abstract GithubSearch build();
  }
}
