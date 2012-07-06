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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ShortcutInfo, LauncherApplication, BadgeCache, Launcher, 
//            FastBitmapDrawable

public class BubbleTextView extends TextView
{

    public BubbleTextView(Context context)
    {
        super(context);
        mTempRect = new Rect();
        mTempRect2 = new Rect();
        mRect = new RectF();
        init();
    }

    public BubbleTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mTempRect = new Rect();
        mTempRect2 = new Rect();
        mRect = new RectF();
        init();
    }

    public BubbleTextView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mTempRect = new Rect();
        mTempRect2 = new Rect();
        mRect = new RectF();
        init();
    }

    private void drawBadge(Canvas canvas)
    {
        Object obj = getTag();
        if(obj != null && (obj instanceof ShortcutInfo)) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i;
        int j;
        int k1;
        int i2;
        int k2;
        int k3;
        int l3;
        ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
        shortcutinfo.badgeCount = getBadgeCount(shortcutinfo);
        if(shortcutinfo.badgeCount <= 0)
            continue; /* Loop/switch isn't completed */
        int k;
        int l;
        int i1;
        int j1;
        int l1;
        String s;
        Rect rect;
        int j2;
        int l2;
        int i3;
        int j3;
        Drawable drawable1;
        int k4;
        int l4;
        if(mIsPressed)
        {
            mBadgeDrawable.setColorFilter(mColorFilter);
            mTextPaint.setColorFilter(mColorFilter);
        } else
        {
            mBadgeDrawable.setColorFilter(null);
            mTextPaint.setColorFilter(null);
        }
        if(shortcutinfo.badgeCount >= 100)
            mTextPaint.setTextSize(mFontSizeSmall);
        else
            mTextPaint.setTextSize(mFontSizeDefault);
        i = mBadgeDrawable.getIntrinsicWidth();
        j = mBadgeDrawable.getIntrinsicHeight();
        mBadgeDrawable.getPadding(mTempRect);
        k = mTempRect.left;
        l = mTempRect.top;
        i1 = mTempRect.bottom;
        j1 = mTempRect.right;
        k1 = i - k - j1;
        l1 = j - l - i1;
        s = Integer.toString(shortcutinfo.badgeCount);
        rect = mTempRect2;
        mTextPaint.getTextBounds(s, 0, s.length(), rect);
        i2 = rect.width();
        j2 = rect.height();
        k2 = 0;
        l2 = 0;
        if(i2 < k1)
        {
            k2 = (k1 - i2) / 2;
            i2 = k1;
        }
        if(j2 < l1)
        {
            l2 = (l1 - j2) / 2;
            j2 = l1;
        }
        i3 = j1 + (i2 + k);
        j3 = i1 + (j2 + l);
        k3 = mScrollY;
        if(shortcutinfo.badgeCount < 100)
            break; /* Loop/switch isn't completed */
        l3 = (mScrollX + getWidth()) - mBadgeRightOffset - i3;
        drawable1 = mBadgeDrawable;
        k4 = l3 + i3;
        l4 = k3 + j3;
        drawable1.setBounds(l3, k3, k4, l4);
_L4:
        mBadgeDrawable.draw(canvas);
        canvas.drawText(s, k + (l3 + (k2 - rect.left)), l + (k3 + (l2 - rect.top)), mTextPaint);
        if(true) goto _L1; else goto _L3
_L3:
        l3 = (mScrollX + getWidth()) - mBadgeRightOffset - i;
        Drawable drawable = mBadgeDrawable;
        int i4 = l3 + i;
        int j4 = k3 + j;
        drawable.setBounds(l3, k3, i4, j4);
        if(i2 > k1)
            k2 = (k1 - i2) / 2;
          goto _L4
        if(true) goto _L1; else goto _L5
_L5:
    }

    private int getBadgeCount(ShortcutInfo shortcutinfo)
    {
        int i;
        if(shortcutinfo == null || shortcutinfo.intent == null || shortcutinfo.intent.getComponent() == null)
            i = 0;
        else
            i = ((LauncherApplication)getContext().getApplicationContext()).getBadgeCache().getBadgeCount(shortcutinfo.intent.getComponent());
        return i;
    }

    private void init()
    {
        setFocusable(true);
        mBackground = getBackground();
        setBackgroundDrawable(null);
        mBackground.setCallback(this);
        Resources resources = getResources();
        mPaint = new Paint(1);
        mPaint.setColor(resources.getColor(0x7f070002));
        float f = resources.getDisplayMetrics().density;
        mCornerRadius = 8F * f;
        mPaddingH = 5F * f;
        mPaddingV = 1.0F * f;
        mColorFilter = new PorterDuffColorFilter(0x7f000000, android.graphics.PorterDuff.Mode.SRC_ATOP);
        if(Launcher.USE_MAINMENU_ICONMODE)
            mBadgeDrawable = resources.getDrawable(0x7f020036);
        else
            mBadgeDrawable = resources.getDrawable(0x7f020035);
        mFontSizeDefault = resources.getDimensionPixelSize(0x7f090021);
        mFontSizeSmall = resources.getDimensionPixelSize(0x7f090022);
        mBadgeRightOffset = resources.getDimensionPixelSize(0x7f090020);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mFontSizeDefault);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mTextPaint.setColor(resources.getColor(0x7f070012));
            mTextPaint.setFakeBoldText(true);
        } else
        {
            mTextPaint.setColor(resources.getColor(0x7f070010));
        }
    }

    public void draw(Canvas canvas)
    {
        Drawable drawable = mBackground;
        if(drawable != null)
        {
            int k = mScrollX;
            int l = mScrollY;
            if(mBackgroundSizeChanged)
            {
                drawable.setBounds(0, 0, mRight - mLeft, mBottom - mTop);
                mBackgroundSizeChanged = false;
            }
            Layout layout;
            RectF rectf;
            int i;
            int j;
            if((k | l) == 0)
            {
                drawable.draw(canvas);
            } else
            {
                canvas.translate(k, l);
                drawable.draw(canvas);
                canvas.translate(-k, -l);
            }
        }
        layout = getLayout();
        rectf = mRect;
        i = getCompoundPaddingLeft();
        j = getExtendedPaddingTop();
        rectf.set(((float)i + layout.getLineLeft(0)) - mPaddingH, (float)(j + layout.getLineTop(0)) - mPaddingV, Math.min((float)i + layout.getLineRight(0) + mPaddingH, (mScrollX + mRight) - mLeft), (float)(j + layout.getLineBottom(0)) + mPaddingV);
        canvas.drawRoundRect(rectf, mCornerRadius, mCornerRadius, mPaint);
        super.draw(canvas);
        drawBadge(canvas);
    }

    protected void drawableStateChanged()
    {
        Drawable drawable = mBackground;
        int ai[] = getDrawableState();
        boolean flag = false;
        int i = 0;
        do
        {
label0:
            {
                if(i < ai.length)
                {
                    if(ai[i] != 0x10100a7)
                        break label0;
                    flag = true;
                }
                if(!flag && drawable != null && drawable.isStateful())
                    drawable.setState(ai);
                if(flag)
                {
                    mIsPressed = true;
                    Drawable adrawable[] = getCompoundDrawables();
                    if(adrawable != null && adrawable[1] != null && mPressDrawable == null)
                    {
                        mCompoundDrawable = adrawable[1];
                        Rect rect = adrawable[1].copyBounds();
                        Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(), android.graphics.Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        adrawable[1].setBounds(0, 0, rect.width(), rect.height());
                        adrawable[1].draw(canvas);
                        adrawable[1].setBounds(rect);
                        mPressDrawable = new FastBitmapDrawable(bitmap);
                        mPressDrawable.setColorFilter(mColorFilter);
                        mPressDrawable.setCallback(null);
                    }
                    if(mPressDrawable != null)
                    {
                        setCompoundDrawablesWithIntrinsicBounds(null, mPressDrawable, null, null);
                        invalidate();
                    }
                } else
                {
                    if(mCompoundDrawable != null)
                        setCompoundDrawablesWithIntrinsicBounds(null, mCompoundDrawable, null, null);
                    if(mIsPressed)
                    {
                        mIsPressed = false;
                        invalidate();
                    }
                }
                super.drawableStateChanged();
                return;
            }
            i++;
        } while(true);
    }

    protected boolean setFrame(int i, int j, int k, int l)
    {
        if(mLeft != i || mRight != k || mTop != j || mBottom != l)
            mBackgroundSizeChanged = true;
        return super.setFrame(i, j, k, l);
    }

    protected boolean verifyDrawable(Drawable drawable)
    {
        boolean flag;
        if(drawable == mBackground || super.verifyDrawable(drawable))
            flag = true;
        else
            flag = false;
        return flag;
    }

    private Drawable mBackground;
    private boolean mBackgroundSizeChanged;
    private Drawable mBadgeDrawable;
    private int mBadgeRightOffset;
    private ColorFilter mColorFilter;
    private Drawable mCompoundDrawable;
    private float mCornerRadius;
    private int mFontSizeDefault;
    private int mFontSizeSmall;
    private boolean mIsPressed;
    private float mPaddingH;
    private float mPaddingV;
    private Paint mPaint;
    private FastBitmapDrawable mPressDrawable;
    private final RectF mRect;
    private final Rect mTempRect;
    private final Rect mTempRect2;
    private Paint mTextPaint;
}
