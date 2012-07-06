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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragSource, DropTarget, ShortcutInfo, LauncherApplication, 
//            BadgeCache, Launcher, DragLayer, ItemInfo, 
//            QuickViewWorkspace, QuickViewMainMenu, MenuManager, Workspace, 
//            MenuItemView, FastBitmapDrawable, ApplicationInfo, LauncherModel, 
//            IconCache

public class TopFourZone extends LinearLayout
    implements DragSource, DropTarget
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
        final TopFourZone this$0;
    }


    public TopFourZone(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        mEnabledDrawing = true;
        mVirtualView = new View[4];
        mOrientation = 1;
        mOldOrientation = -1;
        mChildAnimate = new Animate[4];
        mTmpRect = new Rect();
        mOnClickListener = new android.view.View.OnClickListener() {

            public void onClick(View view)
            {
                Object obj = view.getTag();
                if(obj != null && (obj instanceof ShortcutInfo))
                {
                    ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
                    mLauncher.startActivitySafely(shortcutinfo.intent, null);
                }
            }

            final TopFourZone this$0;

            
            {
                this$0 = TopFourZone.this;
                super();
            }
        }
;
        mOnLongClickListener = new android.view.View.OnLongClickListener() {

            public boolean onLongClick(View view)
            {
                boolean flag;
                if(Launcher.USE_MAINMENU_ICONMODE)
                {
                    flag = true;
                } else
                {
                    Object obj = view.getTag();
                    if(obj != null && (obj instanceof ShortcutInfo))
                        startDragItem(view, (ShortcutInfo)obj);
                    flag = true;
                }
                return flag;
            }

            final TopFourZone this$0;

            
            {
                this$0 = TopFourZone.this;
                super();
            }
        }
;
        init();
    }

    private Bitmap findIconMenuApplicationIcon(String s)
    {
        Bitmap bitmap;
        Resources resources;
        Drawable drawable;
        bitmap = null;
        resources = mContext.getResources();
        drawable = null;
        if(!s.equals("com.android.browser")) goto _L2; else goto _L1
_L1:
        drawable = resources.getDrawable(0x7f0200d6);
_L4:
        if(drawable != null)
        {
            int i = drawable.getIntrinsicWidth();
            int j = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, i, j);
            bitmap = Bitmap.createBitmap(i, j, android.graphics.Bitmap.Config.ARGB_8888);
            drawable.draw(new Canvas(bitmap));
        }
        return bitmap;
_L2:
        if(s.equals("com.android.calculator2"))
            drawable = resources.getDrawable(0x7f0200a7);
        else
        if(s.equals("com.android.calendar"))
            drawable = resources.getDrawable(0x7f0200aa);
        else
        if(s.equals("com.sec.android.app.camera"))
            drawable = resources.getDrawable(0x7f0200ab);
        else
        if(s.equals("com.android.deskclock"))
            drawable = resources.getDrawable(0x7f0200af);
        else
        if(s.equals("com.android.email"))
            drawable = resources.getDrawable(0x7f0200be);
        else
        if(s.equals("com.samsung.app.fmradio"))
            drawable = resources.getDrawable(0x7f0200c2);
        else
        if(s.equals("com.cooliris.media"))
            drawable = resources.getDrawable(0x7f0200c4);
        else
        if(s.equals("com.google.android.gm"))
            drawable = resources.getDrawable(0x7f0200c7);
        else
        if(s.equals("com.android.vending"))
            drawable = resources.getDrawable(0x7f0200df);
        else
        if(s.equals("com.sec.android.app.memo"))
            drawable = resources.getDrawable(0x7f0200e2);
        else
        if(s.equals("com.android.mms"))
            drawable = resources.getDrawable(0x7f0200e4);
        else
        if(s.equals("com.android.music"))
            drawable = resources.getDrawable(0x7f0200ec);
        else
        if(s.equals("com.sec.android.app.myfiles"))
            drawable = resources.getDrawable(0x7f0200ee);
        else
        if(s.equals("com.sec.android.app.samsungapps"))
            drawable = resources.getDrawable(0x7f0200fd);
        else
        if(s.equals("com.android.settings"))
            drawable = resources.getDrawable(0x7f020104);
        else
        if(s.equals("com.sec.android.app.dialertab"))
            drawable = resources.getDrawable(0x7f0200b6);
        else
        if(s.equals("com.android.contacts"))
            drawable = resources.getDrawable(0x7f0200b1);
        if(true) goto _L4; else goto _L3
