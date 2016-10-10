package com.plunder.plunder.ui.search;

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
import android.support.v17.leanback.widget.ObjectAdapter;
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
import com.plunder.plunder.R;
import com.plunder.plunder.ui.common.BaseSearchFragment;
import com.plunder.plunder.ui.presenters.MovieCardPresenter;
import com.plunder.plunder.ui.presenters.TvShowCardPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.squareup.leakcanary.RefWatcher;
import java.util.List;
import javax.inject.Inject;

public class SearchFragment extends BaseSearchFragment
    implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider, SearchView {
  @Inject @Nullable RefWatcher refWatcher;
  @Inject SearchPresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private ArrayObjectAdapter rowsAdapter;

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerSearchComponent.builder()
          .appComponent(appComponent)
          .searchModule(new SearchModule(this))
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
    backgroundManager.setBitmap(null);

    displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    setSearchResultProvider(this);

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

  @Override public ObjectAdapter getResultsAdapter() {
    return rowsAdapter;
  }

  @Override public boolean onQueryTextChange(String newQuery) {
    if (newQuery != null) {
      presenter.setSearchQuery(newQuery);
    }

    return true;
  }

  @Override public boolean onQueryTextSubmit(String query) {
    if (query != null) {
      presenter.setSearchQuery(query);
    }

    return true;
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

  @Override public void clearResults() {
    if (rowsAdapter != null) {
      rowsAdapter.clear();
    }
  }

  @Override public void setMovieResults(@NonNull List<MovieViewModel> results) {
    Preconditions.checkNotNull(results);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
    Stream.of(results).forEach(rowAdapter::add);

    String title = getString(R.string.search_headers_movies);
    HeaderItem header = new HeaderItem(0, title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  @Override public void setTvShowResults(@NonNull List<TvShowViewModel> results) {
    Preconditions.checkNotNull(results);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new TvShowCardPresenter());
    Stream.of(results).forEach(rowAdapter::add);

    String title = getString(R.string.search_headers_tv_shows);
    HeaderItem header = new HeaderItem(1, title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
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
    }
  }
}
