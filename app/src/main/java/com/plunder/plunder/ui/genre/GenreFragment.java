package com.plunder.plunder.ui.genre;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.common.base.Preconditions;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.ui.common.BaseBrowseFragment;
import com.plunder.plunder.ui.presenters.MovieCardPresenter;
import com.plunder.plunder.ui.presenters.TvShowCardPresenter;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.squareup.leakcanary.RefWatcher;
import java.util.Collection;
import java.util.HashMap;
import javax.inject.Inject;

public class GenreFragment extends BaseBrowseFragment implements GenreView {
  private final static String ARGS_GENRE = "genre";

  private final static String STATE_GENRE = "genre";

  @Inject @Nullable RefWatcher refWatcher;
  @Inject GenrePresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;

  private ArrayObjectAdapter rowsAdapter;
  private HashMap<Integer, Integer> rows;

  private Unbinder unbinder;

  public static GenreFragment newInstance(Genre genre) {
    GenreFragment fragment = new GenreFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_GENRE, genre);

    GenreViewModel genreViewModel = new GenreViewModel(genre);
    args = BrowseFragment.createArgs(args, genreViewModel.name(), HEADERS_HIDDEN);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerGenreComponent.builder()
          .appComponent(appComponent)
          .genreModule(new GenreModule(this))
          .build()
          .inject(this);
    }

    rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

    presenter.initialize();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Activity activity = getActivity();

    if (activity == null) {
      return;
    }

    Genre genre;

    if (savedInstanceState != null) {
      genre = savedInstanceState.getParcelable(STATE_GENRE);
    } else {
      genre = getArguments().getParcelable(ARGS_GENRE);
    }

    rows = new HashMap<>();

    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());

    displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    setAdapter(rowsAdapter);

    setOnSearchClickedListener(view -> presenter.search());
    setOnItemViewSelectedListener(this::onItemSelected);
    setOnItemViewClickedListener(this::onItemClicked);

    unbinder = ButterKnife.bind(this, getActivity());

    if (genre != null) {
      presenter.setGenre(genre);
    } else {
      getActivity().finish();
      return;
    }

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

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
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
    outState.putParcelable(STATE_GENRE, presenter.getGenre());
    super.onSaveInstanceState(outState);
  }

  private void addRow(int id, String title, @NonNull Presenter presenter) {
    Preconditions.checkNotNull(presenter);

    if (rowsAdapter != null) {
      HeaderItem header = new HeaderItem(title);
      ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenter);
      rowsAdapter.add(new ListRow(header, adapter));

      rows.put(id, rowsAdapter.size() - 1);
    }
  }

  @Override public void addItems(int id, Collection<?> items) {
    Preconditions.checkNotNull(items);

    if (rowsAdapter != null && rows.containsKey(id)) {
      int index = rows.get(id);
      ListRow row = (ListRow) rowsAdapter.get(index);
      ArrayObjectAdapter adapter = (ArrayObjectAdapter) row.getAdapter();
      int positionStart = adapter.size() > 1 ? adapter.size() - 1 : 0;
      Stream.of(items).forEach(adapter::add);
      adapter.notifyArrayItemRangeChanged(positionStart, items.size());
    }
  }

  @Override public void addMovieCategory(int id, String title) {
    addRow(id, title, new MovieCardPresenter());
  }

  @Override public void addTvCategory(int id, String title) {
    addRow(id, title, new TvShowCardPresenter());
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

    if (rowsAdapter != null && row instanceof ListRow) {
      ListRow listRow = (ListRow) row;
      ObjectAdapter objectAdapter = listRow.getAdapter();

      if (objectAdapter instanceof ArrayObjectAdapter) {
        ArrayObjectAdapter arrayObjectAdapter = (ArrayObjectAdapter) objectAdapter;
        int index = arrayObjectAdapter.indexOf(item);

        if (arrayObjectAdapter.size() - index <= 5) {
          int rowIndex = rowsAdapter.indexOf(row);
          presenter.nextPage(rowIndex);
        }
      }
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
