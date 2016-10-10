package com.plunder.plunder.ui.genre;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = GenreModule.class) public interface GenreComponent {
  void inject(GenreFragment fragment);

  GenrePresenter getSourcesPresenter();
}
