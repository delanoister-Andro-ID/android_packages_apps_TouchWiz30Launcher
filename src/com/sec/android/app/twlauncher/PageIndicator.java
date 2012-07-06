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
import android.os.SystemClock;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Launcher

public class PageIndicator
{
    public class Page
    {

        boolean draw(Canvas canvas, int i)
        {
            if(mAnimationState == 1)
            {
                mAnimationStartTime = SystemClock.uptimeMillis();
                mAnimationState = 2;
            }
            Paint paint = mTextPaint;
            float f = 0.0F;
            int j;
            int k;
            int l;
            int i1;
            int j1;
            int k1;
            int l1;
            int i2;
            int j2;
            int k2;
            int l2;
            String s;
            Rect rect;
            int i3;
            int j3;
            int k3;
            byte byte0;
            int l3;
            int i4;
            Bitmap bitmap;
            Canvas canvas1;
            boolean flag;
            if(mAnimationState == 2)
            {
                float f1 = (float)(SystemClock.uptimeMillis() - mAnimationStartTime) / (float)mAnimationDuration;
                if(f1 >= 1.0F)
                    mAnimationState = 3;
                float f2 = Math.min(f1, 1.0F);
                f = mAnimationFrom + f2 * (mAnimationTo - mAnimationFrom);
            } else
            if(mAnimationState == 3)
                if(Launcher.USE_MAINMENU_ICONMODE)
                    f = PageIndicator.rate_iconmenu[mDrawState];
                else
                    f = PageIndicator.rate[mDrawState];
            j = mPageDrawable.getIntrinsicWidth();
            k = mPageDrawable.getIntrinsicHeight();
            if(mPageDrawableSmall == null)
                l = 0;
            else
                l = mPageDrawableSmall.getIntrinsicWidth();
            if(mPageDrawableSmall == null)
                i1 = 0;
            else
                i1 = mPageDrawableSmall.getIntrinsicHeight();
            mPageDrawable.getPadding(mTempRect);
            j1 = mTempRect.left;
            k1 = mTempRect.top;
            l1 = mTempRect.bottom;
            i2 = mTempRect.right;
            j2 = j - j1 - i2;
            k2 = k - k1 - l1;
            l2 = i + 1;
            s = Integer.toString(l2);
            rect = mTempRect2;
            paint.getTextBounds(s, 0, s.length(), rect);
            i3 = rect.width();
            j3 = rect.height();
            k3 = 0;
            byte0 = 2;
            if(l2 == 1 || l2 == 4 || l2 > 9)
                byte0 = 1;
            if(l2 == 11)
                byte0 = 0;
            if(i3 < j2)
                i3 = j2;
            if(j3 < k2)
            {
                k3 = (k2 - j3) / 2;
                j3 = k2;
            }
            l3 = i2 + (i3 + j1);
            i4 = l1 + (j3 + k1);
            bitmap = mBitmap;
            bitmap.eraseColor(0);
            canvas1 = mCanvas;
            if(mAnimationState == 2)
            {
                mPageDrawable.setBounds(0, 0, l3, i4);
                mPageDrawable.draw(canvas1);
                canvas1.drawText(s, (((l3 - i3) / 2 + (i3 - (rect.right - rect.left)) / 2) - rect.left) + PageIndicator.center_padding[byte0], (j3 + (i4 - j3) / 2) - k3, paint);
            } else
            if(mAnimationState == 3)
            {
                if(mDrawState <= 1)
                {
                    if(l > 0 && i1 > 0)
                    {
                        mPageDrawableSmall.setBounds(0, 0, l, i1);
                        mPageDrawableSmall.draw(canvas1);
                    } else
                    {
                        mPageDrawable.setBounds(0, 0, j, k);
                        mPageDrawable.draw(canvas1);
                    }
                } else
                {
                    mPageDrawable.setBounds(0, 0, l3, i4);
                    mPageDrawable.draw(canvas1);
                }
                if(f >= PageIndicator.rate[2])
                    canvas1.drawText(s, (((l3 - i3) / 2 + (i3 - (rect.right - rect.left)) / 2) - rect.left) + PageIndicator.center_padding[byte0], (j3 + (i4 - j3) / 2) - k3, paint);
            }
            if(mAnimationState == 2)
            {
                canvas.save();
                canvas.translate(((float)j * (1.0F - f)) / 2.0F, ((float)j * (1.0F - f)) / 2.0F);
                canvas.scale(f, f);
                if(!Launcher.USE_MAINMENU_ICONMODE)
                {
                    canvas.translate((j - l3) / 2, (k - i4) / 2);
                    canvas.drawBitmap(bitmap, 0.0F, 0.0F, new Paint());
                }
                canvas.restore();
            } else
            if(mAnimationState == 3)
            {
                canvas.save();
                if(l == 0 || i1 == 0)
                {
                    canvas.translate(((float)j * (1.0F - f)) / 2.0F, ((float)j * (1.0F - f)) / 2.0F);
                    canvas.scale(f, f);
                }
                if(!Launcher.USE_MAINMENU_ICONMODE)
                {
                    if(f > PageIndicator.rate[1])
                        canvas.translate((j - l3) / 2, (k - i4) / 2);
                    if(mDrawState > 0)
                        canvas.drawBitmap(bitmap, 0.0F, 0.0F, mPaint);
                }
                canvas.restore();
            }
            if(mAnimationState != 3)
                flag = true;
            else
                flag = false;
            return flag;
        }

