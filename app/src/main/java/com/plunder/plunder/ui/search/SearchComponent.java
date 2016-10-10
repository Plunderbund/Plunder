package com.plunder.plunder.ui.search;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = SearchModule.class) public interface SearchComponent {
  void inject(SearchFragment fragment);

  SearchPresenter getSearchPresenter();
}
