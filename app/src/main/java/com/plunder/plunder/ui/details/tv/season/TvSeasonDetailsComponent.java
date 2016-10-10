package com.plunder.plunder.ui.details.tv.season;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = TvSeasonDetailsModule.class) public interface TvSeasonDetailsComponent {
  void inject(TvSeasonDetailsFragment fragment);

  TvSeasonDetailsPresenter getTvShowDetailsPresenter();
}
