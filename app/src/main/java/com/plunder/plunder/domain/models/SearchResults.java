package com.plunder.plunder.domain.models;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.plunder.provider.search.SearchResult;
import java.util.List;

@AutoValue public abstract class SearchResults implements Parcelable {
  @Nullable public abstract String provider();

  public abstract List<SearchResult> results();

  public static Builder Builder() {
    return new AutoValue_SearchResults.Builder();
  }

  @AutoValue.Builder public abstract static class Builder {
    public abstract Builder provider(String value);

    public abstract Builder results(List<SearchResult> value);

    public abstract SearchResults build();
  }
}
