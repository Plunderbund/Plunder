package com.plunder.plunder.domain;

import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.catalog.CatalogModule;
import dagger.Component;

@DomainScope @Component(
    modules = {
        CatalogModule.class,
    }) public interface DomainComponent {
  CatalogManager getCatalogManager();
}
