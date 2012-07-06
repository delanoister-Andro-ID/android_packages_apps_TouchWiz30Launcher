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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

// Referenced classes of package com.sec.android.app.twlauncher:
//            BgMapFactory, FastBitmapDrawable, Launcher, BgMap

final class Utilities
{
    static class BubbleText
    {

        Bitmap createTextBitmap(String s)
        {
            Bitmap bitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, android.graphics.Bitmap.Config.ALPHA_8);
            bitmap.setDensity(mDensity);
            Canvas canvas = new Canvas(bitmap);
            StaticLayout staticlayout = new StaticLayout(s, mTextPaint, (int)mTextWidth, android.text.Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
            int i = staticlayout.getLineCount();
            if(i > 2)
                i = 2;
            for(int j = 0; j < i; j++)
            {
                String s1 = s.substring(staticlayout.getLineStart(j), staticlayout.getLineEnd(j));
                int k = (int)(mBubbleRect.left + 0.5F * (mBubbleRect.width() - mTextPaint.measureText(s1)));
                int l = mFirstLineY + j * mLineHeight;
                canvas.drawText(s1, k, l, mTextPaint);
            }

            return bitmap;
        }

        private final int mBitmapHeight;
        private final int mBitmapWidth;
        private final RectF mBubbleRect = new RectF();
        private final int mDensity;
        private final int mFirstLineY;
        private final int mLeading = (int)(0.5F + 0.0F);
        private final int mLineHeight;
        private final TextPaint mTextPaint;
        private final float mTextWidth;

        BubbleText(Context context)
        {
            Resources resources = context.getResources();
            DisplayMetrics displaymetrics = resources.getDisplayMetrics();
            float f = displaymetrics.density;
            mDensity = displaymetrics.densityDpi;
            float f1 = 2.0F * f;
            float f2 = 2.0F * f;
            float f3 = resources.getDimension(0x7f090000);
            RectF rectf = mBubbleRect;
            rectf.left = 0.0F;
            rectf.top = 0.0F;
            rectf.right = (int)f3;
            mTextWidth = f3 - f1 - f2;
            TextPaint textpaint = new TextPaint();
            mTextPaint = textpaint;
            textpaint.setTextSize(13F * f);
            textpaint.setColor(-1);
            textpaint.setAntiAlias(true);
            float f4 = -textpaint.ascent();
            float f5 = textpaint.descent();
            mFirstLineY = (int)(0.5F + (0.0F + f4));
            mLineHeight = (int)(0.5F + (f5 + (0.0F + f4)));
            mBitmapWidth = (int)(0.5F + mBubbleRect.width());
            mBitmapHeight = Utilities.roundToPow2((int)(0.5F + (0.0F + (float)(2 * mLineHeight))));
            mBubbleRect.offsetTo(((float)mBitmapWidth - mBubbleRect.width()) / 2.0F, 0.0F);
        }
    }


    Utilities()
    {
    }

