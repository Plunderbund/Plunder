package com.plunder.plunder.ui.search;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.catalog.CatalogManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class SearchModule {
  @NonNull private final WeakReference<SearchView> viewRef;

  public SearchModule(SearchView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public SearchView provideView() {
    return viewRef.get();
  }

  @Provides public SearchPresenter providePresenter(SearchView view, EventBus eventBus,
      CatalogManager catalogManager) {
    return new SearchPresenterImpl(view, eventBus, catalogManager);
  }
}
