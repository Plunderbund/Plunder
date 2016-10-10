package com.plunder.plunder.ui.details.movies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.DisplayMetrics;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.common.base.Preconditions;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.ui.common.BaseDetailsFragment;
import com.plunder.plunder.ui.presenters.MovieCardPresenter;
import com.plunder.plunder.ui.presenters.MovieDetailsDescriptionPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.VideoViewModel;
import com.plunder.plunder.utils.DrawableUtils;
import com.squareup.leakcanary.RefWatcher;
import java.util.List;
import javax.inject.Inject;

public class MovieDetailsFragment extends BaseDetailsFragment implements MovieDetailsView {
  private final static String ARGS_MOVIE = "movie";
  private final static String STATE_MOVIE = "movie";

  private final static int ACTION_WATCH = 0;
  private final static int ACTION_TRAILER = 0;

  @Inject @Nullable RefWatcher refWatcher;
  @Inject MovieDetailsPresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private ArrayObjectAdapter rowsAdapter;
  private SparseArrayObjectAdapter actionsAdapter;

  public static MovieDetailsFragment newInstance(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);

    MovieDetailsFragment fragment = new MovieDetailsFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_MOVIE, movie);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerMovieDetailsComponent.builder()
          .appComponent(appComponent)
          .movieDetailsModule(new MovieDetailsModule(this))
          .build()
          .inject(this);
    }

    presenter.initialize();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Movie movie;

    if (savedInstanceState != null) {
      movie = savedInstanceState.getParcelable(STATE_MOVIE);
    } else {
      movie = getMovieFromArguments();
      prepareEntranceTransition();
    }

    Activity activity = getActivity();

    if (movie == null) {
      if (activity != null) {
        activity.finish();
      }

      return;
    }

    setOnItemViewClickedListener(this::onItemClicked);

    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());

    displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    presenter.setMovie(movie);
    presenter.onCreated(activity);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onStop() {
    backgroundManager.release();
    presenter.onStop();

    super.onStop();
  }

  @Override public void onDestroy() {
    presenter.onDestroy();

    if (refWatcher != null) {
      refWatcher.watch(this);
    }

    backgroundManager = null;

    super.onDestroy();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(STATE_MOVIE, presenter.getMovie());
    super.onSaveInstanceState(outState);
  }

  @Nullable private Movie getMovieFromArguments() {
    Bundle arguments = getArguments();

    if (arguments != null) {
      return arguments.getParcelable(ARGS_MOVIE);
    }

    return null;
  }

  @Override public void setMovie(@NonNull MovieViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    FullWidthDetailsOverviewRowPresenter detailsPresenter =
        new FullWidthDetailsOverviewRowPresenter(new MovieDetailsDescriptionPresenter());
    DetailsOverviewRow detailsRow = new DetailsOverviewRow(viewModel);

    Uri posterUri = viewModel.largePosterPath();

    if (posterUri != null) {
      detailsRow.setImageBitmap(getActivity(), DrawableUtils.createTransparentBitmap(500, 750));

      Glide.with(this).load(posterUri).asBitmap().into(new SimpleTarget<Bitmap>() {
        @Override public void onResourceReady(Bitmap resource,
            GlideAnimation<? super Bitmap> glideAnimation) {
          detailsRow.setImageBitmap(getActivity(), resource);
        }
      });
    }

    actionsAdapter = new SparseArrayObjectAdapter();
    actionsAdapter.set(0, new Action(ACTION_WATCH, getString(R.string.details_actions_watch)));
    detailsRow.setActionsAdapter(actionsAdapter);
    detailsPresenter.setOnActionClickedListener(this::onActionClicked);
    detailsPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_SMALL);

    ClassPresenterSelector selector = new ClassPresenterSelector();
    selector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    selector.addClassPresenter(ListRow.class, new ListRowPresenter());

    rowsAdapter = new ArrayObjectAdapter(selector);
    setAdapter(rowsAdapter);
    rowsAdapter.add(detailsRow);

    startEntranceTransition();
  }

  @Override public void setTrailer(@NonNull VideoViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);
    addTrailerAction();
  }

  @Override public void setBackgroundUri(@Nullable Uri uri) {
    if (uri == null || displayMetrics == null) {
      if (backgroundManager != null) {
        backgroundManager.setBitmap(null);
      }

      return;
    }

    int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;

    Glide.with(this)
        .load(uri)
        .asBitmap()
        .centerCrop()
        .into(new SimpleTarget<Bitmap>(width, height) {
          @Override public void onResourceReady(Bitmap resource,
              GlideAnimation<? super Bitmap> glideAnimation) {
            if (backgroundManager != null) {
              backgroundManager.setBitmap(resource);
            }
          }
        });
  }

  @Override public void setSimilarMovies(@NonNull List<MovieViewModel> movies) {
    Preconditions.checkNotNull(movies);

    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
    Stream.of(movies).forEach(listRowAdapter::add);

    HeaderItem header = new HeaderItem(0, "Similar");
    rowsAdapter.add(new ListRow(header, listRowAdapter));
  }

  private void onActionClicked(Action action) {
    if (action.getId() == ACTION_WATCH) {
      presenter.watch();
    }
  }

  private void addTrailerAction() {
    String trailerAction = getString(R.string.details_actions_trailer);
    actionsAdapter.set(1, new Action(ACTION_TRAILER, trailerAction));
  }

  private void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
      RowPresenter.ViewHolder rowViewHolder, Object row) {
    if (item instanceof MovieViewModel) {
      MovieViewModel viewModel = (MovieViewModel) item;
      presenter.watchMovie(viewModel);
    }
  }
}
