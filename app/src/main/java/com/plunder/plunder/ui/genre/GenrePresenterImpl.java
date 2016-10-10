package com.plunder.plunder.ui.genre;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowMovieDetails;
import com.plunder.plunder.ui.events.ShowSearch;
import com.plunder.plunder.ui.events.ShowTvShowDetails;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class GenrePresenterImpl extends BaseFragmentPresenter<GenreView> implements GenrePresenter {
  private static final int BACKGROUND_UPDATE_DELAY = 300;

  private static final int POPULAR_MOVIES_ROW = 0;
  private static final int POPULAR_TV_SHOWS_ROW = 1;

  private final CatalogManager catalogManager;
  private Genre genre;
  private Uri backgroundUri;
  private Timer backgroundTimer;

  private boolean requestingMovies;
  private boolean requestingTvShows;

  private int popularMoviesPage = 1;
  private int popularTvShowsPage = 1;

  public GenrePresenterImpl(@NonNull GenreView view, EventBus eventBus,
      CatalogManager catalogManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
  }

  @Override public void setGenre(@NonNull Genre genre) {
    Preconditions.checkNotNull(genre);
    this.genre = genre;
  }

  @Nullable @Override public Genre getGenre() {
    return genre;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    if (genre == null) {
      return;
    }

    GenreView view = getView();

    if (view == null) {
      return;
    }

    if (genre.movieId() != null) {
      String title = context.getString(R.string.main_header_popular_movies);
      view.addMovieCategory(POPULAR_MOVIES_ROW, title);
      addPopularMovies();
    }

    if (genre.tvId() != null) {
      String title = context.getString(R.string.main_header_popular_tv_shows);
      view.addTvCategory(POPULAR_TV_SHOWS_ROW, title);
      addPopularTvShows();
    }
  }

  private void addPopularMovies() {
    if (!requestingMovies) {
      requestingMovies = true;

      catalogManager.popularMovies(genre, popularMoviesPage)
          .compose(getLifecycleTransformer())
          .subscribe(movies -> {
            GenreView genreView = getView();

            if (genreView != null && movies != null) {
              List<MovieViewModel> viewModels = MovieViewModel.fromList(movies);
              genreView.addItems(POPULAR_MOVIES_ROW, viewModels);
            }

            popularMoviesPage += 1;
          }, err -> {
            Timber.e(err, "Failed to request popular movies");
            requestingMovies = false;
          }, () -> requestingMovies = false);
    }
  }

  private void addPopularTvShows() {
    if (!requestingTvShows) {
      requestingTvShows = true;

      catalogManager.popularTvShows(genre, popularTvShowsPage)
          .compose(getLifecycleTransformer())
          .subscribe(tvShows -> {
            GenreView genreView = getView();

            if (genreView != null && tvShows != null) {
              List<TvShowViewModel> viewModels = TvShowViewModel.fromList(tvShows);
              genreView.addItems(POPULAR_TV_SHOWS_ROW, viewModels);
            }

            popularTvShowsPage += 1;
          }, err -> {
            Timber.e(err, "Failed to request popular TV shows");
            requestingTvShows = false;
          }, () -> requestingTvShows = false);
    }
  }

  @Override public void nextPage(int position) {
    switch (position) {
      case POPULAR_MOVIES_ROW:
        addPopularMovies();
        break;
      case POPULAR_TV_SHOWS_ROW:
        addPopularTvShows();
        break;
    }
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

    GenreView view = getView();

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

    GenreView view = getView();

    if (view != null) {
      backgroundUri = viewModel.backdropUri();
      view.setBackgroundUri(backgroundUri);
    }

    eventBus.post(new ShowTvShowDetails(viewModel.tvShow()));
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
        GenreView view = getView();

        if (view != null) {
          runOnView(() -> view.setBackgroundUri(backgroundUri));
        }
      }

      cancelBackgroundTimer();
    }
  }
}
