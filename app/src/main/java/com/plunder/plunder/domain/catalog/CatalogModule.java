package com.plunder.plunder.domain.catalog;

import com.plunder.plunder.BuildConfig;
import com.plunder.plunder.domain.DomainScope;
import com.uwetrottmann.tmdb2.Tmdb;
import dagger.Module;
import dagger.Provides;

@Module public class CatalogModule {
  @Provides @DomainScope public CatalogManager provideCatalogManager(Tmdb tmdbManager) {
    return new TmdbCatalogManager(tmdbManager);
  }

  @Provides @DomainScope public Tmdb provideTmdbManager() {
    return new Tmdb(BuildConfig.TMDB_API_KEY);
  }
}
