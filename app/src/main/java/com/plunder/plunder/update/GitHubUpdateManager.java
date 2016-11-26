package com.plunder.plunder.update;

import android.os.Environment;
import android.util.Pair;
import com.annimon.stream.Stream;
import com.github.zafarkhaja.semver.Version;
import com.plunder.plunder.BuildConfig;
import com.plunder.plunder.github.GithubAsset;
import com.plunder.plunder.github.GithubManager;
import com.plunder.plunder.github.GithubRelease;
import java.io.File;
import java.io.IOException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class GitHubUpdateManager extends BaseUpdateManager {
  private OkHttpClient httpClient;
  private GithubManager githubManager;
  private String updateDirectory;

  public GitHubUpdateManager(OkHttpClient httpClient, GithubManager githubManager,
      String updateDirectory) {
    this.httpClient = httpClient;
    this.githubManager = githubManager;
    this.updateDirectory = updateDirectory;
  }

  @Override public Observable<Update> fetchUpdate() {
    if (!isEnabled()) {
      return Observable.empty();
    }

    Version currentVersion = getCurrentVersion();

    Func1<GithubRelease, Observable<Version>> getVersion = release -> Observable.defer(() -> {
      String versionName = release.tagName();

      if (versionName.indexOf('v') == 0) {
        versionName = versionName.substring(1);
      }

      Version version = Version.valueOf(versionName);
      return Observable.just(version);
    });

    Func1<GithubRelease, Observable<Update>> createUpdate = release -> Observable.defer(() -> {
      List<GithubAsset> assets = release.assets();

      if (assets == null) {
        return Observable.empty();
      }

      Update.Builder builder = null;

      for (GithubAsset asset : assets) {
        String name = asset.name();

        if (name.endsWith(".apk")) {
          builder = Update.Builder()
              .name(release.tagName())
              .downloadUrl(asset.downloadUrl());
          break;
        }
      }

      if (builder == null) {
        Timber.w("Couldn't locate an APK in the asset list");
        return Observable.empty();
      }

      Update update = builder.build();
      return Observable.just(update);
    });

    return githubManager.getReleases(BuildConfig.GITHUB_NAME)
        .filter(release -> !release.isDraft() && !release.isPrerelease()) // ignore drafts and pre-releases
        .flatMap(getVersion, Pair::new) // parse the semvar from the release name
        .filter(pair -> {
          // compare the release's version against the currently installed version
          Version version = pair.second;
          return version.greaterThan(currentVersion);
        })
        .toSortedList((pair1, pair2) -> {
          Version version1 = pair1.second;
          Version version2 = pair2.second;
          return version2.compareTo(version1);
        })
        .flatMap(pairs -> {
          if (pairs.size() > 0) {
            Pair<GithubRelease, Version> pair = pairs.get(0);
            return Observable.just(pair.first);
          }

          return Observable.empty();
        })
        .flatMap(createUpdate);
  }

  @Override public Observable<String> downloadUpdate() {
    return fetchUpdate()
        .flatMap(update -> Observable.defer(() -> {
          String downloadUrl = update.downloadUrl();
          File file = new File(updateDirectory, FilenameUtils.getName(downloadUrl));

          try {
            File directory = file.getParentFile();

            if (directory.exists() && directory.isFile()) {
              if (!directory.delete()) {
                return Observable.error(new Exception("Unable to remove previous directory"));
              }
            }

            if (!directory.exists()) {
              if (!directory.mkdirs()) {
                return Observable.error(new Exception("Unable to create directory"));
              }
            }

            if (file.exists()) {
              if (!file.delete()) {
                return Observable.error(new Exception("Unable to remove previous file"));
              }
            }

            if (!file.createNewFile()) {
              return Observable.error(new Exception("Unable to create file"));
            }

            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = httpClient.newCall(request).execute();

            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();

            return Observable.just(file.getAbsolutePath());
          } catch (IOException e) {
            return Observable.error(e);
          }
        }));
  }
}
