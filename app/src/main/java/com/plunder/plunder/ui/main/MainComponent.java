package com.plunder.plunder.ui.main;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = MainModule.class) public interface MainComponent {
  void inject(MainFragment fragment);

  MainPresenter getMainPresenter();
}
