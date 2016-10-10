package com.plunder.plunder.ui.sources;

import android.support.annotation.NonNull;
import com.plunder.plunder.providers.ProviderManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class SourcesModule {
  @NonNull private final WeakReference<SourcesView> viewRef;

  public SourcesModule(SourcesView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public SourcesView provideView() {
    return viewRef.get();
  }

  @Provides public SourcesPresenter providePresenter(SourcesView view, EventBus eventBus,
      ProviderManager providerManager) {
    return new SourcesPresenterImpl(view, eventBus, providerManager);
  }
}
