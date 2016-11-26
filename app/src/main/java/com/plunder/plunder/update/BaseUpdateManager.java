package com.plunder.plunder.update;

import com.github.zafarkhaja.semver.Version;
import com.plunder.plunder.BuildConfig;

public abstract class BaseUpdateManager implements UpdateManager {
  protected boolean isEnabled() {
    if (BuildConfig.DEBUG) {
      return false;
    }

    return true;
  }

  protected Version getCurrentVersion() {
    return Version.valueOf(BuildConfig.VERSION_NAME);
  }
}
