package com.plunder.plunder.ui.playback;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
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
import com.plunder.plunder.ui.common.BasePlaybackOverlayFragment;
import com.plunder.plunder.ui.viewmodels.MediaViewModel;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;
import com.squareup.leakcanary.RefWatcher;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;
import javax.inject.Inject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import timber.log.Timber;

public class PlaybackFragment extends BasePlaybackOverlayFragment
    implements PlaybackView, IVLCVout.Callback {
  private final static String ARGS_DOWNLOAD_ID = "downloadId";
  private final static String ARGS_MOVIE = "movie";
  private final static String ARGS_TV_SHOW = "tvShow";
  private final static String ARGS_TV_SEASON = "tvSeason";
  private final static String ARGS_TV_EPISODE = "tvEpisode";

  private final static String STATE_DOWNLOAD_ID = "downloadId";
  private final static String STATE_MOVIE = "movie";
  private final static String STATE_TV_SHOW = "tvShow";
  private final static String STATE_TV_SEASON = "tvSeason";
  private final static String STATE_TV_EPISODE = "tvEpisode";

  @Inject @Nullable RefWatcher refWatcher;
  @Inject PlaybackPresenter presenter;
  @Inject LibVLC libVLC;

  private SurfaceView surfaceView;
  private SurfaceHolder surfaceHolder;

  private MediaSession mediaSession;

  private ArrayObjectAdapter rowsAdapter;
  private MediaController.Callback mediaControllerCallback;
  private MediaController mediaController;
  private PlaybackControlHelper glue;
  private MediaPlayer mediaPlayer;
  private boolean isMetadataSet = false;

  private MediaPlayer.EventListener playerListener = new PlayerListener();

  private int videoWidth;
  private int videoHeight;

  private long bufferedPosition;

  public static PlaybackFragment newInstance(UUID downloadId, Movie movie) {
    PlaybackFragment fragment = new PlaybackFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_DOWNLOAD_ID, new ParcelUuid(downloadId));
    args.putParcelable(ARGS_MOVIE, movie);
    fragment.setArguments(args);

    return fragment;
  }

  public static PlaybackFragment newInstance(UUID downloadId, TvShow tvShow, TvSeason tvSeason,
      TvEpisode tvEpisode) {
    PlaybackFragment fragment = new PlaybackFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARGS_DOWNLOAD_ID, new ParcelUuid(downloadId));
    args.putParcelable(ARGS_TV_SHOW, tvShow);
    args.putParcelable(ARGS_TV_SEASON, tvSeason);
    args.putParcelable(ARGS_TV_EPISODE, tvEpisode);
    fragment.setArguments(args);

    return fragment;
  }

  @Override protected void onAttach() {
    AppComponent appComponent = App.getAppComponent(getActivity());

    if (appComponent != null) {
      DaggerPlaybackComponent.builder()
          .appComponent(appComponent)
          .playbackModule(new PlaybackModule(this))
          .build()
          .inject(this);
    }

    createMediaSession();

    mediaControllerCallback = new MediaControllerCallback();
    mediaController = getActivity().getMediaController();
    mediaController.registerCallback(mediaControllerCallback);

    presenter.initialize();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Activity activity = getActivity();

    if (activity == null) {
      return;
    }

    ParcelUuid downloadId;
    Movie movie;
    TvShow tvShow;
    TvSeason tvSeason;
    TvEpisode tvEpisode;

    if (savedInstanceState != null) {
      downloadId = savedInstanceState.getParcelable(STATE_DOWNLOAD_ID);
      movie = savedInstanceState.getParcelable(STATE_MOVIE);
      tvShow = savedInstanceState.getParcelable(STATE_TV_SHOW);
      tvSeason = savedInstanceState.getParcelable(STATE_TV_SEASON);
      tvEpisode = savedInstanceState.getParcelable(STATE_TV_SHOW);
    } else {
      downloadId = getArguments().getParcelable(ARGS_DOWNLOAD_ID);
      movie = getArguments().getParcelable(ARGS_MOVIE);
      tvShow = getArguments().getParcelable(ARGS_TV_SHOW);
      tvSeason = getArguments().getParcelable(ARGS_TV_SEASON);
      tvEpisode = getArguments().getParcelable(ARGS_TV_EPISODE);
    }

    if (downloadId == null) {
      activity.finish();
      return;
    }

    if (movie != null) {
      presenter.setMovie(movie);
    } else if (tvShow != null && tvSeason != null && tvEpisode != null) {
      presenter.setTvDetails(tvShow, tvSeason, tvEpisode);
    } else {
      activity.finish();
      return;
    }

    isMetadataSet = false;
    setBackgroundType(BG_NONE);

    surfaceView = (SurfaceView) getActivity().findViewById(R.id.surface_view);
    surfaceHolder = surfaceView.getHolder();

    presenter.setDownloadId(downloadId.getUuid());
    presenter.onCreated(activity);

    updateMetadata();
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onStop() {
    if (mediaSession != null) {
      mediaSession.release();
      mediaSession = null;
    }

    releasePlayer();

    presenter.onStop();

    super.onStop();
  }

  @Override public void onDestroy() {
    presenter.onDestroy();

    if (mediaSession != null) {
      mediaSession.release();
      mediaSession = null;
    }

    releasePlayer();

    if (refWatcher != null) {
      refWatcher.watch(this);
    }

    super.onDestroy();
  }

  @Override public void onDetach() {
    if (mediaController != null) {
      mediaController.unregisterCallback(mediaControllerCallback);
    }

    super.onDetach();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    setSize(videoWidth, videoHeight);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(STATE_DOWNLOAD_ID, new ParcelUuid(presenter.getDownloadId()));
    outState.putParcelable(STATE_MOVIE, presenter.getMovie());
    outState.putParcelable(STATE_TV_SHOW, presenter.getTvShow());
    outState.putParcelable(STATE_TV_SEASON, presenter.getTvSeason());
    outState.putParcelable(STATE_TV_EPISODE, presenter.getTvEpisode());
    super.onSaveInstanceState(outState);
  }

  private void releasePlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();

      IVLCVout vlcOut = mediaPlayer.getVLCVout();
      vlcOut.removeCallback(this);
      vlcOut.detachViews();
    }

    videoWidth = 0;
    videoHeight = 0;
  }

  private void setSize(int width, int height) {
    videoWidth = width;
    videoHeight = height;

    if (videoWidth * videoHeight <= 1 || surfaceHolder == null || surfaceView == null) {
      return;
    }

    View windowDecorView = getActivity().getWindow().getDecorView();
    int decorWidth = windowDecorView.getWidth();
    int decorHeight = windowDecorView.getHeight();

    boolean isPortrait =
        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

    if (decorWidth > decorHeight && isPortrait || decorWidth < decorHeight && !isPortrait) {
      int i = decorWidth;
      decorWidth = decorHeight;
      decorHeight = i;
    }

    float videoRatio = (float) videoWidth / (float) videoHeight;
    float screenRatio = (float) decorWidth / (float) decorHeight;

    if (screenRatio < videoRatio) {
      decorHeight = (int) (decorWidth / videoRatio);
    } else {
      decorWidth = (int) (decorHeight * videoRatio);
    }

    surfaceHolder.setFixedSize(videoWidth, videoHeight);

    ViewGroup.LayoutParams surfaceLayout = surfaceView.getLayoutParams();
    surfaceLayout.width = decorWidth;
    surfaceLayout.height = decorHeight;
    surfaceView.setLayoutParams(surfaceLayout);
    surfaceView.invalidate();
  }

  private void createPlayer(String url) {
    releasePlayer();

    if (surfaceHolder == null) {
      return;
    }

    surfaceHolder.setKeepScreenOn(true);

    try {
      mediaPlayer = new MediaPlayer(libVLC);
      mediaPlayer.setEventListener(playerListener);

      final IVLCVout vlcOut = mediaPlayer.getVLCVout();
      vlcOut.setVideoView(surfaceView);
      vlcOut.addCallback(this);
      vlcOut.attachViews();

      Media media = new Media(libVLC, Uri.parse(url));
      mediaPlayer.setMedia(media);
      mediaPlayer.play();
    } catch (Exception e) {
      Timber.e(e, "Failed to create VLC");
    }
  }

  @Override public void setAddress(String address) {
    createPlayer(address);
    setPlaybackState(PlaybackState.STATE_PAUSED);
    playPause(true);
  }

  private void createMediaSession() {
    if (mediaSession == null) {
      mediaSession = new MediaSession(getActivity(), "Shun");
      mediaSession.setCallback(new MediaSessionCallback());
      mediaSession.setFlags(
          MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
      mediaSession.setActive(true);

      getActivity().setMediaController(
          new MediaController(getActivity(), mediaSession.getSessionToken()));
      setPlaybackState(PlaybackState.STATE_NONE);
    }
  }

  @Override
  public void onNewLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight,
      int sarNum, int sarDen) {
    if (width * height == 0) return;

    videoWidth = width;
    videoHeight = height;
    setSize(videoWidth, videoHeight);
  }

  @Override public void onSurfacesCreated(IVLCVout vout) {
  }

  @Override public void onSurfacesDestroyed(IVLCVout vout) {
  }

  private void setPosition(long position) {
    if (mediaPlayer == null) {
      return;
    }

    if (position > mediaPlayer.getLength()) {
      position = mediaPlayer.getLength();
    } else if (position < 0) {
      position = 0;
    }

    mediaPlayer.setTime(position);
  }

  private void updatePlaybackRow() {
    rowsAdapter.notifyArrayItemRangeChanged(0, 1);
  }

  public long getBufferedPosition() {
    return bufferedPosition;
  }

  public long getCurrentPosition() {
    if (mediaPlayer != null) {
      return mediaPlayer.getTime();
    }

    return 0L;
  }

  public long getDuration() {
    if (mediaPlayer != null) {
      return mediaPlayer.getLength();
    }

    return 0L;
  }

  private long getAvailableActions(int nextState) {
    long actions = PlaybackState.ACTION_PLAY |
        PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
        PlaybackState.ACTION_PLAY_FROM_SEARCH |
        PlaybackState.ACTION_PAUSE;

    if (nextState == PlaybackState.STATE_PLAYING) {
      actions |= PlaybackState.ACTION_PAUSE;
    }

    return actions;
  }

  private int getPlaybackState() {
    Activity activity = getActivity();

    if (activity != null) {
      PlaybackState state = activity.getMediaController().getPlaybackState();

      if (state != null) {
        return state.getState();
      }
    }

    return PlaybackState.STATE_NONE;
  }

  private void setPlaybackState(int state) {
    long position = getCurrentPosition();

    PlaybackState.Builder stateBuilder =
        new PlaybackState.Builder().setActions(getAvailableActions(state));
    stateBuilder.setState(state, position, 1.0f);

    if (mediaSession != null) {
      mediaSession.setPlaybackState(stateBuilder.build());
    }
  }

  private void playPause(boolean doPlay) {
    if (mediaPlayer == null) {
      setPlaybackState(PlaybackState.STATE_NONE);
      return;
    }

    if (doPlay && getPlaybackState() != PlaybackState.STATE_PLAYING) {
      mediaPlayer.play();
      setPlaybackState(PlaybackState.STATE_PLAYING);
    } else {
      mediaPlayer.pause();
      setPlaybackState(PlaybackState.STATE_PAUSED);
    }
  }

  private void updateMetadata() {
    MovieViewModel movie = presenter.getMovieViewModel();

    if (movie == null) {
      return;
    }

    final MediaMetadata.Builder metadataBuilder =
        new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_MEDIA_ID,
            movie.movie().id() + "")
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, movie.name())
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, movie.tagline())
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, movie.overview())
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                movie.posterUri() != null ? movie.posterUri().toString() : null)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, getDuration())
            .putString(MediaMetadata.METADATA_KEY_TITLE, movie.name())
            .putString(MediaMetadata.METADATA_KEY_ARTIST, movie.name());

    Resources res = getResources();
    int cardWidth = res.getDimensionPixelSize(R.dimen.media_card_main_image_width);
    int cardHeight = res.getDimensionPixelSize(R.dimen.media_card_main_image_height);

    Glide.with(this)
        .load(movie.posterUri())
        .asBitmap()
        .centerCrop()
        .into(new SimpleTarget<Bitmap>(cardWidth, cardHeight) {
          @Override public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
            metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);

            if (mediaSession != null) {
              mediaSession.setMetadata(metadataBuilder.build());
              updatePlaybackRow();
            }
          }
        });
  }

  @Override public void setMediaDetails(@NonNull MediaViewModel viewModel) {
    glue = new PlaybackControlHelper(getActivity(), this, viewModel);
    PlaybackControlsRowPresenter controlsRowPresenter = glue.createControlsRowAndPresenter();
    PlaybackControlsRow controlsRow = glue.getControlsRow();

    ClassPresenterSelector presenterSelector = new ClassPresenterSelector();
    presenterSelector.addClassPresenter(PlaybackControlsRow.class, controlsRowPresenter);
    presenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

    rowsAdapter = new ArrayObjectAdapter(presenterSelector);
    rowsAdapter.add(controlsRow);
    updatePlaybackRow();
    setAdapter(rowsAdapter);
  }

  private class MediaSessionCallback extends MediaSession.Callback {
    @Override public void onPlay() {
      playPause(true);
    }

    @Override public void onPlayFromMediaId(String mediaId, Bundle extras) {
    }

    @Override public void onPause() {
      playPause(false);
    }

    @Override public void onSkipToNext() {
      getActivity().onBackPressed();
    }

    @Override public void onSkipToPrevious() {
      getActivity().onBackPressed();
    }

    @Override public void onFastForward() {
      if (mediaPlayer == null) {
        return;
      }

      if (mediaPlayer.getTime() != -1) {
        int prevState = getPlaybackState();
        setPlaybackState(PlaybackState.STATE_FAST_FORWARDING);
        setPosition(mediaPlayer.getTime() + (30 * 1000));
        setPlaybackState(prevState);
      }
    }

    @Override public void onRewind() {
      if (mediaPlayer == null) {
        return;
      }

      int prevState = getPlaybackState();
      setPlaybackState(PlaybackState.STATE_REWINDING);

      setPosition(mediaPlayer.getTime() - (30 * 1000));
      setPlaybackState(prevState);
    }

    @Override public void onSeekTo(long position) {
      setPosition(position);
    }
  }

  private class MediaControllerCallback extends MediaController.Callback {
    @Override public void onPlaybackStateChanged(@NonNull PlaybackState state) {
      int nextState = state.getState();

      if (nextState != PlaybackState.STATE_NONE) {
        glue.updateProgress();
      }
    }

    @Override public void onMetadataChanged(MediaMetadata metadata) {
      glue.onMetadataChanged();
      updatePlaybackRow();
    }
  }

  private class PlayerListener implements MediaPlayer.EventListener {
    @Override public void onEvent(MediaPlayer.Event event) {
      switch (event.type) {
        case MediaPlayer.Event.Opening:
          isMetadataSet = false;
          break;
        case MediaPlayer.Event.EndReached:
          releasePlayer();

          if (mediaController != null) {
            mediaController.getTransportControls().skipToNext();
          }
          break;
        case MediaPlayer.Event.Playing:
          if (!isMetadataSet) {
            updateMetadata();
            isMetadataSet = true;
          }

          setPlaybackState(PlaybackState.STATE_PLAYING);
          break;
        case MediaPlayer.Event.Paused:
          setPlaybackState(PlaybackState.STATE_PAUSED);
          break;
        case MediaPlayer.Event.Stopped:
          setPlaybackState(PlaybackState.STATE_STOPPED);
          break;
        case MediaPlayer.Event.Buffering:
          bufferedPosition = (long)(event.getBuffering() * mediaPlayer.getLength());
          break;
        default:
          break;
      }
    }
  }
}
