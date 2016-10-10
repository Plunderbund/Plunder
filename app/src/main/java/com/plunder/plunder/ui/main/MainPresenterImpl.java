package com.plunder.plunder.ui.main;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.google.common.base.Preconditions;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowGenre;
import com.plunder.plunder.ui.events.ShowMovieDetails;
import com.plunder.plunder.ui.events.ShowSearch;
import com.plunder.plunder.ui.events.ShowTvShowDetails;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;

public class MainPresenterImpl extends BaseFragmentPresenter<MainView> implements MainPresenter {
  private static final int BACKGROUND_UPDATE_DELAY = 300;

  private final CatalogManager catalogManager;
  private Uri backgroundUri;
  private Timer backgroundTimer;

  public MainPresenterImpl(@NonNull MainView view, EventBus eventBus,
      CatalogManager catalogManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    catalogManager.popularMovies()
        .flatMap(movies -> catalogManager.popularTvShows(), Pair::new)
        .flatMap(tvShows -> catalogManager.genres(), Pair::new)
        .compose(getLifecycleTransformer())
        .subscribe(pair -> {
          List<Movie> movies = pair.first.first;
          List<TvShow> tvShows = pair.first.second;
          List<Genre> genres = pair.second;

          MainView view = getView();

          if (view != null) {
            if (movies != null) {
              String title = context.getString(R.string.main_header_popular_movies);
              List<MovieViewModel> viewModels = MovieViewModel.fromList(movies);
              view.addMoviesRow(title, viewModels);
            }

            if (tvShows != null) {
              String title = context.getString(R.string.main_header_popular_tv_shows);
              List<TvShowViewModel> viewModels = TvShowViewModel.fromList(tvShows);
              view.addTvShowsRow(title, viewModels);
            }

            if (genres != null) {
              String title = context.getString(R.string.main_header_genres);
              List<GenreViewModel> viewModels = GenreViewModel.fromList(genres);
              view.addGenresRow(title, viewModels);
            }
          }
        });
  }

  @Override public void onStop() {
    super.onStop();

    if (backgroundTimer != null) {
      cancelBackgroundTimer();
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
    backgroundTimer.purge();
    backgroundTimer = null;
  }

  @Override public void selectMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    backgroundUri = viewModel.backdropUri();
    startBackgroundTimer();
  }

  @Override public void watchMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    if (backgroundTimer != null) {
      cancelBackgroundTimer();
    }

    MainView view = getView();

    if (view != null) {
      backgroundUri = viewModel.backdropUri();
      view.setBackgroundUri(backgroundUri);
    }

    eventBus.post(new ShowMovieDetails(viewModel.movie()));
  }

  @Override public void selectTvShow(@NonNull TvShowViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    backgroundUri = viewModel.backdropUri();
    startBackgroundTimer();
  }

  @Override public void watchTvShow(@NonNull TvShowViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    if (backgroundTimer != null) {
      cancelBackgroundTimer();
    }

    MainView view = getView();

    if (view != null) {
      backgroundUri = viewModel.backdropUri();
      view.setBackgroundUri(backgroundUri);
    }

    eventBus.post(new ShowTvShowDetails(viewModel.tvShow()));
  }

  @Override public void selectGenre(@NonNull GenreViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    eventBus.post(new ShowGenre(viewModel.genre()));
  }

  @Override public void search() {
    eventBus.post(new ShowSearch());
  }

  @Override public Uri getBackgroundUri() {
    return backgroundUri;
  }

  private class UpdateBackgroundTask extends TimerTask {
    @Override public void run() {
      if (backgroundUri != null) {
        MainView view = getView();

        if (view != null) {
          runOnView(() -> view.setBackgroundUri(backgroundUri));
        }
      }

      cancelBackgroundTimer();
    }
  }
}

