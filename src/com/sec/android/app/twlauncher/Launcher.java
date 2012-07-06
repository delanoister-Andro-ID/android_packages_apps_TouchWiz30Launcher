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

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LiveFolderInfo, Utilities, FolderInfo, LauncherModel, 
//            ItemInfo, DragLayer, IconCache, MenuManager, 
//            Workspace, CellLayout, LauncherAppWidgetInfo, LauncherAppWidgetHost, 
//            LiveFolderIcon, BadgeCache, AppShortcutZone, UserFolderInfo, 
//            UserFolder, Folder, LiveFolder, WidgetPreview, 
//            LauncherProvider, MenuDrawer, DeleteZone, TopFourZone, 
//            QuickViewWorkspace, QuickViewMainMenu, SamsungWidgetPackageManager, SamsungAppWidgetItem, 
//            FolderIcon, SamsungAppWidgetInfo, SamsungAppWidgetView, ApplicationInfo, 
//            ShortcutInfo, FastBitmapDrawable, BuildLocaleChecker, AppMenu, 
//            LauncherApplication, LauncherConfig, WallpaperCanvasView, SamsungUtils, 
//            AddAdapter

public final class Launcher extends ActivityGroup
    implements android.view.View.OnClickListener, android.view.View.OnLongClickListener, LauncherModel.Callbacks
{
    static class WallpaperFastBitmapDrawable extends Drawable
    {

        public void draw(Canvas canvas)
        {
            canvas.drawBitmap(mBitmap, mDrawLeft, mDrawTop, null);
        }

        public int getIntrinsicHeight()
        {
            return mHeight;
        }

        public int getIntrinsicWidth()
        {
            return mWidth;
        }

        public int getMinimumHeight()
        {
            return mHeight;
        }

        public int getMinimumWidth()
        {
            return mWidth;
        }

        public int getOpacity()
        {
            return -1;
        }

        public void setAlpha(int i)
        {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setBounds(int i, int j, int k, int l)
        {
            mDrawLeft = i + (k - i - mWidth) / 2;
            mDrawTop = j + (l - j - mHeight) / 2;
        }

        public void setBounds(Rect rect)
        {
            super.setBounds(rect);
        }

        public void setColorFilter(ColorFilter colorfilter)
        {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setDither(boolean flag)
        {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setFilterBitmap(boolean flag)
        {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        private final Bitmap mBitmap;
        private int mDrawLeft;
        private int mDrawTop;
        private final int mHeight;
        private final int mWidth;

        private WallpaperFastBitmapDrawable(Bitmap bitmap)
        {
            mBitmap = bitmap;
            mWidth = bitmap.getWidth();
            mHeight = bitmap.getHeight();
            setBounds(0, 0, mWidth, mHeight);
        }

    }

    class PackageDeleteObserver extends android.content.pm.IPackageDeleteObserver.Stub
    {

        public void packageDeleted(boolean flag)
        {
            Message message = mHandler.obtainMessage(1);
            int i;
            if(flag)
                i = 1;
            else
                i = 0;
            message.arg1 = i;
            mHandler.sendMessage(message);
        }

        final Launcher this$0;

        PackageDeleteObserver()
        {
            this$0 = Launcher.this;
            super();
        }
    }

    private class BadgeChangeObserver extends ContentObserver
    {

        public void onChange(boolean flag)
        {
            if(mIsActive)
            {
                mHandler.removeCallbacks(mRunBadgeChanged);
                mHandler.post(mRunBadgeChanged);
            } else
            {
                mIsChangedBadge = true;
            }
        }

        final Launcher this$0;

        public BadgeChangeObserver()
        {
            this$0 = Launcher.this;
            super(new Handler());
        }
    }

    private class AppWidgetResetObserver extends ContentObserver
    {

        public void onChange(boolean flag)
        {
            onAppWidgetReset();
        }

        final Launcher this$0;

        public AppWidgetResetObserver()
        {
            this$0 = Launcher.this;
            super(new Handler());
        }
    }

    private class AirplaneModeChangeReceiver extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals("android.intent.action.AIRPLANE_MODE"))
                mModel.startLoader(Launcher.this, false);
        }

        final Launcher this$0;

        private AirplaneModeChangeReceiver()
        {
            this$0 = Launcher.this;
            super();
        }

    }

    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver
    {

        public void onReceive(Context context, Intent intent)
        {
            closeSystemDialogs();
        }

        final Launcher this$0;

        private CloseSystemDialogsIntentReceiver()
        {
            this$0 = Launcher.this;
            super();
        }

    }

    private class TextDialog
        implements android.content.DialogInterface.OnDismissListener, android.content.DialogInterface.OnShowListener
    {

        private void cleanup()
        {
            Launcher.mUninstallPackageName = null;
            mWaitingForResult = false;
        }

        Dialog createDialog(int i)
        {
            android.app.AlertDialog.Builder builder;
            mId = i;
            builder = new android.app.AlertDialog.Builder(Launcher.this);
            if(i != 4) goto _L2; else goto _L1
_L1:
            builder.setIcon(0x1080027);
            builder.setTitle(getString(0x7f0a0008));
            builder.setMessage(0x7f0a003c);
_L4:
            builder.setCancelable(true);
            builder.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface dialoginterface)
                {
                    cancelRemovePage();
                    cleanup();
                }

                final TextDialog this$1;

                
                {
                    this$1 = TextDialog.this;
                    super();
                }
            }
);
            builder.setNegativeButton(getString(0x7f0a000c), new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialoginterface, int j)
                {
                    int k = mId;
                    if(k != 6) goto _L2; else goto _L1
_L1:
                    cancelRemovePage();
_L4:
                    cleanup();
                    return;
_L2:
                    if(k == 4)
                        mDragLayer.invalidate();
                    if(true) goto _L4; else goto _L3
_L3:
                }

                final TextDialog this$1;

                
                {
                    this$1 = TextDialog.this;
                    super();
                }
            }
);
            builder.setPositiveButton(getString(0x7f0a000b), new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialoginterface, int j)
                {
                    int k = mId;
                    if(k != 4) goto _L2; else goto _L1
_L1:
                    uninstallPackage();
                    cleanup();
_L4:
                    return;
_L2:
                    if(k == 5)
                    {
                        menudiscard();
                        cleanup();
                    } else
                    if(k == 6)
                        removePage();
                    if(true) goto _L4; else goto _L3
_L3:
                }

                final TextDialog this$1;

                
                {
                    this$1 = TextDialog.this;
                    super();
                }
            }
);
            AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener(this);
            alertdialog.setOnDismissListener(this);
            return alertdialog;
_L2:
            if(i == 5)
                builder.setMessage(0x7f0a003d);
            else
            if(i == 6)
                builder.setMessage(0x7f0a0045);
            if(true) goto _L4; else goto _L3
