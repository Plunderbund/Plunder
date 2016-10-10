package com.plunder.plunder.ui.presenters;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import com.plunder.plunder.ui.viewmodels.GenreViewModel;
import com.plunder.plunder.ui.widgets.TextCardView;

public class GenreCardPresenter extends Presenter {
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
    TextCardView cardView = new TextCardView(parent.getContext());
    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);
    return new ViewHolder(cardView);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, Object item) {
    GenreViewModel viewModel = (GenreViewModel) item;

    TextCardView cardView = (TextCardView) viewHolder.view;
    cardView.setTitleText(viewModel.name());
    cardView.setDetailText(null);
  }

  @Override public void onUnbindViewHolder(ViewHolder viewHolder) {
  }
}
