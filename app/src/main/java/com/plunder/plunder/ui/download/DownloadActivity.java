package com.plunder.plunder.ui.download;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseActivity;
import com.plunder.provider.search.SearchResult;

public class DownloadActivity extends BaseActivity {
  private final static String EXTRA_MOVIE = "movie";
  private final static String EXTRA_TV_SHOW = "tvShow";
  private final static String EXTRA_TV_SEASON = "tvSeason";
  private final static String EXTRA_TV_EPISODE = "tvEpisode";
  private final static String EXTRA_SEARCH_RESULT = "searchResult";

  public static Intent createIntent(Context context, Movie movie, SearchResult searchResult) {
    Intent intent = new Intent(context, DownloadActivity.class);
    intent.putExtra(EXTRA_MOVIE, movie);
    intent.putExtra(EXTRA_SEARCH_RESULT, searchResult);

    return intent;
  }

  public static Intent createIntent(Context context, TvShow tvShow, TvSeason tvSeason,
      TvEpisode tvEpisode, SearchResult searchResult) {
    Intent intent = new Intent(context, DownloadActivity.class);
    intent.putExtra(EXTRA_TV_SHOW, tvShow);
    intent.putExtra(EXTRA_TV_SEASON, tvSeason);
    intent.putExtra(EXTRA_TV_EPISODE, tvEpisode);
    intent.putExtra(EXTRA_SEARCH_RESULT, searchResult);

    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_download);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
      TvShow tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW);
      TvSeason tvSeason = intent.getParcelableExtra(EXTRA_TV_SEASON);
      TvEpisode tvEpisode = intent.getParcelableExtra(EXTRA_TV_EPISODE);
      SearchResult searchResult = intent.getParcelableExtra(EXTRA_SEARCH_RESULT);

      Fragment fragment = null;

      if (searchResult != null) {
        if (movie != null) {
          fragment = DownloadFragment.newInstance(movie, searchResult);
        } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
          fragment = DownloadFragment.newInstance(tvShow, tvSeason, tvEpisode, searchResult);
        }
      }

      if (fragment == null) {
        finish();
        return;
      }

      getFragmentManager().beginTransaction().replace(R.id.download_frame, fragment).commit();
    }
  }
}
