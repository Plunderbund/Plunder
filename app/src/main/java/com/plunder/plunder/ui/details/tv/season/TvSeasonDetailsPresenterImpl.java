package com.plunder.plunder.ui.details.tv.season;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowTvShowSources;
import com.plunder.plunder.ui.viewmodels.TvEpisodeViewModel;
import com.plunder.plunder.ui.viewmodels.TvSeasonViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class TvSeasonDetailsPresenterImpl extends BaseFragmentPresenter<TvSeasonDetailsView>
    implements TvSeasonDetailsPresenter {
  private final CatalogManager catalogManager;
  private TvShow tvShow;
  private TvSeason tvSeason;

  public TvSeasonDetailsPresenterImpl(@NonNull TvSeasonDetailsView view, EventBus eventBus,
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

  @Nullable @Override public TvSeason getTvSeason() {
    return tvSeason;
  }

  @Override public void setTvSeason(@NonNull TvSeason tvSeason) {
    Preconditions.checkNotNull(tvSeason);
    this.tvSeason = tvSeason;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    TvSeasonDetailsView view = getView();

    if (tvShow != null && tvSeason != null && view != null) {
      TvShowViewModel viewModel = new TvShowViewModel(tvShow);
      view.setBackgroundUri(viewModel.backdropUri());

      catalogManager.tvSeasonEpisodes(tvShow, tvSeason)
          .compose(getLifecycleTransformer())
          .subscribe(episodes -> {
            TvSeasonViewModel seasonViewModel = new TvSeasonViewModel(tvSeason);
            List<TvEpisodeViewModel> episodeViewModels = TvEpisodeViewModel.fromList(episodes);
            view.setTvEpisodes(seasonViewModel, episodeViewModels);
          });
    }
  }

  @Override public void onStop() {
    super.onStop();
  }

  @Override public void watch(TvEpisodeViewModel viewModel) {
    if (tvShow != null) {
      eventBus.post(new ShowTvShowSources(tvShow, tvSeason, viewModel.tvEpisode()));
    }
  }
}