        void initDrawState()
        {
            mPrevDrawState = 0;
            mDrawState = 0;
        }

        void setDrawState(int i)
        {
            mPrevDrawState = mDrawState;
            mDrawState = i;
            if(mDrawState != mPrevDrawState)
            {
                mAnimationState = 1;
                if(Launcher.USE_MAINMENU_ICONMODE)
                {
                    mAnimationTo = PageIndicator.rate_iconmenu[mDrawState];
                    mAnimationFrom = PageIndicator.rate_iconmenu[mPrevDrawState];
                } else
                {
                    mAnimationTo = PageIndicator.rate[mDrawState];
                    mAnimationFrom = PageIndicator.rate[mPrevDrawState];
                }
            }
        }

        int mAnimationDuration;
        float mAnimationFrom;
        long mAnimationStartTime;
        int mAnimationState;
        float mAnimationTo;
        int mDrawState;
        int mPrevDrawState;
        private final Rect mTempRect = new Rect();
        private final Rect mTempRect2 = new Rect();
        final PageIndicator this$0;

        public Page()
        {
            this$0 = PageIndicator.this;
            super();
            mPrevDrawState = 1;
            mDrawState = 1;
            mAnimationState = 3;
            mAnimationDuration = 200;
        }
    }


    public PageIndicator()
    {
        mCurrentPage = -1;
        mTextPaint = new Paint();
        mPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(0xff000000);
        mTextPaint.setTypeface(Typeface.createFromFile("/system/fonts/DroidSans-Bold.ttf"));
        mIsDraw = true;
        mEnableTouch = true;
    }

    boolean draw(Canvas canvas)
    {
        if(mPageDrawable != null && mIsDraw) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L4:
        return flag;
_L2:
        boolean flag1 = false;
        int i = mTotalPageCount;
        int j = mGap;
        int k = mMoreGap;
        canvas.save();
        if(Launcher.USE_MAINMENU_ICONMODE && mOrientation == 1)
        {
            canvas.translate(mScrollX, mTop);
            if(mIsDraw && mBackgroundDrawable != null)
                mBackgroundDrawable.draw(canvas);
            canvas.translate(mLeft - mScrollX, mOffsetY);
        } else
        {
            canvas.translate(mLeft, mTop);
        }
        if(mIsVisibleLeftMore && mMoreDrawable != null)
        {
            int i1 = k + mMoreDrawable.getIntrinsicWidth();
            canvas.translate(-i1, 0.0F);
            mMoreDrawable.draw(canvas);
            canvas.translate(i1, 0.0F);
        }
        for(int l = 0; l < i; l++)
        {
            if(mFirstTextIndex != 0 && l == 0 || mFirstTextIndex == 0 && l == i - 1 && mCurrentTextIndex != 0)
                mPage[l].mAnimationState = 3;
            if(mPage[l].draw(canvas, l + mFirstTextIndex))
                flag1 = true;
            canvas.translate(j + mPageDrawable.getIntrinsicWidth(), 0.0F);
        }

        if(mIsVisibleRightMore && mMoreDrawable != null)
        {
            canvas.translate(k - j, 0.0F);
            mMoreDrawable.draw(canvas);
        }
        canvas.restore();
        if(!flag1 && mIsHiding)
        {
            mIsHiding = false;
            mIsDraw = false;
            if(mDrawLastFrame)
            {
                mDrawLastFrame = false;
                flag = true;
                continue; /* Loop/switch isn't completed */
            }
        }
        flag = flag1;
        if(true) goto _L4; else goto _L3
_L3:
    }

