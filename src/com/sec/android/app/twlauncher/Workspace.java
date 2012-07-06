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
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.Scroller;
import android.widget.TextView;
import java.lang.reflect.Array;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragScroller, DragSource, DropTarget, LauncherConfig, 
//            Launcher, PageIndicator, CellLayout, LauncherApplication, 
//            UserFolder, ItemInfo, ApplicationInfo, ShortcutInfo, 
//            LauncherModel, Folder, UserFolderInfo, FolderIcon, 
//            MenuManager, TopFourZone, QuickViewWorkspace, QuickViewMainMenu, 
//            MenuDrawer, SamsungAppWidgetInfo, SamsungWidgetPackageManager, DragController, 
//            IconCache, FastBitmapDrawable, LiveFolderInfo, LauncherAppWidgetInfo

public class Workspace extends ViewGroup
    implements DragScroller, DragSource, DropTarget
{
    public static class SavedState extends android.view.View.BaseSavedState
    {

        public void writeToParcel(Parcel parcel, int i)
        {
            super.writeToParcel(parcel, i);
            parcel.writeInt(currentScreen);
        }

        public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

            public SavedState createFromParcel(Parcel parcel)
            {
                return new SavedState(parcel);
            }

            public volatile Object createFromParcel(Parcel parcel)
            {
                return createFromParcel(parcel);
            }

            public SavedState[] newArray(int i)
            {
                return new SavedState[i];
            }

            public volatile Object[] newArray(int i)
            {
                return newArray(i);
            }

        }
;
        int currentScreen;


        private SavedState(Parcel parcel)
        {
            super(parcel);
            currentScreen = -1;
            currentScreen = parcel.readInt();
        }


        SavedState(Parcelable parcelable)
        {
            super(parcelable);
            currentScreen = -1;
        }
    }


    public Workspace(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public Workspace(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        SNAP_VELOCITY = 200;
        mNextScreen = -1;
        mSnapToScreenDuration = 400;
        mTargetCell = null;
        mTouchState = 0;
        mVacantCache = null;
        mTempCell = new int[2];
        mTempEstimate = new int[2];
        mOpenFlag = false;
        mDrawerBounds = new Rect();
        mClipBounds = new Rect();
        mMultiTouchUsed = false;
        mHideIndicator = new Runnable() {

            public void run()
            {
                if(mPageIndicator != null)
                {
                    mPageIndicator.hide();
                    postInvalidate();
                }
            }

            final Workspace this$0;

            
            {
                this$0 = Workspace.this;
                super();
            }
        }
;
        mStartScreen = -1;
        mEndScreen = -1;
        mScrollDirection = 0;
        mDimPaint = new Paint();
        mTmpRect = new Rect();
        mWallpaperManager = WallpaperManager.getInstance(context);
        mUseLargeDrawablesOnlyForPageIndicator = LauncherConfig.pageIndicator_getUseLargeDrawablesOnly(context);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Workspace, i, 0);
        mDefaultScreen = typedarray.getInt(0, 1);
        mDelayedShortcutDisplay = typedarray.getBoolean(1, false);
        typedarray.recycle();
        initWorkspace();
    }

    private void clearVacantCache()
    {
        if(mVacantCache != null)
        {
            mVacantCache.clearVacantCells();
            mVacantCache = null;
        }
    }

    private void drawDefaultIMEIText(Canvas canvas)
    {
        canvas.save();
        canvas.translate(mScrollX, 0.0F);
        if(getWidth() < getHeight())
            canvas.drawRect(mDefaultIMEIPosX - 30 - 10, mDefaultIMEIPosY - 20, 230F, 240F, mIMEITextPaint_background);
        else
            canvas.drawRect(mDefaultIMEIPosX - 30 - 10, mDefaultIMEIPosY - 20, 240F, 200F, mIMEITextPaint_background);
        canvas.drawText(Launcher.mHwVer, mDefaultIMEIPosX, mDefaultIMEIPosY, mIMEITextPaint);
        canvas.drawText(Launcher.mPdaVer, mDefaultIMEIPosX, mDefaultIMEIPosY + mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mCscVer, mDefaultIMEIPosX, mDefaultIMEIPosY + 2 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mPhoneVer, mDefaultIMEIPosX, mDefaultIMEIPosY + 3 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mBattVer, mDefaultIMEIPosX, mDefaultIMEIPosY + 4 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mBand, mDefaultIMEIPosX, mDefaultIMEIPosY + 5 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mTSP, mDefaultIMEIPosX, mDefaultIMEIPosY + 6 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mCalDate, mDefaultIMEIPosX, mDefaultIMEIPosY + 7 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.drawText(Launcher.mSmd_Pba, mDefaultIMEIPosX, mDefaultIMEIPosY + 8 * mDefaultIMEIPosYGap, mIMEITextPaint);
        canvas.restore();
    }

    private void drawPageIndicator(Canvas canvas)
    {
        int i;
        PageIndicator pageindicator;
        i = getWhichScreen();
        pageindicator = mPageIndicator;
        if(pageindicator != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        pageindicator.setCurrentPage(i, mUseLargeDrawablesOnlyForPageIndicator);
        pageindicator.setOffset(mPageIndicatorLeft + mScrollX, mPageIndicatorTop);
        if(Launcher.USE_MAINMENU_ICONMODE)
            pageindicator.setOffsetY(mPageIndicatorOffsetY);
        if(pageindicator.draw(canvas))
            postInvalidate();
        if(true) goto _L1; else goto _L3
_L3:
    }

    private int[] estimateDropCell(int i, int j, int k, int l, View view, CellLayout celllayout, int ai[])
    {
        if(mVacantCache == null)
            mVacantCache = celllayout.findAllVacantCells(null, view);
        return celllayout.findNearestVacantArea(i, j, k, l, mVacantCache, ai);
    }

    private CellLayout getCurrentDropLayout()
    {
        int i;
        if(mScroller.isFinished())
            i = mCurrentScreen;
        else
            i = mNextScreen;
        return (CellLayout)getChildAt(i);
    }

    private int getWhichScreen()
    {
        int i = getWidth();
        int j = getChildCount();
        int k = mScrollX;
        int l = getChildAt(j - 1).getRight() - k - getWidth();
        int i1;
        if(k < 0)
            i1 = 0;
        else
        if(l <= 0)
            i1 = j - 1;
        else
            i1 = (k + i / 2) / i;
        return i1;
    }

    private void initPageIndicator()
    {
        if(mPageIndicator == null)
            mPageIndicator = new PageIndicator();
        int i = getChildCount();
        int j = mPageIndicatorLarge.getIntrinsicWidth();
        if(getResources().getBoolean(0x7f080004))
        {
            mPageIndicatorLarge.setAlpha(205);
            mPageIndicatorSmall.setAlpha(153);
        }
        PageIndicator pageindicator = mPageIndicator;
        pageindicator.setPageDrawable(mPageIndicatorLarge);
        pageindicator.setPageDrawableSmall(mPageIndicatorSmall);
        pageindicator.setPageCount(i);
        pageindicator.setGap(mPageIndicatorGap);
        pageindicator.setTextSize(mTextSize);
        int k;
        int l;
        int i1;
        int j1;
        if(mEnablePageIndicatorShowHide)
            pageindicator.enableShowHide(true);
        else
            pageindicator.enableShowHide(false);
        if(Launcher.USE_MAINMENU_ICONMODE)
            pageindicator.setOrientation(mOrientation);
        k = j * i;
        l = mPageIndicatorGap;
        if(i > 0)
            i1 = i - 1;
        else
            i1 = 0;
        j1 = k + i1 * l;
        mPageIndicatorLeft = (getWidth() - j1) / 2;
    }

    private void initWorkspace()
    {
        mScroller = new Scroller(getContext());
        mCurrentScreen = mDefaultScreen;
        Launcher.setScreen(mCurrentScreen);
        mIconCache = ((LauncherApplication)getContext().getApplicationContext()).getIconCache();
        ViewConfiguration viewconfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        SNAP_VELOCITY = 6 * viewconfiguration.getScaledMinimumFlingVelocity();
        Resources resources = getResources();
        mPageIndicatorMiddle = resources.getDrawable(0x7f020046);
        mOrientation = resources.getConfiguration().orientation;
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mPageIndicatorLarge = resources.getDrawable(0x7f02005c);
            mPageIndicatorSmall = resources.getDrawable(0x7f020051);
            mPageIndicatorGap = resources.getDimensionPixelSize(0x7f090033);
            mPageIndicatorOffsetY = resources.getDimensionPixelSize(0x7f090032);
            mPageIndicatorTop = resources.getDimensionPixelSize(0x7f090035);
        } else
        {
            mPageIndicatorLarge = resources.getDrawable(0x7f02005c);
            mPageIndicatorSmall = resources.getDrawable(0x7f020037);
            mPageIndicatorGap = resources.getDimensionPixelSize(0x7f09001d);
            mPageIndicatorTop = resources.getDimensionPixelSize(0x7f09001b);
        }
        mPageIndicatorSmall.setBounds(0, 0, mPageIndicatorSmall.getIntrinsicWidth(), mPageIndicatorSmall.getIntrinsicHeight());
        mPageIndicatorMiddle.setBounds(0, 0, mPageIndicatorMiddle.getIntrinsicWidth(), mPageIndicatorMiddle.getIntrinsicHeight());
        mPageIndicatorLarge.setBounds(0, 0, mPageIndicatorLarge.getIntrinsicWidth(), mPageIndicatorLarge.getIntrinsicHeight());
        mTextSize = resources.getDimensionPixelSize(0x7f09001c);
        mEnablePageIndicatorShowHide = resources.getBoolean(0x7f080002);
        mOpenFlag = true;
        mDimPaint.setColorFilter(new PorterDuffColorFilter(0x7f07000e, android.graphics.PorterDuff.Mode.SRC_ATOP));
        if(resources.getConfiguration().orientation == 1)
            mSnapToScreenDuration = 400;
        else
            mSnapToScreenDuration = 600;
        mIMEITextPaint = new Paint();
        mIMEITextPaint.setAntiAlias(true);
        mIMEITextPaint.setTextAlign(android.graphics.Paint.Align.LEFT);
        mIMEITextPaint.setTextSize(getResources().getDimensionPixelSize(0x7f09002a));
        mIMEITextPaint.setColor(-1);
        mIMEITextPaint_background = new Paint();
        mIMEITextPaint_background.setColor(0x99000000);
    }

    private void onDropExternal(DragSource dragsource, int i, int j, Object obj, CellLayout celllayout)
    {
        onDropExternal(dragsource, i, j, obj, celllayout, false);
    }

    private void onDropExternal(DragSource dragsource, int i, int j, Object obj, CellLayout celllayout, boolean flag)
    {
        Folder folder;
        boolean flag1;
        folder = getOpenFolder();
        if(folder != null)
            flag1 = true;
        else
            flag1 = false;
        if(!flag1 || !(folder instanceof UserFolder)) goto _L2; else goto _L1
_L1:
        ((UserFolder)folder).onDrop(null, i, j, 0, 0, obj);
_L6:
        return;
_L2:
        Object obj1 = (ItemInfo)obj;
        ((ItemInfo) (obj1)).itemType;
        JVM INSTR tableswitch 0 2: default 88
    //                   0 120
    //                   1 120
    //                   2 336;
           goto _L3 _L4 _L4 _L5
_L3:
        throw new IllegalStateException((new StringBuilder()).append("Unknown item type: ").append(((ItemInfo) (obj1)).itemType).toString());
_L4:
        Object obj2;
        int k;
        int ai[];
        CellLayout.LayoutParams layoutparams;
        Launcher launcher;
        int l;
        int i1;
        int j1;
        Object obj3;
        if(((ItemInfo) (obj1)).container == -1L && (obj1 instanceof ApplicationInfo))
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)obj1;
            obj3 = new ShortcutInfo(applicationinfo);
        } else
        {
            obj3 = obj1;
        }
        obj2 = mLauncher.createShortcut(0x7f030001, celllayout, (ShortcutInfo)obj3);
        obj1 = obj3;
