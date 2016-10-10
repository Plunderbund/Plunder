package com.plunder.plunder.ui.genre;

import android.support.annotation.NonNull;
import com.plunder.plunder.domain.catalog.CatalogManager;
import dagger.Module;
import dagger.Provides;
import java.lang.ref.WeakReference;
import org.greenrobot.eventbus.EventBus;

@Module public class GenreModule {
  @NonNull private final WeakReference<GenreView> viewRef;

  public GenreModule(GenreView view) {
    this.viewRef = new WeakReference<>(view);
  }

  @Provides public GenreView provideView() {
    return viewRef.get();
  }

  @Provides public GenrePresenter providePresenter(GenreView view, EventBus eventBus,
      CatalogManager catalogManager) {
    return new GenrePresenterImpl(view, eventBus, catalogManager);
  }
}
