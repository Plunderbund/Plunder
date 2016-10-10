package com.plunder.plunder.ui.playback;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Movie;
import com.plunder.plunder.domain.models.TvEpisode;
import com.plunder.plunder.domain.models.TvSeason;
import com.plunder.plunder.domain.models.TvShow;
import com.plunder.plunder.ui.common.BaseActivity;
import java.util.UUID;

public class PlaybackActivity extends BaseActivity {
  private final static String EXTRA_DOWNLOAD_ID = "downloadId";
  private final static String EXTRA_MOVIE = "movie";
  private final static String EXTRA_TV_SHOW = "tvShow";
  private final static String EXTRA_TV_SEASON = "tvSeason";
  private final static String EXTRA_TV_EPISODE = "tvEpisode";

  public static Intent createIntent(Context context, UUID downloadId, Movie movie) {
    Intent intent = new Intent(context, PlaybackActivity.class);
    intent.putExtra(EXTRA_DOWNLOAD_ID, new ParcelUuid(downloadId));
    intent.putExtra(EXTRA_MOVIE, movie);

    return intent;
  }

  public static Intent createIntent(Context context, UUID downloadId, TvShow tvShow,
      TvSeason tvSeason, TvEpisode tvEpisode) {
    Intent intent = new Intent(context, PlaybackActivity.class);
    intent.putExtra(EXTRA_DOWNLOAD_ID, new ParcelUuid(downloadId));
    intent.putExtra(EXTRA_TV_SHOW, tvShow);
    intent.putExtra(EXTRA_TV_SEASON, tvSeason);
    intent.putExtra(EXTRA_TV_EPISODE, tvEpisode);

    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playback);

    getWindow().getDecorView().setBackgroundColor(Color.BLACK);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      ParcelUuid downloadId = intent.getParcelableExtra(EXTRA_DOWNLOAD_ID);
      Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
      TvShow tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW);
      TvSeason tvSeason = intent.getParcelableExtra(EXTRA_TV_SEASON);
      TvEpisode tvEpisode = intent.getParcelableExtra(EXTRA_TV_EPISODE);

      Fragment fragment = null;

      if (downloadId != null) {
        if (movie != null) {
          fragment = PlaybackFragment.newInstance(downloadId.getUuid(), movie);
        } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
          fragment =
              PlaybackFragment.newInstance(downloadId.getUuid(), tvShow, tvSeason, tvEpisode);
        }
      }

      if (fragment == null) {
        finish();
        return;
      }

      getFragmentManager().beginTransaction().replace(R.id.playback_frame, fragment).commit();
    }
  }

  @Override public void onVisibleBehindCanceled() {
    getMediaController().getTransportControls().pause();
    super.onVisibleBehindCanceled();
  }
}
