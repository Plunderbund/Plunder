package com.plunder.plunder.ui.sources;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.SearchResults;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.providers.ProviderClient;
import com.plunder.plunder.providers.ProviderManager;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.DownloadMovieSource;
import com.plunder.plunder.ui.events.DownloadTvShowSource;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.SearchResultViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.plunder.provider.search.SearchResult;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class SourcesPresenterImpl extends BaseFragmentPresenter<SourcesView>
    implements SourcesPresenter, ProviderClient.Listener {
  private final ProviderManager providerManager;
  private ProviderClient providerClient;
  private Movie movie;
  private TvShow tvShow;
  private TvSeason tvSeason;
  private TvEpisode tvEpisode;

  public SourcesPresenterImpl(@NonNull SourcesView view, EventBus eventBus,
      ProviderManager providerManager) {
    super(view, eventBus);
    this.providerManager = providerManager;
  }

  @Nullable @Override public Movie getMovie() {
    return movie;
  }

  @Override public void setMovie(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);
    this.movie = movie;
  }

  @Nullable @Override public TvShow getTvShow() {
    return tvShow;
  }

  @Nullable @Override public TvSeason getTvSeason() {
    return tvSeason;
  }

  @Nullable @Override public TvEpisode getTvEpisode() {
    return tvEpisode;
  }

  @Override public void setTvDetails(@NonNull TvShow tvShow, @NonNull TvSeason tvSeason,
      @NonNull TvEpisode tvEpisode) {
    Preconditions.checkNotNull(tvShow);
    Preconditions.checkNotNull(tvSeason);
    Preconditions.checkNotNull(tvEpisode);

    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
    this.tvEpisode = tvEpisode;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    SourcesView view = getView();

    if (view == null) {
      return;
    }

    if (movie != null) {
      MovieViewModel viewModel = new MovieViewModel(movie);
      view.setBackgroundUri(viewModel.backdropUri());
    } else if (tvShow != null) {
      TvShowViewModel viewModel = new TvShowViewModel(tvShow);
      view.setBackgroundUri(viewModel.backdropUri());
    }

    providerClient = providerManager.createClient(context);
    providerClient.setListener(this);
    providerClient.bind();
  }

  @Override public void onStop() {
    super.onStop();

    SourcesView view = getView();

    if (view == null) {
      return;
    }

    if (providerClient != null) {
      providerClient.unbind();
    }
  }

  @Override public void selectResult(@NonNull SearchResultViewModel result) {
    Preconditions.checkNotNull(result);
    SearchResult searchResult = result.searchResult();

    if (searchResult != null) {
      if (movie != null) {
        eventBus.post(new DownloadMovieSource(movie, searchResult));
      } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
        eventBus.post(new DownloadTvShowSource(tvShow, tvSeason, tvEpisode, searchResult));
      }
    }
  }

  @Override public void onClientReady() {
    beginSearch();
  }

  private void beginSearch() {
    if (providerClient != null) {
      if (movie != null) {
        providerClient.search(movie)
            .compose(getLifecycleTransformer())
            .subscribe(this::onSearchResults,
                err -> Timber.e(err, "Failed to search for movie sources"), this::onSearchComplete);
      } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
        providerClient.search(tvShow, tvEpisode)
            .compose(getLifecycleTransformer())
            .subscribe(this::onSearchResults,
                err -> Timber.e(err, "Failed to search for TV shows sources"),
                this::onSearchComplete);
      }
    }
  }

  private void onSearchResults(SearchResults results) {
    SourcesView view = getView();

    if (results != null && view != null) {
      List<SearchResultViewModel> viewModels = SearchResultViewModel.fromList(results.results());
      view.setLoading(false);
      view.addResults(results.provider(), viewModels);
    }
  }

  private void onSearchComplete() {
    SourcesView view = getView();

    if (view != null) {
      view.setLoading(false);
    }
  }
}