_L3:
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
        Resources resources;
        mInflater = LayoutInflater.from(getContext());
        mIconCache = ((LauncherApplication)getContext().getApplicationContext()).getIconCache();
        resources = getResources();
        mOrientation = resources.getConfiguration().orientation;
        int i;
        if(Launcher.USE_MAINMENU_ICONMODE)
            mChildHeight = resources.getDimensionPixelSize(0x7f090030);
        else
            mChildHeight = resources.getDimensionPixelSize(0x7f09000c);
        mChildWidth = resources.getDimensionPixelSize(0x7f09000d);
        setClickable(true);
        i = 0;
        while(i < 4) 
        {
            mVirtualView[i] = new View(getContext());
            if(i == 2)
                mVirtualView[i].setTag(APPLICATION_ICON_SLOT_TAG);
            else
                mVirtualView[i].setTag(EMPTY_SLOT_VIEW_TAG);
            i++;
        }
        if(mOrientation != 1) goto _L2; else goto _L1
_L1:
        setOrientation(0);
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            mTopFourBgDrawable = null;
        } else
        {
            mTopFourBgDrawable = resources.getDrawable(0x7f020018);
            mTopOffset = 7;
        }
_L4:
        initVirtualView();
        return;
_L2:
        if(mOrientation == 2)
        {
            setOrientation(1);
            if(Launcher.USE_MAINMENU_ICONMODE)
            {
                mTopFourBgDrawable = null;
            } else
            {
                mTopFourBgDrawable = resources.getDrawable(0x7f020019);
                mTopOffset = 0;
            }
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void initVirtualView()
    {
        for(int i = 0; i < 4; i++)
            addView(mVirtualView[i]);

    }

    private void startDragItem(View view, ShortcutInfo shortcutinfo)
    {
        mDraggingView = view;
        mLauncher.getDragLayer().startDrag(view, this, shortcutinfo, 0);
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        Log.d("Launcher.TopFourZone", "acceptDrop()");
        if(!Launcher.USE_MAINMENU_ICONMODE) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L8:
        return flag;
_L2:
        int i1;
        int j1;
        ItemInfo iteminfo = (ItemInfo)obj;
        if(iteminfo.itemType != 0 && iteminfo.itemType != 1)
        {
            Log.d("Launcher.TopFourZone", "not acceptable type");
            flag = false;
            continue; /* Loop/switch isn't completed */
        }
        i1 = 0;
        j1 = getChildCount();
_L4:
        View view;
        if(i1 >= j1)
            break MISSING_BLOCK_LABEL_201;
        view = getChildAt(i1);
        view.getHitRect(mTmpRect);
        if(mTmpRect.contains(i, j))
            break; /* Loop/switch isn't completed */
_L6:
        i1++;
        if(true) goto _L4; else goto _L3
_L3:
        if(view.getTag() != EMPTY_SLOT_VIEW_TAG)
            continue; /* Loop/switch isn't completed */
        Log.d("Launcher.TopFourZone", (new StringBuilder()).append("empty slot. acceptable !! index:").append(i1).toString());
        flag = true;
        continue; /* Loop/switch isn't completed */
        if(view != mDraggingView) goto _L6; else goto _L5
_L5:
        Log.d("Launcher.TopFourZone", (new StringBuilder()).append("original slot. acceptable !! index:").append(i1).toString());
        flag = true;
        continue; /* Loop/switch isn't completed */
        Log.d("Launcher.TopFourZone", "not acceptable");
        flag = false;
        if(true) goto _L8; else goto _L7
_L7:
    }

    View addItem(View view, int i)
    {
        View view1 = getChildAt(i);
        removeViewAt(i);
        addView(view, i);
        view.setVisibility(0);
        return view1;
    }

    protected void dispatchDraw(Canvas canvas)
    {
        if(mEnabledDrawing && !mLauncher.getQuickViewWorkspace().isOpened() && !mLauncher.getQuickViewMainMenu().isOpened() && mLauncher.getStateQuickNavigation() < 0)
        {
            if(mTopFourBgDrawable != null)
            {
                int i;
                int j;
                if(mOrientation == 1)
                {
                    i = (getWidth() - mTopFourBgDrawable.getIntrinsicWidth()) / 2;
                    j = mTopOffset;
                } else
                {
                    i = 4 + mTopOffset;
                    j = (getHeight() - mTopFourBgDrawable.getIntrinsicHeight()) / 2;
                }
                mTopFourBgDrawable.setBounds(i, j, i + mTopFourBgDrawable.getIntrinsicWidth(), j + mTopFourBgDrawable.getIntrinsicHeight());
                mTopFourBgDrawable.draw(canvas);
            }
            super.dispatchDraw(canvas);
        }
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
                menumanager.snapToScreen(j);
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

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        return super.drawChild(canvas, view, l);
    }

    public void loadApplication(ShortcutInfo shortcutinfo)
    {
        Log.i("Launcher.TopFourZone", "loadApplication()");
        View view = makeItemView(shortcutinfo);
        int i = ((ItemInfo) (shortcutinfo)).cellX;
        Log.d("Launcher.TopFourZone", (new StringBuilder()).append("shortcutInfo.cellX : ").append(i).toString());
        if(i >= 0 && i < 4 && i != 2)
        {
            removeViewAt(i);
            addView(view, i);
        }
    }

    public View makeItemView(ShortcutInfo shortcutinfo)
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
        IconCache iconcache;
        MenuItemView menuitemview;
        if(Launcher.USE_MAINMENU_ICONMODE)
            view = mInflater.inflate(0x7f030005, this, false);
        else
            view = mInflater.inflate(0x7f030004, this, false);
        iconcache = ((LauncherApplication)getContext().getApplicationContext()).getIconCache();
        menuitemview = (MenuItemView)view;
        if(Launcher.USE_MAINMENU_ICONMODE)
        {
            if(((ItemInfo) (shortcutinfo)).screen == -1)
                menuitemview.setImageDrawable(new FastBitmapDrawable(findIconMenuApplicationIcon(shortcutinfo.intent.getComponent().getPackageName())));
            else
                menuitemview.setImageDrawable(new FastBitmapDrawable(shortcutinfo.getIcon(iconcache)));
        } else
        {
            menuitemview.setImageDrawable(new FastBitmapDrawable(shortcutinfo.getIcon(iconcache)));
            menuitemview.setText(shortcutinfo.title);
        }
        menuitemview.setFocusable(true);
        menuitemview.setOnClickListener(mOnClickListener);
        menuitemview.setOnLongClickListener(mOnLongClickListener);
        menuitemview.setTag(shortcutinfo);
        if(mOldOrientation != -1)
        {
            android.util.DisplayMetrics displaymetrics = resources.getDisplayMetrics();
            configuration.orientation = mOldOrientation;
            resources.updateConfiguration(configuration, displaymetrics);
            mOldOrientation = -1;
        }
        return menuitemview;
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        int i1;
        int j1;
        Log.d("Launcher.TopFourZone", "onDrop()");
        i1 = 0;
        j1 = getChildCount();
_L2:
        View view;
        if(i1 >= j1)
            break MISSING_BLOCK_LABEL_319;
        view = getChildAt(i1);
        view.getHitRect(mTmpRect);
        if(mTmpRect.contains(i, j))
            break; /* Loop/switch isn't completed */
_L4:
        i1++;
        if(true) goto _L2; else goto _L1
_L1:
        if(view.getTag() != EMPTY_SLOT_VIEW_TAG)
            continue; /* Loop/switch isn't completed */
        Log.d("Launcher.TopFourZone", (new StringBuilder()).append("drop on empty slot. drop index:").append(i1).toString());
        Object obj1 = (ItemInfo)obj;
        switch(((ItemInfo) (obj1)).itemType)
        {
        default:
            throw new IllegalStateException((new StringBuilder()).append("Unknown item type: ").append(((ItemInfo) (obj1)).itemType).toString());

        case 0: // '\0'
        case 1: // '\001'
            break;
        }
        if(((ItemInfo) (obj1)).container == -1L && (obj1 instanceof ApplicationInfo))
            obj1 = new ShortcutInfo((ApplicationInfo)obj1);
        addItem(makeItemView((ShortcutInfo)obj1), i1);
        if(((ItemInfo) (obj1)).container == -1L)
            mLauncher.addShortcut((ShortcutInfo)obj1);
        LauncherModel.addOrMoveItemInDatabase(getContext(), ((ItemInfo) (obj1)), -100L, -1, i1, 0);
_L5:
        return;
        if(view != mDraggingView) goto _L4; else goto _L3
_L3:
        Log.d("Launcher.TopFourZone", (new StringBuilder()).append("drop on original slot. drop index:").append(i1).toString());
        addItem(makeItemView((ShortcutInfo)(ItemInfo)obj), i1);
          goto _L5
        Log.d("Launcher.TopFourZone", "onDrop() at wrong index !!");
          goto _L5
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
        ItemInfo iteminfo;
        if(flag)
        {
            removeItem(mDraggingView);
            iteminfo = (ItemInfo)obj;
            break MISSING_BLOCK_LABEL_19;
        }
_L1:
        mDraggingView = null;
        return;
        if(view != this && iteminfo.container != -100L)
            mLauncher.removeShortcut((ShortcutInfo)iteminfo);
          goto _L1
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
        int k1 = 0;
        int l1 = mTopOffset;
        int i2 = 0;
        if(i1 != 0)
        {
            k1 = 0;
            l1 = mTopOffset;
            i2 = mTopOffset;
        }
        Rect rect = mTmpRect;
        if(i1 == 0)
        {
            int i3 = 0;
            while(i3 < j1) 
            {
                View view1 = getChildAt(i3);
                int j3 = view1.getMeasuredWidth();
                int k3 = view1.getMeasuredHeight();
                view1.getHitRect(rect);
                if(view1.getVisibility() == 0 && view1.getTag() != null && !rect.isEmpty() && mEnabledChildAnimation)
                {
                    rect.set(k1, l1, k1 + j3, l1 + k3);
                    mChildAnimate[i3].start(view1, rect);
                } else
                {
                    view1.layout(k1, l1, k1 + j3, l1 + k3);
                }
                k1 += j3;
                i3++;
            }
        } else
        {
            int j2 = j1 - 1;
            while(j2 >= 0) 
            {
                View view = getChildAt(j2);
                int k2 = view.getMeasuredWidth();
                int l2 = view.getMeasuredHeight();
                view.getHitRect(rect);
                if(view.getVisibility() == 0 && view.getTag() != null && !rect.isEmpty() && mEnabledChildAnimation)
                {
                    rect.set(0, l1, k2 + 0, l1 + l2);
                    mChildAnimate[j2].start(view, rect);
                } else
                {
                    view.layout(0, l1, k2 + 0, l1 + l2);
                }
                l1 += l2 + i2;
                j2--;
            }
        }
    }

    protected void onMeasure(int i, int j)
    {
        super.onMeasure(i, j);
        int k = android.view.View.MeasureSpec.getSize(i);
        int l = android.view.View.MeasureSpec.getSize(j);
        int i1 = getChildCount();
        int j1;
        int k1;
        if(getOrientation() == 0)
            mChildWidth = k / 4;
        else
            mChildHeight = l / 4 - mTopOffset;
        j1 = android.view.View.MeasureSpec.makeMeasureSpec(mChildWidth, 0x40000000);
        k1 = android.view.View.MeasureSpec.makeMeasureSpec(mChildHeight, 0x40000000);
        for(int l1 = 0; l1 < i1; l1++)
            getChildAt(l1).measure(j1, k1);

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

    public void removeItems(ArrayList arraylist)
    {
        final int count = getChildCount();
        final HashSet packageNames = new HashSet();
        int i = arraylist.size();
        for(int j = 0; j < i; j++)
            packageNames.add(((ApplicationInfo)arraylist.get(j)).componentName.getPackageName());

        post(new Runnable() {

            public void run()
            {
                for(int k = 0; k < count; k++)
                {
                    View view = getChildAt(k);
                    ArrayList arraylist1 = new ArrayList();
                    arraylist1.clear();
                    Object obj = view.getTag();
                    if(obj != null && (obj instanceof ShortcutInfo))
                    {
                        ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
                        Intent intent = shortcutinfo.intent;
                        ComponentName componentname = intent.getComponent();
                        if("android.intent.action.MAIN".equals(intent.getAction()) && componentname != null)
                        {
                            Iterator iterator = packageNames.iterator();
                            do
                            {
                                if(!iterator.hasNext())
                                    break;
                                if(((String)iterator.next()).equals(componentname.getPackageName()))
                                {
                                    LauncherModel.deleteItemFromDatabase(mLauncher, shortcutinfo);
                                    arraylist1.add(view);
                                }
                            } while(true);
                        }
                    }
                    int l = arraylist1.size();
                    for(int i1 = 0; i1 < l; i1++)
                    {
                        View view1 = (View)arraylist1.get(i1);
                        int j1 = indexOfChild(view1);
                        removeViewAt(j1);
                        addView(mVirtualView[j1], j1);
                    }

                }

            }

            final TopFourZone this$0;
            final int val$count;
            final HashSet val$packageNames;

            
            {
                this$0 = TopFourZone.this;
                count = i;
                packageNames = hashset;
                super();
            }
        }
);
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    public void updateBadges()
    {
        int i = 0;
        for(int j = getChildCount(); i < j; i++)
        {
            View view = getChildAt(i);
            Object obj = view.getTag();
            if(obj instanceof ShortcutInfo)
            {
                ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
                shortcutinfo.badgeCount = getBadgeCount(shortcutinfo);
                view.invalidate();
            }
        }

    }

    void updateShortcuts(ArrayList arraylist)
    {
        int i;
        int j;
        i = getChildCount();
        j = 0;
_L7:
        if(j >= i) goto _L2; else goto _L1
_L1:
        View view;
        ShortcutInfo shortcutinfo;
        ComponentName componentname;
        int k;
        int l;
        view = getChildAt(j);
        Object obj = view.getTag();
        if(obj == null || !(obj instanceof ShortcutInfo))
            continue; /* Loop/switch isn't completed */
        shortcutinfo = (ShortcutInfo)obj;
        Intent intent = shortcutinfo.intent;
        componentname = intent.getComponent();
        if(((ItemInfo) (shortcutinfo)).itemType != 0 || !"android.intent.action.MAIN".equals(intent.getAction()) || componentname == null)
            continue; /* Loop/switch isn't completed */
        k = arraylist.size();
        l = 0;
_L4:
        if(l >= k)
            continue; /* Loop/switch isn't completed */
        if(((ApplicationInfo)arraylist.get(l)).componentName.equals(componentname))
        {
            shortcutinfo.setIcon(mIconCache.getIcon(shortcutinfo.intent));
            if(!(view instanceof MenuItemView))
                break; /* Loop/switch isn't completed */
            ((MenuItemView)view).setImageDrawable(new FastBitmapDrawable(shortcutinfo.getIcon(mIconCache)));
        }
_L5:
        l++;
        if(true) goto _L4; else goto _L3
_L3:
        if(view instanceof TextView)
            ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(shortcutinfo.getIcon(mIconCache)), null, null);
        else
            Log.e("Launcher.TopFourZone", "TopFourZone.updateShortcuts : Unknow shortcut type. ICON NOT CHANGED");
          goto _L5
        if(true) goto _L4; else goto _L6
_L6:
        j++;
          goto _L7
_L2:
    }

    private static final Object APPLICATION_ICON_SLOT_TAG = new Object();
    private static final Object EMPTY_SLOT_VIEW_TAG = new Object();
    private Animate mChildAnimate[];
    private int mChildHeight;
    private int mChildWidth;
    private View mDraggingView;
    private boolean mEnabledChildAnimation;
    private boolean mEnabledDrawing;
    private IconCache mIconCache;
    private LayoutInflater mInflater;
    private Launcher mLauncher;
    private int mOldOrientation;
    private android.view.View.OnClickListener mOnClickListener;
    private android.view.View.OnLongClickListener mOnLongClickListener;
    private int mOrientation;
    private Rect mTmpRect;
    private Drawable mTopFourBgDrawable;
    private int mTopOffset;
    private View mVirtualView[];




}