_L7:
        if(flag)
            k = 0;
        else
            k = -1;
        celllayout.addView(((View) (obj2)), k);
        ((View) (obj2)).setOnLongClickListener(mLongClickListener);
        mTargetCell = estimateDropCell(i, j, 1, 1, ((View) (obj2)), celllayout, mTargetCell);
        if(mTargetCell != null)
        {
            ai = mTargetCell;
            celllayout.onDropChild(((View) (obj2)), ai);
            layoutparams = (CellLayout.LayoutParams)((View) (obj2)).getLayoutParams();
            if(mLauncher.getTopFourZone() == null)
                mLauncher.addShortcut((ShortcutInfo)obj1);
            else
            if(dragsource != mLauncher.getTopFourZone())
                mLauncher.addShortcut((ShortcutInfo)obj1);
            launcher = mLauncher;
            l = mCurrentScreen;
            i1 = layoutparams.cellX;
            j1 = layoutparams.cellY;
            LauncherModel.addOrMoveItemInDatabase(launcher, ((ItemInfo) (obj1)), -100L, l, i1, j1);
            if(flag1)
                folder.bringToFront();
        }
        if(true) goto _L6; else goto _L5
_L5:
        obj2 = FolderIcon.fromXml(0x7f03000a, mLauncher, (ViewGroup)getChildAt(mCurrentScreen), (UserFolderInfo)obj1);
          goto _L7
    }

    private void snapToDestination()
    {
        snapToScreen(getWhichScreen());
    }

    private void updateWallpaperOffset(int i)
    {
        float f = 1.0F / (float)(getChildCount() - 1);
        WallpaperManager wallpapermanager = mWallpaperManager;
        float f1;
        int j;
        int k;
        int l;
        android.os.IBinder ibinder;
        if(f == 0.0F)
            f1 = 1.0F;
        else
            f1 = f;
        wallpapermanager.setWallpaperOffsetSteps(f1, 0.0F);
        j = getChildCount();
        k = mScrollX;
        l = (j - 1) * getMeasuredWidth() - k;
        if(k < 0)
            k = 0;
        else
        if(l <= 0)
            k = (j - 1) * getMeasuredWidth();
        ibinder = getWindowToken();
        if(ibinder != null)
            if(i == 0)
            {
                mWallpaperManager.setWallpaperOffsets(ibinder, 0.5F, 0.0F);
            } else
            {
                float f2 = (float)k / (float)i;
                mWallpaperManager.setWallpaperOffsets(ibinder, f2, 0.0F);
            }
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        CellLayout celllayout = getCurrentDropLayout();
        CellLayout.CellInfo cellinfo = mDragInfo;
        int i1;
        int j1;
        if(cellinfo == null)
            i1 = 1;
        else
            i1 = cellinfo.spanX;
        if(cellinfo == null)
            j1 = 1;
        else
            j1 = cellinfo.spanY;
        if(mVacantCache == null)
        {
            View view;
            if(cellinfo == null)
                view = null;
            else
                view = cellinfo.cell;
            mVacantCache = celllayout.findAllVacantCells(null, view);
        }
        return mVacantCache.findCellForSpan(mTempEstimate, i1, j1, false);
    }

    void addApplicationShortcut(ShortcutInfo shortcutinfo, CellLayout.CellInfo cellinfo, boolean flag)
    {
        CellLayout celllayout = (CellLayout)getChildAt(cellinfo.screen);
        int ai[] = new int[2];
        celllayout.cellToPoint(cellinfo.cellX, cellinfo.cellY, ai);
        onDropExternal(null, ai[0], ai[1], shortcutinfo, celllayout, flag);
    }

    public void addFocusables(ArrayList arraylist, int i, int j)
    {
        if(mLauncher.getMenuManager().isOpened()) goto _L2; else goto _L1
_L1:
        Folder folder = getOpenFolder();
        if(folder != null) goto _L4; else goto _L3
_L3:
        getChildAt(mCurrentScreen).addFocusables(arraylist, i);
        if(i != 17) goto _L6; else goto _L5
_L5:
        if(mCurrentScreen > 0)
            getChildAt(mCurrentScreen - 1).addFocusables(arraylist, i);
_L2:
        return;
_L6:
        if(i == 66 && mCurrentScreen < getChildCount() - 1)
            getChildAt(1 + mCurrentScreen).addFocusables(arraylist, i);
        continue; /* Loop/switch isn't completed */
_L4:
        folder.addFocusables(arraylist, i);
        if(true) goto _L2; else goto _L7
_L7:
    }

    void addInCurrentScreen(View view, int i, int j, int k, int l)
    {
        addInScreen(view, mCurrentScreen, i, j, k, l, false);
    }

    void addInCurrentScreen(View view, int i, int j, int k, int l, boolean flag)
    {
        addInScreen(view, mCurrentScreen, i, j, k, l, flag);
    }

    void addInScreen(View view, int i, int j, int k, int l, int i1)
    {
        addInScreen(view, i, j, k, l, i1, false);
    }

    void addInScreen(View view, int i, int j, int k, int l, int i1, boolean flag)
    {
        if(i >= 0 && i < getChildCount()) goto _L2; else goto _L1
_L1:
        Log.e("Launcher.Workspace", (new StringBuilder()).append("Item in db lies on nonexistent screen: ").append(i).append(". Screen count: ").append(getChildCount()).append(". Item will be deleted. Iteminfo:").append(view.getTag().toString()).toString());
        mContext.getContentResolver().delete(LauncherSettings.Favorites.CONTENT_URI, (new StringBuilder()).append("screen=").append(i).toString(), null);
_L4:
        return;
_L2:
        clearVacantCache();
        CellLayout celllayout = (CellLayout)getChildAt(i);
        CellLayout.LayoutParams layoutparams = null;
        if(view.getLayoutParams() != null && (view.getLayoutParams() instanceof CellLayout.LayoutParams))
            layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
        int j1;
        Folder folder;
        if(layoutparams == null)
        {
            layoutparams = new CellLayout.LayoutParams(j, k, l, i1);
        } else
        {
            layoutparams.cellX = j;
            layoutparams.cellY = k;
            layoutparams.cellHSpan = l;
            layoutparams.cellVSpan = i1;
        }
        if(flag)
            j1 = 0;
        else
            j1 = -1;
        celllayout.addView(view, j1, layoutparams);
        if(!(view instanceof Folder))
            view.setOnLongClickListener(mLongClickListener);
        folder = getOpenFolder(i);
        if(folder != null)
            folder.bringToFront();
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void addView(View view)
    {
        if(!(view instanceof CellLayout))
        {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        } else
        {
            super.addView(view);
            return;
        }
    }

    public void addView(View view, int i)
    {
        if(!(view instanceof CellLayout))
        {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        } else
        {
            super.addView(view, i);
            return;
        }
    }

    public void addView(View view, int i, int j)
    {
        if(!(view instanceof CellLayout))
        {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        } else
        {
            super.addView(view, i, j);
            return;
        }
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutparams)
    {
        if(!(view instanceof CellLayout))
        {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        } else
        {
            super.addView(view, i, layoutparams);
            return;
        }
    }

    public void addView(View view, android.view.ViewGroup.LayoutParams layoutparams)
    {
        if(!(view instanceof CellLayout))
        {
            throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
        } else
        {
            super.addView(view, layoutparams);
            return;
        }
    }

    public boolean allowLongPress()
    {
        return mAllowLongPress;
    }

    void clearChildrenCache()
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            ((CellLayout)getChildAt(j)).setChildrenDrawnWithCacheEnabled(false);

        TopFourZone topfourzone = mLauncher.getTopFourZone();
        if(topfourzone != null)
            topfourzone.setDrawingCacheEnabled(false);
    }

    public void computeScroll()
    {
        if(!mScroller.computeScrollOffset()) goto _L2; else goto _L1
_L1:
        mScrollX = mScroller.getCurrX();
        mScrollY = mScroller.getCurrY();
        if(!mLauncher.isWindowOpaque())
            updateWallpaperOffset();
        postInvalidate();
_L4:
        return;
_L2:
        if(mNextScreen == -1)
            continue; /* Loop/switch isn't completed */
        mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
        if(getChildCount() >= 2)
            mCurrentScreen = getWhichScreen();
        Launcher.setScreen(mCurrentScreen);
        mLauncher.saveScreenInfo();
        mNextScreen = -1;
        clearChildrenCache();
        if(mTouchState == 1)
            break; /* Loop/switch isn't completed */
        if(mLauncher.isAddWidgetState())
        {
            mExistWidgetSpace = mLauncher.checkWidgetSpace(mCurrentScreen);
            mFixedWidgetScreen = mCurrentScreen;
        } else
        {
            resumeScreen(mCurrentScreen);
        }
_L6:
        postInvalidate();
        if(true) goto _L4; else goto _L3
_L3:
        if(!mLauncher.isAddWidgetState() || !mIsAutoScrolling) goto _L6; else goto _L5
_L5:
        mExistWidgetSpace = mLauncher.checkWidgetSpace(mCurrentScreen);
          goto _L6
    }

    protected void dispatchDraw(Canvas canvas)
    {
        canvas_factoryinfo = canvas;
        if(!mLauncher.getMenuManager().isOpened()) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i;
        if(mDelayedShortcutDisplay && mLauncher.getMenuManager().isClosing())
        {
            postInvalidate();
            continue; /* Loop/switch isn't completed */
        }
        if(mLauncher.getQuickViewWorkspace().isOpened() || mLauncher.getStateQuickNavigation() >= 0)
            continue; /* Loop/switch isn't completed */
        if(mEnablePageIndicatorShowHide && (mOpenFlag || !mScroller.isFinished()))
        {
            if(mPageIndicator != null)
                mPageIndicator.show(mOpenFlag);
            removeCallbacks(mHideIndicator);
            mOpenFlag = false;
            postDelayed(mHideIndicator, 1000L);
        }
        if(Launcher.mIsDefaultIMEI)
            drawDefaultIMEIText(canvas_factoryinfo);
        i = getChildCount();
        boolean flag;
        if(mTouchState != 1 && mNextScreen == -1)
            flag = true;
        else
            flag = false;
        if(!flag)
            break; /* Loop/switch isn't completed */
        drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
_L4:
        if(!Launcher.USE_MAINMENU_ICONMODE)
            drawPageIndicator(canvas);
        if(false)
            canvas.restore();
        if(true) goto _L1; else goto _L3
_L3:
        int j = mCurrentScreen - mNextScreen;
        long l = getDrawingTime();
        if(mNextScreen >= 0 && mNextScreen < getChildCount() && Math.abs(j) == 1)
        {
            if(j > 0)
            {
                if(mCurrentScreen < i - 1)
                    drawChild(canvas, getChildAt(1 + mCurrentScreen), l);
            } else
            if(mCurrentScreen > 0)
                drawChild(canvas, getChildAt(mCurrentScreen - 1), l);
            drawChild(canvas, getChildAt(mCurrentScreen), l);
            drawChild(canvas, getChildAt(mNextScreen), l);
        } else
        {
            int k = getChildCount();
            int i1 = 0;
            while(i1 < k) 
            {
                drawChild(canvas, getChildAt(i1), l);
                i1++;
            }
        }
          goto _L4
        if(true) goto _L1; else goto _L5
_L5:
    }

    public boolean dispatchUnhandledMove(View view, int i)
    {
        if(i != 17) goto _L2; else goto _L1
_L1:
        if(getCurrentScreen() <= 0) goto _L4; else goto _L3
_L3:
        boolean flag;
        isScrollAble = true;
        snapToScreen(getCurrentScreen() - 1);
        isScrollAble = false;
        flag = true;
_L6:
        return flag;
_L2:
        if(i == 66 && getCurrentScreen() < getChildCount() - 1)
        {
            snapToScreen(1 + getCurrentScreen());
            flag = true;
            continue; /* Loop/switch isn't completed */
        }
_L4:
        flag = super.dispatchUnhandledMove(view, i);
        if(true) goto _L6; else goto _L5
_L5:
    }

    void drawPageIndicatorExternal(Canvas canvas)
    {
        if(Launcher.USE_MAINMENU_ICONMODE && !mLauncher.getQuickViewWorkspace().isOpened() && !mLauncher.getQuickViewMainMenu().isOpened() && mLauncher.getStateQuickNavigation() < 0 && mLauncher.getMenuDrawer().getVisibility() != 0)
        {
            int i = canvas.save();
            canvas.translate(-mScrollX, 0.0F);
            drawPageIndicator(canvas);
            canvas.restoreToCount(i);
        }
    }

    public void drawWallpaperImage(Canvas canvas, Rect rect)
    {
        int i;
        Drawable drawable;
        if(mLauncher.getCurrentImageWallpaperDrawable() != null)
            i = mLauncher.getCurrentImageWallpaperDrawable().getIntrinsicWidth();
        else
            i = 1;
        drawable = mLauncher.getCurrentImageWallpaperDrawable();
        if(drawable != null)
        {
            mTmpRect.set(rect);
            mTmpRect.left = 0;
            mTmpRect.right = i;
            drawable.setBounds(mTmpRect);
            int j = getWidth() * (getChildCount() - 1);
            int k = mScrollX;
            if(k < 0)
                k = 0;
            if(k > j)
                k = j;
            int l = canvas.save();
            int i1 = i - rect.width();
            float f = 0.0F;
            if(getChildCount() > 1)
                f = (float)(-i1) * ((float)k / (float)j);
            boolean flag = false;
            if(i1 <= 0)
            {
                flag = true;
                f = -0.5F * (float)i1;
            }
            if(drawable.getIntrinsicHeight() < rect.height())
                flag = true;
            if(flag)
                canvas.drawColor(0xff000000);
            canvas.translate(f, 0.0F);
            drawable.draw(canvas);
            canvas.restoreToCount(l);
        }
    }

    void enableChildrenCache(int i, int j)
    {
        if(i > j)
        {
            i = j;
            j = i;
        }
        int k = getChildCount();
        int l = Math.max(i, 0);
        int i1 = Math.min(j, k - 1);
        for(int j1 = l; j1 <= i1; j1++)
        {
            CellLayout celllayout = (CellLayout)getChildAt(j1);
            celllayout.setChildrenDrawnWithCacheEnabled(true);
            celllayout.setChildrenDrawingCacheEnabled(true);
        }

        TopFourZone topfourzone = mLauncher.getTopFourZone();
        if(topfourzone != null)
        {
            topfourzone.setDrawingCacheEnabled(true);
            topfourzone.buildDrawingCache(true);
        }
    }

    CellLayout.CellInfo findAllVacantCells(boolean aflag[])
    {
        CellLayout celllayout = (CellLayout)getChildAt(mCurrentScreen);
        CellLayout.CellInfo cellinfo;
        if(celllayout != null)
            cellinfo = celllayout.findAllVacantCells(aflag, null);
        else
            cellinfo = null;
        return cellinfo;
    }

    CellLayout.CellInfo findAllVacantCellsFromModel()
    {
        CellLayout celllayout = (CellLayout)getChildAt(mCurrentScreen);
        CellLayout.CellInfo cellinfo;
        if(celllayout != null)
        {
            int i = celllayout.getCountX();
            int j = celllayout.getCountY();
            int ai[] = new int[2];
            ai[0] = i;
            ai[1] = j;
            boolean aflag[][] = (boolean[][])Array.newInstance(Boolean.TYPE, ai);
            mLauncher.findAllOccupiedCells(aflag, i, j, mCurrentScreen);
            cellinfo = celllayout.findAllVacantCellsFromOccupied(aflag, i, j);
        } else
        {
            cellinfo = null;
        }
        return cellinfo;
    }

    int getCurrentScreen()
    {
        return mCurrentScreen;
    }

    public Folder getFolderForTag(Object obj)
    {
        int i;
        int j;
        i = getChildCount();
        j = 0;
_L4:
        CellLayout celllayout;
        int k;
        int l;
        if(j >= i)
            break MISSING_BLOCK_LABEL_127;
        celllayout = (CellLayout)getChildAt(j);
        k = celllayout.getChildCount();
        l = 0;
_L3:
        if(l >= k) goto _L2; else goto _L1
_L1:
        Folder folder;
        View view = celllayout.getChildAt(l);
        CellLayout.LayoutParams layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
        if(layoutparams.cellHSpan != celllayout.getShortAxisCells() || layoutparams.cellVSpan != celllayout.getLongAxisCells() || !(view instanceof Folder))
            continue; /* Loop/switch isn't completed */
        Folder folder1 = (Folder)view;
        if(folder1.getInfo() != obj)
            continue; /* Loop/switch isn't completed */
        folder = folder1;
_L5:
        return folder;
        l++;
          goto _L3
_L2:
        j++;
          goto _L4
        folder = null;
          goto _L5
    }

    Folder getOpenFolder()
    {
        CellLayout celllayout;
        Folder folder;
        int i;
        if(mScroller.isFinished())
            i = mCurrentScreen;
        else
            i = mNextScreen;
        if(i == -1)
            celllayout = (CellLayout)getChildAt(mCurrentScreen);
        else
            celllayout = (CellLayout)getChildAt(i);
        if(celllayout != null) goto _L2; else goto _L1
_L1:
        folder = null;
_L4:
        return folder;
_L2:
        int j = celllayout.getChildCount();
        int k = 0;
        do
        {
            if(k >= j)
                break;
            View view = celllayout.getChildAt(k);
            CellLayout.LayoutParams layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
            if(layoutparams.cellHSpan == celllayout.getShortAxisCells() && layoutparams.cellVSpan == celllayout.getLongAxisCells() && (view instanceof Folder))
            {
                folder = (Folder)view;
                continue; /* Loop/switch isn't completed */
            }
            k++;
        } while(true);
        folder = null;
        if(true) goto _L4; else goto _L3
_L3:
    }

    Folder getOpenFolder(int i)
    {
        CellLayout celllayout = (CellLayout)getChildAt(i);
        if(celllayout != null) goto _L2; else goto _L1
_L1:
        Folder folder = null;
_L4:
        return folder;
_L2:
        int j = celllayout.getChildCount();
        int k = 0;
        do
        {
            if(k >= j)
                break;
            View view = celllayout.getChildAt(k);
            CellLayout.LayoutParams layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
            if(layoutparams.cellHSpan == celllayout.getShortAxisCells() && layoutparams.cellVSpan == celllayout.getLongAxisCells() && (view instanceof Folder))
            {
                folder = (Folder)view;
                continue; /* Loop/switch isn't completed */
            }
            k++;
        } while(true);
        folder = null;
        if(true) goto _L4; else goto _L3
_L3:
    }

    ArrayList getOpenFolders()
    {
        int i = getChildCount();
        ArrayList arraylist = new ArrayList(i);
        int j = 0;
label0:
        do
        {
            if(j < i)
            {
                CellLayout celllayout = (CellLayout)getChildAt(j);
                int k = celllayout.getChildCount();
                int l = 0;
                do
                {
label1:
                    {
                        if(l < k)
                        {
                            View view = celllayout.getChildAt(l);
                            CellLayout.LayoutParams layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
                            if(layoutparams.cellHSpan != celllayout.getShortAxisCells() || layoutparams.cellVSpan != celllayout.getLongAxisCells() || !(view instanceof Folder))
                                break label1;
                            arraylist.add((Folder)view);
                        }
                        j++;
                        continue label0;
                    }
                    l++;
                } while(true);
            }
            return arraylist;
        } while(true);
    }

    int getPageIndicatorArea(int i, int j)
    {
        int k;
        if(mPageIndicator == null)
            k = -1;
        else
            k = mPageIndicator.getPageTouchArea(i, j);
        return k;
    }

    public int getScreenForView(View view)
    {
        android.view.ViewParent viewparent;
        int j;
        int k;
        if(view == null)
            break MISSING_BLOCK_LABEL_46;
        viewparent = view.getParent();
        j = getChildCount();
        k = 0;
_L3:
        if(k >= j)
            break MISSING_BLOCK_LABEL_46;
        if(viewparent != getChildAt(k)) goto _L2; else goto _L1
_L1:
        int i = k;
_L4:
        return i;
_L2:
        k++;
          goto _L3
        i = -1;
          goto _L4
    }

    public View getViewForTag(Object obj)
    {
        int i;
        int j;
        i = getChildCount();
        j = 0;
_L6:
        CellLayout celllayout;
        int k;
        int l;
        if(j >= i)
            break MISSING_BLOCK_LABEL_76;
        celllayout = (CellLayout)getChildAt(j);
        k = celllayout.getChildCount();
        l = 0;
_L5:
        if(l >= k) goto _L2; else goto _L1
_L1:
        View view1 = celllayout.getChildAt(l);
        if(view1.getTag() != obj) goto _L4; else goto _L3
_L3:
        View view = view1;
_L7:
        return view;
_L4:
        l++;
          goto _L5
_L2:
        j++;
          goto _L6
        view = null;
          goto _L7
    }

    void initAddWidget()
    {
        mExistWidgetSpace = false;
    }

    public boolean isOpaque()
    {
        return false;
    }

    void moveToDefaultScreen()
    {
        snapToScreen(mDefaultScreen, mSnapToScreenDuration);
        getChildAt(mDefaultScreen).requestFocus();
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        clearVacantCache();
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        clearVacantCache();
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        CellLayout celllayout;
        if(mLauncher.getQuickViewWorkspace().isOpened())
            mLauncher.closeQuickViewWorkspace();
        celllayout = getCurrentDropLayout();
        if(dragsource == this) goto _L2; else goto _L1
_L1:
        onDropExternal(dragsource, i - k, j - l, obj, celllayout);
_L6:
        if(Launcher.USE_MAINMENU_ICONMODE)
            mLauncher.getTopFourZone().setVisibility(0);
_L4:
        return;
_L2:
        if(mDragInfo == null)
            continue; /* Loop/switch isn't completed */
        View view = mDragInfo.cell;
        int i1;
        ItemInfo iteminfo;
        CellLayout.LayoutParams layoutparams;
        Folder folder;
        if(mScroller.isFinished())
            i1 = mCurrentScreen;
        else
            i1 = mNextScreen;
        if(i1 != mDragInfo.screen)
        {
            CellLayout celllayout1 = (CellLayout)getChildAt(mDragInfo.screen);
            celllayout1.removeView(view);
            if(view != null && view.getParent() != null)
            {
                Log.w("Launcher.Workspace", (new StringBuilder()).append("onDrop()  both cell and cell parent are NULL : originalCellLayout=").append(celllayout1).append(" screen=").append(mDragInfo.screen).append("cell.getParent()=").append(view.getParent()).toString());
                if(view.getTag() != null)
                {
                    ItemInfo iteminfo1 = (ItemInfo)view.getTag();
                    Log.w("Launcher.Workspace", (new StringBuilder()).append("onDrop() cell screen=").append(iteminfo1.screen).toString());
                }
                ((ViewGroup)view.getParent()).removeView(view);
            }
            celllayout.addView(view);
        }
        mTargetCell = estimateDropCell(i - k, j - l, mDragInfo.spanX, mDragInfo.spanY, view, celllayout, mTargetCell);
        if(mTargetCell == null) goto _L4; else goto _L3
_L3:
        celllayout.onDropChild(view, mTargetCell);
        iteminfo = (ItemInfo)view.getTag();
        layoutparams = (CellLayout.LayoutParams)view.getLayoutParams();
        LauncherModel.moveItemInDatabase(mLauncher, iteminfo, -100L, i1, layoutparams.cellX, layoutparams.cellY);
        if(iteminfo instanceof SamsungAppWidgetInfo)
            mLauncher.getSamsungWidgetPackageManager().resumeWidget(mLauncher, (SamsungAppWidgetInfo)iteminfo);
        folder = getOpenFolder();
        if(folder != null)
            folder.bringToFront();
        if(true) goto _L6; else goto _L5
_L5:
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
        clearVacantCache();
        if(!flag) goto _L2; else goto _L1
_L1:
        if(view != this && mDragInfo != null)
            ((CellLayout)getChildAt(mDragInfo.screen)).removeView(mDragInfo.cell);
_L4:
        mDragInfo = null;
        return;
_L2:
        if(mDragInfo != null)
            ((CellLayout)getChildAt(mDragInfo.screen)).onDropAborted(mDragInfo.cell);
        if(true) goto _L4; else goto _L3
_L3:
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag2;
        int i;
        boolean flag = mLauncher.isWorkspaceLocked();
        boolean flag1 = mLauncher.isAllAppsVisible();
        if(flag || flag1)
        {
            flag2 = false;
        } else
        {
label0:
            {
                if(mVelocityTracker == null)
                    mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionevent);
                i = motionevent.getAction();
                if(i != 2)
                    break label0;
                if(mTouchState == 1)
                {
                    flag2 = true;
                } else
                {
                    if(mTouchState != 2)
                        break label0;
                    flag2 = false;
                }
            }
        }
