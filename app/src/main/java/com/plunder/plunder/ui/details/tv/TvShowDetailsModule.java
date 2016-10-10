package com.plunder.plunder.ui.details.tv;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.catalog.CatalogManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class TvShowDetailsModule {
  @NonNull private final WeakReference<TvShowDetailsView> viewRef;

  public TvShowDetailsModule(TvShowDetailsView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public TvShowDetailsView provideView() {
    return viewRef.get();
  }

  @Provides
  public TvShowDetailsPresenter providePresenter(TvShowDetailsView view, EventBus eventBus,
      CatalogManager catalogManager) {
    return new TvShowDetailsPresenterImpl(view, eventBus, catalogManager);
  }
}
