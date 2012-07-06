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
import android.os.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragController, DragAnimation, DropTarget, DragSource, 
//            MenuManager, Launcher, Workspace, LauncherAppWidgetInfo, 
//            SamsungAppWidgetInfo, DragScroller

public class DragLayer extends FrameLayout
    implements DragController
{
    private class ScrollRunnable
        implements Runnable
    {

        public void run()
        {
            if(mDragScroller != null)
            {
                if(mMenuListener != null)
                {
                    if(mDirection == 0)
                        mDragMenuScroller.scrollLeft();
                    else
                        mDragMenuScroller.scrollRight();
                } else
                if(mDirection == 0)
                    mDragScroller.scrollLeft();
                else
                    mDragScroller.scrollRight();
                mScrollState = 0;
            }
        }

        void setDirection(int i)
        {
            mDirection = i;
        }

        private int mDirection;
        final DragLayer this$0;

        ScrollRunnable()
        {
            this$0 = DragLayer.this;
            super();
        }
    }


    public DragLayer(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mDragging = false;
        mDragBitmap = null;
        mDragRect = new Rect();
        mScrollState = 0;
        mScrollRunnable = new ScrollRunnable();
        mAnimationState = 3;
        mSrcColor1 = context.getResources().getColor(0x7f070003);
        mSrcColor2 = context.getResources().getColor(0x7f070005);
        mTrashPaint.setColorFilter(new PorterDuffColorFilter(mSrcColor1, android.graphics.PorterDuff.Mode.SRC_ATOP));
        int i = context.getResources().getColor(0x7f070008);
        Paint paint = new Paint();
        paint.setColor(i);
        paint.setStrokeWidth(3F);
        paint.setAntiAlias(true);
        mDimPaint.setColorFilter(new PorterDuffColorFilter(0x7f000000, android.graphics.PorterDuff.Mode.SRC_ATOP));
        mScrollPaintColor = context.getResources().getColor(0x7f070006);
        mScrollPaint.setColorFilter(new PorterDuffColorFilter(mScrollPaintColor, android.graphics.PorterDuff.Mode.SRC_ATOP));
        mDragAnimation = new DragAnimation[3];
        int j = context.getResources().getDimensionPixelSize(0x7f090031);
        if(j > 0)
            SCROLL_ZONE = j;
    }

    private boolean drop(float f, float f1)
    {
        invalidate();
        int ai[] = mDropCoordinates;
        DropTarget droptarget = findDropTarget((int)f, (int)f1, ai);
        boolean flag;
        if(droptarget != null)
        {
            droptarget.onDragExit(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
            if(droptarget.acceptDrop(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo))
            {
                droptarget.onDrop(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
                mDragSource.onDropCompleted((View)droptarget, true, mDragInfo);
                flag = true;
            } else
            {
                mDragSource.onDropCompleted((View)droptarget, false, mDragInfo);
                flag = true;
            }
        } else
        {
            mDragSource.onDropCompleted((View)droptarget, false, mDragInfo);
            flag = false;
        }
        return flag;
    }

    private void endDrag()
    {
        if(mDragging)
        {
            mDragging = false;
            if(mDragBitmap != null)
                mDragBitmap.recycle();
            if(mMenuListener == null && mOriginator != null)
                mOriginator.setVisibility(0);
            if(mListener != null)
                mListener.onDragEnd();
        }
    }

    private DropTarget findDropTarget(ViewGroup viewgroup, int i, int j, int ai[])
    {
        Rect rect;
        int l;
        int i1;
        View view;
        int j1;
        rect = mDragRect;
        int k = viewgroup.getChildCount();
        l = i + viewgroup.getScrollX();
        i1 = j + viewgroup.getScrollY();
        view = mIgnoredDropTarget;
        if(mMenuListener != null)
            view = null;
        j1 = k - 1;
_L3:
        if(j1 < 0) goto _L2; else goto _L1
_L1:
        DropTarget droptarget;
        View view1 = viewgroup.getChildAt(j1);
        if(view1.getVisibility() != 0 || view1 == view)
            continue; /* Loop/switch isn't completed */
        view1.getHitRect(rect);
        if(!rect.contains(l, i1))
            continue; /* Loop/switch isn't completed */
        DropTarget droptarget1 = null;
        if(view1 instanceof ViewGroup)
        {
            i = l - view1.getLeft();
            j = i1 - view1.getTop();
            droptarget1 = findDropTarget((ViewGroup)view1, i, j, ai);
        }
        if(droptarget1 == null)
        {
            if(!(view1 instanceof DropTarget))
                continue; /* Loop/switch isn't completed */
            DropTarget droptarget2 = (DropTarget)view1;
            DragSource dragsource = mDragSource;
            Object obj = mDragInfo;
            if(droptarget2.acceptDrop(dragsource, i, j, 0, 0, obj))
            {
                ai[0] = i;
                ai[1] = j;
                droptarget = (DropTarget)view1;
            } else
            {
                droptarget = null;
            }
        } else
        {
            droptarget = droptarget1;
        }
_L4:
        return droptarget;
        j1--;
          goto _L3
_L2:
        droptarget = null;
          goto _L4
    }

    public boolean calcScrollZone(float f, float f1)
    {
        boolean flag;
        if(SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
        {
            if(mMenuManager.isOpened())
            {
                if(f > (float)(getWidth() - L_SCROLL_ZONE) && f < (float)(getWidth() - R_SCROLL_ZONE))
                    flag = true;
                else
                    flag = false;
            } else
            if(f > (float)(getWidth() - SCROLL_ZONE))
                flag = true;
            else
                flag = false;
        } else
        if(!SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510") && f > (float)(getWidth() - SCROLL_ZONE))
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void cancelDrag()
    {
        removeCallbacks(mScrollRunnable);
        if(mShouldDrop)
        {
            drop(mLastMotionX, mLastMotionY);
            mShouldDrop = false;
        }
        if(mDragging)
        {
            mDragging = false;
            if(mDragBitmap != null)
                mDragBitmap.recycle();
            if(mMenuListener == null && mOriginator != null)
                mOriginator.setVisibility(0);
            if(mListener != null)
                mListener.onDragEnd();
        }
    }

    protected void dispatchDraw(Canvas canvas)
    {
        System.currentTimeMillis();
        super.dispatchDraw(canvas);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            if(mWorkspace != null)
                mWorkspace.drawPageIndicatorExternal(canvas);
            if(mMenuManager != null)
                mMenuManager.drawPageIndicatorExternal(canvas);
        }
        if(!mDragging || mDragBitmap == null) goto _L2; else goto _L1
_L1:
        if(mAnimationState == 1)
        {
            mAnimationStartTime = SystemClock.uptimeMillis();
            mAnimationState = 2;
        }
        if(mAnimationState != 2) goto _L4; else goto _L3
_L3:
        float f2;
        float f = (float)(SystemClock.uptimeMillis() - mAnimationStartTime) / (float)mAnimationDuration;
        int i;
        DragAnimation draganimation;
        int k;
        float f1;
        float f3;
        if(f >= 1.0F)
            if(mAnimationType == 1)
                mAnimationState = 1;
            else
                mAnimationState = 3;
        f1 = Math.min(f, 1.0F);
        f2 = mAnimationFrom + f1 * (mAnimationTo - mAnimationFrom);
        mAnimationType;
        JVM INSTR tableswitch 1 1: default 172
    //                   1 325;
           goto _L5 _L6
_L5:
        if(mAnimationState != 3)
        {
            if(mAnimationState == 1)
            {
                f3 = mAnimationTo;
                mAnimationTo = 1.0F;
                mAnimationFrom = f3;
            }
            postInvalidate();
        }
        break; /* Loop/switch isn't completed */
_L6:
        Bitmap bitmap = mDragBitmap;
        canvas.save();
        canvas.translate(((float)mScrollX + mLastMotionX) - mTouchOffsetX - (float)mBitmapOffsetX, ((float)mScrollY + mLastMotionY) - mTouchOffsetY - (float)mBitmapOffsetY);
        canvas.translate(((float)bitmap.getWidth() * (1.0F - f2)) / 2.0F, ((float)bitmap.getHeight() * (1.0F - f2)) / 2.0F);
        canvas.scale(f2, f2);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, mDragPaint);
        canvas.restore();
        continue; /* Loop/switch isn't completed */
_L4:
        canvas.drawBitmap(mDragBitmap, ((float)mScrollX + mLastMotionX) - mTouchOffsetX - (float)mBitmapOffsetX, ((float)mScrollY + mLastMotionY) - mTouchOffsetY - (float)mBitmapOffsetY, mDragPaint);
_L2:
        if(mIsDragAnimation)
        {
            boolean flag = false;
            i = mDragAnimation.length;
            int j = 0;
            while(j < i) 
            {
                draganimation = mDragAnimation[j];
                if(draganimation != null)
                {
                    k = 0;
                    if(mMenuListener != null)
                        k = mMenuListener.getDragAnimationXOffset(draganimation.mAnimationView);
                    canvas.save();
                    canvas.translate(draganimation.mXOffset - (float)k, 0.0F);
                    if(draganimation.draw(canvas))
                        flag = true;
                    else
                        mDragAnimation[j] = null;
                    canvas.restore();
                }
                j++;
            }
            if(flag)
                postInvalidate();
            else
                mIsDragAnimation = false;
        }
        return;
        if(true) goto _L5; else goto _L7
_L7:
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        boolean flag;
        if(mDragging || super.dispatchKeyEvent(keyevent))
            flag = true;
        else
            flag = false;
        return flag;
    }

    DropTarget findDropTarget(int i, int j, int ai[])
    {
        return findDropTarget(((ViewGroup) (this)), i, j, ai);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        int i;
        float f;
        float f1;
        i = motionevent.getAction();
        f = motionevent.getX();
        f1 = motionevent.getY();
        i;
        JVM INSTR tableswitch 0 3: default 48
    //                   0 53
    //                   1 72
    //                   2 48
    //                   3 72;
           goto _L1 _L2 _L3 _L1 _L3
_L1:
        return mDragging;
_L2:
        mLastMotionX = f;
        mLastMotionY = f1;
        mLastDropTarget = null;
        continue; /* Loop/switch isn't completed */
_L3:
        if(mShouldDrop && drop(f, f1))
            mShouldDrop = false;
        endDrag();
        if(true) goto _L1; else goto _L4
_L4:
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        if(mDragging) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L9:
        return flag;
_L2:
        int i;
        float f;
        float f1;
        i = motionevent.getAction();
        f = motionevent.getX();
        f1 = motionevent.getY();
        i;
        JVM INSTR tableswitch 0 3: default 60
    //                   0 66
    //                   1 779
    //                   2 128
    //                   3 815;
           goto _L3 _L4 _L5 _L6 _L7
_L7:
        break MISSING_BLOCK_LABEL_815;
_L3:
        break; /* Loop/switch isn't completed */
_L4:
        break; /* Loop/switch isn't completed */
_L10:
        flag = true;
        if(true) goto _L9; else goto _L8
_L8:
        mLastMotionX = f;
        mLastMotionY = f1;
        if(f < (float)SCROLL_ZONE || f > (float)(getWidth() - SCROLL_ZONE))
        {
            mScrollState = 1;
            postDelayed(mScrollRunnable, 600L);
        } else
        {
            mScrollState = 0;
        }
          goto _L10
_L6:
        int j = mScrollX;
        int k = mScrollY;
        float f2 = mTouchOffsetX;
        float f3 = mTouchOffsetY;
        int l = mBitmapOffsetX;
        int i1 = mBitmapOffsetY;
        int j1 = (int)(((float)j + mLastMotionX) - f2 - (float)l);
        int k1 = (int)(((float)k + mLastMotionY) - f3 - (float)i1);
        Bitmap bitmap = mDragBitmap;
        int l1 = bitmap.getWidth();
        int i2 = bitmap.getHeight();
        Rect rect = mRect;
        rect.set(j1 - 1, k1 - 1, 1 + (j1 + l1), 1 + (k1 + i2));
        mLastMotionX = f;
        mLastMotionY = f1;
        int j2 = (int)((f + (float)j) - f2 - (float)l);
        int k2 = (int)((f1 + (float)k) - f3 - (float)i1);
        rect.union(j2 - 1, k2 - 1, 1 + (j2 + l1), 1 + (k2 + i2));
        int ai[] = mDropCoordinates;
        DropTarget droptarget = findDropTarget((int)f, (int)f1, ai);
        boolean flag1;
        if(droptarget != null)
        {
            if(mLastDropTarget == droptarget)
            {
                droptarget.onDragOver(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
            } else
            {
                if(mLastDropTarget != null)
                    mLastDropTarget.onDragExit(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
                droptarget.onDragEnter(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
            }
        } else
        if(mLastDropTarget != null)
            mLastDropTarget.onDragExit(mDragSource, ai[0], ai[1], (int)mTouchOffsetX, (int)mTouchOffsetY, mDragInfo);
        invalidate(rect);
        mLastDropTarget = droptarget;
        flag1 = false;
        if(mDragRegion != null)
        {
            boolean flag2 = mDragRegion.contains(motionevent.getRawX(), motionevent.getRawY());
            if(!mEnteredRegion && flag2)
            {
                mDragPaint = mTrashPaint;
                mEnteredRegion = true;
                flag1 = true;
            } else
            if(mEnteredRegion && !flag2)
            {
                mDragPaint = mDimPaint;
                mEnteredRegion = false;
            }
        }
        if(!mEnteredRegion)
            if(!flag1 && f < (float)SCROLL_ZONE)
            {
                if(mScrollState == 0)
                {
                    mScrollState = 1;
                    mScrollRunnable.setDirection(0);
                    postDelayed(mScrollRunnable, 600L);
                    mDragPaint = mScrollPaint;
                }
            } else
            if(!flag1 && calcScrollZone(f, f1))
            {
                if(mScrollState == 0)
                {
                    mScrollState = 1;
                    mScrollRunnable.setDirection(1);
                    postDelayed(mScrollRunnable, 600L);
                    mDragPaint = mScrollPaint;
                }
            } else
            if(mScrollState == 1)
            {
                mScrollState = 0;
                mScrollRunnable.setDirection(1);
                removeCallbacks(mScrollRunnable);
            } else
            if(!mEnteredRegion)
                mDragPaint = mDimPaint;
          goto _L10
_L5:
        removeCallbacks(mScrollRunnable);
        if(mShouldDrop)
        {
            drop(f, f1);
            mShouldDrop = false;
        }
        endDrag();
          goto _L10
        endDrag();
          goto _L10
    }

    void setDeleteRegion(RectF rectf)
    {
        mDragRegion = rectf;
    }

    public void setDragListener(DragController.DragListener draglistener)
    {
        mListener = draglistener;
    }

    public void setDragMenuListener(DragController.DragListener draglistener)
    {
        mMenuListener = draglistener;
    }

    public void setDragMenuScoller(DragScroller dragscroller)
    {
        mDragMenuScroller = dragscroller;
    }

    public void setDragScoller(DragScroller dragscroller)
    {
        mDragScroller = dragscroller;
    }

    void setIgnoredDropTarget(View view)
    {
        mIgnoredDropTarget = view;
    }

    void setMenuManager(MenuManager menumanager)
    {
        mMenuManager = menumanager;
    }

    public void setTrashPaint(int i)
    {
        int j;
        if(i == 1)
            j = mSrcColor2;
        else
            j = mSrcColor1;
        mTrashPaint.setColorFilter(new PorterDuffColorFilter(j, android.graphics.PorterDuff.Mode.SRC_ATOP));
    }

    void setWorkspace(Workspace workspace)
    {
        mWorkspace = workspace;
    }

    public void startAnimation(View view, float f, float f1, float f2, float f3, float f4, float f5, 
            int i)
    {
        int j = mDragAnimation.length;
        int k = 0;
        do
        {
label0:
            {
                if(k < j)
                {
                    if(mDragAnimation[k] != null)
                        break label0;
                    DragAnimation adraganimation[] = mDragAnimation;
                    DragAnimation draganimation = new DragAnimation(new DecelerateInterpolator());
                    adraganimation[k] = draganimation;
                    draganimation.setAnimation(view, f, f1, f2, f3, f4, f5, i);
                    mIsDragAnimation = true;
                    invalidate();
                }
                return;
            }
            k++;
        } while(true);
    }

    public void startDrag(View view, DragSource dragsource, Object obj, int i)
    {
        if(mInputMethodManager == null)
            mInputMethodManager = (InputMethodManager)getContext().getSystemService("input_method");
        mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        Rect rect;
        boolean flag;
        int j;
        Bitmap bitmap;
        if(mMenuListener != null)
            mMenuListener.onDragStart(view, dragsource, obj, i);
        else
        if(mListener != null)
            mListener.onDragStart(view, dragsource, obj, i);
        rect = mDragRect;
        rect.set(view.getScrollX(), view.getScrollY(), 0, 0);
        offsetDescendantRectToMyCoords(view, rect);
        mTouchOffsetX = mLastMotionX - (float)rect.left;
        mTouchOffsetY = mLastMotionY - (float)rect.top;
        if(i == 2)
        {
            View view1 = view.findViewById(0x7f06000a);
            if(view1 != null)
            {
                mTouchOffsetX = view1.getWidth() / 2;
                mTouchOffsetY = view1.getHeight() / 2;
            } else
            {
                mTouchOffsetX = 0.0F;
                mTouchOffsetY = view.getHeight() / 2;
            }
        }
        view.clearFocus();
        view.setPressed(false);
        flag = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        j = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        if(j != 0)
            view.destroyDrawingCache();
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        if(bitmap == null)
        {
            Log.e("Launcher.DragLayer", "startDrag().. drawing cache is a null");
            Toast.makeText(getContext(), "Failed to initialize dragging for the widget", 0).show();
            view.destroyDrawingCache();
            view.setWillNotCacheDrawing(flag);
            view.setDrawingCacheBackgroundColor(j);
            if(mMenuListener == null && mListener != null)
                mListener.onDragEnd();
        } else
        {
            int k = bitmap.getWidth();
            int l = bitmap.getHeight();
            Object obj1 = view.getTag();
            Bitmap bitmap1;
            if(obj1 != null && ((obj1 instanceof LauncherAppWidgetInfo) || (obj1 instanceof SamsungAppWidgetInfo)))
                mAnimationTo = 1.05F;
            else
                mAnimationTo = 1.1F;
            mAnimationFrom = 1.0F;
            mAnimationDuration = 100;
            mAnimationState = 1;
            mAnimationType = 1;
            mDragBitmap = Bitmap.createBitmap(bitmap);
            view.destroyDrawingCache();
            view.setWillNotCacheDrawing(flag);
            view.setDrawingCacheBackgroundColor(j);
            bitmap1 = mDragBitmap;
            mBitmapOffsetX = (bitmap1.getWidth() - k) / 2;
            mBitmapOffsetY = (bitmap1.getHeight() - l) / 2;
            if(i == 0)
                view.setVisibility(4);
            mDragPaint = mDimPaint;
            mDragging = true;
            mShouldDrop = true;
            mOriginator = view;
            mDragSource = dragsource;
            mDragInfo = obj;
            mVibrator.vibrate(35L);
            mEnteredRegion = false;
            invalidate();
        }
    }

    static int L_SCROLL_ZONE = 70;
    static int R_SCROLL_ZONE = 48;
    static int SCROLL_ZONE = 50;
    private int mAnimationDuration;
    private float mAnimationFrom;
    private long mAnimationStartTime;
    private int mAnimationState;
    private float mAnimationTo;
    private int mAnimationType;
    private int mBitmapOffsetX;
    private int mBitmapOffsetY;
    private final Paint mDimPaint = new Paint();
    private DragAnimation mDragAnimation[];
    private Bitmap mDragBitmap;
    private Object mDragInfo;
    private DragScroller mDragMenuScroller;
    private Paint mDragPaint;
    private Rect mDragRect;
    private RectF mDragRegion;
    private DragScroller mDragScroller;
    private DragSource mDragSource;
    private boolean mDragging;
    private final int mDropCoordinates[] = new int[2];
    private boolean mEnteredRegion;
    private View mIgnoredDropTarget;
    private InputMethodManager mInputMethodManager;
    private boolean mIsDragAnimation;
    private DropTarget mLastDropTarget;
    private float mLastMotionX;
    private float mLastMotionY;
    private DragController.DragListener mListener;
    private DragController.DragListener mMenuListener;
    private MenuManager mMenuManager;
    private View mOriginator;
    private final Rect mRect = new Rect();
    private final Paint mScrollPaint = new Paint();
    private int mScrollPaintColor;
    private ScrollRunnable mScrollRunnable;
    private int mScrollState;
    private boolean mShouldDrop;
    private int mSrcColor1;
    private int mSrcColor2;
    private float mTouchOffsetX;
    private float mTouchOffsetY;
    private final Paint mTrashPaint = new Paint();
    private final Vibrator mVibrator = new Vibrator();
    private Workspace mWorkspace;






/*
    static int access$302(DragLayer draglayer, int i)
    {
        draglayer.mScrollState = i;
        return i;
    }

*/
}
