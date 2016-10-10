package com.plunder.plunder.ui.common;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.view.View;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class BasePlaybackOverlayFragment extends PlaybackOverlayFragment
    implements FragmentView {
  private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

  @Override public <T> Observable.Transformer<T, T> getLifecycleTransformer(FragmentEvent event) {
    return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
  }

  @Override public boolean hasContext() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return getContext() != null;
    }

    return getActivity() != null;
  }

  @SuppressWarnings("deprecation") @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      lifecycleSubject.onNext(FragmentEvent.ATTACH);
      onAttach();
    }
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    lifecycleSubject.onNext(FragmentEvent.ATTACH);
    onAttach();
  }

  protected void onAttach() {
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lifecycleSubject.onNext(FragmentEvent.CREATE);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
  }

  @Override public void onStart() {
    super.onStart();
    lifecycleSubject.onNext(FragmentEvent.START);
  }

  @Override public void onResume() {
    super.onResume();
    lifecycleSubject.onNext(FragmentEvent.RESUME);
  }

  @Override public void onPause() {
    lifecycleSubject.onNext(FragmentEvent.PAUSE);
    super.onPause();
  }

  @Override public void onStop() {
    lifecycleSubject.onNext(FragmentEvent.STOP);
    super.onStop();
  }

  @Override public void onDestroyView() {
    lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    lifecycleSubject.onNext(FragmentEvent.DESTROY);
    super.onDestroy();
  }

  @Override public void onDetach() {
    lifecycleSubject.onNext(FragmentEvent.DETACH);
    super.onDetach();
  }
}
