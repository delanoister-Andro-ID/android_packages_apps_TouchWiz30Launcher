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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ApplicationInfo, ShortcutInfo, Launcher, Rotate3dAnimation

public class MenuItemView extends LinearLayout
{
    public static interface UninstallableMarkerDrawParent
    {

        public abstract void drawChildUninstallableMarker(Canvas canvas, View view);
    }


    public MenuItemView(Context context)
    {
        this(context, null);
    }

    public MenuItemView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mTmpRect = new Rect();
        mPaint = new Paint();
        mTempRect = new Rect();
        mTempRect2 = new Rect();
        mAnimationListener = null;
        init();
    }

    private void drawBadge(Canvas canvas, View view)
    {
        int i;
        i = 0;
        Object obj = getTag();
        if(obj != null)
            if(obj instanceof ApplicationInfo)
                i = ((ApplicationInfo)obj).badgeCount;
            else
            if(obj instanceof ShortcutInfo)
                i = ((ShortcutInfo)obj).badgeCount;
        if(i > 0) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int j;
        int k;
        int l1;
        int j2;
        int l2;
        int l3;
        int i4;
        int l;
        int i1;
        int j1;
        int k1;
        int i2;
        String s;
        Rect rect;
        int k2;
        int i3;
        int j3;
        int k3;
        Drawable drawable1;
        int l4;
        int i5;
        if(mIsPressed)
        {
            mBadgeDrawable.setColorFilter(mColorFilter);
            mPaint.setColorFilter(mColorFilter);
        } else
        {
            mBadgeDrawable.setColorFilter(null);
            mPaint.setColorFilter(null);
        }
        if(i >= 100)
            mPaint.setTextSize(mFontSizeSmall);
        else
            mPaint.setTextSize(mFontSizeDefault);
        j = mBadgeDrawable.getIntrinsicWidth();
        k = mBadgeDrawable.getIntrinsicHeight();
        mBadgeDrawable.getPadding(mTempRect);
        l = mTempRect.left;
        i1 = mTempRect.top;
        j1 = mTempRect.bottom;
        k1 = mTempRect.right;
        l1 = j - l - k1;
        i2 = k - i1 - j1;
        s = Integer.toString(i);
        rect = mTempRect2;
        mPaint.getTextBounds(s, 0, s.length(), rect);
        j2 = rect.width();
        k2 = rect.height();
        l2 = 0;
        i3 = 0;
        if(j2 < l1)
        {
            l2 = (l1 - j2) / 2;
            if(i < 10 && (l1 - j2) % 2 == 1)
                l2++;
            j2 = l1;
        }
        if(k2 < i2)
        {
            i3 = (i2 - k2) / 2;
            k2 = i2;
        }
        j3 = k1 + (j2 + l);
        k3 = j1 + (k2 + i1);
        l3 = view.getTop() + mBadgeTopOffset;
        if(i < 100)
            break; /* Loop/switch isn't completed */
        i4 = (view.getRight() + mBadgeRightOffset) - j3;
        drawable1 = mBadgeDrawable;
        l4 = i4 + j3;
        i5 = l3 + k3;
        drawable1.setBounds(i4, l3, l4, i5);
_L4:
        mBadgeDrawable.draw(canvas);
        canvas.drawText(s, l + (i4 + (l2 - rect.left)), i1 + (l3 + (i3 - rect.top)), mPaint);
        if(true) goto _L1; else goto _L3
_L3:
        i4 = (view.getRight() + mBadgeRightOffset) - j;
        Drawable drawable = mBadgeDrawable;
        int j4 = i4 + j;
        int k4 = l3 + k;
        drawable.setBounds(i4, l3, j4, k4);
        if(j2 > l1)
            l2 = (l1 - j2) / 2;
          goto _L4
        if(true) goto _L1; else goto _L5
_L5:
    }

    private void init()
    {
        Resources resources = getResources();
        if(Launcher.USE_MAINMENU_ICONMODE)
            mTopOffset = 0;
        else
            mTopOffset = resources.getDimensionPixelSize(0x7f090008);
        mFocusMargin = resources.getDimensionPixelSize(0x7f090014);
        mFocusedDrawable = resources.getDrawable(0x7f020011);
        if(Launcher.USE_MAINMENU_ICONMODE)
            mBadgeDrawable = resources.getDrawable(0x7f020036);
        else
            mBadgeDrawable = resources.getDrawable(0x7f020035);
        mColorFilter = new PorterDuffColorFilter(0x7f000000, android.graphics.PorterDuff.Mode.SRC_ATOP);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mBadgeTopOffset = resources.getDimensionPixelSize(0x7f09003b);
            mBadgeRightOffset = resources.getDimensionPixelSize(0x7f09003c);
        } else
        {
            mBadgeTopOffset = resources.getDimensionPixelSize(0x7f09001f);
            mBadgeRightOffset = resources.getDimensionPixelSize(0x7f090020);
        }
        mFontSizeDefault = resources.getDimensionPixelSize(0x7f090021);
        mFontSizeSmall = resources.getDimensionPixelSize(0x7f090022);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mFontSizeDefault);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mPaint.setColor(resources.getColor(0x7f070012));
            mPaint.setFakeBoldText(true);
        } else
        {
            mPaint.setColor(resources.getColor(0x7f070010));
        }
    }

    void applyRotation(float f, float f1)
    {
        applyRotation(f, f1, 0L);
    }

    void applyRotation(float f, float f1, long l)
    {
        Rotate3dAnimation rotate3danimation = new Rotate3dAnimation(f, f1, (float)getWidth() / 2.0F, (float)getHeight() / 2.0F, 0.0F, true);
        rotate3danimation.setRotateAxis(1);
        rotate3danimation.setDuration(200L);
        rotate3danimation.setStartOffset(l);
        rotate3danimation.setFillAfter(true);
        rotate3danimation.setInterpolator(new DecelerateInterpolator());
        if(mAnimationListener != null)
            rotate3danimation.setAnimationListener(mAnimationListener);
        startAnimation(rotate3danimation);
    }

    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if(!Launcher.USE_MAINMENU_ICONMODE)
        {
            android.view.ViewParent viewparent = getParent();
            if(viewparent instanceof UninstallableMarkerDrawParent)
                ((UninstallableMarkerDrawParent)viewparent).drawChildUninstallableMarker(canvas, this);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        boolean flag;
        if(view instanceof ImageView)
        {
            if(mIsPressed)
            {
                ImageView imageview = (ImageView)view;
                Drawable drawable = imageview.getDrawable();
                if(drawable != null)
                    drawable.setColorFilter(mColorFilter);
                Drawable drawable1 = imageview.getBackground();
                if(drawable1 != null)
                    drawable1.setColorFilter(mColorFilter);
                boolean flag3 = super.drawChild(canvas, view, l);
                if(drawable != null)
                    drawable.clearColorFilter();
                if(drawable1 != null)
                    drawable1.clearColorFilter();
                drawBadge(canvas, view);
                flag = flag3;
            } else
            if(mIsFocused)
            {
                int i = mFocusMargin;
                Rect rect = mTmpRect;
                rect.left = view.getLeft() - i;
                rect.right = i + (view.getLeft() + view.getWidth());
                rect.top = view.getTop() - i;
                rect.bottom = i + (view.getTop() + view.getHeight());
                mFocusedDrawable.setBounds(rect);
                mFocusedDrawable.draw(canvas);
                boolean flag2 = super.drawChild(canvas, view, l);
                drawBadge(canvas, view);
                flag = flag2;
            } else
            {
                boolean flag1 = super.drawChild(canvas, view, l);
                drawBadge(canvas, view);
                flag = flag1;
            }
        } else
        {
            flag = super.drawChild(canvas, view, l);
        }
        return flag;
    }

    protected void drawableStateChanged()
    {
        int ai[];
        int i;
        int j;
        super.drawableStateChanged();
        ai = getDrawableState();
        Drawable drawable = ((ImageView)findViewById(0x7f06000a)).getBackground();
        if(drawable != null)
            drawable.setState(ai);
        i = ai.length;
        j = 0;
_L1:
        if(j >= i)
            break MISSING_BLOCK_LABEL_98;
        if(ai[j] == 0x10100a7)
        {
            mIsPressed = true;
            invalidate();
        } else
        {
label0:
            {
                if(ai[j] != 0x101009c)
                    break label0;
                mIsFocused = true;
                mIsPressed = false;
                invalidate();
            }
        }
_L2:
        return;
        j++;
          goto _L1
        if(mIsPressed || mIsFocused)
        {
            mIsPressed = false;
            mIsFocused = false;
            invalidate();
        }
          goto _L2
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        super.onLayout(flag, i, j, k, l);
        int i1 = getChildCount();
        int j1 = mTopOffset;
        if(j1 > 0)
        {
            for(int k1 = 0; k1 < i1; k1++)
            {
                View view = getChildAt(k1);
                view.layout(view.getLeft(), j1 + view.getTop(), view.getRight(), j1 + view.getBottom());
            }

        }
    }

    public void setBackgroundDrawable(Drawable drawable)
    {
        if(Launcher.USE_MAINMENU_ICONMODE)
            super.setBackgroundDrawable(drawable);
        else
            ((ImageView)findViewById(0x7f06000a)).setBackgroundDrawable(drawable);
    }

    public void setBackgroundResource(int i)
    {
        if(Launcher.USE_MAINMENU_ICONMODE)
            super.setBackgroundResource(i);
        else
            ((ImageView)findViewById(0x7f06000a)).setBackgroundResource(i);
    }

    public void setImageDrawable(Drawable drawable)
    {
        ((ImageView)findViewById(0x7f06000a)).setImageDrawable(drawable);
    }

    public void setText(CharSequence charsequence)
    {
        ((TextView)findViewById(0x7f06000b)).setText(charsequence);
    }

    private android.view.animation.Animation.AnimationListener mAnimationListener;
    private Drawable mBadgeDrawable;
    private int mBadgeRightOffset;
    private int mBadgeTopOffset;
    private ColorFilter mColorFilter;
    private int mFocusMargin;
    private Drawable mFocusedDrawable;
    private int mFontSizeDefault;
    private int mFontSizeSmall;
    private boolean mIsFocused;
    private boolean mIsPressed;
    private Paint mPaint;
    private final Rect mTempRect;
    private final Rect mTempRect2;
    private Rect mTmpRect;
    private int mTopOffset;
}
