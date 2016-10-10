package com.plunder.plunder.ui.download;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseFragment;
import com.plunder.provider.search.SearchResult;
import com.squareup.leakcanary.RefWatcher;
import java.util.Locale;
import javax.inject.Inject;

public class DownloadFragment extends BaseFragment implements DownloadView {
  private final static String ARGS_MOVIE = "movie";
  private final static String ARGS_TV_SHOW = "tvShow";
  private final static String ARGS_TV_SEASON = "tvSeason";
  private final static String ARGS_TV_EPISODE = "tvEpisode";
  private final static String ARGS_SEARCH_RESULT = "searchResult";

  private final static String STATE_MOVIE = "movie";
  private final static String STATE_TV_SHOW = "tvShow";
  private final static String STATE_TV_SEASON = "tvSeason";
  private final static String STATE_TV_EPISODE = "tvEpisode";
  private final static String STATE_SEARCH_RESULT = "searchResult";

  @Inject @Nullable RefWatcher refWatcher;
  @Inject DownloadPresenter presenter;

  @BindView(R.id.download_progress) ProgressBar downloadProgressBar;
  @BindView(R.id.download_progress_label) TextView downloadProgressLabel;
  @BindView(R.id.download_progress_small_label) TextView downloadProgressSmallLabel;

  private BackgroundManager backgroundManager;
  private DisplayMetrics displayMetrics;
  private Unbinder unbinder;

  public static DownloadFragment newInstance(Movie movie, SearchResult searchResult) {
    DownloadFragment fragment = new DownloadFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_MOVIE, movie);
    args.putParcelable(ARGS_SEARCH_RESULT, searchResult);
    fragment.setArguments(args);

    return fragment;
  }

  public static DownloadFragment newInstance(TvShow tvShow, TvSeason tvSeason, TvEpisode tvEpisode,
      SearchResult searchResult) {
    DownloadFragment fragment = new DownloadFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_TV_SHOW, tvShow);
    args.putParcelable(ARGS_TV_SEASON, tvSeason);
    args.putParcelable(ARGS_TV_EPISODE, tvEpisode);
    args.putParcelable(ARGS_SEARCH_RESULT, searchResult);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerDownloadComponent.builder()
          .appComponent(appComponent)
          .downloadModule(new DownloadModule(this))
          .build()
          .inject(this);
    }

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
    SearchResult searchResult;

    if (savedInstanceState != null) {
      movie = savedInstanceState.getParcelable(STATE_MOVIE);
      tvShow = savedInstanceState.getParcelable(STATE_TV_SHOW);
      tvSeason = savedInstanceState.getParcelable(STATE_TV_SEASON);
      tvEpisode = savedInstanceState.getParcelable(STATE_TV_SHOW);
      searchResult = savedInstanceState.getParcelable(STATE_SEARCH_RESULT);
    } else {
      movie = getArguments().getParcelable(ARGS_MOVIE);
      tvShow = getArguments().getParcelable(ARGS_TV_SHOW);
      tvSeason = getArguments().getParcelable(ARGS_TV_SEASON);
      tvEpisode = getArguments().getParcelable(ARGS_TV_EPISODE);
      searchResult = getArguments().getParcelable(ARGS_SEARCH_RESULT);
    }

    if (searchResult == null) {
      activity.finish();
      return;
    }

    backgroundManager = BackgroundManager.getInstance(activity);
    backgroundManager.attach(activity.getWindow());

    displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    if (movie != null) {
      presenter.setMovie(movie);
    } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
      presenter.setTvDetails(tvShow, tvSeason, tvEpisode);
    } else {
      getActivity().finish();
      return;
    }

    presenter.setSearchResult(searchResult);
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

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_download, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(STATE_MOVIE, presenter.getMovie());
    outState.putParcelable(STATE_TV_SHOW, presenter.getTvShow());
    outState.putParcelable(STATE_TV_SEASON, presenter.getTvSeason());
    outState.putParcelable(STATE_TV_EPISODE, presenter.getTvEpisode());
    outState.putParcelable(STATE_SEARCH_RESULT, presenter.getSearchResult());
    super.onSaveInstanceState(outState);
  }

  @Override public void setBackgroundUri(@Nullable Uri uri) {
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

  @Override public void setProgress(int progress, float downloadSpeed) {
    if (downloadProgressBar != null) {
      if (downloadProgressBar.getProgress() == 0) {
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setMax(100);

        downloadProgressLabel.setText(R.string.download_progress_buffering);
      }

      downloadProgressBar.setProgress(progress);
    }

    if (downloadProgressSmallLabel != null) {
      downloadProgressSmallLabel.setText(
          String.format(Locale.getDefault(), "%d%% - %s/sec", progress,
              Formatter.formatFileSize(getActivity(), (long) downloadSpeed)));
    }
  }
}
