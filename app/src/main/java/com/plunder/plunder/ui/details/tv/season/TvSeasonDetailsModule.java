package com.plunder.plunder.ui.details.tv.season;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.catalog.CatalogManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class TvSeasonDetailsModule {
  @NonNull private final WeakReference<TvSeasonDetailsView> viewRef;

  public TvSeasonDetailsModule(TvSeasonDetailsView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public TvSeasonDetailsView provideView() {
    return viewRef.get();
  }

  @Provides
  public TvSeasonDetailsPresenter providePresenter(TvSeasonDetailsView view, EventBus eventBus,
      CatalogManager catalogManager) {
    return new TvSeasonDetailsPresenterImpl(view, eventBus, catalogManager);
  }
}
