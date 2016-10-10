package com.plunder.plunder.ui.presenters;

import android.content.res.Resources;
import android.net.Uri;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.res.ResourcesCompat;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.plunder.plunder.R;
import com.plunder.plunder.ui.viewmodels.TvShowViewModel;

public class TvShowCardPresenter extends Presenter {
  @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
    ImageCardView cardView = new ImageCardView(parent.getContext());
    cardView.setFocusable(true);
    cardView.setFocusableInTouchMode(true);

    Resources res = cardView.getResources();
    int width = res.getDimensionPixelSize(R.dimen.media_card_main_image_width);
    int height = res.getDimensionPixelSize(R.dimen.media_card_main_image_height);
    cardView.setMainImageDimensions(width, height);

    return new ViewHolder(cardView);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, Object item) {
    TvShowViewModel viewModel = (TvShowViewModel) item;

    ImageCardView cardView = (ImageCardView) viewHolder.view;
    cardView.setTitleText(viewModel.name());

    int backgroundColor =
        ResourcesCompat.getColor(cardView.getResources(), R.color.card_movie_background, null);
    cardView.setBackgroundColor(backgroundColor);
    cardView.setInfoAreaBackgroundColor(backgroundColor);

    Uri posterUrl = viewModel.posterUri();

    if (posterUrl != null) {
      Glide.with(cardView.getContext()).load(posterUrl).into(cardView.getMainImageView());
    }
  }

  @Override public void onUnbindViewHolder(ViewHolder viewHolder) {
    ImageCardView cardView = (ImageCardView) viewHolder.view;
    Glide.clear(cardView.getMainImageView());
    cardView.setBadgeImage(null);
    cardView.setMainImage(null);
  }
}
