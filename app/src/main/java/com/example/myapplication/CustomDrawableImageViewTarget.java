package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;


public class CustomDrawableImageViewTarget extends DrawableImageViewTarget {
    private final int loopCount;

    public CustomDrawableImageViewTarget(ImageView view, int loopCount) {
        super(view);
        this.loopCount = loopCount;
    }

    @Override
    protected void setResource(@Nullable Drawable resource) {
        if (resource instanceof GifDrawable) {
            ((GifDrawable) resource).setLoopCount(loopCount);
        }
        super.setResource(resource);
    }
}
