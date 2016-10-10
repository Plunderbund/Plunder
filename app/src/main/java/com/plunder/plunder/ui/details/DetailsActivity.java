package com.plunder.plunder.ui.details;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseActivity;
import com.plunder.plunder.ui.details.movies.MovieDetailsFragment;
import com.plunder.plunder.ui.details.tv.TvShowDetailsFragment;
import com.plunder.plunder.ui.details.tv.season.TvSeasonDetailsFragment;

public class DetailsActivity extends BaseActivity {
  private final static String EXTRA_MOVIE = "movie";
  private final static String EXTRA_TV_SHOW = "tvShow";
  private final static String EXTRA_TV_SEASON = "tvSeason";

  public static Intent createIntent(Context context, Movie movie) {
    Intent intent = new Intent(context, DetailsActivity.class);
    intent.putExtra(EXTRA_MOVIE, movie);

    return intent;
  }

  public static Intent createIntent(Context context, TvShow tvShow) {
    Intent intent = new Intent(context, DetailsActivity.class);
    intent.putExtra(EXTRA_TV_SHOW, tvShow);

    return intent;
  }

  public static Intent createIntent(Context context, TvShow tvShow, TvSeason season) {
    Intent intent = new Intent(context, DetailsActivity.class);
    intent.putExtra(EXTRA_TV_SHOW, tvShow);
    intent.putExtra(EXTRA_TV_SEASON, season);

    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
      TvShow tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW);
      TvSeason season = intent.getParcelableExtra(EXTRA_TV_SEASON);

      Fragment fragment = null;

      if (movie != null) {
        fragment = MovieDetailsFragment.newInstance(movie);
      } else if (tvShow != null) {
        if (season != null) {
          fragment = TvSeasonDetailsFragment.newInstance(tvShow, season);
        } else {
          fragment = TvShowDetailsFragment.newInstance(tvShow);
        }
      }

      if (fragment == null) {
        finish();
        return;
      }

      getFragmentManager().beginTransaction().replace(R.id.details_frame, fragment).commit();
    }
  }
}
