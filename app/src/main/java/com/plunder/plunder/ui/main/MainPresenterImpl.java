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
import com.plunder.plunder.update.Update;
import com.plunder.plunder.update.UpdateManager;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class MainPresenterImpl extends BaseFragmentPresenter<MainView> implements MainPresenter {
  private static final int BACKGROUND_UPDATE_DELAY = 300;

  private final CatalogManager catalogManager;
  private final UpdateManager updateManager;

  private Uri backgroundUri;
  private Timer backgroundTimer;

  public MainPresenterImpl(@NonNull MainView view, EventBus eventBus,
      CatalogManager catalogManager, UpdateManager updateManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
    this.updateManager = updateManager;
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
              List<MovieViewModel> viewModels = MovieViewModel.fromList(movies);
              view.addMovies(viewModels);
            }

            if (tvShows != null) {
              List<TvShowViewModel> viewModels = TvShowViewModel.fromList(tvShows);
              view.addTvShows(viewModels);
            }

            if (genres != null) {
              List<GenreViewModel> viewModels = GenreViewModel.fromList(genres);
              view.addGenres(viewModels);
            }
          }
        });
  }

  @Override public void onStart() {
    super.onStart();

    updateManager.fetchUpdate()
        .compose(getLifecycleTransformer())
        .subscribe(update -> {
          MainView view = getView();

          if (view != null) {
            view.provideUpdate(update.name());
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

  @Override public void performUpdate() {
    MainView view = getView();

    if (view != null) {
      view.updateStarted();
    }

    updateManager.downloadUpdate()
        .compose(getLifecycleTransformer())
        .subscribe(filePath -> {
          if (view != null) {
            view.updateComplete(filePath);
          }
        }, err -> {
          Timber.e(err, "Failed to update");

          if (view != null) {
            view.updateFailed();
          }
        });
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

