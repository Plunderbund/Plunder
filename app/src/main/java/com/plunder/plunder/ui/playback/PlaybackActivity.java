/*
 * Copyright (c) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.plunder.plunder.ui.playback;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.KeyEvent;
import android.view.MotionEvent;
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

  private static final float GAMEPAD_TRIGGER_INTENSITY_ON = 0.5f;
  private static final float GAMEPAD_TRIGGER_INTENSITY_OFF = 0.45f;
  private boolean gamepadTriggerPressed = false;

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

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
      getMediaController().getTransportControls().rewind();
    } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
      getMediaController().getTransportControls().fastForward();
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onGenericMotionEvent(MotionEvent event) {
    if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON && !gamepadTriggerPressed) {
      getMediaController().getTransportControls().rewind();
      gamepadTriggerPressed = true;
    } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON && !gamepadTriggerPressed) {
      getMediaController().getTransportControls().fastForward();
      gamepadTriggerPressed = true;
    } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
        && event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF) {
      gamepadTriggerPressed = false;
    }
    return super.onGenericMotionEvent(event);
  }
}
