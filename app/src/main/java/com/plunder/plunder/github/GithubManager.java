package com.plunder.plunder.github;

import rx.Observable;

public interface GithubManager {
  Observable<GithubRelease> getReleases(String name);

  Observable<GithubRepository> findRepositories(GithubSearch search);

  Observable<GithubRepository> getRepository(String name);
}