    static Bitmap createBitmapThumbnail(Bitmap bitmap, Context context)
    {
        Canvas canvas = sCanvas;
        canvas;
        JVM INSTR monitorenter ;
        int i;
        int j;
        int k;
        int l;
        if(sIconWidth == -1)
        {
            int i1 = (int)context.getResources().getDimension(0x1050000);
            sIconHeight = i1;
            sIconWidth = i1;
        }
        i = sIconWidth;
        j = sIconHeight;
        k = bitmap.getWidth();
        l = bitmap.getHeight();
        if(i <= 0 || j <= 0) goto _L2; else goto _L1
_L1:
        if(i >= k && j >= l) goto _L4; else goto _L3
_L3:
        float f = (float)k / (float)l;
        Bitmap bitmap1;
        android.graphics.Bitmap.Config config;
        Bitmap bitmap2;
        Canvas canvas1;
        Paint paint;
        android.graphics.Bitmap.Config config1;
        Bitmap bitmap3;
        Canvas canvas2;
        Paint paint1;
        if(k > l)
            j = (int)((float)i / f);
        else
        if(l > k)
            i = (int)(f * (float)j);
        if(i == sIconWidth && j == sIconHeight)
            config = bitmap.getConfig();
        else
            config = android.graphics.Bitmap.Config.ARGB_8888;
        bitmap2 = Bitmap.createBitmap(sIconWidth, sIconHeight, config);
        canvas1 = sCanvas;
        paint = sPaint;
        canvas1.setBitmap(bitmap2);
        paint.setDither(false);
        paint.setFilterBitmap(true);
        sBounds.set((sIconWidth - i) / 2, (sIconHeight - j) / 2, i, j);
        sOldBounds.set(0, 0, k, l);
        canvas1.drawBitmap(bitmap, sOldBounds, sBounds, paint);
        bitmap1 = bitmap2;
          goto _L5
_L6:
        config1 = android.graphics.Bitmap.Config.ARGB_8888;
        bitmap3 = Bitmap.createBitmap(sIconWidth, sIconHeight, config1);
        canvas2 = sCanvas;
        paint1 = sPaint;
        canvas2.setBitmap(bitmap3);
        paint1.setDither(false);
        paint1.setFilterBitmap(true);
        canvas2.drawBitmap(bitmap, (sIconWidth - k) / 2, (sIconHeight - l) / 2, paint1);
        bitmap1 = bitmap3;
          goto _L5
_L2:
        canvas;
        JVM INSTR monitorexit ;
        bitmap1 = bitmap;
_L5:
        return bitmap1;
_L4:
        if(k >= i && l >= j) goto _L2; else goto _L6
    }

    static Bitmap createIconBitmap(Drawable drawable, Context context)
    {
        Canvas canvas = sCanvas;
        canvas;
        JVM INSTR monitorenter ;
        int i;
        int j;
        int k;
        int l;
        if(sIconWidth == -1)
            initStatics(context);
        i = sIconWidth;
        j = sIconHeight;
        int k1;
        int l1;
        Bitmap bitmap;
        Canvas canvas1;
        int i2;
        int j2;
        if(drawable instanceof PaintDrawable)
        {
            PaintDrawable paintdrawable = (PaintDrawable)drawable;
            paintdrawable.setIntrinsicWidth(i);
            paintdrawable.setIntrinsicHeight(j);
        } else
        if(drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapdrawable = (BitmapDrawable)drawable;
            if(bitmapdrawable.getBitmap().getDensity() == 0)
                bitmapdrawable.setTargetDensity(context.getResources().getDisplayMetrics());
        }
        k = drawable.getIntrinsicWidth();
        l = drawable.getIntrinsicHeight();
        if(k <= 0 || k <= 0) goto _L2; else goto _L1
_L1:
        if(i >= k && j >= l) goto _L4; else goto _L3
_L6:
        k1 = sIconTextureWidth;
        l1 = sIconTextureHeight;
        bitmap = Bitmap.createBitmap(k1, l1, android.graphics.Bitmap.Config.ARGB_8888);
        canvas1 = sCanvas;
        canvas1.setBitmap(bitmap);
        i2 = (k1 - j1) / 2;
        j2 = (l1 - i1) / 2;
        sOldBounds.set(drawable.getBounds());
        drawable.setBounds(i2, j2, j1 + i2, i1 + j2);
        drawable.draw(canvas1);
        drawable.setBounds(sOldBounds);
        return bitmap;
_L4:
        if(k < i && l < j)
        {
            j1 = k;
            i1 = l;
            continue; /* Loop/switch isn't completed */
        }
_L2:
        i1 = j;
        j1 = i;
        continue; /* Loop/switch isn't completed */
_L3:
        float f = (float)k / (float)l;
        int i1;
        int j1;
        int k2;
        int l2;
        if(k > l)
        {
            k2 = (int)((float)i / f);
            l2 = i;
        } else
        if(l > k)
        {
            l2 = (int)(f * (float)j);
            k2 = j;
        } else
        {
            k2 = j;
            l2 = i;
        }
        i1 = k2;
        j1 = l2;
        if(true) goto _L6; else goto _L5
_L5:
    }

