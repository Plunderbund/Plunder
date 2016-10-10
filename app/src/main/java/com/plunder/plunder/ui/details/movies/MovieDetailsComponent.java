package com.plunder.plunder.ui.details.movies;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = MovieDetailsModule.class) public interface MovieDetailsComponent {
  void inject(MovieDetailsFragment fragment);

  MovieDetailsPresenter getMovieDetailsPresenter();
}
