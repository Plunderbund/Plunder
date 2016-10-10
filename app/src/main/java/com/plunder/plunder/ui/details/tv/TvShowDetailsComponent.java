package com.plunder.plunder.ui.details.tv;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = TvShowDetailsModule.class) public interface TvShowDetailsComponent {
  void inject(TvShowDetailsFragment fragment);

  TvShowDetailsPresenter getTvShowDetailsPresenter();
}
