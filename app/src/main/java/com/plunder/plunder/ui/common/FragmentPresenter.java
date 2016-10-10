package com.plunder.plunder.ui.common;

import android.content.Context;

public interface FragmentPresenter extends Presenter {
  void initialize();

  void onCreated(Context context);

  void onStart();

  void onStop();

  void onDestroy();
}
