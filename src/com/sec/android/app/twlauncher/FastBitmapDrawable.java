/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.sec.android.app.twlauncher;

import android.graphics.*;
import android.graphics.drawable.Drawable;

class FastBitmapDrawable extends Drawable
{

    FastBitmapDrawable(Bitmap bitmap)
    {
        mPaint = new Paint();
        mBitmap = bitmap;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
    }

    public int getIntrinsicHeight()
    {
        return mBitmap.getHeight();
    }

    public int getIntrinsicWidth()
    {
        return mBitmap.getWidth();
    }

    public int getMinimumHeight()
    {
        return mBitmap.getHeight();
    }

    public int getMinimumWidth()
    {
        return mBitmap.getWidth();
    }

    public int getOpacity()
    {
        return -3;
    }

    public void setAlpha(int i)
    {
    }

    public void setColorFilter(ColorFilter colorfilter)
    {
        mPaint.setColorFilter(colorfilter);
    }

    private Bitmap mBitmap;
    private Paint mPaint;
}
