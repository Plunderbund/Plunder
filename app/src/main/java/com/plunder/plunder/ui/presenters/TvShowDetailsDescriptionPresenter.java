package com.plunder.plunder.ui.presenters;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;

public class TvShowDetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
  @Override protected void onBindDescription(ViewHolder viewHolder, Object itemData) {
    if (!(itemData instanceof TvShowViewModel)) {
      return;
    }

    TvShowViewModel viewModel = (TvShowViewModel) itemData;
    viewHolder.getTitle().setText(viewModel.name());
  }
}
