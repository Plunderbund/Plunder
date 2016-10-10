package com.plunder.plunder.ui.details.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.catalog.CatalogManager;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.ShowMovieDetails;
import com.plunder.plunder.ui.events.ShowMovieSources;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class MovieDetailsPresenterImpl extends BaseFragmentPresenter<MovieDetailsView>
    implements MovieDetailsPresenter {
  private final CatalogManager catalogManager;
  private Movie movie;

  public MovieDetailsPresenterImpl(@NonNull MovieDetailsView view, EventBus eventBus,
      CatalogManager catalogManager) {
    super(view, eventBus);
    this.catalogManager = catalogManager;
  }

  @Override @Nullable public Movie getMovie() {
    return movie;
  }

  @Override public void setMovie(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);
    this.movie = movie;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    MovieDetailsView view = getView();

    if (movie != null && view != null) {
      MovieViewModel viewModel = new MovieViewModel(movie);
      view.setBackgroundUri(viewModel.backdropUri());
      view.setMovie(viewModel);

      catalogManager.similarMovies(movie).compose(getLifecycleTransformer()).subscribe(movies -> {
        if (view.hasContext() && movies != null) {
          List<MovieViewModel> viewModels = MovieViewModel.fromList(movies);
          view.setSimilarMovies(viewModels);
        }
      });

      /*catalogManager.movieTrailer(movie)
          .subscribe(video -> {
            VideoViewModel trailer = new VideoViewModel(video);
            view.setTrailer(trailer);
          });*/
    }
  }

  @Override public void onStop() {
    super.onStop();
  }

  @Override public void watch() {
    if (movie != null) {
      eventBus.post(new ShowMovieSources(movie));
    }
  }

  @Override public void watchMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);
    eventBus.post(new ShowMovieDetails(viewModel.movie()));
  }
}
