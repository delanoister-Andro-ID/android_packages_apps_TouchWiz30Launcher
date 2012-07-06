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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.nemustech.tiffany.widget.TFAnimateEngine;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherConfig, Utilities, MenuManager, AppMenu, 
//            ApplicationInfo, Launcher, MenuDrawer

public class QuickViewMainMenu extends FrameLayout
    implements android.view.View.OnClickListener, android.view.View.OnLongClickListener
{
    private class Animate
        implements Runnable
    {

        public void run()
        {
            Scroller scroller = mScroller;
            boolean flag = scroller.computeScrollOffset();
            int i = scroller.getCurrX();
            mAnimateEngine.getRect(mStartRects, mDestRects, mResultRects, (float)i / 100F);
            View view = mTargetView;
            if(flag)
            {
                view.layout(mResultRects[0].left, mResultRects[0].top, mResultRects[0].right, mResultRects[0].bottom);
                invalidate();
                post(this);
            } else
            {
                view.layout(mDestRects[0].left, mDestRects[0].top, mDestRects[0].right, mDestRects[0].bottom);
                invalidate();
            }
        }

        public void start(View view, Rect rect)
        {
            stop();
            mTargetView = view;
            view.getHitRect(mStartRects[0]);
            mDestRects[0].set(rect);
            mScroller.startScroll(0, 0, 100, 0, 300);
            post(this);
        }

        public void stop()
        {
            removeCallbacks(this);
        }

        private TFAnimateEngine mAnimateEngine;
        private Rect mDestRects[];
        private Rect mResultRects[];
        private Scroller mScroller;
        private Rect mStartRects[];
        private View mTargetView;
        final QuickViewMainMenu this$0;

        Animate()
        {
            this$0 = QuickViewMainMenu.this;
            super();
            Rect arect[] = new Rect[1];
            arect[0] = new Rect();
            mStartRects = arect;
            Rect arect1[] = new Rect[1];
            arect1[0] = new Rect();
            mDestRects = arect1;
            Rect arect2[] = new Rect[1];
            arect2[0] = new Rect();
            mResultRects = arect2;
            mAnimateEngine = new TFAnimateEngine();
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }
    }


    public QuickViewMainMenu(Context context)
    {
        QuickViewMainMenu(context, null);
    }

    public QuickViewMainMenu(Context context, AttributeSet attributeset)
    {
        QuickViewMainMenu(context, attributeset, 0);
    }

    public QuickViewMainMenu(Context context, AttributeSet attributeset, int i)
    {
        FrameLayout(context, attributeset, i);
        mAnimationStyle = 1;
        mScrollState = 0;
        mScrollRunnable = new ScrollRunnable();
        mMaxDeltaY = 0;
        mTmpRect = new Rect();
        mScreenRect = new Rect();
        mTouchState = 0;
        mMultiTouchUsed = false;
        mPinchOutProcess = false;
        mScreenBgDrawablePadding = new Rect();
        mOrientation = 1;
        mUseFullScreenInLandScapeMode = true;
        mAnimationState = 7;
        mIsDBUpdate = false;
        mTmpLocation = new int[4];
        Resources resources = getResources();
        mItemNumOfPage = LauncherConfig.getItemNoOfPage(context);
        mPanelSizeShort = resources.getDimensionPixelSize(0x7f090027);
        mPanelSizeLong = resources.getDimensionPixelSize(0x7f090028);
        mPanelSizeLong2 = resources.getDimensionPixelSize(0x7f090029);
        init();
    }

    private void drop(int i, int j)
    {
        mDraggingView.setVisibility(0);
        mDraggingView.requestLayout();
        int k = getTouchedIndex(i, j);
        if(k != -1 && getChildAt(k).getTag() != null)
            swapScreen(k);
        else
            swapScreen(mDraggingIndex);
    }

    private void endDrag()
    {
        mTouchState = 0;
        mDraggingView = null;
        mTouchedView = null;
        mPinchIndex = -1;
        mDraggingIndex = -1;
        mOriginDragIndex = -1;
    }

    private void fling(int i)
    {
        if(getChildCount() > 0)
        {
            mScroller.fling(mScrollX, mScrollY, 0, i, 0, 0, 0, mMaxDeltaY);
            invalidate();
        }
    }

    private int getTouchedIndex(int i, int j)
    {
        if(mChildRects != null) goto _L2; else goto _L1
_L1:
        int i1 = -1;
_L9:
        return i1;
_L2:
        int k;
        int l;
        k = mChildRects.length;
        l = 0;
_L5:
        if(l >= k)
            break; /* Loop/switch isn't completed */
          goto _L3
_L7:
        l++;
        if(true) goto _L5; else goto _L4
_L3:
        if(mChildRects[l] == null || !mChildRects[l].contains(i + mScrollX, j + mScrollY)) goto _L7; else goto _L6
_L6:
        i1 = l;
        continue; /* Loop/switch isn't completed */
_L4:
        i1 = -1;
        if(true) goto _L9; else goto _L8
_L8:
    }

    private void init()
    {
        mAniEngine = new TFAnimateEngine();
        mAniEngine.setInterpolator(new AccelerateDecelerateInterpolator());
        mOrientation = getResources().getConfiguration().orientation;
        mUseFullScreenInLandScapeMode = LauncherConfig.landscapeScreen_isUseFullScreenQuickView(getContext());
        Context context = getContext();
        ViewConfiguration viewconfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mScroller = new Scroller(context);
        mMinimumVelocity = viewconfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        setClipChildren(false);
    }

    private boolean isAvailableRect(Rect rect)
    {
        boolean flag;
        if(rect.right < mScreenRect.left || rect.top > mScreenRect.bottom + mScrollY || rect.left > mScreenRect.right || rect.bottom < mScreenRect.top + mScrollY)
            flag = false;
        else
            flag = true;
        return flag;
    }

    private View makeScreen(int i)
    {
        View view;
        view = new View(getContext());
        view.setBackgroundDrawable(mScreenBgDrawable);
        view.setTag(Integer.valueOf(i));
        if(mOrientation != 1) goto _L2; else goto _L1
_L1:
        addView(view, i, new LayoutParams(mPanelSizeShort, mPanelSizeLong));
_L4:
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
        return view;
_L2:
        if(mOrientation == 2)
            addView(view, i, new LayoutParams(mPanelSizeLong2, mPanelSizeShort));
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void onSwap(int i)
    {
        View view = mMenuManager.getChildAt(mOriginDragIndex);
        Utilities.zOrderTweakMoveChild(mMenuManager, mMenuManager.indexOfChild(view), i, true);
        updateTag();
        updateAppInfo();
    }

    private void savemenupage()
    {
        MenuManager menumanager = (MenuManager)mMenuManager;
        menumanager.stopUpdateDB();
        menumanager.startUpdateDB();
    }

    private void scroll(int i, int j)
    {
        if(j >= 0) goto _L2; else goto _L1
_L1:
        if(mScrollY > 0)
        {
            if(j + mScrollY < 0)
                smoothScrollBy(0, -mScrollY);
            else
                smoothScrollBy(0, j);
        } else
        {
            invalidate();
        }
_L4:
        return;
_L2:
        if(j > 0)
        {
            int k = mMaxDeltaY - mScrollY;
            if(k > 0)
            {
                if(k > j)
                    smoothScrollBy(0, j);
                else
                    smoothScrollBy(0, k);
            } else
            {
                invalidate();
            }
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void setCloseEndRect(int i)
    {
        int j = getChildCount();
        getLocationOnScreen(mTmpLocation);
        int k = mTmpLocation[1];
        View view = mMenuManager.getChildAt(0);
        view.getLocationOnScreen(mTmpLocation);
        int l = view.getWidth();
        int i1 = view.getHeight();
        int j1 = view.getLeft();
        int k1 = mScrollY + view.getTop();
        int l1 = mMenuManager.getPaddingRight();
        mMenuManager.getLocationOnScreen(mTmpLocation);
        int i2 = mTmpLocation[1] + (int)getContext().getResources().getDimension(0x1050004);
        int j2 = mScreenBgDrawablePadding.left;
        int k2 = mScreenBgDrawablePadding.right;
        int l2 = mScreenBgDrawablePadding.top;
        int i3 = mScreenBgDrawablePadding.bottom;
        if(mFinishRects == null || mFinishRects.length != j)
        {
            mFinishRects = new Rect[j];
            for(int j3 = 0; j3 < j; j3++)
                mFinishRects[j3] = new Rect();

        }
        mFinishRects[i].set(j1 - j2, k1 - l2, k2 + (j1 + l), i3 + (k1 + i1));
        if(mOrientation == 2 && mUseFullScreenInLandScapeMode)
            mFinishRects[i].offset(0, i2 - k);
        if(mAnimationStyle == 0 || mChildRects == null)
        {
            for(int k3 = i - 1; k3 >= 0; k3--)
            {
                mFinishRects[k3].set(mFinishRects[k3 + 1]);
                mFinishRects[k3].offset(-mFinishRects[k3].width(), 0);
            }

            for(int l3 = i + 1; l3 < j; l3++)
            {
                mFinishRects[l3].set(mFinishRects[l3 - 1]);
                mFinishRects[l3].offset(l1 + mFinishRects[l3].width(), 0);
            }

        } else
        {
            Log.e("QuickViewMainMenu", "setCloseEndRect : NEW:");
            Rect rect = mFinishRects[i];
            Rect rect1 = mChildRects[i];
            float f = (float)rect.width() / (float)rect1.width();
            float f1 = (float)rect.height() / (float)rect1.height();
            int i4 = 0;
            while(i4 < j) 
            {
                if(i4 != i)
                {
                    Rect rect2 = mChildRects[i4];
                    int j4 = rect2.left - rect1.left;
                    int k4 = rect2.top - rect1.top;
                    int l4 = (int)(f * (float)j4);
                    int i5 = (int)(f1 * (float)k4);
                    mFinishRects[i4].set(l4 + rect.left, i5 + rect.top, l4 + rect.right, i5 + rect.bottom);
                }
                i4++;
            }
        }
    }

    private void setOpenStartRect(int i)
    {
        int j = getChildCount();
        getLocationOnScreen(mTmpLocation);
        int k = mTmpLocation[1];
        View view = mMenuManager.getChildAt(0);
        view.getLocationOnScreen(mTmpLocation);
        int l = view.getWidth();
        int i1 = view.getHeight();
        int j1 = view.getLeft();
        int k1 = mScrollY + view.getTop();
        mMenuManager.getLocationOnScreen(mTmpLocation);
        int l1 = mTmpLocation[1] + (int)getContext().getResources().getDimension(0x1050004);
        int i2 = mScreenBgDrawablePadding.left;
        int j2 = mScreenBgDrawablePadding.right;
        int k2 = mScreenBgDrawablePadding.top;
        int l2 = mScreenBgDrawablePadding.bottom;
        int i3 = mMenuManager.getPaddingRight();
        if(mStartRects == null || mStartRects.length != j)
        {
            mStartRects = new Rect[j];
            for(int j3 = 0; j3 < j; j3++)
                mStartRects[j3] = new Rect();

        }
        mStartRects[i].set(j1 - i2, k1 - k2, j2 + (j1 + l), l2 + (k1 + i1));
        if(mOrientation == 2 && mUseFullScreenInLandScapeMode)
            mStartRects[i].offset(0, l1 - k);
        if(mAnimationStyle == 0 || mChildRects == null)
        {
            for(int k3 = i - 1; k3 >= 0; k3--)
            {
                mStartRects[k3].set(mStartRects[k3 + 1]);
                mStartRects[k3].offset(-mStartRects[k3].width(), 0);
            }

            for(int l3 = i + 1; l3 < j; l3++)
            {
                mStartRects[l3].set(mStartRects[l3 - 1]);
                mStartRects[l3].offset(i3 + mStartRects[l3].width(), 0);
            }

        } else
        {
            Log.e("QuickViewMainMenu", "setOpenStartRect : NEW:");
            Rect rect = mStartRects[i];
            Rect rect1 = mChildRects[i];
            float f = (float)rect.width() / (float)rect1.width();
            float f1 = (float)rect.height() / (float)rect1.height();
            int i4 = 0;
            while(i4 < j) 
            {
                if(i4 != i)
                {
                    Rect rect2 = mChildRects[i4];
                    int j4 = rect2.left - rect1.left;
                    int k4 = rect2.top - rect1.top;
                    int l4 = (int)(f * (float)j4);
                    int i5 = (int)(f1 * (float)k4);
                    mStartRects[i4].set(l4 + rect.left, i5 + rect.top, l4 + rect.right, i5 + rect.bottom);
                }
                i4++;
            }
        }
    }

    private void startDrag(View view)
    {
        mTouchState = 2;
        mDraggingIndex = ((Integer)view.getTag()).intValue();
        mOriginDragIndex = mDraggingIndex;
        mDraggingView = view;
        mTouchedView = view;
        view.invalidate();
    }

    private void swapScreen(int i)
    {
        Utilities.zOrderTweakMoveChild(this, indexOfChild(mDraggingView), i, true);
        onSwap(i);
    }

    private void updateAppInfo()
    {
        int i = mMenuManager.getChildCount();
        for(int j = 0; j < i; j++)
        {
            AppMenu appmenu = (AppMenu)mMenuManager.getChildAt(j);
            int k = appmenu.getChildCount();
            for(int l = 0; l < k && l < mItemNumOfPage; l++)
            {
                ApplicationInfo applicationinfo = (ApplicationInfo)appmenu.getChildAt(l).getTag();
                applicationinfo.topNum = 65535;
                applicationinfo.pageNum = j;
                applicationinfo.isUpdated = false;
                applicationinfo.editTopNum = 65535;
                applicationinfo.editPageNum = j;
                if(!mIsDBUpdate)
                    mIsDBUpdate = true;
            }

        }

    }

    private void updateTag()
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).setTag(Integer.valueOf(j));

    }

    void cancelDrag()
    {
        if(mDraggingView != null)
        {
            mDraggingView.setVisibility(0);
            removeView(mDraggingView);
            addView(mDraggingView, mOriginDragIndex);
            mDraggingView.requestLayout();
        }
        updateTag();
        mDeleteIndex = -1;
        mDeleteView = null;
        mTouchState = 0;
        mDraggingIndex = -1;
        mDraggingView = null;
        mTouchedView = null;
        mPinchIndex = -1;
        mOriginDragIndex = -1;
        postInvalidate();
    }

    void clearChildrenCache()
    {
        MenuManager menumanager = (MenuManager)mMenuManager;
        int i = menumanager.getChildCount();
        for(int j = 0; j < i; j++)
            ((AppMenu)menumanager.getChildAt(j)).setChildrenDrawnWithCacheEnabled(false);

    }

    void close()
    {
        savemenupage();
        mScrollY = 0;
        mEnabledChildAnimation = false;
        clearChildrenCache();
        setVisibility(4);
    }

    public void computeScroll()
    {
        if(mAnimationState == 1)
        {
            int j1 = ((MenuManager)mMenuManager).getCurrentScreen();
            mScrollTop = mChildRects[j1].top;
            if(mScrollY == 0 && mScrollTop > mChildTop)
                mScrollY = Math.min(mScrollTop - mChildTop, mMaxDeltaY);
        }
        if(mScroller.computeScrollOffset())
        {
            int i = mScrollX;
            int j = mScrollY;
            int k = mScroller.getCurrX();
            int l = mScroller.getCurrY();
            mScrollX = k;
            mScrollY = l;
            if(i != mScrollX || j != mScrollY)
                onScrollChanged(mScrollX, mScrollY, i, j);
            if(mTouchState == 2)
            {
                int i1 = mScrollY - j;
                View view = mDraggingView;
                if(view != null)
                {
                    Rect rect = mTmpRect;
                    view.getHitRect(rect);
                    rect.top = i1 + rect.top;
                    rect.bottom = i1 + rect.bottom;
                    view.layout(rect.left, rect.top, rect.right, rect.bottom);
                }
            }
            postInvalidate();
        }
    }

    protected void dispatchDraw(Canvas canvas)
    {
        int i;
        int j;
        int k;
        long l;
        i = getChildCount();
        j = getWidth();
        k = getHeight();
        l = getDrawingTime();
        if(j != 0 && k != 0) goto _L2; else goto _L1
_L1:
        return;
_L2:
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        if(mAnimationState == 1)
        {
            mAnimationStartTime = SystemClock.uptimeMillis();
            mAnimationState = 2;
            mPinchIndex = ((MenuManager)mMenuManager).getCurrentScreen();
            setOpenStartRect(mPinchIndex);
            mFinishRects = new Rect[i];
            mCurrentRects = new Rect[i];
            for(int k2 = 0; k2 < i; k2++)
            {
                mFinishRects[k2] = new Rect();
                mFinishRects[k2].set(mChildRects[k2]);
                if(k2 == mPinchIndex)
                    mScrollTop = mChildRects[k2].top;
                mCurrentRects[k2] = new Rect();
            }

        } else
        if(mAnimationState == 3)
        {
            mAnimationStartTime = SystemClock.uptimeMillis();
            mAnimationState = 4;
            mPinchIndex = mCurrentPage;
            setCloseEndRect(mPinchIndex);
            mStartRects = new Rect[i];
            mCurrentRects = new Rect[i];
            for(int j2 = 0; j2 < i; j2++)
            {
                mStartRects[j2] = new Rect();
                mStartRects[j2].set(mChildRects[j2]);
                mCurrentRects[j2] = new Rect();
            }

        }
        if(mTouchState == 2)
        {
            for(int i2 = 0; i2 < i; i2++)
            {
                View view2 = getChildAt(i2);
                if(view2 != mDraggingView)
                    drawChild(canvas, view2, l);
            }

            canvas.clipRect(mScrollX, mScrollY, j + mScrollX, k + mScrollY, android.graphics.Region.Op.REPLACE);
            drawChild(canvas, mDraggingView, l);
            continue; /* Loop/switch isn't completed */
        }
        if(mAnimationState != 2 && mAnimationState != 4)
            break; /* Loop/switch isn't completed */
        float f = SystemClock.uptimeMillis() - mAnimationStartTime;
        int i1;
        if(f >= (float)mAnimationDuration)
        {
            f = mAnimationDuration;
            if(mAnimationState == 4)
                mLauncher.closeQuickViewMainMenu();
            int k1;
            if(mAnimationState == 2)
                k1 = 7;
            else
                k1 = 8;
            mAnimationState = k1;
        }
        if((mCurrentRects == null || mCurrentRects.length != i) && i > 0)
            mCurrentRects = new Rect[i];
        mCurrentRects = mAniEngine.getRect(mStartRects, mFinishRects, mCurrentRects, f / (float)mAnimationDuration);
        i1 = 0;
        while(i1 < i) 
        {
            if(i1 != mPinchIndex)
            {
                View view1 = getChildAt(i1);
                canvas.clipRect(mCurrentRects[i1].left, mCurrentRects[i1].top, mCurrentRects[i1].right, mCurrentRects[i1].bottom, android.graphics.Region.Op.REPLACE);
                view1.layout(mCurrentRects[i1].left, mCurrentRects[i1].top, mCurrentRects[i1].right, mCurrentRects[i1].bottom);
                if(isAvailableRect(mCurrentRects[i1]))
                {
                    view1.setBackgroundDrawable(mScreenBgDrawable);
                    drawChild(canvas, view1, l);
                }
            }
            i1++;
        }
        if(mPinchIndex >= 0 && mPinchIndex < i)
        {
            int j1 = mPinchIndex;
            View view = getChildAt(j1);
            canvas.clipRect(mCurrentRects[j1].left, mCurrentRects[j1].top, mCurrentRects[j1].right, mCurrentRects[j1].bottom, android.graphics.Region.Op.REPLACE);
            view.layout(mCurrentRects[j1].left, mCurrentRects[j1].top, mCurrentRects[j1].right, mCurrentRects[j1].bottom);
            view.setBackgroundDrawable(mScreenBgDrawable);
            drawChild(canvas, view, l);
        }
        postInvalidate();
        if(true) goto _L1; else goto _L3
_L3:
        int l1 = 0;
        while(l1 < i) 
        {
            drawChild(canvas, getChildAt(l1), l);
            l1++;
        }
        if(true) goto _L1; else goto _L4
_L4:
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        boolean flag = false;
        if(mAnimationState == 2 || mAnimationState == 4)
        {
            float f = (float)(int)(SystemClock.uptimeMillis() - mAnimationStartTime) / (float)mAnimationDuration;
            float f1;
            float f2;
            Object obj;
            int j1;
            View view1;
            float f4;
            int k1;
            int l1;
            if(mAnimationState == 2)
                f1 = f;
            else
                f1 = 1.0F - f;
            f2 = Math.max(0.0F, Math.min(1.0F, f1));
            mScreenBgDrawable2.setAlpha((int)(255F * f2));
            mScreenBgDrawable2.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            mScreenBgDrawable2.draw(canvas);
        } else
        if(mAnimationState == 8)
            flag = false;
        else
            flag = drawChild(canvas, view, l);
        obj = view.getTag();
        if(obj != null && mLauncher != null && (obj instanceof Integer))
        {
            j1 = ((Integer)obj).intValue();
            view1 = mMenuManager.getChildAt(j1);
            if(view1 != null)
            {
                canvas.save();
                float f3 = (float)(view.getHeight() - view.getPaddingLeft() - view.getPaddingRight()) / (float)view1.getHeight();
                f4 = (float)(view.getWidth() - view.getPaddingTop() - view.getPaddingBottom()) / (float)view1.getWidth();
                float f5;
                if(f3 > f4)
                    f5 = f4;
                else
                    f5 = f3;
                mTmpRect.left = view.getPaddingLeft();
                mTmpRect.top = view.getPaddingTop();
                mTmpRect.right = view.getWidth() - view.getPaddingRight();
                mTmpRect.bottom = view.getHeight() - view.getPaddingBottom();
                Gravity.apply(17, (int)(f5 * (float)view1.getWidth()), (int)(f5 * (float)view1.getHeight()), mTmpRect, mTmpRect);
                k1 = mTmpRect.left;
                l1 = mTmpRect.top;
                canvas.translate(k1 + view.getLeft(), l1 + view.getTop());
                canvas.scale(f5, f5);
                view1.draw(canvas);
                canvas.restore();
            }
        } else
        {
            int i = mAddDrawable.getIntrinsicWidth();
            int j = mAddDrawable.getIntrinsicHeight();
            int k = view.getLeft() + (view.getWidth() - i) / 2;
            int i1 = view.getTop() + (view.getHeight() - j) / 2;
            mAddDrawable.setBounds(k, i1, k + i, i1 + j);
            mAddDrawable.draw(canvas);
        }
        if(mTouchedView == view)
        {
            mPressedDrawable.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            mPressedDrawable.draw(canvas);
        }
        return flag;
    }

    public void drawCloseAnimation()
    {
        if(mAnimationState == 7)
        {
            mAnimationState = 3;
            mAnimationDuration = 300;
        }
    }

    public void drawOpenAnimation()
    {
        mAnimationState = 1;
        mAnimationDuration = 400;
    }

    void enableChildrenCache()
    {
        MenuManager menumanager = (MenuManager)mMenuManager;
        int i = menumanager.getChildCount();
        for(int j = 0; j < i; j++)
        {
            AppMenu appmenu = (AppMenu)menumanager.getChildAt(j);
            appmenu.setChildrenDrawnWithCacheEnabled(true);
            appmenu.setChildrenDrawingCacheEnabled(true);
        }

    }

    public int getCurrentPage()
    {
        return mCurrentPage;
    }

    void initScreen(int i)
    {
        removeAllViews();
        setBackgroundResource(0);
        Resources resources = getResources();
        mChildAnimate = new Animate[i];
        mChildGapV = resources.getDimensionPixelSize(0x7f090023);
        mChildGapH = resources.getDimensionPixelSize(0x7f090024);
        mChildLeft = resources.getDimensionPixelSize(0x7f090025);
        mChildTop = resources.getDimensionPixelSize(0x7f090026);
        Context context = getContext();
        Resources resources1 = context.getResources();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        mScreenBgDrawable = resources1.getDrawable(0x7f020137);
        mScreenBgDrawable.getPadding(mScreenBgDrawablePadding);
        mScreenBgDrawable2 = resources1.getDrawable(0x7f020137).mutate();
        mPressedDrawable = resources1.getDrawable(0x7f020063);
        for(int j = 0; j < i; j++)
        {
            makeScreen(j);
            mChildAnimate[j] = new Animate();
        }

        getGlobalVisibleRect(mScreenRect);
    }

    boolean isOpened()
    {
        boolean flag;
        if(getVisibility() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void onClick(View view)
    {
        if(view.getTag() != null)
        {
            mCurrentPage = ((Integer)view.getTag()).intValue();
            drawCloseAnimation();
            postInvalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        int i;
        float f;
        float f1;
        i = motionevent.getAction();
        f = motionevent.getX();
        f1 = motionevent.getY();
        if(mAnimationState == 7 || mAnimationState == 8) goto _L2; else goto _L1
_L1:
        boolean flag = true;
_L8:
        return flag;
_L2:
        i;
        JVM INSTR lookupswitch 5: default 92
    //                   0: 307
    //                   1: 346
    //                   2: 105
    //                   3: 346
    //                   261: 381;
           goto _L3 _L4 _L5 _L6 _L5 _L7
_L3:
        break; /* Loop/switch isn't completed */
_L7:
        break MISSING_BLOCK_LABEL_381;
_L10:
        int i2;
        int j2;
        int k2;
        int l2;
        if(mTouchState != 0)
            flag = true;
        else
            flag = false;
          goto _L8
_L6:
        if(!mMultiTouchUsed || !mPinchOutProcess)
            break MISSING_BLOCK_LABEL_234;
        mTouchState = 0;
        i2 = (int)motionevent.getY(0);
        j2 = (int)motionevent.getX(0);
        k2 = (int)motionevent.getY(1);
        l2 = (int)motionevent.getX(1);
        mMovePinch = (int)Math.sqrt((i2 - k2) * (i2 - k2) + (j2 - l2) * (j2 - l2)) - mMovePinchStart;
        if(mMovePinch <= 100) goto _L10; else goto _L9
_L9:
        mMultiTouchUsed = false;
        mPinchOutProcess = false;
        mCurrentPage = mPinchOutIndex;
        drawCloseAnimation();
        postInvalidate();
        flag = true;
          goto _L8
        int k1 = (int)(mLastMotionY - f1);
        int l1 = mTouchSlop;
        boolean flag1;
        if(Math.abs(k1) > l1)
            flag1 = true;
        else
            flag1 = false;
        if(mTouchState == 0 && flag1)
        {
            mTouchState = 1;
            scroll(0, k1);
            mLastMotionX = f;
            mLastMotionY = f1;
        }
          goto _L10
_L4:
        mLastMotionX = f;
        mLastMotionY = f1;
        int j1;
        if(!mScroller.isFinished())
            j1 = 1;
        else
            j1 = 0;
        mTouchState = j1;
          goto _L10
_L5:
        if(mMultiTouchUsed)
        {
            mMultiTouchUsed = false;
            mPinchOutProcess = false;
        } else
        if(mTouchState == 2)
            endDrag();
          goto _L10
        if(mTouchState == 0 && !mMultiTouchUsed)
        {
            int j = (int)motionevent.getY(0);
            int k = (int)motionevent.getX(0);
            int l = (int)motionevent.getY(1);
            int i1 = (int)motionevent.getX(1);
            if(getTouchedIndex(k, j) == getTouchedIndex(i1, l))
            {
                mMultiTouchUsed = true;
                mPinchOutProcess = true;
                mMovePinchStart = (int)Math.sqrt((j - l) * (j - l) + (k - i1) * (k - i1));
                mPinchOutIndex = getTouchedIndex(k, j);
            } else
            {
                mMultiTouchUsed = false;
            }
        }
          goto _L10
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = mChildLeft;
        int j1 = mChildTop;
        int k1 = mChildGapV;
        int l1 = mChildGapH;
        int i2 = getChildCount();
        if(i2 >= 1)
        {
            if(mChildRects == null || mChildRects.length != i2)
                mChildRects = new Rect[i2];
            int j2 = 0;
            while(j2 < i2) 
            {
                View view = getChildAt(j2);
                Rect rect = new Rect();
                if(view.getVisibility() != 8)
                {
                    int k2 = view.getMeasuredWidth();
                    int l2 = view.getMeasuredHeight();
                    if(i1 + k2 > getMeasuredWidth())
                    {
                        j1 += k1 + l2;
                        i1 = mChildLeft;
                    }
                    if(mChildRects[j2] == null)
                        mChildRects[j2] = new Rect();
                    mChildRects[j2].set(i1, j1, i1 + k2, j1 + l2);
                    view.getHitRect(rect);
                    if(!rect.isEmpty() && mEnabledChildAnimation)
                    {
                        if(view.getVisibility() == 0)
                            mChildAnimate[j2].start(view, mChildRects[j2]);
                    } else
                    {
                        view.layout(i1, j1, i1 + k2, j1 + l2);
                    }
                    i1 += l1 + k2;
                    if(j2 == i2 - 3)
                        mMaxDeltaY = j1 - mChildTop;
                }
                j2++;
            }
        }
    }

    public boolean onLongClick(View view)
    {
        if(!mMultiTouchUsed && (view.getTag() != null && getChildCount() > 1))
            startDrag(view);
        return false;
    }

    protected void onMeasure(int i, int j)
    {
        onMeasure(i, j);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        int i;
        float f;
        float f1;
        i = motionevent.getAction();
        f = motionevent.getX();
        f1 = motionevent.getY();
        if(mAnimationState == 7 || mAnimationState == 8) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L8:
        return flag;
_L2:
        if(mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(motionevent);
        i;
        JVM INSTR tableswitch 0 3: default 92
    //                   0 98
    //                   1 540
    //                   2 170
    //                   3 540;
           goto _L3 _L4 _L5 _L6 _L5
_L5:
        break MISSING_BLOCK_LABEL_540;
_L3:
        break; /* Loop/switch isn't completed */
_L4:
        break; /* Loop/switch isn't completed */
_L9:
        flag = true;
        if(true) goto _L8; else goto _L7
_L7:
        if(!mScroller.isFinished())
        {
            mScroller.abortAnimation();
            computeScroll();
        }
        if(f1 < 80F || f1 > (float)(getHeight() - 80))
        {
            mScrollState = 1;
            postDelayed(mScrollRunnable, 600L);
        } else
        {
            mScrollState = 0;
        }
          goto _L9
_L6:
        int k = (int)(mLastMotionX - f);
        int l = (int)(mLastMotionY - f1);
        int i1 = mTouchSlop;
        boolean flag1;
        if(Math.abs(l) > i1)
            flag1 = true;
        else
            flag1 = false;
        if(flag1 && mTouchState == 0)
            mTouchState = 1;
        if(mTouchState == 1)
        {
            scroll(0, l);
            mLastMotionX = f;
            mLastMotionY = f1;
        } else
        if(mTouchState == 2)
        {
            View view = mDraggingView;
            if(view != null)
            {
                Rect rect = mTmpRect;
                view.getHitRect(rect);
                rect.left = rect.left - k;
                rect.top = rect.top - l;
                rect.right = rect.right - k;
                rect.bottom = rect.bottom - l;
                view.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
            mLastMotionX = f;
            mLastMotionY = f1;
            View view1 = mDraggingView;
            int j1 = getTouchedIndex((int)f, (int)f1);
            if(j1 != -1 && j1 != mDraggingIndex && getChildAt(j1).getTag() != null)
            {
                removeView(view1);
                addView(view1, j1);
                view1.setVisibility(4);
                mDraggingIndex = j1;
            }
            if(f1 < 80F)
            {
                if(mScrollState == 0)
                {
                    mScrollState = 1;
                    mScrollRunnable.setDirection(0);
                    postDelayed(mScrollRunnable, 0L);
                }
            } else
            if(f1 > (float)(getHeight() - 80) && mScrollState == 0)
            {
                mScrollState = 1;
                mScrollRunnable.setDirection(1);
                postDelayed(mScrollRunnable, 0L);
            }
        }
          goto _L9
        if(mTouchState == 1)
        {
            VelocityTracker velocitytracker = mVelocityTracker;
            velocitytracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int j = (int)velocitytracker.getYVelocity();
            if(Math.abs(j) > mMinimumVelocity && getChildCount() > 0)
                fling(-j);
            if(mVelocityTracker != null)
            {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            mTouchState = 0;
        } else
        if(mTouchState == 2)
            drop((int)f, (int)f1);
        if(mDraggingView != null || mTouchedView != null)
            postInvalidate();
        endDrag();
          goto _L9
    }

    void open()
    {
        mEnabledChildAnimation = true;
        enableChildrenCache();
        setVisibility(0);
        mCurrentPage = ((MenuManager)mMenuManager).getCurrentScreen();
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
        mMenuManager = mLauncher.getMenuManager();
    }

    public final void smoothScrollBy(int i, int j)
    {
        if(AnimationUtils.currentAnimationTimeMillis() - mLastScroll > 250L)
        {
            mScroller.startScroll(mScrollX, mScrollY, i, j);
            invalidate();
        } else
        {
            if(!mScroller.isFinished())
                mScroller.abortAnimation();
            scrollBy(i, j);
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis();
    }

    private Drawable mAddDrawable;
    private TFAnimateEngine mAniEngine;
    private int mAnimationDuration;
    private long mAnimationStartTime;
    private int mAnimationState;
    private int mAnimationStyle;
    private Animate mChildAnimate[];
    private int mChildGapH;
    private int mChildGapV;
    private int mChildLeft;
    private Rect mChildRects[];
    private int mChildTop;
    private int mCurrentPage;
    private Rect mCurrentRects[];
    private int mDeleteIndex;
    private View mDeleteView;
    private int mDraggingIndex;
    private View mDraggingView;
    private boolean mEnabledChildAnimation;
    private Rect mFinishRects[];
    private boolean mIsDBUpdate;
    private int mItemNumOfPage;
    private float mLastMotionX;
    private float mLastMotionY;
    private long mLastScroll;
    private Launcher mLauncher;
    private int mMaxDeltaY;
    private int mMaximumVelocity;
    private ViewGroup mMenuManager;
    private int mMinimumVelocity;
    private int mMovePinch;
    private int mMovePinchStart;
    private boolean mMultiTouchUsed;
    private int mOrientation;
    private int mOriginDragIndex;
    private final int mPanelSizeLong;
    private final int mPanelSizeLong2;
    private final int mPanelSizeShort;
    private int mPinchIndex;
    private int mPinchOutIndex;
    private boolean mPinchOutProcess;
    private Drawable mPressedDrawable;
    private Drawable mScreenBgDrawable;
    private Drawable mScreenBgDrawable2;
    private Rect mScreenBgDrawablePadding;
    private Rect mScreenRect;
    private ScrollRunnable mScrollRunnable;
    private int mScrollState;
    private int mScrollTop;
    private Scroller mScroller;
    private Rect mStartRects[];
    private int mTmpLocation[];
    private Rect mTmpRect;
    private int mTouchSlop;
    private int mTouchState;
    private View mTouchedView;
    private boolean mUseFullScreenInLandScapeMode;
    private VelocityTracker mVelocityTracker;








/*
    static int access$602(QuickViewMainMenu quickviewmainmenu, int i)
    {
        quickviewmainmenu.mScrollState = i;
        return i;
    }

*/
}