    static Drawable createIconThumbnail(Drawable drawable, Context context)
    {
        Canvas canvas = sCanvas;
        canvas;
        JVM INSTR monitorenter ;
        int i;
        int j;
        int k;
        int l;
        if(sIconWidth == -1)
        {
            int l2 = (int)context.getResources().getDimension(0x1050000);
            sIconHeight = l2;
            sIconWidth = l2;
        }
        i = sIconWidth;
        j = sIconHeight;
        Bitmap bitmap;
        Canvas canvas1;
        int k1;
        int l1;
        if(drawable instanceof PaintDrawable)
        {
            PaintDrawable paintdrawable = (PaintDrawable)drawable;
            paintdrawable.setIntrinsicWidth(i);
            paintdrawable.setIntrinsicHeight(j);
        } else
        if(drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapdrawable = (BitmapDrawable)drawable;
            if(bitmapdrawable.getBitmap().getDensity() == 0)
                bitmapdrawable.setTargetDensity(context.getResources().getDisplayMetrics());
        }
        k = drawable.getIntrinsicWidth();
        l = drawable.getIntrinsicHeight();
        if(i <= 0 || j <= 0) goto _L2; else goto _L1
_L1:
        if(i >= k && j >= l && 1.0F == 1.0F) goto _L4; else goto _L3
_L3:
        float f = (float)k / (float)l;
        Exception exception;
        int i1;
        int j1;
        android.graphics.Bitmap.Config config;
        android.graphics.Bitmap.Config config1;
        Bitmap bitmap1;
        Canvas canvas2;
        int j2;
        int k2;
        FastBitmapDrawable fastbitmapdrawable;
        if(k > l)
        {
            i1 = (int)((float)i / f);
            j1 = i;
        } else
        if(l > k)
        {
            int i2 = (int)(f * (float)j);
            i1 = j;
            j1 = i2;
        } else
        {
            i1 = j;
            j1 = i;
        }
        if(drawable.getOpacity() == -1) goto _L6; else goto _L5
_L5:
        config = android.graphics.Bitmap.Config.ARGB_8888;
_L7:
        bitmap = Bitmap.createBitmap(sIconWidth, sIconHeight, config);
        canvas1 = sCanvas;
        canvas1.setBitmap(bitmap);
        sOldBounds.set(drawable.getBounds());
        k1 = (sIconWidth - j1) / 2;
        l1 = (sIconHeight - i1) / 2;
        drawable.setBounds(k1, l1, k1 + j1, l1 + i1);
        drawable.draw(canvas1);
        drawable.setBounds(sOldBounds);
        drawable = new FastBitmapDrawable(bitmap);
        i1;
        j1;
_L2:
        canvas;
        JVM INSTR monitorexit ;
        return drawable;
        exception;
        throw exception;
_L6:
        config = android.graphics.Bitmap.Config.RGB_565;
          goto _L7
_L4:
        if(k >= i || l >= j) goto _L2; else goto _L8
_L8:
        config1 = android.graphics.Bitmap.Config.ARGB_8888;
        bitmap1 = Bitmap.createBitmap(sIconWidth, sIconHeight, config1);
        canvas2 = sCanvas;
        canvas2.setBitmap(bitmap1);
        sOldBounds.set(drawable.getBounds());
        j2 = (i - k) / 2;
        k2 = (j - l) / 2;
        drawable.setBounds(j2, k2, k + j2, l + k2);
        drawable.draw(canvas2);
        drawable.setBounds(sOldBounds);
        fastbitmapdrawable = new FastBitmapDrawable(bitmap1);
        drawable = fastbitmapdrawable;
          goto _L2
    }

    static int findResFixedBg(String s, String s1)
    {
        int i;
        int j;
        i = -1;
        j = sBgMap.length;
        if(!Launcher.USE_MAINMENU_ICONMODE) goto _L2; else goto _L1
_L1:
        int k = 0;
_L9:
        if(k >= j) goto _L2; else goto _L3
_L3:
        BgMap bgmap = sBgMap[k];
        if(!s.startsWith(bgmap.mPackageName)) goto _L5; else goto _L4
_L4:
        if(s1 != null && bgmap.mClassName != null) goto _L7; else goto _L6
_L6:
        i = bgmap.mResid;
_L2:
        return i;
_L7:
        if(!s1.equals(bgmap.mClassName))
            break; /* Loop/switch isn't completed */
        i = bgmap.mResid;
        if(true) goto _L2; else goto _L5
_L5:
        k++;
        if(true) goto _L9; else goto _L8
_L8:
    }

