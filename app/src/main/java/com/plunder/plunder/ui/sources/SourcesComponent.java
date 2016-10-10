package com.plunder.plunder.ui.sources;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = SourcesModule.class) public interface SourcesComponent {
  void inject(SourcesFragment fragment);

  SourcesPresenter getSourcesPresenter();
}