_L3:
        }

        public void onDismiss(DialogInterface dialoginterface)
        {
        }

        public void onShow(DialogInterface dialoginterface)
        {
            mWaitingForResult = true;
        }

        int mId;
        final Launcher this$0;


        private TextDialog()
        {
            this$0 = Launcher.this;
            super();
        }

    }

    private class CreateShortcut
        implements android.content.DialogInterface.OnCancelListener, android.content.DialogInterface.OnClickListener, android.content.DialogInterface.OnDismissListener, android.content.DialogInterface.OnShowListener
    {

        private void cleanup()
        {
            dismissDialog(1);
_L2:
            return;
            Exception exception;
            exception;
            if(true) goto _L2; else goto _L1
_L1:
        }

        Dialog createDialog()
        {
            mAdapter = new AddAdapter(Launcher.this);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Launcher.this);
            builder.setTitle(getString(0x7f0a0011));
            builder.setAdapter(mAdapter, this);
            builder.setInverseBackgroundForced(true);
            AlertDialog alertdialog = builder.create();
            alertdialog.setOnCancelListener(this);
            alertdialog.setOnDismissListener(this);
            alertdialog.setOnShowListener(this);
            return alertdialog;
        }

        public void onCancel(DialogInterface dialoginterface)
        {
            mWaitingForResult = false;
            cleanup();
        }

        public void onClick(DialogInterface dialoginterface, int i)
        {
            Resources resources;
            resources = getResources();
            cleanup();
            i;
            JVM INSTR tableswitch 0 3: default 44
        //                       0 59
        //                       1 45
        //                       2 423
        //                       3 571;
               goto _L1 _L2 _L3 _L4 _L5
_L1:
            return;
_L3:
            pickShortcut(7, 0x7f0a0022);
            continue; /* Loop/switch isn't completed */
_L2:
            int j = mAppWidgetHost.allocateAppWidgetId();
            Intent intent1 = new Intent("android.appwidget.action.APPWIDGET_PICK");
            intent1.putExtra("appWidgetId", j);
            Intent intent2 = new Intent();
            intent2.setAction("com.samsung.sec.android.SAMSUNG_APP_WIDGET_ACTION");
            intent2.addCategory("com.samsung.sec.android.SAMSUNG_APP_WIDGET");
            ResolveInfo aresolveinfo[] = (ResolveInfo[])getPackageManager().queryIntentActivities(intent2, 0).toArray(new ResolveInfo[0]);
            ArrayList arraylist2 = new ArrayList();
            ArrayList arraylist3 = new ArrayList();
            Iterator iterator = mSamsungWidgetPackageManager.getWidgetItems().iterator();
label0:
            do
            {
                if(iterator.hasNext())
                {
                    SamsungAppWidgetItem samsungappwidgetitem = (SamsungAppWidgetItem)iterator.next();
                    int k = 0;
                    int l = aresolveinfo.length;
                    int i1 = 0;
                    do
                    {
label1:
                        {
                            if(i1 < l)
                            {
                                ResolveInfo resolveinfo = aresolveinfo[i1];
                                if(!resolveinfo.activityInfo.packageName.equals(samsungappwidgetitem.mPackageName) || !resolveinfo.activityInfo.name.equals(samsungappwidgetitem.mClassName))
                                    break label1;
                                k = resolveinfo.activityInfo.getIconResource();
                            }
                            AppWidgetProviderInfo appwidgetproviderinfo = new AppWidgetProviderInfo();
                            appwidgetproviderinfo.label = samsungappwidgetitem.mWidgetTitle;
                            appwidgetproviderinfo.icon = k;
                            appwidgetproviderinfo.provider = new ComponentName(samsungappwidgetitem.mPackageName, samsungappwidgetitem.mClassName);
                            arraylist2.add(appwidgetproviderinfo);
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("custom_widget", (new StringBuilder()).append(samsungappwidgetitem.mPackageName).append(samsungappwidgetitem.mClassName).toString());
                            arraylist3.add(bundle1);
                            continue label0;
                        }
                        i1++;
                    } while(true);
                }
                intent1.putParcelableArrayListExtra("customInfo", arraylist2);
                intent1.putParcelableArrayListExtra("customExtras", arraylist3);
                startActivityForResult(intent1, 9);
                continue; /* Loop/switch isn't completed */
            } while(true);
_L4:
            Bundle bundle = new Bundle();
            ArrayList arraylist = new ArrayList();
            arraylist.add(resources.getString(0x7f0a0016));
            bundle.putStringArrayList("android.intent.extra.shortcut.NAME", arraylist);
            ArrayList arraylist1 = new ArrayList();
            arraylist1.add(android.content.Intent.ShortcutIconResource.fromContext(Launcher.this, 0x7f020073));
            bundle.putParcelableArrayList("android.intent.extra.shortcut.ICON_RESOURCE", arraylist1);
            Intent intent = new Intent("android.intent.action.PICK_ACTIVITY");
            intent.putExtra("android.intent.extra.INTENT", new Intent("android.intent.action.CREATE_LIVE_FOLDER"));
            intent.putExtra("android.intent.extra.TITLE", getText(0x7f0a0023));
            intent.putExtras(bundle);
            startActivityForResult(intent, 8);
            continue; /* Loop/switch isn't completed */
_L5:
            startWallpaper();
            if(true) goto _L1; else goto _L6
_L6:
        }

        public void onDismiss(DialogInterface dialoginterface)
        {
            mWaitingForResult = false;
        }

        public void onShow(DialogInterface dialoginterface)
        {
            mWaitingForResult = true;
        }

        private AddAdapter mAdapter;
        final Launcher this$0;

        private CreateShortcut()
        {
            this$0 = Launcher.this;
            super();
        }

    }

    private class RenameFolder
    {

        private void changeFolderName()
        {
            String s = mInput.getText().toString();
            if(!TextUtils.isEmpty(s))
            {
                mFolderInfo = (FolderInfo)Launcher.mFolders.get(Long.valueOf(((ItemInfo) (mFolderInfo)).id));
                mFolderInfo.title = s;
                LauncherModel.updateItemInDatabase(Launcher.this, mFolderInfo);
                if(mWorkspaceLoading)
                {
                    lockAllApps();
                    mModel.startLoader(Launcher.this, false);
                } else
                {
                    FolderIcon foldericon = (FolderIcon)mWorkspace.getViewForTag(mFolderInfo);
                    if(foldericon != null)
                    {
                        foldericon.setText(s);
                        getWorkspace().requestLayout();
                    } else
                    {
                        lockAllApps();
                        mWorkspaceLoading = true;
                        mModel.startLoader(Launcher.this, false);
                    }
                }
            }
            cleanup();
        }

        private void cleanup()
        {
            try
            {
                dismissDialog(2);
            }
            catch(Exception exception) { }
            mWaitingForResult = false;
            mFolderInfo = null;
        }

        Dialog createDialog()
        {
            View view = View.inflate(Launcher.this, 0x7f030012, null);
            mInput = (EditText)view.findViewById(0x7f060027);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Launcher.this);
            builder.setIcon(0);
            builder.setTitle(getString(0x7f0a000a));
            builder.setCancelable(true);
            builder.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface dialoginterface)
                {
                    cleanup();
                }

                final RenameFolder this$1;

                
                {
                    this$1 = RenameFolder.this;
                    super();
                }
            }
);
            builder.setNegativeButton(getString(0x7f0a000c), new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialoginterface, int i)
                {
                    cleanup();
                }

                final RenameFolder this$1;

                
                {
                    this$1 = RenameFolder.this;
                    super();
                }
            }
);
            builder.setPositiveButton(getString(0x7f0a000b), new android.content.DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialoginterface, int i)
                {
                    changeFolderName();
                }

                final RenameFolder this$1;

                
                {
                    this$1 = RenameFolder.this;
                    super();
                }
            }
);
            builder.setView(view);
            AlertDialog alertdialog = builder.create();
            alertdialog.setOnShowListener(new android.content.DialogInterface.OnShowListener() {

                public void onShow(DialogInterface dialoginterface)
                {
                    mWaitingForResult = true;
                    mInput.requestFocus();
                    ((InputMethodManager)getSystemService("input_method")).showSoftInput(mInput, 0);
                }

                final RenameFolder this$1;

                
                {
                    this$1 = RenameFolder.this;
                    super();
                }
            }
);
            return alertdialog;
        }

        private EditText mInput;
        final Launcher this$0;




        private RenameFolder()
        {
            this$0 = Launcher.this;
            super();
        }

    }

    private static class LocaleConfiguration
    {

        public String locale;
        public int mcc;
        public int mnc;

        private LocaleConfiguration()
        {
            mcc = -1;
            mnc = -1;
        }

    }


    public Launcher()
    {
        super(false);
        MenuManagerBackKeyDownInfo = false;
        mDefaultKeySsb = null;
        mWorkspaceLoading = true;
        mMainMenuLoading = true;
        mDesktopItems = new ArrayList();
        isHomeKeyToDefaultPage = false;
        mStateQuickNavigation = -1;
        mIsDeletePopup = false;
        mDeleteIndex = -1;
        mMenuScreenCount = -1;
        mWallpaperImageDrawable = null;
        mDimWallpaperImageDrawable = null;
        mProductModelFamilyName = "S1";
        mProductModelName = "GT-I9000";
        mForce16BitWindow = false;
        mConfigChange = false;
        mOpenFolders = new ArrayList();
        mUserFolders = null;
        mHiddenFocus = false;
        mOptionMenuOpening = false;
        mHandler = new Handler() {

            public void handleMessage(Message message)
            {
                message.what;
                JVM INSTR lookupswitch 3: default 40
            //                           1: 41
            //                           2: 81
            //                           100: 101;
                   goto _L1 _L2 _L3 _L4
_L1:
                return;
_L2:
                if(message.arg1 == 1)
                    Log.w("Launcher", "uninstall succeeded");
                else
                    Log.w("Launcher", "uninstall failed");
                mMenuManager.unlock();
                continue; /* Loop/switch isn't completed */
_L3:
                if(isDefaultIMEI())
                    setSomethingsInDefaultIMEI();
                continue; /* Loop/switch isn't completed */
_L4:
                if(mModel.isBusy())
                {
                    Message message1 = obtainMessage(100);
                    message1.setData(message.getData());
                    sendMessageDelayed(message1, 100L);
                } else
                {
                    completeAddAppWidget(message.getData(), mAddItemCellInfo);
                }
                if(true) goto _L1; else goto _L5
_L5:
            }

            final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
        }
;
        mAddWidgetType = 0;
        mWidgetId = -1;
        mLauncherAppWidgetInfo = null;
        mAppWidgetInfo = null;
        mSpans = new int[2];
        mSamsungWidgetInfo = null;
        mBlankScreen = new int[2];
        mRunBadgeChanged = new Runnable() {

            public void run()
            {
                onBadgeChanged();
            }

            final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
        }
;
        mIsOpaqueWindow = false;
    }

    private boolean acceptFilter()
    {
        boolean flag;
        if(!((InputMethodManager)getSystemService("input_method")).isFullscreenMode())
            flag = true;
        else
            flag = false;
        return flag;
    }

    private void addItems()
    {
        showAddDialog(mMenuAddInfo);
    }

    static LiveFolderInfo addLiveFolder(Context context, Intent intent, CellLayout.CellInfo cellinfo, boolean flag)
    {
        Intent intent1;
        String s;
        android.os.Parcelable parcelable;
        intent1 = (Intent)intent.getParcelableExtra("android.intent.extra.livefolder.BASE_INTENT");
        s = intent.getStringExtra("android.intent.extra.livefolder.NAME");
        parcelable = intent.getParcelableExtra("android.intent.extra.livefolder.ICON");
        if(parcelable == null || !(parcelable instanceof android.content.Intent.ShortcutIconResource)) goto _L2; else goto _L1
_L1:
        android.content.Intent.ShortcutIconResource shortcuticonresource1 = (android.content.Intent.ShortcutIconResource)parcelable;
        Drawable drawable2;
        Resources resources = context.getPackageManager().getResourcesForApplication(shortcuticonresource1.packageName);
        drawable2 = resources.getDrawable(resources.getIdentifier(shortcuticonresource1.resourceName, null, null));
        android.content.Intent.ShortcutIconResource shortcuticonresource;
        Drawable drawable;
        android.content.Intent.ShortcutIconResource shortcuticonresource2 = shortcuticonresource1;
        drawable = drawable2;
        shortcuticonresource = shortcuticonresource2;
_L5:
        Drawable drawable1;
        LiveFolderInfo livefolderinfo;
        Exception exception;
        Exception exception1;
        if(drawable == null)
            drawable1 = context.getResources().getDrawable(0x7f020073);
        else
            drawable1 = drawable;
        livefolderinfo = new LiveFolderInfo();
        livefolderinfo.icon = Utilities.createIconBitmap(drawable1, context);
        livefolderinfo.title = s;
        livefolderinfo.iconResource = shortcuticonresource;
        livefolderinfo.uri = intent.getData();
        livefolderinfo.baseIntent = intent1;
        livefolderinfo.displayMode = intent.getIntExtra("android.intent.extra.livefolder.DISPLAY_MODE", 1);
        LauncherModel.addItemToDatabase(context, livefolderinfo, -100L, cellinfo.screen, cellinfo.cellX, cellinfo.cellY, flag);
        mFolders.put(Long.valueOf(((ItemInfo) (livefolderinfo)).id), livefolderinfo);
        return livefolderinfo;
        exception;
        shortcuticonresource1 = null;
_L3:
        Log.w("Launcher", (new StringBuilder()).append("Could not load live folder icon: ").append(parcelable).toString());
        shortcuticonresource = shortcuticonresource1;
        drawable = null;
        continue; /* Loop/switch isn't completed */
        exception1;
        if(true) goto _L3; else goto _L2
_L2:
        shortcuticonresource = null;
        drawable = null;
        if(true) goto _L5; else goto _L4
_L4:
    }

    private void addOccupiedCells(boolean aflag[][], int i, ItemInfo iteminfo)
    {
        if(iteminfo.screen == i)
        {
            for(int j = iteminfo.cellX; j < iteminfo.cellX + iteminfo.spanX; j++)
            {
                for(int k = iteminfo.cellY; k < iteminfo.cellY + iteminfo.spanY; k++)
                    aflag[j][k] = true;

            }

        }
    }

    private void blockChildrenFocus()
    {
        mDragLayer.setDescendantFocusability(0x60000);
        mDragLayer.requestFocus();
    }

    private void checkForLocaleChange()
    {
        LocaleConfiguration localeconfiguration = new LocaleConfiguration();
        readConfiguration(this, localeconfiguration);
        Configuration configuration = getResources().getConfiguration();
        String s = localeconfiguration.locale;
        String s1 = configuration.locale.toString();
        int i = localeconfiguration.mcc;
        int j = configuration.mcc;
        int k = localeconfiguration.mnc;
        int l = configuration.mnc;
        boolean flag;
        if(!s1.equals(s) || j != i || l != k)
            flag = true;
        else
            flag = false;
        mLocaleChanged = flag;
        if(mLocaleChanged)
        {
            localeconfiguration.locale = s1;
            localeconfiguration.mcc = j;
            localeconfiguration.mnc = l;
            writeConfiguration(this, localeconfiguration);
            mIconCache.flush();
        }
    }

    private void checkHwNaviKey()
    {
        int i;
        Log.d("Launcher", (new StringBuilder()).append("checkHwNaviKey(). current configuration : ").append(getResources().getConfiguration()).toString());
        i = getResources().getConfiguration().navigation;
        if(i != 1) goto _L2; else goto _L1
_L1:
        Log.i("Launcher", "No navigation h/w keys. block focus");
        blockChildrenFocus();
_L8:
        return;
_L2:
        i;
        JVM INSTR tableswitch 2 4: default 92
    //                   2 132
    //                   3 139
    //                   4 146;
           goto _L3 _L4 _L5 _L6
_L6:
        break MISSING_BLOCK_LABEL_146;
_L4:
        break; /* Loop/switch isn't completed */
_L3:
        String s = "UNKNOWN";
_L9:
        Log.d("Launcher", (new StringBuilder()).append("You have ").append(s).append(" navigation keys").toString());
        if(true) goto _L8; else goto _L7
_L7:
        s = "DPAD";
          goto _L9
_L5:
        s = "TRACKBALL";
          goto _L9
        s = "WHEEL";
          goto _L9
    }

    private void clearTypedText()
    {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    private void closeDrawer()
    {
        closeDrawer(true);
    }

    private void closeDrawer(boolean flag)
    {
        MenuManager menumanager = mMenuManager;
        if(menumanager.isOpened())
        {
            if(flag)
                menumanager.animateClose();
            else
                menumanager.close();
            if(menumanager.hasFocus())
                mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
        }
        if(menumanager.getMode() == 2)
        {
            menusave();
            menumanager.setMode(0);
        }
    }

    private void closeFolder()
    {
        Folder folder = mWorkspace.getOpenFolder();
        if(folder != null)
            closeFolder(folder);
    }

    private void completeAddAppWidget(Intent intent, CellLayout.CellInfo cellinfo)
    {
        completeAddAppWidget(intent.getExtras(), cellinfo);
    }

    private void completeAddAppWidget(Bundle bundle, CellLayout.CellInfo cellinfo)
    {
label0:
        {
            {
                int i = bundle.getInt("appWidgetId", -1);
                Log.d("Launcher", (new StringBuilder()).append("completeAddAppWidget(): dumping extras content=").append(bundle.toString()).toString());
                AppWidgetProviderInfo appwidgetproviderinfo = mAppWidgetManager.getAppWidgetInfo(i);
                if(cellinfo.screen >= mWorkspace.getChildCount())
                    cellinfo.screen = mWorkspace.getCurrentScreen();
                int ai[] = ((CellLayout)mWorkspace.getChildAt(cellinfo.screen)).rectToCell(appwidgetproviderinfo.minWidth, appwidgetproviderinfo.minHeight);
                int ai1[] = mCellCoordinates;
                if(findSlot(cellinfo, ai1, ai[0], ai[1]))
                    break label0;
                mWidgetId = i;
                mAppWidgetInfo = appwidgetproviderinfo;
                mSpans[0] = ai[0];
                mSpans[1] = ai[1];
                mAddWidgetType = 2;
                if(isAllPageSlot(mWorkspace.getCurrentScreen()))
                {
                    cancelAddWidget();
                } else
                {
                    int j = mWorkspace.getChildCount();
                    LauncherAppWidgetInfo launcherappwidgetinfo;
                    if(j < SCREEN_COUNT)
                        createBlankPage();
                    else
                        j = -1;
                    prepareWidgetPreview(2, j);
                }
            }
            return;
        }
        launcherappwidgetinfo = new LauncherAppWidgetInfo(i);
        launcherappwidgetinfo.spanX = ai[0];
        launcherappwidgetinfo.spanY = ai[1];
        LauncherModel.addItemToDatabase(this, launcherappwidgetinfo, -100L, mWorkspace.getCurrentScreen(), ai1[0], ai1[1], false);
        if(!mRestoring)
        {
            mDesktopItems.add(launcherappwidgetinfo);
            launcherappwidgetinfo.hostView = mAppWidgetHost.createView(this, i, appwidgetproviderinfo);
            launcherappwidgetinfo.hostView.setAppWidget(i, appwidgetproviderinfo);
            launcherappwidgetinfo.hostView.setTag(launcherappwidgetinfo);
            mWorkspace.addInCurrentScreen(launcherappwidgetinfo.hostView, ai1[0], ai1[1], ((ItemInfo) (launcherappwidgetinfo)).spanX, ((ItemInfo) (launcherappwidgetinfo)).spanY, isWorkspaceLocked());
        }
        if(false)
            ;
        else
            break MISSING_BLOCK_LABEL_182;
    }

    private void completeAddLiveFolder(Intent intent, CellLayout.CellInfo cellinfo)
    {
        int i;
        cellinfo.screen = mWorkspace.getCurrentScreen();
        i = findPreferredSingleSlotOrAnySlot(cellinfo);
        if(i != -1) goto _L2; else goto _L1
_L1:
        return;
_L2:
        boolean flag;
        LiveFolderInfo livefolderinfo;
        if(i != mWorkspace.getCurrentScreen())
        {
            mWorkspace.setCurrentScreen(i);
            flag = true;
        } else
        {
            flag = false;
        }
        livefolderinfo = addLiveFolder(this, intent, cellinfo, false);
        if(!mRestoring)
        {
            LiveFolderIcon livefoldericon = LiveFolderIcon.fromXml(0x7f030010, this, (ViewGroup)mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), livefolderinfo);
            mWorkspace.addInScreen(livefoldericon, i, cellinfo.cellX, cellinfo.cellY, 1, 1, isWorkspaceLocked());
            if(flag)
            {
                mDragLayer.invalidate();
                mWorkspace.setFastAtuoScrollScreen(i);
            }
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    private void completeAddShortcut(Intent intent, CellLayout.CellInfo cellinfo)
    {
        boolean flag;
        int i;
        flag = false;
        i = findPreferredSingleSlotOrAnySlot(cellinfo);
        if(i != -1) goto _L2; else goto _L1
_L1:
        return;
_L2:
        if(i != mWorkspace.getCurrentScreen())
        {
            mWorkspace.setCurrentScreen(i);
            flag = true;
        }
        ShortcutInfo shortcutinfo = mModel.addShortcut(this, intent, cellinfo, false);
        if(!mRestoring)
        {
            mWorkspace.addApplicationShortcut(shortcutinfo, cellinfo, isWorkspaceLocked());
            if(flag)
            {
                mDragLayer.invalidate();
                mWorkspace.setFastAtuoScrollScreen(i);
            }
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    private void completePreviewAppWidget()
    {
        if(mLauncherAppWidgetInfo == null)
        {
            cancelAddWidget();
        } else
        {
            int ai[] = mCellCoordinates;
            LauncherAppWidgetInfo launcherappwidgetinfo = mLauncherAppWidgetInfo;
            mDesktopItems.add(launcherappwidgetinfo);
            LauncherModel.addItemToDatabase(this, launcherappwidgetinfo, -100L, mWorkspace.getCurrentScreen(), ai[0], ai[1], false);
            mWorkspace.addInCurrentScreen(launcherappwidgetinfo.hostView, ai[0], ai[1], ((ItemInfo) (launcherappwidgetinfo)).spanX, ((ItemInfo) (launcherappwidgetinfo)).spanY, isWorkspaceLocked());
        }
    }

    private int createBlankPage()
    {
        int i;
        if(mBlankScreen[0] != -1 || mBlankScreen[1] != -1)
        {
            i = -1;
        } else
        {
            Workspace workspace = mWorkspace;
            int j = workspace.getChildCount();
            if(j < SCREEN_COUNT)
            {
                CellLayout celllayout = (CellLayout)LayoutInflater.from(this).inflate(0x7f030016, workspace, false);
                celllayout.setOnLongClickListener(this);
                celllayout.setId(getCellLayoutId(j));
                workspace.addView(celllayout);
                mBlankScreen[0] = j;
                saveScreenInfo();
                i = j;
            } else
            {
                i = -1;
            }
        }
        return i;
    }

    private boolean findSlot(CellLayout.CellInfo cellinfo, int ai[], int i, int j)
    {
        return findSlot(cellinfo, ai, i, j, true);
    }

    private boolean findSlot(CellLayout.CellInfo cellinfo, int ai[], int i, int j, boolean flag)
    {
        if(cellinfo.findCellForSpan(ai, i, j)) goto _L2; else goto _L1
_L1:
        boolean flag1;
        boolean aflag[];
        CellLayout.CellInfo cellinfo1;
        if(mSavedState != null)
            aflag = mSavedState.getBooleanArray("launcher.add_occupied_cells");
        else
            aflag = null;
        cellinfo1 = mWorkspace.findAllVacantCells(aflag);
        if(cellinfo1 == null || cellinfo1.findCellForSpan(ai, i, j)) goto _L2; else goto _L3
_L3:
        if(!flag)
        {
            flag1 = false;
        } else
        {
            showWidgetMessage(true);
            flag1 = false;
        }
_L5:
        return flag1;
_L2:
        flag1 = true;
        if(true) goto _L5; else goto _L4
_L4:
    }

    private String getBatteryLevel()
    {
        StringBuffer stringbuffer;
        BufferedReader bufferedreader;
        stringbuffer = new StringBuffer();
        bufferedreader = null;
        BufferedReader bufferedreader1 = new BufferedReader(new FileReader("/sys/class/power_supply/battery/capacity"), 4096);
_L4:
        String s = bufferedreader1.readLine();
        if(s == null) goto _L2; else goto _L1
_L1:
        stringbuffer.append(s);
        if(true) goto _L4; else goto _L3
_L3:
        FileNotFoundException filenotfoundexception;
        filenotfoundexception;
        FileNotFoundException filenotfoundexception1;
        filenotfoundexception1 = filenotfoundexception;
        bufferedreader = bufferedreader1;
_L10:
        filenotfoundexception1.printStackTrace();
        IOException ioexception1;
        Exception exception;
        IOException ioexception6;
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception4) { }
        return stringbuffer.toString();
_L2:
        if(bufferedreader1 != null)
            try
            {
                bufferedreader1.close();
            }
            catch(IOException ioexception5) { }
        break MISSING_BLOCK_LABEL_73;
        ioexception6;
        ioexception1 = ioexception6;
_L8:
        ioexception1.printStackTrace();
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception3) { }
        break MISSING_BLOCK_LABEL_73;
        exception;
_L6:
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception2) { }
        throw exception;
        exception;
        bufferedreader = bufferedreader1;
        if(true) goto _L6; else goto _L5
_L5:
        IOException ioexception;
        ioexception;
        ioexception1 = ioexception;
        bufferedreader = bufferedreader1;
        if(true) goto _L8; else goto _L7
_L7:
        FileNotFoundException filenotfoundexception2;
        filenotfoundexception2;
        filenotfoundexception1 = filenotfoundexception2;
        if(true) goto _L10; else goto _L9
_L9:
    }

    static int getScreen()
    {
        Object obj = sLock;
        obj;
        JVM INSTR monitorenter ;
        int i = sScreen;
        return i;
    }

    private String getTSPfirmware()
    {
        BufferedReader bufferedreader;
        String s;
        new StringBuffer();
        bufferedreader = null;
        s = null;
        BufferedReader bufferedreader1 = new BufferedReader(new FileReader("/sys/class/touch/firmware/firmware"), 7);
        String s1 = bufferedreader1.readLine();
        s = s1;
        if(bufferedreader1 != null)
            try
            {
                bufferedreader1.close();
            }
            catch(IOException ioexception5) { }
_L1:
        return s.substring(3, 5);
        FileNotFoundException filenotfoundexception2;
        filenotfoundexception2;
        FileNotFoundException filenotfoundexception1 = filenotfoundexception2;
_L5:
        filenotfoundexception1.printStackTrace();
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception1) { }
          goto _L1
        IOException ioexception6;
        ioexception6;
        IOException ioexception3 = ioexception6;
_L4:
        ioexception3.printStackTrace();
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception4) { }
          goto _L1
        Exception exception;
        exception;
_L3:
        if(bufferedreader != null)
            try
            {
                bufferedreader.close();
            }
            catch(IOException ioexception) { }
        throw exception;
        exception;
        bufferedreader = bufferedreader1;
        if(true) goto _L3; else goto _L2
