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

import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.*;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import java.util.List;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragSource, ApplicationInfo, LauncherApplication, Launcher, 
//            MenuItemView, MenuManager, Workspace, CellLayout, 
//            LauncherModel, FastBitmapDrawable, BadgeCache, DragController, 
//            IconCache

public class AppShortcutZone extends LinearLayout
    implements DragSource, MenuItemView.UninstallableMarkerDrawParent
{
    static class LateStartLinearInterpolator
        implements Interpolator
    {

        public float getInterpolation(float f)
        {
            float f2;
            if(f <= mStartFactor)
            {
                f2 = 0.0F;
            } else
            {
                float f1 = (1.0F * (f - mStartFactor)) / (1.0F - mStartFactor);
                if(f1 > 1.0F)
                    f1 = 1.0F;
                f2 = f1;
            }
            return f2;
        }

        private float mStartFactor;

        public LateStartLinearInterpolator(float f)
        {
            mStartFactor = 0.0F;
            if(f > 0.0F && f < 1.0F)
                mStartFactor = f;
        }
    }

    static class PrematureLinearInterpolator
        implements Interpolator
    {

        public float getInterpolation(float f)
        {
            float f1 = (f * 1.0F) / mPrematureFactor;
            if(f1 > 1.0F)
                f1 = 1.0F;
            return f1;
        }

        private float mPrematureFactor;

        public PrematureLinearInterpolator(float f)
        {
            mPrematureFactor = 1.0F;
            if(f > 0.0F && f < 1.0F)
                mPrematureFactor = f;
        }
    }

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

        public void start(View view, Rect rect)
        {
            stop();
            mTargetView = view;
            Rect rect1 = mRect;
            view.getHitRect(rect1);
            mScroller.startScroll(rect1.left, rect1.top, rect.left - rect1.left, rect.top - rect1.top, 300);
            post(this);
        }

        public void stop()
        {
            removeCallbacks(this);
        }

        private Rect mRect;
        private Scroller mScroller;
        private View mTargetView;
        final AppShortcutZone this$0;

        Animate()
        {
            this$0 = AppShortcutZone.this;
            super();
            mRect = new Rect();
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }
    }


    public AppShortcutZone(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mAnimationState = 3;
        mEnabledDrawing = true;
        mOrientation = 1;
        mOldOrientation = -1;
        mTmpRect = new Rect();
        mEditIndex = -1;
        mOpenInterpolator = new PrematureLinearInterpolator(0.75F);
        mCloseInterpolator = new LateStartLinearInterpolator(0.5F);
        mInterpolator = mOpenInterpolator;
        mOnClickListener = new android.view.View.OnClickListener() {

            public void onClick(View view)
            {
                Object obj = view.getTag();
                if(obj != null && (obj instanceof ApplicationInfo))
                {
                    ApplicationInfo applicationinfo = (ApplicationInfo)obj;
                    if(mLauncher.getMenuManager().getMode() == 2)
                    {
                        if(!applicationinfo.systemApp)
                            mLauncher.showDeleteApplication(applicationinfo.intent.getComponent().getPackageName());
                    } else
                    {
                        mLauncher.startActivitySafely(applicationinfo.intent, null);
                    }
                }
            }

            final AppShortcutZone this$0;

            
            {
                this$0 = AppShortcutZone.this;
                super();
            }
        }
;
        mOnLongClickListener = new android.view.View.OnLongClickListener() {

            public boolean onLongClick(View view)
            {
                boolean flag;
                if(!view.isInTouchMode())
                    flag = false;
                else
                if(!mLauncher.getMenuManager().isOpened())
                {
                    view.setHapticFeedbackEnabled(false);
                    flag = true;
                } else
                {
                    Object obj = view.getTag();
                    if(obj != null && (obj instanceof ApplicationInfo))
                    {
                        ApplicationInfo applicationinfo = (ApplicationInfo)obj;
                        if(mLauncher.getMenuManager().getMode() == 2)
                        {
                            mDragger.startDrag(view, AppShortcutZone.this, applicationinfo, 0);
                        } else
                        {
                            mDragger.startDrag(view, AppShortcutZone.this, applicationinfo, 1);
                            mLauncher.closeAllApplications();
                        }
                    }
                    flag = true;
                }
                return flag;
            }

            final AppShortcutZone this$0;

            
            {
                this$0 = AppShortcutZone.this;
                super();
            }
        }
;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.AppShortcutZone, 0, 0);
        mColumnCount = typedarray.getInt(0, 4);
        typedarray.recycle();
        if(mColumnCount < 1)
        {
            throw new IllegalStateException("AppShortcutZone_iconColumnCount should be at least 1");
        } else
        {
            mVirtualView = new View[mColumnCount - 1];
            mChildAnimate = new Animate[mColumnCount];
            init(context);
            return;
        }
    }

    private ApplicationInfo getApplicationInfo(Context context, ComponentName componentname)
    {
        ApplicationInfo applicationinfo = null;
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentname);
        List list = context.getPackageManager().queryIntentActivities(intent, 0);
        if(list != null && list.size() > 0)
            applicationinfo = new ApplicationInfo((ResolveInfo)list.get(0), mIconCache);
        return applicationinfo;
    }

    private int getFocusChild()
    {
        int i = getEnabledChildCount();
        int ai[] = new int[i];
        int j = getChildCount() - 1;
        int k = 0;
        while(j >= 0) 
        {
            int l;
            int i1;
            int ai1[];
            int j1;
            int k1;
            if(getChildAt(j).getVisibility() != 8)
            {
                k1 = k + 1;
                ai[k] = j;
            } else
            {
                k1 = k;
            }
            j--;
            k = k1;
        }
        l = 0;
        k;
        if(l >= i)
            break MISSING_BLOCK_LABEL_122;
        ai1 = getChildAt(ai[l]).getDrawableState();
        j1 = 0;
_L5:
        if(j1 >= ai1.length) goto _L2; else goto _L1
_L1:
        if(ai1[j1] != 0x101009c) goto _L4; else goto _L3
_L3:
        i1 = l;
_L6:
        return i1;
_L4:
        j1++;
          goto _L5
_L2:
        l++;
        break MISSING_BLOCK_LABEL_63;
        i1 = -1;
          goto _L6
    }

    private int getItemGap(int i)
    {
        int j = 0;
        i;
        JVM INSTR tableswitch 2 3: default 24
    //                   2 34
    //                   3 26;
           goto _L1 _L2 _L3
_L1:
        return j;
_L3:
        j = mItemGap1;
        continue; /* Loop/switch isn't completed */
_L2:
        j = mItemGap2;
        if(true) goto _L1; else goto _L4
_L4:
    }

    private void init(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mPaint = new Paint();
        mIconCache = ((LauncherApplication)context.getApplicationContext()).getIconCache();
        Resources resources = getResources();
        mOrientation = resources.getConfiguration().orientation;
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mChildHeight = resources.getDimensionPixelSize(0x7f090030);
            mTopOffset = 0;
        } else
        {
            mChildHeight = resources.getDimensionPixelSize(0x7f09000c);
            mTopOffset = resources.getDimensionPixelSize(0x7f09000b);
        }
        mChildWidth = resources.getDimensionPixelSize(0x7f09000d);
        mEditLeftOffset = resources.getDimensionPixelSize(0x7f090011);
        mEditLeftOffset2 = resources.getDimensionPixelSize(0x7f090010);
        mEditTopOffset = resources.getDimensionPixelSize(0x7f090012);
        mDeleteIconTopOffset = resources.getDimensionPixelSize(0x7f090016);
        mDeleteIconRightOffset = resources.getDimensionPixelSize(0x7f090017);
        mIconWidth = resources.getDimensionPixelSize(0x7f090013);
        mLRPadding = resources.getDimensionPixelSize(0x7f090018);
        mItemGap1 = resources.getDimensionPixelSize(0x7f09000e);
        mItemGap2 = resources.getDimensionPixelSize(0x7f09000f);
        setClickable(true);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mApplicationsDrawableNormal = resources.getDrawable(0x7f02009b);
            mApplicationsDrawableMenu = resources.getDrawable(0x7f0200d0);
        } else
        {
            mApplicationsDrawableNormal = resources.getDrawable(0x7f02009a);
            mApplicationsDrawableMenu = resources.getDrawable(0x7f0200cf);
        }
        mApplicationsDrawableEdit = mApplicationsDrawableMenu;
        for(int i = 0; i < mColumnCount - 1; i++)
        {
            mVirtualView[i] = new View(context);
            mVirtualView[i].setVisibility(8);
        }

        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mBgDrawable = resources.getDrawable(0x7f02004d);
            mBgDrawable2 = resources.getDrawable(0x7f02002e);
        } else
        {
            mBgDrawable = resources.getDrawable(0x7f02004a);
            mBgDrawable2 = resources.getDrawable(0x7f02002d);
        }
        initApplicationsView();
        setDrawingCacheEnabled(true);
        buildDrawingCache(true);
        for(int j = 0; j < mColumnCount; j++)
            mChildAnimate[j] = new Animate();

    }

    private void initApplicationsView()
    {
        View view;
        MenuItemView menuitemview;
        int i;
        int j;
        int k;
        int l;
        if(Launcher.USE_MAINMENU_ICONMODE)
            view = mInflater.inflate(0x7f030005, this, false);
        else
            view = mInflater.inflate(0x7f030004, this, false);
        menuitemview = (MenuItemView)view;
        menuitemview.setImageDrawable(mApplicationsDrawableNormal);
        i = view.getPaddingTop();
        j = view.getPaddingLeft();
        k = view.getPaddingBottom();
        l = view.getPaddingRight();
        if(Launcher.USE_MAINMENU_ICONMODE)
            menuitemview.setBackgroundDrawable(null);
        else
            menuitemview.setBackgroundResource(0x7f020015);
        menuitemview.setPadding(j, i, l, k);
        menuitemview.setFocusable(true);
        menuitemview.setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1)
            {
                mLauncher.closeOptionsMenu();
                MenuManager menumanager = mLauncher.getMenuManager();
                if(menumanager.isOpened())
                {
                    if(menumanager.getMode() == 2)
                    {
                        mLauncher.menusave();
                        menumanager.setMode(0);
                    } else
                    {
                        menumanager.animateClose();
                    }
                } else
                {
                    menumanager.animateOpen();
                }
            }

            final AppShortcutZone this$0;

            
            {
                this$0 = AppShortcutZone.this;
                super();
            }
        }
);
        mApplicationsView = menuitemview;
        for(int i1 = 0; i1 < mColumnCount - 1; i1++)
            addView(mVirtualView[i1]);

        addView(mApplicationsView);
    }

    View addItem(View view, int i)
    {
        View view1 = getChildAt(i);
        removeViewAt(i);
        addView(view, i);
        Object obj = view.getTag();
        if(obj != null)
        {
            ApplicationInfo applicationinfo = (ApplicationInfo)obj;
            applicationinfo.editTopNum = i;
            applicationinfo.editPageNum = 65535;
            applicationinfo.editCellNum = 65535;
        }
        return view1;
    }

    void arrangeItem(int i)
    {
        if(getChildAt(i).getTag() != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int j;
        int k;
        int l;
        j = getChildCount();
        k = -1;
        l = i + 1;
_L8:
        if(l >= j) goto _L4; else goto _L3
_L3:
        if(getChildAt(l).getVisibility() == 0) goto _L6; else goto _L5
_L5:
        k = l;
_L4:
        int j1;
        if(k != -1)
            continue; /* Loop/switch isn't completed */
        j1 = i - 1;
_L9:
        if(j1 < 0)
            continue; /* Loop/switch isn't completed */
        if(getChildAt(j1).getVisibility() == 0)
            break MISSING_BLOCK_LABEL_154;
        k = j1;
        if(k == -1) goto _L1; else goto _L7
_L7:
        removeViewAt(k);
        addView(mVirtualView[i], i);
        int i1 = 0;
        while(i1 < j) 
        {
            Object obj = getChildAt(i1).getTag();
            if(obj != null && (obj instanceof ApplicationInfo))
                ((ApplicationInfo)obj).editTopNum = i1;
            i1++;
        }
          goto _L1
_L6:
        l++;
          goto _L8
        j1--;
          goto _L9
    }

    public void changeApplicationsIcon()
    {
        MenuManager menumanager = mLauncher.getMenuManager();
        MenuItemView menuitemview = (MenuItemView)mApplicationsView;
        if(!menumanager.isOpened())
            menuitemview.setImageDrawable(mApplicationsDrawableNormal);
        else
        if(menumanager.getMode() != 2)
            menuitemview.setImageDrawable(mApplicationsDrawableMenu);
        else
            menuitemview.setImageDrawable(mApplicationsDrawableEdit);
    }

    void changeItem(View view, int i)
    {
        View view1 = getChildAt(i);
        if(view1 != null)
            if(view1.getTag() != null)
            {
                int j = removeItem(view);
                removeItem(view1);
                addItem(view1, j);
                addItem(view, i);
            } else
            {
                removeItem(view);
                addItem(view, i);
            }
    }

    public void clearApplications()
    {
        removeAllViews();
        for(int i = 0; i < mColumnCount - 1; i++)
            addView(mVirtualView[i]);

        addView(mApplicationsView);
    }

    protected void dispatchDraw(Canvas canvas)
    {
        if(mEnabledDrawing) goto _L2; else goto _L1
_L1:
        return;
_L2:
        mBgDrawable2.setBounds(0, 0, getWidth(), getHeight());
        if(mAnimationState == 1)
            mAnimationState = 2;
        if(mAnimationState != 2) goto _L4; else goto _L3
_L3:
        float f = (float)(SystemClock.uptimeMillis() - mAnimationStartTime) / (float)mAnimationDuration;
        float f3;
        if(f >= 1.0F)
        {
            mAnimationState = 3;
            float f1;
            float f2;
            int j;
            if(mIsDrawBg)
                mLauncher.getMenuManager().mMenuOpenAnimationListener.onAnimationEnd(null);
            else
                mLauncher.getMenuManager().mMenuCloseAnimationListener.onAnimationEnd(null);
        }
        f1 = Math.min(f, 1.0F);
        f2 = mInterpolator.getInterpolation(f1);
        f3 = mAnimationFrom + f2 * (mAnimationTo - mAnimationFrom);
        mBgDrawable.setAlpha((int)(255F * f3));
        if(getOrientation() == 0)
        {
            j = (int)((float)getHeight() * (1.0F - f3));
            mBgDrawable.setBounds(0, j, getWidth(), j + getHeight());
        } else
        {
            int i = (int)((float)getWidth() * (1.0F - f3));
            mBgDrawable.setBounds(i, 0, i + getWidth(), getHeight());
        }
        if(mLauncher.getMenuManager().isOpened())
            mBgDrawable.draw(canvas);
        if(mAnimationState != 3)
            invalidate();
_L5:
        if(!mLauncher.getMenuManager().isOpened())
            mBgDrawable2.draw(canvas);
        super.dispatchDraw(canvas);
        if(mEditBg != null && mEditIndex != -1)
        {
            View view = getChildAt(mEditIndex);
            if(view != null && view.getTag() != null && view.getVisibility() != 0)
                if(getOrientation() != 0)
                    canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset2, 1 + (view.getTop() + mEditTopOffset), mPaint);
                else
                    canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset, 1 + (view.getTop() - mTopOffset), mPaint);
        }
        if(true) goto _L1; else goto _L4
