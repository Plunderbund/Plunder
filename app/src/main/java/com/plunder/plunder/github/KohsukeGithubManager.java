package com.plunder.plunder.github;

import android.util.Pair;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.io.IOException;
import java.util.List;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import rx.Observable;

public class KohsukeGithubManager implements GithubManager {
  @Override public Observable<GithubRelease> getReleases(String name) {
    return Observable.defer(() -> {
      try {
        GitHub gitHub = GitHub.connectAnonymously();

        return Observable.from(gitHub.getRepository(name).listReleases())
            .concatMap(this::mapGithubRelease);
      } catch (IOException e) {
        return Observable.error(e);
      }
    });
  }

  @Override public Observable<GithubRepository> findRepositories(GithubSearch search) {
    return Observable.defer(() -> {
      try {
        GitHub gitHub = GitHub.connectAnonymously();
        GHRepositorySearchBuilder builder = gitHub.searchRepositories().q(search.query());

        if (search.in() != null) {
          builder.in(search.in());
        }

        if (search.size() != null) {
          builder.size(search.size());
        }

        if (search.forks() != null) {
          builder.forks(search.forks());
        }

        if (search.created() != null) {
          builder.created(search.created());
        }

        if (search.pushed() != null) {
          builder.pushed(search.pushed());
        }

        if (search.user() != null) {
          builder.user(search.user());
        }

        if (search.language() != null) {
          builder.language(search.language());
        }

        if (search.stars() != null) {
          builder.stars(search.stars());
        }

        return Observable.from(builder.list())
            .concatMap(this::mapGithubRepository);
      } catch (IOException e) {
        return Observable.error(e);
      }
    });
  }

  @Override public Observable<GithubRepository> getRepository(String name) {
    return null;
  }

  private Observable<GithubRelease> mapGithubRelease(GHRelease release) {
    return Observable.just(GithubRelease.Builder()
        .name(release.getName())
        .tagName(release.getTagName())
        .body(release.getBody())
        .isDraft(release.isDraft())
        .isPrerelease(release.isPrerelease())
        .publishedAt(release.getPublished_at()))
        .flatMap(result -> getGithubAssets(release).toList(), Pair::new)
        .map(pair -> {
          GithubRelease.Builder builder = pair.first;
          List<GithubAsset> assets = pair.second;

          return builder.assets(assets).build();
        });
  }

  private Observable<GithubAsset> getGithubAssets(GHRelease release) {
    return Observable.just(release)
        .concatMap(result -> {
          try {
            return Observable.from(release.getAssets());
          } catch (IOException e) {
            return Observable.error(e);
          }
        })
        .map(asset -> GithubAsset.Builder()
            .name(asset.getName())
            .label(asset.getLabel())
            .state(asset.getState())
            .contentType(asset.getContentType())
            .size(asset.getSize())
            .downloadCount(asset.getDownloadCount())
            .downloadUrl(asset.getBrowserDownloadUrl())
            .build());
  }

  private Observable<GithubRepository> mapGithubRepository(GHRepository repository) {
    return Observable.just(GithubRepository.Builder()
        .name(repository.getName())
        .description(repository.getDescription())
        .fullName(repository.getFullName())
        .isFork(repository.isFork())
        .build());
  }
}