_L2:
        IOException ioexception2;
        ioexception2;
        ioexception3 = ioexception2;
        bufferedreader = bufferedreader1;
          goto _L4
        FileNotFoundException filenotfoundexception;
        filenotfoundexception;
        filenotfoundexception1 = filenotfoundexception;
        bufferedreader = bufferedreader1;
          goto _L5
    }

    private String getTypedText()
    {
        return mDefaultKeySsb.toString();
    }

    private void handleFolderClick(FolderInfo folderinfo)
    {
        if(folderinfo.opened) goto _L2; else goto _L1
_L1:
        closeFolder();
        openFolder(folderinfo);
_L4:
        return;
_L2:
        Folder folder = mWorkspace.getFolderForTag(folderinfo);
        if(folder != null)
        {
            int i = mWorkspace.getScreenForView(folder);
            closeFolder(folder);
            if(i != mWorkspace.getCurrentScreen())
            {
                closeFolder();
                openFolder(folderinfo);
            }
        } else
        {
            closeFolder();
            openFolder(folderinfo);
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void launchMtpApp(Context context)
    {
        ContentResolver contentresolver = context.getContentResolver();
        if(android.provider.Settings.System.getInt(contentresolver, "mtp_usb_connection_status", 0) != 0 && android.provider.Settings.System.getInt(contentresolver, "media_player_mode", 0) != 2) goto _L2; else goto _L1
_L1:
        Log.e("Launcher", "MTP-LAUNCHER: media scanning not yet finished. ");
_L4:
        return;
_L2:
        if(android.provider.Settings.System.getInt(contentresolver, "mtp_usb_conditions_met", 0) != 1)
        {
            boolean flag;
            boolean flag1;
            boolean flag2;
            if(android.provider.Settings.System.getInt(contentresolver, "mtp_usb_connection_status", 0) == 1)
                flag = true;
            else
                flag = false;
            if(android.provider.Settings.System.getInt(contentresolver, "media_player_mode", 0) == 1 || android.provider.Settings.System.getInt(contentresolver, "media_player_mode", 0) == 2)
                flag1 = true;
            else
                flag1 = false;
            if((android.provider.Settings.Secure.getInt(contentresolver, "usb_setting_mode", 0) == 1 || android.provider.Settings.Secure.getInt(contentresolver, "usb_setting_mode", 0) == 0) && android.provider.Settings.Secure.getInt(contentresolver, "adb_enabled", 0) == 0)
                flag2 = true;
            else
                flag2 = false;
            if(flag1 || flag2 && flag)
            {
                Intent intent = new Intent();
                intent.setClassName("com.android.MtpApplication", "com.android.MtpApplication.MtpApplication");
                startActivity(intent);
            }
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    private void makeBitmapMenuWallpaper()
    {
        if(mWallpaperImageDrawable != null)
        {
            Bitmap bitmap = mMenuWallpaperBitmap;
            Canvas canvas = new Canvas(bitmap);
            mWallpaperImageDrawable.setBounds(0, 0, mMenuWallpaperBitmap.getWidth(), mMenuWallpaperBitmap.getHeight());
            mWallpaperImageDrawable.draw(canvas);
            canvas.drawARGB(153, 0, 0, 0);
            mDimWallpaperImageDrawable = new WallpaperFastBitmapDrawable(bitmap);
        }
    }

    private void onAppWidgetReset()
    {
        if(!mDestroyed)
            mAppWidgetHost.startListening();
        else
            Log.w("Launcher", "onAppWidgetReset() : DISCARD widget reset. Launcher destroyed");
    }

    private void onBadgeChanged()
    {
        Cursor cursor1 = getContentResolver().query(Uri.parse("content://com.sec.badge/apps"), null, null, null, null);
        Cursor cursor = cursor1;
_L1:
        Exception exception;
        IllegalStateException illegalstateexception;
        SQLiteException sqliteexception;
        if(cursor != null)
        {
            ArrayList arraylist = new ArrayList();
            if(cursor.moveToFirst())
                do
                {
                    String s = cursor.getString(1);
                    String s1 = cursor.getString(2);
                    int i = cursor.getInt(3);
                    byte abyte0[] = cursor.getBlob(4);
                    Bitmap bitmap;
                    Intent intent;
                    if(abyte0 != null && abyte0.length > 0)
                        bitmap = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
                    else
                        bitmap = null;
                    intent = new Intent();
                    intent.setClassName(s, s1);
                    mBadgeCache.setBadgeCount(intent.getComponent(), i, bitmap);
                    arraylist.add(intent.getComponent());
                } while(cursor.moveToNext());
            cursor.close();
            mIsChangedBadge = false;
            mMenuManager.updateDrawingCacheForApplicationBadgeCountChange(arraylist);
            arraylist.clear();
            mAppShortcutZone.updateBadgeCount(arraylist);
            updateWorkspaceBadge();
            updateTopfourBadge();
        }
        return;
        sqliteexception;
        Log.e("Launcher", (new StringBuilder()).append("onBadgeChanged() ").append(sqliteexception).toString());
        cursor = null;
          goto _L1
        illegalstateexception;
        Log.e("Launcher", (new StringBuilder()).append("onBadgeChanged() ").append(illegalstateexception).toString());
        cursor = null;
          goto _L1
        exception;
        throw exception;
    }

    private void onShareAppRequested()
    {
        Intent intent = new Intent();
        intent.setClassName("com.sec.android.app.shareapp", "com.sec.android.app.shareapp.ShareApp");
        startActivity(intent);
    }

    private void openFolder(FolderInfo folderinfo)
    {
        if(!(folderinfo instanceof UserFolderInfo)) goto _L2; else goto _L1
_L1:
        Object obj = UserFolder.fromXml(this);
_L6:
        ((Folder) (obj)).setDragger(mDragLayer);
        ((Folder) (obj)).setLauncher(this);
        ((Folder) (obj)).bind(folderinfo);
        folderinfo.opened = true;
        CellLayout celllayout = (CellLayout)mWorkspace.getChildAt(((ItemInfo) (folderinfo)).screen);
        mWorkspace.addInScreen(((View) (obj)), ((ItemInfo) (folderinfo)).screen, 0, 0, celllayout.getShortAxisCells(), celllayout.getLongAxisCells());
        ((Folder) (obj)).onOpen();
_L4:
        return;
_L2:
        if(!(folderinfo instanceof LiveFolderInfo)) goto _L4; else goto _L3
_L3:
        obj = LiveFolder.fromXml(this, folderinfo);
        if(true) goto _L6; else goto _L5
_L5:
    }

    private void pickShortcut(int i, int j)
    {
        Bundle bundle = new Bundle();
        ArrayList arraylist = new ArrayList();
        arraylist.add(getString(0x7f0a0013));
        bundle.putStringArrayList("android.intent.extra.shortcut.NAME", arraylist);
        ArrayList arraylist1 = new ArrayList();
        arraylist1.add(android.content.Intent.ShortcutIconResource.fromContext(this, 0x7f020071));
        bundle.putParcelableArrayList("android.intent.extra.shortcut.ICON_RESOURCE", arraylist1);
        Intent intent = new Intent("android.intent.action.PICK_ACTIVITY");
        intent.putExtra("android.intent.extra.INTENT", new Intent("android.intent.action.CREATE_SHORTCUT"));
        intent.putExtra("android.intent.extra.TITLE", getText(j));
        intent.putExtras(bundle);
        startActivityForResult(intent, i);
    }

    private void prepareWidgetPreview(int i, int j)
    {
        mMenuManager.lock();
        mWaitingForResult = false;
        int k;
        if(i == 2)
        {
            previewAppWidget();
        } else
        {
label0:
            {
                if(i != 3)
                    break label0;
                previewAddSamsungWidget();
            }
        }
        mWorkspace.initAddWidget();
        mWidgetPreview.setVisibility(0);
        mDragLayer.invalidate();
        k = -1;
        if(mBlankScreen[0] != -1)
            k = mBlankScreen[0];
        else
        if(mBlankScreen[1] != -1)
            k = mBlankScreen[1];
        mWorkspace.setAtuoScrollScreen(k);
        return;
        cancelAddWidget();
        if(false)
            ;
        else
            break MISSING_BLOCK_LABEL_72;
    }

    private void previewAppWidget()
    {
        if(mWidgetId == -1 || mAppWidgetInfo == null)
        {
            cancelAddWidget();
        } else
        {
            int i = mWidgetId;
            int ai[] = mSpans;
            AppWidgetProviderInfo appwidgetproviderinfo = mAppWidgetInfo;
            LauncherAppWidgetInfo launcherappwidgetinfo = new LauncherAppWidgetInfo(i);
            launcherappwidgetinfo.spanX = ai[0];
            launcherappwidgetinfo.spanY = ai[1];
            launcherappwidgetinfo.hostView = mAppWidgetHost.createView(this, i, appwidgetproviderinfo);
            launcherappwidgetinfo.hostView.setAppWidget(i, appwidgetproviderinfo);
            launcherappwidgetinfo.hostView.setTag(launcherappwidgetinfo);
            int j = getResources().getDimensionPixelSize(0x7f090003);
            int k = getResources().getDimensionPixelSize(0x7f090004);
            mWidgetPreview.addView(launcherappwidgetinfo.hostView, new android.view.ViewGroup.LayoutParams(j * ((ItemInfo) (launcherappwidgetinfo)).spanX, k * ((ItemInfo) (launcherappwidgetinfo)).spanY));
            mWidgetPreview.setVisibility(0);
            mLauncherAppWidgetInfo = launcherappwidgetinfo;
        }
    }

    private static void readConfiguration(Context context, LocaleConfiguration localeconfiguration)
    {
        DataInputStream datainputstream = null;
        DataInputStream datainputstream1 = new DataInputStream(context.openFileInput("launcher.preferences"));
        localeconfiguration.locale = datainputstream1.readUTF();
        localeconfiguration.mcc = datainputstream1.readInt();
        localeconfiguration.mnc = datainputstream1.readInt();
        if(datainputstream1 == null)
            break MISSING_BLOCK_LABEL_49;
        datainputstream1.close();
_L1:
        return;
        FileNotFoundException filenotfoundexception1;
        filenotfoundexception1;
_L4:
        if(datainputstream != null)
            try
            {
                datainputstream.close();
            }
            catch(IOException ioexception) { }
          goto _L1
        IOException ioexception5;
        ioexception5;
_L3:
        if(datainputstream != null)
            try
            {
                datainputstream.close();
            }
            catch(IOException ioexception2) { }
          goto _L1
        Exception exception1;
        exception1;
        Exception exception;
        datainputstream1 = null;
        exception = exception1;
_L2:
        IOException ioexception4;
        if(datainputstream1 != null)
            try
            {
                datainputstream1.close();
            }
            catch(IOException ioexception3) { }
        throw exception;
        ioexception4;
          goto _L1
        exception;
          goto _L2
        IOException ioexception1;
        ioexception1;
        datainputstream = datainputstream1;
          goto _L3
        FileNotFoundException filenotfoundexception;
        filenotfoundexception;
        datainputstream = datainputstream1;
          goto _L4
    }

    private void registerContentObservers()
    {
        ContentResolver contentresolver = getContentResolver();
        contentresolver.registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI, true, mWidgetObserver);
        contentresolver.registerContentObserver(Uri.parse("content://com.sec.badge/apps"), true, mBadgeObserver);
    }

    private void registerIntentReceivers()
    {
        IntentFilter intentfilter = new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        registerReceiver(mCloseSystemDialogsReceiver, intentfilter);
        IntentFilter intentfilter1 = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        registerReceiver(mAirplaneModeChangeReceiver, intentfilter1);
        IntentFilter intentfilter2 = new IntentFilter();
        intentfilter2.addAction("android.intent.action.SCREEN_OFF");
        intentfilter2.addAction("android.intent.action.USER_PRESENT");
        intentfilter2.addAction("android.intent.action.MULTI_CSC_CLEAR");
        registerReceiver(mReceiver, intentfilter2);
        IntentFilter intentfilter3 = new IntentFilter();
        intentfilter3.addAction("android.intent.action.WALLPAPER_CHANGED");
        registerReceiver(mWallpaperChangedReceiver, intentfilter3);
    }

    private void restoreState(Bundle bundle)
    {
        if(bundle != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        mUninstallPackageName = bundle.getString("launcher.delete_application");
        int i = bundle.getInt("launcher.current_screen", -1);
        if(i > -1)
            mWorkspace.setCurrentScreen(i);
        int j = bundle.getInt("launcher.menu_mode", 0);
        if(j == 2)
            mMenuManager.setMode(j);
        int k = bundle.getInt("launcher.menu_current_screen", -1);
        if(k > -1)
            mMenuManager.setCurrentScreen(k);
        int l = bundle.getInt("launcher.add_screen", -1);
        if(l > -1)
        {
            mAddItemCellInfo = new CellLayout.CellInfo();
            CellLayout.CellInfo cellinfo = mAddItemCellInfo;
            cellinfo.valid = true;
            cellinfo.screen = l;
            cellinfo.cellX = bundle.getInt("launcher.add_cellX");
            cellinfo.cellY = bundle.getInt("launcher.add_cellY");
            cellinfo.spanX = bundle.getInt("launcher.add_spanX");
            cellinfo.spanY = bundle.getInt("launcher.add_spanY");
            cellinfo.findVacantCellsFromOccupied(bundle.getBooleanArray("launcher.add_occupied_cells"), bundle.getInt("launcher.add_countX"), bundle.getInt("launcher.add_countY"));
            mRestoring = true;
        }
        if(bundle.getBoolean("launcher.rename_folder", false))
        {
            long l1 = bundle.getLong("launcher.rename_folder_id");
            mFolderInfo = mModel.getFolderById(this, mFolders, l1);
            mRestoring = true;
        }
        if(bundle.getBoolean("launcher.all_apps_folder", false))
            mMenuManager.open();
        mStateQuickNavigation = bundle.getInt("launcher.quick_navigation", -1);
        if(mStateQuickNavigation != 1)
            break; /* Loop/switch isn't completed */
        if(!mMainMenuLoading)
        {
            Log.d("Launcher", "MainMenu loading is already finished. opening QuickViewMainMenu in restoreState()");
            openQuickViewMainMenu();
            mStateQuickNavigation = -1;
        }
_L5:
        mMenuScreenCount = bundle.getInt("launcher.menu_screen_count", -1);
        mDeleteIndex = bundle.getInt("launcher.delete_index", -1);
        if(mDeleteIndex >= 0)
            mIsDeletePopup = true;
        if(true) goto _L1; else goto _L3
_L3:
        if(mStateQuickNavigation != 0) goto _L5; else goto _L4
_L4:
        Log.d("Launcher", "opening QuickViewWorkspace in restoreState()");
        openQuickViewWorkspace();
        mStateQuickNavigation = -1;
          goto _L5
    }

    static void setScreen(int i)
    {
        Object obj = sLock;
        obj;
        JVM INSTR monitorenter ;
        sScreen = i;
        return;
    }

    private void setSomethingsInDefaultIMEI()
    {
        String s = SystemProperties.get("ro.build.PDA", "Not Available");
        int i = android.provider.Settings.System.getInt(getContentResolver(), "uartapcpmode", 0);
        int j = android.provider.Settings.System.getInt(getContentResolver(), "usbapcpmode", 0);
        android.provider.Settings.System.getInt(getContentResolver(), "PbaTestPass", 0);
        android.provider.Settings.System.getInt(getContentResolver(), "SmdTestPass", 0);
        String s1 = getBatteryLevel();
        String s2 = getTSPfirmware();
        mIsDefaultIMEI = true;
        mIsSmdP = "F";
        mIsPbaP = "F";
        mHwVer = (new StringBuilder()).append("HW: ").append(SystemProperties.get("ril.hw_ver", "Unknown")).toString();
        mCalDate = (new StringBuilder()).append("RF Cal Date: ").append(SystemProperties.get("ril.rfcal_date", "Unknown")).toString();
        mBootVer = (new StringBuilder()).append("BOOT: ").append(s).toString();
        mPdaVer = (new StringBuilder()).append("PDA: ").append(SystemProperties.get("ro.build.PDA", "Not Available")).toString();
        mPhoneVer = (new StringBuilder()).append("Phone: ").append(SystemProperties.get("ril.sw_ver", "Not Available")).toString();
        mBattVer = (new StringBuilder()).append("Batt Level: ").append(s1).toString();
        String s3;
        String s4;
        if(i == 0)
            s3 = "UART: MODEM";
        else
            s3 = "UART: PDA";
        mUART = s3;
        if(j == 0)
            s4 = "USB: MODEM";
        else
            s4 = "USB: PDA";
        mUSB = s4;
        mIsSmdP = SystemProperties.get("ril.smd", "F");
        mIsPbaP = SystemProperties.get("ril.pba", "F");
        mBand = (new StringBuilder()).append("Band: ").append(ratMode[Integer.parseInt(SystemProperties.get("persist.radio.networktype", "0"))]).toString();
        mTSP = (new StringBuilder()).append("TSP : 0x").append(s2).toString();
        mSmd_Pba = (new StringBuilder()).append("SMD : 01").append(mIsSmdP).append(", PBA : 04").append(mIsPbaP).toString();
        mCscVer = (new StringBuilder()).append("CSC Version : ").append(SystemProperties.get("ril.official_cscver", "Not Available")).toString();
    }

    private void setWallpaperDimension()
    {
        Log.d("Launcher", "setWallpaperDimension() called");
        WallpaperManager wallpapermanager = (WallpaperManager)getSystemService("wallpaper");
        Display display = getWindowManager().getDefaultDisplay();
        boolean flag;
        int i;
        int j;
        if(display.getWidth() < display.getHeight())
            flag = true;
        else
            flag = false;
        if(flag)
            i = display.getWidth();
        else
            i = display.getHeight();
        if(flag)
            j = display.getHeight();
        else
            j = display.getWidth();
        Log.d("Launcher", "USE_NON_SCROLLABLE_IMAGE_WALLPAPER is true");
        if(wallpapermanager.getWallpaperInfo() == null)
        {
            Log.d("Launcher", "It's image wallpaper. suggestDesiredDimensions(-1,-1)");
            try
            {
                wallpapermanager.suggestDesiredDimensions(-1, -1);
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        } else
        {
            Log.d("Launcher", "It's live wallpaper. suggestDesiredDimensions() with SPAN 2");
            wallpapermanager.suggestDesiredDimensions(i * 2, j);
        }
        if(mMenuWallpaperBitmap == null && mForce16BitWindow)
            mMenuWallpaperBitmap = Bitmap.createBitmap(i * 2, j, android.graphics.Bitmap.Config.RGB_565);
    }

    private void setupViews()
    {
        mDragLayer = (DragLayer)findViewById(0x7f060013);
        DragLayer draglayer = mDragLayer;
        mWorkspace = (Workspace)draglayer.findViewById(0x7f060014);
        Workspace workspace = mWorkspace;
        if(USE_MAINMENU_ICONMODE)
            draglayer.setWorkspace(workspace);
        mMenuDrawer = (MenuDrawer)draglayer.findViewById(0x7f060015);
        MenuDrawer menudrawer = mMenuDrawer;
        mMenuManager = (MenuManager)draglayer.findViewById(0x7f060016);
        MenuManager menumanager = mMenuManager;
        if(USE_MAINMENU_ICONMODE || SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
            draglayer.setMenuManager(menumanager);
        mDeleteZone = (DeleteZone)draglayer.findViewById(0x7f060018);
        mAppShortcutZone = (AppShortcutZone)draglayer.findViewById(0x7f060017);
        AppShortcutZone appshortcutzone = mAppShortcutZone;
        appshortcutzone.setLauncher(this);
        appshortcutzone.setDragger(draglayer);
        mTopFourZone = (TopFourZone)draglayer.findViewById(0x7f06001e);
        if(mTopFourZone != null)
            mTopFourZone.setLauncher(this);
        menumanager.setDragger(draglayer);
        if(loadMenuMode() == 2)
        {
            Log.i("Launcher", "setupViews loadMenuMode() == MenuManager.EDIT_MODE");
            saveMenuMode(0);
        }
        menumanager.setLauncher(this);
        menumanager.setMode(loadMenuMode());
        int i = mPrefs.getInt("screencount", DEFAULT_SCREEN_COUNT);
        for(int j = 0; j < i; j++)
        {
            CellLayout celllayout = (CellLayout)LayoutInflater.from(this).inflate(0x7f030016, workspace, false);
            celllayout.setId(getCellLayoutId(j));
            android.view.ViewGroup.LayoutParams layoutparams = celllayout.getLayoutParams();
            if(layoutparams == null)
                layoutparams = new android.view.ViewGroup.LayoutParams(-1, -1);
            workspace.addView(celllayout, layoutparams);
        }

        int k = mPrefs.getInt("currentscreen", -1);
        if(k == -1 || k >= i)
            k = 0;
        sScreen = k;
        workspace.setCurrentScreen(k);
        workspace.setOnLongClickListener(this);
        workspace.setDragger(draglayer);
        workspace.setLauncher(this);
        mDeleteZone.setLauncher(this);
        mDeleteZone.setDragController(draglayer);
        mDeleteZone.setHandle(mAppShortcutZone);
        draglayer.setIgnoredDropTarget(menudrawer);
        draglayer.setDragScoller(workspace);
        draglayer.setDragListener(mDeleteZone);
        draglayer.setDragMenuScoller(menumanager);
        mQuickViewWorkspace = (QuickViewWorkspace)draglayer.findViewById(0x7f06001c);
        mQuickViewWorkspace.setLauncher(this);
        mQuickViewMainMenu = (QuickViewMainMenu)draglayer.findViewById(0x7f06001d);
        mQuickViewMainMenu.setLauncher(this);
        mWidgetPreview = (WidgetPreview)draglayer.findViewById(0x7f06001a);
        if(!SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
            checkHwNaviKey();
        if(SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
        {
            blockChildrenFocus();
            mHiddenFocus = true;
        }
    }

    private void showAddDialog(CellLayout.CellInfo cellinfo)
    {
        mAddItemCellInfo = cellinfo;
        mWaitingForResult = true;
        showDialog(1);
    }

    private void showNotifications()
    {
        StatusBarManager statusbarmanager = (StatusBarManager)getSystemService("statusbar");
        if(statusbarmanager != null)
            statusbarmanager.expand();
    }

    private void startWallpaper()
    {
        Intent intent = Intent.createChooser(new Intent("android.intent.action.SET_WALLPAPER"), getText(0x7f0a0003));
        WallpaperInfo wallpaperinfo = ((WallpaperManager)getSystemService("wallpaper")).getWallpaperInfo();
        if(wallpaperinfo != null && wallpaperinfo.getSettingsActivity() != null)
        {
            LabeledIntent labeledintent = new LabeledIntent(getPackageName(), 0x7f0a0007, 0);
            labeledintent.setClassName(wallpaperinfo.getPackageName(), wallpaperinfo.getSettingsActivity());
            Intent aintent[] = new Intent[1];
            aintent[0] = labeledintent;
            intent.putExtra("android.intent.extra.INITIAL_INTENTS", aintent);
        }
        startActivityForResult(intent, 10);
    }

    private void unbindDesktopItems()
    {
        for(Iterator iterator = mDesktopItems.iterator(); iterator.hasNext(); ((ItemInfo)iterator.next()).unbind());
    }

    private static void writeConfiguration(Context context, LocaleConfiguration localeconfiguration)
    {
        DataOutputStream dataoutputstream = null;
        DataOutputStream dataoutputstream1 = new DataOutputStream(context.openFileOutput("launcher.preferences", 0));
        dataoutputstream1.writeUTF(localeconfiguration.locale);
        dataoutputstream1.writeInt(localeconfiguration.mcc);
        dataoutputstream1.writeInt(localeconfiguration.mnc);
        dataoutputstream1.flush();
        if(dataoutputstream1 == null)
            break MISSING_BLOCK_LABEL_54;
        dataoutputstream1.close();
_L1:
        return;
        FileNotFoundException filenotfoundexception1;
        filenotfoundexception1;
_L4:
        if(dataoutputstream != null)
            try
            {
                dataoutputstream.close();
            }
            catch(IOException ioexception) { }
          goto _L1
        IOException ioexception5;
        ioexception5;
_L3:
        context.getFileStreamPath("launcher.preferences").delete();
        if(dataoutputstream != null)
            try
            {
                dataoutputstream.close();
            }
            catch(IOException ioexception3) { }
          goto _L1
        Exception exception2;
        exception2;
        Exception exception1;
        dataoutputstream1 = null;
        exception1 = exception2;
_L2:
        IOException ioexception4;
        if(dataoutputstream1 != null)
            try
            {
                dataoutputstream1.close();
            }
            catch(IOException ioexception2) { }
        throw exception1;
        ioexception4;
          goto _L1
        exception1;
          goto _L2
        Exception exception;
        exception;
        dataoutputstream1 = dataoutputstream;
        exception1 = exception;
          goto _L2
        IOException ioexception1;
        ioexception1;
        dataoutputstream = dataoutputstream1;
          goto _L3
        FileNotFoundException filenotfoundexception;
        filenotfoundexception;
        dataoutputstream = dataoutputstream1;
          goto _L4
    }

    void addAppWidget(Intent intent)
    {
        int i;
        String s;
        i = intent.getIntExtra("appWidgetId", -1);
        s = intent.getStringExtra("custom_widget");
        if(s == null) goto _L2; else goto _L1
_L1:
        Iterator iterator = mSamsungWidgetPackageManager.getWidgetItems().iterator();
_L5:
        if(!iterator.hasNext()) goto _L2; else goto _L3
_L3:
        SamsungAppWidgetItem samsungappwidgetitem = (SamsungAppWidgetItem)iterator.next();
        if(!s.equals((new StringBuilder()).append(samsungappwidgetitem.mPackageName).append(samsungappwidgetitem.mClassName).toString())) goto _L5; else goto _L4
_L4:
        mAppWidgetHost.deleteAppWidgetId(i);
        addSamsungWidget(samsungappwidgetitem);
_L7:
        return;
_L2:
        AppWidgetProviderInfo appwidgetproviderinfo = mAppWidgetManager.getAppWidgetInfo(i);
        if(appwidgetproviderinfo.configure != null)
        {
            Intent intent1 = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent1.setComponent(appwidgetproviderinfo.configure);
            intent1.putExtra("appWidgetId", i);
            startActivityForResult(intent1, 5);
        } else
        {
            onActivityResult(5, -1, intent);
        }
        if(true) goto _L7; else goto _L6
_L6:
    }

    void addFolder()
    {
        UserFolderInfo userfolderinfo;
        CellLayout.CellInfo cellinfo;
        int i;
        userfolderinfo = new UserFolderInfo();
        userfolderinfo.title = getText(0x7f0a0002);
        cellinfo = mAddItemCellInfo;
        cellinfo.screen = mWorkspace.getCurrentScreen();
        i = findPreferredSingleSlotOrAnySlot(cellinfo);
        if(i != -1) goto _L2; else goto _L1
_L1:
        return;
_L2:
        boolean flag;
        FolderIcon foldericon;
        if(i != mWorkspace.getCurrentScreen())
        {
            mWorkspace.setCurrentScreen(i);
            flag = true;
        } else
        {
            flag = false;
        }
        LauncherModel.addItemToDatabase(this, userfolderinfo, -100L, i, cellinfo.cellX, cellinfo.cellY, false);
        mFolders.put(Long.valueOf(((ItemInfo) (userfolderinfo)).id), userfolderinfo);
        foldericon = FolderIcon.fromXml(0x7f03000a, this, (ViewGroup)mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), userfolderinfo);
        mWorkspace.addInScreen(foldericon, i, cellinfo.cellX, cellinfo.cellY, 1, 1, isWorkspaceLocked());
        if(flag)
        {
            mDragLayer.invalidate();
            mWorkspace.setFastAtuoScrollScreen(i);
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    void addLiveFolder(Intent intent)
    {
        String s = getResources().getString(0x7f0a0016);
        String s1 = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        if(s != null && s.equals(s1))
            addFolder();
        else
            startActivityForResult(intent, 4);
    }

    void addSamsungWidget(SamsungAppWidgetInfo samsungappwidgetinfo, int i)
    {
        if(samsungappwidgetinfo != null) goto _L2; else goto _L1
_L1:
        Log.w("Launcher", "addSamsungWidget(..) : SamsungWidget is a null");
        cancelAddWidget();
_L4:
        return;
_L2:
        int ai[] = mCellCoordinates;
        int j = ((ItemInfo) (samsungappwidgetinfo)).spanX;
        int k = ((ItemInfo) (samsungappwidgetinfo)).spanY;
        mDesktopItems.add(samsungappwidgetinfo);
        LauncherModel.addItemToDatabase(this, samsungappwidgetinfo, -100L, i, ai[0], ai[1], false);
        SamsungAppWidgetView samsungappwidgetview = samsungappwidgetinfo.widgetView;
        samsungappwidgetview.setTag(samsungappwidgetinfo);
        mWorkspace.addInScreen(samsungappwidgetview, i, ai[0], ai[1], j, k);
        if(mWorkspace.getCurrentScreen() == i)
            mWorkspace.resumeScreen(i);
        if(true) goto _L4; else goto _L3
_L3:
    }

    void addSamsungWidget(SamsungAppWidgetItem samsungappwidgetitem)
    {
        if(samsungappwidgetitem != null) goto _L2; else goto _L1
_L1:
        return;
_L2:
        SamsungAppWidgetInfo samsungappwidgetinfo = mSamsungWidgetPackageManager.createWidget(this, samsungappwidgetitem);
        if(samsungappwidgetinfo == null)
        {
            Log.w("Launcher", "addSamsungWidget() : SamsungWidget is a null");
        } else
        {
            if(mAddItemCellInfo == null || mAddItemCellInfo.screen != mWorkspace.getCurrentScreen())
                mAddItemCellInfo = mWorkspace.findAllVacantCells(null);
            CellLayout.CellInfo cellinfo = mAddItemCellInfo;
            if(cellinfo == null)
            {
                Log.w("Launcher", "addSamsungWidget() : cellInfo is a null");
                cancelAddWidget();
                if(mToast == null)
                    mToast = Toast.makeText(this, getString(0x7f0a0039), 0);
                else
                    mToast.setText(getString(0x7f0a0039));
                mToast.show();
            } else
            {
                CellLayout celllayout = (CellLayout)mWorkspace.getChildAt(cellinfo.screen);
                if(celllayout == null)
                {
                    Log.w("Launcher", (new StringBuilder()).append("addSamsungWidget() : CellLayout is a null(").append(cellinfo.screen).append(")").toString());
                    cancelAddWidget();
                    if(mToast == null)
                        mToast = Toast.makeText(this, getString(0x7f0a0039), 0);
                    else
                        mToast.setText(getString(0x7f0a0039));
                    mToast.show();
                } else
                {
                    int ai[] = celllayout.rectToCell2(samsungappwidgetitem.getWidth(mResOrientation), samsungappwidgetitem.getHeight(mResOrientation));
                    int ai1[] = mCellCoordinates;
                    samsungappwidgetinfo.spanX = ai[0];
                    samsungappwidgetinfo.spanY = ai[1];
                    if(!findSlot(cellinfo, ai1, ai[0], ai[1]))
                    {
                        mSamsungWidgetInfo = samsungappwidgetinfo;
                        mSpans[0] = ai[0];
                        mSpans[1] = ai[1];
                        mAddWidgetType = 3;
                        if(isAllPageSlot(mWorkspace.getCurrentScreen()))
                        {
                            cancelAddWidget();
                        } else
                        {
                            int i = -1;
                            int j = mWorkspace.getChildCount();
                            if(j < SCREEN_COUNT)
                            {
                                createBlankPage();
                                i = j;
                            }
                            prepareWidgetPreview(3, i);
                        }
                    } else
                    {
                        mDesktopItems.add(samsungappwidgetinfo);
                        LauncherModel.addItemToDatabase(this, samsungappwidgetinfo, -100L, mWorkspace.getCurrentScreen(), ai1[0], ai1[1], false);
                        SamsungAppWidgetView samsungappwidgetview = samsungappwidgetinfo.widgetView;
                        samsungappwidgetview.setTag(samsungappwidgetinfo);
                        mWorkspace.addInCurrentScreen(samsungappwidgetview, ai1[0], ai1[1], ai[0], ai[1]);
                        if(mIsActive)
                            mSamsungWidgetPackageManager.resumeWidget(this, samsungappwidgetinfo);
                    }
                }
            }
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    public void addShortcut(ShortcutInfo shortcutinfo)
    {
        mDesktopItems.add(shortcutinfo);
    }

    public void bindAllApplications(ArrayList arraylist)
    {
        mMenuManager.setApps(arraylist);
        mMainMenuLoading = false;
        Log.d("Launcher", "Main menu binding finished");
        if(mStateQuickNavigation == 1)
        {
            Log.d("Launcher", "opening QuickViewMainMenu in bindAllApplications()");
            openQuickViewMainMenu();
            mStateQuickNavigation = -1;
        }
    }

    public void bindAppWidget(final LauncherAppWidgetInfo infoItem)
    {
        long l = SystemClock.uptimeMillis();
        Log.d("Launcher", (new StringBuilder()).append("bindAppWidget: ").append(infoItem).toString());
        final Workspace workspace = mWorkspace;
        int i = infoItem.appWidgetId;
        AppWidgetProviderInfo appwidgetproviderinfo = mAppWidgetManager.getAppWidgetInfo(i);
        if(appwidgetproviderinfo == null)
        {
            Log.d("Launcher", (new StringBuilder()).append("bindAppWidget(). appWidget id:").append(i).append(" is not available").toString());
            if(mConfigChange && infoItem.hostView != null)
                workspace.removeInScreen(infoItem.hostView, ((ItemInfo) (infoItem)).screen);
        } else
        {
            Log.d("Launcher", (new StringBuilder()).append("bindAppWidget: id=").append(infoItem.appWidgetId).append(" belongs to component ").append(appwidgetproviderinfo.provider).toString());
            if(mConfigChange)
            {
                AppWidgetManager appwidgetmanager = mAppWidgetManager;
                Log.d("Launcher", (new StringBuilder()).append("bindAppWidget start no destroy hostView:").append(infoItem.hostView).append("  screen:").append(((ItemInfo) (infoItem)).screen).toString());
                if(infoItem.hostView != null)
                    workspace.removeInScreen(infoItem.hostView, ((ItemInfo) (infoItem)).screen);
                Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
                int ai[] = new int[1];
                ai[0] = i;
                intent.putExtra("appWidgetIds", ai);
                intent.setComponent(appwidgetmanager.getAppWidgetInfo(i).provider);
                appwidgetmanager.updateAppWidget(i, null);
                sendBroadcast(intent);
                if(infoItem.hostView == null)
                {
                    infoItem.hostView = mAppWidgetHost.createView(this, i, appwidgetproviderinfo);
                    infoItem.hostView.setAppWidget(i, appwidgetproviderinfo);
                    infoItem.hostView.setTag(infoItem);
                    if(mDesktopItems.indexOf(infoItem) == -1)
                        mDesktopItems.add(infoItem);
                }
                mHandler.postDelayed(new Runnable() {

                    public void run()
                    {
                        workspace.addInScreen(infoItem.hostView, ((ItemInfo) (infoItem)).screen, ((ItemInfo) (infoItem)).cellX, ((ItemInfo) (infoItem)).cellY, ((ItemInfo) (infoItem)).spanX, ((ItemInfo) (infoItem)).spanY, false);
                    }

                    final Launcher this$0;
                    final LauncherAppWidgetInfo val$infoItem;
                    final Workspace val$workspace;

            
            {
                this$0 = Launcher.this;
                workspace = workspace1;
                infoItem = launcherappwidgetinfo;
                super();
            }
                }
, 0L);
                Log.d("Launcher", (new StringBuilder()).append("--> id=").append(i).append("  title:").append(appwidgetproviderinfo.label).toString());
            } else
            {
                infoItem.hostView = mAppWidgetHost.createView(this, i, appwidgetproviderinfo);
                infoItem.hostView.setAppWidget(i, appwidgetproviderinfo);
                infoItem.hostView.setTag(infoItem);
                workspace.addInScreen(infoItem.hostView, ((ItemInfo) (infoItem)).screen, ((ItemInfo) (infoItem)).cellX, ((ItemInfo) (infoItem)).cellY, ((ItemInfo) (infoItem)).spanX, ((ItemInfo) (infoItem)).spanY, false);
                workspace.requestLayout();
                mDesktopItems.add(infoItem);
                Log.d("Launcher", (new StringBuilder()).append("bound widget id=").append(infoItem.appWidgetId).append(" in ").append(SystemClock.uptimeMillis() - l).append("ms").toString());
            }
        }
    }

    public void bindAppsAdded(ArrayList arraylist)
    {
        if(!mDestroyed)
        {
            removeDialog(1);
            if(arraylist != null)
            {
                int i = arraylist.size();
                for(int j = 0; j < i; j++)
                    LauncherModel.addAppToDatabase(this, (ApplicationInfo)arraylist.get(j));

            }
            mMenuManager.addApps(arraylist);
            mMainMenuLoading = false;
        }
    }

    public void bindAppsRemoved(ArrayList arraylist)
    {
        if(!mDestroyed)
        {
            removeDialog(1);
            if(arraylist != null)
            {
                int i = arraylist.size();
                for(int j = 0; j < i; j++)
                    LauncherModel.removeAppToDatabase(this, (ApplicationInfo)arraylist.get(j));

            }
            if(mTopFourZone != null)
                mTopFourZone.removeItems(arraylist);
            mWorkspace.removeItems(arraylist);
            mMenuManager.removeApps(arraylist);
        }
    }

    public void bindAppsUpdated(ArrayList arraylist)
    {
        if(!mDestroyed)
        {
            removeDialog(1);
            if(mTopFourZone != null)
                mTopFourZone.updateShortcuts(arraylist);
            mWorkspace.updateShortcuts(arraylist);
            mMenuManager.updateApps(arraylist);
        }
    }

    public void bindFolders(HashMap hashmap)
    {
        mFolders.clear();
        mFolders.putAll(hashmap);
    }

    public void bindItems(ArrayList arraylist, int i, int j)
    {
        Workspace workspace;
        int k;
        workspace = mWorkspace;
        k = i;
_L6:
        ItemInfo iteminfo;
        if(k >= j)
            break MISSING_BLOCK_LABEL_247;
        iteminfo = (ItemInfo)arraylist.get(k);
        mDesktopItems.add(iteminfo);
        iteminfo.itemType;
        JVM INSTR tableswitch 0 3: default 72
    //                   0 78
    //                   1 78
    //                   2 145
    //                   3 196;
           goto _L1 _L2 _L2 _L3 _L4
_L4:
        break MISSING_BLOCK_LABEL_196;
_L1:
        break; /* Loop/switch isn't completed */
_L2:
        break; /* Loop/switch isn't completed */
_L7:
        k++;
        if(true) goto _L6; else goto _L5
_L5:
        if(iteminfo.screen == -1)
        {
            if(getTopFourZone() != null)
                getTopFourZone().loadApplication((ShortcutInfo)iteminfo);
        } else
        {
            workspace.addInScreen(createShortcut((ShortcutInfo)iteminfo), iteminfo.screen, iteminfo.cellX, iteminfo.cellY, 1, 1, false);
        }
          goto _L7
_L3:
        workspace.addInScreen(FolderIcon.fromXml(0x7f03000a, this, (ViewGroup)workspace.getChildAt(workspace.getCurrentScreen()), (UserFolderInfo)iteminfo), iteminfo.screen, iteminfo.cellX, iteminfo.cellY, 1, 1, false);
          goto _L7
        workspace.addInScreen(LiveFolderIcon.fromXml(0x7f030010, this, (ViewGroup)workspace.getChildAt(workspace.getCurrentScreen()), (LiveFolderInfo)iteminfo), iteminfo.screen, iteminfo.cellX, iteminfo.cellY, 1, 1, false);
          goto _L7
        workspace.requestLayout();
        return;
    }

    public void bindSamsungAppWidget(SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        Log.d("Launcher", (new StringBuilder()).append("bindSamsungAppWidget: ").append(samsungappwidgetinfo).toString());
        Workspace workspace = mWorkspace;
        SamsungAppWidgetItem samsungappwidgetitem = null;
        Intent intent = samsungappwidgetinfo.intent;
        if(intent != null)
        {
            ComponentName componentname = intent.getComponent();
            samsungappwidgetitem = mSamsungWidgetPackageManager.findWidget(componentname.getPackageName(), componentname.getClassName());
        }
        if(samsungappwidgetitem != null)
        {
            mSamsungWidgetPackageManager.createWidget(this, samsungappwidgetitem, samsungappwidgetinfo);
            SamsungAppWidgetView samsungappwidgetview1 = samsungappwidgetinfo.widgetView;
            samsungappwidgetview1.setTag(samsungappwidgetinfo);
            workspace.addInScreen(samsungappwidgetview1, ((ItemInfo) (samsungappwidgetinfo)).screen, ((ItemInfo) (samsungappwidgetinfo)).cellX, ((ItemInfo) (samsungappwidgetinfo)).cellY, ((ItemInfo) (samsungappwidgetinfo)).spanX, ((ItemInfo) (samsungappwidgetinfo)).spanY, false);
            workspace.requestLayout();
            if(getScreen() == ((ItemInfo) (samsungappwidgetinfo)).screen && mIsActive)
                mSamsungWidgetPackageManager.resumeWidget(this, samsungappwidgetinfo);
        } else
        {
            SamsungAppWidgetView samsungappwidgetview = new SamsungAppWidgetView(this);
            android.widget.FrameLayout.LayoutParams layoutparams = new android.widget.FrameLayout.LayoutParams(-2, -2);
            layoutparams.gravity = 17;
            samsungappwidgetview.addView(samsungappwidgetview.getErrorView(), layoutparams);
            samsungappwidgetview.setTag(samsungappwidgetinfo);
            samsungappwidgetinfo.widgetView = samsungappwidgetview;
            workspace.addInScreen(samsungappwidgetview, ((ItemInfo) (samsungappwidgetinfo)).screen, ((ItemInfo) (samsungappwidgetinfo)).cellX, ((ItemInfo) (samsungappwidgetinfo)).cellY, ((ItemInfo) (samsungappwidgetinfo)).spanX, ((ItemInfo) (samsungappwidgetinfo)).spanY, false);
            workspace.requestLayout();
        }
        mDesktopItems.add(samsungappwidgetinfo);
        if(mQuickViewWorkspace.isOpened())
            mQuickViewWorkspace.invalidate();
        mConfigChange = false;
    }

    void cancelAddWidget()
    {
        int i = mAddWidgetType;
        if(i != 2) goto _L2; else goto _L1
_L1:
        if(mWidgetId != -1)
            mAppWidgetHost.deleteAppWidgetId(mWidgetId);
        mWidgetId = -1;
        mAppWidgetInfo = null;
        mLauncherAppWidgetInfo = null;
_L4:
        mWidgetPreview.removeAllViews();
        mWidgetPreview.setVisibility(4);
        mWorkspace.stopAutoScrollRunnable();
        if(mToast != null)
            mToast.cancel();
        clearAddWidget();
        return;
_L2:
        if(i == 3)
        {
            if(mSamsungWidgetInfo != null)
                getLocalActivityManager().destroyActivity(Integer.toString(mSamsungWidgetInfo.widgetId), true);
            mSamsungWidgetInfo = null;
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    void cancelRemovePage()
    {
        mIsDeletePopup = false;
        mQuickViewWorkspace.cancelDeleteView();
    }

    boolean checkWidgetSpace(int i)
    {
        int j = mAddWidgetType;
        boolean flag;
        if(j == 1 || j == 2 || j == 3)
        {
            CellLayout celllayout = (CellLayout)mWorkspace.getChildAt(i);
            int ai[] = mSpans;
            CellLayout.CellInfo cellinfo = celllayout.findAllVacantCells(null, null);
            int ai1[] = mCellCoordinates;
            if(cellinfo != null && !findSlot(cellinfo, ai1, ai[0], ai[1]))
            {
                flag = false;
            } else
            {
                showWidgetMessage(false);
                flag = true;
            }
        } else
        {
            flag = false;
        }
        return flag;
    }

    void clearAddWidget()
    {
        mAddWidgetType = 0;
        mWidgetId = -1;
        mBlankScreen[0] = -1;
        mBlankScreen[1] = -1;
        mMenuManager.unlock();
    }

    void closeAllApplications()
    {
        mMenuManager.close();
    }

    void closeFolder(Folder folder)
    {
        folder.getInfo().opened = false;
        ViewGroup viewgroup = (ViewGroup)folder.getParent();
        if(viewgroup != null)
            viewgroup.removeView(folder);
        if(SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
            folder.onCloseInNavy();
        else
            folder.onClose();
    }

    void closeQuickViewMainMenu()
    {
        if(mQuickViewMainMenu.isOpened()) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i;
        getWindow().clearFlags(1024);
        mQuickViewMainMenu.close();
        i = mQuickViewMainMenu.getCurrentPage();
        if(i >= 0 && i < mMenuManager.getChildCount())
            break; /* Loop/switch isn't completed */
        mMenuManager.getCurrentScreen();
_L5:
        mMenuManager.setVisibility(0);
        mAppShortcutZone.setVisibility(0);
        if(mTopFourZone != null)
            mTopFourZone.setVisibility(0);
        mMenuManager.getChildAt(mMenuManager.getCurrentScreen()).requestFocus();
        mMenuManager.computeScroll();
        mMenuManager.scrollTo(mMenuManager.getCurrentScreen() * mMenuManager.getWidth(), 0);
        if(true) goto _L1; else goto _L3
_L3:
        if(mMenuManager.getCurrentScreen() == i) goto _L5; else goto _L4
_L4:
        mMenuManager.setCurrentScreen(i);
          goto _L5
    }

    void closeQuickViewWorkspace()
    {
        if(mQuickViewWorkspace.isOpened()) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i;
        getWindow().clearFlags(1024);
        mQuickViewWorkspace.close();
        i = mQuickViewWorkspace.getCurrentPage();
        if(i >= 0 && i < mWorkspace.getChildCount())
            break; /* Loop/switch isn't completed */
        i = mWorkspace.getCurrentScreen();
_L5:
        setScreen(i);
        mWorkspace.updateWallpaperOffset();
        mWorkspace.setVisibility(0);
        mAppShortcutZone.setVisibility(0);
        if(mTopFourZone != null)
            mTopFourZone.setVisibility(0);
        mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
        final int newScreen = i;
        mHandler.postDelayed(new Runnable() {

            public void run()
            {
                mWorkspace.resumeScreen(newScreen);
            }

            final Launcher this$0;
            final int val$newScreen;

            
            {
                this$0 = Launcher.this;
                newScreen = i;
                super();
            }
        }
, 350L);
        if(true) goto _L1; else goto _L3
_L3:
        if(mWorkspace.getCurrentScreen() == i) goto _L5; else goto _L4
_L4:
        mWorkspace.setCurrentScreen(i);
          goto _L5
    }

    void closeSystemDialogs()
    {
        getWindow().closeAllPanels();
        try
        {
            dismissDialog(1);
        }
        catch(Exception exception) { }
        try
        {
            dismissDialog(2);
        }
        catch(Exception exception1) { }
        try
        {
            mUninstallPackageName = null;
            dismissDialog(4);
        }
        catch(Exception exception2) { }
        try
        {
            dismissDialog(5);
        }
        catch(Exception exception3) { }
        try
        {
            dismissDialog(6);
        }
        catch(Exception exception4) { }
        mWaitingForResult = false;
    }

    void completeAddApplication(Context context, Intent intent, CellLayout.CellInfo cellinfo)
    {
        boolean flag;
        int i;
        flag = false;
        cellinfo.screen = mWorkspace.getCurrentScreen();
        i = findPreferredSingleSlotOrAnySlot(cellinfo);
        if(i != -1) goto _L2; else goto _L1
_L1:
        return;
_L2:
        if(i != mWorkspace.getCurrentScreen())
        {
            flag = true;
            mWorkspace.setCurrentScreen(i);
        }
        ShortcutInfo shortcutinfo = mModel.getShortcutInfo(context.getPackageManager(), intent, context);
        if(shortcutinfo != null)
        {
            shortcutinfo.setActivity(intent.getComponent(), 0x10200000);
            shortcutinfo.container = -1L;
            mWorkspace.addApplicationShortcut(shortcutinfo, cellinfo, isWorkspaceLocked());
            if(flag)
            {
                mDragLayer.invalidate();
                mWorkspace.setFastAtuoScrollScreen(i);
            }
        } else
        {
            Log.e("Launcher", (new StringBuilder()).append("Couldn't find ActivityInfo for selected application: ").append(intent).toString());
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    void completeAddWidget(int i)
    {
        int j;
        j = mAddWidgetType;
        if(mCellCoordinates[0] == -1 || mCellCoordinates[1] == -1)
        {
            mCellCoordinates[0] = 0;
            mCellCoordinates[1] = 0;
        }
        mWidgetPreview.removeAllViews();
        if(j != 2) goto _L2; else goto _L1
_L1:
        completePreviewAppWidget();
_L4:
        mWidgetPreview.setVisibility(4);
        clearAddWidget();
        mDragLayer.invalidate();
        return;
_L2:
        if(j == 3)
            addSamsungWidget(mSamsungWidgetInfo, i);
        if(true) goto _L4; else goto _L3
_L3:
    }

    View createShortcut(int i, ViewGroup viewgroup, ShortcutInfo shortcutinfo)
    {
        TextView textview = (TextView)mInflater.inflate(i, viewgroup, false);
        if(SystemProperties.get("ro.product.model", "Not Available").equals("GT-B5510"))
        {
            CellLayout.LayoutParams layoutparams = (CellLayout.LayoutParams)textview.getLayoutParams();
            float f = 1.0F / getResources().getDisplayMetrics().density;
            layoutparams.setMargins(8, 0, 8, 0);
            textview.setTextSize(10F * f);
        }
        textview.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(shortcutinfo.getIcon(mIconCache)), null, null);
        textview.setText(shortcutinfo.title);
        textview.setTag(shortcutinfo);
        textview.setOnClickListener(this);
        return textview;
    }

    View createShortcut(ShortcutInfo shortcutinfo)
    {
        return createShortcut(0x7f030001, (ViewGroup)mWorkspace.getChildAt(mWorkspace.getCurrentScreen()), shortcutinfo);
    }

    public boolean dispatchKeyEvent(KeyEvent keyevent)
    {
        Log.d("Launcher", "dispatchKeyEvent");
        if(mHiddenFocus)
        {
            mDragLayer.setDescendantFocusability(0x20000);
            mDragLayer.requestFocus();
            mHiddenFocus = false;
        }
        if(keyevent.getKeyCode() == 22)
            Workspace.isScrollAble = true;
        if(keyevent.getAction() != 0) goto _L2; else goto _L1
_L1:
        keyevent.getKeyCode();
        JVM INSTR tableswitch 3 4: default 88
    //                   3 177
    //                   4 98;
           goto _L3 _L4 _L5
_L3:
        boolean flag = super.dispatchKeyEvent(keyevent);
_L6:
        return flag;
_L5:
        if(mMenuManager.isOpened())
        {
            MenuManagerBackKeyDownInfo = true;
            if(keyevent.isLongPress())
                MenuManagerBackKeyDownInfo = false;
        }
        Log.d("Launcher", (new StringBuilder()).append("dispatchKeyEvent DOWN KEYCODE_BACK / MainMenu isOpened=").append(mMenuManager.isOpened()).append(" Info=").append(MenuManagerBackKeyDownInfo).toString());
        flag = true;
          goto _L6
_L4:
        if(mMenuManager.getMode() == 2)
        {
            menusave();
            mMenuManager.setMode(0);
            flag = true;
        } else
        {
            if(mMenuManager.isOpened())
                closeDrawer();
            flag = true;
        }
          goto _L6
_L2:
        if(keyevent.getAction() != 1) goto _L3; else goto _L7
_L7:
        if(!isAddWidgetState()) goto _L9; else goto _L8
_L8:
        switch(keyevent.getKeyCode())
        {
        default:
            flag = true;
            break;

        case 4: // '\004'
            cancelAddWidget();
            mDragLayer.invalidate();
            flag = true;
            break;
        }
          goto _L10
_L9:
        keyevent.getKeyCode();
          goto _L11
_L10:
        if(true) goto _L6; else goto _L12
_L12:
_L11:
        JVM INSTR tableswitch 3 4: default 312
    //                   3 315
    //                   4 321;
           goto _L3 _L13 _L14
_L13:
        flag = true;
          goto _L6
_L14:
        Log.d("Launcher", "KEYCODE_BACK launcher 2");
        if(mOptionMenuOpening)
            flag = true;
        else
        if(mMenuManager.getMode() == 2)
        {
            mDragLayer.cancelDrag();
            menusave();
            mMenuManager.setMode(0);
            flag = true;
        } else
        if(mQuickViewWorkspace.isOpened() || mStateQuickNavigation == 0)
        {
            if(mQuickViewWorkspace.isAnimating())
            {
                flag = true;
            } else
            {
                cancelRemovePage();
                mQuickViewWorkspace.cancelDrag();
                mQuickViewWorkspace.drawCloseAnimation();
                mQuickViewWorkspace.invalidate();
                flag = true;
            }
        } else
        if(mQuickViewMainMenu.isOpened() || mStateQuickNavigation == 1)
        {
            mQuickViewMainMenu.cancelDrag();
            mQuickViewMainMenu.drawCloseAnimation();
            mQuickViewMainMenu.invalidate();
            flag = true;
        } else
        {
            if(mMenuManager.isOpened() && MenuManagerBackKeyDownInfo)
            {
                closeDrawer();
                MenuManagerBackKeyDownInfo = false;
            } else
            {
                closeFolder();
            }
            Log.d("Launcher", (new StringBuilder()).append("dispatchKeyEvent UP KEYCODE_BACK / MainMenu isOpened=").append(mMenuManager.isOpened()).append(" Info=").append(MenuManagerBackKeyDownInfo).toString());
            if(!keyevent.isCanceled())
                mWorkspace.dispatchKeyEvent(keyevent);
            flag = true;
        }
          goto _L6
    }

    void findAllOccupiedCells(boolean aflag[][], int i, int j, int k)
    {
        ArrayList arraylist = mDesktopItems;
        if(arraylist != null)
        {
            int l = arraylist.size();
            for(int i1 = 0; i1 < l; i1++)
                addOccupiedCells(aflag, k, (ItemInfo)arraylist.get(i1));

        }
    }

    public int findPreferredSingleSlotOrAnySlot(CellLayout.CellInfo cellinfo)
    {
        int ai[] = new int[2];
        int j;
        if(findSlot(cellinfo, ai, 1, 1, false))
        {
            cellinfo.cellX = ai[0];
            cellinfo.cellY = ai[1];
            j = cellinfo.screen;
        } else
        {
            int i = findSingleSlotAnywhere(cellinfo, true);
            if(i == -1)
                j = -1;
            else
                j = i;
        }
        return j;
    }

    public int findSingleSlotAnywhere(CellLayout.CellInfo cellinfo, boolean flag)
    {
        cellinfo.screen = mWorkspace.getCurrentScreen();
        int ai[] = new int[2];
        ai[0] = cellinfo.cellX;
        ai[1] = cellinfo.cellY;
        int ai1[] = new int[2];
        int i = findSlotAndScreen(ai, ai1, getResources().getDimensionPixelSize(0x7f090003), getResources().getDimensionPixelSize(0x7f090004), cellinfo, flag);
        if(i != -1)
        {
            cellinfo.spanX = ai1[0];
            cellinfo.spanY = ai1[1];
            cellinfo.cellX = ai[0];
            cellinfo.cellY = ai[1];
            cellinfo.screen = i;
        }
        return i;
    }

    int findSlotAndScreen(int ai[], int ai1[], int i, int j, CellLayout.CellInfo cellinfo, boolean flag)
    {
        return findSlotAndScreen(ai, ai1, i, j, cellinfo, false, flag);
    }

    int findSlotAndScreen(int ai[], int ai1[], int i, int j, CellLayout.CellInfo cellinfo, boolean flag, boolean flag1)
    {
        Workspace workspace;
        int k;
        int l;
        int i1;
        workspace = mWorkspace;
        k = workspace.getChildCount();
        l = workspace.getCurrentScreen();
        i1 = l;
_L5:
        if(i1 >= k) goto _L2; else goto _L1
_L1:
        int j1;
        CellLayout celllayout2 = (CellLayout)workspace.getChildAt(i1);
        int ai4[];
        CellLayout.CellInfo cellinfo3;
        if(flag)
            ai4 = celllayout2.rectToCell(i, j);
        else
            ai4 = celllayout2.rectToCell2(i, j);
        cellinfo3 = celllayout2.findAllVacantCells(null, null);
        if(cellinfo3 != null && !cellinfo3.findCellForSpan(ai, ai4[0], ai4[1])) goto _L4; else goto _L3
_L3:
        ai1[0] = ai4[0];
        ai1[1] = ai4[1];
        j1 = i1;
_L6:
        return j1;
_L4:
        i1++;
          goto _L5
_L2:
        int l1;
        if(l <= 0)
            break MISSING_BLOCK_LABEL_243;
        l1 = l - 1;
_L7:
label0:
        {
            if(l1 < 0)
                break MISSING_BLOCK_LABEL_243;
            CellLayout celllayout1 = (CellLayout)workspace.getChildAt(l1);
            int ai3[];
            CellLayout.CellInfo cellinfo2;
            if(flag)
                ai3 = celllayout1.rectToCell(i, j);
            else
                ai3 = celllayout1.rectToCell2(i, j);
            cellinfo2 = celllayout1.findAllVacantCells(null, null);
            if(cellinfo2 != null && !cellinfo2.findCellForSpan(ai, ai3[0], ai3[1]))
                break label0;
            ai1[0] = ai3[0];
            ai1[1] = ai3[1];
            j1 = l1;
        }
          goto _L6
        l1--;
          goto _L7
label1:
        {
            if(!flag1 || isAllPageSlot(l))
                break label1;
            int k1 = workspace.getChildCount() - 1;
            CellLayout celllayout = (CellLayout)workspace.getChildAt(k1);
            int ai2[];
            CellLayout.CellInfo cellinfo1;
            if(flag)
                ai2 = celllayout.rectToCell(i, j);
            else
                ai2 = celllayout.rectToCell2(i, j);
            cellinfo1 = celllayout.findAllVacantCells(null, null);
            if(cellinfo1 != null && !cellinfo1.findCellForSpan(ai, ai2[0], ai2[1]))
                break label1;
            ai1[0] = ai2[0];
            ai1[1] = ai2[1];
            j1 = k1;
        }
          goto _L6
        j1 = -1;
          goto _L6
    }

    public void finishBindingAllWorkspaceItems()
    {
        if(mSavedState != null)
        {
            if(!mWorkspace.hasFocus())
                mWorkspace.getChildAt(mWorkspace.getCurrentScreen()).requestFocus();
            long al[] = mSavedState.getLongArray("launcher.user_folder");
            if(al != null)
            {
                int i = al.length;
                for(int j = 0; j < i; j++)
                {
                    long l = al[j];
                    FolderInfo folderinfo = (FolderInfo)mFolders.get(Long.valueOf(l));
                    if(folderinfo != null)
                        openFolder(folderinfo);
                }

                Folder folder = mWorkspace.getOpenFolder();
                if(folder != null)
                    folder.requestFocus();
            }
            mSavedState = null;
        }
        if(mSavedInstanceState != null)
        {
            super.onRestoreInstanceState(mSavedInstanceState);
            mSavedInstanceState = null;
        }
        mWorkspaceLoading = false;
        Log.d("Launcher", "Workspace binding finished");
        mHandler.removeCallbacks(mRunBadgeChanged);
        mHandler.post(mRunBadgeChanged);
    }

    public void finishBindingAppWidgets()
    {
        Log.d("Launcher", "App widget binding finished");
    }

    public void finishBindingSamsungWidgets()
    {
        Log.d("Launcher", "Samung widget binding finished");
    }

    public void finishBindingShortcuts()
    {
        Log.d("Launcher", "Workspace shortcut binding finished");
    }

    int getAdjacentBlankScreen(int i)
    {
        int k;
        int ai[];
        int ai1[];
        int l;
        int i1;
        int j = mAddWidgetType;
        k = -1;
        if(j != 1 && j != 2 && j != 3)
            break MISSING_BLOCK_LABEL_204;
        ai = mCellCoordinates;
        ai1 = mSpans;
        l = mWorkspace.getChildCount();
        i1 = i;
_L7:
        if(i1 >= l) goto _L2; else goto _L1
_L1:
        CellLayout.CellInfo cellinfo1 = ((CellLayout)mWorkspace.getChildAt(i1)).findAllVacantCells(null, null);
        if(cellinfo1 == null || !cellinfo1.findCellForSpan(ai, ai1[0], ai1[1])) goto _L4; else goto _L3
_L3:
        mBlankScreen[0] = i1;
        k = i1;
_L2:
        if(i != 0) goto _L6; else goto _L5
_L5:
        int k1 = k;
_L8:
        return k1;
_L4:
        i1++;
          goto _L7
_L6:
        int j1 = i - 1;
_L9:
label0:
        {
            if(j1 < 0)
                break MISSING_BLOCK_LABEL_204;
            CellLayout.CellInfo cellinfo = ((CellLayout)mWorkspace.getChildAt(j1)).findAllVacantCells(null, null);
            if(cellinfo == null || !cellinfo.findCellForSpan(ai, ai1[0], ai1[1]))
                break label0;
            mBlankScreen[1] = j1;
            if(k == -1)
                k = j1;
            k1 = k;
        }
          goto _L8
        j1--;
          goto _L9
        k1 = k;
          goto _L8
    }

    AppShortcutZone getAppShortcutZone()
    {
        return mAppShortcutZone;
    }

    public LauncherAppWidgetHost getAppWidgetHost()
    {
        return mAppWidgetHost;
    }

    int getCellLayoutId(int i)
    {
        i;
        JVM INSTR tableswitch 0 6: default 44
    //                   0 49
    //                   1 56
    //                   2 63
    //                   3 70
    //                   4 77
    //                   5 84
    //                   6 91;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8
_L1:
        int j = -1;
_L10:
        return j;
_L2:
        j = 0x7f060002;
        continue; /* Loop/switch isn't completed */
_L3:
        j = 0x7f060003;
        continue; /* Loop/switch isn't completed */
_L4:
        j = 0x7f060004;
        continue; /* Loop/switch isn't completed */
_L5:
        j = 0x7f060005;
        continue; /* Loop/switch isn't completed */
_L6:
        j = 0x7f060006;
        continue; /* Loop/switch isn't completed */
_L7:
        j = 0x7f060007;
        continue; /* Loop/switch isn't completed */
_L8:
        j = 0x7f060008;
        if(true) goto _L10; else goto _L9
_L9:
    }

    Drawable getCurrentImageWallpaperDrawable()
    {
        return mWallpaperImageDrawable;
    }

    public int getCurrentWorkspaceScreen()
    {
        return getScreen();
    }

    DeleteZone getDeleteZone()
    {
        return mDeleteZone;
    }

    DragLayer getDragLayer()
    {
        return mDragLayer;
    }

    MenuDrawer getMenuDrawer()
    {
        return mMenuDrawer;
    }

    MenuManager getMenuManager()
    {
        return mMenuManager;
    }

    QuickViewMainMenu getQuickViewMainMenu()
    {
        return mQuickViewMainMenu;
    }

    QuickViewWorkspace getQuickViewWorkspace()
    {
        return mQuickViewWorkspace;
    }

    int getResOrientation()
    {
        return mResOrientation;
    }

    SamsungWidgetPackageManager getSamsungWidgetPackageManager()
    {
        return mSamsungWidgetPackageManager;
    }

    public int getStateQuickNavigation()
    {
        return mStateQuickNavigation;
    }

    TopFourZone getTopFourZone()
    {
        return mTopFourZone;
    }

    Workspace getWorkspace()
    {
        return mWorkspace;
    }

    boolean isAddWidgetState()
    {
        boolean flag;
        if(mAddWidgetType != 0)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean isAllAppsVisible()
    {
        boolean flag;
        if(mMenuManager != null)
            flag = mMenuManager.isOpened();
        else
            flag = false;
        return flag;
    }

    boolean isAllPageSlot(int i)
    {
        mBlankScreen[0] = -1;
        mBlankScreen[1] = -1;
        boolean flag;
        if(getAdjacentBlankScreen(i) == -1)
        {
            if(mWorkspace.getChildCount() < SCREEN_COUNT)
            {
                createBlankPage();
                flag = false;
            } else
            {
                if(mToast == null)
                    mToast = Toast.makeText(this, getString(0x7f0a0042), 0);
                else
                    mToast.setText(getString(0x7f0a0042));
                mToast.show();
                flag = true;
            }
        } else
        {
            flag = false;
        }
        return flag;
    }

    public boolean isDefaultIMEI()
    {
        String s1;
        String s2;
        boolean flag;
        if(BuildLocaleChecker.getBuildLocale() == 1)
        {
            String s3 = SystemProperties.get("ril.IMEI", "Default");
            s1 = "357858010034783";
            s2 = s3;
        } else
        {
            String s = SystemProperties.get("ril.IMSI");
            s1 = "999999999999999";
            s2 = s;
        }
        if(s2.equals(s1))
            flag = true;
        else
        if(s2.equals("Default"))
        {
            mHandler.sendEmptyMessageDelayed(2, 1000L);
            flag = false;
        } else
        {
            flag = false;
        }
        return flag;
    }

    boolean isWindowOpaque()
    {
        return mIsOpaqueWindow;
    }

    public boolean isWorkspaceLocked()
    {
        boolean flag;
        if(mWorkspaceLoading || mWaitingForResult)
            flag = true;
        else
            flag = false;
        return flag;
    }

    int loadMenuMode()
    {
        return mPrefs.getInt("menu", 0);
    }

    void lockAllApps()
    {
        mMenuManager.lock();
    }

    void menudiscard()
    {
        mMenuManager.discardMenuEdit();
        mMenuManager.setMode(0);
        mAppShortcutZone.updateApplications();
    }

    void menusave()
    {
        AppShortcutZone appshortcutzone = mAppShortcutZone;
        MenuManager menumanager = mMenuManager;
        mMenuManager.stopUpdateDB();
        int i = appshortcutzone.getChildCount();
        for(int j = 0; j < i; j++)
        {
            Object obj1 = appshortcutzone.getChildAt(j).getTag();
            if(!(obj1 instanceof ApplicationInfo))
                continue;
            ApplicationInfo applicationinfo1 = (ApplicationInfo)obj1;
            if(applicationinfo1.topNum != j || applicationinfo1.pageNum != 65535 || applicationinfo1.cellNum != 65535)
            {
                applicationinfo1.topNum = j;
                applicationinfo1.pageNum = 65535;
                applicationinfo1.cellNum = 65535;
                applicationinfo1.isUpdated = false;
                applicationinfo1.editTopNum = j;
                applicationinfo1.editPageNum = 65535;
                applicationinfo1.editCellNum = 65535;
                LauncherModel.updateAppToDatabase(this, applicationinfo1);
            }
        }

        int k = menumanager.getChildCount();
        int l = 0;
        int i1 = 0;
        while(l < k) 
        {
            AppMenu appmenu = (AppMenu)menumanager.getChildAt(l);
            int j1 = appmenu.getChildCount();
            int k1 = 0;
            while(k1 < j1) 
            {
                Object obj = appmenu.getChildAt(k1).getTag();
                if(obj instanceof ApplicationInfo)
                {
                    ApplicationInfo applicationinfo = (ApplicationInfo)obj;
                    if(applicationinfo.topNum != 65535 || applicationinfo.pageNum != i1 || applicationinfo.cellNum != k1)
                    {
                        int l1;
                        boolean flag;
                        if(applicationinfo.topNum != 65535)
                            flag = true;
                        else
                            flag = false;
                        applicationinfo.topNum = 65535;
                        applicationinfo.pageNum = i1;
                        applicationinfo.cellNum = k1;
                        applicationinfo.isUpdated = false;
                        applicationinfo.editTopNum = 65535;
                        applicationinfo.editPageNum = i1;
                        applicationinfo.editCellNum = k1;
                        if(flag)
                            LauncherModel.updateAppToDatabase(this, applicationinfo);
                    } else
                    {
                        applicationinfo.editTopNum = applicationinfo.topNum;
                        applicationinfo.editPageNum = applicationinfo.pageNum;
                        applicationinfo.editCellNum = applicationinfo.cellNum;
                    }
                }
                k1++;
            }
            if(j1 > 0)
                l1 = i1 + 1;
            else
                l1 = i1;
            l++;
            i1 = l1;
        }
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        mWaitingForResult = false;
        Log.d("Launcher", (new StringBuilder()).append("onActivityResult(). reqCode:").append(i).append(", resultCode:").append(j).append(", mAddItemCellInfo:").append(mAddItemCellInfo).toString());
        if(j != -1 || mAddItemCellInfo == null || mWorkspace == null) goto _L2; else goto _L1
_L1:
        if(mAddItemCellInfo.screen != mWorkspace.getCurrentScreen())
        {
            mAddItemCellInfo = mWorkspace.findAllVacantCells(null);
            mAddItemCellInfo.screen = mWorkspace.getCurrentScreen();
        }
        i;
        JVM INSTR tableswitch 1 9: default 168
    //                   1 193
    //                   2 168
    //                   3 168
    //                   4 232
    //                   5 271
    //                   6 169
    //                   7 182
    //                   8 224
    //                   9 263;
           goto _L3 _L4 _L3 _L3 _L5 _L6 _L7 _L8 _L9 _L10
_L3:
        return;
_L7:
        completeAddApplication(this, intent, mAddItemCellInfo);
        continue; /* Loop/switch isn't completed */
_L8:
        processShortcut(intent, 6, 1);
        continue; /* Loop/switch isn't completed */
_L4:
        completeAddShortcut(intent, mAddItemCellInfo);
        if(mModel.isBusy())
            mModel.startLoader(this, false);
        continue; /* Loop/switch isn't completed */
_L9:
        addLiveFolder(intent);
        continue; /* Loop/switch isn't completed */
_L5:
        completeAddLiveFolder(intent, mAddItemCellInfo);
        if(mModel.isBusy())
            mModel.startLoader(this, false);
        continue; /* Loop/switch isn't completed */
_L10:
        addAppWidget(intent);
        continue; /* Loop/switch isn't completed */
_L6:
        completeAddAppWidget(intent, mAddItemCellInfo);
        if(mModel.isBusy())
            mModel.startLoader(this, false);
        continue; /* Loop/switch isn't completed */
_L2:
        if((i == 9 || i == 5) && j == 0 && intent != null)
        {
            int k = intent.getIntExtra("appWidgetId", -1);
            if(k != -1)
                mAppWidgetHost.deleteAppWidgetId(k);
        }
        if(true) goto _L3; else goto _L11
_L11:
    }

    public void onClick(View view)
    {
        Object obj = view.getTag();
        if(!(obj instanceof ShortcutInfo)) goto _L2; else goto _L1
_L1:
        Intent intent = ((ShortcutInfo)obj).intent;
        int ai[] = new int[2];
        view.getLocationOnScreen(ai);
        intent.setSourceBounds(new Rect(ai[0], ai[1], ai[0] + view.getWidth(), ai[1] + view.getHeight()));
        startActivitySafely(intent, null);
_L4:
        return;
_L2:
        if(obj instanceof FolderInfo)
            handleFolderClick((FolderInfo)obj);
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void onConfigurationChanged(Configuration configuration)
    {
        Log.d("Launcher", "onConfigurationChanged()");
        super.onConfigurationChanged(configuration);
    }

    protected void onCreate(Bundle bundle)
    {
        boolean flag = mIsOpaqueWindow;
        mIsOpaqueWindow = true;
        overridePendingTransitionForOpaqueWindowExit();
        mIsOpaqueWindow = flag;
        super.onCreate(bundle);
        LauncherApplication launcherapplication = (LauncherApplication)getApplication();
        mModel = launcherapplication.setLauncher(this);
        mIconCache = launcherapplication.getIconCache();
        mBadgeCache = launcherapplication.getBadgeCache();
        mProductModelFamilyName = LauncherConfig.getProductModelFamily(this);
        mProductModelName = LauncherConfig.getProductModel(this);
        Log.i("Launcher", (new StringBuilder()).append("onCreate():  product model family:").append(mProductModelFamilyName).append(" product model : ").append(mProductModelName).toString());
        mForce16BitWindow = LauncherConfig.getUse16BitWindow(this);
        SCREEN_COUNT = LauncherConfig.getScreenCount(this);
        DEFAULT_SCREEN_COUNT = LauncherConfig.getDefaultScreenCount(this);
        USE_MAINMENU_CONCENTRATION_EFFECT = LauncherConfig.getUseMainMenuConcentrationEffect(this);
        USE_MAINMENU_LISTMODE = LauncherConfig.getUseMainMenuListMode(this);
        USE_MAINMENU_ICONMODE = LauncherConfig.getUseIconMenu(this);
        NUMBER_CELLS_X = LauncherConfig.getWorkspaceCellsX(this);
        NUMBER_CELLS_Y = LauncherConfig.getWorkspaceCellsY(this);
        int i = SystemProperties.getInt("ro.csc.homescreen.screencount", -1);
        if(i > 0 && i <= SCREEN_COUNT)
            DEFAULT_SCREEN_COUNT = i;
        mInflater = getLayoutInflater();
        mPrefs = getSharedPreferences("launcher", 0);
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, 1024);
        mAppWidgetHost.startListening();
        mResOrientation = getResources().getConfiguration().orientation;
        checkForLocaleChange();
        mSamsungWidgetPackageManager = SamsungWidgetPackageManager.getInstance();
        mSamsungWidgetPackageManager.start(this, mLocaleChanged);
        setWallpaperDimension();
        android.view.ViewGroup.LayoutParams layoutparams;
        if(USE_MAINMENU_ICONMODE)
            setContentView(0x7f03000c);
        else
            setContentView(0x7f03000b);
        setupViews();
        registerIntentReceivers();
        registerContentObservers();
        mSavedState = bundle;
        restoreState(mSavedState);
        if(!mRestoring)
        {
            mAppShortcutZone.loadApplications();
            mModel.startLoader(this, true);
        }
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);
        saveScreenInfo();
        mWallpaperCanvasView = new WallpaperCanvasView(this);
        mWallpaperCanvasView.setWorkspace(mWorkspace);
        mWallpaperCanvasView.setId(0x7f060009);
        layoutparams = new android.view.ViewGroup.LayoutParams(-1, -1);
        addContentView(mWallpaperCanvasView, layoutparams);
        mDragLayer.bringToFront();
        if(isDefaultIMEI())
            setSomethingsInDefaultIMEI();
        Log.i("Launcher", "onCreate() ended");
    }

    protected Dialog onCreateDialog(int i)
    {
        i;
        JVM INSTR tableswitch 1 6: default 40
    //                   1 48
    //                   2 64
    //                   3 40
    //                   4 80
    //                   5 97
    //                   6 114;
           goto _L1 _L2 _L3 _L1 _L4 _L5 _L6
_L1:
        Dialog dialog = super.onCreateDialog(i);
_L8:
        return dialog;
_L2:
        dialog = (new CreateShortcut()).createDialog();
        continue; /* Loop/switch isn't completed */
_L3:
        dialog = (new RenameFolder()).createDialog();
        continue; /* Loop/switch isn't completed */
_L4:
        dialog = (new TextDialog()).createDialog(i);
        continue; /* Loop/switch isn't completed */
_L5:
        dialog = (new TextDialog()).createDialog(i);
        continue; /* Loop/switch isn't completed */
_L6:
        dialog = (new TextDialog()).createDialog(i);
        if(true) goto _L8; else goto _L7
_L7:
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean flag;
        if(isWorkspaceLocked() && mSavedInstanceState == null)
        {
            flag = false;
        } else
        {
            Log.d("Launcher", "onCreateOptionsMenu");
            super.onCreateOptionsMenu(menu);
            menu.add(1, 2, 0, 0x7f0a0024).setIcon(0x7f02007b).setAlphabeticShortcut('A');
            menu.add(0, 3, 0, 0x7f0a0025).setIcon(0x7f02008a).setAlphabeticShortcut('W');
            menu.add(0, 4, 0, 0x7f0a0026).setIcon(0x7f020086).setAlphabeticShortcut('s');
            menu.add(0, 5, 0, 0x7f0a0027).setIcon(0x7f02007d).setAlphabeticShortcut('N');
            menu.add(0, 13, 0, 0x7f0a002a).setIcon(0x7f02007f).setAlphabeticShortcut('E');
            Intent intent = new Intent("android.settings.SETTINGS");
            intent.setFlags(0x10200000);
            menu.add(0, 7, 0, 0x7f0a0029).setIcon(0x7f020087).setAlphabeticShortcut('P').setIntent(intent);
            menu.add(2, 8, 0, 0x7f0a002a).setIcon(0x7f02007f);
            if(USE_MAINMENU_LISTMODE)
                menu.add(2, 9, 0, 0x7f0a002b).setIcon(0x7f020082);
            menu.add(2, 15, 0, 0x7f0a002f).setIcon(0x7f020088);
            menu.add(3, 10, 0, 0x7f0a002c).setIcon(0x7f020081);
            menu.add(3, 15, 0, 0x7f0a002f).setIcon(0x7f020088);
            menu.add(4, 11, 0, 0x7f0a002d).setIcon(0x7f020085);
            menu.add(4, 12, 0, 0x7f0a002e).setIcon(0x7f02007c);
            flag = true;
        }
        return flag;
    }

    public void onDestroy()
    {
        Log.i("Launcher", "onDestroy()");
        mDestroyed = true;
        super.onDestroy();
        cancelAddWidget();
        try
        {
            mAppWidgetHost.stopListening();
        }
        catch(NullPointerException nullpointerexception)
        {
            Log.w("Launcher", "problem while stopping AppWidgetHost during Launcher destruction", nullpointerexception);
        }
        TextKeyListener.getInstance().release();
        mModel.stopLoader();
        unbindDesktopItems();
        mMenuManager.setAdapter(null);
        mSamsungWidgetPackageManager.unbind();
        getContentResolver().unregisterContentObserver(mWidgetObserver);
        getContentResolver().unregisterContentObserver(mBadgeObserver);
        unregisterReceiver(mCloseSystemDialogsReceiver);
        unregisterReceiver(mReceiver);
        unregisterReceiver(mWallpaperChangedReceiver);
        unregisterReceiver(mAirplaneModeChangeReceiver);
    }

    public boolean onKeyDown(int i, KeyEvent keyevent)
    {
        boolean flag = super.onKeyDown(i, keyevent);
        if(i == 27)
        {
            Intent intent = new Intent();
            intent.setClassName("com.sec.android.app.camera", "com.sec.android.app.camera.Camera");
            startActivity(intent);
        }
        if(!flag && acceptFilter() && i != 66 && TextKeyListener.getInstance().onKeyDown(mWorkspace, mDefaultKeySsb, i, keyevent) && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0)
            flag = onSearchRequested();
        return flag;
    }

    public void onLoadingStart()
    {
        if(mMenuManager != null)
            mMenuManager.stopUpdateDB();
    }

    public boolean onLongClick(View view)
    {
        if(!isWorkspaceLocked()) goto _L2; else goto _L1
_L1:
        boolean flag;
        Log.d("Launcher", (new StringBuilder()).append("onLongClick()/isWorkspaceLocked() - mWorkspaceLoading=").append(mWorkspaceLoading).append(" mWaitingForResult=").append(mWaitingForResult).toString());
        flag = false;
_L4:
        return flag;
_L2:
        View view1;
        CellLayout.CellInfo cellinfo;
        if(!(view instanceof CellLayout))
            view1 = (View)view.getParent();
        else
            view1 = view;
        cellinfo = (CellLayout.CellInfo)view1.getTag();
        if(cellinfo == null)
        {
            Log.d("Launcher", "onLongClick - cellInfo is a null");
            flag = true;
            continue; /* Loop/switch isn't completed */
        }
        Log.d("Launcher", (new StringBuilder()).append("onLongClick - allowLongPress = ").append(mWorkspace.allowLongPress()).toString());
        if(mWorkspace.allowLongPress())
        {
            if(cellinfo.cell != null)
                break; /* Loop/switch isn't completed */
            if(cellinfo.valid)
            {
                mWorkspace.setAllowLongPress(false);
                showAddDialog(cellinfo);
            }
        }
        flag = true;
        if(true) goto _L4; else goto _L3
_L3:
        if(cellinfo.cell instanceof Folder)
            break MISSING_BLOCK_LABEL_169;
        mWorkspace.startDrag(cellinfo);
        break MISSING_BLOCK_LABEL_169;
        if(true) goto _L4; else goto _L5
_L5:
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        isHomeKeyToDefaultPage = true;
        if("android.intent.action.MAIN".equals(intent.getAction()))
        {
            closeSystemDialogs();
            mIsNewIntent = true;
            boolean flag;
            if(intent.hasExtra("widgetInstall"))
            {
                Intent intent1 = getIntent();
                intent1.putExtra("widgetInstall", intent.getBooleanExtra("widgetInstall", false));
                intent1.putExtra("packageName", intent.getStringExtra("packageName"));
                intent1.putExtra("className", intent.getStringExtra("className"));
                flag = true;
            } else
            {
                flag = false;
            }
            if((0x400000 & intent.getFlags()) != 0x400000)
            {
                MenuManager menumanager = mMenuManager;
                Workspace workspace = mWorkspace;
                if(mQuickViewWorkspace.isOpened())
                    closeQuickViewWorkspace();
                View view;
                if(mQuickViewMainMenu.isOpened())
                {
                    closeQuickViewMainMenu();
                    if(menumanager.isOpened())
                        closeDrawer(false);
                } else
                if(menumanager.isOpened())
                    closeDrawer(false);
                else
                if(!flag)
                {
                    workspace.moveToDefaultScreen();
                    sScreen = 0;
                    mIsMoveDefaultScreen = true;
                }
                view = getWindow().peekDecorView();
                if(view != null && view.getWindowToken() != null)
                    ((InputMethodManager)getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else
            {
                closeQuickViewWorkspace();
                closeQuickViewMainMenu();
                closeDrawer(false);
                mStateQuickNavigation = -1;
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        menuitem.getItemId();
        JVM INSTR tableswitch 2 15: default 76
    //                   2 84
    //                   3 103
    //                   4 112
    //                   5 122
    //                   6 131
    //                   7 76
    //                   8 136
    //                   9 149
    //                   10 179
    //                   11 192
    //                   12 209
    //                   13 218
    //                   14 244
    //                   15 260;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L1 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14
_L1:
        boolean flag = super.onOptionsItemSelected(menuitem);
_L16:
        return flag;
_L2:
        if(!mMenuManager.isOpened())
            addItems();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L3:
        startWallpaper();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L4:
        onSearchRequested();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L5:
        showNotifications();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L6:
        flag = true;
        continue; /* Loop/switch isn't completed */
_L7:
        mMenuManager.setMode(2);
        flag = true;
        continue; /* Loop/switch isn't completed */
_L8:
        if(USE_MAINMENU_LISTMODE)
            mMenuManager.setMode(1);
        else
            mMenuManager.setMode(0);
        flag = true;
        continue; /* Loop/switch isn't completed */
_L9:
        mMenuManager.setMode(0);
        flag = true;
        continue; /* Loop/switch isn't completed */
_L10:
        menusave();
        mMenuManager.setMode(0);
        flag = true;
        continue; /* Loop/switch isn't completed */
_L11:
        showDiscardMenuEdit();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L12:
        if(!mMenuManager.isOpened())
        {
            getQuickViewWorkspace().drawOpenAnimation();
            openQuickViewWorkspace();
        }
        flag = true;
        continue; /* Loop/switch isn't completed */
_L13:
        getQuickViewMainMenu().drawOpenAnimation();
        openQuickViewMainMenu();
        flag = true;
        continue; /* Loop/switch isn't completed */
_L14:
        onShareAppRequested();
        flag = true;
        if(true) goto _L16; else goto _L15
_L15:
    }

    public void onOptionsMenuClosed(Menu menu)
    {
        Log.d("Launcher", "onOptionsMenuClosed");
        mOptionMenuOpening = false;
        super.onOptionsMenuClosed(menu);
    }

    protected void onPause()
    {
        Log.i("Launcher", "onPause()");
        super.onPause();
        if(isDefaultIMEI())
            mCscVer = (new StringBuilder()).append("CSC Version : ").append(SystemProperties.get("ril.official_cscver", "Not available")).toString();
        if(mExec != null)
            mExec.shutdown();
        mExec = null;
        mWorkspace.pauseScreen(sScreen);
        mDragLayer.cancelDrag();
        saveScreenInfo();
        mIsActive = false;
        cancelAddWidget();
        mQuickViewWorkspace.cancelDrag();
        mQuickViewMainMenu.cancelDrag();
        setWallpaperVisiblity(true);
    }

    protected void onPrepareDialog(int i, Dialog dialog)
    {
        i;
        JVM INSTR tableswitch 1 2: default 24
    //                   1 24
    //                   2 25;
           goto _L1 _L1 _L2
_L1:
        return;
_L2:
        if(mFolderInfo != null)
        {
            EditText edittext = (EditText)dialog.findViewById(0x7f060027);
            edittext.setText(mFolderInfo.title);
            edittext.selectAll();
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        Log.d("Launcher", "onPrepareOptionsMenu");
        boolean flag;
        if(mDeleteZone.mTrashMode)
            flag = false;
        else
        if(mWidgetPreview.getVisibility() == 0)
            flag = false;
        else
        if(mQuickViewMainMenu.isOpened() || mQuickViewWorkspace.isOpened())
            flag = false;
        else
        if(mMenuManager.getAnimateStatus())
        {
            flag = false;
        } else
        {
            if(mMenuManager.isOpened())
            {
                menu.setGroupVisible(0, false);
                menu.setGroupVisible(1, false);
                menu.setGroupVisible(5, false);
                if(mMenuManager.getMode() == 0)
                {
                    menu.setGroupVisible(2, true);
                    menu.setGroupVisible(3, false);
                    menu.setGroupVisible(4, false);
                } else
                if(mMenuManager.getMode() == 1)
                {
                    menu.setGroupVisible(2, false);
                    menu.setGroupVisible(3, true);
                    menu.setGroupVisible(4, false);
                } else
                if(mMenuManager.getMode() == 2)
                {
                    menu.setGroupVisible(2, false);
                    menu.setGroupVisible(3, false);
                    menu.setGroupVisible(4, true);
                }
                if(!mMainMenuLoading)
                {
                    menu.findItem(8).setEnabled(true);
                    mOptionMenuOpening = true;
                } else
                {
                    menu.findItem(8).setEnabled(false);
                }
            } else
            {
                mMenuAddInfo = mWorkspace.findAllVacantCells(null);
                CellLayout.CellInfo cellinfo = mWorkspace.findAllVacantCellsFromModel();
                if(cellinfo == null)
                {
                    Log.e("Launcher", "Unable to find a vacant cell from the model");
                } else
                {
                    if(findSingleSlotAnywhere(cellinfo, false) == -1 && mWorkspace.getChildCount() == SCREEN_COUNT)
                        menu.setGroupEnabled(1, false);
                    else
                        menu.setGroupEnabled(1, true);
                    menu.setGroupVisible(0, true);
                    menu.setGroupVisible(1, true);
                    menu.setGroupVisible(2, false);
                    menu.setGroupVisible(3, false);
                    menu.setGroupVisible(4, false);
                    menu.setGroupVisible(5, false);
                }
            }
            flag = true;
        }
        return flag;
    }

    protected void onRestoreInstanceState(Bundle bundle)
    {
        Bundle bundle1 = bundle.getBundle("android:viewHierarchyState");
        android.util.SparseArray sparsearray;
        int i;
        if(bundle1 != null)
        {
            android.util.SparseArray sparsearray1 = bundle1.getSparseParcelableArray("android:views");
            bundle1.remove("android:views");
            int j = bundle1.getInt("android:focusedViewId", -1);
            bundle1.remove("android:focusedViewId");
            sparsearray = sparsearray1;
            i = j;
        } else
        {
            sparsearray = null;
            i = -1;
        }
        super.onRestoreInstanceState(bundle);
        if(bundle1 != null)
        {
            bundle1.putSparseParcelableArray("android:views", sparsearray);
            bundle1.putInt("android:focusedViewId", i);
            bundle1.remove("android:Panels");
        }
        mSavedInstanceState = bundle;
    }

    protected void onResume()
    {
        Log.i("Launcher", (new StringBuilder()).append("onResume(). mIsNewIntent : ").append(mIsNewIntent).toString());
        overridePendingTransitionForOpaqueWindowEnter();
        super.onResume();
        sendBroadcast(new Intent("com.sec.android.app.controlpanel.RUNNING_PROGRAM_REQ"));
        updateWindowTransparency();
        mIsActive = true;
        mExec = Executors.newSingleThreadExecutor();
        if(mRestoring)
        {
            mWorkspaceLoading = true;
            mAppShortcutZone.loadApplications();
            mModel.startLoader(this, true);
            mRestoring = false;
        } else
        if(mIsChangedBadge)
        {
            mHandler.removeCallbacks(mRunBadgeChanged);
            mHandler.post(mRunBadgeChanged);
        }
        SamsungUtils.broadcastStkIntent(this);
        mDragLayer.cancelDrag();
        mMenuManager.resume();
        if(mIsNewIntent)
        {
            if(!mIsMoveDefaultScreen)
                mWorkspace.resumeScreen(sScreen);
        } else
        {
            mWorkspace.resume(sScreen);
            mWorkspace.resumeScreen(sScreen);
        }
        if(mQuickViewMainMenu.isOpened())
            mQuickViewMainMenu.cancelDrag();
        if(mQuickViewWorkspace.isOpened())
            mQuickViewWorkspace.cancelDrag();
        mIsNewIntent = false;
        mIsMoveDefaultScreen = false;
        launchMtpApp(this);
        if(mIsScreenOff)
            mIsScreenOff = false;
        else
            mHandler.postDelayed(new Runnable() {

                public void run()
                {
                    updateWallpaperVisiblity();
                }

                final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
            }
, 1000L);
        sendBroadcast(new Intent("sec.android.intent.action.HOME_RESUME"));
        Log.i("Launcher", "onResume() ended");
    }

    public Object onRetainNonConfigurationInstance()
    {
        mModel.stopLoader();
        return Boolean.TRUE;
    }

    protected void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putInt("launcher.current_screen", mWorkspace.getCurrentScreen());
        int i = mMenuManager.getCurrentScreen();
        bundle.putString("launcher.delete_application", mUninstallPackageName);
        bundle.putInt("launcher.menu_mode", mMenuManager.getMode());
        bundle.putInt("launcher.menu_current_screen", i);
        bundle.putInt("launcher.menu_screen_count", mMenuManager.getChildCount());
        if(mStateQuickNavigation > -1)
        {
            bundle.putInt("launcher.quick_navigation", mStateQuickNavigation);
            ArrayList arraylist;
            int j;
            int k;
            if(mDeleteIndex > -1)
                bundle.putInt("launcher.delete_index", mDeleteIndex);
            else
                bundle.putInt("launcher.delete_index", -1);
        } else
        if(mQuickViewWorkspace.isOpened())
        {
            bundle.putInt("launcher.quick_navigation", 0);
            if(mIsDeletePopup)
                bundle.putInt("launcher.delete_index", mQuickViewWorkspace.getDeleteIndex());
            else
                bundle.putInt("launcher.delete_index", -1);
        } else
        if(mQuickViewMainMenu.isOpened())
            bundle.putInt("launcher.quick_navigation", 1);
        else
            bundle.putInt("launcher.quick_navigation", -1);
        arraylist = mWorkspace.getOpenFolders();
        if(arraylist.size() > 0)
        {
            j = arraylist.size();
            long al[] = new long[j];
            for(k = 0; k < j; k++)
                al[k] = ((ItemInfo) (((Folder)arraylist.get(k)).getInfo())).id;

            bundle.putLongArray("launcher.user_folder", al);
        }
        if(mMenuManager.isOpened())
            bundle.putBoolean("launcher.all_apps_folder", true);
        if(mAddItemCellInfo != null && mAddItemCellInfo.valid && mWaitingForResult)
        {
            CellLayout.CellInfo cellinfo = mAddItemCellInfo;
            CellLayout celllayout = (CellLayout)mWorkspace.getChildAt(cellinfo.screen);
            if(celllayout == null)
            {
                mAddItemCellInfo.valid = false;
            } else
            {
                bundle.putInt("launcher.add_screen", cellinfo.screen);
                bundle.putInt("launcher.add_cellX", cellinfo.cellX);
                bundle.putInt("launcher.add_cellY", cellinfo.cellY);
                bundle.putInt("launcher.add_spanX", cellinfo.spanX);
                bundle.putInt("launcher.add_spanY", cellinfo.spanY);
                bundle.putInt("launcher.add_countX", celllayout.getCountX());
                bundle.putInt("launcher.add_countY", celllayout.getCountY());
                bundle.putBooleanArray("launcher.add_occupied_cells", celllayout.getOccupiedCells());
            }
        }
        if(mFolderInfo != null && mWaitingForResult)
        {
            bundle.putBoolean("launcher.rename_folder", true);
            bundle.putLong("launcher.rename_folder_id", ((ItemInfo) (mFolderInfo)).id);
        }
    }

    public boolean onSearchRequested()
    {
        startSearch(null, false, null, true);
        return true;
    }

    public void onWindowFocusChanged(boolean flag)
    {
        Log.i("Launcher", (new StringBuilder()).append("onWindowFocusChanged(").append(flag).append(")").toString());
        super.onWindowFocusChanged(flag);
        setWallpaperDimension();
        mWorkspace.updateWallpaperOffset();
        if(flag)
        {
            updateWindowTransparency();
            if(mDefaultKeySsb != null && mDefaultKeySsb.length() > 0)
                startSearch(null, false, null, true);
            mWorkspace.setShowIndicator();
        } else
        {
            mQuickViewWorkspace.cancelDrag();
            mQuickViewMainMenu.cancelDrag();
        }
    }

    void openQuickViewMainMenu()
    {
        if(mMenuManager.isOpened())
        {
            if(getResources().getConfiguration().orientation == 2 && LauncherConfig.landscapeScreen_isUseFullScreenQuickView(this))
                getWindow().addFlags(1024);
            if(mTopFourZone != null)
                mTopFourZone.setVisibility(8);
            mAppShortcutZone.setVisibility(8);
            setBackgroundNotification(0);
            mMenuManager.invalidate();
            if(mMenuManager.getChildCount() == 0 && mMenuScreenCount > 0)
                mQuickViewMainMenu.initScreen(mMenuScreenCount);
            else
                mQuickViewMainMenu.initScreen(mMenuManager.getChildCount());
            closeOptionsMenu();
            mQuickViewMainMenu.open();
        }
    }

    void openQuickViewWorkspace()
    {
        if(getResources().getConfiguration().orientation == 2 && LauncherConfig.landscapeScreen_isUseFullScreenQuickView(this))
            getWindow().addFlags(1024);
        mAppShortcutZone.setVisibility(8);
        if(mTopFourZone != null)
            mTopFourZone.setVisibility(8);
        mWorkspace.setVisibility(4);
        mWorkspace.invalidate();
        mQuickViewWorkspace.initScreen(mWorkspace.getChildCount());
        closeOptionsMenu();
        mQuickViewWorkspace.open();
        final int curScreen = mWorkspace.getCurrentScreen();
        mHandler.postDelayed(new Runnable() {

            public void run()
            {
                mWorkspace.pauseScreen(curScreen);
            }

            final Launcher this$0;
            final int val$curScreen;

            
            {
                this$0 = Launcher.this;
                curScreen = i;
                super();
            }
        }
, 450L);
    }

    void overridePendingTransitionForOpaqueWindowEnter()
    {
        if(mIsOpaqueWindow)
            overridePendingTransition(0x7f040004, 0x7f040005);
    }

    void overridePendingTransitionForOpaqueWindowExit()
    {
        if(mIsOpaqueWindow)
            overridePendingTransition(0x7f040002, 0x7f040003);
    }

    void previewAddSamsungWidget()
    {
        if(mSamsungWidgetInfo == null)
        {
            cancelAddWidget();
        } else
        {
            SamsungAppWidgetInfo samsungappwidgetinfo = mSamsungWidgetInfo;
            int i = getResources().getDimensionPixelSize(0x7f090003);
            int j = getResources().getDimensionPixelSize(0x7f090004);
            mWidgetPreview.addView(samsungappwidgetinfo.widgetView, new android.view.ViewGroup.LayoutParams(i * ((ItemInfo) (samsungappwidgetinfo)).spanX, j * ((ItemInfo) (samsungappwidgetinfo)).spanY));
        }
    }

    void processShortcut(Intent intent, int i, int j)
    {
        String s = getResources().getString(0x7f0a0013);
        String s1 = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        if(s != null && s.equals(s1))
        {
            Intent intent1 = new Intent("android.intent.action.MAIN", null);
            intent1.addCategory("android.intent.category.LAUNCHER");
            Intent intent2 = new Intent("android.intent.action.PICK_ACTIVITY");
            intent2.putExtra("android.intent.extra.INTENT", intent1);
            startActivityForResult(intent2, i);
        } else
        {
            startActivityForResult(intent, j);
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherappwidgetinfo)
    {
        mDesktopItems.remove(launcherappwidgetinfo);
        launcherappwidgetinfo.hostView = null;
    }

    void removeFolder(FolderInfo folderinfo)
    {
        mFolders.remove(Long.valueOf(((ItemInfo) (folderinfo)).id));
    }

    void removePage()
    {
        mIsDeletePopup = false;
        mQuickViewWorkspace.removeScreen();
        mWaitingForResult = false;
    }

    public void removeSamsungAppWidget(SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        mDesktopItems.remove(samsungappwidgetinfo);
    }

    public void removeShortcut(ShortcutInfo shortcutinfo)
    {
        mDesktopItems.remove(shortcutinfo);
    }

    void saveMenuMode(int i)
    {
        android.content.SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("menu", i);
        editor.commit();
    }

    public void saveScreenInfo()
    {
        android.content.SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("screencount", mWorkspace.getChildCount());
        editor.putInt("currentscreen", sScreen);
        editor.commit();
    }

    public void setBackgroundNotification(int i)
    {
        findViewById(0x1020002).setBackgroundColor(i);
    }

    void setWallpaperVisiblity(boolean flag)
    {
        SamsungUtils.setWallpaperVisibility((WallpaperManager)getSystemService("wallpaper"), flag);
    }

    void setWindowOpaque()
    {
        Log.e("Launcher", "setWindowOpaque()");
        Window window = getWindow();
        window.clearFlags(0x100000);
        window.setFormat(-1);
        window.setBackgroundDrawable(null);
        mIsOpaqueWindow = true;
    }

    void setWindowTranslucent()
    {
        Log.e("Launcher", "setWindowTranslucent()");
        Window window = getWindow();
        window.addFlags(0x100000);
        window.setFormat(-3);
        window.setBackgroundDrawableResource(0x106000d);
        mIsOpaqueWindow = false;
    }

    void showDeleteApplication(String s)
    {
        mUninstallPackageName = s;
        showDialog(4);
    }

    void showDeleteWorkScreen()
    {
        mIsDeletePopup = true;
        showDialog(6);
    }

    void showDiscardMenuEdit()
    {
        showDialog(5);
    }

    void showRenameDialog(FolderInfo folderinfo)
    {
        mFolderInfo = folderinfo;
        mWaitingForResult = true;
        showDialog(2);
    }

    void showWidgetMessage(boolean flag)
    {
        if(flag)
        {
            if(mToast == null)
                mToast = Toast.makeText(this, getString(0x7f0a001e), 0);
            else
                mToast.setText(getString(0x7f0a001e));
        } else
        if(mToast == null)
            mToast = Toast.makeText(this, 0x7f0a000e, 0);
        else
            mToast.setText(0x7f0a000e);
        mToast.show();
    }

    public void startActivityForResult(Intent intent, int i)
    {
        if(i >= 0)
            mWaitingForResult = true;
        super.startActivityForResult(intent, i);
    }

    void startActivitySafely(Intent intent, Object obj)
    {
        if(intent != null && mIsActive)
        {
            intent.addFlags(0x10000000);
            MenuManagerBackKeyDownInfo = false;
            try
            {
                SamsungUtils.acquireDVFSlock(0, 1000);
                startActivity(intent);
                overridePendingTransitionForOpaqueWindowExit();
            }
            catch(ActivityNotFoundException activitynotfoundexception)
            {
                Toast.makeText(this, 0x7f0a0006, 0).show();
                Log.e("Launcher", (new StringBuilder()).append("Unable to launch. tag=").append(obj).append(" intent=").append(intent).toString(), activitynotfoundexception);
            }
            catch(SecurityException securityexception)
            {
                Toast.makeText(this, 0x7f0a0006, 0).show();
                Log.e("Launcher", (new StringBuilder()).append("Launcher does not have the permission to launch ").append(intent).append(". Make sure to create a MAIN intent-filter for the corresponding activity ").append("or use the exported attribute for this activity. ").append("tag=").append(obj).append(" intent=").append(intent).toString(), securityexception);
            }
        }
    }

    public void startBinding()
    {
        Log.d("Launcher", "Launcher binding started");
        getLocalActivityManager().removeAllActivities();
        mDesktopItems.clear();
        Workspace workspace = mWorkspace;
        int i = workspace.getChildCount();
        for(int j = 0; j < i; j++)
            ((ViewGroup)workspace.getChildAt(j)).removeAllViewsInLayout();

    }

    public void startSearch(String s, boolean flag, Bundle bundle, boolean flag1)
    {
        mHandler.post(new Runnable() {

            public void run()
            {
                if(mQuickViewMainMenu.isOpened())
                    closeQuickViewMainMenu();
                if(mQuickViewWorkspace.isOpened())
                    closeQuickViewWorkspace();
            }

            final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
        }
);
        if(s == null)
        {
            s = getTypedText();
            clearTypedText();
        }
        SearchManager searchmanager = (SearchManager)getSystemService("search");
        ComponentName componentname = getComponentName();
        searchmanager.startSearch(s, flag, componentname, bundle, flag1);
    }

    void uninstallPackage()
    {
        Log.d("Launcher", (new StringBuilder()).append("uninstallPackage(). package:").append(mUninstallPackageName).toString());
        mMenuManager.lock();
        PackageDeleteObserver packagedeleteobserver = new PackageDeleteObserver();
        try
        {
            getPackageManager().deletePackage(mUninstallPackageName, packagedeleteobserver, 0);
        }
        catch(Exception exception)
        {
            Log.e("Launcher", (new StringBuilder()).append("uninstallPackage() ").append(exception).toString());
            mMenuManager.unlock();
        }
        mUninstallPackageName = null;
        mDragLayer.invalidate();
    }

    void updateTopfourBadge()
    {
        if(mTopFourZone != null)
            mTopFourZone.updateBadges();
    }

    void updateWallpaperVisiblity()
    {
        WallpaperManager wallpapermanager = (WallpaperManager)getSystemService("wallpaper");
        if(mMenuManager != null && mMenuManager.isOpened())
            SamsungUtils.setWallpaperVisibility(wallpapermanager, false);
        else
            SamsungUtils.setWallpaperVisibility(wallpapermanager, true);
    }

    void updateWindowTransparency()
    {
        if(mForce16BitWindow)
        {
            WallpaperManager wallpapermanager = (WallpaperManager)getSystemService("wallpaper");
            boolean flag;
            if(wallpapermanager.getWallpaperInfo() != null)
                flag = true;
            else
                flag = false;
            if(flag)
            {
                setWindowTranslucent();
                mWallpaperCanvasView.setVisibility(4);
                mWallpaperImageDrawable = null;
            } else
            {
                setWindowOpaque();
                mWallpaperCanvasView.setVisibility(0);
                mWallpaperImageDrawable = wallpapermanager.getFastDrawable();
                makeBitmapMenuWallpaper();
            }
        }
    }

    void updateWorkspaceBadge()
    {
        int i = mWorkspace.getChildCount();
        int j = 0;
        do
        {
            if(j >= i)
                break;
            CellLayout celllayout = (CellLayout)mWorkspace.getChildAt(j);
            int k = celllayout.getChildCount();
            int l = 0;
            while(l < k) 
            {
                View view = celllayout.getChildAt(l);
                if(view instanceof UserFolder)
                {
                    ((UserFolder)view).notifyDataSetChanged();
                } else
                {
                    Object obj = view.getTag();
                    if(obj != null && (obj instanceof ShortcutInfo))
                    {
                        ShortcutInfo shortcutinfo = (ShortcutInfo)obj;
                        shortcutinfo.badgeCount = mBadgeCache.getBadgeCount(shortcutinfo.intent);
                        view.invalidate();
                    }
                }
                l++;
            }
            celllayout.invalidate();
            j++;
        } while(true);
    }

    static int DEFAULT_SCREEN_COUNT = 7;
    static int DEFAULT_SCREEN_POSITION = 0;
    static int NUMBER_CELLS_X = 4;
    static int NUMBER_CELLS_Y = 4;
    static int SCREEN_COUNT = 7;
    static boolean USE_MAINMENU_CONCENTRATION_EFFECT = false;
    static boolean USE_MAINMENU_ICONMODE = false;
    static boolean USE_MAINMENU_LISTMODE = false;
    protected static String mBand;
    protected static String mBattVer;
    protected static String mBootVer;
    protected static String mCalDate;
    protected static String mCscVer;
    private static HashMap mFolders = new HashMap();
    protected static String mHwVer;
    protected static boolean mIsDefaultIMEI = false;
    protected static String mIsPbaP;
    protected static String mIsSmdP;
    public static Bitmap mMenuWallpaperBitmap;
    protected static String mPdaVer;
    protected static String mPhoneVer;
    protected static String mSmd_Pba;
    protected static String mTSP;
    protected static String mUART;
    protected static String mUSB;
    static String mUninstallPackageName;
    private static final String ratMode[];
    private static final Object sLock = new Object();
    private static int sScreen = 0;
    private boolean MenuManagerBackKeyDownInfo;
    private final int UNINSTALL_COMPLETE = 1;
    public boolean isHomeKeyToDefaultPage;
    private CellLayout.CellInfo mAddItemCellInfo;
    private int mAddWidgetType;
    private final BroadcastReceiver mAirplaneModeChangeReceiver = new AirplaneModeChangeReceiver();
    private AppShortcutZone mAppShortcutZone;
    private LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetProviderInfo mAppWidgetInfo;
    private AppWidgetManager mAppWidgetManager;
    private BadgeCache mBadgeCache;
    private final ContentObserver mBadgeObserver = new BadgeChangeObserver();
    int mBlankScreen[];
    private final int mCellCoordinates[] = new int[2];
    private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
    private boolean mConfigChange;
    private SpannableStringBuilder mDefaultKeySsb;
    private int mDeleteIndex;
    private DeleteZone mDeleteZone;
    private ArrayList mDesktopItems;
    private boolean mDestroyed;
    private Drawable mDimWallpaperImageDrawable;
    private DragLayer mDragLayer;
    private ExecutorService mExec;
    private FolderInfo mFolderInfo;
    private boolean mForce16BitWindow;
    private Handler mHandler;
    private boolean mHiddenFocus;
    private IconCache mIconCache;
    private LayoutInflater mInflater;
    private boolean mIsActive;
    private boolean mIsChangedBadge;
    private boolean mIsDeletePopup;
    private boolean mIsMoveDefaultScreen;
    private boolean mIsNewIntent;
    private boolean mIsOpaqueWindow;
    private boolean mIsScreenOff;
    private LauncherAppWidgetInfo mLauncherAppWidgetInfo;
    private boolean mLocaleChanged;
    private boolean mMainMenuLoading;
    private CellLayout.CellInfo mMenuAddInfo;
    private MenuDrawer mMenuDrawer;
    private MenuManager mMenuManager;
    private int mMenuScreenCount;
    private LauncherModel mModel;
    private ArrayList mOpenFolders;
    private boolean mOptionMenuOpening;
    private SharedPreferences mPrefs;
    String mProductModelFamilyName;
    String mProductModelName;
    private QuickViewMainMenu mQuickViewMainMenu;
    private QuickViewWorkspace mQuickViewWorkspace;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent)
        {
            String s = intent.getAction();
            if(!"android.intent.action.SCREEN_OFF".equals(s)) goto _L2; else goto _L1
_L1:
            Log.i("Launcher", "ACTION_SCREEN_OFF");
            mIsScreenOff = true;
_L4:
            return;
_L2:
            if("android.intent.action.USER_PRESENT".equals(s))
            {
                Log.i("Launcher", "ACTION_USER_PRESENT");
                updateWallpaperVisiblity();
                if(!mIsActive)
                    mIsScreenOff = false;
            } else
            if("android.intent.action.MULTI_CSC_CLEAR".equals(s))
            {
                boolean flag = deleteDatabase("launcher.db");
                Log.i("Launcher", (new StringBuilder()).append("deleted a database file = ").append(flag).toString());
            }
            if(true) goto _L4; else goto _L3
_L3:
        }

        final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
    }
;
    private int mResOrientation;
    private boolean mRestoring;
    private Runnable mRunBadgeChanged;
    private SamsungAppWidgetInfo mSamsungWidgetInfo;
    private SamsungWidgetPackageManager mSamsungWidgetPackageManager;
    private Bundle mSavedInstanceState;
    private Bundle mSavedState;
    private int mSpans[];
    private int mStateQuickNavigation;
    private Toast mToast;
    private TopFourZone mTopFourZone;
    private long mUserFolders[];
    private boolean mWaitingForResult;
    private WallpaperCanvasView mWallpaperCanvasView;
    private final BroadcastReceiver mWallpaperChangedReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent)
        {
            if("android.intent.action.WALLPAPER_CHANGED".equals(intent.getAction()))
            {
                Log.i("Launcher", "ACTION_WALLPAPER_CHANGED broadcast received");
                if(mIsActive)
                {
                    updateWindowTransparency();
                    if(mWorkspace != null)
                        mWorkspace.invalidate();
                }
            }
        }

        final Launcher this$0;

            
            {
                this$0 = Launcher.this;
                super();
            }
    }
;
    private Drawable mWallpaperImageDrawable;
    private int mWidgetId;
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();
    private WidgetPreview mWidgetPreview;
    private Workspace mWorkspace;
    private boolean mWorkspaceLoading;

    static 
    {
        String as[] = new String[4];
        as[0] = "DUAL_MODE";
        as[1] = "GSM_ONLY";
        as[2] = "UMTS_ONLY";
        as[3] = "INVALID_RAT";
        ratMode = as;
    }


/*
    static boolean access$1302(Launcher launcher, boolean flag)
    {
        launcher.mWaitingForResult = flag;
        return flag;
    }

*/



/*
    static FolderInfo access$1502(Launcher launcher, FolderInfo folderinfo)
    {
        launcher.mFolderInfo = folderinfo;
        return folderinfo;
    }

*/




/*
    static boolean access$1702(Launcher launcher, boolean flag)
    {
        launcher.mWorkspaceLoading = flag;
        return flag;
    }

*/





/*
    static boolean access$202(Launcher launcher, boolean flag)
    {
        launcher.mIsScreenOff = flag;
        return flag;
    }

*/








/*
    static boolean access$2802(Launcher launcher, boolean flag)
    {
        launcher.mIsChangedBadge = flag;
        return flag;
    }

*/









}
