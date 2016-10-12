package com.plunder.plunder.update;

import android.app.DownloadManager;
import rx.Observable;

public interface UpdateManager {
  Observable<Update> fetchUpdate();

  Observable<String> downloadUpdate();
}
