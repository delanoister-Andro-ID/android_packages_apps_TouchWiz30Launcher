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

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.*;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import com.android.internal.util.XmlUtils;
import com.nemustech.tiffany.widget.TFAnimateEngine;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Launcher, Workspace, CellLayout, LauncherConfig, 
//            DeleteZone, ItemInfo, Folder, SamsungAppWidgetInfo, 
//            UserFolderInfo, LauncherModel, LauncherAppWidgetInfo, ShortcutInfo, 
//            FolderInfo, UserFolder, LauncherAppWidgetHost, SamsungWidgetPackageManager, 
//            Utilities, MenuDrawer

public class QuickViewWorkspace extends FrameLayout
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
        final QuickViewWorkspace this$0;

        Animate()
        {
            this$0 = QuickViewWorkspace.this;
            Object();
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


    public QuickViewWorkspace(Context context)
    {
        QuickViewWorkspace(context, null);
    }

    public QuickViewWorkspace(Context context, AttributeSet attributeset)
    {
        QuickViewWorkspace(context, attributeset, 0);
    }

    public QuickViewWorkspace(Context context, AttributeSet attributeset, int i)
    {
        FrameLayout(context, attributeset, i);
        mAnimationStyle = 1;
        mChildAnimate = new Animate[Launcher.SCREEN_COUNT];
        mTmpRect = new Rect();
        mScreenRect = new Rect();
        mTouchState = 0;
        mDragState = 0;
        mMultiTouchUsed = false;
        mPinchOutProcess = false;
        mClickCheck = false;
        mDragBitmap = null;
        mPressBitmap = null;
        mScreenBgDrawablePadding = new Rect();
        mTrashPaint = new Paint();
        mDragPaint = new Paint();
        mOrientation = 1;
        mUseFullScreenInLandScapeMode = true;
        mAnimationState = 7;
        mTmpLocation = new int[4];
        mTmpLocation2 = new int[4];
        init();
    }

    private void addScreen()
    {
        int i = getChildCount();
        int j = i - 1;
        if(j < 0)
            j = 0;
        View view = getChildAt(j);
        makeScreen(j).setOnLongClickListener(this);
        if(i >= Launcher.SCREEN_COUNT)
            removeView(view);
        onAdd();
    }

    private void applyDeleteZone()
    {
        mDragState = 1;
        mDraggingView.buildDrawingCache();
        Bitmap bitmap = mDraggingView.getDrawingCache();
        int i = bitmap.getWidth();
        int j = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(1.0F, 1.0F);
        mDragBitmap = Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, true);
    }

    private void drop(int i, int j)
    {
        mDraggingView.setVisibility(0);
        mDraggingView.requestLayout();
        if(isDeleteZone(i, j) && mDragState == 1)
        {
            removeView(mDraggingView);
            addView(mDraggingView, mDraggingIndex);
            swapScreen(mDraggingIndex);
            mDeleteIndex = mDraggingIndex;
            mDeleteView = mDraggingView;
            setDeleteZoneState(false);
            if(isIncludeItem())
                mLauncher.showDeleteWorkScreen();
            else
                removeScreen();
        } else
        {
            int k = getTouchedIndex(i, j);
            if(k != -1 && getChildAt(k).getTag() != ADD_BTN_TAG)
            {
                Log.d("QuickViewWorkspace", (new StringBuilder()).append("drop index:").append(k).toString());
                swapScreen(k);
            } else
            {
                Log.d("QuickViewWorkspace", (new StringBuilder()).append("invalid drop index:").append(k).toString());
                swapScreen(mDraggingIndex);
            }
        }
    }

    private void enableWorkspaceScreensCache()
    {
        Workspace workspace = mLauncher.getWorkspace();
        int i = workspace.getChildCount();
        for(int j = 0; j < i; j++)
        {
            CellLayout celllayout = (CellLayout)workspace.getChildAt(j);
            celllayout.setChildrenDrawingCacheEnabled(true);
            celllayout.setChildrenDrawnWithCacheEnabled(true);
        }

    }

    private void endDrag()
    {
        mTouchState = 0;
        mDragState = 0;
        mDraggingView = null;
        mTouchedView = null;
        mPinchIndex = -1;
        mDraggingIndex = -1;
        mOriginDragIndex = -1;
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

    private void getViewLayout(int i)
    {
        mTempRects = null;
        mTempRects = new Rect[i];
        XmlResourceParser xmlresourceparser;
        AttributeSet attributeset;
        int j;
        xmlresourceparser = mContext.getResources().getXml(0x7f050005);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "quickviewlayout");
        j = xmlresourceparser.getDepth();
_L4:
        int k = xmlresourceparser.next();
        if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1) goto _L2; else goto _L1
_L1:
        if(k != 2 || !xmlresourceparser.getName().equals("quickviewset")) goto _L4; else goto _L3
_L3:
        String s = xmlresourceparser.getAttributeValue(0);
        if(!String.valueOf(i).equals(s)) goto _L4; else goto _L5
_L5:
        int l;
        int i1;
        l = xmlresourceparser.getDepth();
        i1 = 0;
