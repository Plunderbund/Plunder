package com.plunder.plunder.ui.common;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.trello.rxlifecycle.android.FragmentEvent;
import org.greenrobot.eventbus.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseFragmentPresenter<V extends FragmentView> extends BasePresenter<V>
    implements FragmentPresenter {
  protected BaseFragmentPresenter(@NonNull V view, EventBus eventBus) {
    super(view, eventBus);
  }

  @Override @CallSuper public void initialize() {
  }

  @Override @CallSuper public void onCreated(Context context) {
  }

  @Override @CallSuper public void onStart() {

  }

  @Override @CallSuper public void onStop() {
  }

  @Override @CallSuper public void onDestroy() {
  }

  @Nullable @Override protected V getView() {
    V view = super.getView();

    if (view == null || !view.hasContext()) {
      return null;
    }

    return view;
  }

  protected <T> Observable.Transformer<T, T> getLifecycleTransformer() {
    return getLifecycleTransformer(FragmentEvent.STOP);
  }

  protected <T> Observable.Transformer<T, T> getLifecycleTransformer(FragmentEvent event) {
    return observable -> {
      FragmentView view = getView();

      if (view != null) {
        observable = observable.compose(view.getLifecycleTransformer(event));
      }

      return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    };
  }
}
