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

import android.appwidget.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.text.Collator;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DeferredHandler, AllAppsList, LauncherApplication, Utilities, 
//            ApplicationInfo, ItemInfo, FolderInfo, LiveFolderInfo, 
//            UserFolderInfo, ShortcutInfo, FastBitmapDrawable, IconCache, 
//            SamsungWidgetPackageManager, BadgeCache, LauncherAppWidgetInfo, SamsungAppWidgetInfo, 
//            Launcher

public class LauncherModel extends BroadcastReceiver
{
    static class ApplicationInfoComparator
        implements Comparator
    {

        private int integerCompare(int i, int j)
        {
            int k = 0;
            if(i <= j) goto _L2; else goto _L1
_L1:
            k = 1;
_L4:
            return k;
_L2:
            if(i < j)
                k = -1;
            if(true) goto _L4; else goto _L3
_L3:
        }

        public final int compare(ApplicationInfo applicationinfo, ApplicationInfo applicationinfo1)
        {
            if(mMode != 2) goto _L2; else goto _L1
_L1:
            int i;
            i = integerCompare(applicationinfo.editTopNum, applicationinfo1.editTopNum);
            if(i == 0)
            {
                i = integerCompare(applicationinfo.editPageNum, applicationinfo1.editPageNum);
                if(i == 0)
                    i = integerCompare(applicationinfo.editCellNum, applicationinfo1.editCellNum);
            }
_L4:
            if(i == 0)
                i = LauncherModel.sCollator.compare(applicationinfo.title.toString(), applicationinfo1.title.toString());
            return i;
_L2:
            i = integerCompare(applicationinfo.topNum, applicationinfo1.topNum);
            if(i == 0)
            {
                i = integerCompare(applicationinfo.pageNum, applicationinfo1.pageNum);
                if(i == 0)
                    i = integerCompare(applicationinfo.cellNum, applicationinfo1.cellNum);
            }
            if(true) goto _L4; else goto _L3
_L3:
        }

        public volatile int compare(Object obj, Object obj1)
        {
            return compare((ApplicationInfo)obj, (ApplicationInfo)obj1);
        }

        public void setMode(int i)
        {
            mMode = i;
        }

        private int mMode;

        ApplicationInfoComparator()
        {
        }
    }

    public class Loader
    {
        private class LoaderThread extends Thread
        {

            private void bindWorkspace()
            {
                final long t = SystemClock.uptimeMillis();
                final Callbacks oldCallbacks = (Callbacks)mCallbacks.get();
                if(oldCallbacks == null)
                {
                    Log.w("Launcher.Model", "LoaderThread running with no launcher");
                } else
                {
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                            {
                                callbacks.startBinding();
                                mIsBusy = false;
                            }
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    int i = mItems.size();
                    int j = 0;
                    while(j < i) 
                    {
                        final int start = j;
                        final int chunkSize;
                        if(j + 6 <= i)
                            chunkSize = 6;
                        else
                            chunkSize = i - j;
                        mHandler.post(new Runnable() {

                            public void run()
                            {
                                Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                                if(callbacks != null)
                                    callbacks.bindItems(mItems, start, start + chunkSize);
                            }

                            final LoaderThread this$2;
                            final int val$chunkSize;
                            final Callbacks val$oldCallbacks;
                            final int val$start;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        start = i;
                        chunkSize = j;
                        super();
                    }
                        }
);
                        j += 6;
                    }
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                                callbacks.bindFolders(mFolders);
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Log.d("Launcher.Model", "Going to start binding widgets soon.");
                        }

                        final LoaderThread this$2;

                    
                    {
                        this$2 = LoaderThread.this;
                        super();
                    }
                    }
);
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                                callbacks.finishBindingShortcuts();
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    int k = oldCallbacks.getCurrentWorkspaceScreen();
                    int l = mAppWidgets.size();
                    for(int i1 = 0; i1 < l; i1++)
                    {
                        final LauncherAppWidgetInfo widget = (LauncherAppWidgetInfo)mAppWidgets.get(i1);
                        if(((ItemInfo) (widget)).screen == k)
                            mHandler.post(new Runnable() {

                                public void run()
                                {
                                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                                    if(callbacks != null)
                                        callbacks.bindAppWidget(widget);
                                }

                                final LoaderThread this$2;
                                final Callbacks val$oldCallbacks;
                                final LauncherAppWidgetInfo val$widget;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        widget = launcherappwidgetinfo;
                        super();
                    }
                            }
);
                    }

                    for(int j1 = 0; j1 < l; j1++)
                    {
                        final LauncherAppWidgetInfo widget = (LauncherAppWidgetInfo)mAppWidgets.get(j1);
                        if(((ItemInfo) (widget)).screen != k)
                            mHandler.post(new Runnable() {

                                public void run()
                                {
                                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                                    if(callbacks != null)
                                        callbacks.bindAppWidget(widget);
                                }

                                final LoaderThread this$2;
                                final Callbacks val$oldCallbacks;
                                final LauncherAppWidgetInfo val$widget;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        widget = launcherappwidgetinfo;
                        super();
                    }
                            }
);
                    }

                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                                callbacks.finishBindingAppWidgets();
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    int k1 = mSamsungAppWidgets.size();
                    for(int l1 = 0; l1 < k1; l1++)
                    {
                        final SamsungAppWidgetInfo widget = (SamsungAppWidgetInfo)mSamsungAppWidgets.get(l1);
                        if(((ItemInfo) (widget)).screen == k)
                            mHandler.post(new Runnable() {

                                public void run()
                                {
                                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                                    if(callbacks != null)
                                        callbacks.bindSamsungAppWidget(widget);
                                }

                                final LoaderThread this$2;
                                final Callbacks val$oldCallbacks;
                                final SamsungAppWidgetInfo val$widget;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        widget = samsungappwidgetinfo;
                        super();
                    }
                            }
);
                    }

                    for(int i2 = 0; i2 < k1; i2++)
                    {
                        final SamsungAppWidgetInfo widget = (SamsungAppWidgetInfo)mSamsungAppWidgets.get(i2);
                        if(((ItemInfo) (widget)).screen != k)
                            mHandler.post(new Runnable() {

                                public void run()
                                {
                                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                                    if(callbacks != null)
                                        callbacks.bindSamsungAppWidget(widget);
                                }

                                final LoaderThread this$2;
                                final Callbacks val$oldCallbacks;
                                final SamsungAppWidgetInfo val$widget;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        widget = samsungappwidgetinfo;
                        super();
                    }
                            }
);
                    }

                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                                callbacks.finishBindingSamsungWidgets();
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                            {
                                callbacks.finishBindingAllWorkspaceItems();
                                mIsBusy = false;
                            }
                        }

                        final LoaderThread this$2;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        super();
                    }
                    }
);
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            Log.d("Launcher.Model", (new StringBuilder()).append("bound workspace in ").append(SystemClock.uptimeMillis() - t).append("ms").toString());
                        }

                        final LoaderThread this$2;
                        final long val$t;

                    
                    {
                        this$2 = LoaderThread.this;
                        t = l;
                        super();
                    }
                    }
);
                }
            }

            private boolean checkItemPlacement(ItemInfo aiteminfo[][][], ItemInfo iteminfo)
            {
                if(iteminfo.container == -100L) goto _L2; else goto _L1
_L1:
                boolean flag = true;
_L4:
                return flag;
_L2:
label0:
                for(int i = iteminfo.cellX; i < iteminfo.cellX + iteminfo.spanX; i++)
                {
                    int l = iteminfo.cellY;
                    do
                    {
                        if(l >= iteminfo.cellY + iteminfo.spanY)
                            continue label0;
                        if(aiteminfo[iteminfo.screen][i][l] != null)
                        {
                            Log.e("Launcher.Model", (new StringBuilder()).append("Error loading shortcut ").append(iteminfo).append(" into cell (").append(iteminfo.screen).append(":").append(i).append(",").append(l).append(") occupied by ").append(aiteminfo[iteminfo.screen][i][l]).toString());
                            flag = false;
                            continue; /* Loop/switch isn't completed */
                        }
                        l++;
                    } while(true);
                }

                for(int j = iteminfo.cellX; j < iteminfo.cellX + iteminfo.spanX; j++)
                {
                    for(int k = iteminfo.cellY; k < iteminfo.cellY + iteminfo.spanY; k++)
                        aiteminfo[iteminfo.screen][j][k] = iteminfo;

                }

                flag = true;
                if(true) goto _L4; else goto _L3
_L3:
            }

            private void loadAllAppsByBatch()
            {
                long l;
                final Callbacks oldCallbacks;
                l = SystemClock.uptimeMillis();
                oldCallbacks = (Callbacks)mCallbacks.get();
                if(oldCallbacks != null) goto _L2; else goto _L1
_L1:
                Log.w("Launcher.Model", "LoaderThread running with no launcher (loadAllAppsByBatch)");
_L7:
                return;
_L2:
                Intent intent;
                PackageManager packagemanager;
                ArrayList arraylist;
                int i;
                List list;
                int j;
                int k;
                Cursor cursor;
                intent = new Intent("android.intent.action.MAIN", null);
                intent.addCategory("android.intent.category.LAUNCHER");
                packagemanager = mContext.getPackageManager();
                arraylist = new ArrayList();
                i = 0x7fffffff;
                list = null;
                j = -1;
                k = 0;
                cursor = null;
_L22:
                if(k >= i || mStopped) goto _L4; else goto _L3
_L3:
                Object obj = mAllAppsListLock;
                obj;
                JVM INSTR monitorenter ;
                if(k != 0) goto _L6; else goto _L5
_L5:
                List list2;
                mAllAppsList.clear();
                long l4 = SystemClock.uptimeMillis();
                list2 = packagemanager.queryIntentActivities(intent, 0);
                Log.d("Launcher.Model", (new StringBuilder()).append("queryIntentActivities took ").append(SystemClock.uptimeMillis() - l4).append("ms").toString());
                if(list2 != null) goto _L8; else goto _L7
_L8:
                break MISSING_BLOCK_LABEL_207;
                Exception exception;
                exception;
                throw exception;
                int i4;
                i4 = list2.size();
                Log.d("Launcher.Model", (new StringBuilder()).append("queryIntentActivities got ").append(i4).append(" apps").toString());
                if(i4 != 0) goto _L10; else goto _L9
_L9:
                obj;
                JVM INSTR monitorexit ;
                  goto _L7
_L10:
                List list1;
                Cursor cursor1;
                int i1;
                int j1;
                long l1;
                int k1;
                int i2;
                mBeforeFirstQuery = false;
                int j4;
                if(mBatchSize == 0)
                    j4 = i4;
                else
                    j4 = mBatchSize;
                cursor1 = LauncherModel.loadAppToDatabase(mContext);
                list1 = list2;
                j1 = j4;
                i1 = i4;
_L25:
                l1 = SystemClock.uptimeMillis();
                k1 = 0;
                i2 = k;
_L27:
                if(i2 >= i1 || k1 >= j1) goto _L12; else goto _L11
_L11:
                ApplicationInfo applicationinfo1 = new ApplicationInfo((ResolveInfo)list1.get(i2), mIconCache);
                if(((ResolveInfo)list1.get(i2)).activityInfo.packageName.equals("com.android.stk"))
                {
                    if(applicationinfo1.title == null || applicationinfo1.title == "" || applicationinfo1.title.length() == 0)
                        Log.d("Launcher.Model", "STK have no title, Remove stk in launcher");
                    else
                        mAllAppsList.add(applicationinfo1);
                } else
                {
                    mAllAppsList.add(applicationinfo1);
                }
                  goto _L13
_L12:
                if(cursor1 == null || !cursor1.moveToFirst()) goto _L15; else goto _L14
_L14:
                long l2;
                ComponentName componentname;
                int j2;
                int k2;
                int i3;
                int j3;
                int k3;
                l2 = cursor1.getLong(0);
                componentname = ComponentName.unflattenFromString(cursor1.getString(1));
                j2 = cursor1.getInt(2);
                k2 = cursor1.getInt(3);
                i3 = cursor1.getInt(4);
                j3 = mAllAppsList.size();
                k3 = 0;
_L23:
                if(k3 >= j3) goto _L17; else goto _L16
_L16:
                ApplicationInfo applicationinfo = mAllAppsList.get(k3);
                if(applicationinfo.intent == null || !componentname.equals(applicationinfo.intent.getComponent())) goto _L19; else goto _L18
_L18:
                boolean flag;
                applicationinfo.topNum = j2;
                applicationinfo.pageNum = k2;
                applicationinfo.cellNum = i3;
                applicationinfo.editTopNum = j2;
                applicationinfo.editPageNum = k2;
                applicationinfo.editCellNum = i3;
                applicationinfo.isUpdated = true;
                Log.d("Launcher.Model", (new StringBuilder()).append("ApplicationInfo title=").append(applicationinfo.title).append(" top=").append(applicationinfo.topNum).append(" page=").append(applicationinfo.pageNum).append(" cell=").append(applicationinfo.cellNum).toString());
                flag = true;
_L26:
                if(!flag)
                    arraylist.add(Long.toString(l2));
                if(cursor1.moveToNext()) goto _L14; else goto _L15
_L24:
                final boolean first;
                final ArrayList added = mAllAppsList.added;
                mAllAppsList.added = new ArrayList();
                DeferredHandler deferredhandler = mHandler;
                Runnable runnable = new Runnable() {

                    public void run()
                    {
                        long l5 = SystemClock.uptimeMillis();
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if(callbacks != null)
                        {
                            if(first)
                            {
                                mBeforeFirstLoad = false;
                                callbacks.bindAllApplications(added);
                            } else
                            {
                                callbacks.bindAppsAdded(added);
                            }
                            Log.d("Launcher.Model", (new StringBuilder()).append("bound ").append(added.size()).append(" apps in ").append(SystemClock.uptimeMillis() - l5).append("ms").toString());
                        } else
                        {
                            Log.i("Launcher.Model", "not binding apps: no Launcher activity");
                        }
                    }

                    final LoaderThread this$2;
                    final ArrayList val$added;
                    final boolean val$first;
                    final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        first = flag;
                        added = arraylist;
                        super();
                    }
                }
