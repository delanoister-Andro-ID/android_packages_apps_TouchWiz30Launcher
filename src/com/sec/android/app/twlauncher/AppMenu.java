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

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherConfig, Launcher, ApplicationInfo

public class AppMenu extends FrameLayout
    implements MenuItemView.UninstallableMarkerDrawParent
{
    private class Animate
        implements Runnable
    {

        public void run()
        {
            Scroller scroller = mScroller;
            boolean flag = scroller.computeScrollOffset();
            int i = scroller.getCurrX();
            int j = scroller.getCurrY();
            View view = mTargetView;
            if(flag)
            {
                view.layout(i, j, i + view.getWidth(), j + view.getHeight());
                invalidate();
                post(this);
            } else
            {
                view.layout(i, j, i + view.getWidth(), j + view.getHeight());
                invalidate();
            }
        }

        public void start(View view, Rect rect, int i)
        {
            stop();
            mTargetView = view;
            Rect rect1 = mRect;
            view.getHitRect(rect1);
            mScroller.startScroll(rect1.left, rect1.top, rect.left - rect1.left, rect.top - rect1.top, i);
            post(this);
        }

        public void stop()
        {
            removeCallbacks(this);
        }

        private Rect mRect;
        private Scroller mScroller;
        private View mTargetView;
        final AppMenu this$0;

        Animate()
        {
            this$0 = AppMenu.this;
            super();
            mRect = new Rect();
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }
    }


    public AppMenu(Context context)
    {
        this(context, ((AttributeSet) (null)));
    }

    public AppMenu(Context context, int i)
    {
        super(context);
        mOrientation = 1;
        mTmpRect = new Rect();
        mEditIndex = -1;
        mOrientation = i;
        mChildAnimate = new Animate[mItemNumOfPage];
        init();
    }

    public AppMenu(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public AppMenu(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mOrientation = 1;
        mTmpRect = new Rect();
        mEditIndex = -1;
        init();
    }

    private void init()
    {
        setClickable(true);
        mPaint = new Paint();
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Context context = getContext();
        int i = -1;
        if(configuration.orientation != mOrientation)
        {
            i = configuration.orientation;
            configuration.orientation = mOrientation;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        mColumnNum = LauncherConfig.getColumnNo(context);
        mItemNumOfPage = LauncherConfig.getItemNoOfPage(context);
        mChildAnimate = new Animate[mItemNumOfPage];
        mAppHeight = resources.getDimensionPixelSize(0x7f090006);
        mVerticalSpace = resources.getDimensionPixelSize(0x7f090007);
        mEditLeftOffset = resources.getDimensionPixelSize(0x7f090011);
        mEditTopOffset = resources.getDimensionPixelSize(0x7f090012);
        mDeleteIconTopOffset = resources.getDimensionPixelSize(0x7f090008) + resources.getDimensionPixelSize(0x7f090016);
        mDeleteIconRightOffset = resources.getDimensionPixelSize(0x7f090017);
        mIconWidth = resources.getDimensionPixelSize(0x7f090013);
        mTopOffset = resources.getDimensionPixelSize(0x7f09000a);
        mLRPadding = resources.getDimensionPixelSize(0x7f090018);
        mRightOffset = resources.getDimensionPixelSize(0x7f090019);
        mLeftStartOffset = resources.getDimensionPixelSize(0x7f090039);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mLeftOffset = resources.getDimensionPixelSize(0x7f090038);
            Drawable drawable = resources.getDrawable(0x7f020026);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mEditFoldBg1 = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable.draw(new Canvas(mEditFoldBg1));
            Drawable drawable1 = resources.getDrawable(0x7f020027);
            drawable1.setBounds(0, 0, drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
            mEditFoldBg2 = Bitmap.createBitmap(drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable1.draw(new Canvas(mEditFoldBg2));
            Drawable drawable2 = resources.getDrawable(0x7f020028);
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
            mEditFoldBg3 = Bitmap.createBitmap(drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable2.draw(new Canvas(mEditFoldBg3));
            Drawable drawable3 = resources.getDrawable(0x7f020023);
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
            mEditFoldBgDelete1 = Bitmap.createBitmap(drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable3.draw(new Canvas(mEditFoldBgDelete1));
            Drawable drawable4 = resources.getDrawable(0x7f020024);
            drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight());
            mEditFoldBgDelete2 = Bitmap.createBitmap(drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable4.draw(new Canvas(mEditFoldBgDelete2));
            Drawable drawable5 = resources.getDrawable(0x7f020025);
            drawable5.setBounds(0, 0, drawable5.getIntrinsicWidth(), drawable5.getIntrinsicHeight());
            mEditFoldBgDelete3 = Bitmap.createBitmap(drawable5.getIntrinsicWidth(), drawable5.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
            drawable5.draw(new Canvas(mEditFoldBgDelete3));
        }
        if(i != -1)
        {
            android.util.DisplayMetrics displaymetrics = resources.getDisplayMetrics();
            configuration.orientation = i;
            resources.updateConfiguration(configuration, displaymetrics);
        }
        for(int j = 0; j < mItemNumOfPage; j++)
            mChildAnimate[j] = new Animate();

        setAnimationCacheEnabled(false);
    }

    void cellToPoint(int i, int ai[])
    {
        int j = i / mColumnNum;
        int k = i % mColumnNum;
        ai[0] = mLRPadding + k * mAppWidth;
        ai[1] = mTopOffset + j * (mAppHeight + mVerticalSpace);
    }

    void cellToPoint(View view, int ai[])
    {
        Rect rect = new Rect();
        view.getHitRect(rect);
        if(rect.isEmpty())
        {
            cellToPoint(indexOfChild(view), ai);
        } else
        {
            ai[0] = rect.left;
            ai[1] = rect.top;
        }
    }

    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if(mEditBg != null && mEditIndex != -1)
        {
            View view = getChildAt(mEditIndex);
            if(view != null && view.getVisibility() != 0)
                canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset, view.getTop() + mEditTopOffset, mPaint);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        boolean flag;
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            boolean flag1 = super.drawChild(canvas, view, l);
            if(mImage != null)
            {
                Object obj = view.getTag();
                boolean flag2 = false;
                boolean flag3 = false;
                int i;
                if(obj != null && (obj instanceof ApplicationInfo))
                {
                    ApplicationInfo applicationinfo = (ApplicationInfo)obj;
                    if(!applicationinfo.systemApp)
                        flag2 = true;
                    else
                        flag2 = false;
                    if(applicationinfo.componentName.getPackageName().startsWith("com.google.android") || applicationinfo.componentName.getPackageName().startsWith("com.android.vending"))
                        flag3 = true;
                    else
                        flag3 = false;
                }
                i = ((ViewGroup)view.getParent()).indexOfChild(view);
                if(flag2)
                {
                    if(flag3)
                        canvas.drawBitmap(mEditFoldBgDelete3, view.getRight() - mEditFoldBgDelete3.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
                    else
                    if(i % 2 == 0)
                        canvas.drawBitmap(mEditFoldBgDelete1, view.getRight() - mEditFoldBgDelete1.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
                    else
                        canvas.drawBitmap(mEditFoldBgDelete2, view.getRight() - mEditFoldBgDelete2.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
                } else
                if(flag3)
                    canvas.drawBitmap(mEditFoldBg3, view.getRight() - mEditFoldBg3.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
                else
                if(i % 2 == 0)
                    canvas.drawBitmap(mEditFoldBg1, view.getRight() - mEditFoldBg1.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
                else
                    canvas.drawBitmap(mEditFoldBg2, view.getRight() - mEditFoldBg2.getWidth() - view.getPaddingLeft(), view.getTop() + view.getPaddingTop(), mPaint);
            }
            flag = flag1;
        } else
        {
            if(mImage != null && mEditBg != null)
                canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset, view.getTop() + mEditTopOffset, mPaint);
            flag = super.drawChild(canvas, view, l);
        }
        return flag;
    }

    public void drawChildUninstallableMarker(Canvas canvas, View view)
    {
        if(mImage != null)
        {
            Object obj = view.getTag();
            if(obj != null && (obj instanceof ApplicationInfo) && !((ApplicationInfo)obj).systemApp)
            {
                Bitmap bitmap = mImage;
                canvas.drawBitmap(bitmap, view.getWidth() - bitmap.getWidth() - ((view.getWidth() - mIconWidth) / 2 + mDeleteIconRightOffset), 0 + mDeleteIconTopOffset, mPaint);
            }
        }
    }

    public int getChildHeight()
    {
        return mAppHeight;
    }

    public int getChildWidth()
    {
        return mAppWidth;
    }

    public int getLRPadding()
    {
        return mLRPadding;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = mTopOffset;
        int j1;
        Rect rect;
        int k1;
        int l1;
        int i2;
        int j2;
        if(Launcher.USE_MAINMENU_ICONMODE)
            j1 = mLeftOffset;
        else
            j1 = mLeftStartOffset;
        rect = mTmpRect;
        k1 = getChildCount();
        l1 = mVerticalSpace;
        i2 = 0;
        j2 = 0;
        while(j2 < k1) 
        {
            View view = getChildAt(j2);
            if(view.getVisibility() == 8)
                continue;
            int k2 = view.getMeasuredWidth();
            int l2 = view.getMeasuredHeight();
            view.getHitRect(rect);
            if(!rect.isEmpty() && mEnabledChildAnimation)
            {
                rect.set(j1, i1, j1 + k2, i1 + l2);
                mChildAnimate[j2].start(view, rect, 300);
            } else
            {
                view.layout(j1, i1, j1 + k2, i1 + l2);
            }
            j1 += k2;
            if(Launcher.USE_MAINMENU_ICONMODE)
                j1 += mLRPadding;
            if(++i2 % mColumnNum == 0)
            {
                i1 = l1 + (i1 + l2);
                if(Launcher.USE_MAINMENU_ICONMODE)
                    j1 = mLeftOffset;
                else
                    j1 = mLeftStartOffset;
            }
            j2++;
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        android.view.View.MeasureSpec.getSize(j);
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        if(Launcher.USE_MAINMENU_ICONMODE)
            mAppWidth = (k - 2 * mLRPadding - mRightOffset - mLeftOffset) / mColumnNum;
        else
            mAppWidth = (k - 2 * mLRPadding - mRightOffset) / mColumnNum;
        l = mAppWidth;
        i1 = mAppHeight;
        j1 = android.view.View.MeasureSpec.makeMeasureSpec(l, 0x40000000);
        k1 = android.view.View.MeasureSpec.makeMeasureSpec(i1, 0x40000000);
        l1 = getChildCount();
        for(int i2 = 0; i2 < l1; i2++)
            getChildAt(i2).measure(j1, k1);

    }

    public void requestChildFocus(View view, View view1)
    {
        super.requestChildFocus(view, view1);
        if(view != null)
        {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    public void setBitmap(Bitmap bitmap)
    {
        mImage = bitmap;
    }

    protected void setChildrenDrawingCacheEnabled(boolean flag)
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
        {
            View view = getChildAt(j);
            view.setDrawingCacheEnabled(flag);
            view.buildDrawingCache(true);
        }

    }

    protected void setChildrenDrawnWithCacheEnabled(boolean flag)
    {
        super.setChildrenDrawnWithCacheEnabled(flag);
    }

    public void setEditBg(Bitmap bitmap)
    {
        mEditBg = bitmap;
    }

    public void setEditIndex(int i)
    {
        mEditIndex = i;
    }

    public void setEnabledChildAnimation(boolean flag)
    {
        mEnabledChildAnimation = flag;
    }

    private int mAppHeight;
    private int mAppWidth;
    private Animate mChildAnimate[];
    private int mColumnNum;
    private int mDeleteIconRightOffset;
    private int mDeleteIconTopOffset;
    private Bitmap mEditBg;
    private Bitmap mEditFoldBg1;
    private Bitmap mEditFoldBg2;
    private Bitmap mEditFoldBg3;
    private Bitmap mEditFoldBgDelete1;
    private Bitmap mEditFoldBgDelete2;
    private Bitmap mEditFoldBgDelete3;
    private int mEditIndex;
    private int mEditLeftOffset;
    private int mEditTopOffset;
    private boolean mEnabledChildAnimation;
    private int mIconWidth;
    private Bitmap mImage;
    private int mItemNumOfPage;
    private int mLRPadding;
    private int mLeftOffset;
    private int mLeftStartOffset;
    private int mOrientation;
    private Paint mPaint;
    private int mRightOffset;
    private Rect mTmpRect;
    private int mTopOffset;
    private int mVerticalSpace;
}
