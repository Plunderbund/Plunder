package com.plunder.plunder.ui.genre;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;
import java.util.Collection;

public interface GenreView extends FragmentView {
  void addMovieCategory(int id, String title);

  void addTvCategory(int id, String title);

  void addItems(int id, @NonNull Collection<?> items);

  void setBackgroundUri(@Nullable Uri uri);
}
