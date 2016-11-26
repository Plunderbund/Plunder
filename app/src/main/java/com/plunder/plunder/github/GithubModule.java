package com.plunder.plunder.github;

import com.plunder.plunder.AppScope;
import com.plunder.plunder.domain.DomainScope;
import dagger.Module;
import dagger.Provides;
import org.kohsuke.github.GitHub;

@Module public class GithubModule {
  @Provides @AppScope public GithubManager provideGithubManager() {
    return new KohsukeGithubManager();
  }
}
