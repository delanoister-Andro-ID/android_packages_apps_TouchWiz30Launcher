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

import android.app.SearchManager;
import android.appwidget.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.*;
import android.view.Display;
import android.view.WindowManager;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherModel, LauncherConfig, Launcher, LauncherCustomer

public class LauncherProvider extends ContentProvider
{
    static class SqlArguments
    {

        public final String args[];
        public final String table;
        public final String where;

        SqlArguments(Uri uri)
        {
            if(uri.getPathSegments().size() == 1)
            {
                table = (String)uri.getPathSegments().get(0);
                where = null;
                args = null;
                return;
            } else
            {
                throw new IllegalArgumentException((new StringBuilder()).append("Invalid URI: ").append(uri).toString());
            }
        }

        SqlArguments(Uri uri, String s, String as[])
        {
            if(uri.getPathSegments().size() == 1)
            {
                table = (String)uri.getPathSegments().get(0);
                where = s;
                args = as;
            } else
            {
                if(uri.getPathSegments().size() != 2)
                    throw new IllegalArgumentException((new StringBuilder()).append("Invalid URI: ").append(uri).toString());
                if(!TextUtils.isEmpty(s))
                    throw new UnsupportedOperationException((new StringBuilder()).append("WHERE clause not supported: ").append(uri).toString());
                table = (String)uri.getPathSegments().get(0);
                where = (new StringBuilder()).append("_id=").append(ContentUris.parseId(uri)).toString();
                args = null;
            }
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        private boolean addAppShortcut(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray, PackageManager packagemanager, Intent intent)
        {
            String s;
            String s1;
            s = typedarray.getString(1);
            s1 = typedarray.getString(0);
            ComponentName componentname = new ComponentName(s, s1);
            ActivityInfo activityinfo = packagemanager.getActivityInfo(componentname, 0);
            intent.setComponent(componentname);
            intent.setFlags(0x10200000);
            contentvalues.put("intent", intent.toUri(0));
            contentvalues.put("title", activityinfo.loadLabel(packagemanager).toString());
            contentvalues.put("itemType", Integer.valueOf(0));
            contentvalues.put("spanX", Integer.valueOf(1));
            contentvalues.put("spanY", Integer.valueOf(1));
            sqlitedatabase.insert("favorites", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("Unable to add favorite: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addAppWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, ComponentName componentname, int i, int j)
        {
            boolean flag = false;
            AppWidgetManager appwidgetmanager = AppWidgetManager.getInstance(mContext);
            try
            {
                int k = mAppWidgetHost.allocateAppWidgetId();
                contentvalues.put("itemType", Integer.valueOf(4));
                contentvalues.put("spanX", Integer.valueOf(i));
                contentvalues.put("spanY", Integer.valueOf(j));
                contentvalues.put("appWidgetId", Integer.valueOf(k));
                sqlitedatabase.insert("favorites", null, contentvalues);
                flag = true;
                appwidgetmanager.bindAppWidgetId(k, componentname);
            }
            catch(RuntimeException runtimeexception)
            {
                runtimeexception.printStackTrace();
            }
            return flag;
        }

        private boolean addAppWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray)
        {
            String s;
            String s1;
            s = typedarray.getString(1);
            s1 = typedarray.getString(0);
            if(s != null && s1 != null) goto _L2; else goto _L1
_L1:
            boolean flag = false;
_L4:
            return flag;
_L2:
            ComponentName componentname = new ComponentName(s, s1);
            boolean flag1 = false;
            AppWidgetManager appwidgetmanager = AppWidgetManager.getInstance(mContext);
            try
            {
                int i = mAppWidgetHost.allocateAppWidgetId();
                contentvalues.put("itemType", Integer.valueOf(4));
                contentvalues.put("spanX", typedarray.getString(5));
                contentvalues.put("spanY", typedarray.getString(6));
                contentvalues.put("appWidgetId", Integer.valueOf(i));
                sqlitedatabase.insert("favorites", null, contentvalues);
                flag1 = true;
                appwidgetmanager.bindAppWidgetId(i, componentname);
            }
            catch(RuntimeException runtimeexception)
            {
                Log.e("Launcher.LauncherProvider", "Problem allocating appWidgetId", runtimeexception);
                runtimeexception.printStackTrace();
            }
            flag = flag1;
            if(true) goto _L4; else goto _L3
_L3:
        }

        private boolean addCSCAppWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, String as[])
        {
            String s;
            String s1;
            s = as[1];
            s1 = as[2];
            if(s != null && s1 != null) goto _L2; else goto _L1
_L1:
            boolean flag = false;
_L4:
            return flag;
_L2:
            ComponentName componentname;
            AppWidgetManager appwidgetmanager;
            componentname = new ComponentName(s, s1);
            appwidgetmanager = AppWidgetManager.getInstance(mContext);
            int i;
            i = mAppWidgetHost.allocateAppWidgetId();
            contentvalues.put("itemType", Integer.valueOf(4));
            contentvalues.put("spanX", as[6]);
            contentvalues.put("spanY", as[7]);
            contentvalues.put("appWidgetId", Integer.valueOf(i));
            sqlitedatabase.insert("favorites", null, contentvalues);
            appwidgetmanager.bindAppWidgetId(i, componentname);
            flag = true;
            continue; /* Loop/switch isn't completed */
            RuntimeException runtimeexception;
            runtimeexception;
            boolean flag1 = false;
_L5:
            Log.e("Launcher.LauncherProvider", "(CSC)Problem allocating appWidgetId", runtimeexception);
            runtimeexception.printStackTrace();
            flag = flag1;
            if(true) goto _L4; else goto _L3
_L3:
            runtimeexception;
            flag1 = true;
              goto _L5
        }

        private boolean addCSCFolder(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, String s)
        {
            contentvalues.put("title", s);
            contentvalues.put("itemType", Integer.valueOf(2));
            contentvalues.put("spanX", Integer.valueOf(1));
            contentvalues.put("spanY", Integer.valueOf(1));
            sqlitedatabase.insert("favorites", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            RuntimeException runtimeexception;
            runtimeexception;
            Log.e("kss", "addCSCFolder exception");
            runtimeexception.printStackTrace();
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addCSCMainApp(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, String s, String s1, int i, PackageManager packagemanager)
        {
            int k;
            int l;
            int j = LauncherConfig.getItemNoOfPage(mContext);
            k = i / j;
            l = i % j;
            ComponentName componentname = new ComponentName(s, s1);
            packagemanager.getActivityInfo(componentname, 0);
            contentvalues.clear();
            contentvalues.put("componentname", componentname.flattenToString());
            contentvalues.put("page_number", Integer.valueOf(k));
            contentvalues.put("cell_number", Integer.valueOf(l));
            sqlitedatabase.insert("apps", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("(CSC)Unable to add mainapp: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addCSCSamsungAppWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, String as[], PackageManager packagemanager)
        {
            String s;
            String s1;
            Intent intent;
            s = as[1];
            s1 = as[2];
            intent = new Intent();
            Context context;
            int i;
            int j;
            DisplayMetrics displaymetrics;
            ComponentName componentname = new ComponentName(s, s1);
            packagemanager.getActivityInfo(componentname, 0);
            intent.setComponent(componentname);
            contentvalues.put("intent", intent.toUri(0));
            contentvalues.put("itemType", Integer.valueOf(5));
            context = mContext.createPackageContext(s, 3);
            i = context.getResources().getIdentifier("min_width", "string", s);
            j = context.getResources().getIdentifier("min_height", "string", s);
            displaymetrics = new DisplayMetrics();
            ((WindowManager)mContext.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
            if(i == 0) goto _L2; else goto _L1
_L1:
            int k = (int)((float)Integer.valueOf(context.getResources().getString(i)).intValue() * displaymetrics.density);
_L4:
            int l;
            if(j == 0)
                break MISSING_BLOCK_LABEL_412;
            l = (int)((float)Integer.valueOf(context.getResources().getString(j)).intValue() * displaymetrics.density);
_L5:
            boolean flag;
            Resources resources = mContext.getResources();
            int i1 = resources.getDimensionPixelSize(0x7f090003);
            int j1 = resources.getDimensionPixelSize(0x7f090004);
            int k1;
            int l1;
            if(k % i1 != 0)
                k1 = 1 + k / i1;
            else
                k1 = k / i1;
            if(l % j1 != 0)
                l1 = 1 + l / j1;
            else
                l1 = l / j1;
            if(k1 > Launcher.NUMBER_CELLS_X)
                k1 = Launcher.NUMBER_CELLS_X;
            if(l1 > Launcher.NUMBER_CELLS_Y)
                l1 = Launcher.NUMBER_CELLS_Y;
            contentvalues.put("spanX", Integer.valueOf(k1));
            contentvalues.put("spanY", Integer.valueOf(l1));
            sqlitedatabase.insert("favorites", null, contentvalues);
            flag = true;
              goto _L3
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("(CSC)Unable to add samsungappwidget: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
_L3:
            return flag;
_L2:
            k = 0;
              goto _L4
            l = 0;
              goto _L5
        }

        private boolean addCSCShortcut(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, String s, String s1, String s2, PackageManager packagemanager, Intent intent)
        {
            ComponentName componentname = new ComponentName(s, s1);
            if(s2 != null)
                contentvalues.put("container", s2);
            ActivityInfo activityinfo = packagemanager.getActivityInfo(componentname, 0);
            intent.setComponent(componentname);
            intent.setFlags(0x10200000);
            contentvalues.put("intent", intent.toURI());
            contentvalues.put("title", activityinfo.loadLabel(packagemanager).toString());
            contentvalues.put("itemType", Integer.valueOf(0));
            contentvalues.put("spanX", Integer.valueOf(1));
            contentvalues.put("spanY", Integer.valueOf(1));
            sqlitedatabase.insert("favorites", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("(CSC)Unable to add favorite: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addClockWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues)
        {
            int ai[];
            ArrayList arraylist;
            boolean flag;
            ai = new int[1];
            ai[0] = 1000;
            arraylist = new ArrayList();
            arraylist.add(new ComponentName("com.android.alarmclock", "com.android.alarmclock.AnalogAppWidgetProvider"));
            flag = false;
            int i = mAppWidgetHost.allocateAppWidgetId();
            contentvalues.put("itemType", Integer.valueOf(1000));
            contentvalues.put("spanX", Integer.valueOf(2));
            contentvalues.put("spanY", Integer.valueOf(2));
            contentvalues.put("appWidgetId", Integer.valueOf(i));
            sqlitedatabase.insert("favorites", null, contentvalues);
            flag = true;
_L2:
            if(flag)
                launchAppWidgetBinder(ai, arraylist);
            return flag;
            RuntimeException runtimeexception;
            runtimeexception;
            Log.e("Launcher.LauncherProvider", "Problem allocating appWidgetId", runtimeexception);
            runtimeexception.printStackTrace();
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addMainApp(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray, PackageManager packagemanager, int i)
        {
            String s;
            String s1;
            int k;
            int l;
            s = typedarray.getString(1);
            s1 = typedarray.getString(0);
            int j = LauncherConfig.getItemNoOfPage(mContext);
            k = i / j;
            l = i % j;
            ComponentName componentname = new ComponentName(s, s1);
            packagemanager.getActivityInfo(componentname, 0);
            contentvalues.put("componentname", componentname.flattenToString());
            contentvalues.put("page_number", Integer.valueOf(k));
            contentvalues.put("cell_number", Integer.valueOf(l));
            sqlitedatabase.insert("apps", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("Unable to add mainapp: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean addSamsungAppWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray, PackageManager packagemanager)
        {
            String s;
            String s1;
            Intent intent;
            s = typedarray.getString(1);
            s1 = typedarray.getString(0);
            intent = new Intent();
            Context context;
            int i;
            int j;
            DisplayMetrics displaymetrics;
            ComponentName componentname = new ComponentName(s, s1);
            packagemanager.getActivityInfo(componentname, 0);
            intent.setComponent(componentname);
            contentvalues.put("intent", intent.toUri(0));
            contentvalues.put("itemType", Integer.valueOf(5));
            context = mContext.createPackageContext(s, 3);
            i = context.getResources().getIdentifier("min_width", "string", s);
            j = context.getResources().getIdentifier("min_height", "string", s);
            displaymetrics = new DisplayMetrics();
            ((WindowManager)mContext.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
            if(i == 0) goto _L2; else goto _L1
_L1:
            int k = (int)((float)Integer.valueOf(context.getResources().getString(i)).intValue() * displaymetrics.density);
_L7:
            int l;
            if(j == 0)
                break MISSING_BLOCK_LABEL_441;
            l = (int)((float)Integer.valueOf(context.getResources().getString(j)).intValue() * displaymetrics.density);
_L8:
            boolean flag;
            int j1;
            int i2;
            Resources resources = mContext.getResources();
            int i1 = resources.getDimensionPixelSize(0x7f090003);
            j1 = resources.getDimensionPixelSize(0x7f090004);
            int k1;
            int j2;
            int k2;
            if(k % i1 != 0)
                k1 = 1 + k / i1;
            else
                k1 = k / i1;
            if(l % j1 == 0) goto _L4; else goto _L3
_L3:
            i2 = 1 + l / j1;
_L6:
            j2 = Launcher.NUMBER_CELLS_X;
            if(k1 > j2)
                k1 = Launcher.NUMBER_CELLS_X;
            k2 = Launcher.NUMBER_CELLS_Y;
            if(i2 > k2)
                i2 = Launcher.NUMBER_CELLS_Y;
            contentvalues.put("spanX", Integer.valueOf(k1));
            contentvalues.put("spanY", Integer.valueOf(i2));
            contentvalues.put("appWidgetId", Integer.valueOf((int)System.currentTimeMillis()));
            sqlitedatabase.insert("favorites", null, contentvalues);
            flag = true;
              goto _L5
_L4:
            int l1 = l / j1;
            i2 = l1;
              goto _L6
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("Unable to add samsungappwidget: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
_L5:
            return flag;
_L2:
            k = 0;
              goto _L7
            l = 0;
              goto _L8
        }

        private boolean addSearchWidget(SQLiteDatabase sqlitedatabase, ContentValues contentvalues)
        {
            return addAppWidget(sqlitedatabase, contentvalues, getSearchWidgetProvider(), 4, 1);
        }

        private boolean addShortcut(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray)
        {
label0:
            {
                Resources resources = mContext.getResources();
                int i = typedarray.getResourceId(7, 0);
                int j = typedarray.getResourceId(8, 0);
                String s = null;
                boolean flag;
                Intent intent;
                try
                {
                    s = typedarray.getString(9);
                    intent = Intent.parseUri(s, 0);
                }
                catch(URISyntaxException urisyntaxexception)
                {
                    Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("Shortcut has malformed uri: ").append(s).toString());
                    urisyntaxexception.printStackTrace();
                    flag = false;
                    if(false)
                        ;
                    else
                        break label0;
                }
                if(i == 0 || j == 0)
                {
                    Log.w("Launcher.LauncherProvider", "Shortcut is missing title or icon resource ID");
                    flag = false;
                } else
                {
                    intent.setFlags(0x10000000);
                    contentvalues.put("intent", intent.toUri(0));
                    contentvalues.put("title", resources.getString(j));
                    contentvalues.put("itemType", Integer.valueOf(1));
                    contentvalues.put("spanX", Integer.valueOf(1));
                    contentvalues.put("spanY", Integer.valueOf(1));
                    contentvalues.put("iconType", Integer.valueOf(0));
                    contentvalues.put("iconPackage", mContext.getPackageName());
                    contentvalues.put("iconResource", mContext.getResources().getResourceName(i));
                    sqlitedatabase.insert("favorites", null, contentvalues);
                    flag = true;
                }
            }
            return flag;
        }

        private boolean addTopApp(SQLiteDatabase sqlitedatabase, ContentValues contentvalues, TypedArray typedarray, PackageManager packagemanager, int i)
        {
            String s;
            String s1;
            s = typedarray.getString(1);
            s1 = typedarray.getString(0);
            ComponentName componentname = new ComponentName(s, s1);
            packagemanager.getActivityInfo(componentname, 0);
            contentvalues.put("componentname", componentname.flattenToString());
            contentvalues.put("top_number", Integer.valueOf(i));
            sqlitedatabase.insert("apps", null, contentvalues);
            boolean flag = true;
_L2:
            return flag;
            android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
            namenotfoundexception;
            Log.w("Launcher.LauncherProvider", (new StringBuilder()).append("Unable to add topapp: ").append(s).append("/").append(s1).toString(), namenotfoundexception);
            namenotfoundexception.printStackTrace();
            flag = false;
            if(true) goto _L2; else goto _L1
_L1:
        }

        private boolean convertDatabase(SQLiteDatabase sqlitedatabase)
        {
            boolean flag;
            Uri uri;
            ContentResolver contentresolver;
            Cursor cursor;
            Log.d("Launcher.LauncherProvider", "converting database from an older format, but not onUpgrade");
            flag = false;
            uri = Uri.parse("content://settings/old_favorites?notify=true");
            contentresolver = mContext.getContentResolver();
            cursor = null;
            Cursor cursor1 = contentresolver.query(uri, null, null, null, null);
            cursor = cursor1;
_L2:
            if(cursor == null || cursor.getCount() <= 0)
                break MISSING_BLOCK_LABEL_100;
            int i = copyFromCursor(sqlitedatabase, cursor);
            Exception exception;
            if(i > 0)
                flag = true;
            else
                flag = false;
            cursor.close();
            if(flag)
                contentresolver.delete(uri, null, null);
            if(flag)
            {
                Log.d("Launcher.LauncherProvider", "converted and now triggering widget upgrade");
                convertWidgets(sqlitedatabase);
            }
            return flag;
            exception;
            exception.printStackTrace();
            if(true) goto _L2; else goto _L1
_L1:
            Exception exception1;
            exception1;
            cursor.close();
            throw exception1;
        }

        private void convertWidgets(SQLiteDatabase sqlitedatabase)
        {
            int ai[];
            ArrayList arraylist;
            String s;
            Cursor cursor;
            boolean flag;
            ai = new int[2];
            ai[0] = 1000;
            ai[1] = 1002;
            arraylist = new ArrayList();
            arraylist.add(new ComponentName("com.android.alarmclock", "com.android.alarmclock.AnalogAppWidgetProvider"));
            arraylist.add(new ComponentName("com.android.camera", "com.android.camera.PhotoAppWidgetProvider"));
            s = LauncherProvider.buildOrWhereString("itemType", ai);
            cursor = null;
            flag = false;
            sqlitedatabase.beginTransaction();
            ContentValues contentvalues;
            String as[] = new String[1];
            as[0] = "_id";
            cursor = sqlitedatabase.query("favorites", as, s, null, null, null, null);
            contentvalues = new ContentValues();
_L3:
            if(cursor == null || !cursor.moveToNext()) goto _L2; else goto _L1
_L1:
            long l = cursor.getLong(0);
            int i = mAppWidgetHost.allocateAppWidgetId();
            Log.d("Launcher.LauncherProvider", (new StringBuilder()).append("allocated appWidgetId=").append(i).append(" for favoriteId=").append(l).toString());
            contentvalues.clear();
            contentvalues.put("appWidgetId", Integer.valueOf(i));
            contentvalues.put("spanX", Integer.valueOf(2));
            contentvalues.put("spanY", Integer.valueOf(2));
            sqlitedatabase.update("favorites", contentvalues, (new StringBuilder("_id")).append("=").append(l).toString(), null);
            flag = true;
              goto _L3
            RuntimeException runtimeexception;
            runtimeexception;
            Log.e("Launcher.LauncherProvider", "Problem allocating appWidgetId", runtimeexception);
            runtimeexception.printStackTrace();
              goto _L3
            SQLException sqlexception;
            sqlexception;
            Log.w("Launcher.LauncherProvider", "Problem while allocating appWidgetIds for existing widgets", sqlexception);
            sqlexception.printStackTrace();
            sqlitedatabase.endTransaction();
            if(cursor == null) goto _L5; else goto _L4
_L4:
            cursor.close();
_L5:
            if(flag)
                launchAppWidgetBinder(ai, arraylist);
            return;
_L2:
            sqlitedatabase.setTransactionSuccessful();
            sqlitedatabase.endTransaction();
            if(cursor == null) goto _L5; else goto _L4
            Exception exception;
            exception;
            sqlitedatabase.endTransaction();
            if(cursor != null)
                cursor.close();
            throw exception;
        }

        private int copyFromCursor(SQLiteDatabase sqlitedatabase, Cursor cursor)
        {
            ContentValues acontentvalues[];
            int j4;
            int i = cursor.getColumnIndexOrThrow("_id");
            int j = cursor.getColumnIndexOrThrow("intent");
            int k = cursor.getColumnIndexOrThrow("title");
            int l = cursor.getColumnIndexOrThrow("iconType");
            int i1 = cursor.getColumnIndexOrThrow("icon");
            int j1 = cursor.getColumnIndexOrThrow("iconPackage");
            int k1 = cursor.getColumnIndexOrThrow("iconResource");
            int l1 = cursor.getColumnIndexOrThrow("container");
            int i2 = cursor.getColumnIndexOrThrow("itemType");
            int j2 = cursor.getColumnIndexOrThrow("screen");
            int k2 = cursor.getColumnIndexOrThrow("cellX");
            int l2 = cursor.getColumnIndexOrThrow("cellY");
            int i3 = cursor.getColumnIndexOrThrow("uri");
            int j3 = cursor.getColumnIndexOrThrow("displayMode");
            int k3 = cursor.getColumnIndexOrThrow("appWidgetId");
            acontentvalues = new ContentValues[cursor.getCount()];
            int i4;
            for(int l3 = 0; cursor.moveToNext(); l3 = i4)
            {
                ContentValues contentvalues = new ContentValues(cursor.getColumnCount());
                contentvalues.put("_id", Long.valueOf(cursor.getLong(i)));
                contentvalues.put("intent", cursor.getString(j));
                contentvalues.put("title", cursor.getString(k));
                contentvalues.put("iconType", Integer.valueOf(cursor.getInt(l)));
                contentvalues.put("icon", cursor.getBlob(i1));
                contentvalues.put("iconPackage", cursor.getString(j1));
                contentvalues.put("iconResource", cursor.getString(k1));
                contentvalues.put("container", Integer.valueOf(cursor.getInt(l1)));
                contentvalues.put("itemType", Integer.valueOf(cursor.getInt(i2)));
                contentvalues.put("appWidgetId", Integer.valueOf(cursor.getInt(k3)));
                contentvalues.put("screen", Integer.valueOf(cursor.getInt(j2)));
                contentvalues.put("cellX", Integer.valueOf(cursor.getInt(k2)));
                contentvalues.put("cellY", Integer.valueOf(cursor.getInt(l2)));
                contentvalues.put("uri", cursor.getString(i3));
                contentvalues.put("displayMode", Integer.valueOf(cursor.getInt(j3)));
                i4 = l3 + 1;
                acontentvalues[l3] = contentvalues;
            }

            sqlitedatabase.beginTransaction();
            j4 = 0;
            int k4;
            int l4;
            k4 = acontentvalues.length;
            l4 = 0;
_L5:
            if(l4 >= k4) goto _L2; else goto _L1
_L1:
            long l5 = sqlitedatabase.insert("favorites", null, acontentvalues[l4]);
            if(l5 >= 0L) goto _L4; else goto _L3
_L3:
            int i5;
            i5 = 0;
            sqlitedatabase.endTransaction();
_L6:
            return i5;
_L4:
            j4++;
            l4++;
              goto _L5
_L2:
            sqlitedatabase.setTransactionSuccessful();
            sqlitedatabase.endTransaction();
            i5 = j4;
              goto _L6
            Exception exception;
            exception;
            sqlitedatabase.endTransaction();
            throw exception;
              goto _L5
        }

        private ComponentName getProviderInPackage(String s)
        {
            List list = AppWidgetManager.getInstance(mContext).getInstalledProviders();
            if(list != null) goto _L2; else goto _L1
_L1:
            ComponentName componentname = null;
_L4:
            return componentname;
_L2:
            int i = list.size();
            int j = 0;
            do
            {
                if(j >= i)
                    break;
                ComponentName componentname1 = ((AppWidgetProviderInfo)list.get(j)).provider;
                if(componentname1 != null && componentname1.getPackageName().equals(s))
                {
                    componentname = componentname1;
                    continue; /* Loop/switch isn't completed */
                }
                j++;
            } while(true);
            componentname = null;
            if(true) goto _L4; else goto _L3
_L3:
        }

        private ComponentName getSearchWidgetProvider()
        {
            ComponentName componentname = ((SearchManager)mContext.getSystemService("search")).getGlobalSearchActivity();
            ComponentName componentname1;
            if(componentname == null)
                componentname1 = null;
            else
                componentname1 = getProviderInPackage(componentname.getPackageName());
            return componentname1;
        }

        private void launchAppWidgetBinder(int ai[], ArrayList arraylist)
        {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.LauncherAppWidgetBinder"));
            intent.setFlags(0x10000000);
            Bundle bundle = new Bundle();
            bundle.putIntArray("com.sec.android.app.twlauncher.settings.bindsources", ai);
            bundle.putParcelableArrayList("com.sec.android.app.twlauncher.settings.bindtargets", arraylist);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }

        private boolean loadCustomerFavorites(SQLiteDatabase sqlitedatabase)
        {
            int i;
            mCustomer = LauncherCustomer.getInstance("others.xml", 1);
            i = mCustomer.getCustomerCount();
            if(i > 0) goto _L2; else goto _L1
_L1:
            boolean flag = false;
_L4:
            return flag;
_L2:
            Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            ContentValues contentvalues = new ContentValues();
            PackageManager packagemanager = mContext.getPackageManager();
            int j = 0;
            while(j < i) 
            {
                String as[] = new String[10];
                mCustomer.getCustomerFavoriteInfo(j, as);
                contentvalues.clear();
                contentvalues.put("container", Integer.valueOf(-100));
                contentvalues.put("screen", as[3]);
                contentvalues.put("cellX", as[4]);
                contentvalues.put("cellY", as[5]);
                if("favorite".equals(as[0]))
                    addCSCShortcut(sqlitedatabase, contentvalues, as[1], as[2], as[8], packagemanager, intent);
                else
                if("folder".equals(as[0]))
                    addCSCFolder(sqlitedatabase, contentvalues, as[9]);
                else
                if("search".equals(as[0]))
                    addSearchWidget(sqlitedatabase, contentvalues);
                else
                if("clock".equals(as[0]))
                    addClockWidget(sqlitedatabase, contentvalues);
                else
                if("appwidget".equals(as[0]))
                    addCSCAppWidget(sqlitedatabase, contentvalues, as);
                else
                if("samsungappwidget".equals(as[0]))
                    addCSCSamsungAppWidget(sqlitedatabase, contentvalues, as, packagemanager);
                else
                if("mainapp".equals(as[0]))
                {
                    addCSCMainApp(sqlitedatabase, contentvalues, as[1], as[2], menuIndexfromCSC, packagemanager);
                    menuIndexfromCSC = 1 + menuIndexfromCSC;
                }
                j++;
            }
            flag = true;
            if(true) goto _L4; else goto _L3
_L3:
        }

        private boolean loadCustomerMainApps(SQLiteDatabase sqlitedatabase)
        {
            mCustomer = LauncherCustomer.getInstance("default_mainapplication_order.xml", 2);
            int i = mCustomer.getCustomerCount();
            boolean flag;
            if(i <= 0)
            {
                flag = false;
            } else
            {
                (new Intent("android.intent.action.MAIN", null)).addCategory("android.intent.category.LAUNCHER");
                ContentValues contentvalues = new ContentValues();
                PackageManager packagemanager = mContext.getPackageManager();
                for(int j = 0; j < i; j++)
                {
                    String as[] = new String[2];
                    mCustomer.getCustomerMainAppInfo(j, as);
                    contentvalues.clear();
                    addCSCMainApp(sqlitedatabase, contentvalues, as[0], as[1], menuIndexfromCSC, packagemanager);
                    menuIndexfromCSC = 1 + menuIndexfromCSC;
                }

                flag = true;
            }
            return flag;
        }

        private int loadFavorites(SQLiteDatabase sqlitedatabase)
        {
            Intent intent;
            ContentValues contentvalues;
            PackageManager packagemanager;
            int i;
            intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            contentvalues = new ContentValues();
            packagemanager = mContext.getPackageManager();
            i = 0;
            XmlResourceParser xmlresourceparser;
            android.util.AttributeSet attributeset;
            int j;
            xmlresourceparser = mContext.getResources().getXml(0x7f050002);
            attributeset = Xml.asAttributeSet(xmlresourceparser);
            XmlUtils.beginDocument(xmlresourceparser, "favorites");
            j = xmlresourceparser.getDepth();
_L4:
            int k = xmlresourceparser.next();
            if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1) goto _L2; else goto _L1
_L1:
            if(k != 2) goto _L4; else goto _L3
_L3:
            boolean flag;
            String s;
            TypedArray typedarray;
            flag = false;
            s = xmlresourceparser.getName();
            typedarray = mContext.obtainStyledAttributes(attributeset, R.styleable.Favorite);
            contentvalues.clear();
            contentvalues.put("container", Integer.valueOf(-100));
            contentvalues.put("screen", typedarray.getString(2));
            contentvalues.put("cellX", typedarray.getString(3));
            contentvalues.put("cellY", typedarray.getString(4));
            if(!"favorite".equals(s)) goto _L6; else goto _L5
_L5:
            flag = addAppShortcut(sqlitedatabase, contentvalues, typedarray, packagemanager, intent);
              goto _L7
_L9:
            typedarray.recycle();
              goto _L4
            XmlPullParserException xmlpullparserexception;
            xmlpullparserexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing favorites.", xmlpullparserexception);
            xmlpullparserexception.printStackTrace();
_L2:
            return i;
_L6:
            boolean flag1;
            if("search".equals(s))
            {
                flag = addSearchWidget(sqlitedatabase, contentvalues);
                break; /* Loop/switch isn't completed */
            }
            if("clock".equals(s))
            {
                flag = addClockWidget(sqlitedatabase, contentvalues);
                break; /* Loop/switch isn't completed */
            }
            if("shortcut".equals(s))
            {
                flag = addShortcut(sqlitedatabase, contentvalues, typedarray);
                break; /* Loop/switch isn't completed */
            }
            if("appwidget".equals(s))
            {
                flag = addAppWidget(sqlitedatabase, contentvalues, typedarray);
                break; /* Loop/switch isn't completed */
            }
            if(!"samsungappwidget".equals(s))
                break; /* Loop/switch isn't completed */
            flag1 = addSamsungAppWidget(sqlitedatabase, contentvalues, typedarray, packagemanager);
            flag = flag1;
            break; /* Loop/switch isn't completed */
            IOException ioexception;
            ioexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing favorites.", ioexception);
            ioexception.printStackTrace();
            if(true) goto _L2; else goto _L7
_L7:
            if(!flag) goto _L9; else goto _L8
_L8:
            i++;
              goto _L9
        }

        private int loadMainApps(SQLiteDatabase sqlitedatabase)
        {
            ContentValues contentvalues;
            PackageManager packagemanager;
            int i;
            contentvalues = new ContentValues();
            packagemanager = mContext.getPackageManager();
            i = 0;
            XmlResourceParser xmlresourceparser;
            android.util.AttributeSet attributeset;
            int j;
            xmlresourceparser = mContext.getResources().getXml(0x7f050000);
            attributeset = Xml.asAttributeSet(xmlresourceparser);
            XmlUtils.beginDocument(xmlresourceparser, "mainapps");
            j = xmlresourceparser.getDepth();
_L3:
            boolean flag;
            TypedArray typedarray;
            int k = xmlresourceparser.next();
            if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                break; /* Loop/switch isn't completed */
            if(k != 2)
                continue; /* Loop/switch isn't completed */
            flag = false;
            String s = xmlresourceparser.getName();
            typedarray = mContext.obtainStyledAttributes(attributeset, R.styleable.MainApp);
            contentvalues.clear();
            if("mainapp".equals(s))
                flag = addMainApp(sqlitedatabase, contentvalues, typedarray, packagemanager, i + menuIndexfromCSC);
              goto _L1
_L4:
            typedarray.recycle();
            if(true) goto _L3; else goto _L2
            XmlPullParserException xmlpullparserexception;
            xmlpullparserexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing mainapps.", xmlpullparserexception);
            xmlpullparserexception.printStackTrace();
_L2:
            return i;
            IOException ioexception;
            ioexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing mainapps.", ioexception);
            ioexception.printStackTrace();
            if(true) goto _L2; else goto _L1
_L1:
            if(flag)
                i++;
              goto _L4
        }

        private int loadTopApps(SQLiteDatabase sqlitedatabase)
        {
            ContentValues contentvalues;
            PackageManager packagemanager;
            int i;
            contentvalues = new ContentValues();
            packagemanager = mContext.getPackageManager();
            i = 0;
            XmlResourceParser xmlresourceparser;
            android.util.AttributeSet attributeset;
            int j;
            xmlresourceparser = mContext.getResources().getXml(0x7f050001);
            attributeset = Xml.asAttributeSet(xmlresourceparser);
            XmlUtils.beginDocument(xmlresourceparser, "topapps");
            j = xmlresourceparser.getDepth();
_L3:
            boolean flag;
            TypedArray typedarray;
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break; /* Loop/switch isn't completed */
            } while(k != 2);
            flag = false;
            String s = xmlresourceparser.getName();
            typedarray = mContext.obtainStyledAttributes(attributeset, R.styleable.TopApp);
            contentvalues.clear();
            if("topapp".equals(s))
                flag = addTopApp(sqlitedatabase, contentvalues, typedarray, packagemanager, i);
              goto _L1
_L4:
            typedarray.recycle();
            if(i < 3) goto _L3; else goto _L2
_L2:
            return i;
            XmlPullParserException xmlpullparserexception;
            xmlpullparserexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing topapps.", xmlpullparserexception);
            xmlpullparserexception.printStackTrace();
            continue; /* Loop/switch isn't completed */
            IOException ioexception;
            ioexception;
            Log.w("Launcher.LauncherProvider", "Got exception parsing topapps.", ioexception);
            ioexception.printStackTrace();
            if(true) goto _L2; else goto _L1
_L1:
            if(flag)
                i++;
              goto _L4
        }

        private void sendAppWidgetResetNotify()
        {
            mContext.getContentResolver().notifyChange(LauncherProvider.CONTENT_APPWIDGET_RESET_URI, null);
        }

        public void onCreate(SQLiteDatabase sqlitedatabase)
        {
            Log.d("Launcher.LauncherProvider", "creating new launcher database");
            sqlitedatabase.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,screen INTEGER,cellX INTEGER,cellY INTEGER,spanX INTEGER,spanY INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,isShortcut INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB,uri TEXT,displayMode INTEGER);");
            sqlitedatabase.execSQL("CREATE TABLE gestures (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,itemType INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB);");
            sqlitedatabase.execSQL("CREATE TABLE apps (_id INTEGER PRIMARY KEY,componentname TEXT,top_number INTEGER NOT NULL DEFAULT 65535,page_number INTEGER NOT NULL DEFAULT 65535,cell_number INTEGER NOT NULL DEFAULT 65535);");
            if(mAppWidgetHost != null)
            {
                mAppWidgetHost.deleteHost();
                sendAppWidgetResetNotify();
            }
            SharedPreferences sharedpreferences = mContext.getSharedPreferences("launcher", 0);
            android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.putBoolean("initialDataFeeding", true);
            editor.commit();
            if(!convertDatabase(sqlitedatabase))
            {
                if(!loadCustomerFavorites(sqlitedatabase))
                    loadFavorites(sqlitedatabase);
                loadTopApps(sqlitedatabase);
                if(!loadCustomerMainApps(sqlitedatabase))
                    loadMainApps(sqlitedatabase);
            }
            android.content.SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.putBoolean("initialDataFeeding", false);
            editor1.commit();
        }

        public void onOpen(SQLiteDatabase sqlitedatabase)
        {
            Log.d("Launcher.LauncherProvider", "SQLiteDatabase.onOpen()");
            super.onOpen(sqlitedatabase);
            if(mContext.getSharedPreferences("launcher", 0).getBoolean("initialDataFeeding", false))
            {
                sqlitedatabase.execSQL("DROP TABLE favorites;");
                sqlitedatabase.execSQL("DROP TABLE gestures;");
                sqlitedatabase.execSQL("DROP TABLE apps;");
                onCreate(sqlitedatabase);
            }
        }

        public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j)
        {
            int k;
            Log.d("Launcher.LauncherProvider", "onUpgrade triggered");
            k = i;
            if(k >= 3) goto _L2; else goto _L1
_L1:
            sqlitedatabase.beginTransaction();
            sqlitedatabase.execSQL("ALTER TABLE favorites ADD COLUMN appWidgetId INTEGER NOT NULL DEFAULT -1;");
            sqlitedatabase.setTransactionSuccessful();
            k = 3;
_L8:
            sqlitedatabase.endTransaction();
            if(k == 3)
                convertWidgets(sqlitedatabase);
_L2:
            if(k >= 4) goto _L4; else goto _L3
_L3:
            sqlitedatabase.beginTransaction();
            sqlitedatabase.execSQL("CREATE TABLE gestures (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,itemType INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB);");
            sqlitedatabase.setTransactionSuccessful();
            k = 4;
_L6:
            sqlitedatabase.endTransaction();
_L4:
            if(k != 8)
            {
                Log.w("Launcher.LauncherProvider", "Destroying all old data.");
                sqlitedatabase.execSQL("DROP TABLE IF EXISTS favorites");
                sqlitedatabase.execSQL("DROP TABLE IF EXISTS gestures");
                sqlitedatabase.execSQL("DROP TABLE IF EXISTS apps");
                onCreate(sqlitedatabase);
            }
            return;
            SQLException sqlexception1;
            sqlexception1;
            Log.e("Launcher.LauncherProvider", sqlexception1.getMessage(), sqlexception1);
            sqlexception1.printStackTrace();
            continue; /* Loop/switch isn't completed */
            Exception exception1;
            exception1;
            sqlitedatabase.endTransaction();
            throw exception1;
            SQLException sqlexception;
            sqlexception;
            Log.e("Launcher.LauncherProvider", sqlexception.getMessage(), sqlexception);
            sqlexception.printStackTrace();
            if(true) goto _L6; else goto _L5
_L5:
            Exception exception;
            exception;
            sqlitedatabase.endTransaction();
            throw exception;
            if(true) goto _L8; else goto _L7
_L7:
        }

        private final AppWidgetHost mAppWidgetHost;
        private final Context mContext;
        private LauncherCustomer mCustomer;
        private int menuIndexfromCSC;

        DatabaseHelper(Context context)
        {
            super(context, "launcher.db", null, 8);
            menuIndexfromCSC = 0;
            mContext = context;
            mAppWidgetHost = new AppWidgetHost(context, 1024);
        }
    }


    public LauncherProvider()
    {
    }

    static String buildOrWhereString(String s, int ai[])
    {
        StringBuilder stringbuilder = new StringBuilder();
        for(int i = ai.length - 1; i >= 0; i--)
        {
            stringbuilder.append(s).append("=").append(ai[i]);
            if(i > 0)
                stringbuilder.append(" OR ");
        }

        return stringbuilder.toString();
    }

    private void sendNotify(Uri uri)
    {
        String s = uri.getQueryParameter("notify");
        if(s == null || "true".equals(s))
            getContext().getContentResolver().notifyChange(uri, null);
    }

    public int bulkInsert(Uri uri, ContentValues acontentvalues[])
    {
        Object obj = LauncherModel.mDBLock;
        obj;
        JVM INSTR monitorenter ;
        SqlArguments sqlarguments;
        SQLiteDatabase sqlitedatabase;
        sqlarguments = new SqlArguments(uri);
        sqlitedatabase = mOpenHelper.getWritableDatabase();
        sqlitedatabase.beginTransaction();
        int i;
        int j;
        i = acontentvalues.length;
        j = 0;
_L5:
        if(j >= i) goto _L2; else goto _L1
_L1:
        long l1 = sqlitedatabase.insert(sqlarguments.table, null, acontentvalues[j]);
        if(l1 >= 0L) goto _L4; else goto _L3
_L3:
        sqlitedatabase.endTransaction();
        obj;
        JVM INSTR monitorexit ;
        int l = 0;
_L6:
        return l;
_L4:
        j++;
          goto _L5
_L2:
        sqlitedatabase.setTransactionSuccessful();
        int k;
        sqlitedatabase.endTransaction();
        sendNotify(uri);
        k = acontentvalues.length;
        obj;
        JVM INSTR monitorexit ;
        l = k;
          goto _L6
        Exception exception1;
        exception1;
        sqlitedatabase.endTransaction();
        throw exception1;
        Exception exception;
        exception;
        throw exception;
          goto _L5
    }

    public int delete(Uri uri, String s, String as[])
    {
        Object obj = LauncherModel.mDBLock;
        obj;
        JVM INSTR monitorenter ;
        SqlArguments sqlarguments = new SqlArguments(uri, s, as);
        int i = mOpenHelper.getWritableDatabase().delete(sqlarguments.table, sqlarguments.where, sqlarguments.args);
        if(i > 0)
            sendNotify(uri);
        return i;
    }

    public String getType(Uri uri)
    {
        SqlArguments sqlarguments = new SqlArguments(uri, null, null);
        String s;
        if(TextUtils.isEmpty(sqlarguments.where))
            s = (new StringBuilder()).append("vnd.android.cursor.dir/").append(sqlarguments.table).toString();
        else
            s = (new StringBuilder()).append("vnd.android.cursor.item/").append(sqlarguments.table).toString();
        return s;
    }

    public Uri insert(Uri uri, ContentValues contentvalues)
    {
        Object obj = LauncherModel.mDBLock;
        obj;
        JVM INSTR monitorenter ;
        SqlArguments sqlarguments = new SqlArguments(uri);
        long l = mOpenHelper.getWritableDatabase().insert(sqlarguments.table, null, contentvalues);
        Uri uri2;
        if(l <= 0L)
        {
            uri2 = null;
        } else
        {
            Uri uri1 = ContentUris.withAppendedId(uri, l);
            sendNotify(uri1);
            uri2 = uri1;
        }
        return uri2;
    }

    public boolean onCreate()
    {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String as[], String s, String as1[], String s1)
    {
        Object obj = LauncherModel.mDBLock;
        obj;
        JVM INSTR monitorenter ;
        SqlArguments sqlarguments = new SqlArguments(uri, s, as1);
        SQLiteQueryBuilder sqlitequerybuilder = new SQLiteQueryBuilder();
        sqlitequerybuilder.setTables(sqlarguments.table);
        Cursor cursor = sqlitequerybuilder.query(mOpenHelper.getWritableDatabase(), as, sqlarguments.where, sqlarguments.args, null, null, s1);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public int update(Uri uri, ContentValues contentvalues, String s, String as[])
    {
        Object obj = LauncherModel.mDBLock;
        obj;
        JVM INSTR monitorenter ;
        SqlArguments sqlarguments = new SqlArguments(uri, s, as);
        int i = mOpenHelper.getWritableDatabase().update(sqlarguments.table, contentvalues, sqlarguments.where, sqlarguments.args);
        if(i > 0)
            sendNotify(uri);
        return i;
    }

    static final Uri CONTENT_APPWIDGET_RESET_URI = Uri.parse("content://com.sec.android.app.twlauncher.settings/appWidgetReset");
    private SQLiteOpenHelper mOpenHelper;

}
