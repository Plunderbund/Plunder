package com.plunder.plunder.ui.viewmodels;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.plunder.provider.search.SearchResult;
import java.util.List;

public class SearchResultViewModel {
  private final SearchResult searchResult;

  public SearchResultViewModel(@NonNull SearchResult searchResult) {
    this.searchResult = searchResult;
  }

  public SearchResult searchResult() {
    return searchResult;
  }

  public String name() {
    return searchResult.name();
  }

  public Integer peers() {
    return searchResult.peers();
  }

  public Integer seeds() {
    return searchResult.seeds();
  }

  public String fileSize(Context context) {
    return Formatter.formatShortFileSize(context, searchResult.fileSize());
  }

  public static List<SearchResultViewModel> fromList(@NonNull List<SearchResult> items) {
    return Stream.of(items).map(SearchResultViewModel::new).collect(Collectors.toList());
  }
}