;
                deferredhandler.post(runnable);
                Log.d("Launcher.Model", (new StringBuilder()).append("batch of ").append(i2 - k).append(" icons processed in ").append(SystemClock.uptimeMillis() - l1).append("ms").toString());
                obj;
                JVM INSTR monitorexit ;
                if(mAllAppsLoadDelay <= 0 || i2 >= i1) goto _L21; else goto _L20
_L20:
                Log.d("Launcher.Model", (new StringBuilder()).append("sleeping for ").append(mAllAppsLoadDelay).append("ms").toString());
                Thread.sleep(mAllAppsLoadDelay);
                j = j1;
                k = i2;
                i = i1;
                cursor = cursor1;
                list = list1;
                  goto _L22
_L19:
                k3++;
                  goto _L23
_L29:
                first = false;
                  goto _L24
                InterruptedException interruptedexception;
                interruptedexception;
                j = j1;
                k = i2;
                i = i1;
                cursor = cursor1;
                list = list1;
                  goto _L22
_L4:
                if(cursor != null)
                    cursor.close();
                if(!mStopped)
                {
                    String s1;
                    for(Iterator iterator = arraylist.iterator(); iterator.hasNext(); LauncherModel.removeAppToDatabase(mContext, s1))
                        s1 = (String)iterator.next();

                }
                StringBuilder stringbuilder = (new StringBuilder()).append("cached all ").append(i).append(" apps in ").append(SystemClock.uptimeMillis() - l).append("ms");
                String s;
                if(mAllAppsLoadDelay > 0)
                    s = " (including delay)";
                else
                    s = "";
                Log.d("Launcher.Model", stringbuilder.append(s).toString());
                  goto _L7
_L21:
                j = j1;
                k = i2;
                i = i1;
                cursor = cursor1;
                list = list1;
                  goto _L22
_L6:
                list1 = list;
                cursor1 = cursor;
                i1 = i;
                j1 = j;
                  goto _L25
_L17:
                flag = false;
                  goto _L26
_L13:
                int l3 = i2 + 1;
                k1++;
                i2 = l3;
                  goto _L27
_L15:
                if(i2 > j1) goto _L29; else goto _L28
