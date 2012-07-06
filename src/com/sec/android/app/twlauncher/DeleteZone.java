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
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.*;
import android.widget.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DropTarget, Launcher, DragLayer, TopFourZone, 
//            ItemInfo, SamsungAppWidgetInfo, UserFolderInfo, LauncherModel, 
//            LauncherAppWidgetInfo, ShortcutInfo, FolderInfo, UserFolder, 
//            LauncherAppWidgetHost, SamsungWidgetPackageManager, DragSource

public class DeleteZone extends LinearLayout
    implements DragController.DragListener, DropTarget
{
    private static class FastAnimationSet extends AnimationSet
    {

        public boolean willChangeBounds()
        {
            return false;
        }

        public boolean willChangeTransformationMatrix()
        {
            return true;
        }

        FastAnimationSet()
        {
            super(false);
        }
    }

    private static class FastTranslateAnimation extends TranslateAnimation
    {

        public boolean willChangeBounds()
        {
            return false;
        }

        public boolean willChangeTransformationMatrix()
        {
            return true;
        }

        public FastTranslateAnimation(int i, float f, int j, float f1, int k, float f2, int l, 
                float f3)
        {
            super(i, f, j, f1, k, f2, l, f3);
        }
    }


    public DeleteZone(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mIconHoverColorFilter = new PorterDuffColorFilter(0x9fff0000, android.graphics.PorterDuff.Mode.SRC_ATOP);
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.DeleteZone);
        mApplyIconHoverColorFilter = typedarray.getBoolean(1, false);
        mDrawDeleteZoneBg = typedarray.getBoolean(2, false);
        mInOutAnimationDuration = typedarray.getInt(4, 200);
        mInOutAnimationTranslationRatio = typedarray.getFloat(3, 1.0F);
        pm = (PowerManager)context.getSystemService("power");
        typedarray.recycle();
        init();
    }

    private void createAnimations()
    {
        if(mInAnimation == null)
        {
            mInAnimation = new FastAnimationSet();
            AnimationSet animationset1 = mInAnimation;
            animationset1.setInterpolator(new AccelerateInterpolator());
            animationset1.addAnimation(new AlphaAnimation(0.0F, 1.0F));
            if(getOrientation() == 0)
                animationset1.addAnimation(new TranslateAnimation(0, 0.0F, 0, 0.0F, 1, mInOutAnimationTranslationRatio, 1, 0.0F));
            else
                animationset1.addAnimation(new TranslateAnimation(1, mInOutAnimationTranslationRatio, 1, 0.0F, 0, 0.0F, 0, 0.0F));
            animationset1.setStartOffset(mInOutAnimationDuration);
            animationset1.setDuration(mInOutAnimationDuration);
        }
        if(mHandleInAnimation == null)
        {
            FastAnimationSet fastanimationset = new FastAnimationSet();
            fastanimationset.addAnimation(new AlphaAnimation(0.0F, 1.0F));
            if(getOrientation() == 0)
                fastanimationset.addAnimation(new TranslateAnimation(0, 0.0F, 0, 0.0F, 1, mInOutAnimationTranslationRatio, 1, 0.0F));
            else
                fastanimationset.addAnimation(new TranslateAnimation(1, mInOutAnimationTranslationRatio, 1, 0.0F, 0, 0.0F, 0, 0.0F));
            mHandleInAnimation = fastanimationset;
            mHandleInAnimation.setStartOffset(mInOutAnimationDuration);
            mHandleInAnimation.setDuration(mInOutAnimationDuration);
        }
        if(mOutAnimation == null)
        {
            mOutAnimation = new FastAnimationSet();
            AnimationSet animationset = mOutAnimation;
            animationset.setInterpolator(new AccelerateInterpolator());
            animationset.addAnimation(new AlphaAnimation(1.0F, 0.0F));
            if(getOrientation() == 0)
                animationset.addAnimation(new FastTranslateAnimation(0, 0.0F, 0, 0.0F, 1, 0.0F, 1, mInOutAnimationTranslationRatio));
            else
                animationset.addAnimation(new FastTranslateAnimation(1, 0.0F, 1, mInOutAnimationTranslationRatio, 0, 0.0F, 0, 0.0F));
            animationset.setDuration(mInOutAnimationDuration);
        }
        if(mHandleOutAnimation == null)
        {
            FastAnimationSet fastanimationset1 = new FastAnimationSet();
            fastanimationset1.addAnimation(new AlphaAnimation(1.0F, 0.0F));
            if(getOrientation() == 0)
                fastanimationset1.addAnimation(new FastTranslateAnimation(0, 0.0F, 0, 0.0F, 1, 0.0F, 1, mInOutAnimationTranslationRatio));
            else
                fastanimationset1.addAnimation(new FastTranslateAnimation(1, 0.0F, 1, mInOutAnimationTranslationRatio, 0, 0.0F, 0, 0.0F));
            mHandleOutAnimation = fastanimationset1;
            mHandleOutAnimation.setDuration(mInOutAnimationDuration);
        }
    }

    private void init()
    {
        Resources resources = getResources();
        if(resources.getConfiguration().orientation == 2)
        {
            mRemoveBg = resources.getDrawable(0x7f020066);
            mRemoveBgDrag = resources.getDrawable(0x7f020068);
        } else
        {
            mRemoveBg = resources.getDrawable(0x7f020065);
            mRemoveBgDrag = resources.getDrawable(0x7f020067);
        }
    }

    private void setIconViewColorFilter(TextView textview, ColorFilter colorfilter)
    {
        Drawable adrawable[] = textview.getCompoundDrawables();
        adrawable[1].setColorFilter(colorfilter);
        textview.setCompoundDrawables(adrawable[0], adrawable[1], adrawable[2], adrawable[3]);
        textview.invalidate();
    }

    private void setMode(ItemInfo iteminfo)
    {
        mDragEnterForced = false;
        mRemoveView.setVisibility(0);
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        return true;
    }

    protected void dispatchDraw(Canvas canvas)
    {
        if(mDrawDeleteZoneBg)
        {
            Drawable drawable;
            if(mDragEnter || mDragEnterForced)
                drawable = mRemoveBgDrag;
            else
                drawable = mRemoveBg;
            if(drawable == mRemoveBgDrag && !pm.isScreenOn())
            {
                mDragEnter = false;
                mDragEnterForced = false;
            }
            if(Launcher.USE_MAINMENU_ICONMODE || getResources().getBoolean(0x7f080003))
                drawable.setBounds(0, 0, getWidth(), drawable.getIntrinsicHeight());
            else
                drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    public void getDeleteAreaRect(Rect rect)
    {
        if(rect != null)
            mRemoveView.getHitRect(rect);
    }

    public int getDragAnimationXOffset(View view)
    {
        return 0;
    }

    public void onDragEnd()
    {
        if(mTrashMode)
        {
            mTrashMode = false;
            mDragLayer.setDeleteRegion(null);
            startAnimation(mOutAnimation);
            mHandle.startAnimation(mHandleInAnimation);
            setVisibility(8);
            mHandle.setVisibility(0);
            if(Launcher.USE_MAINMENU_ICONMODE)
                mLauncher.getTopFourZone().setVisibility(0);
        }
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mDragEnter = true;
        TextView textview = mRemoveIcon;
        if(mApplyIconHoverColorFilter)
            setIconViewColorFilter(textview, mIconHoverColorFilter);
        invalidate();
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mDragEnter = false;
        mDragLayer.setTrashPaint(0);
        TextView textview = mRemoveIcon;
        if(mApplyIconHoverColorFilter)
            setIconViewColorFilter(textview, null);
        invalidate();
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragStart(View view, DragSource dragsource, Object obj, int i)
    {
        ItemInfo iteminfo = (ItemInfo)obj;
        if(iteminfo != null)
        {
            setMode(iteminfo);
            mTrashMode = true;
            createAnimations();
            int ai[] = mLocation;
            getLocationOnScreen(ai);
            mRegion.set(ai[0], ai[1], (ai[0] + mRight) - mLeft, (ai[1] + mBottom) - mTop);
            mDragLayer.setDeleteRegion(mRegion);
            startAnimation(mInAnimation);
            mHandle.startAnimation(mHandleOutAnimation);
            setVisibility(0);
            mHandle.setVisibility(8);
        }
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        ItemInfo iteminfo = (ItemInfo)obj;
        if(iteminfo.container != -1L) goto _L2; else goto _L1
_L1:
        return;
_L2:
        UserFolderInfo userfolderinfo;
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
        if(dragsource instanceof UserFolder)
            ((UserFolderInfo)((UserFolder)dragsource).getInfo()).remove((ShortcutInfo)iteminfo);
        if(!(iteminfo instanceof UserFolderInfo))
            break; /* Loop/switch isn't completed */
        userfolderinfo = (UserFolderInfo)iteminfo;
        LauncherModel.deleteUserFolderContentsFromDatabase(mLauncher, userfolderinfo);
        mLauncher.removeFolder(userfolderinfo);
_L4:
        LauncherModel.deleteItemFromDatabase(mLauncher, iteminfo);
        if(Launcher.USE_MAINMENU_ICONMODE)
            mLauncher.getTopFourZone().setVisibility(0);
        if(true) goto _L1; else goto _L3
_L3:
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
          goto _L4
        if(true) goto _L1; else goto _L5
_L5:
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mRemoveView = (FrameLayout)getChildAt(0);
        mRemoveIcon = (TextView)mRemoveView.getChildAt(0);
    }

    void resetMode()
    {
        setMode(null);
    }

    void setDragController(DragLayer draglayer)
    {
        mDragLayer = draglayer;
    }

    void setDragEnterForced(boolean flag)
    {
        mDragEnterForced = flag;
        if(mApplyIconHoverColorFilter)
        {
            TextView textview = mRemoveIcon;
            ColorFilter colorfilter;
            if(mDragEnterForced)
                colorfilter = mIconHoverColorFilter;
            else
                colorfilter = null;
            setIconViewColorFilter(textview, colorfilter);
        }
    }

    void setHandle(View view)
    {
        mHandle = view;
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    private final boolean mApplyIconHoverColorFilter;
    private boolean mDragEnter;
    private boolean mDragEnterForced;
    private DragLayer mDragLayer;
    private final boolean mDrawDeleteZoneBg;
    private View mHandle;
    private Animation mHandleInAnimation;
    private Animation mHandleOutAnimation;
    private ColorFilter mIconHoverColorFilter;
    private AnimationSet mInAnimation;
    private final int mInOutAnimationDuration;
    private final float mInOutAnimationTranslationRatio;
    private Launcher mLauncher;
    private final int mLocation[] = new int[2];
    private AnimationSet mOutAnimation;
    private final RectF mRegion = new RectF();
    private Drawable mRemoveBg;
    private Drawable mRemoveBgDrag;
    private TextView mRemoveIcon;
    private FrameLayout mRemoveView;
    public boolean mTrashMode;
    private PowerManager pm;
}
