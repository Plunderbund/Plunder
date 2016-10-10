package com.plunder.plunder.ui.details.tv;

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
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.common.base.Preconditions;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseDetailsFragment;
import com.plunder.plunder.ui.presenters.SeasonsCardPresenter;
import com.plunder.plunder.ui.viewmodels.TvSeasonViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowDetailsViewModel;
import com.squareup.leakcanary.RefWatcher;
import javax.inject.Inject;

public class TvShowDetailsFragment extends BaseDetailsFragment implements TvShowDetailsView {
  private final static String ARGS_TV_SHOW = "tvShow";
  private final static String STATE_TV_SHOW = "tvShow";

  @Inject @Nullable RefWatcher refWatcher;
  @Inject TvShowDetailsPresenter presenter;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;

  public static TvShowDetailsFragment newInstance(@NonNull TvShow tvShow) {
    Preconditions.checkNotNull(tvShow);

    TvShowDetailsFragment fragment = new TvShowDetailsFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_TV_SHOW, tvShow);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerTvShowDetailsComponent.builder()
          .appComponent(appComponent)
          .tvShowDetailsModule(new TvShowDetailsModule(this))
          .build()
          .inject(this);
    }

    presenter.initialize();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TvShow tvShow;

    if (savedInstanceState != null) {
      tvShow = savedInstanceState.getParcelable(STATE_TV_SHOW);
    } else {
      tvShow = getTvShowFromArguments();
    }

    Activity activity = getActivity();

    if (tvShow == null) {
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

    presenter.setTvShow(tvShow);
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
    outState.putParcelable(STATE_TV_SHOW, presenter.getTvShow());
    super.onSaveInstanceState(outState);
  }

  @Nullable private TvShow getTvShowFromArguments() {
    Bundle arguments = getArguments();

    if (arguments != null) {
      return arguments.getParcelable(ARGS_TV_SHOW);
    }

    return null;
  }

  @Override public void setTvShowDetails(@NonNull TvShowDetailsViewModel viewModel) {
    Preconditions.checkNotNull(viewModel);

    HeaderItem header = new HeaderItem(viewModel.name());
    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new SeasonsCardPresenter());
    Stream.of(viewModel.seasons()).forEach(rowAdapter::add);

    ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    rowsAdapter.add(new ListRow(header, rowAdapter));
    setAdapter(rowsAdapter);
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

  private void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
      RowPresenter.ViewHolder rowViewHolder, Object row) {
    if (item instanceof TvSeasonViewModel) {
      TvSeasonViewModel viewModel = (TvSeasonViewModel) item;
      presenter.watchSeason(viewModel);
    }
  }
}
