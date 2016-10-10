package com.plunder.plunder.ui.main;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.catalog.CatalogManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class MainModule {
  @NonNull private final WeakReference<MainView> viewRef;

  public MainModule(MainView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public MainView provideView() {
    return viewRef.get();
  }

  @Provides public MainPresenter providePresenter(MainView view, EventBus eventBus,
      CatalogManager catalogManager) {
    return new MainPresenterImpl(view, eventBus, catalogManager);
  }
}