    void enableLeftMore(boolean flag)
    {
        mIsVisibleLeftMore = flag;
    }

    void enableRightMore(boolean flag)
    {
        mIsVisibleRightMore = flag;
    }

    public void enableShowHide(boolean flag)
    {
        mEnableShowHide = flag;
        boolean flag1;
        boolean flag2;
        if(!flag)
            flag1 = true;
        else
            flag1 = false;
        mIsDraw = flag1;
        if(!flag)
            flag2 = true;
        else
            flag2 = false;
        mEnableTouch = flag2;
    }

    int getPageTouchArea(int i, int j)
    {
        if(mPageDrawable != null && mEnableTouch) goto _L2; else goto _L1
_L1:
        int k = -1;
_L4:
        return k;
_L2:
        int l = mTotalPageCount;
        int i1 = mGap;
        int j1 = mMoreGap;
        int k1 = mLeft;
        int l1 = i1 / 2;
        int i2 = mPageDrawable.getIntrinsicWidth();
        int j2 = mPageDrawable.getIntrinsicHeight();
        Rect rect = new Rect();
        rect.top = 0;
        rect.bottom = 8 + (j2 + mTop);
        if(mIsVisibleLeftMore)
        {
            rect.left = k1 - mMoreDrawable.getIntrinsicWidth() - j1 - l1;
            rect.right = k1;
            if(rect.contains(i, j))
            {
                k = -2;
                continue; /* Loop/switch isn't completed */
            }
        }
        if(mIsVisibleRightMore)
        {
            rect.left = k1 + i2 * 9 + i1 * 7;
            rect.right = l1 + (rect.left + mMoreDrawable.getIntrinsicWidth());
            if(rect.contains(i, j))
            {
                k = -3;
                continue; /* Loop/switch isn't completed */
            }
        }
        int k2 = 0;
        do
        {
            if(k2 >= l)
                break;
            int l2 = k1 + i2 * k2;
            int i3;
            if(k2 > 0)
                i3 = k2 - 1;
            else
                i3 = 0;
            rect.left = (l2 + i3 * i1) - l1;
            rect.right = i2 + rect.left + l1 * 2;
            if(rect.contains(i, j))
            {
                k = k2 + mFirstTextIndex;
                continue; /* Loop/switch isn't completed */
            }
            k2++;
        } while(true);
        k = -1;
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void hide()
    {
        if(mEnableShowHide)
        {
            int i = mTotalPageCount;
            for(int j = 0; j < i; j++)
                mPage[j].setDrawState(0);

            mIsHiding = true;
        }
    }

    void setCurrentPage(int i, boolean flag)
    {
        mCurrentPage = i;
        if(!mIsHiding)
        {
            int j = mTotalPageCount;
            int k = 0;
            while(k < j) 
            {
                if(k == i)
                {
                    if(flag)
                        mPage[k].setDrawState(3);
                    else
                    if(k + mFirstTextIndex < 9)
                        mPage[k].setDrawState(2);
                    else
                        mPage[k].setDrawState(3);
                } else
                if(k == i - 1 || k == i + 1)
                    mPage[k].setDrawState(1);
                else
                    mPage[k].setDrawState(1);
                k++;
            }
        }
    }

    void setFirstTextNum(int i)
    {
        mCurrentTextIndex = mFirstTextIndex;
        mFirstTextIndex = i;
    }

    void setGap(int i)
    {
        setGap(i, i);
    }

    void setGap(int i, int j)
    {
        mGap = i;
        mMoreGap = j;
    }

    public void setMoreDrawable(Drawable drawable)
    {
        mMoreDrawable = drawable;
    }

    public void setMoreDrawableDim(Drawable adrawable[])
    {
        mMoreDrawableDim = adrawable;
    }

    void setOffset(int i, int j)
    {
        mTop = j;
        mLeft = i;
    }

    void setOffsetY(int i)
    {
        mOffsetY = i;
    }

    void setOrientation(int i)
    {
        mOrientation = i;
    }

    void setPageCount(int i)
    {
        if(!Launcher.USE_MAINMENU_ICONMODE) goto _L2; else goto _L1
_L1:
        if(i > 11)
            i = 11;
_L4:
        if(mTotalPageCount != i)
        {
            mPage = new Page[i];
            for(int j = 0; j < i; j++)
                mPage[j] = new Page();

        }
        break; /* Loop/switch isn't completed */
_L2:
        if(i > 9)
            i = 9;
        if(true) goto _L4; else goto _L3
_L3:
        mTotalPageCount = i;
        return;
    }

    public void setPageDrawable(Drawable drawable)
    {
        mPageDrawable = drawable;
        if(mBitmap != null)
            mBitmap.recycle();
        mBitmap = Bitmap.createBitmap(3 * drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), android.graphics.Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setPageDrawableSmall(Drawable drawable)
    {
        mPageDrawableSmall = drawable;
    }

    void setScrollX(int i)
    {
        mScrollX = i;
    }

    void setTextSize(int i)
    {
        mTextSize = i;
        mTextPaint.setTextSize(mTextSize);
    }

    public void show()
    {
        show(false);
    }

    public void show(boolean flag)
    {
        if(mEnableShowHide && (!mIsDraw || flag))
        {
            boolean flag1 = mIsDraw;
            mIsDraw = true;
            mIsHiding = false;
            int i = mTotalPageCount;
            int j = 0;
            while(j < i && !flag1) 
            {
                mPage[j].initDrawState();
                j++;
            }
        }
    }

    private static final int center_padding[];
    private static final float rate[];
    private static final float rate_iconmenu[];
    Drawable mBackgroundDrawable;
    Bitmap mBitmap;
    Canvas mCanvas;
    int mCurrentPage;
    int mCurrentTextIndex;
    private boolean mDrawLastFrame;
    boolean mEnableShowHide;
    boolean mEnableTouch;
    int mFirstTextIndex;
    int mGap;
    boolean mIsDraw;
    boolean mIsHiding;
    boolean mIsVisibleLeftMore;
    boolean mIsVisibleRightMore;
    int mLeft;
    Drawable mMoreDrawable;
    Drawable mMoreDrawableDim[];
    int mMoreGap;
    int mOffsetY;
    int mOrientation;
    Page mPage[];
    Drawable mPageDrawable;
    Drawable mPageDrawableSmall;
    Paint mPaint;
    int mScrollX;
    Paint mTextPaint;
    int mTextSize;
    int mTop;
    int mTotalPageCount;

    static 
    {
        float af[] = new float[4];
        af[0] = 0.0F;
        af[1] = 0.38F;
        af[2] = 1.0F;
        af[3] = 1.0F;
        rate = af;
        float af1[] = new float[4];
        af1[0] = 0.0F;
        af1[1] = 0.6F;
        af1[2] = 1.0F;
        af1[3] = 1.0F;
        rate_iconmenu = af1;
        int ai[] = new int[3];
        ai[0] = -1;
        ai[1] = 0;
        ai[2] = 1;
        center_padding = ai;
    }



}