_L6:
        return flag2;
        float f;
        float f1;
        f = motionevent.getX();
        f1 = motionevent.getY();
        i;
        JVM INSTR lookupswitch 5: default 156
    //                   0: 504
    //                   1: 600
    //                   2: 170
    //                   3: 600
    //                   261: 735;
           goto _L1 _L2 _L3 _L4 _L3 _L5
_L1:
        int j1;
        int k1;
        if(mTouchState == 1)
            flag2 = true;
        else
            flag2 = false;
          goto _L6
_L4:
        if(!mMultiTouchUsed) goto _L8; else goto _L7
_L7:
        if(mAllowLongPress)
        {
            mAllowLongPress = false;
            getChildAt(mCurrentScreen).cancelLongPress();
        }
        mTouchState = 0;
        int k2 = (int)motionevent.getY(0);
        int l2 = (int)motionevent.getX(0);
        int i3 = (int)motionevent.getY(1);
        int j3 = (int)motionevent.getX(1);
        mMovePinch = mMovePinchStart - (int)Math.sqrt((k2 - i3) * (k2 - i3) + (l2 - j3) * (l2 - j3));
        if(!mLauncher.getQuickViewWorkspace().isOpened() && mLauncher.getStateQuickNavigation() < 0) goto _L10; else goto _L9
