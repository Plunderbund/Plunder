package com.plunder.plunder.ui.download;

import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.ActivityScope;
import dagger.Component;

@ActivityScope @Component(
    dependencies = {
        AppComponent.class
    },
    modules = DownloadModule.class) public interface DownloadComponent {
  void inject(DownloadFragment fragment);
}
