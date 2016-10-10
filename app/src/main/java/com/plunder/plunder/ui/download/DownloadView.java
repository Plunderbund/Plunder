package com.plunder.plunder.ui.download;

import android.net.Uri;
import android.support.annotation.Nullable;
import com.plunder.plunder.ui.common.FragmentView;

public interface DownloadView extends FragmentView {
  void setBackgroundUri(@Nullable Uri uri);

  void setProgress(int progress, float downloadSpeed);
}
