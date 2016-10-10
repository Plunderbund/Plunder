package com.plunder.plunder.ui.viewmodels;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.models.Video;

public class VideoViewModel {
  private final Video video;

  public VideoViewModel(@NonNull Video video) {
    this.video = video;
  }

  public String key() {
    return video.key();
  }
}