_L4:
        if(mIsDrawBg)
        {
            mBgDrawable.setAlpha(255);
            mBgDrawable.setBounds(0, 0, getWidth(), getHeight());
            if(mLauncher.getMenuManager().isOpened())
                mBgDrawable.draw(canvas);
        }
          goto _L5
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        if(!SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510")) goto _L2; else goto _L1
_L1:
        MenuManager menumanager = mLauncher.getMenuManager();
        if(!menumanager.isOpened()) goto _L2; else goto _L3
_L3:
        int i;
        int j;
        ViewGroup viewgroup;
        i = getFocusChild();
        j = menumanager.getCurrentScreen();
        viewgroup = (ViewGroup)menumanager.getChildAt(j);
        if(viewgroup != null) goto _L5; else goto _L4
_L4:
        boolean flag = false;
_L7:
        return flag;
_L5:
        int k = viewgroup.getChildCount();
        if(keyevent.getAction() == 0 && keyevent.getKeyCode() == 21)
        {
            if(k == 0)
            {
                viewgroup = (ViewGroup)menumanager.getChildAt(j - 1);
                if(viewgroup == null)
                {
                    flag = false;
                    continue; /* Loop/switch isn't completed */
                }
                k = viewgroup.getChildCount();
            }
            int k1;
            if(k > 4)
            {
                if(k > 4 * 2)
                {
                    if(i == 0)
                        k1 = 4;
                    else
                    if(i == 1)
                        k1 = 4 * 2;
                    else
                        k1 = k;
                } else
                if(i == 0)
                    k1 = 4;
                else
                    k1 = k;
            } else
            {
                k1 = k;
            }
            if(k1 > 0)
                viewgroup.getChildAt(k1 - 1).requestFocus();
            flag = true;
            continue; /* Loop/switch isn't completed */
        }
        if(keyevent.getAction() == 0 && keyevent.getKeyCode() == 22)
        {
            int l;
            ViewGroup viewgroup1;
            if(j + 1 >= menumanager.getChildCount())
                l = 0;
            else
                l = j + 1;
            viewgroup1 = (ViewGroup)menumanager.getChildAt(l);
            if(viewgroup1 == null)
            {
                flag = false;
            } else
            {
                int i1 = viewgroup1.getChildCount();
                menumanager.snapToScreen(j + 1);
                int j1;
                if(i1 > 4)
                {
                    if(i1 > 4 * 2)
                    {
                        if(i == 0)
                            j1 = 0;
                        else
                        if(i == 1)
                            j1 = 4;
                        else
                            j1 = 4 * 2;
                    } else
                    if(i == 0)
                        j1 = 0;
                    else
                        j1 = 4;
                } else
                {
                    j1 = 0;
                }
                if(i1 != 0)
                    viewgroup1.getChildAt(j1).requestFocus();
                flag = true;
            }
            continue; /* Loop/switch isn't completed */
        }
_L2:
        flag = super.dispatchKeyEvent(keyevent);
        if(true) goto _L7; else goto _L6
_L6:
    }

    public boolean dispatchUnhandledMove(View view, int i)
    {
        if(mLauncher == null) goto _L2; else goto _L1
_L1:
        MenuManager menumanager = mLauncher.getMenuManager();
        if(!menumanager.isOpened()) goto _L4; else goto _L3
_L3:
        int j = menumanager.getChildCount();
        if(j <= 1) goto _L2; else goto _L5
_L5:
        if(i != 17) goto _L7; else goto _L6
_L6:
        boolean flag;
        if(menumanager.getWhichScreen() == 0)
            menumanager.snapToScreen(-1);
        flag = true;
_L9:
        return flag;
_L7:
        if(i == 66)
        {
            if(menumanager.getWhichScreen() == j - 1)
            {
                menumanager.snapToScreen(j);
                if(menumanager.getFocusedChild() == null)
                    menumanager.getChildAt(0).requestFocus();
            }
            flag = true;
            continue; /* Loop/switch isn't completed */
        }
          goto _L2
_L4:
        if(!mLauncher.isWorkspaceLocked())
        {
            Workspace workspace = mLauncher.getWorkspace();
            if(i == 17)
            {
                if(workspace.getCurrentScreen() > 0)
                {
                    if(((CellLayout)workspace.getChildAt(workspace.getCurrentScreen())).getChildCount() <= 0)
                        Workspace.isScrollAble = true;
                    workspace.snapToScreen(workspace.getCurrentScreen() - 1);
                    flag = true;
                    continue; /* Loop/switch isn't completed */
                }
            } else
            if(i == 66 && workspace.getCurrentScreen() < workspace.getChildCount() - 1)
            {
                workspace.snapToScreen(1 + workspace.getCurrentScreen());
                flag = true;
                continue; /* Loop/switch isn't completed */
            }
        }
_L2:
        flag = super.dispatchUnhandledMove(view, i);
        if(true) goto _L9; else goto _L8
_L8:
    }

    public void drawBg(boolean flag, boolean flag1)
    {
        mIsDrawBg = flag;
        if(flag1)
        {
            mAnimationDuration = 500;
            mAnimationStartTime = SystemClock.uptimeMillis();
            if(mIsDrawBg)
            {
                mAnimationState = 1;
                mAnimationFrom = 0.0F;
                mAnimationTo = 1.0F;
                mInterpolator = mOpenInterpolator;
            } else
            {
                mAnimationState = 1;
                mAnimationFrom = 1.0F;
                mAnimationTo = 0.0F;
                mInterpolator = mCloseInterpolator;
            }
        } else
        {
            mAnimationState = 3;
        }
        invalidate();
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        boolean flag;
        if(mDeleteIcon != null)
        {
            if(mEditBg != null && view.getTag() != null)
                if(getOrientation() != 0)
                    canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset2, 1 + (view.getTop() + mEditTopOffset), mPaint);
                else
                    canvas.drawBitmap(mEditBg, view.getLeft() + mEditLeftOffset, 1 + (view.getTop() - mTopOffset), mPaint);
            flag = super.drawChild(canvas, view, l);
        } else
        {
            flag = super.drawChild(canvas, view, l);
        }
        return flag;
    }

    public void drawChildUninstallableMarker(Canvas canvas, View view)
    {
        if(mDeleteIcon != null)
        {
            Object obj = view.getTag();
            if(obj != null && (obj instanceof ApplicationInfo) && !((ApplicationInfo)obj).systemApp)
            {
                int i = 0;
                if(getOrientation() != 0)
                    i = mTopOffset;
                canvas.drawBitmap(mDeleteIcon, view.getWidth() - mDeleteIcon.getWidth() - ((view.getWidth() - mIconWidth) / 2 + mDeleteIconRightOffset), (0 + mDeleteIconTopOffset) - i, mPaint);
            }
        }
    }

    public int getColumnCount()
    {
        return mColumnCount;
    }

    public int getEnabledChildCount()
    {
        int i = getChildCount();
        int j = 0;
        for(int k = 0; k < i; k++)
            if(getChildAt(k).getVisibility() != 8)
                j++;

        return j;
    }

    int getIndexOfCell(int i, int j)
    {
        Rect rect;
        rect = new Rect();
        getHitRect(rect);
        if(!rect.contains(i, j)) goto _L2; else goto _L1
_L1:
        int l;
        int i1;
        int j1;
        int k1;
        int l1;
        int i2;
        int j2;
        l = i - rect.left;
        i1 = j - rect.top;
        j1 = -1;
        k1 = getEnabledChildCount();
        l1 = getChildCount();
        i2 = mChildWidth;
        j2 = mChildHeight;
        if(k1 < mColumnCount) goto _L4; else goto _L3
_L3:
        int k;
        if(getOrientation() == 0)
        {
            if(l < 0)
                j1 = 0;
            else
                j1 = l / i2;
        } else
        {
            int j4 = i1 / (j2 + mTopOffset);
            j1 = l1 - 1 - j4;
        }
_L8:
        if(j1 < 0 || j1 >= l1 - 1)
            j1 = -1;
        k = j1;
_L16:
        return k;
_L4:
        if(getOrientation() != 0) goto _L6; else goto _L5
_L5:
        int k3;
        int l3;
        int i4;
        k3 = getItemGap(k1);
        l3 = (getMeasuredWidth() - k1 * i2 - k3 * (k1 - 1)) / 2;
        i4 = 0;
_L11:
        if(i4 >= l1) goto _L8; else goto _L7
_L7:
        if(getChildAt(i4).getVisibility() != 8) goto _L10; else goto _L9
_L9:
        i4++;
          goto _L11
_L10:
        j1++;
        if(l >= l3 + j1 * (i2 + k3) + i2 / 2) goto _L9; else goto _L8
_L6:
        int k2;
        int l2;
        int i3;
        int j3;
        k2 = getItemGap(k1);
        l2 = (getMeasuredHeight() - k1 * j2 - (k1 - 1) * (k2 + mTopOffset)) / 2;
        j1 = l1 - 1;
        i3 = 0;
        j3 = 0;
_L15:
        if(j3 >= l1) goto _L8; else goto _L12
_L12:
        if(getChildAt(j3).getVisibility() != 8) goto _L14; else goto _L13
_L13:
        j3++;
          goto _L15
_L14:
        j1--;
        i3++;
        if(i1 >= l2 + i3 * (j2 + k2) + j2 / 2) goto _L13; else goto _L8
_L2:
        k = -1;
          goto _L16
    }

    public int getVisibleChildCount()
    {
        int i = getChildCount();
        int j = 0;
        for(int k = 0; k < i; k++)
            if(getChildAt(k).getVisibility() == 0)
                j++;

        return j;
    }

    public void loadApplications()
    {
        Cursor cursor = LauncherModel.loadTopAppToDatabase(getContext());
        if(cursor != null)
        {
            int i = Math.min(cursor.getCount(), mColumnCount - 1);
            cursor.moveToFirst();
            for(int j = 0; j < i; j++)
            {
                String s = cursor.getString(1);
                ApplicationInfo applicationinfo = getApplicationInfo(getContext(), ComponentName.unflattenFromString(s));
                if(applicationinfo != null)
                {
                    int k = cursor.getInt(2);
                    applicationinfo.topNum = k;
                    applicationinfo.editTopNum = k;
                    View view = makeItemView(applicationinfo);
                    int l = j;
                    if(k >= 0 && k < 3 && k < mColumnCount - 1)
                        l = k;
                    removeViewAt(l);
                    addView(view, l);
                }
                cursor.moveToNext();
            }

            cursor.close();
        }
    }

    public View makeItemView(ApplicationInfo applicationinfo)
    {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        if(configuration.orientation != mOrientation)
        {
            mOldOrientation = configuration.orientation;
            configuration.orientation = mOrientation;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        View view;
        MenuItemView menuitemview;
        if(Launcher.USE_MAINMENU_ICONMODE)
            view = mInflater.inflate(0x7f030005, this, false);
        else
            view = mInflater.inflate(0x7f030004, this, false);
        menuitemview = (MenuItemView)view;
        menuitemview.setImageDrawable(new FastBitmapDrawable(applicationinfo.iconBitmap));
        if(!Launcher.USE_MAINMENU_ICONMODE)
            menuitemview.setFocusable(true);
        menuitemview.setOnClickListener(mOnClickListener);
        menuitemview.setOnLongClickListener(mOnLongClickListener);
        menuitemview.setTag(applicationinfo);
        if(mOldOrientation != -1)
        {
            android.util.DisplayMetrics displaymetrics = resources.getDisplayMetrics();
            configuration.orientation = mOldOrientation;
            resources.updateConfiguration(configuration, displaymetrics);
            mOldOrientation = -1;
        }
        return menuitemview;
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
        mLauncher.getMenuManager().onDropCompleted(view, flag, obj);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        if(mLauncher.isAddWidgetState())
            flag = true;
        else
            flag = false;
        return flag;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = getOrientation();
        int j1 = getChildCount();
        int k1 = mLRPadding;
        int l1 = mTopOffset;
        int i2 = 0;
        int j2 = 0;
        if(i1 != 0)
        {
            k1 = 0;
            l1 = mTopOffset;
            i2 = mTopOffset;
        }
        int k2 = getEnabledChildCount();
        Rect rect = mTmpRect;
        if(i1 == 0)
        {
            if(k2 > 0 && k2 < mColumnCount)
            {
                j2 = getItemGap(k2);
                k1 = (getMeasuredWidth() - k2 * mChildWidth - j2 * (k2 - 1)) / 2;
            }
            int k3 = 0;
            while(k3 < j1) 
            {
                View view1 = getChildAt(k3);
                if(view1.getVisibility() != 8)
                {
                    int l3 = view1.getMeasuredWidth();
                    int i4 = view1.getMeasuredHeight();
                    view1.getHitRect(rect);
                    if(view1.getVisibility() == 0 && !rect.isEmpty() && mEnabledChildAnimation)
                    {
                        rect.set(k1, l1, k1 + l3, l1 + i4);
                        mChildAnimate[k3].start(view1, rect);
                    } else
                    {
                        view1.layout(k1, l1, k1 + l3, l1 + i4);
                    }
                    k1 += l3 + j2;
                }
                k3++;
            }
        } else
        {
            if(k2 > 0 && k2 < mColumnCount)
            {
                j2 = getItemGap(k2);
                l1 = (getMeasuredHeight() - k2 * mChildHeight - (k2 - 1) * (j2 + mTopOffset)) / 2;
            }
            int l2 = j1 - 1;
            while(l2 >= 0) 
            {
                View view = getChildAt(l2);
                if(view.getVisibility() != 8)
                {
                    int i3 = view.getMeasuredWidth();
                    int j3 = view.getMeasuredHeight();
                    view.getHitRect(rect);
                    if(view.getVisibility() == 0 && !rect.isEmpty() && mEnabledChildAnimation)
                    {
                        rect.set(k1, l1, k1 + i3, l1 + j3);
                        mChildAnimate[l2].start(view, rect);
                    } else
                    {
                        view.layout(k1, l1, k1 + i3, l1 + j3);
                    }
                    l1 += j2 + (j3 + i2);
                }
                l2--;
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        android.view.View.MeasureSpec.getSize(j);
        int l = getChildCount();
        if(getOrientation() == 0)
            mChildWidth = (k - 2 * mLRPadding) / mColumnCount;
        int i1 = android.view.View.MeasureSpec.makeMeasureSpec(mChildWidth, 0x40000000);
        int j1 = android.view.View.MeasureSpec.makeMeasureSpec(mChildHeight, 0x40000000);
        for(int k1 = 0; k1 < l; k1++)
            getChildAt(k1).measure(i1, j1);

    }

    int removeItem(View view)
    {
        int i = getChildCount();
        int j = -1;
        int k = 0;
        do
        {
label0:
            {
                if(k < i)
                {
                    if(getChildAt(k) != view)
                        break label0;
                    j = k;
                }
                if(j != -1)
                {
                    removeViewAt(j);
                    addView(mVirtualView[j], j);
                }
                return j;
            }
            k++;
        } while(true);
    }

    public void setBitmap(Bitmap bitmap)
    {
        mDeleteIcon = bitmap;
    }

    public void setDragger(DragController dragcontroller)
    {
        mDragger = dragcontroller;
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

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    public void updateApplication(ApplicationInfo applicationinfo, int i)
    {
        if(applicationinfo != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        View view = getChildAt(i);
        if(view != null && i < 3 && i < mColumnCount - 1)
        {
            Object obj = view.getTag();
            boolean flag = true;
            if(obj != null && (ApplicationInfo)obj == applicationinfo)
                flag = false;
            if(flag)
            {
                BadgeCache badgecache = ((LauncherApplication)mLauncher.getApplicationContext()).getBadgeCache();
                if(badgecache != null)
                    applicationinfo.badgeCount = badgecache.getBadgeCount(applicationinfo.componentName);
                addItem(makeItemView(applicationinfo), i);
            }
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    public void updateApplications()
    {
        clearApplications();
        loadApplications();
    }

    void updateBadgeCount(List list)
    {
        BadgeCache badgecache = ((LauncherApplication)mLauncher.getApplicationContext()).getBadgeCache();
        if(badgecache != null)
        {
            for(int i = 0; i < getChildCount(); i++)
            {
                View view = getChildAt(i);
                ApplicationInfo applicationinfo = (ApplicationInfo)view.getTag();
                if(applicationinfo != null)
                {
                    applicationinfo.badgeCount = badgecache.getBadgeCount(applicationinfo.intent);
                    view.destroyDrawingCache();
                    view.buildDrawingCache();
                    view.invalidate();
                }
            }

        }
        invalidate();
    }

    private int mAnimationDuration;
    private float mAnimationFrom;
    private long mAnimationStartTime;
    private int mAnimationState;
    private float mAnimationTo;
    private Drawable mApplicationsDrawableEdit;
    private Drawable mApplicationsDrawableMenu;
    private Drawable mApplicationsDrawableNormal;
    private View mApplicationsView;
    private Drawable mBgDrawable;
    private Drawable mBgDrawable2;
    private Animate mChildAnimate[];
    private int mChildHeight;
    private int mChildWidth;
    private Interpolator mCloseInterpolator;
    private final int mColumnCount;
    private Bitmap mDeleteIcon;
    private int mDeleteIconRightOffset;
    private int mDeleteIconTopOffset;
    private DragController mDragger;
    private Bitmap mEditBg;
    private int mEditIndex;
    private int mEditLeftOffset;
    private int mEditLeftOffset2;
    private int mEditTopOffset;
    private boolean mEnabledChildAnimation;
    private boolean mEnabledDrawing;
    private IconCache mIconCache;
    private int mIconWidth;
    private LayoutInflater mInflater;
    private Interpolator mInterpolator;
    private boolean mIsDrawBg;
    private int mItemGap1;
    private int mItemGap2;
    private int mLRPadding;
    private Launcher mLauncher;
    private int mOldOrientation;
    private android.view.View.OnClickListener mOnClickListener;
    private android.view.View.OnLongClickListener mOnLongClickListener;
    private Interpolator mOpenInterpolator;
    private int mOrientation;
    private Paint mPaint;
    private Rect mTmpRect;
    private int mTopOffset;
    private View mVirtualView[];


}
