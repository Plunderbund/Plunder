package com.plunder.plunder.ui.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.common.base.Preconditions;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.ui.common.BaseBrowseFragment;
import com.plunder.plunder.ui.presenters.GenreCardPresenter;
import com.plunder.plunder.ui.presenters.MovieCardPresenter;
import com.plunder.plunder.ui.presenters.TvShowCardPresenter;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.squareup.leakcanary.RefWatcher;
import java.util.Collection;
import javax.inject.Inject;

public class MainFragment extends BaseBrowseFragment implements MainView {
  @Inject @Nullable RefWatcher refWatcher;
  @Inject MainPresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private ArrayObjectAdapter rowsAdapter;

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerMainComponent.builder()
          .appComponent(appComponent)
          .mainModule(new MainModule(this))
          .build()
          .inject(this);
    }

    rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    presenter.initialize();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Activity activity = getActivity();
    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());

    displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    setAdapter(rowsAdapter);

    setOnSearchClickedListener(view -> presenter.search());
    setOnItemViewSelectedListener(this::onItemSelected);
    setOnItemViewClickedListener(this::onItemClicked);

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

  @Override public void addMoviesRow(String title, @NonNull Collection<MovieViewModel> movies) {
    Preconditions.checkNotNull(movies);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
    Stream.of(movies).forEach(rowAdapter::add);

    HeaderItem header = new HeaderItem(title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  @Override public void addTvShowsRow(String title, @NonNull Collection<TvShowViewModel> tvShows) {
    Preconditions.checkNotNull(tvShows);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new TvShowCardPresenter());
    Stream.of(tvShows).forEach(rowAdapter::add);

    HeaderItem header = new HeaderItem(title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  @Override public void addGenresRow(String title, @NonNull Collection<GenreViewModel> genres) {
    Preconditions.checkNotNull(genres);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new GenreCardPresenter());
    Stream.of(genres).forEach(rowAdapter::add);

    HeaderItem header = new HeaderItem(title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
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
            if (presenter.getBackgroundUri().equals(uri) && backgroundManager != null) {
              backgroundManager.setBitmap(resource);
            }
          }
        });
  }

  private void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
      RowPresenter.ViewHolder rowViewHolder, Row row) {
    if (item instanceof MovieViewModel) {
      MovieViewModel viewModel = (MovieViewModel) item;
      presenter.selectMovie(viewModel);
    } else if (item instanceof TvShowViewModel) {
      TvShowViewModel viewModel = (TvShowViewModel) item;
      presenter.selectTvShow(viewModel);
    }
  }

  private void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
      RowPresenter.ViewHolder rowViewHolder, Row row) {
    if (item instanceof MovieViewModel) {
      MovieViewModel viewModel = (MovieViewModel) item;
      presenter.watchMovie(viewModel);
    } else if (item instanceof TvShowViewModel) {
      TvShowViewModel viewModel = (TvShowViewModel) item;
      presenter.watchTvShow(viewModel);
    } else if (item instanceof GenreViewModel) {
      GenreViewModel viewModel = (GenreViewModel) item;
      presenter.selectGenre(viewModel);
    }
  }
}
