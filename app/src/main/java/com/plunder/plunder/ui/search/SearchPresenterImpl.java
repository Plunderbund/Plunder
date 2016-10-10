package com.plunder.plunder.ui.search;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowMovieDetails;
import com.plunder.plunder.ui.events.ShowTvShowDetails;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class SearchPresenterImpl extends BaseFragmentPresenter<SearchView>
    implements SearchPresenter {
  private static final int BACKGROUND_UPDATE_DELAY = 300;
  private static final int SEARCH_DELAY = 500;

  private final CatalogManager catalogManager;
  private String query;
  private Timer searchTimer;
  private Uri backgroundUri;
  private Timer backgroundTimer;

  public SearchPresenterImpl(@NonNull SearchView view, EventBus eventBus,
      CatalogManager catalogManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
  }

  private void startSearchTimer() {
    if (searchTimer != null) {
      cancelSearchTimer();
    }

    searchTimer = new Timer();
    searchTimer.schedule(new SearchTask(), SEARCH_DELAY);
  }

  private void cancelSearchTimer() {
    if (searchTimer != null) {

      searchTimer.cancel();
      searchTimer = null;
    }
  }

  private void startBackgroundTimer() {
    if (backgroundTimer != null) {
      cancelBackgroundTimer();
    }

    backgroundTimer = new Timer();
    backgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
  }

  private void cancelBackgroundTimer() {
    backgroundTimer.cancel();
    backgroundTimer = null;
  }

  @Override public void setSearchQuery(@NonNull String query) {
    Preconditions.checkNotNull(query);

    this.query = query;
    startSearchTimer();
  }

  @Override public void selectMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    backgroundUri = viewModel.backdropUri();
    startBackgroundTimer();
  }

  @Override public void watchMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);
    eventBus.post(new ShowMovieDetails(viewModel.movie()));
  }

  @Override public void selectTvShow(@NonNull TvShowViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    backgroundUri = viewModel.backdropUri();
    startBackgroundTimer();
  }

  @Override public void watchTvShow(@NonNull TvShowViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);
    eventBus.post(new ShowTvShowDetails(viewModel.tvShow()));
  }

  private class SearchTask extends TimerTask {
    @Override public void run() {
      SearchView view = getView();

      if (view == null) {
        return;
      }

      runOnView(view::clearResults);

      catalogManager.searchMovies(query)
          .flatMap(movies -> catalogManager.searchTvShows(query), Pair::new)
          .compose(getLifecycleTransformer())
          .subscribe(pair -> {
            List<Movie> movieResults = pair.first;
            List<TvShow> tvResults = pair.second;

            view.setMovieResults(MovieViewModel.fromList(movieResults));
            view.setTvShowResults(TvShowViewModel.fromList(tvResults));
            cancelSearchTimer();
          }, err -> {
            cancelSearchTimer();
            Timber.e(err);
          }, SearchPresenterImpl.this::cancelSearchTimer);
    }
  }

  private class UpdateBackgroundTask extends TimerTask {
    @Override public void run() {
      if (backgroundUri != null) {
        SearchView view = getView();

        if (view != null) {
          runOnView(() -> view.setBackgroundUri(backgroundUri));
        }
      }

      cancelBackgroundTimer();
    }
  }
}