_L9:
        flag2 = false;
          goto _L6
_L10:
        if(mMovePinch <= 100) goto _L1; else goto _L11
_L11:
        mMultiTouchUsed = false;
        mLauncher.getQuickViewWorkspace().drawOpenAnimation();
        mLauncher.openQuickViewWorkspace();
        flag2 = true;
          goto _L6
_L8:
        int l1 = (int)Math.abs(f - mDownX);
        int i2 = (int)Math.abs(f1 - mLastMotionY);
        int j2 = mTouchSlop;
        boolean flag3;
        boolean flag4;
        if(l1 > j2)
            flag3 = true;
        else
            flag3 = false;
        if(i2 > j2)
            flag4 = true;
        else
            flag4 = false;
        if(flag3 || flag4)
        {
            if(l1 < i2 && flag4)
                mTouchState = 2;
            else
            if(flag3)
            {
                mTouchState = 1;
                enableChildrenCache(mCurrentScreen - 1, 1 + mCurrentScreen);
            } else
            {
                mLastMotionX = f;
            }
            if(mAllowLongPress)
            {
                mAllowLongPress = false;
                getChildAt(mCurrentScreen).cancelLongPress();
            }
        } else
        {
            mLastMotionX = f;
        }
          goto _L1
_L2:
        mLastMotionX = f;
        mDownX = f;
        mLastMotionY = f1;
        mAllowLongPress = true;
        if(!mScroller.isFinished() || mLauncher.isAddWidgetState()) goto _L13; else goto _L12