_L28:
                first = true;
                  goto _L24
            }

            private void loadAndBindAllApps()
            {
                this;
                JVM INSTR monitorenter ;
                boolean flag;
                flag = mAllAppsLoaded;
                mAllAppsLoaded = true;
                Log.d("Launcher.Model", (new StringBuilder()).append("loadAndBindAllApps loaded=").append(flag).toString());
                if(flag) goto _L2; else goto _L1
_L1:
                loadAllAppsByBatch();
                if(!mStopped) goto _L4; else goto _L3
_L3:
                mAllAppsLoaded = false;
                  goto _L5
_L2:
                onlyBindAllApps();
_L4:
                this;
                JVM INSTR monitorexit ;
_L5:
            }

            private void loadAndBindWorkspace()
            {
                this;
                JVM INSTR monitorenter ;
                boolean flag;
                flag = mWorkspaceLoaded;
                mWorkspaceLoaded = true;
                this;
                JVM INSTR monitorexit ;
                Log.d("Launcher.Model", (new StringBuilder()).append("loadAndBindWorkspace loaded=").append(flag).toString());
                if(flag) goto _L2; else goto _L1
_L1:
                loadWorkspace();
                if(!mStopped) goto _L2; else goto _L3
_L3:
                mWorkspaceLoaded = false;
_L5:
                return;
                Exception exception;
                exception;
                this;
                JVM INSTR monitorexit ;
                throw exception;
_L2:
                bindWorkspace();
                if(true) goto _L5; else goto _L4
_L4:
            }

            private void loadWorkspace()
            {
                long l;
                Context context;
                ContentResolver contentresolver;
                PackageManager packagemanager;
                AppWidgetManager appwidgetmanager;
                boolean flag;
                HashMap hashmap;
                ArrayList arraylist;
                Cursor cursor;
                ItemInfo aiteminfo[][][];
                l = SystemClock.uptimeMillis();
                context = mContext;
                contentresolver = context.getContentResolver();
                packagemanager = context.getPackageManager();
                appwidgetmanager = AppWidgetManager.getInstance(context);
                flag = packagemanager.isSafeMode();
                hashmap = new HashMap();
                mItems.clear();
                for(Iterator iterator = mAppWidgets.iterator(); iterator.hasNext();)
                {
                    LauncherAppWidgetInfo launcherappwidgetinfo2 = (LauncherAppWidgetInfo)iterator.next();
                    if(launcherappwidgetinfo2.appWidgetId > -1 && launcherappwidgetinfo2.hostView != null)
                        hashmap.put(Integer.valueOf(launcherappwidgetinfo2.appWidgetId), launcherappwidgetinfo2.hostView);
                    launcherappwidgetinfo2.hostView = null;
                }

                mAppWidgets.clear();
                for(Iterator iterator1 = mSamsungAppWidgets.iterator(); iterator1.hasNext();)
                {
                    SamsungAppWidgetInfo samsungappwidgetinfo1 = (SamsungAppWidgetInfo)iterator1.next();
                    samsungappwidgetinfo1.intent = null;
                    samsungappwidgetinfo1.widgetView = null;
                }

                mSamsungAppWidgets.clear();
                mFolders.clear();
                arraylist = new ArrayList();
                cursor = contentresolver.query(LauncherSettings.Favorites.CONTENT_URI, null, null, null, null);
                int i = Launcher.SCREEN_COUNT;
                int j = Launcher.NUMBER_CELLS_X;
                int k = Launcher.NUMBER_CELLS_Y;
                int ai[] = new int[3];
                ai[0] = i;
                ai[1] = j;
                ai[2] = k;
                aiteminfo = (ItemInfo[][][])Array.newInstance(com/sec/android/app/twlauncher/ItemInfo, ai);
                int i1;
                int j1;
                int k1;
                int l1;
                int i2;
                int j2;
                int k2;
                int l2;
                int i3;
                int j3;
                int k3;
                int l3;
                int i4;
                int j4;
                int k4;
                int l4;
                int i5;
                i1 = cursor.getColumnIndexOrThrow("_id");
                j1 = cursor.getColumnIndexOrThrow("intent");
                k1 = cursor.getColumnIndexOrThrow("title");
                l1 = cursor.getColumnIndexOrThrow("iconType");
                i2 = cursor.getColumnIndexOrThrow("icon");
                j2 = cursor.getColumnIndexOrThrow("iconPackage");
                k2 = cursor.getColumnIndexOrThrow("iconResource");
                l2 = cursor.getColumnIndexOrThrow("container");
                i3 = cursor.getColumnIndexOrThrow("itemType");
                j3 = cursor.getColumnIndexOrThrow("appWidgetId");
                k3 = cursor.getColumnIndexOrThrow("screen");
                l3 = cursor.getColumnIndexOrThrow("cellX");
                i4 = cursor.getColumnIndexOrThrow("cellY");
                j4 = cursor.getColumnIndexOrThrow("spanX");
                k4 = cursor.getColumnIndexOrThrow("spanY");
                l4 = cursor.getColumnIndexOrThrow("uri");
                i5 = cursor.getColumnIndexOrThrow("displayMode");
_L30:
                if(mStopped) goto _L2; else goto _L1
_L1:
                boolean flag1 = cursor.moveToNext();
                if(!flag1) goto _L2; else goto _L3
_L3:
                int j7 = cursor.getInt(i3);
                j7;
                JVM INSTR tableswitch 0 5: default 2557
            //                           0 560
            //                           1 560
            //                           2 1035
            //                           3 1212
            //                           4 1512
            //                           5 1781;
                   goto _L4 _L5 _L5 _L6 _L7 _L8 _L9
_L5:
                String s4 = cursor.getString(j1);
                Intent intent3 = Intent.parseUri(s4, 0);
                if(j7 != 0) goto _L11; else goto _L10
_L10:
                ShortcutInfo shortcutinfo = getShortcutInfo(packagemanager, intent3, context, cursor, i2, k1);
_L19:
                if(shortcutinfo == null) goto _L13; else goto _L12
_L12:
                int i9;
                ResolveInfo resolveinfo1;
                boolean flag2;
                TelephonyManager telephonymanager;
                updateSavedIcon(context, shortcutinfo, cursor, i2);
                shortcutinfo.intent = intent3;
                long l13 = cursor.getLong(i1);
                shortcutinfo.id = l13;
                i9 = cursor.getInt(l2);
                long l14 = i9;
                shortcutinfo.container = l14;
                int j9 = cursor.getInt(k3);
                shortcutinfo.screen = j9;
                int k9 = cursor.getInt(l3);
                shortcutinfo.cellX = k9;
                int i10 = cursor.getInt(i4);
                shortcutinfo.cellY = i10;
                resolveinfo1 = packagemanager.resolveActivity(intent3, 0);
                flag2 = false;
                telephonymanager = (TelephonyManager)context.getSystemService("phone");
                if(!resolveinfo1.activityInfo.packageName.equals("com.android.stk")) goto _L15; else goto _L14
_L14:
                if(SystemProperties.get("gsm.SKT_SETUP_MENU").length() <= 0) goto _L17; else goto _L16
_L16:
                Log.w("Launcher.Model", "SIM enabled");
_L15:
                if(((ItemInfo) (shortcutinfo)).screen != -1 && !checkItemPlacement(aiteminfo, shortcutinfo)) goto _L4; else goto _L18
_L301:
                LauncherModel.findOrMakeUserFolder(mFolders, i9).add(shortcutinfo);
                  goto _L4
                Exception exception1;
                exception1;
                Log.w("Launcher.Model", "Desktop items loading interrupted:", exception1);
                  goto _L4
                Exception exception;
                exception;
                cursor.close();
                throw exception;
                URISyntaxException urisyntaxexception2;
                urisyntaxexception2;
                  goto _L4
_L11:
                shortcutinfo = getShortcutInfo(packagemanager, intent3, cursor, context, l1, j2, k2, i2, k1);
                  goto _L19
_L17:
                if(telephonymanager.getSimState() != 1) goto _L21; else goto _L20
_L20:
                flag2 = true;
_L25:
                if(!flag2) goto _L15; else goto _L22
_L22:
                arraylist.add(Long.valueOf(((ItemInfo) (shortcutinfo)).id));
                  goto _L4
_L21:
                if(telephonymanager.getSimState() != 5) goto _L24; else goto _L23
_L23:
                Log.w("Launcher.Model", "SIM Exists");
                  goto _L25
_L299:
                mItems.add(shortcutinfo);
                  goto _L4
_L13:
                long l12 = cursor.getLong(i1);
                Log.e("Launcher.Model", (new StringBuilder()).append("Error loading shortcut ").append(l12).append(", removing it").toString());
                contentresolver.delete(LauncherSettings.Favorites.getContentUri(l12, false), null, null);
                  goto _L4
_L6:
                UserFolderInfo userfolderinfo;
                int k8;
                long l11 = cursor.getLong(i1);
                userfolderinfo = LauncherModel.findOrMakeUserFolder(mFolders, l11);
                userfolderinfo.title = cursor.getString(k1);
                userfolderinfo.id = l11;
                k8 = cursor.getInt(l2);
                userfolderinfo.container = k8;
                userfolderinfo.screen = cursor.getInt(k3);
                userfolderinfo.cellX = cursor.getInt(l3);
                userfolderinfo.cellY = cursor.getInt(i4);
                if(!checkItemPlacement(aiteminfo, userfolderinfo)) goto _L4; else goto _L26
_L26:
                k8;
                JVM INSTR tableswitch -100 -100: default 1172
            //                           -100 1196;
                   goto _L27 _L28
_L32:
                if(true) goto _L30; else goto _L29
_L29:
_L34:
                if(true) goto _L32; else goto _L31
_L31:
_L36:
                if(true) goto _L34; else goto _L33
_L33:
_L38:
                if(true) goto _L36; else goto _L35
_L35:
_L40:
                if(true) goto _L38; else goto _L37
_L37:
_L42:
                if(true) goto _L40; else goto _L39
_L39:
_L44:
                if(true) goto _L42; else goto _L41
_L41:
_L46:
                if(true) goto _L44; else goto _L43
_L43:
_L48:
                if(true) goto _L46; else goto _L45
_L45:
_L50:
                if(true) goto _L48; else goto _L47
_L47:
_L52:
                if(true) goto _L50; else goto _L49
_L49:
_L54:
                if(true) goto _L52; else goto _L51
_L51:
_L56:
                if(true) goto _L54; else goto _L53
_L53:
_L58:
                if(true) goto _L56; else goto _L55
_L55:
_L60:
                if(true) goto _L58; else goto _L57
_L57:
_L62:
                if(true) goto _L60; else goto _L59
_L59:
_L64:
                if(true) goto _L62; else goto _L61
_L61:
_L66:
                if(true) goto _L64; else goto _L63
_L63:
_L68:
                if(true) goto _L66; else goto _L65
_L65:
_L70:
                if(true) goto _L68; else goto _L67
_L67:
_L72:
                if(true) goto _L70; else goto _L69
_L69:
_L74:
                if(true) goto _L72; else goto _L71
_L71:
_L76:
                if(true) goto _L74; else goto _L73
_L73:
_L78:
                if(true) goto _L76; else goto _L75
_L75:
_L80:
                if(true) goto _L78; else goto _L77
_L77:
_L82:
                if(true) goto _L80; else goto _L79
_L79:
_L84:
                if(true) goto _L82; else goto _L81
_L81:
_L86:
                if(true) goto _L84; else goto _L83
_L83:
_L88:
                if(true) goto _L86; else goto _L85
_L85:
_L90:
                if(true) goto _L88; else goto _L87
_L87:
_L92:
                if(true) goto _L90; else goto _L89
_L89:
_L94:
                if(true) goto _L92; else goto _L91
_L91:
_L96:
                if(true) goto _L94; else goto _L93
_L93:
_L98:
                if(true) goto _L96; else goto _L95
_L95:
_L100:
                if(true) goto _L98; else goto _L97
_L97:
_L102:
                if(true) goto _L100; else goto _L99
_L99:
_L104:
                if(true) goto _L102; else goto _L101
_L101:
_L106:
                if(true) goto _L104; else goto _L103
_L103:
_L108:
                if(true) goto _L106; else goto _L105
_L105:
_L110:
                if(true) goto _L108; else goto _L107
_L107:
_L112:
                if(true) goto _L110; else goto _L109
_L109:
_L114:
                if(true) goto _L112; else goto _L111
_L111:
_L116:
                if(true) goto _L114; else goto _L113
_L113:
_L118:
                if(true) goto _L116; else goto _L115
_L115:
_L120:
                if(true) goto _L118; else goto _L117
_L117:
_L122:
                if(true) goto _L120; else goto _L119
_L119:
_L124:
                if(true) goto _L122; else goto _L121
_L121:
_L126:
                if(true) goto _L124; else goto _L123
_L123:
_L128:
                if(true) goto _L126; else goto _L125
_L125:
_L130:
                if(true) goto _L128; else goto _L127
_L127:
_L132:
                if(true) goto _L130; else goto _L129
_L129:
_L134:
                if(true) goto _L132; else goto _L131
_L131:
_L136:
                if(true) goto _L134; else goto _L133
_L133:
_L138:
                if(true) goto _L136; else goto _L135
_L135:
_L140:
                if(true) goto _L138; else goto _L137
_L137:
_L142:
                if(true) goto _L140; else goto _L139
_L139:
_L144:
                if(true) goto _L142; else goto _L141
_L141:
_L146:
                if(true) goto _L144; else goto _L143
_L143:
_L148:
                if(true) goto _L146; else goto _L145
_L145:
_L150:
                if(true) goto _L148; else goto _L147
_L147:
_L152:
                if(true) goto _L150; else goto _L149
_L149:
_L154:
                if(true) goto _L152; else goto _L151
_L151:
_L156:
                if(true) goto _L154; else goto _L153
_L153:
_L158:
                if(true) goto _L156; else goto _L155
_L155:
_L160:
                if(true) goto _L158; else goto _L157
_L157:
_L162:
                if(true) goto _L160; else goto _L159
_L159:
_L164:
                if(true) goto _L162; else goto _L161
_L161:
_L4:
                if(true) goto _L164; else goto _L163
_L163:
_L27:
                mFolders.put(Long.valueOf(((ItemInfo) (userfolderinfo)).id), userfolderinfo);
                  goto _L30
_L28:
                mItems.add(userfolderinfo);
                  goto _L27
_L7:
                long l10;
                Uri uri;
                l10 = cursor.getLong(i1);
                uri = Uri.parse(cursor.getString(l4));
                if(context.getPackageManager().resolveContentProvider(uri.getAuthority(), 0) != null || flag) goto _L166; else goto _L165
_L165:
                arraylist.add(Long.valueOf(l10));
                  goto _L30
_L166:
                LiveFolderInfo livefolderinfo;
                String s3;
                livefolderinfo = LauncherModel.findOrMakeLiveFolder(mFolders, l10);
                s3 = cursor.getString(j1);
                Intent intent1;
                intent1 = null;
                if(s3 == null)
                    break MISSING_BLOCK_LABEL_1317;
                Intent intent2 = Intent.parseUri(s3, 0);
                intent1 = intent2;
_L297:
                int j8;
                livefolderinfo.title = cursor.getString(k1);
                livefolderinfo.id = l10;
                livefolderinfo.uri = uri;
                j8 = cursor.getInt(l2);
                livefolderinfo.container = j8;
                livefolderinfo.screen = cursor.getInt(k3);
                livefolderinfo.cellX = cursor.getInt(l3);
                livefolderinfo.cellY = cursor.getInt(i4);
                livefolderinfo.baseIntent = intent1;
                livefolderinfo.displayMode = cursor.getInt(i5);
                if(!checkItemPlacement(aiteminfo, livefolderinfo)) goto _L30; else goto _L167
_L167:
                LauncherModel.loadLiveFolderIcon(context, cursor, l1, j2, k2, livefolderinfo);
                j8;
                JVM INSTR tableswitch -100 -100: default 1472
            //                           -100 1496;
                   goto _L168 _L169
_L172:
                if(true) goto _L30; else goto _L170
_L170:
_L174:
                if(true) goto _L172; else goto _L171
_L171:
_L176:
                if(true) goto _L174; else goto _L173
_L173:
_L178:
                if(true) goto _L176; else goto _L175
_L175:
_L180:
                if(true) goto _L178; else goto _L177
_L177:
_L182:
                if(true) goto _L180; else goto _L179
_L179:
_L184:
                if(true) goto _L182; else goto _L181
_L181:
_L186:
                if(true) goto _L184; else goto _L183
_L183:
_L188:
                if(true) goto _L186; else goto _L185
_L185:
_L190:
                if(true) goto _L188; else goto _L187
_L187:
_L192:
                if(true) goto _L190; else goto _L189
_L189:
_L194:
                if(true) goto _L192; else goto _L191
_L191:
_L196:
                if(true) goto _L194; else goto _L193
_L193:
_L198:
                if(true) goto _L196; else goto _L195
_L195:
_L200:
                if(true) goto _L198; else goto _L197
_L197:
_L202:
                if(true) goto _L200; else goto _L199
_L199:
_L204:
                if(true) goto _L202; else goto _L201
_L201:
_L206:
                if(true) goto _L204; else goto _L203
_L203:
_L208:
                if(true) goto _L206; else goto _L205
_L205:
_L210:
                if(true) goto _L208; else goto _L207
_L207:
_L212:
                if(true) goto _L210; else goto _L209
_L209:
_L214:
                if(true) goto _L212; else goto _L211
_L211:
_L216:
                if(true) goto _L214; else goto _L213
_L213:
_L218:
                if(true) goto _L216; else goto _L215
_L215:
_L220:
                if(true) goto _L218; else goto _L217
_L217:
_L222:
                if(true) goto _L220; else goto _L219
_L219:
_L224:
                if(true) goto _L222; else goto _L221
_L221:
_L226:
                if(true) goto _L224; else goto _L223
_L223:
_L228:
                if(true) goto _L226; else goto _L225
_L225:
_L230:
                if(true) goto _L228; else goto _L227
_L227:
_L232:
                if(true) goto _L230; else goto _L229
_L229:
_L234:
                if(true) goto _L232; else goto _L231
_L231:
_L236:
                if(true) goto _L234; else goto _L233
_L233:
_L238:
                if(true) goto _L236; else goto _L235
_L235:
_L240:
                if(true) goto _L238; else goto _L237
_L237:
_L242:
                if(true) goto _L240; else goto _L239
_L239:
_L244:
                if(true) goto _L242; else goto _L241
_L241:
_L246:
                if(true) goto _L244; else goto _L243
_L243:
_L248:
                if(true) goto _L246; else goto _L245
_L245:
_L250:
                if(true) goto _L248; else goto _L247
_L247:
_L252:
                if(true) goto _L250; else goto _L249
_L249:
_L254:
                if(true) goto _L252; else goto _L251
_L251:
_L256:
                if(true) goto _L254; else goto _L253
_L253:
_L258:
                if(true) goto _L256; else goto _L255
_L255:
_L260:
                if(true) goto _L258; else goto _L257
_L257:
_L262:
                if(true) goto _L260; else goto _L259
_L259:
_L264:
                if(true) goto _L262; else goto _L261
_L261:
_L266:
                if(true) goto _L264; else goto _L263
_L263:
_L268:
                if(true) goto _L266; else goto _L265
_L265:
_L270:
                if(true) goto _L268; else goto _L267
_L267:
_L272:
                if(true) goto _L270; else goto _L269
_L269:
_L274:
                if(true) goto _L272; else goto _L271
_L271:
_L276:
                if(true) goto _L274; else goto _L273
_L273:
_L278:
                if(true) goto _L276; else goto _L275
_L275:
_L280:
                if(true) goto _L278; else goto _L277
_L277:
_L282:
                if(true) goto _L280; else goto _L279
_L279:
_L284:
                if(true) goto _L282; else goto _L281
_L281:
_L286:
                if(true) goto _L284; else goto _L283
_L283:
_L288:
                if(true) goto _L286; else goto _L285
_L285:
_L290:
                if(true) goto _L288; else goto _L287
_L287:
_L292:
                if(true) goto _L290; else goto _L289
_L289:
                if(true) goto _L292; else goto _L291
_L291:
_L168:
                mFolders.put(Long.valueOf(((ItemInfo) (livefolderinfo)).id), livefolderinfo);
                  goto _L30
_L169:
                mItems.add(livefolderinfo);
                  goto _L168
_L8:
                int i8 = cursor.getInt(j3);
                long l9 = cursor.getInt(i1);
                AppWidgetProviderInfo appwidgetproviderinfo = appwidgetmanager.getAppWidgetInfo(i8);
                if(!flag && (appwidgetproviderinfo == null || appwidgetproviderinfo.provider == null || appwidgetproviderinfo.provider.getPackageName() == null))
                {
                    Log.e("Launcher.Model", (new StringBuilder()).append("Deleting widget that isn't installed anymore: id=").append(l9).append(" appWidgetId=").append(i8).toString());
                    arraylist.add(Long.valueOf(l9));
                } else
                {
                    LauncherAppWidgetInfo launcherappwidgetinfo1 = new LauncherAppWidgetInfo(i8);
                    launcherappwidgetinfo1.id = l9;
                    launcherappwidgetinfo1.screen = cursor.getInt(k3);
                    launcherappwidgetinfo1.cellX = cursor.getInt(l3);
                    launcherappwidgetinfo1.cellY = cursor.getInt(i4);
                    launcherappwidgetinfo1.spanX = cursor.getInt(j4);
                    launcherappwidgetinfo1.spanY = cursor.getInt(k4);
                    if(cursor.getInt(l2) != -100)
                    {
                        Log.e("Launcher.Model", "Widget found where container != CONTAINER_DESKTOP -- ignoring!");
                    } else
                    {
                        launcherappwidgetinfo1.container = cursor.getInt(l2);
                        if(checkItemPlacement(aiteminfo, launcherappwidgetinfo1))
                            mAppWidgets.add(launcherappwidgetinfo1);
                    }
                }
                  goto _L30
_L9:
                long l8;
                SamsungAppWidgetInfo samsungappwidgetinfo;
                int k7;
                l8 = cursor.getLong(i1);
                samsungappwidgetinfo = new SamsungAppWidgetInfo();
                samsungappwidgetinfo.id = l8;
                samsungappwidgetinfo.screen = cursor.getInt(k3);
                samsungappwidgetinfo.cellX = cursor.getInt(l3);
                samsungappwidgetinfo.cellY = cursor.getInt(i4);
                samsungappwidgetinfo.spanX = cursor.getInt(j4);
                samsungappwidgetinfo.spanY = cursor.getInt(k4);
                samsungappwidgetinfo.widgetId = cursor.getInt(j3);
                k7 = cursor.getInt(l2);
                if(k7 == -100) goto _L294; else goto _L293
_L293:
                Log.e("Launcher.Model", "SamsungWidget found where container != CONTAINER_DESKTOP -- ignoring!");
                  goto _L30
_L294:
                String s2;
                samsungappwidgetinfo.container = k7;
                s2 = cursor.getString(j1);
                Intent intent = Intent.parseUri(s2, 0);
                ResolveInfo resolveinfo;
                samsungappwidgetinfo.intent = intent;
                resolveinfo = packagemanager.resolveActivity(intent, 0);
                if(resolveinfo != null) goto _L296; else goto _L295
_L295:
                Log.e("Launcher.Model", (new StringBuilder()).append("Can't resolve SamsungWidget's activity. Deleting it. id:").append(l8).toString());
                arraylist.add(Long.valueOf(l8));
                  goto _L30
                URISyntaxException urisyntaxexception;
                urisyntaxexception;
                  goto _L30
_L296:
                packagemanager.getApplicationInfo(resolveinfo.activityInfo.packageName, 0);
                samsungappwidgetinfo.packageName = resolveinfo.activityInfo.packageName;
                if(checkItemPlacement(aiteminfo, samsungappwidgetinfo))
                    mSamsungAppWidgets.add(samsungappwidgetinfo);
                  goto _L30
                android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
                namenotfoundexception;
                Log.e("Launcher.Model", (new StringBuilder()).append("SamsungWidget's apk has move to sdcard and unmounted. Deleting it. id:").append(l8).toString());
                arraylist.add(Long.valueOf(l8));
                  goto _L30
_L2:
                cursor.close();
                if(arraylist.size() > 0)
                {
                    ContentProviderClient contentproviderclient = contentresolver.acquireContentProviderClient(LauncherSettings.Favorites.CONTENT_URI);
                    int l6 = 0;
                    int i7 = arraylist.size();
                    while(l6 < i7) 
                    {
                        long l7 = ((Long)arraylist.get(l6)).longValue();
                        Log.d("Launcher.Model", (new StringBuilder()).append("Removed id = ").append(l7).toString());
                        try
                        {
                            contentproviderclient.delete(LauncherSettings.Favorites.getContentUri(l7, false), null, null);
                        }
                        catch(RemoteException remoteexception)
                        {
                            Log.w("Launcher.Model", (new StringBuilder()).append("Could not remove id = ").append(l7).toString());
                        }
                        l6++;
                    }
                }
                Iterator iterator2 = mAppWidgets.iterator();
                do
                {
                    if(!iterator2.hasNext())
                        break;
                    LauncherAppWidgetInfo launcherappwidgetinfo = (LauncherAppWidgetInfo)iterator2.next();
                    if(launcherappwidgetinfo.appWidgetId > -1)
                        launcherappwidgetinfo.hostView = (AppWidgetHostView)hashmap.get(Integer.valueOf(launcherappwidgetinfo.appWidgetId));
                } while(true);
                hashmap.clear();
                Log.d("Launcher.Model", (new StringBuilder()).append("loaded workspace in ").append(SystemClock.uptimeMillis() - l).append("ms").toString());
                Log.d("Launcher.Model", "workspace layout: ");
                int j5 = 0;
                do
                {
                    int k5 = Launcher.NUMBER_CELLS_Y;
                    URISyntaxException urisyntaxexception1;
                    if(j5 < k5)
                    {
                        String s = "";
                        int l5 = 0;
                        do
                        {
                            int i6 = Launcher.SCREEN_COUNT;
                            if(l5 >= i6)
                                break;
                            if(l5 > 0)
                                s = (new StringBuilder()).append(s).append(" | ").toString();
                            int j6 = 0;
                            do
                            {
                                int k6 = Launcher.NUMBER_CELLS_X;
                                if(j6 >= k6)
                                    break;
                                StringBuilder stringbuilder = (new StringBuilder()).append(s);
                                String s1;
                                if(aiteminfo[l5][j6][j5] != null)
                                    s1 = "#";
                                else
                                    s1 = ".";
                                s = stringbuilder.append(s1).toString();
                                j6++;
                            } while(true);
                            l5++;
                        } while(true);
                        Log.d("Launcher.Model", (new StringBuilder()).append("[ ").append(s).append(" ]").toString());
                        j5++;
                    } else
                    {
                        return;
                    }
                } while(true);
                urisyntaxexception1;
                  goto _L297
_L18:
                i9;
                JVM INSTR tableswitch -100 -100: default 818
            //                           -100 959;
                   goto _L298 _L299
_L298:
                if(true) goto _L301; else goto _L300
_L300:
_L24:
                flag2 = true;
                  goto _L25
            }

            private void onlyBindAllApps()
            {
                final Callbacks oldCallbacks = (Callbacks)mCallbacks.get();
                if(oldCallbacks == null)
                {
                    Log.w("Launcher.Model", "LoaderThread running with no launcher (onlyBindAllApps)");
                } else
                {
                    final ArrayList list = (ArrayList)mAllAppsList.data.clone();
                    mHandler.post(new Runnable() {

                        public void run()
                        {
                            long l = SystemClock.uptimeMillis();
                            Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                            if(callbacks != null)
                                callbacks.bindAllApplications(list);
                            Log.d("Launcher.Model", (new StringBuilder()).append("bound all ").append(list.size()).append(" apps from cache in ").append(SystemClock.uptimeMillis() - l).append("ms").toString());
                        }

                        final LoaderThread this$2;
                        final ArrayList val$list;
                        final Callbacks val$oldCallbacks;

                    
                    {
                        this$2 = LoaderThread.this;
                        oldCallbacks = callbacks;
                        list = arraylist;
                        super();
                    }
                    }
);
                }
            }

            private void waitForOtherThread()
            {
                if(mWaitThread == null) goto _L2; else goto _L1
_L1:
                boolean flag = false;
_L4:
                if(flag)
                    break MISSING_BLOCK_LABEL_25;
                mWaitThread.join();
                flag = true;
                continue; /* Loop/switch isn't completed */
                mWaitThread = null;
_L2:
                return;
                InterruptedException interruptedexception;
                interruptedexception;
                if(true) goto _L4; else goto _L3
_L3:
            }

            boolean isLaunching()
            {
                return mIsLaunching;
            }

            public void run()
            {
                Object obj;
                int i;
                waitForOtherThread();
                Callbacks callbacks = (Callbacks)mCallbacks.get();
                boolean flag;
                if(callbacks != null)
                {
                    if(!callbacks.isAllAppsVisible())
                        flag = true;
                    else
                        flag = false;
                } else
                {
                    flag = true;
                }
                obj = mLock;
                obj;
                JVM INSTR monitorenter ;
                if(!mIsLaunching)
                    break MISSING_BLOCK_LABEL_194;
                i = 0;
_L1:
                Process.setThreadPriority(i);
                Exception exception;
                if(flag)
                {
                    Log.d("Launcher.Model", "step 1: loading workspace");
                    loadAndBindWorkspace();
                } else
                {
                    Log.d("Launcher.Model", "step 1: special: loading all apps");
                    loadAndBindAllApps();
                }
                synchronized(mLock)
                {
                    if(mIsLaunching)
                        Process.setThreadPriority(10);
                }
                if(flag)
                {
                    Log.d("Launcher.Model", "step 2: loading all apps");
                    loadAndBindAllApps();
                } else
                {
                    Log.d("Launcher.Model", "step 2: special: loading workspace");
                    loadAndBindWorkspace();
                }
                mContext = null;
                synchronized(mLock)
                {
                    mLoaderThread = null;
                }
                mHandler.post(new Runnable() {

                    public void run()
                    {
                        System.gc();
                    }

                    final LoaderThread this$2;

                    
                    {
                        this$2 = LoaderThread.this;
                        super();
                    }
                }
);
                return;
                i = 10;
                  goto _L1
                exception;
                obj;
                JVM INSTR monitorexit ;
                throw exception;
                exception1;
                obj1;
                JVM INSTR monitorexit ;
                throw exception1;
                exception2;
                obj2;
                JVM INSTR monitorexit ;
                throw exception2;
            }

            public void stopLocked()
            {
                this;
                JVM INSTR monitorenter ;
                mStopped = true;
                notify();
                return;
            }

            Callbacks tryGetCallbacks(Callbacks callbacks)
            {
                Object obj = mLock;
                obj;
                JVM INSTR monitorenter ;
                Callbacks callbacks2;
                if(mStopped)
                    callbacks2 = null;
                else
                if(mCallbacks == null)
                {
                    callbacks2 = null;
                } else
                {
                    Callbacks callbacks1 = (Callbacks)mCallbacks.get();
                    if(callbacks1 != callbacks)
                        callbacks2 = null;
                    else
                    if(callbacks1 == null)
                    {
                        Log.w("Launcher.Model", "no mCallbacks");
                        callbacks2 = null;
                    } else
                    {
                        callbacks2 = callbacks1;
                    }
                }
                return callbacks2;
            }

            private Context mContext;
            private boolean mIsLaunching;
            private boolean mStopped;
            private Thread mWaitThread;
            final Loader this$1;

            LoaderThread(Context context, Thread thread, boolean flag)
            {
                this$1 = Loader.this;
                super();
                mContext = context;
                mWaitThread = thread;
                mIsLaunching = flag;
            }
        }


        public void startLoader(Context context, boolean flag)
        {
            Object obj = mLock;
            obj;
            JVM INSTR monitorenter ;
            Log.d("Launcher.Model", (new StringBuilder()).append("startLoader isLaunching=").append(flag).toString());
            if(mCallbacks != null && mCallbacks.get() != null)
            {
                mIsBusy = true;
                LoaderThread loaderthread = mLoaderThread;
                if(loaderthread != null)
                {
                    if(loaderthread.isLaunching())
                        flag = true;
                    loaderthread.stopLocked();
                }
                mLoaderThread = new LoaderThread(context, loaderthread, flag);
                mLoaderThread.start();
            }
            return;
        }

        public void stopLoader()
        {
            Object obj = mLock;
            obj;
            JVM INSTR monitorenter ;
            if(mLoaderThread != null)
                mLoaderThread.stopLocked();
            return;
        }

        final ArrayList mAppWidgets = new ArrayList();
        final HashMap mFolders = new HashMap();
        final ArrayList mItems = new ArrayList();
        private LoaderThread mLoaderThread;
        final ArrayList mSamsungAppWidgets = new ArrayList();
        final LauncherModel this$0;


