package com.plunder.plunder.ui.details.tv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowTvSeasonDetails;
import com.plunder.plunder.ui.viewmodels.TvSeasonViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowDetailsViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import org.greenrobot.eventbus.EventBus;

public class TvShowDetailsPresenterImpl extends BaseFragmentPresenter<TvShowDetailsView>
    implements TvShowDetailsPresenter {
  private final CatalogManager catalogManager;
  private TvShow tvShow;

  public TvShowDetailsPresenterImpl(@NonNull TvShowDetailsView view, EventBus eventBus,
      CatalogManager catalogManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
  }

  @Override @Nullable public TvShow getTvShow() {
    return tvShow;
  }

  @Override public void setTvShow(@NonNull TvShow tvShow) {
    Preconditions.checkNotNull(tvShow);
    this.tvShow = tvShow;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    TvShowDetailsView view = getView();

    if (tvShow != null && view != null) {
      TvShowViewModel viewModel = new TvShowViewModel(tvShow);
      view.setBackgroundUri(viewModel.backdropUri());

      catalogManager.tvShowDetails(tvShow).compose(getLifecycleTransformer()).subscribe(details -> {
        TvShowDetailsViewModel detailsViewModel = new TvShowDetailsViewModel(details);
        view.setTvShowDetails(detailsViewModel);
      });
    }
  }

  @Override public void watchSeason(@NonNull TvSeasonViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);
    eventBus.post(new ShowTvSeasonDetails(tvShow, viewModel.tvSeason()));
  }
}
