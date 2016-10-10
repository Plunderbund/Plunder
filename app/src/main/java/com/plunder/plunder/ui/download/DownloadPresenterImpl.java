package com.plunder.plunder.ui.download;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.downloads.DownloadClient;
import com.plunder.plunder.downloads.DownloadManager;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.events.PlayMovieSource;
import com.plunder.plunder.ui.events.PlayTvShowSource;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;
import com.plunder.provider.search.SearchResult;
import org.greenrobot.eventbus.EventBus;

public class DownloadPresenterImpl extends BaseFragmentPresenter<DownloadView>
    implements DownloadPresenter, DownloadClient.Listener {
  private final DownloadManager downloadManager;
  private DownloadClient downloadClient;
  private boolean downloadReady;
  private Movie movie;
  private TvShow tvShow;
  private TvSeason tvSeason;
  private TvEpisode tvEpisode;
  private SearchResult searchResult;

  public DownloadPresenterImpl(@NonNull DownloadView view, EventBus eventBus,
      DownloadManager downloadManager) {
    super(view, eventBus);
    this.downloadManager = downloadManager;
  }

  @Nullable public Movie getMovie() {
    return movie;
  }

  @Nullable @Override public MovieViewModel getMovieViewModel() {
    if (movie != null) {
      return new MovieViewModel(movie);
    }

    return null;
  }

  public void setMovie(@NonNull Movie movie) {
    Preconditions.checkNotNull(movie);
    this.movie = movie;
  }

  @Nullable @Override public TvShow getTvShow() {
    return tvShow;
  }

  @Nullable @Override public TvSeason getTvSeason() {
    return tvSeason;
  }

  @Nullable @Override public TvEpisode getTvEpisode() {
    return tvEpisode;
  }

  @Override public void setTvDetails(@NonNull TvShow tvShow, @NonNull TvSeason tvSeason,
      @NonNull TvEpisode tvEpisode) {
    Preconditions.checkNotNull(tvShow);
    Preconditions.checkNotNull(tvSeason);
    Preconditions.checkNotNull(tvEpisode);

    this.tvShow = tvShow;
    this.tvSeason = tvSeason;
    this.tvEpisode = tvEpisode;
  }

  @Nullable @Override public SearchResult getSearchResult() {
    return searchResult;
  }

  @Override public void setSearchResult(@NonNull SearchResult searchResult) {
    Preconditions.checkNotNull(searchResult);
    this.searchResult = searchResult;
  }

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    DownloadView view = getView();

    if (view == null) {
      return;
    }

    if (movie != null) {
      MovieViewModel viewModel = new MovieViewModel(movie);
      view.setBackgroundUri(viewModel.backdropUri());
    } else if (tvShow != null) {
      TvShowViewModel viewModel = new TvShowViewModel(tvShow);
      view.setBackgroundUri(viewModel.backdropUri());
    }

    if (searchResult != null) {
      downloadClient = downloadManager.create(searchResult.uri());
      downloadClient.addListener(this);
      downloadClient.start();
    }
  }

  @Override public void onStop() {
    super.onStop();

    if (downloadClient != null) {
      downloadClient.removeListener(this);

      if (!downloadReady) {
        downloadClient.stop();
      }

      downloadClient = null;
    }
  }

  @Override public void onPrepared(DownloadClient client) {

  }

  @Override public void onStarted(DownloadClient client) {

  }

  @Override public void onError(DownloadClient client) {

  }

  @Override public void onReady(DownloadClient client) {
    downloadReady = true;

    if (movie != null) {
      eventBus.post(new PlayMovieSource(client.getId(), movie));
    } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
      eventBus.post(new PlayTvShowSource(client.getId(), tvShow, tvSeason, tvEpisode));
    }
  }

  @Override public void onProgress(DownloadClient client) {
    DownloadView view = getView();

    if (view != null) {
      view.setProgress(client.getBufferProgress(), client.getDownloadSpeed());
    }
  }

  @Override public void onStopped(DownloadClient client) {

  }
}
