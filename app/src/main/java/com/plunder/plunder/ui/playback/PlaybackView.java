package com.plunder.plunder.ui.playback;

import android.support.annotation.NonNull;
import com.plunder.plunder.ui.common.FragmentView;
import com.plunder.plunder.ui.viewmodels.MediaViewModel;
import java.io.File;

public interface PlaybackView extends FragmentView {
  void setMediaDetails(@NonNull MediaViewModel viewModel);

  void setAddress(String address);
}
