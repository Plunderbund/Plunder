package com.plunder.plunder.ui.sources;

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

public class SourcesActivity extends BaseActivity {
  private final static String EXTRA_MOVIE = "movie";
  private final static String EXTRA_TV_SHOW = "tvShow";
  private final static String EXTRA_TV_SEASON = "tvSeason";
  private final static String EXTRA_TV_EPISODE = "tvEpisode";

  public static Intent createIntent(Context context, Movie movie) {
    Intent intent = new Intent(context, SourcesActivity.class);
    intent.putExtra(EXTRA_MOVIE, movie);

    return intent;
  }

  public static Intent createIntent(Context context, TvShow tvShow, TvSeason tvSeason,
      TvEpisode tvEpisode) {
    Intent intent = new Intent(context, SourcesActivity.class);
    intent.putExtra(EXTRA_TV_SHOW, tvShow);
    intent.putExtra(EXTRA_TV_SEASON, tvSeason);
    intent.putExtra(EXTRA_TV_EPISODE, tvEpisode);

    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sources);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
      TvShow tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW);
      TvSeason tvSeason = intent.getParcelableExtra(EXTRA_TV_SEASON);
      TvEpisode tvEpisode = intent.getParcelableExtra(EXTRA_TV_EPISODE);

      Fragment fragment = null;

      if (movie != null) {
        fragment = SourcesFragment.newInstance(movie);
      } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
        fragment = SourcesFragment.newInstance(tvShow, tvSeason, tvEpisode);
      }

      if (fragment == null) {
        finish();
        return;
      }

      getFragmentManager().beginTransaction().replace(R.id.sources_frame, fragment).commit();
    }
  }
}
