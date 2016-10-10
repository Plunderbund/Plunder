package com.plunder.plunder.ui.presenters;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import com.plunder.plunder.ui.viewmodels.SearchResultViewModel;
import com.plunder.plunder.ui.widgets.TextCardView;

public class SourceCardPresenter extends Presenter {
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
    TextCardView cardView = new TextCardView(parent.getContext());
    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);
    return new ViewHolder(cardView);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, Object item) {
    SearchResultViewModel viewModel = (SearchResultViewModel) item;

    TextCardView cardView = (TextCardView) viewHolder.view;
    cardView.setTitleText(viewModel.name());

    if (viewModel.seeds() != null && viewModel.peers() != null) {
      cardView.setDetailText(viewModel.seeds() + " seeds / " + viewModel.peers() + " peers");
    } else {
      cardView.setDetailText(viewModel.fileSize(cardView.getContext()));
    }
  }

  @Override public void onUnbindViewHolder(ViewHolder viewHolder) {
  }
}