/*
        static LoaderThread access$602(Loader loader, LoaderThread loaderthread)
        {
            loader.mLoaderThread = loaderthread;
            return loaderthread;
        }

*/

        public Loader()
        {
            this$0 = LauncherModel.this;
            super();
        }
    }

    public static interface Callbacks
    {

        public abstract void bindAllApplications(ArrayList arraylist);

        public abstract void bindAppWidget(LauncherAppWidgetInfo launcherappwidgetinfo);

        public abstract void bindAppsAdded(ArrayList arraylist);

        public abstract void bindAppsRemoved(ArrayList arraylist);

        public abstract void bindAppsUpdated(ArrayList arraylist);

        public abstract void bindFolders(HashMap hashmap);

        public abstract void bindItems(ArrayList arraylist, int i, int j);

        public abstract void bindSamsungAppWidget(SamsungAppWidgetInfo samsungappwidgetinfo);

        public abstract void finishBindingAllWorkspaceItems();

        public abstract void finishBindingAppWidgets();

        public abstract void finishBindingSamsungWidgets();

        public abstract void finishBindingShortcuts();

        public abstract int getCurrentWorkspaceScreen();

        public abstract boolean isAllAppsVisible();

        public abstract void onLoadingStart();

        public abstract void startBinding();
    }


    LauncherModel(LauncherApplication launcherapplication, IconCache iconcache, BadgeCache badgecache)
    {
        mHandler = new DeferredHandler();
        mLoader = new Loader();
        mBeforeFirstLoad = true;
        mBeforeFirstQuery = true;
        mIsBusy = false;
        mApp = launcherapplication;
        mAllAppsList = new AllAppsList(iconcache);
        mIconCache = iconcache;
        mDefaultIcon = Utilities.createIconBitmap(launcherapplication.getPackageManager().getDefaultActivityIcon(), launcherapplication);
        mAllAppsLoadDelay = 0;
        mBatchSize = 0;
    }

    static void addAppToDatabase(Context context, ApplicationInfo applicationinfo)
    {
        ContentValues contentvalues;
        ContentResolver contentresolver;
        ComponentName componentname;
        boolean flag;
        contentvalues = new ContentValues();
        contentresolver = context.getContentResolver();
        componentname = applicationinfo.intent.getComponent();
        flag = true;
        Cursor cursor;
        Uri uri = LauncherSettings.Apps.CONTENT_URI;
        String as[] = new String[1];
        as[0] = "_id";
        cursor = contentresolver.query(uri, as, (new StringBuilder()).append("componentname='").append(componentname.flattenToString()).append("'").toString(), null, null);
        if(cursor != null)
        {
            if(cursor.getCount() > 0)
                flag = false;
            cursor.close();
        }
_L2:
        if(flag)
        {
            contentvalues.put("componentname", componentname.flattenToString());
            contentresolver.insert(LauncherSettings.Apps.CONTENT_URI, contentvalues);
        }
        return;
        SQLiteException sqliteexception;
        sqliteexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("addAppToDatabase() ").append(sqliteexception).toString());
        if(false)
        {
            if(null.getCount() > 0)
                flag = false;
            null.close();
        }
        continue; /* Loop/switch isn't completed */
        IllegalStateException illegalstateexception;
        illegalstateexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("addAppToDatabase() ").append(illegalstateexception).toString());
        if(false)
        {
            if(null.getCount() > 0)
                flag = false;
            null.close();
        }
        if(true) goto _L2; else goto _L1
