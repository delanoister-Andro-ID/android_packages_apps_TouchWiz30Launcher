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
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import java.io.File;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragScroller, DragSource, LauncherConfig, ApplicationsAdapter, 
//            ApplicationInfo, Launcher, PageIndicator, AppMenu, 
//            Rotate3dAnimation, AppShortcutZone, ApplicationsListAdapter, LauncherModel, 
//            SamsungUtils, MenuDrawer, TopFourZone, Workspace, 
//            QuickViewMainMenu, QuickViewWorkspace, DragLayer, DragController, 
//            MenuItemView, LauncherApplication, BadgeCache

public class MenuManager extends ViewGroup
    implements DragController.DragListener, DragScroller, DragSource
{
    public static interface InterruptableAnimationListener
        extends android.view.animation.Animation.AnimationListener
    {

        public abstract void interrupt();
    }

    private class MenuCloseAnimationController extends LayoutAnimationController
    {

        protected long getDelayForView(View view)
        {
            android.view.animation.LayoutAnimationController.AnimationParameters animationparameters = view.getLayoutParams().layoutAnimationParameters;
            mAnimation = mCloseAnimations[animationparameters.index];
            mAnimation.setDuration(400L);
            return super.getDelayForView(view);
        }

        final MenuManager this$0;

        public MenuCloseAnimationController()
        {
            this$0 = MenuManager.this;
            super(new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F));
        }
    }

    private class MenuOpenAnimationController extends LayoutAnimationController
    {

        private void initAnimations()
        {
            Integer ainteger[] = new Integer[mItemNumOfPage];
            for(int i = 0; i < mItemNumOfPage; i++)
                ainteger[i] = Integer.valueOf(i);

            mAniIndexSelector = Arrays.asList(ainteger);
            Collections.shuffle(mAniIndexSelector);
        }

        protected long getDelayForView(View view)
        {
            long l;
            if(Launcher.USE_MAINMENU_ICONMODE)
            {
                int i = ((ViewGroup)view.getParent()).indexOfChild(view);
                if(i == 0)
                    initAnimations();
                if(i >= 0 && i < mItemNumOfPage)
                {
                    int j = mAniIndexSelector.indexOf(Integer.valueOf(i));
                    if(j < 4)
                    {
                        mAnimation = getRotateAnimation(view);
                        l = j * 30;
                    } else
                    {
                        mAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F);
                        l = 0L;
                    }
                } else
                {
                    mAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F);
                    l = 0L;
                }
            } else
            {
                android.view.animation.LayoutAnimationController.AnimationParameters animationparameters = view.getLayoutParams().layoutAnimationParameters;
                mAnimation = mOpenAnimations[animationparameters.index];
                mAnimation.setDuration(600L);
                l = super.getDelayForView(view);
            }
            return l;
        }

        List mAniIndexSelector;
        Random mRand;
        final MenuManager this$0;

        public MenuOpenAnimationController()
        {
            this$0 = MenuManager.this;
            super(new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F));
            mRand = new Random();
        }
    }

    private class UpdateDBTask extends AsyncTask
    {

        protected volatile Object doInBackground(Object aobj[])
        {
            return doInBackground((ApplicationsAdapter[])aobj);
        }

        protected transient Void doInBackground(ApplicationsAdapter aapplicationsadapter[])
        {
            if(aapplicationsadapter != null) goto _L2; else goto _L1
_L1:
            return null;
_L2:
            ApplicationsAdapter applicationsadapter = aapplicationsadapter[0];
            Object obj = LauncherModel.mDBLock;
            obj;
            JVM INSTR monitorenter ;
            SQLiteDatabase sqlitedatabase;
            sqlitedatabase = SQLiteDatabase.openDatabase(mLauncher.getDatabasePath("launcher.db").getPath(), null, 0);
            sqlitedatabase.beginTransaction();
            int i = 0;
_L3:
            ContentValues contentvalues;
            ComponentName componentname;
            if(isCancelled())
                break MISSING_BLOCK_LABEL_506;
            int l = applicationsadapter.getCount();
            if(i >= l)
                break MISSING_BLOCK_LABEL_506;
            ApplicationInfo applicationinfo = (ApplicationInfo)applicationsadapter.getItem(i);
            contentvalues = new ContentValues();
            componentname = applicationinfo.componentName;
            contentvalues.put("top_number", Integer.valueOf(applicationinfo.topNum));
            contentvalues.put("page_number", Integer.valueOf(applicationinfo.pageNum));
            contentvalues.put("cell_number", Integer.valueOf(applicationinfo.cellNum));
            applicationinfo.isUpdated = true;
            int i1 = -1;
            Cursor cursor;
            String as[] = new String[1];
            as[0] = "_id";
            cursor = sqlitedatabase.query("apps", as, (new StringBuilder()).append("componentname='").append(componentname.flattenToString()).append("'").toString(), null, null, null, null);
            if(cursor == null)
                break MISSING_BLOCK_LABEL_243;
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                i1 = cursor.getInt(0);
            }
            cursor.close();
_L5:
            if(i1 == -1)
                break MISSING_BLOCK_LABEL_480;
            sqlitedatabase.update("apps", contentvalues, (new StringBuilder()).append("_id=").append(i1).toString(), null);
_L7:
            i++;
              goto _L3
            SQLiteException sqliteexception;
            sqliteexception;
            Log.e("Launcher.MenuManager", (new StringBuilder()).append("doInBackground() ").append(sqliteexception).toString());
            if(true) goto _L5; else goto _L4
_L4:
            if(null.getCount() > 0)
            {
                null.moveToFirst();
                i1 = null.getInt(0);
            }
            null.close();
              goto _L5
            Exception exception1;
            exception1;
            sqlitedatabase.endTransaction();
            sqlitedatabase.close();
            throw exception1;
            Exception exception;
            exception;
            throw exception;
            IllegalStateException illegalstateexception;
            illegalstateexception;
            Log.e("Launcher.MenuManager", (new StringBuilder()).append("doInBackground() ").append(illegalstateexception).toString());
            if(true) goto _L5; else goto _L6
_L6:
            if(null.getCount() > 0)
            {
                null.moveToFirst();
                i1 = null.getInt(0);
            }
            null.close();
              goto _L5
            Exception exception2;
            exception2;
            if(false)
            {
                if(null.getCount() > 0)
                {
                    null.moveToFirst();
                    null.getInt(0);
                }
                null.close();
            }
            throw exception2;
            contentvalues.put("componentname", componentname.flattenToString());
            sqlitedatabase.insert("apps", null, contentvalues);
              goto _L7
            int j;
            if(isCancelled())
                break MISSING_BLOCK_LABEL_553;
            sqlitedatabase.setTransactionSuccessful();
            j = 0;
_L8:
            int k = applicationsadapter.getCount();
            if(j >= k)
                break MISSING_BLOCK_LABEL_553;
            ((ApplicationInfo)applicationsadapter.getItem(j)).isUpdated = true;
            j++;
              goto _L8
            sqlitedatabase.endTransaction();
            sqlitedatabase.close();
            obj;
            JVM INSTR monitorexit ;
              goto _L1
        }

        final MenuManager this$0;

        private UpdateDBTask()
        {
            this$0 = MenuManager.this;
            super();
        }

    }

    class AdapterDataSetObserver extends DataSetObserver
    {

        public void onChanged()
        {
            updateMenu();
            if(mMode != 2)
            {
                stopUpdateDB();
                startUpdateDB();
            }
        }

        public void onInvalidated()
        {
            updateMenu();
        }

        final MenuManager this$0;

        AdapterDataSetObserver()
        {
            this$0 = MenuManager.this;
            super();
        }
    }


    public MenuManager(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mIsIndicatorShow = false;
        SNAP_VELOCITY = 200;
        MIN_GAP = 2;
        mFirstLayout = true;
        mNextScreen = -1;
        mTouchState = 0;
        mMode = 0;
        mAllAppsList = new ArrayList();
        mOpenFlag = false;
        mAnimating = false;
        mPoint = new int[2];
        mDragRect = new Rect();
        mMultiTouchUsed = false;
        mMenuOpenAni = new MenuOpenAnimationController();
        mMenuCloseAni = new MenuCloseAnimationController();
        mOvershootInterpolator = new OvershootInterpolator(4F);
        mAnticipateInterpolator = new AnticipateInterpolator(4F);
        mOrientation = 1;
        mSnapToScreenDuration = 400;
        mDelayedAppClickRunnable = new Runnable() {

            public void run()
            {
                if(mClickedApp != null)
                    if(mMode == 2)
                    {
                        if(!mClickedApp.systemApp)
                            mLauncher.showDeleteApplication(mClickedApp.intent.getComponent().getPackageName());
                    } else
                    {
                        mLauncher.startActivitySafely(mClickedApp.intent, null);
                    }
                mClickedApp = null;
            }

            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mOnClickListener = new android.view.View.OnClickListener() {

            public void onClick(View view)
            {
                Object obj = view.getTag();
                if(obj == null || !(obj instanceof ApplicationInfo)) goto _L2; else goto _L1
_L1:
                if(!Launcher.USE_MAINMENU_ICONMODE) goto _L4; else goto _L3
_L3:
                if(mMode == 2)
                {
                    ApplicationInfo applicationinfo1 = (ApplicationInfo)obj;
                    if(!applicationinfo1.systemApp)
                        mLauncher.showDeleteApplication(applicationinfo1.intent.getComponent().getPackageName());
                } else
                {
                    removeCallbacks(mDelayedAppClickRunnable);
                    mClickedApp = (ApplicationInfo)obj;
                    postDelayed(mDelayedAppClickRunnable, 200L);
                    ((MenuItemView)view).applyRotation(180F, 360F);
                }
_L2:
                return;
_L4:
                ApplicationInfo applicationinfo = (ApplicationInfo)obj;
                if(mMode == 2)
                {
                    if(!applicationinfo.systemApp)
                        mLauncher.showDeleteApplication(applicationinfo.intent.getComponent().getPackageName());
                } else
                {
                    mLauncher.startActivitySafely(applicationinfo.intent, null);
                }
                if(true) goto _L2; else goto _L5
_L5:
            }

            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mOnLongClickListener = new android.view.View.OnLongClickListener() {

            public boolean onLongClick(View view)
            {
                boolean flag;
                if(!view.isInTouchMode())
                {
                    flag = false;
                } else
                {
label0:
                    {
                        if(!mMultiTouchUsed)
                            break label0;
                        flag = false;
                    }
                }
_L7:
                return flag;
                Object obj = view.getTag();
                if(obj == null || !(obj instanceof ApplicationInfo)) goto _L2; else goto _L1
_L1:
                ApplicationInfo applicationinfo;
                AppMenu appmenu;
                int i;
                applicationinfo = (ApplicationInfo)obj;
                if(mMode != 2)
                    break MISSING_BLOCK_LABEL_150;
                appmenu = (AppMenu)getChildAt(getDropScreen());
                i = 0;
_L8:
                if(i >= appmenu.getChildCount()) goto _L4; else goto _L3
_L3:
                if(appmenu.getChildAt(i) != view) goto _L6; else goto _L5
_L5:
                mDragView = view;
                mDragCell = i;
_L4:
                mDragger.startDrag(view, MenuManager.this, applicationinfo, 0);
_L2:
                flag = true;
                  goto _L7
_L6:
                i++;
                  goto _L8
                mLauncher.closeAllApplications();
                if(Launcher.USE_MAINMENU_ICONMODE)
                    mLauncher.getTopFourZone().setVisibility(4);
                mDragger.startDrag(view, MenuManager.this, applicationinfo, 1);
                  goto _L2
            }

            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mOpenAlphaAnimationInterpolator = new Interpolator() {

            public float getInterpolation(float f)
            {
                float f1;
                if(f < 0.1F)
                    f1 = 0.0F;
                else
                    f1 = Math.min(4F * f, 1.0F);
                return f1;
            }

            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mMenuOpenAnimationListener = new InterruptableAnimationListener() {

            public void interrupt()
            {
                mIsEnd = true;
            }

            public void onAnimationEnd(Animation animation)
            {
                if(!mIsEnd)
                {
                    setClipChildren(true);
                    int i = getChildCount();
                    for(int j = 0; j < i; j++)
                        getChildAt(j).setVisibility(0);

                    unlock();
                    mIsEnd = true;
                }
            }

            public void onAnimationRepeat(Animation animation)
            {
            }

            public void onAnimationStart(Animation animation)
            {
                setClipChildren(false);
                mAnimationStartTime = SystemClock.uptimeMillis();
                lock();
                mIsEnd = false;
            }

            private boolean mIsEnd;
            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mIsClosing = false;
        mMenuCloseAnimationListener = new InterruptableAnimationListener() {

            public void interrupt()
            {
                mIsEnd = true;
                mIsClosing = false;
            }

            public void onAnimationEnd(Animation animation)
            {
                if(!mIsEnd)
                {
                    mLauncher.getWorkspace().setShowIndicator();
                    mLauncher.getWorkspace().postInvalidate();
                    setClipChildren(true);
                    unlock();
                    mIsEnd = true;
                    mIsClosing = false;
                }
            }

            public void onAnimationRepeat(Animation animation)
            {
            }

            public void onAnimationStart(Animation animation)
            {
                setClipChildren(false);
                mAnimationStartTime = -SystemClock.uptimeMillis();
                lock();
                mIsEnd = false;
                mIsClosing = true;
            }

            private boolean mIsEnd;
            final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
        }
;
        mColumnNum = LauncherConfig.getColumnNo(context);
        mItemNumOfPage = LauncherConfig.getItemNoOfPage(context);
        mUseLargeDrawablesOnlyForPageIndicator = LauncherConfig.pageIndicator_getUseLargeDrawablesOnly(context);
        mOpenAnimations = new Animation[mItemNumOfPage];
        mCloseAnimations = new Animation[mItemNumOfPage];
        init();
    }

    private void addBlankPage()
    {
        if(mMode == 2) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i = getChildCount();
        if(i != 0 && ((ViewGroup)getChildAt(i - 1)).getChildCount() == 1)
            makeMenuView();
        if(true) goto _L1; else goto _L3
_L3:
    }

    private ApplicationInfo copyAdapterInfo(ApplicationsAdapter applicationsadapter, ApplicationInfo applicationinfo)
    {
        if(applicationsadapter != null && applicationinfo != null) goto _L2; else goto _L1
_L1:
        ApplicationInfo applicationinfo1 = null;
_L4:
        return applicationinfo1;
_L2:
        ApplicationInfo applicationinfo2 = null;
        int i = applicationsadapter.getCount();
        int j = 0;
        do
        {
label0:
            {
                if(j < i)
                {
                    applicationinfo2 = (ApplicationInfo)mAdapter.getItem(j);
                    if(!applicationinfo2.title.equals(applicationinfo.title) || !applicationinfo2.intent.getComponent().flattenToString().equals(applicationinfo.intent.getComponent().flattenToString()))
                        break label0;
                    applicationinfo2.topNum = applicationinfo.topNum;
                    applicationinfo2.pageNum = applicationinfo.pageNum;
                    applicationinfo2.cellNum = applicationinfo.cellNum;
                    applicationinfo2.editTopNum = applicationinfo.editTopNum;
                    applicationinfo2.editPageNum = applicationinfo.editPageNum;
                    applicationinfo2.editCellNum = applicationinfo.editCellNum;
                }
                applicationinfo1 = applicationinfo2;
            }
            if(true)
                continue;
            j++;
        } while(true);
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void drawPageIndicator(Canvas canvas)
    {
        PageIndicator pageindicator;
        int i;
        pageindicator = mPageIndicator;
        i = getChildCount();
        if(i > 0 && pageindicator != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int j;
        int l;
        int i1;
        float f;
        j = getWhichScreen();
        byte byte0;
        int k;
        boolean flag;
        boolean flag1;
        if(i > 1)
            if(j < 0)
                j = i - 1;
            else
            if(j >= i)
                j = 0;
        if(Launcher.USE_MAINMENU_ICONMODE)
            byte0 = 11;
        else
            byte0 = 9;
        k = byte0 / 2;
        l = 0;
        flag = false;
        flag1 = false;
        if(i > byte0)
        {
            l = j - k;
            int j1;
            long l1;
            if(l < 0)
                l = 0;
            else
            if(j + k >= i)
                l = i - byte0;
            else
                flag = true;
            flag1 = true;
        }
        if(Launcher.USE_MAINMENU_ICONMODE && mPageIndicator.mIsDraw && mOrientation == 1)
            i1 = mPageIndicatorBottom;
        else
            i1 = mPageIndicatorTop;
        if(!Launcher.USE_MAINMENU_CONCENTRATION_EFFECT || mAnimationStartTime == 0L) goto _L4; else goto _L3
_L3:
        l1 = Math.abs(mAnimationStartTime);
        f = (float)(SystemClock.uptimeMillis() - l1) / 400F;
        if(f >= 1.0F)
        {
            mAnimationStartTime = 0L;
            f = 1.0F;
        }
        if(mAnimationStartTime <= 0L) goto _L6; else goto _L5
_L5:
        i1 = (int)(mOvershootInterpolator.getInterpolation(f) * (float)mPageIndicatorTop);
_L4:
        pageindicator.setFirstTextNum(l);
        pageindicator.setOffset(mPageIndicatorLeft + mScrollX, i1);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            pageindicator.setScrollX(mScrollX);
            pageindicator.setOffsetY(mPageIndicatorOffsetY);
        }
        float f1;
        if(flag)
            pageindicator.setCurrentPage(k, mUseLargeDrawablesOnlyForPageIndicator);
        else
            pageindicator.setCurrentPage(j - l, mUseLargeDrawablesOnlyForPageIndicator);
        pageindicator.enableLeftMore(false);
        pageindicator.enableRightMore(false);
        if(flag1)
        {
            if(j > 0 && l > 0 && mIsIndicatorShow)
                pageindicator.enableLeftMore(true);
            j1 = i - 1;
            if(j < j1 && l + byte0 < i && mIsIndicatorShow)
                pageindicator.enableRightMore(true);
        }
        if(pageindicator.draw(canvas))
            postInvalidate();
        if(true) goto _L1; else goto _L6
_L6:
        if(mAnimationStartTime < 0L)
        {
            f1 = mAnticipateInterpolator.getInterpolation(f);
            i1 = mPageIndicatorTop - (int)(f1 * (float)mPageIndicatorTop);
        }
          goto _L4
    }

    private static int findAppByComponent(ArrayList arraylist, ApplicationInfo applicationinfo)
    {
        ComponentName componentname;
        int i;
        int j;
        componentname = applicationinfo.intent.getComponent();
        i = arraylist.size();
        j = 0;
_L3:
        if(j >= i)
            break MISSING_BLOCK_LABEL_57;
        if(!((ApplicationInfo)arraylist.get(j)).intent.getComponent().equals(componentname)) goto _L2; else goto _L1
_L1:
        int k = j;
_L4:
        return k;
_L2:
        j++;
          goto _L3
        k = -1;
          goto _L4
    }

    private AppMenu getEmptyPageView(int i)
    {
        int j;
        j = getChildCount();
        mTempPage = i;
        if(i < j) goto _L2; else goto _L1
_L1:
        makeMenuView();
        i = j;
        mTempPage = j;
_L6:
        AppMenu appmenu = (AppMenu)getChildAt(i);
_L4:
        return appmenu;
_L2:
        if(((AppMenu)getChildAt(i)).getChildCount() < mItemNumOfPage)
            continue; /* Loop/switch isn't completed */
        appmenu = getEmptyPageView(i + 1);
        if(true) goto _L4; else goto _L3
_L3:
        if(true) goto _L6; else goto _L5
_L5:
    }

    private int getFocusChild(ViewGroup viewgroup)
    {
        int i = 0;
_L6:
        int ai[];
        int j;
        if(i >= viewgroup.getChildCount())
            break MISSING_BLOCK_LABEL_53;
        ai = viewgroup.getChildAt(i).getDrawableState();
        j = 0;
_L5:
        if(j >= ai.length) goto _L2; else goto _L1
_L1:
        if(ai[j] != 0x101009c) goto _L4; else goto _L3
_L3:
        return i;
_L4:
        j++;
          goto _L5
_L2:
        i++;
          goto _L6
        i = -1;
          goto _L3
    }

    private Animation getRotateAnimation(View view)
    {
        Rotate3dAnimation rotate3danimation = new Rotate3dAnimation(90F, 0.0F, (float)view.getWidth() / 2.0F, (float)view.getHeight() / 2.0F, 0.0F, true);
        rotate3danimation.setRotateAxis(1);
        rotate3danimation.setDuration(300L);
        rotate3danimation.setFillAfter(true);
        rotate3danimation.setInterpolator(new DecelerateInterpolator());
        return rotate3danimation;
    }

    private float getXDeltaValue(int i)
    {
        int j = i % mColumnNum;
        if(mOrientation != 1) goto _L2; else goto _L1
_L1:
        j;
        JVM INSTR tableswitch 0 3: default 48
    //                   0 52
    //                   1 59
    //                   2 66
    //                   3 73;
           goto _L3 _L4 _L5 _L6 _L7
_L3:
        float f = 0.0F;
_L8:
        return f;
_L4:
        f = -120F;
          goto _L8
_L5:
        f = -60F;
          goto _L8
_L6:
        f = 60F;
          goto _L8
_L7:
        f = 120F;
          goto _L8
_L2:
        if(mItemNumOfPage != 20) goto _L10; else goto _L9
_L9:
        j;
        JVM INSTR tableswitch 0 4: default 124
    //                   0 127
    //                   1 134
    //                   2 141
    //                   3 146
    //                   4 153;
           goto _L3 _L11 _L12 _L13 _L14 _L15
_L11:
        f = -100F;
          goto _L8
_L12:
        f = -50F;
          goto _L8
_L13:
        f = 0.0F;
          goto _L8
_L14:
        f = 50F;
          goto _L8
_L15:
        f = 100F;
          goto _L8
_L10:
        j;
        JVM INSTR tableswitch 0 3: default 192
    //                   0 195
    //                   1 202
    //                   2 209
    //                   3 216;
           goto _L3 _L16 _L17 _L18 _L19
_L16:
        f = -100F;
          goto _L8
_L17:
        f = -50F;
          goto _L8
_L18:
        f = 50F;
          goto _L8
_L19:
        f = 100F;
          goto _L8
    }

    private float getYDeltaValue(int i)
    {
        int j = i / mColumnNum;
        if(mOrientation != 1) goto _L2; else goto _L1
_L1:
        if(mItemNumOfPage != 20) goto _L4; else goto _L3
_L3:
        j;
        JVM INSTR tableswitch 0 4: default 60
    //                   0 64
    //                   1 71
    //                   2 78
    //                   3 83
    //                   4 90;
           goto _L5 _L6 _L7 _L8 _L9 _L10
_L5:
        float f = 0.0F;
_L11:
        return f;
_L6:
        f = -140F;
          goto _L11
_L7:
        f = -70F;
          goto _L11
_L8:
        f = 0.0F;
          goto _L11
_L9:
        f = 70F;
          goto _L11
_L10:
        f = 140F;
          goto _L11
_L4:
        j;
        JVM INSTR tableswitch 0 3: default 128
    //                   0 131
    //                   1 138
    //                   2 145
    //                   3 152;
           goto _L5 _L12 _L13 _L14 _L15
_L12:
        f = -140F;
          goto _L11
_L13:
        f = -70F;
          goto _L11
_L14:
        f = 70F;
          goto _L11
_L15:
        f = 140F;
          goto _L11
_L2:
        j;
        JVM INSTR tableswitch 0 3: default 192
    //                   0 195
    //                   1 202
    //                   2 209
    //                   3 216;
           goto _L5 _L16 _L17 _L18 _L19
_L16:
        f = -80F;
          goto _L11
_L17:
        f = -40F;
          goto _L11
_L18:
        f = 40F;
          goto _L11
_L19:
        f = 80F;
          goto _L11
    }

    private void init()
    {
        Resources resources = getResources();
        setClipChildren(false);
        mOrientation = resources.getConfiguration().orientation;
        mScroller = new Scroller(getContext());
        mCurrentScreen = 0;
        mPaint = new Paint();
        mPaint.setDither(false);
        ViewConfiguration viewconfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewconfiguration.getScaledTouchSlop();
        mMaximumVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        SNAP_VELOCITY = 6 * viewconfiguration.getScaledMinimumFlingVelocity();
        if(mDeleteIcon == null)
            mDeleteIcon = BitmapFactory.decodeResource(resources, 0x7f020022);
        if(mEditTopBg == null && !Launcher.USE_MAINMENU_ICONMODE)
            mEditTopBg = BitmapFactory.decodeResource(resources, 0x7f020029);
        if(mEditMenuBg == null)
            mEditMenuBg = mEditTopBg;
        mPageIndicatorMiddle = resources.getDrawable(0x7f020046);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mPageIndicatorLarge = resources.getDrawable(0x7f02005c);
            mPageIndicatorSmall = resources.getDrawable(0x7f020051);
            mPageIndicatorOffsetY = resources.getDimensionPixelSize(0x7f090032);
            mPageIndicatorGap = resources.getDimensionPixelSize(0x7f090033);
            mPageIndicatorMoreGap = resources.getDimensionPixelSize(0x7f090034);
            mPageIndicatorMoreDim = new Drawable[3];
            for(int j = 0; j < 3; j++)
                mPageIndicatorMoreDim[j] = resources.getDrawable(0x7f020059 + j);

        } else
        {
            mPageIndicatorLarge = resources.getDrawable(0x7f02005c);
            mPageIndicatorSmall = resources.getDrawable(0x7f020037);
            mPageIndicatorGap = resources.getDimensionPixelSize(0x7f09001d);
            mPageIndicatorMoreGap = resources.getDimensionPixelSize(0x7f09001e);
            mPageIndicatorMore = resources.getDrawable(0x7f020048);
        }
        mTopOffset = resources.getDimensionPixelSize(0x7f090009);
        mTextSize = resources.getDimensionPixelSize(0x7f09001c);
        mPageIndicatorSmall.setBounds(0, 0, mPageIndicatorSmall.getIntrinsicWidth(), mPageIndicatorSmall.getIntrinsicHeight());
        mPageIndicatorMiddle.setBounds(0, 0, mPageIndicatorMiddle.getIntrinsicWidth(), mPageIndicatorMiddle.getIntrinsicHeight());
        mPageIndicatorLarge.setBounds(0, 0, mPageIndicatorLarge.getIntrinsicWidth(), mPageIndicatorLarge.getIntrinsicHeight());
        if(mPageIndicatorMore != null)
            mPageIndicatorMore.setBounds(0, 0, mPageIndicatorMore.getIntrinsicWidth(), mPageIndicatorMore.getIntrinsicHeight());
        if(Launcher.USE_MAINMENU_ICONMODE && mPageIndicatorMoreDim != null)
        {
            for(int i = 0; i < mPageIndicatorMoreDim.length; i++)
                if(mPageIndicatorMoreDim[i] != null)
                    mPageIndicatorMoreDim[i].setBounds(0, 0, mPageIndicatorMoreDim[i].getIntrinsicWidth(), mPageIndicatorMoreDim[i].getIntrinsicHeight());

        }
        mEnablePageIndicatorShowHide = resources.getBoolean(0x7f080001);
        mAniFadeIn = AnimationUtils.loadAnimation(getContext(), 0x7f040000);
        mAniFadeOut = AnimationUtils.loadAnimation(getContext(), 0x7f040001);
        mAniFadeIn.setDuration(600L);
        mAniFadeOut.setDuration(400L);
        if(mOrientation == 1)
            mSnapToScreenDuration = 400;
        else
            mSnapToScreenDuration = 600;
        mWallpaperManager = WallpaperManager.getInstance(getContext());
        initAnimation();
        setAnimationCacheEnabled(false);
        mAdapter = new ApplicationsAdapter(getContext(), mAllAppsList);
        mDataSetObserver = new AdapterDataSetObserver();
        mAdapter.registerDataSetObserver(mDataSetObserver);
        mAdapter.setNotifyOnChange(false);
    }

    private void initAnimation()
    {
        int i = 0;
        while(i < mItemNumOfPage) 
        {
            if(Launcher.USE_MAINMENU_CONCENTRATION_EFFECT)
            {
                float f = getXDeltaValue(i);
                float f1 = getYDeltaValue(i);
                TranslateAnimation translateanimation = new TranslateAnimation(f, 0.0F, f1, 0.0F);
                translateanimation.setInterpolator(new OvershootInterpolator(1.0F));
                AlphaAnimation alphaanimation = new AlphaAnimation(0.0F, 1.0F);
                alphaanimation.setInterpolator(mOpenAlphaAnimationInterpolator);
                AnimationSet animationset = new AnimationSet(false);
                animationset.addAnimation(translateanimation);
                animationset.addAnimation(alphaanimation);
                mOpenAnimations[i] = animationset;
                AnimationSet animationset1 = new AnimationSet(true);
                animationset1.addAnimation(new TranslateAnimation(0.0F, f, 0.0F, f1));
                animationset1.addAnimation(new AlphaAnimation(1.0F, 1.0F));
                mCloseAnimations[i] = animationset1;
                mCloseAnimations[i].setInterpolator(new AnticipateInterpolator(1.0F));
            } else
            {
                mOpenAnimations[i] = new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F);
                mCloseAnimations[i] = new TranslateAnimation(0.0F, 0.0F, 0.0F, 0.0F);
                mOpenAnimations[i].setInterpolator(new LinearInterpolator());
                mCloseAnimations[i].setInterpolator(new LinearInterpolator());
            }
            i++;
        }
        mMenuOpenAni.setDelay(0.0F);
        mMenuCloseAni.setDelay(0.0F);
    }

    private void initPageIndicator()
    {
        int i;
        int j;
        int k;
        i = 0;
        j = mPageIndicatorLarge.getIntrinsicWidth();
        mPageIndicatorLarge.getIntrinsicHeight();
        k = getChildCount();
        if(k > 0) goto _L2; else goto _L1
_L1:
        return;
_L2:
        if(mPageIndicator == null)
            mPageIndicator = new PageIndicator();
        byte byte0;
        PageIndicator pageindicator;
        int l;
        int i1;
        int j1;
        if(Launcher.USE_MAINMENU_ICONMODE)
            byte0 = 11;
        else
            byte0 = 9;
        if(k > byte0)
            k = byte0;
        if(getResources().getBoolean(0x7f080004))
        {
            mPageIndicatorLarge.setAlpha(205);
            mPageIndicatorSmall.setAlpha(153);
            mPageIndicatorMore.setAlpha(115);
        } else
        {
            mPageIndicatorMore.setAlpha(153);
        }
        pageindicator = mPageIndicator;
        pageindicator.setPageDrawable(mPageIndicatorLarge);
        pageindicator.setPageDrawableSmall(mPageIndicatorSmall);
        pageindicator.setPageCount(k);
        pageindicator.setGap(mPageIndicatorGap, mPageIndicatorMoreGap);
        pageindicator.setTextSize(mTextSize);
        if(Launcher.USE_MAINMENU_ICONMODE)
            pageindicator.setMoreDrawableDim(mPageIndicatorMoreDim);
        else
            pageindicator.setMoreDrawable(mPageIndicatorMore);
        if(mEnablePageIndicatorShowHide)
            pageindicator.enableShowHide(true);
        else
            pageindicator.enableShowHide(false);
        l = j * k;
        i1 = mPageIndicatorGap;
        if(k > 0)
            i = k - 1;
        j1 = l + i1 * i;
        mPageIndicatorLeft = (getWidth() - j1) / 2;
        if(mTopOffset == 0 || mOrientation == 2)
            mPageIndicatorTop = getResources().getDimensionPixelSize(0x7f09001b);
        else
            mPageIndicatorTop = getResources().getDimensionPixelSize(0x7f090032);
        pageindicator.setOrientation(mOrientation);
        if(Launcher.USE_MAINMENU_ICONMODE)
            mPageIndicatorBottom = getHeight() - getResources().getDimensionPixelSize(0x7f090030);
        if(true) goto _L1; else goto _L3
_L3:
    }

    private boolean isListChild()
    {
        if(getChildCount() != 1) goto _L2; else goto _L1
_L1:
        View view = getChildAt(0);
        if(view == null || !(view instanceof ListView)) goto _L2; else goto _L3
_L3:
        boolean flag = true;
_L5:
        return flag;
_L2:
        flag = false;
        if(true) goto _L5; else goto _L4
_L4:
    }

    private AppMenu makeMenuView()
    {
        AppMenu appmenu = new AppMenu(getContext(), mOrientation);
        addView(appmenu, new android.view.ViewGroup.LayoutParams(-1, -1));
        if(mMode == 2)
        {
            appmenu.setBitmap(mDeleteIcon);
            appmenu.setEditBg(mEditMenuBg);
            appmenu.setEnabledChildAnimation(true);
        } else
        {
            appmenu.setBitmap(null);
            appmenu.setEditBg(null);
            appmenu.setEnabledChildAnimation(false);
        }
        return appmenu;
    }

    private void snapToDestination()
    {
        snapToScreen(getWhichScreen());
    }

    private void updateMenu()
    {
        ApplicationsAdapter applicationsadapter = mAdapter;
        if(applicationsadapter != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i = mAdapter.getCount();
        int j = mMode;
        AppShortcutZone appshortcutzone = mLauncher.getAppShortcutZone();
        removeAllViews();
        ArrayList arraylist;
        if(j == 2)
        {
            appshortcutzone.setBitmap(mDeleteIcon);
            appshortcutzone.setEditBg(mEditTopBg);
            appshortcutzone.setEnabledChildAnimation(true);
        } else
        {
            appshortcutzone.setBitmap(null);
            appshortcutzone.setEditBg(null);
            appshortcutzone.setEnabledChildAnimation(false);
        }
        appshortcutzone.changeApplicationsIcon();
        appshortcutzone.invalidate();
        if(i <= 0)
            continue; /* Loop/switch isn't completed */
        arraylist = new ArrayList();
        if(j == 1)
        {
            ArrayList arraylist1 = new ArrayList();
            arraylist1.clear();
            int k = 0;
            while(k < i) 
            {
                ApplicationInfo applicationinfo1 = (ApplicationInfo)applicationsadapter.getItem(k);
                if(applicationinfo1.topNum != 65535)
                    arraylist.add(applicationinfo1);
                else
                    arraylist1.add(applicationinfo1);
                k++;
            }
            ListView listview = new ListView(getContext());
            final ApplicationsListAdapter listadapter = new ApplicationsListAdapter(getContext(), arraylist1);
            listadapter.sort(LauncherModel.APP_NAME_COMPARATOR);
            listview.setAdapter(listadapter);
            addView(listview, new android.view.ViewGroup.LayoutParams(-1, -1));
            listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView adapterview, View view2, int l2, long l3)
                {
                    ApplicationInfo applicationinfo4 = (ApplicationInfo)listadapter.getItem(l2);
                    mLauncher.startActivitySafely(applicationinfo4.intent, null);
                }

                final MenuManager this$0;
                final ApplicationsListAdapter val$listadapter;

            
            {
                this$0 = MenuManager.this;
                listadapter = applicationslistadapter;
                super();
            }
            }
);
            listview.setOnItemLongClickListener(new android.widget.AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView adapterview, View view2, int l2, long l3)
                {
                    ApplicationInfo applicationinfo4 = (ApplicationInfo)listadapter.getItem(l2);
                    mDragger.startDrag(view2, MenuManager.this, applicationinfo4, 2);
                    mLauncher.closeAllApplications();
                    return true;
                }

                final MenuManager this$0;
                final ApplicationsListAdapter val$listadapter;

            
            {
                this$0 = MenuManager.this;
                listadapter = applicationslistadapter;
                super();
            }
            }
);
            mCurrentScreen = 0;
            scrollTo(0, 0);
            appshortcutzone.clearApplications();
            int l = arraylist.size();
            for(int i1 = 0; i1 < l; i1++)
            {
                ApplicationInfo applicationinfo = (ApplicationInfo)arraylist.get(i1);
                appshortcutzone.updateApplication(applicationinfo, applicationinfo.topNum);
            }

            arraylist.clear();
            continue; /* Loop/switch isn't completed */
        }
        applicationsadapter.setNotifyOnChange(false);
        mOrderComparator.setMode(mMode);
        applicationsadapter.sort(mOrderComparator);
        int j1 = 0;
        while(j1 < i) 
        {
            ApplicationInfo applicationinfo3 = (ApplicationInfo)applicationsadapter.getItem(j1);
            if(j == 2)
            {
                if(applicationinfo3.editTopNum != 65535)
                {
                    arraylist.add(applicationinfo3);
                } else
                {
                    AppMenu appmenu1;
                    View view1;
                    int k2;
                    if(applicationinfo3.editPageNum == 65535)
                    {
                        appmenu1 = getEmptyPageView(0);
                        applicationinfo3.editPageNum = mTempPage;
                        applicationinfo3.editCellNum = appmenu1.getChildCount();
                    } else
                    {
                        appmenu1 = getEmptyPageView(applicationinfo3.editPageNum);
                    }
                    view1 = makeItemView(j1, appmenu1);
                    view1.setTag(applicationinfo3);
                    appmenu1.addView(view1);
                    k2 = appmenu1.getChildCount();
                    if(applicationinfo3.cellNum != k2 - 1)
                        applicationinfo3.editCellNum = k2 - 1;
                }
            } else
            {
                applicationinfo3.editTopNum = applicationinfo3.topNum;
                applicationinfo3.editPageNum = applicationinfo3.pageNum;
                applicationinfo3.editCellNum = applicationinfo3.cellNum;
                if(applicationinfo3.topNum != 65535)
                {
                    arraylist.add(applicationinfo3);
                } else
                {
                    AppMenu appmenu;
                    View view;
                    int j2;
                    if(applicationinfo3.pageNum == 65535)
                    {
                        appmenu = getEmptyPageView(0);
                        applicationinfo3.pageNum = mTempPage;
                        applicationinfo3.cellNum = appmenu.getChildCount();
                        applicationinfo3.editPageNum = mTempPage;
                        applicationinfo3.editCellNum = appmenu.getChildCount();
                        applicationinfo3.isUpdated = false;
                    } else
                    {
                        appmenu = getEmptyPageView(applicationinfo3.pageNum);
                    }
                    view = makeItemView(j1, appmenu);
                    view.setTag(applicationinfo3);
                    appmenu.addView(view);
                    j2 = appmenu.getChildCount();
                    if(applicationinfo3.cellNum != j2 - 1)
                    {
                        applicationinfo3.cellNum = j2 - 1;
                        applicationinfo3.editCellNum = j2 - 1;
                        applicationinfo3.isUpdated = false;
                    }
                }
            }
            j1++;
        }
        if(j == 2)
            makeMenuView();
        appshortcutzone.clearApplications();
        int k1 = arraylist.size();
        int l1 = 0;
        while(l1 < k1) 
        {
            ApplicationInfo applicationinfo2 = (ApplicationInfo)arraylist.get(l1);
            if(j == 2)
                appshortcutzone.updateApplication(applicationinfo2, applicationinfo2.editTopNum);
            else
                appshortcutzone.updateApplication(applicationinfo2, applicationinfo2.topNum);
            l1++;
        }
        arraylist.clear();
        int i2 = getChildCount();
        if(i2 > 0)
        {
            if(mCurrentScreen >= i2)
            {
                mCurrentScreen = i2 - 1;
                scrollTo(mCurrentScreen * getWidth(), 0);
            }
        } else
        {
            mCurrentScreen = 0;
            scrollTo(0, 0);
        }
        mLauncher.updateWorkspaceBadge();
        if(true) goto _L1; else goto _L3
_L3:
    }

    public void addApps(ArrayList arraylist)
    {
        int i = arraylist.size();
        mOrderComparator.setMode(mMode);
        for(int j = 0; j < i; j++)
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)arraylist.get(j);
            int k = Collections.binarySearch(mAllAppsList, applicationinfo, mOrderComparator);
            if(k < 0)
                k = -(k + 1);
            mAllAppsList.add(k, applicationinfo);
        }

        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public void addFocusables(ArrayList arraylist, int i, int j)
    {
        int k = getChildCount();
        if(k > 1)
        {
            int l = getWhichScreen();
            if(l < 0)
                l = k - 1;
            else
            if(l >= k)
                l = 0;
            getChildAt(l).addFocusables(arraylist, i);
            if(i == 17)
            {
                if(l > 0)
                    getChildAt(l - 1).addFocusables(arraylist, i);
                else
                if(l == 0)
                    getChildAt(k - 1).addFocusables(arraylist, i);
            } else
            if(i == 66)
                if(l < k - 1)
                    getChildAt(l + 1).addFocusables(arraylist, i);
                else
                if(l == k - 1)
                    getChildAt(0).addFocusables(arraylist, i);
        } else
        {
            super.addFocusables(arraylist, i, j);
        }
    }

    View addItem(View view, int i)
    {
        int j;
        AppMenu appmenu;
        int k;
        ApplicationInfo applicationinfo;
        int l;
        j = getDropScreen();
        appmenu = (AppMenu)getChildAt(j);
        k = appmenu.getChildCount();
        applicationinfo = (ApplicationInfo)view.getTag();
        l = mAdapter.getPosition(applicationinfo);
        if(l >= 0) goto _L2; else goto _L1
_L1:
        ApplicationInfo applicationinfo3 = copyAdapterInfo(mAdapter, applicationinfo);
        if(applicationinfo3 != null) goto _L4; else goto _L3
_L3:
        View view2 = null;
_L5:
        return view2;
_L4:
        ApplicationInfo applicationinfo1;
        int i1;
        int l1 = mAdapter.getPosition(applicationinfo3);
        applicationinfo1 = applicationinfo3;
        i1 = l1;
_L6:
        View view1 = makeItemView(i1, appmenu);
        view1.setTag(applicationinfo1);
        int j1;
        if(k < i)
            appmenu.addView(view1);
        else
            appmenu.addView(view1, i);
        j1 = appmenu.getChildCount();
        if(j1 > mItemNumOfPage)
        {
            View view3 = appmenu.getChildAt(j1 - 1);
            appmenu.removeView(view3);
            shiftItem(view3, j + 1);
        }
        for(int k1 = 0; k1 < j1 && k1 < mItemNumOfPage; k1++)
        {
            ApplicationInfo applicationinfo2 = (ApplicationInfo)appmenu.getChildAt(k1).getTag();
            applicationinfo2.editTopNum = 65535;
            applicationinfo2.editPageNum = j;
            applicationinfo2.editCellNum = k1;
        }

        view2 = view1;
        if(true) goto _L5; else goto _L2
_L2:
        applicationinfo1 = applicationinfo;
        i1 = l;
          goto _L6
    }

    public void animateClose()
    {
        if(!mScroller.isFinished())
        {
            mScroller.abortAnimation();
            computeScroll();
            scrollTo(mCurrentScreen * getWidth(), 0);
        }
        SamsungUtils.setWallpaperVisibility(mWallpaperManager, true);
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).setVisibility(4);

        mLauncher.getMenuDrawer().startAnimation(mAniFadeOut);
        mLauncher.getMenuDrawer().setVisibility(4);
        mLauncher.getMenuDrawer().setBackgroundClose();
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        mLauncher.getAppShortcutZone().changeApplicationsIcon();
        mLauncher.getAppShortcutZone().drawBg(false, true);
        TopFourZone topfourzone = mLauncher.getTopFourZone();
        ViewGroup viewgroup;
        if(topfourzone != null)
            if(Launcher.USE_MAINMENU_ICONMODE)
                topfourzone.setBackgroundColor(0x7f070013);
            else
                topfourzone.setVisibility(0);
        viewgroup = (ViewGroup)getChildAt(mCurrentScreen);
        if(viewgroup != null)
        {
            mIsClosing = true;
            viewgroup.setLayoutAnimation(mMenuCloseAni);
            viewgroup.setLayoutAnimationListener(mMenuCloseAnimationListener);
        }
    }

    public void animateOpen()
    {
        SamsungUtils.setWallpaperVisibility(mWallpaperManager, false);
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).setVisibility(4);

        mLauncher.getMenuDrawer().setBackgroundImage();
        if(!Launcher.USE_MAINMENU_ICONMODE)
            mLauncher.getMenuDrawer().startAnimation(mAniFadeIn);
        mLauncher.getMenuDrawer().setVisibility(0);
        mLauncher.getMenuDrawer().setEnabledProgress(true);
        mLauncher.getAppShortcutZone().changeApplicationsIcon();
        mLauncher.getAppShortcutZone().drawBg(true, true);
        TopFourZone topfourzone = mLauncher.getTopFourZone();
        ViewGroup viewgroup;
        if(topfourzone != null)
            if(Launcher.USE_MAINMENU_ICONMODE)
                topfourzone.setBackgroundResource(0x7f02004e);
            else
                topfourzone.setVisibility(4);
        mOpenFlag = true;
        viewgroup = (ViewGroup)getChildAt(mCurrentScreen);
        if(viewgroup != null)
        {
            viewgroup.setLayoutAnimation(mMenuOpenAni);
            viewgroup.setLayoutAnimationListener(mMenuOpenAnimationListener);
        }
    }

    void clearChildrenCache()
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            ((AppMenu)getChildAt(j)).setChildrenDrawnWithCacheEnabled(false);

    }

    public void close()
    {
        mIsClosing = false;
        mMenuCloseAnimationListener.interrupt();
        mMenuOpenAnimationListener.interrupt();
        SamsungUtils.setWallpaperVisibility(mWallpaperManager, true);
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).setVisibility(4);

        mLauncher.getMenuDrawer().setVisibility(4);
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        mLauncher.getAppShortcutZone().changeApplicationsIcon();
        mLauncher.getAppShortcutZone().drawBg(false, false);
        TopFourZone topfourzone = mLauncher.getTopFourZone();
        if(topfourzone != null)
            if(Launcher.USE_MAINMENU_ICONMODE)
                topfourzone.setBackgroundColor(0x7f070013);
            else
                topfourzone.setVisibility(0);
        mLauncher.getWorkspace().setShowIndicator();
        mLauncher.getWorkspace().postInvalidate();
    }

    public void computeScroll()
    {
        if(mMode == 1 || isListChild())
        {
            if(!mScroller.isFinished())
                mScroller.abortAnimation();
            mNextScreen = -1;
        } else
        if(mScroller.computeScrollOffset())
        {
            mScrollX = mScroller.getCurrX();
            mScrollY = mScroller.getCurrY();
            int j = getChildCount();
            if(j >= 2)
                if(mScrollX < -getWidth() / 2)
                    mScrollX = mScrollX + j * getWidth();
                else
                if(mScrollX > (j - 1) * getWidth() + getWidth() / 2)
                    mScrollX = mScrollX - j * getWidth();
            postInvalidate();
        } else
        if(mNextScreen != -1)
        {
            int i = getChildCount();
            mCurrentScreen = Math.max(0, Math.min(mNextScreen, i - 1));
            if(i >= 2)
            {
                mCurrentScreen = getWhichScreen();
                if(mCurrentScreen < 0)
                    mCurrentScreen = i - 1;
                else
                if(mCurrentScreen >= i)
                    mCurrentScreen = 0;
                if((mScrollX < 0 || mScrollX > (i - 1) * getWidth()) && mScrollX % getWidth() == 0)
                    scrollTo(mCurrentScreen * getWidth(), 0);
            }
            mNextScreen = -1;
            clearChildrenCache();
            postInvalidate();
        }
    }

    public ApplicationsAdapter discardMenuEdit()
    {
        int i = mAdapter.getCount();
        for(int j = 0; j < i; j++)
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)mAdapter.getItem(j);
            applicationinfo.editTopNum = applicationinfo.topNum;
            applicationinfo.editPageNum = applicationinfo.pageNum;
            applicationinfo.editCellNum = applicationinfo.cellNum;
        }

        return null;
    }

    protected void dispatchDraw(Canvas canvas)
    {
        int i;
        i = getChildCount();
        break MISSING_BLOCK_LABEL_5;
_L2:
        int j;
        do
            return;
        while(i <= 0 || mLauncher.getQuickViewMainMenu().isOpened() || mLauncher.getStateQuickNavigation() >= 0);
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        j = (i - 1) * getMeasuredWidth();
        boolean flag;
        if(i > 1)
            if(mCurrentScreen < 0)
                mCurrentScreen = i - 1;
            else
            if(mCurrentScreen >= i)
                mCurrentScreen = 0;
        if(mEnablePageIndicatorShowHide && (mOpenFlag || !mScroller.isFinished()))
        {
            if(mPageIndicator != null)
                mPageIndicator.show();
            mIsIndicatorShow = true;
            removeCallbacks(mHideIndicator);
            mOpenFlag = false;
            postDelayed(mHideIndicator, 1000L);
        }
        if(mTouchState != 1 && mNextScreen == -1)
            flag = true;
        else
            flag = false;
        if(!flag)
            break; /* Loop/switch isn't completed */
        if(mCurrentScreen < i)
            drawChild(canvas, getChildAt(mCurrentScreen), getDrawingTime());
_L3:
        if(mMode != 1 && !Launcher.USE_MAINMENU_ICONMODE)
            drawPageIndicator(canvas);
        if(true) goto _L2; else goto _L1
_L1:
        long l = getDrawingTime();
        int k = mCurrentScreen - mNextScreen;
        if(mNextScreen >= 0 && mNextScreen < i && (Math.abs(k) == 1 || Math.abs(k) == i - 1 && (mScrollX < 0 || mScrollX > j)))
        {
            if(k > 0)
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
            int i1 = getChildCount();
            int j1 = 0;
            while(j1 < i1) 
            {
                drawChild(canvas, getChildAt(j1), l);
                j1++;
            }
        }
        if(i >= 2)
            if(mScrollX < 0)
            {
                canvas.translate(-i * getWidth(), 0.0F);
                drawChild(canvas, getChildAt(i - 1), l);
                canvas.translate(i * getWidth(), 0.0F);
            } else
            if(mScrollX > j)
            {
                canvas.translate(i * getWidth(), 0.0F);
                drawChild(canvas, getChildAt(0), l);
                canvas.translate(-i * getWidth(), 0.0F);
            }
          goto _L3
        if(true) goto _L2; else goto _L4
_L4:
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        if(!SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510")) goto _L2; else goto _L1
_L1:
        int i;
        AppShortcutZone appshortcutzone;
        int j;
        int k;
        i = 4;
        appshortcutzone = mLauncher.getAppShortcutZone();
        ViewGroup viewgroup = (ViewGroup)getChildAt(mCurrentScreen);
        j = getFocusChild(viewgroup);
        k = viewgroup.getChildCount();
        if(keyevent.getAction() != 0 || keyevent.getKeyCode() != 21) goto _L4; else goto _L3
_L3:
        if(k - 1 >= i) goto _L6; else goto _L5
_L5:
        if(j != 0) goto _L8; else goto _L7
_L7:
        int i2 = 0;
_L24:
        if(i2 == -1) goto _L10; else goto _L9
_L9:
        if(mCurrentScreen == 0) goto _L12; else goto _L11
_L11:
        boolean flag;
        ViewGroup viewgroup1 = (ViewGroup)getChildAt(mCurrentScreen - 1);
        int j2 = viewgroup1.getChildCount();
        if(j2 > i)
        {
            if(j2 > i * 2)
            {
                if(i2 != 0)
                    if(i2 == 1)
                        i *= 2;
                    else
                        i = j2;
            } else
            if(i2 != 0)
                i = j2;
        } else
        {
            i = j2;
        }
        snapToScreen(mCurrentScreen - 1);
        viewgroup1.getChildAt(i - 1).requestFocus();
        flag = true;
_L22:
        return flag;
_L6:
        if(k - 1 < i * 2)
        {
            if(j == i)
            {
                i2 = 1;
                continue; /* Loop/switch isn't completed */
            }
            if(j == 0)
            {
                i2 = 0;
                continue; /* Loop/switch isn't completed */
            }
        } else
        {
            if(j == i * 2)
            {
                i2 = 2;
                continue; /* Loop/switch isn't completed */
            }
            if(j == i)
            {
                i2 = 1;
                continue; /* Loop/switch isn't completed */
            }
            if(j == 0)
            {
                i2 = 0;
                continue; /* Loop/switch isn't completed */
            }
        }
          goto _L8
_L12:
        int l;
        snapToScreen(-1);
        l = i2;
_L19:
        if(l == -1) goto _L2; else goto _L13
_L4:
        if(keyevent.getAction() != 0 || keyevent.getKeyCode() != 22) goto _L15; else goto _L14
_L14:
        if(k - 1 >= i) goto _L17; else goto _L16
_L16:
        if(j != k - 1) goto _L15; else goto _L18
_L18:
        l = 0;
          goto _L19
_L17:
        if(k - 1 < i * 2)
        {
label0:
            {
                if(j != k - 1)
                    break label0;
                l = 1;
            }
        } else
        if(j == k - 1)
        {
            l = 2;
        } else
        {
label1:
            {
                if(j != i * 2 - 1)
                    break label1;
                l = 1;
            }
        }
          goto _L19
        if(j != i - 1) goto _L15; else goto _L20
_L20:
        l = 0;
          goto _L19
        if(j != i - 1) goto _L15; else goto _L21
_L21:
        l = 0;
          goto _L19
_L13:
        int i1 = appshortcutzone.getEnabledChildCount();
        int ai[] = new int[i1];
        int j1 = appshortcutzone.getChildCount() - 1;
        int k1 = 0;
        for(; j1 >= 0; j1--)
            if(appshortcutzone.getChildAt(j1).getVisibility() != 8)
            {
                int l1 = k1 + 1;
                ai[k1] = j1;
                k1 = l1;
            }

        if(l > i1 - 1)
            l = i1 - 1;
        appshortcutzone.getChildAt(ai[l]).requestFocus();
        flag = true;
          goto _L22
_L2:
        flag = super.dispatchKeyEvent(keyevent);
          goto _L22
_L15:
        l = -1;
          goto _L19
_L10:
        l = i2;
          goto _L19
_L8:
        i2 = -1;
        if(true) goto _L24; else goto _L23
_L23:
    }

    public boolean dispatchUnhandledMove(View view, int i)
    {
        int j = getChildCount();
        if(j <= 1) goto _L2; else goto _L1
_L1:
        if(i != 17) goto _L4; else goto _L3
_L3:
        if(mCurrentScreen != 0) goto _L2; else goto _L5
_L5:
        boolean flag;
        snapToScreen(-1);
        flag = true;
_L7:
        return flag;
_L4:
        if(i == 66 && mCurrentScreen == j - 1)
        {
            snapToScreen(j);
            flag = true;
            continue; /* Loop/switch isn't completed */
        }
_L2:
        flag = super.dispatchUnhandledMove(view, i);
        if(true) goto _L7; else goto _L6
_L6:
    }

    void drawPageIndicatorExternal(Canvas canvas)
    {
        if(Launcher.USE_MAINMENU_ICONMODE && !mLauncher.getQuickViewWorkspace().isOpened() && !mLauncher.getQuickViewMainMenu().isOpened() && mLauncher.getStateQuickNavigation() < 0 && mLauncher.getMenuDrawer().getVisibility() == 0)
        {
            int i = canvas.save();
            canvas.translate(-mScrollX, 0.0F);
            drawPageIndicator(canvas);
            canvas.restoreToCount(i);
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
        int l = -1;
        int i1;
        int j1;
        int k1;
        if(k > 1)
            if(i < 0)
            {
                l = k - 1;
                AppMenu appmenu2 = (AppMenu)getChildAt(l);
                appmenu2.setChildrenDrawnWithCacheEnabled(true);
                appmenu2.setChildrenDrawingCacheEnabled(true);
            } else
            if(j >= k)
            {
                l = 0;
                AppMenu appmenu1 = (AppMenu)getChildAt(0);
                appmenu1.setChildrenDrawnWithCacheEnabled(true);
                appmenu1.setChildrenDrawingCacheEnabled(true);
            }
        i1 = Math.max(i, 0);
        j1 = Math.min(j, k - 1);
        k1 = i1;
        while(k1 <= j1) 
        {
            if(l != k1)
            {
                AppMenu appmenu = (AppMenu)getChildAt(k1);
                appmenu.setChildrenDrawnWithCacheEnabled(true);
                appmenu.setChildrenDrawingCacheEnabled(true);
            }
            k1++;
        }
    }

    public boolean getAnimateStatus()
    {
        return mAnimating;
    }

    public int getCurrentScreen()
    {
        return mCurrentScreen;
    }

    public int getDragAnimationXOffset(View view)
    {
        int i;
        if(view != null && (view.getParent() instanceof AppShortcutZone))
            i = 0;
        else
            i = mScrollX;
        return i;
    }

    int getDropScreen()
    {
        int i;
        if(mScroller.isFinished())
            i = mCurrentScreen;
        else
            i = mNextScreen;
        return i;
    }

    int getIndexOfCell(int i, int j)
    {
        AppMenu appmenu = (AppMenu)getChildAt(getDropScreen());
        int k = appmenu.getChildWidth();
        int l = appmenu.getChildHeight();
        int i1 = appmenu.getLRPadding();
        Rect rect = new Rect();
        getHitRect(rect);
        rect.top = rect.top + appmenu.getTop();
        int j1 = mColumnNum;
        int k1 = mItemNumOfPage / mColumnNum;
        int l1;
        if(rect.contains(i, j))
        {
            int i2 = i - rect.left - i1;
            int j2 = j - rect.top;
            int k2;
            int l2;
            if(i2 < 0)
                k2 = 0;
            else
                k2 = i2 / k;
            l2 = j2 / l;
            if(k2 > j1 - 1)
                k2 = j1 - 1;
            if(l2 > k1 - 1)
                l2 = k1 - 1;
            l1 = k2 + l2 * j1;
        } else
        {
            l1 = -1;
        }
        return l1;
    }

    public int getMode()
    {
        return mMode;
    }

    int getPageIndicatorArea(int i, int j)
    {
        int k;
        if(Launcher.USE_MAINMENU_ICONMODE)
            k = -1;
        else
        if(mPageIndicator == null)
            k = -1;
        else
            k = mPageIndicator.getPageTouchArea(i, j);
        return k;
    }

    public int getWhichScreen()
    {
        int i = getMeasuredWidth();
        int j = getChildCount();
        int k = mScrollX;
        int i1;
        if(j >= 2)
        {
            if(k < 0)
                i1 = (k - i / 2) / i;
            else
                i1 = (k + i / 2) / i;
        } else
        {
            int l = getChildAt(j - 1).getRight() - k - getWidth();
            if(k < 0)
                i1 = 0;
            else
            if(l <= 0)
                i1 = j - 1;
            else
            if(i > 0)
                i1 = (k + i / 2) / i;
            else
                i1 = 0;
        }
        return i1;
    }

    public boolean isClosing()
    {
        return mIsClosing;
    }

    public boolean isOpened()
    {
        boolean flag;
        if(mLauncher.getMenuDrawer().getVisibility() == 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean isRightScroll(int i)
    {
        boolean flag;
        if(SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510") && i > mLauncher.getMenuDrawer().getWidth())
            flag = true;
        else
        if(!SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510") && i > mLauncher.getMenuDrawer().getWidth() - DragLayer.SCROLL_ZONE)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void listsetApps(ArrayList arraylist)
    {
        int i = arraylist.size();
        int j = 0;
        while(j < i) 
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)arraylist.get(j);
            int k = findAppByComponent(mAllAppsList, applicationinfo);
            if(k >= 0)
            {
                mAllAppsList.set(k, applicationinfo);
            } else
            {
                mOrderComparator.setMode(mMode);
                int l = Collections.binarySearch(mAllAppsList, applicationinfo, mOrderComparator);
                if(l < 0)
                    l = -(l + 1);
                mAllAppsList.add(l, applicationinfo);
            }
            j++;
        }
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public void lock()
    {
        mLocked = true;
    }

    public View makeItemView(int i, ViewGroup viewgroup)
    {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        int j = -1;
        if(configuration.orientation != mOrientation)
        {
            j = configuration.orientation;
            configuration.orientation = mOrientation;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        View view = mAdapter.getView(i, null, viewgroup);
        view.setFocusable(true);
        view.setOnClickListener(mOnClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        if(j != -1)
        {
            android.util.DisplayMetrics displaymetrics = resources.getDisplayMetrics();
            configuration.orientation = j;
            resources.updateConfiguration(configuration, displaymetrics);
        }
        return view;
    }

    public void onDragEnd()
    {
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        if(i >= DragLayer.SCROLL_ZONE && !isRightScroll(i)) goto _L2; else goto _L1
_L1:
        mDragCell = -1;
_L9:
        return;
_L2:
        Rect rect;
        int ai[];
        AppShortcutZone appshortcutzone;
        rect = mDragRect;
        ai = mPoint;
        appshortcutzone = mLauncher.getAppShortcutZone();
        if(appshortcutzone != null)
            appshortcutzone.getHitRect(rect);
        if(appshortcutzone == null || !rect.contains(i, j)) goto _L4; else goto _L3
_L3:
        AppShortcutZone appshortcutzone1;
        int k1;
        appshortcutzone1 = (AppShortcutZone)appshortcutzone;
        k1 = appshortcutzone1.getIndexOfCell(i, j);
        if(k1 == -1 || k1 == mDragCell || mDragView == null) goto _L6; else goto _L5
_L5:
        View view5 = mDragView;
        if((View)view5.getParent() instanceof AppShortcutZone)
        {
            appshortcutzone1.changeItem(view5, k1);
        } else
        {
            removeItem(view5);
            View view6 = appshortcutzone1.makeItemView((ApplicationInfo)view5.getTag());
            if(appshortcutzone1.getVisibleChildCount() < appshortcutzone1.getColumnCount())
                appshortcutzone1.arrangeItem(k1);
            View view7 = appshortcutzone1.addItem(view6, k1);
            view6.setVisibility(4);
            if(view7.getTag() != null)
            {
                View view8 = addItem(view7, 0);
                view8.setVisibility(4);
                view6.setVisibility(4);
                AppMenu appmenu2 = (AppMenu)getChildAt(getDropScreen());
                appmenu2.cellToPoint(0, ai);
                mDragger.startAnimation(view8, view7.getLeft() + appshortcutzone1.getLeft(), ai[0], view7.getTop() + appshortcutzone1.getTop(), appmenu2.getTop() + ai[1], mScrollX, mScrollY, 300);
            }
            mDragView = view6;
        }
        appshortcutzone1.setEditIndex(k1);
_L7:
        mDragCell = k1;
        continue; /* Loop/switch isn't completed */
_L6:
        if(k1 == -1 && mDragView != null)
        {
            View view2 = mDragView;
            View view3 = (View)view2.getParent();
            AppMenu appmenu1 = (AppMenu)getChildAt(getDropScreen());
            if(!(view3 instanceof AppShortcutZone) && view3 != appmenu1)
            {
                removeItem(view2);
                View view4 = addItem(view2, k1);
                view4.setVisibility(4);
                int l1 = appmenu1.indexOfChild(view4);
                appmenu1.setEditIndex(l1);
                mDropCell = l1;
                mDragView = view4;
            }
        }
        if(true) goto _L7; else goto _L4
_L4:
        int i1 = getIndexOfCell(i, j);
        if(i1 != -1 && i1 != mDragCell && mDragView != null)
        {
            View view = mDragView;
            View view1;
            AppMenu appmenu;
            int j1;
            if((View)view.getParent() instanceof AppShortcutZone)
                ((AppShortcutZone)view.getParent()).removeItem(view);
            else
                removeItem(view);
            view1 = addItem(view, i1);
            view1.setVisibility(4);
            appmenu = (AppMenu)getChildAt(getDropScreen());
            j1 = appmenu.indexOfChild(view1);
            appmenu.setEditIndex(j1);
            mDropCell = j1;
            mDragView = view1;
        }
        mDragCell = i1;
        if(true) goto _L9; else goto _L8
_L8:
    }

    public void onDragStart(View view, DragSource dragsource, Object obj, int i)
    {
        if(!(dragsource instanceof AppShortcutZone)) goto _L2; else goto _L1
_L1:
        AppShortcutZone appshortcutzone = (AppShortcutZone)dragsource;
        mDragView = view;
        int k = appshortcutzone.indexOfChild(view);
        mDropCell = k;
        mDragCell = k;
        appshortcutzone.setEditIndex(mDropCell);
_L4:
        return;
_L2:
        AppMenu appmenu = (AppMenu)getChildAt(getDropScreen());
        if(appmenu != null)
        {
            mDragView = view;
            int j = appmenu.indexOfChild(view);
            mDropCell = j;
            mDragCell = j;
            appmenu.setEditIndex(mDropCell);
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        if(mDragView != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        Rect rect = mDragRect;
        AppShortcutZone appshortcutzone = mLauncher.getAppShortcutZone();
        appshortcutzone.getHitRect(rect);
        AppMenu appmenu = (AppMenu)getChildAt(getDropScreen());
        ViewGroup viewgroup = (ViewGroup)mDragView.getParent();
        if(viewgroup != null)
        {
            int ai[] = new int[2];
            if(viewgroup instanceof AppShortcutZone)
            {
                mDragger.startAnimation(mDragView, i - k, appshortcutzone.getLeft() + mDragView.getLeft(), j - l, appshortcutzone.getTop() + mDragView.getTop(), 0.0F, 0.0F, 300);
            } else
            {
                if(viewgroup != appmenu)
                {
                    int i1 = getIndexOfCell(i, j);
                    if(i1 != -1 && mDragView != null || rect.contains(i, j))
                    {
                        View view = mDragView;
                        View view1;
                        if(viewgroup instanceof AppShortcutZone)
                            ((AppShortcutZone)view.getParent()).removeItem(view);
                        else
                            removeItem(view);
                        view1 = addItem(view, i1);
                        view1.setVisibility(4);
                        appmenu.setEditIndex(appmenu.indexOfChild(view1));
                        mDragView = view1;
                    }
                }
                appmenu.cellToPoint(mDragView, ai);
                mDragger.startAnimation(mDragView, i - k, (appmenu.getLeft() + ai[0]) - mScrollX, (j - l) + mScrollY, appmenu.getTop() + ai[1], mScrollX, mScrollY, 300);
            }
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
        if(!flag) goto _L2; else goto _L1
_L1:
        addBlankPage();
_L4:
        mDragView = null;
        mDragCell = -1;
        mDropCell = -1;
        return;
_L2:
        if(mDragView != null)
            mDragView.setVisibility(0);
        if(true) goto _L4; else goto _L3
_L3:
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        int i;
        boolean flag1;
        if(mLocked)
            flag1 = true;
        else
        if(mAnimating)
            flag1 = true;
        else
        if(mMode == 1)
        {
            flag1 = false;
        } else
        {
label0:
            {
                if(mVelocityTracker == null)
                    mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionevent);
                i = motionevent.getAction();
                if(i != 2 || mTouchState == 0)
                    break label0;
                flag1 = true;
            }
        }
_L6:
        return flag1;
        float f;
        float f1;
        f = motionevent.getX();
        f1 = motionevent.getY();
        i;
        JVM INSTR lookupswitch 5: default 148
    //                   0: 417
    //                   1: 511
    //                   2: 165
    //                   3: 511
    //                   261: 590;
           goto _L1 _L2 _L3 _L4 _L3 _L5
_L1:
        int j;
        int k;
        int l;
        int i1;
        boolean flag;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        boolean flag2;
        boolean flag3;
        int k2;
        int l2;
        int i3;
        int j3;
        if(mTouchState != 0)
            flag = true;
        else
            flag = false;
        flag1 = flag;
          goto _L6
_L4:
        if(!mMultiTouchUsed) goto _L8; else goto _L7
_L7:
        k2 = (int)motionevent.getY(0);
        l2 = (int)motionevent.getX(0);
        i3 = (int)motionevent.getY(1);
        j3 = (int)motionevent.getX(1);
        mMovePinch = mMovePinchStart - (int)Math.sqrt((k2 - i3) * (k2 - i3) + (l2 - j3) * (l2 - j3));
        if(!mLauncher.getQuickViewMainMenu().isOpened() && mLauncher.getStateQuickNavigation() < 0) goto _L10; else goto _L9
_L9:
        flag1 = false;
          goto _L6
_L10:
        if(mMovePinch <= 100) goto _L12; else goto _L11
_L11:
        mMultiTouchUsed = false;
        mLauncher.getQuickViewMainMenu().drawOpenAnimation();
        mLauncher.openQuickViewMainMenu();
        flag1 = true;
          goto _L6
_L12:
        mTouchState = 0;
          goto _L1
_L8:
        l1 = (int)Math.abs(f - mDownX);
        i2 = (int)Math.abs(f1 - mLastMotionY);
        j2 = mTouchSlop;
        if(l1 > j2)
            flag2 = true;
        else
            flag2 = false;
        if(i2 > j2)
            flag3 = true;
        else
            flag3 = false;
        if(flag2 || flag3)
        {
            if(flag2)
                mTouchState = 1;
            else
                mLastMotionX = f;
        } else
        {
            mLastMotionX = f;
        }
          goto _L1
_L2:
        mLastMotionX = f;
        mDownX = f;
        mLastMotionY = f1;
        enableChildrenCache(mCurrentScreen - 1, 1 + mCurrentScreen);
        if(!mScroller.isFinished()) goto _L14; else goto _L13
_L13:
        k1 = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
        mTouchedPageIndicatorIndex = k1;
        if(k1 == -1) goto _L15; else goto _L14
_L14:
        j1 = 1;
_L16:
        mTouchState = j1;
          goto _L1
_L15:
        j1 = 0;
          goto _L16
_L3:
        clearChildrenCache();
        mTouchState = 0;
        if(!mMultiTouchUsed) goto _L1; else goto _L17
_L17:
        mMultiTouchUsed = false;
        if(!mLauncher.getQuickViewMainMenu().isOpened() && mLauncher.getStateQuickNavigation() < 0) goto _L19; else goto _L18
_L18:
        flag1 = false;
          goto _L6
_L19:
        if(mMovePinch > 100)
        {
            mLauncher.getQuickViewMainMenu().drawOpenAnimation();
            mLauncher.openQuickViewMainMenu();
        }
          goto _L1
_L5:
        if(mMode != 2)
        {
            j = (int)motionevent.getY(0);
            k = (int)motionevent.getX(0);
            l = (int)motionevent.getY(1);
            i1 = (int)motionevent.getX(1);
            mMultiTouchUsed = true;
            mMovePinchStart = (int)Math.sqrt((j - l) * (j - l) + (k - i1) * (k - i1));
        }
          goto _L1
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = 0;
        int j1 = getChildCount();
        int k1 = mTopOffset;
        if(mMode == 1)
            k1 = 0;
        for(int l1 = 0; l1 < j1; l1++)
        {
            View view = getChildAt(l1);
            if(view.getVisibility() != 8)
            {
                int i2 = view.getMeasuredWidth();
                view.layout(i1, k1, (i1 + i2) - getPaddingRight(), view.getMeasuredHeight() - getPaddingBottom());
                i1 += i2;
            }
        }

        initPageIndicator();
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        if(android.view.View.MeasureSpec.getMode(i) != 0x40000000)
            throw new IllegalStateException("Menulayout can only be used in EXACTLY mode.");
        if(mMode == 1)
        {
            k -= mRightOffset;
            i = android.view.View.MeasureSpec.makeMeasureSpec(k, 0x40000000);
        }
        if(android.view.View.MeasureSpec.getMode(j) != 0x40000000)
            throw new IllegalStateException("Menulayout can only be used in EXACTLY mode.");
        int l = getChildCount();
        for(int i1 = 0; i1 < l; i1++)
            getChildAt(i1).measure(i, j);

        if(mFirstLayout)
            scrollTo(k * mCurrentScreen, 0);
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect)
    {
        if(getChildCount() > 0)
        {
            int j;
            View view;
            if(mNextScreen != -1)
                j = mNextScreen;
            else
                j = mCurrentScreen;
            view = getChildAt(j);
            if(view != null)
                view.requestFocus(i, rect);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        if(!mLocked) goto _L2; else goto _L1
_L1:
        boolean flag = true;
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
        JVM INSTR tableswitch 0 3: default 84
    //                   0 90
    //                   1 531
    //                   2 165
    //                   3 795;
           goto _L3 _L4 _L5 _L6 _L7
_L4:
        break; /* Loop/switch isn't completed */
_L3:
        break; /* Loop/switch isn't completed */
_L7:
        break MISSING_BLOCK_LABEL_795;
_L10:
        flag = true;
        if(true) goto _L9; else goto _L8
_L8:
        if(!mScroller.isFinished())
            mScroller.abortAnimation();
        mLastMotionX = f;
        mDownX = f;
        mLastMotionY = f1;
        int k3 = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
        mTouchedPageIndicatorIndex = k3;
        if(k3 != -1)
            mIsSingleTap = true;
          goto _L10
_L6:
        int j2;
        int j3;
        if(mTouchState != 1)
            break MISSING_BLOCK_LABEL_414;
        j2 = (int)(mLastMotionX - f);
        if(!mIsSingleTap)
            break MISSING_BLOCK_LABEL_228;
        j3 = (int)(mLastMotionY - f1);
        if(Math.abs(j2) <= mTouchSlop && Math.abs(j3) <= mTouchSlop) goto _L10; else goto _L11
_L11:
        mIsSingleTap = false;
        if(mPageIndicator != null)
        {
            mPageIndicator.show();
            mIsIndicatorShow = true;
            removeCallbacks(mHideIndicator);
        }
        mLastMotionX = f;
        int k2 = getChildCount();
        (k2 - 1) * getWidth();
        if(k2 >= 2)
            scrollBy(j2, 0);
        else
        if(j2 < 0)
        {
            int i3 = -(getWidth() / 2);
            if(mScrollX > i3)
                if(mScrollX > 0)
                    scrollBy(j2, 0);
                else
                    scrollBy(Math.max(i3 - mScrollX, j2), 0);
        } else
        if(j2 > 0)
        {
            int l2 = (getChildAt(getChildCount() - 1).getRight() - getWidth()) + getWidth() / 2;
            if(mScrollX < l2)
                scrollBy(Math.min(l2 - mScrollX, j2), 0);
        }
          goto _L10
        int k1 = (int)Math.abs(f - mDownX);
        int l1 = (int)Math.abs(f1 - mLastMotionY);
        int i2 = mTouchSlop;
        boolean flag1;
        boolean flag2;
        if(k1 > i2)
            flag1 = true;
        else
            flag1 = false;
        if(l1 > i2)
            flag2 = true;
        else
            flag2 = false;
        if(getChildCount() > 0 && (flag1 || flag2))
        {
            if(flag1 && mMode != 1)
                mTouchState = 1;
            else
                mLastMotionX = f;
        } else
        {
            mLastMotionX = f;
        }
          goto _L10
_L5:
label0:
        {
            if(mTouchState != 1)
                break MISSING_BLOCK_LABEL_734;
            if(!mIsSingleTap)
                break label0;
            int l = getPageIndicatorArea(mScrollX + (int)f, mScrollY + (int)f1);
            if(l != -1)
            {
                int i1 = mTouchedPageIndicatorIndex;
                if(l == i1)
                {
                    if(l == -2)
                    {
                        l = mCurrentScreen - 9;
                        if(l < 0)
                            l = 0;
                    } else
                    if(l == -3)
                    {
                        l = 9 + mCurrentScreen;
                        int j1 = getChildCount();
                        if(l >= j1)
                            l = j1 - 1;
                    }
                    snapToScreen(l);
                }
            }
            mIsSingleTap = false;
        }
          goto _L10
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
        mVelocityTracker.recycle();
        mVelocityTracker = null;
        mTouchState = 0;
        postDelayed(mHideIndicator, 1000L);
          goto _L10
        mTouchState = 0;
        postDelayed(mHideIndicator, 1000L);
          goto _L10
    }

    public void open()
    {
        mIsClosing = false;
        mMenuCloseAnimationListener.interrupt();
        mMenuOpenAnimationListener.interrupt();
        SamsungUtils.setWallpaperVisibility(mWallpaperManager, false);
        int i = mItemNumOfPage / mColumnNum;
        int j = getChildCount();
        for(int k = 0; k < j; k++)
        {
            MenuItemView menuitemview = (MenuItemView)getChildAt(k);
            menuitemview.setVisibility(0);
            menuitemview.applyRotation(0.0F, 360F, 50 * (k / i));
        }

        mLauncher.getMenuDrawer().setBackgroundImage();
        mLauncher.getMenuDrawer().setVisibility(0);
        mLauncher.getMenuDrawer().setEnabledProgress(false);
        mLauncher.getAppShortcutZone().changeApplicationsIcon();
        mOpenFlag = true;
        mLauncher.getAppShortcutZone().drawBg(true, false);
        TopFourZone topfourzone = mLauncher.getTopFourZone();
        if(topfourzone != null)
            if(Launcher.USE_MAINMENU_ICONMODE)
                topfourzone.setBackgroundResource(0x7f02004e);
            else
                topfourzone.setVisibility(4);
    }

    public void removeApps(ArrayList arraylist)
    {
        int i = arraylist.size();
        int j = 0;
        while(j < i) 
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)arraylist.get(j);
            int k = findAppByComponent(mAllAppsList, applicationinfo);
            if(k >= 0)
                mAllAppsList.remove(k);
            else
                Log.w("Launcher.MenuManager", (new StringBuilder()).append("couldn't find a match for item \"").append(applicationinfo).append("\"").toString());
            j++;
        }
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    void removeItem(View view)
    {
        int i = getChildCount();
        AppMenu appmenu = (AppMenu)view.getParent();
        int j = 0;
        do
        {
label0:
            {
                if(j < i)
                {
                    if(getChildAt(j) != appmenu)
                        break label0;
                    appmenu.removeView(view);
                }
                return;
            }
            j++;
        } while(true);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean flag)
    {
        int i = indexOfChild(view);
        int j = getWhichScreen();
        int k = Math.abs(i - j);
        boolean flag1;
        if((i != j || !mScroller.isFinished()) && k < MIN_GAP)
        {
            snapToScreen(i);
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        return flag1;
    }

    void resume()
    {
        int i;
        int j;
        i = getChildCount();
        j = mCurrentScreen;
        if(i <= 1) goto _L2; else goto _L1
_L1:
        if(j >= 0) goto _L4; else goto _L3
_L3:
        j = i - 1;
        mCurrentScreen = j;
_L2:
        mNextScreen = -1;
        int k = getWidth();
        if(k != 0)
        {
            Scroller scroller = mScroller;
            if(scroller != null && !scroller.isFinished())
                scroller.abortAnimation();
            scrollTo(j * k, 0);
        }
        return;
_L4:
        if(j >= i)
        {
            j = 0;
            mCurrentScreen = 0;
        }
        if(true) goto _L2; else goto _L5
_L5:
    }

    public void scrollLeft()
    {
        if(mNextScreen == -1 && mCurrentScreen >= 0 && mScroller.isFinished())
            snapToScreen(mCurrentScreen - 1);
    }

    public void scrollRight()
    {
        if(mNextScreen == -1 && mCurrentScreen <= getChildCount() - 1 && mScroller.isFinished())
            snapToScreen(1 + mCurrentScreen);
    }

    public void setAdapter(ApplicationsAdapter applicationsadapter)
    {
        if(mAdapter != null)
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        mAdapter = applicationsadapter;
        if(mAdapter != null)
        {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            updateMenu();
        }
    }

    public void setApps(ArrayList arraylist)
    {
        mAllAppsList.clear();
        addApps(arraylist);
    }

    public void setCurrentScreen(int i)
    {
        mCurrentScreen = i;
    }

    public void setDragger(DragController dragcontroller)
    {
        mDragger = dragcontroller;
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    public void setMode(int i)
    {
        if(mMode == i) goto _L2; else goto _L1
_L1:
        mMode = i;
        if(!mScroller.isFinished())
        {
            mScroller.abortAnimation();
            computeScroll();
        }
        if(i != 2) goto _L4; else goto _L3
_L3:
        ((DragLayer)mDragger).setDragMenuListener(this);
        updateMenu();
_L6:
        mLauncher.saveMenuMode(i);
_L2:
        return;
_L4:
        ((DragLayer)mDragger).setDragMenuListener(null);
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
        if(true) goto _L6; else goto _L5
_L5:
    }

    void shiftItem(View view, int i)
    {
        AppMenu appmenu;
        ApplicationInfo applicationinfo;
        int j;
        ApplicationInfo applicationinfo3;
        if(getChildCount() <= i)
            appmenu = makeMenuView();
        else
            appmenu = (AppMenu)getChildAt(i);
        applicationinfo = (ApplicationInfo)view.getTag();
        j = mAdapter.getPosition(applicationinfo);
        if(j >= 0) goto _L2; else goto _L1
_L1:
        applicationinfo3 = copyAdapterInfo(mAdapter, applicationinfo);
        if(applicationinfo3 != null) goto _L4; else goto _L3
_L3:
        return;
_L4:
        ApplicationInfo applicationinfo1;
        int k;
        int j1 = mAdapter.getPosition(applicationinfo3);
        applicationinfo1 = applicationinfo3;
        k = j1;
_L5:
        View view1 = makeItemView(k, appmenu);
        view1.setTag(applicationinfo1);
        appmenu.addView(view1, 0);
        int l = appmenu.getChildCount();
        if(l > mItemNumOfPage)
        {
            View view2 = appmenu.getChildAt(l - 1);
            appmenu.removeView(view2);
            shiftItem(view2, i + 1);
        }
        int i1 = 0;
        while(i1 < l && i1 < mItemNumOfPage) 
        {
            ApplicationInfo applicationinfo2 = (ApplicationInfo)appmenu.getChildAt(i1).getTag();
            applicationinfo2.editTopNum = 65535;
            applicationinfo2.editPageNum = i;
            applicationinfo2.editCellNum = i1;
            i1++;
        }
        if(true) goto _L3; else goto _L2
_L2:
        applicationinfo1 = applicationinfo;
        k = j;
          goto _L5
    }

    void snapToScreen(int i)
    {
        snapToScreen(i, mSnapToScreenDuration);
    }

    void snapToScreen(int i, int j)
    {
        if(!isListChild()) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int k;
        enableChildrenCache(mCurrentScreen - 1, 1 + mCurrentScreen);
        k = getChildCount();
        boolean flag;
        View view;
        int l;
        if(k >= 2)
        {
            if(i >= 0 && i < k)
                i = Math.max(0, Math.min(i, k - 1));
        } else
        {
            i = Math.max(0, Math.min(i, k - 1));
        }
        if(i != mCurrentScreen)
            flag = true;
        else
            flag = false;
        view = getFocusedChild();
        if(view != null && flag && view == getChildAt(mCurrentScreen))
            view.clearFocus();
        l = i * getWidth() - mScrollX;
        if(i >= 0)
            break; /* Loop/switch isn't completed */
        i = k - 1;
_L5:
        mNextScreen = i;
        if(j < 0)
            j = mSnapToScreenDuration;
        mScroller.startScroll(mScrollX, 0, l, 0, j);
        invalidate();
        mCurrentScreen = mNextScreen;
        if(true) goto _L1; else goto _L3
_L3:
        if(i < k) goto _L5; else goto _L4
_L4:
        i = 0;
          goto _L5
    }

    public void startUpdateDB()
    {
        mUpdateDBTask = new UpdateDBTask();
        UpdateDBTask updatedbtask = mUpdateDBTask;
        ApplicationsAdapter aapplicationsadapter[] = new ApplicationsAdapter[1];
        aapplicationsadapter[0] = mAdapter;
        updatedbtask.execute(aapplicationsadapter);
    }

    public void stopUpdateDB()
    {
        if(mUpdateDBTask != null)
            mUpdateDBTask.cancel(true);
    }

    public void unlock()
    {
        mLocked = false;
    }

    public void updateApps(ArrayList arraylist)
    {
        Log.i("welgate", "updateApps");
        listsetApps(arraylist);
    }

    public void updateDrawingCacheForApplicationBadgeCountChange(List list)
    {
        BadgeCache badgecache = ((LauncherApplication)mLauncher.getApplicationContext()).getBadgeCache();
        if(mMode != 1)
        {
            int i = 0;
            int j = getChildCount();
            while(i < j) 
            {
                ViewGroup viewgroup = (ViewGroup)getChildAt(i);
                int k = 0;
                for(int l = viewgroup.getChildCount(); k < l; k++)
                {
                    View view = viewgroup.getChildAt(k);
                    ApplicationInfo applicationinfo = (ApplicationInfo)view.getTag();
                    applicationinfo.badgeCount = badgecache.getBadgeCount(applicationinfo.intent);
                    Bitmap bitmap = badgecache.getBadgeIcon(applicationinfo.intent.getComponent());
                    if(bitmap != null)
                        view.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
                    view.destroyDrawingCache();
                    view.buildDrawingCache();
                    view.invalidate();
                }

                i++;
            }
        }
    }

    private int MIN_GAP;
    private int SNAP_VELOCITY;
    private ApplicationsAdapter mAdapter;
    private ArrayList mAllAppsList;
    private Animation mAniFadeIn;
    private Animation mAniFadeOut;
    private boolean mAnimating;
    private long mAnimationStartTime;
    private AnticipateInterpolator mAnticipateInterpolator;
    private ApplicationInfo mClickedApp;
    private Animation mCloseAnimations[];
    private final int mColumnNum;
    private int mCurrentScreen;
    private AdapterDataSetObserver mDataSetObserver;
    private Runnable mDelayedAppClickRunnable;
    private Bitmap mDeleteIcon;
    private float mDownX;
    int mDragCell;
    private Rect mDragRect;
    View mDragView;
    private DragController mDragger;
    int mDropCell;
    private Bitmap mEditMenuBg;
    private Bitmap mEditTopBg;
    private boolean mEnablePageIndicatorShowHide;
    private boolean mFirstLayout;
    private final Runnable mHideIndicator = new Runnable() {

        public void run()
        {
            if(mPageIndicator != null)
            {
                mPageIndicator.hide();
                postInvalidate();
                if(mEnablePageIndicatorShowHide)
                    mIsIndicatorShow = false;
            }
        }

        final MenuManager this$0;

            
            {
                this$0 = MenuManager.this;
                super();
            }
    }
;
    private boolean mIsClosing;
    private boolean mIsIndicatorShow;
    private boolean mIsSingleTap;
    private int mItemNumOfPage;
    private float mLastMotionX;
    private float mLastMotionY;
    private Launcher mLauncher;
    private boolean mLocked;
    private int mMaximumVelocity;
    private MenuCloseAnimationController mMenuCloseAni;
    InterruptableAnimationListener mMenuCloseAnimationListener;
    private MenuOpenAnimationController mMenuOpenAni;
    InterruptableAnimationListener mMenuOpenAnimationListener;
    private int mMode;
    private int mMovePinch;
    private int mMovePinchStart;
    private boolean mMultiTouchUsed;
    private int mNextScreen;
    private android.view.View.OnClickListener mOnClickListener;
    private android.view.View.OnLongClickListener mOnLongClickListener;
    private Interpolator mOpenAlphaAnimationInterpolator;
    private Animation mOpenAnimations[];
    private boolean mOpenFlag;
    public final LauncherModel.ApplicationInfoComparator mOrderComparator = new LauncherModel.ApplicationInfoComparator();
    private int mOrientation;
    private OvershootInterpolator mOvershootInterpolator;
    private PageIndicator mPageIndicator;
    private int mPageIndicatorBottom;
    private int mPageIndicatorGap;
    private Drawable mPageIndicatorLarge;
    private int mPageIndicatorLeft;
    private Drawable mPageIndicatorMiddle;
    private Drawable mPageIndicatorMore;
    private Drawable mPageIndicatorMoreDim[];
    private int mPageIndicatorMoreGap;
    private int mPageIndicatorOffsetY;
    private Drawable mPageIndicatorSmall;
    private int mPageIndicatorTop;
    private Paint mPaint;
    private int mPoint[];
    private int mRightOffset;
    private Scroller mScroller;
    private int mSnapToScreenDuration;
    private int mTempPage;
    private int mTextSize;
    private int mTopOffset;
    private int mTouchSlop;
    private int mTouchState;
    private int mTouchedPageIndicatorIndex;
    private UpdateDBTask mUpdateDBTask;
    private final boolean mUseLargeDrawablesOnlyForPageIndicator;
    private VelocityTracker mVelocityTracker;
    private WallpaperManager mWallpaperManager;








/*
    static long access$1602(MenuManager menumanager, long l)
    {
        menumanager.mAnimationStartTime = l;
        return l;
    }

*/


/*
    static boolean access$1702(MenuManager menumanager, boolean flag)
    {
        menumanager.mIsClosing = flag;
        return flag;
    }

*/


/*
    static boolean access$202(MenuManager menumanager, boolean flag)
    {
        menumanager.mIsIndicatorShow = flag;
        return flag;
    }

*/





/*
    static ApplicationInfo access$502(MenuManager menumanager, ApplicationInfo applicationinfo)
    {
        menumanager.mClickedApp = applicationinfo;
        return applicationinfo;
    }

*/




}
