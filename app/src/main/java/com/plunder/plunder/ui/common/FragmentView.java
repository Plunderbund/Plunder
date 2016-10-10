package com.plunder.plunder.ui.common;

import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;

public interface FragmentView {
  boolean hasContext();

  <T> Observable.Transformer<T, T> getLifecycleTransformer(FragmentEvent event);
}
