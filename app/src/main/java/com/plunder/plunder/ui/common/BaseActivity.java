package com.plunder.plunder.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.plunder.plunder.App;
import com.plunder.plunder.AppComponent;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.details.DetailsActivity;
import com.plunder.plunder.ui.download.DownloadActivity;
import com.plunder.plunder.ui.events.DownloadMovieSource;
import com.plunder.plunder.ui.events.DownloadTvShowSource;
import com.plunder.plunder.ui.events.PlayMovieSource;
import com.plunder.plunder.ui.events.PlayTvShowSource;
import com.plunder.plunder.ui.events.ShowGenre;
import com.plunder.plunder.ui.events.ShowMovieDetails;
import com.plunder.plunder.ui.events.ShowMovieSources;
import com.plunder.plunder.ui.events.ShowSearch;
import com.plunder.plunder.ui.events.ShowTvSeasonDetails;
import com.plunder.plunder.ui.events.ShowTvShowDetails;
import com.plunder.plunder.ui.events.ShowTvShowSources;
import com.plunder.plunder.ui.genre.GenreActivity;
import com.plunder.plunder.ui.playback.PlaybackActivity;
import com.plunder.plunder.ui.search.SearchActivity;
import com.plunder.plunder.ui.sources.SourcesActivity;
import com.plunder.provider.search.SearchResult;
import java.util.UUID;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class BaseActivity extends Activity {
  @Inject protected EventBus eventBus;

  @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AppComponent appComponent = App.getAppComponent(this);

    if (appComponent != null) {
      setupComponent(appComponent);
    }
  }

  @Override protected void onStart() {
    super.onStart();

    if (eventBus != null) {
      eventBus.register(this);
    }
  }

  @Override protected void onStop() {
    if (eventBus != null) {
      eventBus.unregister(this);
    }

    super.onStop();
  }

  @Subscribe public void onShowSearch(ShowSearch event) {
    Intent intent = SearchActivity.createIntent(this);
    startActivity(intent);
  }

  @Subscribe public void onShowGenre(ShowGenre event) {
    Genre genre = event.getGenre();

    if (genre != null) {
      Intent intent = GenreActivity.createIntent(this, genre);
      startActivity(intent);
    }
  }

  @Subscribe public void onShowMovieDetails(ShowMovieDetails event) {
    Movie movie = event.getMovie();

    if (movie != null) {
      Intent intent = DetailsActivity.createIntent(this, movie);
      startActivity(intent);
    }
  }

  @Subscribe public void onShowMovieSources(ShowMovieSources event) {
    Movie movie = event.getMovie();

    if (movie != null) {
      Intent intent = SourcesActivity.createIntent(this, movie);
      startActivity(intent);
    }
  }

  @Subscribe public void onDownloadMovieSource(DownloadMovieSource event) {
    Movie movie = event.getMovie();
    SearchResult searchResult = event.getSearchResult();

    if (movie != null && searchResult != null) {
      Intent intent = DownloadActivity.createIntent(this, movie, searchResult);
      startActivity(intent);
    }
  }

  @Subscribe public void onPlayMovieSource(PlayMovieSource event) {
    UUID downloadId = event.getDownloadId();
    Movie movie = event.getMovie();

    if (downloadId != null && movie != null) {
      Intent intent = PlaybackActivity.createIntent(this, downloadId, movie);
      startActivity(intent);
      finish();
    }
  }

  @Subscribe public void onShowTvShowDetails(ShowTvShowDetails event) {
    TvShow tvShow = event.getTvShow();

    if (tvShow != null) {
      Intent intent = DetailsActivity.createIntent(this, tvShow);
      startActivity(intent);
    }
  }

  @Subscribe public void onShowTvSeasonDetails(ShowTvSeasonDetails event) {
    TvShow tvShow = event.getTvShow();
    TvSeason tvSeason = event.getTvSeason();

    if (tvShow != null && tvSeason != null) {
      Intent intent = DetailsActivity.createIntent(this, tvShow, tvSeason);
      startActivity(intent);
    }
  }

  @Subscribe public void ShowTvShowSources(ShowTvShowSources event) {
    TvShow tvShow = event.getTvShow();
    TvSeason tvSeason = event.getTvSeason();
    TvEpisode tvEpisode = event.getTvEpisode();

    if (tvShow != null && tvSeason != null && tvEpisode != null) {
      Intent intent = SourcesActivity.createIntent(this, tvShow, tvSeason, tvEpisode);
      startActivity(intent);
    }
  }

  @Subscribe public void DownloadTvShowSource(DownloadTvShowSource event) {
    SearchResult searchResult = event.getSearchResult();
    TvShow tvShow = event.getTvShow();
    TvSeason tvSeason = event.getTvSeason();
    TvEpisode tvEpisode = event.getTvEpisode();

    if (searchResult != null && tvShow != null && tvSeason != null && tvEpisode != null) {
      Intent intent =
          DownloadActivity.createIntent(this, tvShow, tvSeason, tvEpisode, searchResult);
      startActivity(intent);
      finish();
    }
  }

  @Subscribe public void PlayTvShowSource(PlayTvShowSource event) {
    UUID downloadId = event.getDownloadId();
    TvShow tvShow = event.getTvShow();
    TvSeason tvSeason = event.getTvSeason();
    TvEpisode tvEpisode = event.getTvEpisode();

    if (downloadId != null && tvShow != null && tvSeason != null && tvEpisode != null) {
      Intent intent = PlaybackActivity.createIntent(this, downloadId, tvShow, tvSeason, tvEpisode);
      startActivity(intent);
      finish();
    }
  }

  protected void setupComponent(@NonNull AppComponent appComponent) {
    appComponent.inject(this);
  }
}
