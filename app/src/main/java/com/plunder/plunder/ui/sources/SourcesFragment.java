package com.plunder.plunder.ui.sources;

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
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.common.base.Preconditions;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseDetailsFragment;
import com.plunder.plunder.ui.presenters.SourceCardPresenter;
import com.plunder.plunder.ui.viewmodels.SearchResultViewModel;
import com.squareup.leakcanary.RefWatcher;
import java.util.List;
import javax.inject.Inject;

public class SourcesFragment extends BaseDetailsFragment implements SourcesView {
  private final static String ARGS_MOVIE = "movie";
  private final static String ARGS_TV_SHOW = "tvShow";
  private final static String ARGS_TV_SEASON = "tvSeason";
  private final static String ARGS_TV_EPISODE = "tvEpisode";

  private final static String STATE_MOVIE = "movie";
  private final static String STATE_TV_SHOW = "tvShow";
  private final static String STATE_TV_SEASON = "tvSeason";
  private final static String STATE_TV_EPISODE = "tvEpisode";

  @Inject @Nullable RefWatcher refWatcher;
  @Inject SourcesPresenter presenter;

  @BindView(R.id.source_progress) ProgressBar progressBar;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private ArrayObjectAdapter rowsAdapter;
  private Unbinder unbinder;

  public static SourcesFragment newInstance(Movie movie) {
    SourcesFragment fragment = new SourcesFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_MOVIE, movie);
    fragment.setArguments(args);

    return fragment;
  }

  public static SourcesFragment newInstance(TvShow tvShow, TvSeason tvSeason, TvEpisode tvEpisode) {
    SourcesFragment fragment = new SourcesFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_TV_SHOW, tvShow);
    args.putParcelable(ARGS_TV_SEASON, tvSeason);
    args.putParcelable(ARGS_TV_EPISODE, tvEpisode);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerSourcesComponent.builder()
          .appComponent(appComponent)
          .sourcesModule(new SourcesModule(this))
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

    Movie movie;
    TvShow tvShow;
    TvSeason tvSeason;
    TvEpisode tvEpisode;

    if (savedInstanceState != null) {
      movie = savedInstanceState.getParcelable(STATE_MOVIE);
      tvShow = savedInstanceState.getParcelable(STATE_TV_SHOW);
      tvSeason = savedInstanceState.getParcelable(STATE_TV_SEASON);
      tvEpisode = savedInstanceState.getParcelable(STATE_TV_SHOW);
    } else {
      movie = getArguments().getParcelable(ARGS_MOVIE);
      tvShow = getArguments().getParcelable(ARGS_TV_SHOW);
      tvSeason = getArguments().getParcelable(ARGS_TV_SEASON);
      tvEpisode = getArguments().getParcelable(ARGS_TV_EPISODE);
    }

    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());

    displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    setAdapter(rowsAdapter);
    setOnItemViewClickedListener(this::onItemClicked);

    unbinder = ButterKnife.bind(this, getActivity());

    if (movie != null) {
      presenter.setMovie(movie);
    } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
      presenter.setTvDetails(tvShow, tvSeason, tvEpisode);
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
    outState.putParcelable(STATE_MOVIE, presenter.getMovie());
    outState.putParcelable(STATE_TV_SHOW, presenter.getTvShow());
    outState.putParcelable(STATE_TV_SEASON, presenter.getTvSeason());
    outState.putParcelable(STATE_TV_EPISODE, presenter.getTvEpisode());
    super.onSaveInstanceState(outState);
  }

  @Override public void setBackgroundUri(Uri uri) {
    if (uri == null || displayMetrics == null) {
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

  @Override
  public void addResults(String providerName, @NonNull List<SearchResultViewModel> results) {
    Preconditions.checkNotNull(results);

    ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(new SourceCardPresenter());
    Stream.of(results).forEach(rowAdapter::add);

    HeaderItem header = new HeaderItem(providerName);
    rowsAdapter.add(new ListRow(header, rowAdapter));
  }

  private void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
      RowPresenter.ViewHolder rowViewHolder, Object row) {
    if (item instanceof SearchResultViewModel) {
      SearchResultViewModel viewModel = (SearchResultViewModel) item;
      presenter.selectResult(viewModel);
    }
  }

  @Override public void setLoading(boolean loading) {
    if (progressBar != null) {
      if (loading) {
        progressBar.setVisibility(View.VISIBLE);
      } else {
        progressBar.setVisibility(View.GONE);
      }
    }
  }
}