    static Drawable getDrawableIconBg(String s, String s1, Context context, boolean flag)
    {
        Resources resources = context.getResources();
        boolean flag1 = false;
        if(flag)
            flag1 = true;
        int j;
        if(flag1 && s != null)
        {
            j = findResFixedBg(s, s1);
            if(j == -1)
            {
                long l = SystemClock.uptimeMillis() % (long)sRandomIconBg.length;
                j = sRandomIconBg[(int)l];
            }
        } else
        if(s == null)
        {
            j = sRandom3rdIconBg[0];
        } else
        {
            int i = s.length();
            j = sRandom3rdIconBg[i % sRandom3rdIconBg.length];
        }
        return resources.getDrawable(j);
    }

    private static void initStatics(Context context)
    {
        Resources resources = context.getResources();
        float f = resources.getDisplayMetrics().density;
        int i = (int)resources.getDimension(0x1050000);
        sIconHeight = i;
        sIconWidth = i;
        int j = sIconWidth;
        sIconTextureHeight = j;
        sIconTextureWidth = j;
        sBlurPaint.setMaskFilter(new BlurMaskFilter(5F * f, android.graphics.BlurMaskFilter.Blur.NORMAL));
        sGlowColorPressedPaint.setColor(-15616);
        sGlowColorPressedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
        sGlowColorFocusedPaint.setColor(-29184);
        sGlowColorFocusedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
        ColorMatrix colormatrix = new ColorMatrix();
        colormatrix.setSaturation(0.2F);
        sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(colormatrix));
        sDisabledPaint.setAlpha(136);
    }

    static int roundToPow2(int i)
    {
        int j = i >> 1;
        int k;
        for(k = 0x8000000; k != 0 && (j & k) == 0; k >>= 1);
        for(; k != 0; k >>= 1)
            j |= k;

        int l = j + 1;
        if(l != i)
            l <<= 1;
        return l;
    }

    static void zOrderTweakMoveChild(ViewGroup viewgroup, int i, int j, boolean flag)
    {
        if(i != j) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int k = viewgroup.getChildCount();
        viewgroup.getChildAt(i).bringToFront();
        for(int l = 0; l < k - j - 1; l++)
            viewgroup.getChildAt(j).bringToFront();

        if(flag)
            viewgroup.requestLayout();
        if(true) goto _L1; else goto _L3
_L3:
    }

    static final BgMap sBgMap[] = BgMapFactory.getBgMap();
    private static final Paint sBlurPaint = new Paint();
    private static final Rect sBounds = new Rect();
    private static Canvas sCanvas;
    static int sColorIndex = 0;
    static int sColors[];
    private static final Paint sDisabledPaint = new Paint();
    private static final Paint sGlowColorFocusedPaint = new Paint();
    private static final Paint sGlowColorPressedPaint = new Paint();
    private static int sIconHeight = -1;
    private static int sIconTextureHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconWidth = -1;
    private static final Rect sOldBounds = new Rect();
    private static final Paint sPaint = new Paint();
    static final int sRandom3rdIconBg[];
    static final int sRandomIconBg[];

    static 
    {
        sCanvas = new Canvas();
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        int ai[] = new int[3];
        ai[0] = 0xffff0000;
        ai[1] = 0xff00ff00;
        ai[2] = 0xff0000ff;
        sColors = ai;
        int ai1[] = new int[5];
        ai1[0] = 0x7f02002f;
        ai1[1] = 0x7f020030;
        ai1[2] = 0x7f020031;
        ai1[3] = 0x7f020032;
        ai1[4] = 0x7f020033;
        sRandomIconBg = ai1;
        int ai2[] = new int[5];
        ai2[0] = 0x7f0200c6;
        ai2[1] = 0x7f0200c1;
        ai2[2] = 0x7f0200f1;
        ai2[3] = 0x7f0200d7;
        ai2[4] = 0x7f0200cb;
        sRandom3rdIconBg = ai2;
    }
}
