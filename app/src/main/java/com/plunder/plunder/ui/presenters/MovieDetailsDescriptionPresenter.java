package com.plunder.plunder.ui.presenters;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import com.plunder.plunder.ui.viewmodels.MovieViewModel;

public class MovieDetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {
  @Override protected void onBindDescription(ViewHolder viewHolder, Object itemData) {
    if (!(itemData instanceof MovieViewModel)) {
      return;
    }

    MovieViewModel viewModel = (MovieViewModel) itemData;
    viewHolder.getTitle().setText(viewModel.name());
    viewHolder.getSubtitle().setText(String.valueOf(viewModel.releaseYear()));
    viewHolder.getBody().setText(viewModel.overview());
  }
}