_L1:
        Exception exception;
        exception;
        if(false)
        {
            if(null.getCount() <= 0);
            null.close();
        }
        throw exception;
    }

    static void addItemToDatabase(Context context, ItemInfo iteminfo, long l, int i, int j, int k, boolean flag)
    {
        iteminfo.container = l;
        iteminfo.screen = i;
        iteminfo.cellX = j;
        iteminfo.cellY = k;
        ContentValues contentvalues = new ContentValues();
        ContentResolver contentresolver = context.getContentResolver();
        iteminfo.onAddToDatabase(contentvalues);
        Uri uri;
        Uri uri1;
        if(flag)
            uri = LauncherSettings.Favorites.CONTENT_URI;
        else
            uri = LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION;
        uri1 = contentresolver.insert(uri, contentvalues);
        if(uri1 != null)
            iteminfo.id = Integer.parseInt((String)uri1.getPathSegments().get(1));
    }

    static void addOrMoveItemInDatabase(Context context, ItemInfo iteminfo, long l, int i, int j, int k)
    {
        if(iteminfo.container == -1L)
            addItemToDatabase(context, iteminfo, l, i, j, k, false);
        else
            moveItemInDatabase(context, iteminfo, l, i, j, k);
    }

    static int allocWidgetId(Context context)
    {
        int i;
        int j;
        do
        {
            i = 0;
            j = (int)UUID.randomUUID().getLeastSignificantBits();
            Cursor cursor = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, null, (new StringBuilder()).append("appWidgetId=").append(j).toString(), null, null);
            if(cursor != null)
            {
                i = cursor.getCount();
                cursor.close();
            }
        } while(i > 0);
        return j;
    }

    static void deleteItemFromDatabase(Context context, ItemInfo iteminfo)
    {
        context.getContentResolver().delete(LauncherSettings.Favorites.getContentUri(iteminfo.id, false), null, null);
    }

    static void deleteUserFolderContentsFromDatabase(Context context, UserFolderInfo userfolderinfo)
    {
        ContentResolver contentresolver = context.getContentResolver();
        contentresolver.delete(LauncherSettings.Favorites.getContentUri(((ItemInfo) (userfolderinfo)).id, false), null, null);
        contentresolver.delete(LauncherSettings.Favorites.CONTENT_URI, (new StringBuilder()).append("container=").append(((ItemInfo) (userfolderinfo)).id).toString(), null);
    }

    private static LiveFolderInfo findOrMakeLiveFolder(HashMap hashmap, long l)
    {
        Object obj = (FolderInfo)hashmap.get(Long.valueOf(l));
        if(obj == null || !(obj instanceof LiveFolderInfo))
        {
            obj = new LiveFolderInfo();
            hashmap.put(Long.valueOf(l), obj);
        }
        return (LiveFolderInfo)obj;
    }

    private static UserFolderInfo findOrMakeUserFolder(HashMap hashmap, long l)
    {
        Object obj = (FolderInfo)hashmap.get(Long.valueOf(l));
        if(obj == null || !(obj instanceof UserFolderInfo))
        {
            obj = new UserFolderInfo();
            hashmap.put(Long.valueOf(l), obj);
        }
        return (UserFolderInfo)obj;
    }

    private ShortcutInfo getShortcutInfo(PackageManager packagemanager, Intent intent, Cursor cursor, Context context, int i, int j, int k, 
            int l, int i1)
    {
        Bitmap bitmap;
        ShortcutInfo shortcutinfo;
        bitmap = null;
        shortcutinfo = new ShortcutInfo();
        shortcutinfo.itemType = 1;
        ResolveInfo resolveinfo = packagemanager.resolveActivity(intent, 0);
        if(resolveinfo != null && intent.hasCategory("com.android.settings.SHORTCUT"))
            shortcutinfo.title = resolveinfo.activityInfo.loadLabel(packagemanager);
        if(shortcutinfo.title == null && cursor != null)
            shortcutinfo.title = cursor.getString(i1);
        cursor.getInt(i);
        JVM INSTR tableswitch 0 1: default 112
    //                   0 140
    //                   1 246;
           goto _L1 _L2 _L3
_L1:
        bitmap = getFallbackIcon();
        shortcutinfo.usingFallbackIcon = true;
        shortcutinfo.customIcon = false;
_L4:
        shortcutinfo.setIcon(bitmap);
        return shortcutinfo;
_L2:
        String s;
        String s1;
        PackageManager packagemanager1;
        s = cursor.getString(j);
        s1 = cursor.getString(k);
        packagemanager1 = context.getPackageManager();
        shortcutinfo.customIcon = false;
        Bitmap bitmap1;
        Resources resources = packagemanager1.getResourcesForApplication(s);
        if(resources == null)
            break MISSING_BLOCK_LABEL_212;
        bitmap1 = Utilities.createIconBitmap(resources.getDrawable(resources.getIdentifier(s1, null, null)), context);
        bitmap = bitmap1;
_L5:
        if(bitmap == null)
            bitmap = getIconFromCursor(cursor, l);
        if(bitmap == null)
        {
            bitmap = getFallbackIcon();
            shortcutinfo.usingFallbackIcon = true;
        }
          goto _L4
_L3:
        bitmap = getIconFromCursor(cursor, l);
        if(bitmap == null)
        {
            bitmap = getFallbackIcon();
            shortcutinfo.customIcon = false;
            shortcutinfo.usingFallbackIcon = true;
        } else
        {
            shortcutinfo.customIcon = true;
        }
          goto _L4
        Exception exception;
        exception;
          goto _L5
    }

    private ShortcutInfo infoFromShortcutIntent(Context context, Intent intent)
    {
        Intent intent1;
        String s;
        android.os.Parcelable parcelable;
        Bitmap bitmap;
        boolean flag;
        android.content.Intent.ShortcutIconResource shortcuticonresource;
        intent1 = (Intent)intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        s = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        parcelable = intent.getParcelableExtra("android.intent.extra.shortcut.ICON");
        bitmap = null;
        flag = false;
        shortcuticonresource = null;
        if(parcelable == null || !(parcelable instanceof Bitmap)) goto _L2; else goto _L1
_L1:
        bitmap = Utilities.createIconBitmap(new FastBitmapDrawable((Bitmap)parcelable), context);
        flag = true;
_L4:
        ShortcutInfo shortcutinfo = new ShortcutInfo();
        if(bitmap == null)
        {
            bitmap = getFallbackIcon();
            shortcutinfo.usingFallbackIcon = true;
        }
        shortcutinfo.setIcon(bitmap);
        shortcutinfo.title = s;
        shortcutinfo.intent = intent1;
        shortcutinfo.customIcon = flag;
        shortcutinfo.iconResource = shortcuticonresource;
        return shortcutinfo;
_L2:
        android.os.Parcelable parcelable1;
        parcelable1 = intent.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
        if(parcelable1 == null || !(parcelable1 instanceof android.content.Intent.ShortcutIconResource))
            continue; /* Loop/switch isn't completed */
        Bitmap bitmap1;
        shortcuticonresource = (android.content.Intent.ShortcutIconResource)parcelable1;
        Resources resources = context.getPackageManager().getResourcesForApplication(shortcuticonresource.packageName);
        bitmap1 = Utilities.createIconBitmap(resources.getDrawable(resources.getIdentifier(shortcuticonresource.resourceName, null, null)), context);
        bitmap = bitmap1;
        continue; /* Loop/switch isn't completed */
        Exception exception;
        exception;
        Log.w("Launcher.Model", (new StringBuilder()).append("Could not load shortcut icon: ").append(parcelable1).toString());
        if(true) goto _L4; else goto _L3
_L3:
    }

    static Cursor loadAppToDatabase(Context context)
    {
        Cursor cursor;
        ContentResolver contentresolver;
        cursor = null;
        contentresolver = context.getContentResolver();
        Cursor cursor1;
        Uri uri = LauncherSettings.Apps.CONTENT_URI;
        String as[] = new String[5];
        as[0] = "_id";
        as[1] = "componentname";
        as[2] = "top_number";
        as[3] = "page_number";
        as[4] = "cell_number";
        cursor1 = contentresolver.query(uri, as, null, null, "top_number ASC, page_number ASC, cell_number ASC");
        cursor = cursor1;
_L1:
        return cursor;
        SQLiteException sqliteexception;
        sqliteexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("loadAppToDatabase() ").append(sqliteexception).toString());
          goto _L1
        Exception exception;
        exception;
        throw exception;
        IllegalStateException illegalstateexception;
        illegalstateexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("loadAppToDatabase() ").append(illegalstateexception).toString());
          goto _L1
    }

    private static void loadLiveFolderIcon(Context context, Cursor cursor, int i, int j, int k, LiveFolderInfo livefolderinfo)
    {
        cursor.getInt(i);
        JVM INSTR tableswitch 0 0: default 24
    //                   0 44;
           goto _L1 _L2
_L1:
        livefolderinfo.icon = Utilities.createIconBitmap(context.getResources().getDrawable(0x7f020073), context);
_L4:
        return;
_L2:
        String s = cursor.getString(j);
        String s1 = cursor.getString(k);
        PackageManager packagemanager = context.getPackageManager();
        try
        {
            Resources resources = packagemanager.getResourcesForApplication(s);
            livefolderinfo.icon = Utilities.createIconBitmap(resources.getDrawable(resources.getIdentifier(s1, null, null)), context);
        }
        catch(Exception exception)
        {
            livefolderinfo.icon = Utilities.createIconBitmap(context.getResources().getDrawable(0x7f020073), context);
        }
        livefolderinfo.iconResource = new android.content.Intent.ShortcutIconResource();
        livefolderinfo.iconResource.packageName = s;
        livefolderinfo.iconResource.resourceName = s1;
        if(true) goto _L4; else goto _L3
_L3:
    }

    static Cursor loadTopAppToDatabase(Context context)
    {
        ContentResolver contentresolver = context.getContentResolver();
        Cursor cursor1;
        Uri uri = LauncherSettings.Apps.CONTENT_URI;
        String as[] = new String[3];
        as[0] = "_id";
        as[1] = "componentname";
        as[2] = "top_number";
        cursor1 = contentresolver.query(uri, as, "top_number!=65535", null, "top_number ASC");
        Cursor cursor = cursor1;
_L1:
        return cursor;
        SQLiteException sqliteexception;
        sqliteexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("loadTopAppToDatabase() ").append(sqliteexception).toString());
        cursor = null;
          goto _L1
        IllegalStateException illegalstateexception;
        illegalstateexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("loadTopAppToDatabase() ").append(illegalstateexception).toString());
        cursor = null;
          goto _L1
        Exception exception;
        exception;
        throw exception;
    }

    static void moveItemInDatabase(Context context, ItemInfo iteminfo, long l, int i, int j, int k)
    {
        iteminfo.container = l;
        iteminfo.screen = i;
        iteminfo.cellX = j;
        iteminfo.cellY = k;
        ContentValues contentvalues = new ContentValues();
        ContentResolver contentresolver = context.getContentResolver();
        contentvalues.put("container", Long.valueOf(iteminfo.container));
        contentvalues.put("cellX", Integer.valueOf(iteminfo.cellX));
        contentvalues.put("cellY", Integer.valueOf(iteminfo.cellY));
        contentvalues.put("screen", Integer.valueOf(iteminfo.screen));
        contentresolver.update(LauncherSettings.Favorites.getContentUri(iteminfo.id, false), contentvalues, null, null);
    }

    private void onReceiveExternalApplications(Context context, Intent intent)
    {
        if(!mBeforeFirstQuery) goto _L2; else goto _L1
_L1:
        return;
_L2:
        String s;
        s = intent.getAction();
        if(!"android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(s))
            break MISSING_BLOCK_LABEL_100;
        Log.d("Launcher.Model", (new StringBuilder()).append(s).append(" received").toString());
        String as1[] = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
        if(as1 == null || as1.length == 0)
            continue; /* Loop/switch isn't completed */
        this;
        JVM INSTR monitorenter ;
        mWorkspaceLoaded = false;
        mAllAppsLoaded = false;
        this;
        JVM INSTR monitorexit ;
        startLoader(context, false);
        continue; /* Loop/switch isn't completed */
        Exception exception1;
        exception1;
        this;
        JVM INSTR monitorexit ;
        throw exception1;
        if(!"android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(s))
            continue; /* Loop/switch isn't completed */
        Log.d("Launcher.Model", (new StringBuilder()).append(s).append(" received").toString());
        String as[] = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
        if(as == null || as.length == 0)
            continue; /* Loop/switch isn't completed */
        this;
        JVM INSTR monitorenter ;
        mWorkspaceLoaded = false;
        mAllAppsLoaded = false;
        this;
        JVM INSTR monitorexit ;
        startLoader(context, false);
        if(true) goto _L1; else goto _L3
_L3:
        Exception exception;
        exception;
        this;
        JVM INSTR monitorexit ;
        throw exception;
    }

    static void removeAppToDatabase(Context context, ApplicationInfo applicationinfo)
    {
        context.getContentResolver().delete(LauncherSettings.Apps.CONTENT_URI, (new StringBuilder()).append("componentname='").append(applicationinfo.intent.getComponent().flattenToString()).append("'").toString(), null);
    }

    static void removeAppToDatabase(Context context, String s)
    {
        context.getContentResolver().delete(LauncherSettings.Apps.CONTENT_URI, (new StringBuilder()).append("_id='").append(s).append("'").toString(), null);
    }

    static boolean shortcutExists(Context context, String s, Intent intent)
    {
        Cursor cursor;
        ContentResolver contentresolver = context.getContentResolver();
        Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        String as[] = new String[2];
        as[0] = "title";
        as[1] = "intent";
        String as1[] = new String[2];
        as1[0] = s;
        as1[1] = intent.toUri(0);
        cursor = contentresolver.query(uri, as, "title=? and intent=?", as1, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
        Exception exception;
        exception;
        cursor.close();
        throw exception;
    }

    static int updateAppToDatabase(Context context, ApplicationInfo applicationinfo)
    {
        if(!applicationinfo.isUpdated) goto _L2; else goto _L1
_L1:
        int j = -1;
_L4:
        return j;
_L2:
        ContentValues contentvalues;
        ContentResolver contentresolver;
        ComponentName componentname;
        contentvalues = new ContentValues();
        contentresolver = context.getContentResolver();
        componentname = applicationinfo.intent.getComponent();
        Cursor cursor;
        Uri uri = LauncherSettings.Apps.CONTENT_URI;
        String as[] = new String[1];
        as[0] = "_id";
        cursor = contentresolver.query(uri, as, (new StringBuilder()).append("componentname='").append(componentname.flattenToString()).append("'").toString(), null, null);
        int i;
        if(cursor != null)
        {
            Exception exception;
            IllegalStateException illegalstateexception;
            int k;
            SQLiteException sqliteexception;
            int l;
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                i = cursor.getInt(0);
            } else
            {
                i = -1;
            }
            cursor.close();
        } else
        {
            i = -1;
        }
        contentvalues.put("top_number", Integer.valueOf(applicationinfo.topNum));
        contentvalues.put("page_number", Integer.valueOf(applicationinfo.pageNum));
        contentvalues.put("cell_number", Integer.valueOf(applicationinfo.cellNum));
        applicationinfo.isUpdated = true;
        if(i != -1)
        {
            j = contentresolver.update(LauncherSettings.Apps.getContentUri(i), contentvalues, null, null);
        } else
        {
            contentvalues.put("componentname", componentname.flattenToString());
            contentresolver.insert(LauncherSettings.Apps.CONTENT_URI, contentvalues);
            j = -1;
        }
        if(true) goto _L4; else goto _L3
_L3:
        sqliteexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("updatePageNumberToDatabase() ").append(sqliteexception).toString());
        if(true)
            break MISSING_BLOCK_LABEL_429;
        if(null.getCount() > 0)
        {
            null.moveToFirst();
            l = null.getInt(0);
        } else
        {
            l = -1;
        }
        null.close();
        i = l;
        break MISSING_BLOCK_LABEL_133;
        illegalstateexception;
        Log.e("Launcher.Model", (new StringBuilder()).append("updatePageNumberToDatabase() ").append(illegalstateexception).toString());
        if(true)
            break MISSING_BLOCK_LABEL_429;
        if(null.getCount() > 0)
        {
            null.moveToFirst();
            k = null.getInt(0);
        } else
        {
            k = -1;
        }
        null.close();
        i = k;
        break MISSING_BLOCK_LABEL_133;
        exception;
        if(false)
        {
            if(null.getCount() > 0)
            {
                null.moveToFirst();
                null.getInt(0);
            }
            null.close();
        }
        throw exception;
        i = -1;
        break MISSING_BLOCK_LABEL_133;
    }

    static void updateItemInDatabase(Context context, ItemInfo iteminfo)
    {
        ContentValues contentvalues = new ContentValues();
        ContentResolver contentresolver = context.getContentResolver();
        iteminfo.onAddToDatabase(contentvalues);
        contentresolver.update(LauncherSettings.Favorites.getContentUri(iteminfo.id, false), contentvalues, null, null);
    }

    ShortcutInfo addShortcut(Context context, Intent intent, CellLayout.CellInfo cellinfo, boolean flag)
    {
        ShortcutInfo shortcutinfo = infoFromShortcutIntent(context, intent);
        addItemToDatabase(context, shortcutinfo, -100L, cellinfo.screen, cellinfo.cellX, cellinfo.cellY, flag);
        return shortcutinfo;
    }

    public Bitmap getFallbackIcon()
    {
        return Bitmap.createBitmap(mDefaultIcon);
    }

    FolderInfo getFolderById(Context context, HashMap hashmap, long l)
    {
        Cursor cursor;
        ContentResolver contentresolver = context.getContentResolver();
        Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        String as[] = new String[3];
        as[0] = String.valueOf(l);
        as[1] = String.valueOf(2);
        as[2] = String.valueOf(3);
        cursor = contentresolver.query(uri, null, "_id=? and (itemType=? or itemType=?)", as, null);
        if(!cursor.moveToFirst()) goto _L2; else goto _L1
_L1:
        int i;
        int j;
        int k;
        int i1;
        int j1;
        int k1;
        Object obj;
        i = cursor.getColumnIndexOrThrow("itemType");
        j = cursor.getColumnIndexOrThrow("title");
        k = cursor.getColumnIndexOrThrow("container");
        i1 = cursor.getColumnIndexOrThrow("screen");
        j1 = cursor.getColumnIndexOrThrow("cellX");
        k1 = cursor.getColumnIndexOrThrow("cellY");
        obj = null;
        cursor.getInt(i);
        JVM INSTR tableswitch 2 3: default 172
    //                   2 263
    //                   3 273;
           goto _L3 _L4 _L5
_L3:
        obj.title = cursor.getString(j);
        obj.id = l;
        obj.container = cursor.getInt(k);
        obj.screen = cursor.getInt(i1);
        obj.cellX = cursor.getInt(j1);
        obj.cellY = cursor.getInt(k1);
        FolderInfo folderinfo;
        cursor.close();
        folderinfo = ((FolderInfo) (obj));
_L6:
        return folderinfo;
_L4:
        obj = findOrMakeUserFolder(hashmap, l);
          goto _L3
_L5:
        LiveFolderInfo livefolderinfo = findOrMakeLiveFolder(hashmap, l);
        obj = livefolderinfo;
          goto _L3
_L2:
        cursor.close();
        folderinfo = null;
          goto _L6
        Exception exception;
        exception;
        cursor.close();
        throw exception;
          goto _L3
    }

    Bitmap getIconFromCursor(Cursor cursor, int i)
    {
        byte abyte0[] = cursor.getBlob(i);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
        Bitmap bitmap = bitmap1;
_L2:
        return bitmap;
        Exception exception;
        exception;
        bitmap = null;
        if(true) goto _L2; else goto _L1
_L1:
    }

    public ShortcutInfo getShortcutInfo(PackageManager packagemanager, Intent intent, Context context)
    {
        return getShortcutInfo(packagemanager, intent, context, null, -1, -1);
    }

    public ShortcutInfo getShortcutInfo(PackageManager packagemanager, Intent intent, Context context, Cursor cursor, int i, int j)
    {
        Bitmap bitmap = null;
        ShortcutInfo shortcutinfo = new ShortcutInfo();
        ComponentName componentname = intent.getComponent();
        ShortcutInfo shortcutinfo1;
        if(componentname == null)
        {
            shortcutinfo1 = null;
        } else
        {
            ResolveInfo resolveinfo = packagemanager.resolveActivity(intent, 0);
            if(resolveinfo != null)
                bitmap = mIconCache.getIcon(componentname, resolveinfo);
            if(bitmap == null && cursor != null)
                bitmap = getIconFromCursor(cursor, i);
            if(bitmap == null)
            {
                bitmap = getFallbackIcon();
                shortcutinfo.usingFallbackIcon = true;
            }
            shortcutinfo.setIcon(bitmap);
            if(resolveinfo != null)
                shortcutinfo.title = resolveinfo.activityInfo.loadLabel(packagemanager);
            if(shortcutinfo.title == null && cursor != null)
                shortcutinfo.title = cursor.getString(j);
            if(shortcutinfo.title == null)
                shortcutinfo.title = componentname.getClassName();
            shortcutinfo.itemType = 0;
            shortcutinfo1 = shortcutinfo;
        }
        return shortcutinfo1;
    }

    public void initialize(Callbacks callbacks)
    {
        Object obj = mLock;
        obj;
        JVM INSTR monitorenter ;
        mCallbacks = new WeakReference(callbacks);
        mHandler.cancel();
        return;
    }

    boolean isBusy()
    {
        return mIsBusy;
    }

    public void onReceive(Context context, Intent intent)
    {
        String s;
        s = intent.getAction();
        if(mApp.mSystemShuttingDown)
        {
            Log.d("Launcher.Model", (new StringBuilder()).append("Ignoring ").append(s).append(" because the system is shutting down").toString());
        } else
        {
label0:
            {
                if(!"android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(s) && !"android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(s))
                    break label0;
                onReceiveExternalApplications(context, intent);
            }
        }
_L2:
        return;
        LauncherApplication launcherapplication;
        ArrayList arraylist;
        ArrayList arraylist1;
        ArrayList arraylist2;
        if(mBeforeFirstLoad) goto _L2; else goto _L1
_L1:
        Object obj = mAllAppsListLock;
        obj;
        JVM INSTR monitorenter ;
        if(!"android.intent.action.PACKAGE_CHANGED".equals(s) && !"android.intent.action.PACKAGE_REMOVED".equals(s) && !"android.intent.action.PACKAGE_ADDED".equals(s)) goto _L4; else goto _L3
_L3:
        String s1;
        boolean flag;
        s1 = intent.getData().getSchemeSpecificPart();
        flag = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
        if(s1 != null && s1.length() != 0) goto _L5; else goto _L2
_L5:
        break MISSING_BLOCK_LABEL_185;
        Exception exception;
        exception;
        throw exception;
        SamsungWidgetPackageManager samsungwidgetpackagemanager = SamsungWidgetPackageManager.getInstance();
        if(!"android.intent.action.PACKAGE_CHANGED".equals(s)) goto _L7; else goto _L6
_L6:
        if(samsungwidgetpackagemanager != null)
            samsungwidgetpackagemanager.updatePackage(launcherapplication, s1);
        mAllAppsList.updatePackage(launcherapplication, s1);
_L9:
        if(mAllAppsList.added.size() > 0)
        {
            arraylist = mAllAppsList.added;
            mAllAppsList.added = new ArrayList();
        }
        if(mAllAppsList.removed.size() > 0)
        {
            arraylist1 = mAllAppsList.removed;
            mAllAppsList.removed = new ArrayList();
            ApplicationInfo applicationinfo;
            for(Iterator iterator = arraylist1.iterator(); iterator.hasNext(); mIconCache.remove(applicationinfo.intent.getComponent()))
                applicationinfo = (ApplicationInfo)iterator.next();

        }
        break; /* Loop/switch isn't completed */
_L7:
        if("android.intent.action.PACKAGE_REMOVED".equals(s))
        {
            if(!flag)
            {
                if(samsungwidgetpackagemanager != null)
                    samsungwidgetpackagemanager.removePackage(launcherapplication, s1);
                mAllAppsList.removePackage(s1);
            }
        } else
        if("android.intent.action.PACKAGE_ADDED".equals(s))
            if(!flag)
            {
                if(samsungwidgetpackagemanager != null)
                    samsungwidgetpackagemanager.addPackage(launcherapplication, s1);
                mAllAppsList.addPackage(launcherapplication, s1);
            } else
            {
                if(samsungwidgetpackagemanager != null)
                    samsungwidgetpackagemanager.updatePackage(launcherapplication, s1);
                mAllAppsList.updatePackage(launcherapplication, s1);
            }
        if(true) goto _L9; else goto _L8
_L8:
        if(mAllAppsList.modified.size() > 0)
        {
            arraylist2 = mAllAppsList.modified;
            mAllAppsList.modified = new ArrayList();
        }
        if(mCallbacks == null) goto _L11; else goto _L10
_L10:
        final Callbacks callbacks = (Callbacks)mCallbacks.get();
_L14:
        if(callbacks != null) goto _L13; else goto _L12
_L12:
        Log.w("Launcher.Model", "Nobody to tell about the new app.  Launcher is probably loading.");
        obj;
        JVM INSTR monitorexit ;
          goto _L2
_L13:
        if(arraylist != null)
        {
            final ArrayList addedFinal = arraylist;
            DeferredHandler deferredhandler2 = mHandler;
            Runnable runnable2 = new Runnable() {

                public void run()
                {
                    callbacks.bindAppsAdded(addedFinal);
                }

                final LauncherModel this$0;
                final ArrayList val$addedFinal;
                final Callbacks val$callbacks;

            
            {
                this$0 = LauncherModel.this;
                callbacks = callbacks1;
                addedFinal = arraylist;
                super();
            }
            }
;
            deferredhandler2.post(runnable2);
        }
        if(arraylist2 != null)
        {
            final ArrayList modifiedFinal = arraylist2;
            DeferredHandler deferredhandler1 = mHandler;
            Runnable runnable1 = new Runnable() {

                public void run()
                {
                    callbacks.bindAppsUpdated(modifiedFinal);
                }

                final LauncherModel this$0;
                final Callbacks val$callbacks;
                final ArrayList val$modifiedFinal;

            
            {
                this$0 = LauncherModel.this;
                callbacks = callbacks1;
                modifiedFinal = arraylist;
                super();
            }
            }
;
            deferredhandler1.post(runnable1);
        }
        if(arraylist1 != null)
        {
            final ArrayList removedFinal = arraylist1;
            DeferredHandler deferredhandler = mHandler;
            Runnable runnable = new Runnable() {

                public void run()
                {
                    callbacks.bindAppsRemoved(removedFinal);
                }

                final LauncherModel this$0;
                final Callbacks val$callbacks;
                final ArrayList val$removedFinal;

            
            {
                this$0 = LauncherModel.this;
                callbacks = callbacks1;
                removedFinal = arraylist;
                super();
            }
            }
;
            deferredhandler.post(runnable);
        }
_L4:
        obj;
        JVM INSTR monitorexit ;
          goto _L2
_L11:
        callbacks = null;
          goto _L14
    }

    public void startLoader(Context context, boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        mWorkspaceLoaded = false;
        mAllAppsLoaded = false;
        this;
        JVM INSTR monitorexit ;
        mHandler.cancel();
        Exception exception;
        Callbacks callbacks;
        if(mCallbacks != null)
            callbacks = (Callbacks)mCallbacks.get();
        else
            callbacks = null;
        if(callbacks != null)
            callbacks.onLoadingStart();
        mLoader.startLoader(context, flag);
        return;
        exception;
        this;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public void stopLoader()
    {
        mLoader.stopLoader();
    }

    void updateSavedIcon(Context context, ShortcutInfo shortcutinfo, Cursor cursor, int i)
    {
        if(!shortcutinfo.onExternalStorage || shortcutinfo.customIcon || shortcutinfo.usingFallbackIcon) goto _L2; else goto _L1
_L1:
        byte abyte0[] = cursor.getBlob(i);
        if(abyte0 == null) goto _L4; else goto _L3
_L3:
        boolean flag1 = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length).sameAs(shortcutinfo.getIcon(mIconCache));
        boolean flag;
        if(!flag1)
            flag = true;
        else
            flag = false;
_L6:
        if(flag)
        {
            Log.d("Launcher.Model", (new StringBuilder()).append("going to save icon bitmap for info=").append(shortcutinfo).toString());
            updateItemInDatabase(context, shortcutinfo);
        }
_L2:
        return;
_L4:
        flag = true;
        continue; /* Loop/switch isn't completed */
        Exception exception;
        exception;
        flag = true;
        if(true) goto _L6; else goto _L5
_L5:
    }

    public static final Comparator APP_NAME_COMPARATOR = new Comparator() {

        public final int compare(ApplicationInfo applicationinfo, ApplicationInfo applicationinfo1)
        {
            return LauncherModel.sCollator.compare(applicationinfo.title.toString(), applicationinfo1.title.toString());
        }

        public volatile int compare(Object obj, Object obj1)
        {
            return compare((ApplicationInfo)obj, (ApplicationInfo)obj1);
        }

    }
;
    static final Object mDBLock = new Object();
    private static final Collator sCollator = Collator.getInstance();
    private AllAppsList mAllAppsList;
    private final Object mAllAppsListLock = new Object();
    private int mAllAppsLoadDelay;
    private boolean mAllAppsLoaded;
    private final LauncherApplication mApp;
    private int mBatchSize;
    private boolean mBeforeFirstLoad;
    private boolean mBeforeFirstQuery;
    private WeakReference mCallbacks;
    private Bitmap mDefaultIcon;
    private DeferredHandler mHandler;
    private IconCache mIconCache;
    private boolean mIsBusy;
    private Loader mLoader;
    private final Object mLock = new Object();
    private boolean mWorkspaceLoaded;







/*
    static boolean access$1102(LauncherModel launchermodel, boolean flag)
    {
        launchermodel.mAllAppsLoaded = flag;
        return flag;
    }

*/




/*
    static boolean access$1402(LauncherModel launchermodel, boolean flag)
    {
        launchermodel.mBeforeFirstQuery = flag;
        return flag;
    }

*/




/*
    static boolean access$1702(LauncherModel launchermodel, boolean flag)
    {
        launchermodel.mBeforeFirstLoad = flag;
        return flag;
    }

*/




/*
    static boolean access$202(LauncherModel launchermodel, boolean flag)
    {
        launchermodel.mIsBusy = flag;
        return flag;
    }

*/



/*
    static boolean access$302(LauncherModel launchermodel, boolean flag)
    {
        launchermodel.mWorkspaceLoaded = flag;
        return flag;
    }

*/




}
