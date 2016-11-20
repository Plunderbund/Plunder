package com.plunder.plunder.ui.playback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.torrents.TorrentClient;
import com.plunder.plunder.torrents.TorrentManager;
import com.plunder.plunder.torrents.TorrentWebServer;
import com.plunder.plunder.ui.common.BaseFragmentPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.plunder.plunder.ui.viewmodels.TvEpisodeViewModel;
import java.io.IOException;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class PlaybackPresenterImpl extends BaseFragmentPresenter<PlaybackView>
    implements PlaybackPresenter {
  private final TorrentManager torrentManager;
  private TorrentClient torrentClient;
  private TorrentWebServer webServer;

  private UUID downloadId;
  private Movie movie;
  private TvShow tvShow;
  private TvSeason tvSeason;
  private TvEpisode tvEpisode;

  public PlaybackPresenterImpl(@NonNull PlaybackView view, EventBus eventBus,
      TorrentManager torrentManager) {
    super(view, eventBus);
    this.torrentManager = torrentManager;
  }

  @Nullable @Override public UUID getDownloadId() {
    return downloadId;
  }

  @Override public void setDownloadId(@NonNull UUID id) {
    Preconditions.checkNotNull(id);
    downloadId = id;
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

  @Override public void onCreated(Context context) {
    super.onCreated(context);

    PlaybackView view = getView();

    if (view == null) {
      return;
    }

    if (movie != null) {
      MovieViewModel viewModel = new MovieViewModel(movie);
      view.setMediaDetails(viewModel);
    } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
      TvEpisodeViewModel tvEpisodeViewModel = new TvEpisodeViewModel(tvEpisode);
      view.setMediaDetails(tvEpisodeViewModel);
    }

    if (downloadId != null) {
      torrentClient = torrentManager.getClientById(downloadId);

      if (torrentClient != null) {
        if (webServer != null) {
          webServer.stop();
        }

        webServer = new TorrentWebServer(torrentClient);

        try {
          webServer.start();
        } catch (IOException e) {
          Timber.e(e, "Failed to start torrent web server");
        }

        view.setAddress(webServer.getAddress());
      }
    }
  }

  @Override public void onStop() {
    super.onStop();

    if (torrentClient != null) {
      torrentClient.stop();
      torrentClient = null;
    }

    if (webServer != null) {
      webServer.stop();
      webServer = null;
    }
  }
}