_L9:
        int j1;
        j1 = xmlresourceparser.next();
        if(j1 == 3 && xmlresourceparser.getDepth() <= l || j1 == 1)
            continue; /* Loop/switch isn't completed */
        if(j1 == 2) goto _L7; else goto _L6
_L6:
        if(!xmlresourceparser.getName().equals("quickviewset")) goto _L9; else goto _L8
_L8:
        if(i1 == i) goto _L4; else goto _L10
_L10:
        throw new IllegalStateException((new StringBuilder()).append("xml item count mismatch : item ").append(i1).append(", count ").append(i).toString());
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("QuickViewWorkspace", "Got exception parsing quickviews.", xmlpullparserexception);
        xmlpullparserexception.printStackTrace();
_L2:
        return;
_L7:
        if(!xmlresourceparser.getName().equals("quickview")) goto _L9; else goto _L11
_L11:
        TypedArray typedarray = mContext.obtainStyledAttributes(attributeset, R.styleable.QuickView);
        int k1 = Integer.valueOf(typedarray.getString(0)).intValue();
        int l1 = Integer.valueOf(typedarray.getString(1)).intValue();
        int i2 = Integer.valueOf(typedarray.getString(2)).intValue();
        int j2 = Integer.valueOf(typedarray.getString(3)).intValue();
        if(mTempRects[i1] == null)
            mTempRects[i1] = new Rect();
        mTempRects[i1].set(k1, l1, i2, j2);
        typedarray.recycle();
        i1++;
          goto _L9
        IOException ioexception;
        ioexception;
        Log.w("QuickViewWorkspace", "Got exception parsing quickviews.", ioexception);
        ioexception.printStackTrace();
          goto _L2
    }

    private void init()
    {
        mWallpaperManager = WallpaperManager.getInstance(getContext());
        mAniEngine = new TFAnimateEngine();
        mAniEngine.setInterpolator(new AccelerateDecelerateInterpolator());
        mOrientation = getResources().getConfiguration().orientation;
        mUseFullScreenInLandScapeMode = LauncherConfig.landscapeScreen_isUseFullScreenQuickView(getContext());
        Context context = getContext();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int i = context.getResources().getColor(0x7f070004);
        mTrashPaint.setColorFilter(new PorterDuffColorFilter(i, android.graphics.PorterDuff.Mode.SRC_ATOP));
        int j = context.getResources().getColor(0x7f07000b);
        mDragPaint.setColorFilter(new PorterDuffColorFilter(j, android.graphics.PorterDuff.Mode.SRC_ATOP));
        for(int k = 0; k < Launcher.SCREEN_COUNT; k++)
            mChildAnimate[k] = new Animate();

        setClipChildren(false);
    }

    private boolean isAvailableRect(Rect rect)
    {
        boolean flag;
        if(rect.right < mScreenRect.left || rect.top > mScreenRect.bottom || rect.left > mScreenRect.right || rect.bottom < mScreenRect.top)
            flag = false;
        else
            flag = true;
        return flag;
    }

    private boolean isDeleteZone(int i, int j)
    {
        DeleteZone deletezone = mLauncher.getDeleteZone();
        deletezone.getDeleteAreaRect(mTmpRect);
        deletezone.getLocationOnScreen(mTmpLocation);
        getLocationInWindow(mTmpLocation2);
        mTmpRect.offset(mTmpLocation[0] - mTmpLocation2[0], mTmpLocation[1] - mTmpLocation2[1]);
        return mTmpRect.contains(i, j);
    }

    private boolean isIncludeItem()
    {
        boolean flag;
        CellLayout celllayout;
        int i;
        int j;
        flag = false;
        celllayout = (CellLayout)mLauncher.getWorkspace().getChildAt(mDraggingIndex);
        i = celllayout.getChildCount();
        j = 0;
_L7:
        if(j >= i) goto _L2; else goto _L1
_L1:
        ItemInfo iteminfo = (ItemInfo)celllayout.getChildAt(j).getTag();
        if(iteminfo.container != -1L) goto _L4; else goto _L3
_L3:
        flag = false;
_L2:
        return flag;
_L4:
        if(iteminfo.container != -100L)
            break; /* Loop/switch isn't completed */
        flag = true;
        if(true) goto _L2; else goto _L5
_L5:
        j++;
        if(true) goto _L7; else goto _L6
_L6:
    }

    private View makeAddBtn()
    {
        View view = makeScreen(getChildCount());
        view.setTag(ADD_BTN_TAG);
        return view;
    }

    private View makeScreen(int i)
    {
        View view;
        if(getChildCount() >= Launcher.SCREEN_COUNT)
            mClickCheck = false;
        else
            mClickCheck = true;
        view = new View(getContext());
        view.setBackgroundDrawable(mScreenBgDrawable);
        addView(view, i);
        view.setTag(Integer.valueOf(i));
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
        return view;
    }

    private void onAdd()
    {
        int i = getChildCount();
        Workspace workspace = mLauncher.getWorkspace();
        CellLayout celllayout = (CellLayout)LayoutInflater.from(mLauncher).inflate(0x7f030016, workspace, false);
        celllayout.setOnLongClickListener(mLauncher);
        celllayout.setId(mLauncher.getCellLayoutId(i));
        android.view.ViewGroup.LayoutParams layoutparams = celllayout.getLayoutParams();
        if(layoutparams == null)
            layoutparams = new LayoutParams(-1, -1);
        workspace.addView(celllayout, layoutparams);
        mLauncher.saveScreenInfo();
        updateTag();
    }

    private void onRemove()
    {
        Workspace workspace;
        CellLayout celllayout;
        int i;
        int j;
        workspace = mLauncher.getWorkspace();
        celllayout = (CellLayout)workspace.getChildAt(mDeleteIndex);
        i = celllayout.getChildCount();
        j = 0;
_L2:
        Object obj;
        if(j >= i)
            break MISSING_BLOCK_LABEL_360;
        View view = celllayout.getChildAt(j);
        obj = view.getTag();
        if(!(view instanceof Folder))
            break; /* Loop/switch isn't completed */
_L5:
        j++;
        if(true) goto _L2; else goto _L1
_L1:
        ItemInfo iteminfo = (ItemInfo)obj;
        if(iteminfo.container != -1L) goto _L4; else goto _L3
_L3:
        return;
_L4:
        if(iteminfo.container == -100L)
        {
            if(iteminfo instanceof SamsungAppWidgetInfo)
                mLauncher.removeSamsungAppWidget((SamsungAppWidgetInfo)iteminfo);
            else
            if(iteminfo instanceof LauncherAppWidgetInfo)
                mLauncher.removeAppWidget((LauncherAppWidgetInfo)iteminfo);
            else
            if(iteminfo instanceof ShortcutInfo)
                mLauncher.removeShortcut((ShortcutInfo)iteminfo);
            else
            if(iteminfo instanceof FolderInfo)
                mLauncher.removeFolder((FolderInfo)iteminfo);
            else
                throw new IllegalStateException((new StringBuilder()).append("What's that????? ").append(iteminfo).toString());
        } else
        if(obj instanceof UserFolder)
            ((UserFolderInfo)((UserFolder)obj).getInfo()).remove((ShortcutInfo)iteminfo);
        if(iteminfo instanceof UserFolderInfo)
        {
            UserFolderInfo userfolderinfo = (UserFolderInfo)iteminfo;
            LauncherModel.deleteUserFolderContentsFromDatabase(mLauncher, userfolderinfo);
            mLauncher.removeFolder(userfolderinfo);
        } else
        if(iteminfo instanceof LauncherAppWidgetInfo)
        {
            LauncherAppWidgetInfo launcherappwidgetinfo = (LauncherAppWidgetInfo)iteminfo;
            LauncherAppWidgetHost launcherappwidgethost = mLauncher.getAppWidgetHost();
            if(launcherappwidgethost != null)
                launcherappwidgethost.deleteAppWidgetId(launcherappwidgetinfo.appWidgetId);
        } else
        if(iteminfo instanceof SamsungAppWidgetInfo)
        {
            SamsungAppWidgetInfo samsungappwidgetinfo = (SamsungAppWidgetInfo)iteminfo;
            SamsungWidgetPackageManager.getInstance().destroyWidget(mLauncher, samsungappwidgetinfo);
        }
        LauncherModel.deleteItemFromDatabase(mLauncher, iteminfo);
          goto _L5
        workspace.removeView(workspace.getChildAt(mDeleteIndex));
        updateAppInfoInDatabase();
        mLauncher.saveScreenInfo();
        mDeleteIndex = -1;
        mDeleteView = null;
        int k = workspace.getChildCount();
        if(k <= mLauncher.getWorkspace().getCurrentScreen())
        {
            mLauncher.getWorkspace().setCurrentScreen(k - 1);
            Launcher.setScreen(mLauncher.getWorkspace().getCurrentScreen());
        }
        updateTag();
          goto _L3
    }

    private void onSwap(int i)
    {
        Workspace workspace = mLauncher.getWorkspace();
        Utilities.zOrderTweakMoveChild(workspace, workspace.indexOfChild(workspace.getChildAt(mOriginDragIndex)), i, true);
        updateTag();
        updateAppInfoInDatabase();
        mLauncher.saveScreenInfo();
    }

    private void setCloseEndRect(int i)
    {
        int j = getChildCount();
        getLocationOnScreen(mTmpLocation);
        int k = mTmpLocation[1];
        View view = mLauncher.getWorkspace().getChildAt(0);
        view.getLocationOnScreen(mTmpLocation);
        int l = view.getWidth();
        int i1 = view.getHeight();
        int j1 = view.getLeft();
        int k1 = view.getTop();
        mLauncher.getWorkspace().getLocationOnScreen(mTmpLocation);
        int l1 = mTmpLocation[1] + (int)getContext().getResources().getDimension(0x1050004);
        int i2 = mScreenBgDrawablePadding.left;
        int j2 = mScreenBgDrawablePadding.right;
        int k2 = mScreenBgDrawablePadding.top;
        int l2 = mScreenBgDrawablePadding.bottom;
        if(j > Launcher.SCREEN_COUNT)
            j = Launcher.SCREEN_COUNT;
        if(mFinishRects == null || mFinishRects.length != j)
        {
            mFinishRects = new Rect[j];
            for(int i3 = 0; i3 < j; i3++)
                mFinishRects[i3] = new Rect();

        }
        mFinishRects[i].set(j1 - i2, k1 - k2, j2 + (l + j1), l2 + (k1 + i1));
        if(mOrientation == 2 && mUseFullScreenInLandScapeMode)
            mFinishRects[i].offset(0, l1 - k);
        if(mAnimationStyle == 0 || mChildRects == null)
        {
            if(Launcher.SCREEN_COUNT == 9)
            {
                Log.e("QuickViewWorkspace", "setCloseEndRect : 9:");
                byte byte0;
                int l3;
                int i4;
                int j4;
                if(j > 4)
                    byte0 = 3;
                else
                    byte0 = 2;
                l3 = i / byte0;
                i4 = i % byte0;
                j4 = 0;
                while(j4 < j) 
                {
                    if(j4 != i)
                    {
                        mFinishRects[j4].set(mFinishRects[i]);
                        int k4 = j4 / byte0;
                        int l4 = j4 % byte0;
                        mFinishRects[j4].offset((l4 - i4) * mFinishRects[i].width(), (k4 - l3) * mFinishRects[i].height());
                    }
                    j4++;
                }
            } else
            {
                Log.e("QuickViewWorkspace", "setCloseEndRect : X");
                for(int j3 = i - 1; j3 >= 0; j3--)
                {
                    mFinishRects[j3].set(mFinishRects[j3 + 1]);
                    mFinishRects[j3].offset(-mFinishRects[j3].width(), 0);
                }

                for(int k3 = i + 1; k3 < j; k3++)
                {
                    mFinishRects[k3].set(mFinishRects[k3 - 1]);
                    mFinishRects[k3].offset(mFinishRects[k3].width(), 0);
                }

            }
        } else
        {
            Log.e("QuickViewWorkspace", "setCloseEndRect : NEW:");
            Rect rect = mFinishRects[i];
            Rect rect1 = mChildRects[i];
            float f = (float)rect.width() / (float)rect1.width();
            float f1 = (float)rect.height() / (float)rect1.height();
            int i5 = 0;
            while(i5 < j) 
            {
                if(i5 != i)
                {
                    Rect rect2 = mChildRects[i5];
                    int j5 = rect2.left - rect1.left;
                    int k5 = rect2.top - rect1.top;
                    int l5 = (int)(f * (float)j5);
                    int i6 = (int)(f1 * (float)k5);
                    mFinishRects[i5].set(l5 + rect.left, i6 + rect.top, l5 + rect.right, i6 + rect.bottom);
                }
                i5++;
            }
        }
    }

    private void setDeleteZoneState(boolean flag)
    {
        mLauncher.getDeleteZone().setDragEnterForced(flag);
        mLauncher.getDeleteZone().invalidate();
    }

    private void setOpenStartRect(int i)
    {
        int j = getChildCount();
        getLocationOnScreen(mTmpLocation);
        int k = mTmpLocation[1];
        View view = mLauncher.getWorkspace().getChildAt(0);
        view.getLocationOnScreen(mTmpLocation);
        int l = view.getWidth();
        int i1 = view.getHeight();
        int j1 = view.getLeft();
        int k1 = view.getTop();
        mLauncher.getWorkspace().getLocationOnScreen(mTmpLocation);
        int l1 = mTmpLocation[1] + (int)getContext().getResources().getDimension(0x1050004);
        int i2 = mScreenBgDrawablePadding.left;
        int j2 = mScreenBgDrawablePadding.right;
        int k2 = mScreenBgDrawablePadding.top;
        int l2 = mScreenBgDrawablePadding.bottom;
        if(mStartRects == null || mStartRects.length != j)
        {
            mStartRects = new Rect[j];
            for(int i3 = 0; i3 < j; i3++)
                mStartRects[i3] = new Rect();

        }
        mStartRects[i].set(j1 - i2, k1 - k2, j2 + (l + j1), l2 + (k1 + i1));
        if(mOrientation == 2 && mUseFullScreenInLandScapeMode)
            mStartRects[i].offset(0, l1 - k);
        if(mAnimationStyle == 0 || mChildRects == null)
        {
            if(Launcher.SCREEN_COUNT == 9)
            {
                byte byte0;
                int l3;
                int i4;
                int j4;
                if(j > 4)
                    byte0 = 3;
                else
                    byte0 = 2;
                l3 = i / byte0;
                i4 = i % byte0;
                j4 = 0;
                while(j4 < j) 
                {
                    if(j4 != i)
                    {
                        mStartRects[j4].set(mStartRects[i]);
                        int k4 = j4 / byte0;
                        int l4 = j4 % byte0;
                        mStartRects[j4].offset((l4 - i4) * mStartRects[i].width(), (k4 - l3) * mStartRects[i].height());
                    }
                    j4++;
                }
            } else
            {
                for(int j3 = i - 1; j3 >= 0; j3--)
                {
                    mStartRects[j3].set(mStartRects[j3 + 1]);
                    mStartRects[j3].offset(-mStartRects[j3].width(), 0);
                }

                for(int k3 = i + 1; k3 < j; k3++)
                {
                    mStartRects[k3].set(mStartRects[k3 - 1]);
                    mStartRects[k3].offset(mStartRects[k3].width(), 0);
                }

            }
        } else
        {
            Log.e("QuickViewWorkspace", "setOpenStartRect : NEW:");
            Rect rect = mStartRects[i];
            Rect rect1 = mChildRects[i];
            float f = (float)rect.width() / (float)rect1.width();
            int i5 = 0;
            while(i5 < j) 
            {
                if(i5 != i)
                {
                    Rect rect2 = mChildRects[i5];
                    int j5 = rect2.left - rect1.left;
                    int k5 = rect2.top - rect1.top;
                    int l5 = (int)(f * (float)j5);
                    int i6 = (int)(f * (float)k5);
                    mStartRects[i5].set(l5 + rect.left, i6 + rect.top, l5 + rect.right, i6 + rect.bottom);
                }
                i5++;
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
        mDraggingView.buildDrawingCache();
        Bitmap bitmap = mDraggingView.getDrawingCache();
        int i = bitmap.getWidth();
        int j = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(1.0F, 1.0F);
        mPressBitmap = Bitmap.createBitmap(bitmap, 0, 0, i, j, matrix, true);
        view.invalidate();
    }

    private void swapScreen(int i)
    {
        Utilities.zOrderTweakMoveChild(this, indexOfChild(mDraggingView), i, true);
        onSwap(i);
    }

    private void updateAppInfoInDatabase()
    {
        Workspace workspace = mLauncher.getWorkspace();
        int i = workspace.getChildCount();
        for(int j = 0; j < i; j++)
        {
            ViewGroup viewgroup = (ViewGroup)workspace.getChildAt(j);
            int k = viewgroup.getChildCount();
            for(int l = 0; l < k; l++)
            {
                Object obj = viewgroup.getChildAt(l).getTag();
                if(obj == null)
                    continue;
                ItemInfo iteminfo = (ItemInfo)obj;
                if(iteminfo.screen != j)
                {
                    iteminfo.screen = j;
                    LauncherModel.updateItemInDatabase(mLauncher, iteminfo);
                }
            }

        }

    }

    private void updateTag()
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
        {
            View view = getChildAt(j);
            if(view.getTag() != ADD_BTN_TAG)
                view.setTag(Integer.valueOf(j));
        }

    }

    private void updateWallpaperOffset(int i, int j, float f)
    {
        float f1 = 0.5F;
        if(mWorkspaceScreenCountOnOpen > 1)
            f1 = (float)mWorkspaceScreenIndexOnOpen / (float)(mWorkspaceScreenCountOnOpen - 1);
        float f2 = 0.5F;
        if(j > 1)
            f2 = (float)i / (float)(j - 1);
        float f3 = f1 + f * (f2 - f1);
        mLauncher.getWorkspace().scrollTo((int)(f3 * (float)(mLauncher.getWorkspace().getChildCount() - 1) * (float)mLauncher.getWorkspace().getWidth()), 0);
        android.os.IBinder ibinder = getWindowToken();
        if(ibinder != null)
            mWallpaperManager.setWallpaperOffsets(ibinder, f3, 0.0F);
    }

    void cancelDeleteView()
    {
        if(mDeleteView != null)
        {
            removeView(mDeleteView);
            addView(mDeleteView, mDeleteIndex);
            mDeleteIndex = -1;
            mDeleteView = null;
            mDragState = 0;
        }
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
        setDeleteZoneState(false);
        mDragState = 0;
        mTouchState = 0;
        mDraggingIndex = -1;
        mDraggingView = null;
        mTouchedView = null;
        mPinchIndex = -1;
        mOriginDragIndex = -1;
        postInvalidate();
    }

    void close()
    {
        mScrollY = 0;
        mEnabledChildAnimation = false;
        setVisibility(4);
        mLauncher.getDeleteZone().setVisibility(4);
        disableWorkspaceScreensCache();
    }

    void disableWorkspaceScreensCache()
    {
        Workspace workspace = mLauncher.getWorkspace();
        int i = workspace.getChildCount();
        for(int j = 0; j < i; j++)
            ((CellLayout)workspace.getChildAt(j)).setChildrenDrawnWithCacheEnabled(false);

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
        if(i > Launcher.SCREEN_COUNT)
            i = Launcher.SCREEN_COUNT;
        if(j != 0 && k != 0) goto _L2; else goto _L1
_L1:
        return;
_L2:
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        if(mAnimationState == 1)
        {
            mAnimationStartTime = SystemClock.uptimeMillis();
            mAnimationState = 2;
            mPinchIndex = mLauncher.getWorkspace().getCurrentScreen();
            setOpenStartRect(mPinchIndex);
            requestLayout();
            mFinishRects = new Rect[i];
            mCurrentRects = new Rect[i];
            for(int i3 = 0; i3 < i; i3++)
            {
                mFinishRects[i3] = new Rect();
                mFinishRects[i3].set(mChildRects[i3]);
                mCurrentRects[i3] = new Rect();
            }

        } else
        if(mAnimationState == 3)
        {
            mAnimationStartTime = SystemClock.uptimeMillis();
            mAnimationState = 4;
            mPinchIndex = mCurrentPage;
            requestLayout();
            setCloseEndRect(mPinchIndex);
            mStartRects = new Rect[i];
            mCurrentRects = new Rect[i];
            for(int l2 = 0; l2 < i; l2++)
            {
                mStartRects[l2] = new Rect();
                mStartRects[l2].set(mChildRects[l2]);
                mCurrentRects[l2] = new Rect();
            }

        }
        if(mTouchState == 2)
        {
            for(int k2 = 0; k2 < i; k2++)
            {
                View view2 = getChildAt(k2);
                if(view2 != mDraggingView)
                    drawChild(canvas, view2, l);
            }

            canvas.clipRect(mScrollX, mScrollY, j + mScrollX, k + mScrollY, android.graphics.Region.Op.REPLACE);
            drawChild(canvas, mDraggingView, l);
            continue; /* Loop/switch isn't completed */
        }
        if(mAnimationState != 2 && mAnimationState != 4)
            break; /* Loop/switch isn't completed */
        int i1 = (int)(SystemClock.uptimeMillis() - mAnimationStartTime);
        if(i1 >= mAnimationDuration)
        {
            i1 = mAnimationDuration;
            if(mAnimationState == 4)
                mLauncher.closeQuickViewWorkspace();
        }
        float f = (float)i1 / (float)mAnimationDuration;
        if(mAnimationState == 4 && mPinchIndex != mLauncher.getWorkspace().getCurrentScreen())
            updateWallpaperOffset(mPinchIndex, mLauncher.getWorkspace().getChildCount(), f);
        int j1;
        if(i1 >= mAnimationDuration)
        {
            int l1;
            int i2;
            if(mAnimationState == 2)
                i2 = 7;
            else
                i2 = 8;
            mAnimationState = i2;
        }
        if(mCurrentRects == null || mCurrentRects.length != i)
        {
            Log.d("QuickViewWorkspace", (new StringBuilder()).append("dipathDraw mAnimationState:").append(mAnimationState).append("  mCurrnetRect.length:").append(mCurrentRects.length).append("  array count:").append(i).toString());
            if(i > 0)
                mCurrentRects = new Rect[i];
        }
        mCurrentRects = mAniEngine.getRect(mStartRects, mFinishRects, mCurrentRects, f);
        j1 = 0;
        while(j1 < i) 
        {
            l1 = mPinchIndex;
            if(j1 != l1)
            {
                View view1 = getChildAt(j1);
                canvas.clipRect(mCurrentRects[j1].left, mCurrentRects[j1].top, mCurrentRects[j1].right, mCurrentRects[j1].bottom, android.graphics.Region.Op.REPLACE);
                view1.layout(mCurrentRects[j1].left, mCurrentRects[j1].top, mCurrentRects[j1].right, mCurrentRects[j1].bottom);
                if(isAvailableRect(mCurrentRects[j1]))
                {
                    view1.setBackgroundDrawable(mScreenBgDrawable);
                    drawChild(canvas, view1, l);
                }
            }
            j1++;
        }
        if(mPinchIndex >= 0 && mPinchIndex < i)
        {
            int k1 = mPinchIndex;
            View view = getChildAt(k1);
            canvas.clipRect(mCurrentRects[k1].left, mCurrentRects[k1].top, mCurrentRects[k1].right, mCurrentRects[k1].bottom, android.graphics.Region.Op.REPLACE);
            view.layout(mCurrentRects[k1].left, mCurrentRects[k1].top, mCurrentRects[k1].right, mCurrentRects[k1].bottom);
            view.setBackgroundDrawable(mScreenBgDrawable);
            drawChild(canvas, view, l);
        }
        postInvalidate();
        if(true) goto _L1; else goto _L3
_L3:
        int j2 = 0;
        while(j2 < i) 
        {
            drawChild(canvas, getChildAt(j2), l);
            j2++;
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
        if(obj != ADD_BTN_TAG && mLauncher != null && (obj instanceof Integer))
        {
            j1 = ((Integer)obj).intValue();
            view1 = mLauncher.getWorkspace().getChildAt(j1);
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
            if(mDragState == 1)
                canvas.drawBitmap(mDragBitmap, view.getLeft(), view.getTop(), mTrashPaint);
            else
                canvas.drawBitmap(mPressBitmap, view.getLeft(), view.getTop(), mDragPaint);
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

    public int getCurrentPage()
    {
        return mCurrentPage;
    }

    public int getDeleteIndex()
    {
        return mDeleteIndex;
    }

    void initScreen(int i)
    {
        removeAllViews();
        setBackgroundResource(0);
        mOrientation = getResources().getConfiguration().orientation;
        Context context = getContext();
        Resources resources = context.getResources();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScreenBgDrawable = resources.getDrawable(0x7f020137);
        mScreenBgDrawable.getPadding(mScreenBgDrawablePadding);
        mScreenBgDrawable2 = resources.getDrawable(0x7f020137).mutate();
        mAddDrawable = resources.getDrawable(0x7f02005f);
        for(int j = 0; j < i; j++)
            makeScreen(j);

        if(i < Launcher.SCREEN_COUNT)
            makeAddBtn();
        getGlobalVisibleRect(mScreenRect);
    }

    public boolean isAnimating()
    {
        boolean flag;
        if(mAnimationState == 7 || mAnimationState == 8)
            flag = false;
        else
            flag = true;
        return flag;
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
        if(view.getTag() == ADD_BTN_TAG) goto _L2; else goto _L1
_L1:
        mCurrentPage = ((Integer)view.getTag()).intValue();
        drawCloseAnimation();
        postInvalidate();
_L4:
        return;
_L2:
        if(mClickCheck)
            addScreen();
        if(true) goto _L4; else goto _L3
_L3:
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
    //                   0: 260
    //                   1: 274
    //                   2: 105
    //                   3: 274
    //                   261: 309;
           goto _L3 _L4 _L5 _L6 _L5 _L7
_L3:
        break; /* Loop/switch isn't completed */
_L7:
        break MISSING_BLOCK_LABEL_309;
_L10:
        View view;
        if(mTouchState != 0)
            flag = true;
        else
            flag = false;
          goto _L8
_L6:
        if(!mMultiTouchUsed || !mPinchOutProcess) goto _L10; else goto _L9
_L9:
        mTouchState = 0;
        int j1 = (int)motionevent.getY(0);
        int k1 = (int)motionevent.getX(0);
        int l1 = (int)motionevent.getY(1);
        int i2 = (int)motionevent.getX(1);
        mMovePinch = (int)Math.sqrt((j1 - l1) * (j1 - l1) + (k1 - i2) * (k1 - i2)) - mMovePinchStart;
        if(mMovePinch <= 100) goto _L10; else goto _L11
_L11:
        view = getChildAt(mPinchOutIndex);
        if(view == null || view.getTag() == ADD_BTN_TAG) goto _L10; else goto _L12
_L12:
        mMultiTouchUsed = false;
        mPinchOutProcess = false;
        mCurrentPage = mPinchOutIndex;
        drawCloseAnimation();
        postInvalidate();
        flag = true;
          goto _L8
_L4:
        mLastMotionX = f;
        mLastMotionY = f1;
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
            mMultiTouchUsed = true;
            int j = (int)motionevent.getY(0);
            int k = (int)motionevent.getX(0);
            int l = (int)motionevent.getY(1);
            int i1 = (int)motionevent.getX(1);
            if(getTouchedIndex(k, j) == getTouchedIndex(i1, l))
            {
                mPinchOutProcess = true;
                mMovePinchStart = (int)Math.sqrt((j - l) * (j - l) + (k - i1) * (k - i1));
                mPinchOutIndex = getTouchedIndex(k, j);
            }
        }
          goto _L10
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = getChildCount();
        if(i1 >= 1)
        {
            if(i1 > Launcher.SCREEN_COUNT)
                i1 = Launcher.SCREEN_COUNT;
            if((mChildRects == null || mChildRects.length != i1) && i1 > 0)
                mChildRects = new Rect[i1];
            getViewLayout(i1);
            int j1 = 0;
            while(j1 < i1) 
            {
                View view = getChildAt(j1);
                Rect rect = new Rect();
                if(view.getVisibility() != 8)
                {
                    if(mChildRects[j1] == null)
                        mChildRects[j1] = new Rect();
                    if(mTempRects[j1] == null)
                    {
                        Log.i("QuickViewWorkspace", (new StringBuilder()).append("getChildCount() = ").append(getChildCount()).toString());
                        Log.i("QuickViewWorkspace", (new StringBuilder()).append("'i' value = ").append(j1).toString());
                        getViewLayout(i1);
                    }
                    mChildRects[j1].set(mTempRects[j1]);
                    view.getHitRect(rect);
                    if(!rect.isEmpty() && mEnabledChildAnimation)
                    {
                        if(view.getVisibility() == 0)
                            mChildAnimate[j1].start(view, mChildRects[j1]);
                    } else
                    {
                        view.layout(mChildRects[j1].left, mChildRects[j1].top, mChildRects[j1].right, mChildRects[j1].bottom);
                    }
                }
                j1++;
            }
        }
    }

    public boolean onLongClick(View view)
    {
        if(!mMultiTouchUsed) goto _L2; else goto _L1
_L1:
        return false;
_L2:
        if(mLauncher.isWorkspaceLocked())
            Log.d("QuickViewWorkspace", "ignoring long click. Desktop is loading. Editing not allowed.");
        else
        if(view.getTag() != ADD_BTN_TAG && getChildCount() > 2)
            startDrag(view);
        if(true) goto _L1; else goto _L3
_L3:
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
_L4:
        return flag;
_L2:
        switch(i)
        {
        case 0: // '\0'
        default:
            break;

        case 2: // '\002'
            break; /* Loop/switch isn't completed */

        case 1: // '\001'
        case 3: // '\003'
            break;
        }
        break MISSING_BLOCK_LABEL_452;
_L5:
        flag = true;
        if(true) goto _L4; else goto _L3
_L3:
        int j = (int)(mLastMotionX - f);
        int k = (int)(mLastMotionY - f1);
        int l = mTouchSlop;
        boolean flag1;
        if(Math.abs(k) > l)
            flag1 = true;
        else
            flag1 = false;
        if(flag1 && mTouchState == 0)
            mTouchState = 1;
        if(mTouchState == 2)
        {
            View view = mDraggingView;
            if(view != null)
            {
                Rect rect = mTmpRect;
                view.getHitRect(rect);
                rect.left = rect.left - j;
                rect.top = rect.top - k;
                rect.right = rect.right - j;
                rect.bottom = rect.bottom - k;
                view.layout(rect.left, rect.top, rect.right, rect.bottom);
            }
            mLastMotionX = f;
            mLastMotionY = f1;
            View view1 = mDraggingView;
            int i1 = getTouchedIndex((int)f, (int)f1);
            if(i1 != -1 && i1 != mDraggingIndex && getChildAt(i1).getTag() != ADD_BTN_TAG)
            {
                removeView(view1);
                addView(view1, i1);
                view1.setVisibility(4);
                mDraggingIndex = i1;
            }
            if(isDeleteZone((int)f, (int)f1))
            {
                applyDeleteZone();
                setDeleteZoneState(true);
            } else
            {
                View view2 = mDraggingView;
                if(mDragState == 1)
                    setDeleteZoneState(false);
                mDragState = 0;
                int j1 = getTouchedIndex((int)f, (int)f1);
                if(j1 != -1 && j1 != mDraggingIndex && getChildAt(j1).getTag() != ADD_BTN_TAG)
                {
                    removeView(view2);
                    addView(view2, j1);
                    view2.setVisibility(4);
                    mDraggingIndex = j1;
                }
            }
        }
          goto _L5
        if(mTouchState == 2)
            drop((int)f, (int)f1);
        if(mDraggingView != null || mTouchedView != null)
            postInvalidate();
        endDrag();
          goto _L5
    }

    void open()
    {
        mEnabledChildAnimation = true;
        setVisibility(0);
        mCurrentPage = mLauncher.getWorkspace().getCurrentScreen();
        DeleteZone deletezone = mLauncher.getDeleteZone();
        deletezone.setVisibility(0);
        deletezone.resetMode();
        mWorkspaceScreenCountOnOpen = mLauncher.getWorkspace().getChildCount();
        mWorkspaceScreenIndexOnOpen = mCurrentPage;
        enableWorkspaceScreensCache();
    }

    public void removeScreen()
    {
        if(indexOfChild(mDeleteView) == -1 || mDeleteView == null)
        {
            mDeleteView = null;
        } else
        {
            removeView(mDeleteView);
            int i = getChildCount();
            if(i < 1 || getChildAt(i - 1).getTag() != ADD_BTN_TAG)
                makeAddBtn();
            onRemove();
            mCurrentPage = mLauncher.getWorkspace().getCurrentScreen();
        }
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    private static final Object ADD_BTN_TAG = new Object();
    private Drawable mAddDrawable;
    private TFAnimateEngine mAniEngine;
    private int mAnimationDuration;
    private long mAnimationStartTime;
    private int mAnimationState;
    private int mAnimationStyle;
    private Animate mChildAnimate[];
    private Rect mChildRects[];
    private boolean mClickCheck;
    private int mCurrentPage;
    private Rect mCurrentRects[];
    private int mDeleteIndex;
    private View mDeleteView;
    private Bitmap mDragBitmap;
    private final Paint mDragPaint;
    private int mDragState;
    private int mDraggingIndex;
    private View mDraggingView;
    private boolean mEnabledChildAnimation;
    private Rect mFinishRects[];
    private float mLastMotionX;
    private float mLastMotionY;
    private Launcher mLauncher;
    private int mMovePinch;
    private int mMovePinchStart;
    private boolean mMultiTouchUsed;
    private int mOrientation;
    private int mOriginDragIndex;
    private int mPinchIndex;
    private int mPinchOutIndex;
    private boolean mPinchOutProcess;
    private Bitmap mPressBitmap;
    private Drawable mScreenBgDrawable;
    private Drawable mScreenBgDrawable2;
    private Rect mScreenBgDrawablePadding;
    private Rect mScreenRect;
    private Rect mStartRects[];
    private Rect mTempRects[];
    private int mTmpLocation[];
    private int mTmpLocation2[];
    private Rect mTmpRect;
    private int mTouchSlop;
    private int mTouchState;
    private View mTouchedView;
    private final Paint mTrashPaint;
    private boolean mUseFullScreenInLandScapeMode;
    private WallpaperManager mWallpaperManager;
    private int mWorkspaceScreenCountOnOpen;
    private int mWorkspaceScreenIndexOnOpen;

}
