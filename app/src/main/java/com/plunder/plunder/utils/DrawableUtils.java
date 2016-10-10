package com.plunder.plunder.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DrawableUtils {
  private static final Map<Integer, WeakReference<Drawable>> transparentCache;
  private static final Map<Integer, WeakReference<Bitmap>> transparentBitmapCache;

  static {
    transparentCache = new ConcurrentHashMap<>();
    transparentBitmapCache = new ConcurrentHashMap<>();
  }

  public static Drawable createTransparentDrawable(int width, int height) {
    int hashCode = 1;
    hashCode = 31 * hashCode + width;
    hashCode = 31 * hashCode + height;

    if (transparentCache.containsKey(hashCode)) {
      WeakReference<Drawable> drawableRef = transparentCache.get(hashCode);
      Drawable drawable = drawableRef.get();

      if (drawable != null) {
        return drawable;
      }

      transparentCache.remove(hashCode);
    }

    Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
    drawable.setBounds(new Rect(0, 0, width, height));
    transparentCache.put(hashCode, new WeakReference<>(drawable));

    return drawable;
  }

  public static Bitmap createTransparentBitmap(int width, int height) {
    int hashCode = 1;
    hashCode = 31 * hashCode + width;
    hashCode = 31 * hashCode + height;

    if (transparentBitmapCache.containsKey(hashCode)) {
      WeakReference<Bitmap> bitmapRef = transparentBitmapCache.get(hashCode);
      Bitmap bitmap = bitmapRef.get();

      if (bitmap != null) {
        return bitmap;
      }

      transparentBitmapCache.remove(hashCode);
    }

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
    transparentBitmapCache.put(hashCode, new WeakReference<>(bitmap));

    return bitmap;
  }
}
