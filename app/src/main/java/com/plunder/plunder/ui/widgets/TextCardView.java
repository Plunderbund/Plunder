package com.plunder.plunder.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.plunder.plunder.R;

/**
 * Adapted from https://github.com/hitherejoe/LeanbackCards
 */
public class TextCardView extends BaseCardView {
  private LinearLayout mDetail;
  private TextView mTitle;
  private TextView mValue;

  public TextCardView(Context context) {
    this(context, null);
  }

  public TextCardView(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.imageCardViewStyle);
  }

  public TextCardView(Context context, int styleResId) {
    super(new ContextThemeWrapper(context, styleResId), null, 0);
    buildIconCardView(styleResId);
  }

  public TextCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(getStyledContext(context, attrs, defStyleAttr), attrs, defStyleAttr);
    buildIconCardView(getTextCardViewStyle(context, attrs, defStyleAttr));
  }

  @Override public boolean hasOverlappingRendering() {
    return false;
  }

  private void buildIconCardView(int styleResId) {
    setFocusable(true);
    setFocusableInTouchMode(true);
    setCardType(CARD_TYPE_MAIN_ONLY);

    Context context = getContext();

    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.view_text_card, this);
    TypedArray cardAttrs = context.obtainStyledAttributes(styleResId, R.styleable.TextCardView);

    int headerBackgroundColor =
        cardAttrs.getInt(R.styleable.TextCardView_text_title_background_color,
            ContextCompat.getColor(context, R.color.default_header));
    int detailBackgroundColor =
        cardAttrs.getInt(R.styleable.TextCardView_text_detail_background_color,
            ContextCompat.getColor(context, R.color.default_detail));
    int titleTextColor = cardAttrs.getInt(R.styleable.TextCardView_text_title_text_color,
        ContextCompat.getColor(context, R.color.white));
    int detailTextColor = cardAttrs.getInt(R.styleable.TextCardView_text_detail_text_color,
        ContextCompat.getColor(context, R.color.white));

    mDetail = (LinearLayout) findViewById(R.id.layout_detail);
    mTitle = (TextView) findViewById(R.id.text_option_title);
    mValue = (TextView) findViewById(R.id.text_option_value);

    setCardBackgroundColor(headerBackgroundColor);
    setDetailBackgroundColor(detailBackgroundColor);
    setTitleTextColor(titleTextColor);
    setDetailTextColor(detailTextColor);

    cardAttrs.recycle();
  }

  public void setTitleText(String titleText) {
    if (!TextUtils.isEmpty(titleText)) {
      mTitle.setText(titleText);
      mTitle.setVisibility(VISIBLE);
    } else {
      mTitle.setVisibility(GONE);
    }
  }

  public void setDetailText(String detailText) {
    if (!TextUtils.isEmpty(detailText)) {
      mValue.setText(detailText);
      mValue.setVisibility(VISIBLE);
    } else {
      mValue.setVisibility(GONE);
    }
  }

  public void setTitleTextColor(@ColorInt int color) {
    mTitle.setTextColor(color);
  }

  public void setDetailTextColor(@ColorInt int color) {
    mValue.setTextColor(color);
  }

  public void setCardBackgroundColor(@ColorInt int color) {
    setBackgroundColor(color);
  }

  public void setDetailBackgroundColor(@ColorInt int color) {
    mDetail.setBackgroundColor(color);
  }

  private static Context getStyledContext(Context context, AttributeSet attrs, int defStyleAttr) {
    int style = getTextCardViewStyle(context, attrs, defStyleAttr);
    return new ContextThemeWrapper(context, style);
  }

  private static int getTextCardViewStyle(Context context, AttributeSet attrs, int defStyleAttr) {
    int style = null == attrs ? 0 : attrs.getStyleAttribute();
    if (0 == style) {
      TypedArray styledAttrs = context.obtainStyledAttributes(R.styleable.TextCardView);
      style = styledAttrs.getResourceId(R.styleable.TextCardView_text_theme, 0);
      styledAttrs.recycle();
    }
    return style;
  }
}
