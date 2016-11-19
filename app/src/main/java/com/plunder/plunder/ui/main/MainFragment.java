package com.plunder.plunder.ui.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.plunder.plunder.R;
import com.plunder.plunder.ui.common.BaseBrowseFragment;
import com.plunder.plunder.ui.presenters.GenreCardPresenter;
import com.plunder.plunder.ui.presenters.MovieCardPresenter;
import com.plunder.plunder.ui.presenters.TvShowCardPresenter;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.squareup.leakcanary.RefWatcher;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.io.File;
import java.util.Collection;
import java.util.Locale;
import javax.inject.Inject;

public class MainFragment extends BaseBrowseFragment implements MainView {
  @Inject @Nullable RefWatcher refWatcher;
  @Inject MainPresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private ArrayObjectAdapter rowsAdapter;

  private AlertDialog dialog;
  private ProgressDialog progressDialog;

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

  @Override public void addMovies(@NonNull Collection<MovieViewModel> movies) {
    Preconditions.checkNotNull(movies);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
    Stream.of(movies).forEach(rowAdapter::add);

    String title = getString(R.string.main_header_popular_movies);
    HeaderItem header = new HeaderItem(title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  @Override public void addTvShows(@NonNull Collection<TvShowViewModel> tvShows) {
    Preconditions.checkNotNull(tvShows);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new TvShowCardPresenter());
    Stream.of(tvShows).forEach(rowAdapter::add);

    String title = getString(R.string.main_header_popular_tv_shows);
    HeaderItem header = new HeaderItem(title);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  @Override public void addGenres(@NonNull Collection<GenreViewModel> genres) {
    Preconditions.checkNotNull(genres);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new GenreCardPresenter());
    Stream.of(genres).forEach(rowAdapter::add);

    String title = getString(R.string.main_header_genres);
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

  @Override public void provideUpdate(String name) {
    if (dialog != null && dialog.isShowing()) {
      dialog.hide();
    }

    if (progressDialog == null || !progressDialog.isShowing()) {
      dialog = new AlertDialog.Builder(getActivity())
          .setTitle("Update available")
          .setMessage(String.format(Locale.getDefault(), "Update Plunder to %s?", name))
          .setPositiveButton("Yes", (where, which) -> {
            new RxPermissions(getActivity())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                  if (granted) {
                    presenter.performUpdate();
                  } else {
                    new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("You must accept the permission before updating")
                        .show();
                  }
                });
          })
          .setNegativeButton("Later", null)
          .setCancelable(true)
          .create();
      dialog.show();
    }
  }

  @Override public void updateStarted() {
    progressDialog = new ProgressDialog(getActivity());
    progressDialog.setMessage("Updating, this could take a few minutes...");
    progressDialog.setIndeterminate(true);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setCancelable(false);
    progressDialog.show();
  }

  @Override public void updateComplete(String filePath) {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.hide();
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(new File(filePath)),
        "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  @Override public void updateFailed() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.hide();
    }

    new AlertDialog.Builder(getActivity())
        .setTitle("Error")
        .setMessage("There was an error while updating")
        .show();
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
