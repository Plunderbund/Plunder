package com.plunder.plunder.ui.genre;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.plunder.plunder.R;
import com.plunder.plunder.domain.models.Genre;
import com.plunder.plunder.ui.common.BaseActivity;

public class GenreActivity extends BaseActivity {
  private final static String EXTRA_GENRE = "genre";

  public static Intent createIntent(Context context, Genre genre) {
    Intent intent = new Intent(context, GenreActivity.class);
    intent.putExtra(EXTRA_GENRE, genre);

    return intent;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_genre);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      Genre genre = intent.getParcelableExtra(EXTRA_GENRE);

      Fragment fragment = null;

      if (genre != null) {
        fragment = GenreFragment.newInstance(genre);
      }

      if (fragment == null) {
        finish();
        return;
      }

      getFragmentManager().beginTransaction().replace(R.id.genre_frame, fragment).commit();
    }
  }
}