_L12:
        k1 = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
        mTouchedPageIndicatorIndex = k1;
        if(k1 == -1) goto _L14; else goto _L13
_L13:
        j1 = 1;
_L15:
        mTouchState = j1;
          goto _L1
_L14:
        j1 = 0;
          goto _L15
_L3:
        if(mTouchState != 1 && !((CellLayout)getChildAt(mCurrentScreen)).lastDownOnOccupiedCell())
            mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.wallpaper.tap", (int)motionevent.getX(), (int)motionevent.getY(), 0, null);
        clearChildrenCache();
        mTouchState = 0;
        mAllowLongPress = false;
        if(!mMultiTouchUsed) goto _L1; else goto _L16
_L16:
        mMultiTouchUsed = false;
        if(!mLauncher.getQuickViewWorkspace().isOpened() && mLauncher.getStateQuickNavigation() < 0) goto _L18; else goto _L17
_L17:
        flag2 = false;
          goto _L6
_L18:
        if(mMovePinch > 100)
        {
            mLauncher.getQuickViewWorkspace().drawOpenAnimation();
            mLauncher.openQuickViewWorkspace();
        }
          goto _L1
_L5:
        if(!mMultiTouchUsed)
        {
            int j = (int)motionevent.getY(0);
            int k = (int)motionevent.getX(0);
            int l = (int)motionevent.getY(1);
            int i1 = (int)motionevent.getX(1);
            mMultiTouchUsed = true;
            mMovePinchStart = (int)Math.sqrt((j - l) * (j - l) + (k - i1) * (k - i1));
            if(mAllowLongPress)
            {
                mAllowLongPress = false;
                getChildAt(mCurrentScreen).cancelLongPress();
            }
        }
          goto _L1
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = 0;
        int j1 = getChildCount();
        for(int k1 = 0; k1 < j1; k1++)
        {
            View view = getChildAt(k1);
            if(view.getVisibility() != 8)
            {
                int l1 = view.getMeasuredWidth();
                view.layout(i1, 0, i1 + l1, view.getMeasuredHeight());
                i1 += l1;
            }
        }

        initPageIndicator();
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        if(android.view.View.MeasureSpec.getMode(i) != 0x40000000)
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        if(android.view.View.MeasureSpec.getMode(j) != 0x40000000)
            throw new IllegalStateException("Workspace can only be used in EXACTLY mode.");
        int l = getChildCount();
        for(int i1 = 0; i1 < l; i1++)
            getChildAt(i1).measure(i, j);

        if(mTouchState == 0 && mScroller.isFinished() && mNextScreen == -1)
        {
            scrollTo(k * mCurrentScreen, 0);
            if(!mLauncher.isWindowOpaque())
                updateWallpaperOffset(k * (l - 1));
        }
        mIgnoreRestore = false;
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect)
    {
        if(mLauncher.getMenuManager().isOpened()) goto _L2; else goto _L1
_L1:
        Folder folder = getOpenFolder();
        if(folder == null) goto _L4; else goto _L3
_L3:
        boolean flag = folder.requestFocus(i, rect);
_L6:
        return flag;
_L4:
        int j;
        View view;
        if(mNextScreen != -1)
            j = mNextScreen;
        else
            j = mCurrentScreen;
        view = getChildAt(j);
        if(view != null)
            view.requestFocus(i, rect);
_L2:
        flag = false;
        if(true) goto _L6; else goto _L5
_L5:
    }

    protected void onRestoreInstanceState(Parcelable parcelable)
    {
        super.onRestoreInstanceState(((AbsSavedState)parcelable).getSuperState());
        if(parcelable instanceof SavedState) goto _L2; else goto _L1
_L1:
        return;
_L2:
        SavedState savedstate = (SavedState)parcelable;
        if(savedstate.currentScreen != -1 && !mIgnoreRestore)
        {
            mCurrentScreen = savedstate.currentScreen;
            Launcher.setScreen(mCurrentScreen);
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    protected Parcelable onSaveInstanceState()
    {
        SavedState savedstate = new SavedState(super.onSaveInstanceState());
        savedstate.currentScreen = mCurrentScreen;
        return savedstate;
    }

    protected void onSizeChanged(int i, int j, int k, int l)
    {
        Log.v("Launcher.Workspace", (new StringBuilder()).append("onSizeChanged( ").append(i).append(",").append(j).append(",").append(k).append(",").append(l).append(")").append("getWidth() = ").append(getWidth()).append("getHeight() = ").append(getHeight()).toString());
        if(getWidth() < getHeight())
        {
            mDefaultIMEIPosX = getResources().getDimensionPixelSize(0x7f09002b);
            mDefaultIMEIPosY = getResources().getDimensionPixelSize(0x7f09002c);
        } else
        {
            mDefaultIMEIPosX = getResources().getDimensionPixelSize(0x7f09002e);
            mDefaultIMEIPosY = getResources().getDimensionPixelSize(0x7f09002f) - 20;
        }
        mDefaultIMEIPosYGap = getResources().getDimensionPixelSize(0x7f09002d);
        super.onSizeChanged(i, j, k, l);
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        if(!isScrollAble)
            isScrollAble = true;
        if(!mLauncher.isWorkspaceLocked() && !mLauncher.isAllAppsVisible()) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L9:
        return flag;
_L2:
        int i;
        float f;
        float f1;
        if(mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(motionevent);
        i = motionevent.getAction();
        f = motionevent.getX();
        f1 = motionevent.getY();
        i;
        JVM INSTR tableswitch 0 3: default 104
    //                   0 109
    //                   1 501
    //                   2 212
    //                   3 929;
           goto _L3 _L4 _L5 _L6 _L7
_L4:
        break; /* Loop/switch isn't completed */
_L3:
        break; /* Loop/switch isn't completed */
_L7:
        break MISSING_BLOCK_LABEL_929;
_L10:
        flag = true;
        if(true) goto _L9; else goto _L8
_L8:
        if(!mIsAutoScrolling && !mScroller.isFinished())
        {
            mScroller.abortAnimation();
        } else
        {
            if(mLauncher.isAddWidgetState())
                mIsSingleTap = true;
            int i2 = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
            mTouchedPageIndicatorIndex = i2;
            if(i2 != -1)
                mIsSingleTap = true;
        }
        mLastMotionX = f;
        mDownX = f;
        mLastMotionY = f1;
          goto _L10
_L6:
        if(mTouchState == 1)
        {
label0:
            {
                if(!mIsAutoScrolling)
                    break label0;
                mIsSingleTap = true;
            }
        }
          goto _L10
        int i1;
        int j1;
        i1 = (int)(mLastMotionX - f);
        j1 = (int)(mLastMotionY - f1);
        if(!mIsSingleTap)
            break MISSING_BLOCK_LABEL_291;
        if(Math.abs(i1) <= mTouchSlop && Math.abs(j1) <= mTouchSlop) goto _L10; else goto _L11
_L11:
        mIsSingleTap = false;
        if(mPageIndicator != null)
        {
            mPageIndicator.show();
            removeCallbacks(mHideIndicator);
        }
        mLastMotionX = f;
        (getChildCount() - 1) * getWidth();
        if(i1 < 0 && mCurrentScreen != 0)
        {
            int l1 = -(getWidth() / 2);
            if(mScrollX > l1)
            {
                if(mScrollX > 0)
                    scrollBy(i1, 0);
                else
                    scrollBy(Math.max(l1 - mScrollX, i1), 0);
                if(!mLauncher.isWindowOpaque())
                    updateWallpaperOffset();
            }
        } else
        if(i1 > 0 && mCurrentScreen != getChildCount() - 1)
        {
            int k1 = (getChildAt(getChildCount() - 1).getRight() - getWidth()) + getWidth() / 2;
            if(mScrollX < k1)
            {
                scrollBy(Math.min(k1 - mScrollX, i1), 0);
                if(!mLauncher.isWindowOpaque())
                    updateWallpaperOffset();
            }
        }
          goto _L10
_L5:
        if(mTouchState == 1)
        {
            if(mIsSingleTap)
            {
                int l = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
                if(mLauncher.isAddWidgetState() && mIsAutoScrolling)
                {
                    if(!mExistWidgetSpace)
                    {
                        removeCallbacks(mAutoScrollRunnable);
                        mIsAutoScrolling = false;
                        if(!mScroller.isFinished())
                            mScroller.abortAnimation();
                        snapToScreen(mStartScreen);
                        mLauncher.cancelAddWidget();
                        mEndScreen = -1;
                    }
                } else
                if(l != -1 && l == mTouchedPageIndicatorIndex)
                    snapToScreen(l);
                else
                if(mLauncher.isAddWidgetState() && mScroller.isFinished())
                {
                    Log.e("hwan", (new StringBuilder()).append("mFixedWidgetScreen =").append(mFixedWidgetScreen).append(", mCurrentScreen =").append(mCurrentScreen).toString());
                    if(mExistWidgetSpace && mFixedWidgetScreen == mCurrentScreen)
                    {
                        mLauncher.completeAddWidget(mCurrentScreen);
                    } else
                    {
                        removeCallbacks(mAutoScrollRunnable);
                        mIsAutoScrolling = false;
                        if(!mScroller.isFinished())
                            mScroller.abortAnimation();
                        snapToScreen(mStartScreen);
                        mLauncher.cancelAddWidget();
                        mEndScreen = -1;
                    }
                }
                mIsSingleTap = false;
            } else
            {
                VelocityTracker velocitytracker = mVelocityTracker;
                velocitytracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int j = (int)velocitytracker.getXVelocity();
                int k = getChildCount();
                if(j > SNAP_VELOCITY && mCurrentScreen >= 0)
                    snapToScreen(mCurrentScreen - 1);
                else
                if(j < -SNAP_VELOCITY && mCurrentScreen <= k - 1)
                    snapToScreen(1 + mCurrentScreen);
                else
                    snapToDestination();
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        mTouchState = 0;
        postDelayed(mHideIndicator, 1000L);
          goto _L10
        mTouchState = 0;
        postDelayed(mHideIndicator, 1000L);
          goto _L10
    }

    void pauseScreen(int i)
    {
        CellLayout celllayout = (CellLayout)getChildAt(i);
        if(celllayout != null)
        {
            int j = celllayout.getChildCount();
            int k = 0;
            while(k < j) 
            {
                Object obj = celllayout.getChildAt(k).getTag();
                if(obj instanceof SamsungAppWidgetInfo)
                    mLauncher.getSamsungWidgetPackageManager().pauseWidget(mLauncher, (SamsungAppWidgetInfo)obj);
                k++;
            }
        }
    }

    void removeInScreen(View view, int i)
    {
        if(i < 0 || i >= getChildCount())
            throw new IllegalStateException((new StringBuilder()).append("The screen must be >= 0 and < ").append(getChildCount()).toString());
        CellLayout celllayout = (CellLayout)getChildAt(i);
        ViewGroup viewgroup = (ViewGroup)view.getParent();
        if(viewgroup != null)
            viewgroup.removeView(view);
        Log.d("Workspace", (new StringBuilder()).append("removeInScreen group:").append(celllayout).append("  child:").append(view).append("  screen:").append(i).toString());
    }

    void removeItems(ArrayList arraylist)
    {
        int i = getChildCount();
        PackageManager packagemanager = getContext().getPackageManager();
        AppWidgetManager appwidgetmanager = AppWidgetManager.getInstance(getContext());
        HashSet hashset = new HashSet();
        int j = arraylist.size();
        for(int k = 0; k < j; k++)
            hashset.add(((ApplicationInfo)arraylist.get(k)).componentName.getPackageName());

        for(int l = 0; l < i; l++)
            post(new Runnable() {

                public void run()
                {
                    ArrayList arraylist1;
                    int i1;
                    int j1;
                    arraylist1 = new ArrayList();
                    arraylist1.clear();
                    i1 = layout.getChildCount();
                    j1 = 0;
_L1:
                    View view1;
                    Object obj;
                    if(j1 >= i1)
                        break MISSING_BLOCK_LABEL_654;
                    view1 = layout.getChildAt(j1);
                    obj = view1.getTag();
                    if(obj instanceof ShortcutInfo)
                    {
                        ShortcutInfo shortcutinfo1 = (ShortcutInfo)obj;
                        Intent intent1 = shortcutinfo1.intent;
                        ComponentName componentname1 = intent1.getComponent();
                        if("android.intent.action.MAIN".equals(intent1.getAction()) && componentname1 != null)
                        {
                            Iterator iterator4 = packageNames.iterator();
                            do
                            {
                                if(!iterator4.hasNext())
                                    break;
                                if(((String)iterator4.next()).equals(componentname1.getPackageName()))
                                {
                                    LauncherModel.deleteItemFromDatabase(mLauncher, shortcutinfo1);
                                    arraylist1.add(view1);
                                }
                            } while(true);
                        }
                    } else
                    if(obj instanceof UserFolderInfo)
                    {
                        ArrayList arraylist2 = ((UserFolderInfo)obj).contents;
                        ArrayList arraylist3 = new ArrayList(1);
                        int i2 = arraylist2.size();
                        boolean flag = false;
label0:
                        for(int j2 = 0; j2 < i2; j2++)
                        {
                            ShortcutInfo shortcutinfo = (ShortcutInfo)arraylist2.get(j2);
                            Intent intent = shortcutinfo.intent;
                            ComponentName componentname = intent.getComponent();
                            if(!"android.intent.action.MAIN".equals(intent.getAction()) || componentname == null)
                                continue;
                            Iterator iterator3 = packageNames.iterator();
                            do
                            {
                                do
                                    if(!iterator3.hasNext())
                                        continue label0;
                                while(!((String)iterator3.next()).equals(componentname.getPackageName()));
                                arraylist3.add(shortcutinfo);
                                LauncherModel.deleteItemFromDatabase(mLauncher, shortcutinfo);
                                flag = true;
                            } while(true);
                        }

                        arraylist2.removeAll(arraylist3);
                        if(flag)
                        {
                            Folder folder = getOpenFolder();
                            if(folder != null)
                                folder.notifyDataSetChanged();
                        }
                    } else
                    if(obj instanceof LiveFolderInfo)
                    {
                        LiveFolderInfo livefolderinfo = (LiveFolderInfo)obj;
                        Uri uri = livefolderinfo.uri;
                        ProviderInfo providerinfo = manager.resolveContentProvider(uri.getAuthority(), 0);
                        if(providerinfo != null)
                        {
                            Iterator iterator2 = packageNames.iterator();
                            while(iterator2.hasNext()) 
                                if(((String)iterator2.next()).equals(providerinfo.packageName))
                                {
                                    LauncherModel.deleteItemFromDatabase(mLauncher, livefolderinfo);
                                    arraylist1.add(view1);
                                }
                        }
                    } else
                    {
                        if(!(obj instanceof LauncherAppWidgetInfo))
                            continue; /* Loop/switch isn't completed */
                        LauncherAppWidgetInfo launcherappwidgetinfo = (LauncherAppWidgetInfo)obj;
                        AppWidgetProviderInfo appwidgetproviderinfo = widgets.getAppWidgetInfo(launcherappwidgetinfo.appWidgetId);
                        if(appwidgetproviderinfo != null)
                        {
                            Iterator iterator1 = packageNames.iterator();
                            while(iterator1.hasNext()) 
                                if(((String)iterator1.next()).equals(appwidgetproviderinfo.provider.getPackageName()))
                                {
                                    LauncherModel.deleteItemFromDatabase(mLauncher, launcherappwidgetinfo);
                                    arraylist1.add(view1);
                                }
                        }
                    }
_L3:
                    j1++;
                      goto _L1
                    if(!(obj instanceof SamsungAppWidgetInfo)) goto _L3; else goto _L2
_L2:
                    SamsungAppWidgetInfo samsungappwidgetinfo = (SamsungAppWidgetInfo)obj;
                    SamsungWidgetPackageManager samsungwidgetpackagemanager = SamsungWidgetPackageManager.getInstance();
                    Iterator iterator = packageNames.iterator();
                    while(iterator.hasNext()) 
                        if(((String)iterator.next()).equals(((ItemInfo) (samsungappwidgetinfo)).packageName))
                        {
                            samsungwidgetpackagemanager.destroyWidget(mLauncher, samsungappwidgetinfo);
                            LauncherModel.deleteItemFromDatabase(mLauncher, samsungappwidgetinfo);
                            arraylist1.add(view1);
                        }
                      goto _L3
                    int k1 = arraylist1.size();
                    for(int l1 = 0; l1 < k1; l1++)
                    {
                        View view = (View)arraylist1.get(l1);
                        layout.removeViewInLayout(view);
                    }

                    if(k1 > 0)
                    {
                        layout.requestLayout();
                        layout.invalidate();
                    }
                    return;
                }

                final Workspace this$0;
                final CellLayout val$layout;
                final PackageManager val$manager;
                final HashSet val$packageNames;
                final AppWidgetManager val$widgets;

            
            {
                this$0 = Workspace.this;
                layout = celllayout;
                packageNames = hashset;
                manager = packagemanager;
                widgets = appwidgetmanager;
                super();
            }
            }
);

    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean flag)
    {
        int i = indexOfChild(view);
        boolean flag1;
        if(i != mCurrentScreen || !mScroller.isFinished())
        {
            if(!mLauncher.isWorkspaceLocked())
                if(!mLauncher.isHomeKeyToDefaultPage)
                    snapToScreen(i);
                else
                    mLauncher.isHomeKeyToDefaultPage = false;
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        return flag1;
    }

    void resume(int i)
    {
        int j = getWidth();
        if(j != 0 && i >= 0)
        {
            Scroller scroller = mScroller;
            if(scroller != null && !scroller.isFinished())
                scroller.abortAnimation();
            scrollTo(i * j, 0);
        }
    }

    void resumeScreen(int i)
    {
        CellLayout celllayout = (CellLayout)getChildAt(i);
        if(celllayout != null)
        {
            int j = celllayout.getChildCount();
            int k = 0;
            while(k < j) 
            {
                Object obj = celllayout.getChildAt(k).getTag();
                if(obj instanceof SamsungAppWidgetInfo)
                    mLauncher.getSamsungWidgetPackageManager().resumeWidget(mLauncher, (SamsungAppWidgetInfo)obj);
                k++;
            }
        }
    }

    public void scrollLeft()
    {
        clearVacantCache();
        if(mNextScreen == -1 && mCurrentScreen > 0 && mScroller.isFinished())
            snapToScreen(mCurrentScreen - 1);
    }

    public void scrollRight()
    {
        clearVacantCache();
        if(mNextScreen == -1 && mCurrentScreen < getChildCount() - 1 && mScroller.isFinished())
            snapToScreen(1 + mCurrentScreen);
    }

    public void setAllowLongPress(boolean flag)
    {
        mAllowLongPress = flag;
    }

    void setAtuoScrollScreen(int i)
    {
        mStartScreen = mCurrentScreen;
        mEndScreen = i;
        mScrollDirection = mEndScreen - mStartScreen;
        mIsAutoScrolling = true;
        postDelayed(mAutoScrollRunnable, 1000L);
    }

    void setCurrentScreen(int i)
    {
        clearVacantCache();
        mCurrentScreen = Math.max(0, Math.min(i, getChildCount() - 1));
        scrollTo(mCurrentScreen * getWidth(), 0);
        invalidate();
    }

    public void setDragger(DragController dragcontroller)
    {
        mDragger = dragcontroller;
    }

    void setFastAtuoScrollScreen(int i)
    {
        mEndScreen = i;
        post(new Runnable() {

            public void run()
            {
                if(mEndScreen <= getChildCount() - 1 && mEndScreen >= 0)
                    snapToScreen(mEndScreen, 300);
            }

            final Workspace this$0;

            
            {
                this$0 = Workspace.this;
                super();
            }
        }
);
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    public void setOnLongClickListener(android.view.View.OnLongClickListener onlongclicklistener)
    {
        mLongClickListener = onlongclicklistener;
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).setOnLongClickListener(onlongclicklistener);

    }

    public void setShowIndicator()
    {
        mOpenFlag = true;
    }

    void snapToScreen(int i)
    {
        snapToScreen(i, 0);
    }

    void snapToScreen(int i, int j)
    {
        clearVacantCache();
        enableChildrenCache(mCurrentScreen, i);
        int k = Math.max(0, Math.min(i, getChildCount() - 1));
        boolean flag;
        View view;
        int l;
        if(k != mCurrentScreen)
            flag = true;
        else
            flag = false;
        if(flag)
            pauseScreen(mCurrentScreen);
        view = getFocusedChild();
        if(view != null && flag && view == getChildAt(mCurrentScreen))
            view.clearFocus();
        l = k * getWidth() - mScrollX;
        mNextScreen = k;
        if(j <= 0)
            j = 2 * Math.abs(l);
        mScroller.startScroll(mScrollX, 0, l, 0, j);
        invalidate();
    }

    void startDrag(CellLayout.CellInfo cellinfo)
    {
        View view;
        view = cellinfo.cell;
        break MISSING_BLOCK_LABEL_5;
        if(view.isInTouchMode() && view.getTag() != null && (view.getTag() instanceof ItemInfo))
        {
            ItemInfo iteminfo = (ItemInfo)view.getTag();
            if(iteminfo instanceof SamsungAppWidgetInfo)
                mLauncher.getSamsungWidgetPackageManager().pauseWidget(mLauncher, (SamsungAppWidgetInfo)iteminfo);
            clearVacantCache();
            mDragInfo = cellinfo;
            mDragInfo.screen = mCurrentScreen;
            CellLayout celllayout = (CellLayout)getChildAt(mCurrentScreen);
            if(Launcher.USE_MAINMENU_ICONMODE)
                mLauncher.getTopFourZone().setVisibility(4);
            celllayout.onDragChild(view);
            mDragger.startDrag(view, this, iteminfo, 0);
            invalidate();
        }
        return;
    }

    void stopAutoScrollRunnable()
    {
        removeCallbacks(mAutoScrollRunnable);
        mIsAutoScrolling = false;
    }

    void updateShortcuts(ArrayList arraylist)
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
        {
            CellLayout celllayout = (CellLayout)getChildAt(j);
            int k = celllayout.getChildCount();
            for(int l = 0; l < k; l++)
            {
                View view = celllayout.getChildAt(l);
                Object obj = view.getTag();
                if(!(obj instanceof ShortcutInfo))
                    continue;
                ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
                Intent intent = shortcutinfo.intent;
                ComponentName componentname = intent.getComponent();
                if(((ItemInfo) (shortcutinfo)).itemType != 0 || !"android.intent.action.MAIN".equals(intent.getAction()) || componentname == null)
                    continue;
                int i1 = arraylist.size();
                for(int j1 = 0; j1 < i1; j1++)
                    if(((ApplicationInfo)arraylist.get(j1)).componentName.equals(componentname))
                    {
                        shortcutinfo.setIcon(mIconCache.getIcon(shortcutinfo.intent));
                        ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(shortcutinfo.getIcon(mIconCache)), null, null);
                    }

            }

        }

    }

    void updateWallpaperOffset()
    {
        updateWallpaperOffset((getChildCount() - 1) * getWidth());
    }

    public static boolean isScrollAble = true;
    private int SNAP_VELOCITY;
    private Canvas canvas_factoryinfo;
    private boolean mAllowLongPress;
    private Runnable mAutoScrollRunnable = new Runnable() ;
    final Rect mClipBounds;
    private int mCurrentScreen;
    private int mDefaultIMEIPosX;
    private int mDefaultIMEIPosY;
    private int mDefaultIMEIPosYGap;
    private int mDefaultScreen;
    private final boolean mDelayedShortcutDisplay;
    private final Paint mDimPaint;
    private float mDownX;
    private CellLayout.CellInfo mDragInfo;
    private DragController mDragger;
    final Rect mDrawerBounds;
    private boolean mEnablePageIndicatorShowHide;
    private int mEndScreen;
    private boolean mExistWidgetSpace;
    private int mFixedWidgetScreen;
    private final Runnable mHideIndicator;
    private Paint mIMEITextPaint;
    private Paint mIMEITextPaint_background;
    private IconCache mIconCache;
    private boolean mIgnoreRestore;
    private boolean mIsAutoScrolling;
    private boolean mIsSingleTap;
    private float mLastMotionX;
    private float mLastMotionY;
    private Launcher mLauncher;
    private android.view.View.OnLongClickListener mLongClickListener;
    private int mMaximumVelocity;
    private int mMovePinch;
    private int mMovePinchStart;
    private boolean mMultiTouchUsed;
    private int mNextScreen;
    private boolean mOpenFlag;
    private int mOrientation;
    private PageIndicator mPageIndicator;
    private int mPageIndicatorGap;
    private Drawable mPageIndicatorLarge;
    private int mPageIndicatorLeft;
    private Drawable mPageIndicatorMiddle;
    private int mPageIndicatorOffsetY;
    private Drawable mPageIndicatorSmall;
    private int mPageIndicatorTop;
    private int mScrollDirection;
    private Scroller mScroller;
    private int mSnapToScreenDuration;
    private int mStartScreen;
    private int mTargetCell[];
    private int mTempCell[];
    private int mTempEstimate[];
    private int mTextSize;
    private Rect mTmpRect;
    private int mTouchSlop;
    private int mTouchState;
    private int mTouchedPageIndicatorIndex;
    private boolean mUseLargeDrawablesOnlyForPageIndicator;
    private CellLayout.CellInfo mVacantCache;
    private VelocityTracker mVelocityTracker;
    private final WallpaperManager mWallpaperManager;








/*
    static int access$508(Workspace workspace)
    {
        int i = workspace.mScrollDirection;
        workspace.mScrollDirection = i + 1;
        return i;
    }

*/


/*
    static int access$510(Workspace workspace)
    {
        int i = workspace.mScrollDirection;
        workspace.mScrollDirection = i - 1;
        return i;
    }

*/


/*
    static int access$602(Workspace workspace, int i)
    {
        workspace.mTouchState = i;
        return i;
    }

*/


/*
    static boolean access$702(Workspace workspace, boolean flag)
    {
        workspace.mIsAutoScrolling = flag;
        return flag;
    }

*/
}
