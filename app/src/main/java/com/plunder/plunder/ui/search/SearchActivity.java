package com.plunder.plunder.ui.search;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.plunder.plunder.R;
import com.plunder.plunder.ui.common.BaseActivity;

public class SearchActivity extends BaseActivity {
  private final static int PERMISSIONS_RECORD_AUDIO = 0;

  public static Intent createIntent(Context context) {
    return new Intent(context, SearchActivity.class);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
          PERMISSIONS_RECORD_AUDIO);
    } else {
      setContentView(R.layout.activity_search);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == PERMISSIONS_RECORD_AUDIO) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        setContentView(R.layout.activity_search);
      } else {
        finish();
      }

      return;
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
