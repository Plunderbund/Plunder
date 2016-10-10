package com.plunder.plunder.ui.sources;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.SearchResultViewModel;
import java.util.List;

public interface SourcesView extends FragmentView {
  void setBackgroundUri(@Nullable Uri uri);

  void addResults(String providerName, @NonNull List<SearchResultViewModel> results);

  void setLoading(boolean loading);
}
