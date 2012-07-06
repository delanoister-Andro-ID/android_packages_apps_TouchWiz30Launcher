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

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.*;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import java.util.HashMap;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherApplication, Utilities, ApplicationInfo

public class IconCache
{
    private static class CacheEntry
    {

        public Bitmap icon;
        public Bitmap iconBgBitmap;
        public String title;
        public Bitmap titleBitmap;

        private CacheEntry()
        {
        }

    }


    public IconCache(LauncherApplication launcherapplication)
    {
        mContext = launcherapplication;
        mPackageManager = launcherapplication.getPackageManager();
        mBubble = new Utilities.BubbleText(launcherapplication);
    }

    private CacheEntry cacheLocked(ComponentName componentname, ResolveInfo resolveinfo)
    {
        CacheEntry cacheentry = (CacheEntry)mCache.get(componentname);
        if(cacheentry == null)
        {
            cacheentry = new CacheEntry();
            mCache.put(componentname, cacheentry);
            cacheentry.title = resolveinfo.loadLabel(mPackageManager).toString();
            if(cacheentry.title == null)
                cacheentry.title = resolveinfo.activityInfo.name;
            if(cacheentry.icon == null)
                cacheentry.icon = Utilities.createIconBitmap(resolveinfo.activityInfo.loadIcon(mPackageManager), mContext);
        }
        return cacheentry;
    }

    private Bitmap makeDefaultIcon()
    {
        Drawable drawable = mPackageManager.getDefaultActivityIcon();
        Bitmap bitmap = Bitmap.createBitmap(Math.max(drawable.getIntrinsicWidth(), 1), Math.max(drawable.getIntrinsicHeight(), 1), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void flush()
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        mCache.clear();
        return;
    }

    public Bitmap getIcon(ComponentName componentname, ResolveInfo resolveinfo)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        Bitmap bitmap;
        if(resolveinfo == null || componentname == null)
        {
            bitmap = null;
        } else
        {
            Bitmap bitmap1 = cacheLocked(componentname, resolveinfo).icon;
            bitmap = bitmap1;
        }
        return bitmap;
    }

    public Bitmap getIcon(Intent intent)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        ResolveInfo resolveinfo = mPackageManager.resolveActivity(intent, 0);
        ComponentName componentname = intent.getComponent();
        Bitmap bitmap1;
        if(resolveinfo == null || componentname == null)
        {
            Bitmap bitmap = mDefaultIcon;
            bitmap1 = bitmap;
        } else
        {
            Bitmap bitmap2 = cacheLocked(componentname, resolveinfo).icon;
            bitmap1 = bitmap2;
        }
        return bitmap1;
    }

    public void getTitleAndIcon(com.sec.android.app.twlauncher.ApplicationInfo applicationinfo, ResolveInfo resolveinfo)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        CacheEntry cacheentry = cacheLocked(applicationinfo.componentName, resolveinfo);
        if(cacheentry.titleBitmap == null)
            cacheentry.titleBitmap = mBubble.createTextBitmap(cacheentry.title);
        applicationinfo.title = cacheentry.title;
        applicationinfo.titleBitmap = cacheentry.titleBitmap;
        applicationinfo.iconBitmap = cacheentry.icon;
        applicationinfo.iconBgBitmap = cacheentry.iconBgBitmap;
        int i = resolveinfo.activityInfo.applicationInfo.flags;
        if((i & 1) != 0 || (i & 0x80) != 0)
            applicationinfo.systemApp = true;
        else
            applicationinfo.systemApp = false;
        return;
    }

    public void remove(ComponentName componentname)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        mCache.remove(componentname);
        return;
    }

    private final Utilities.BubbleText mBubble;
    private final HashMap mCache = new HashMap(50);
    private final LauncherApplication mContext;
    private final Bitmap mDefaultIcon = makeDefaultIcon();
    private final PackageManager mPackageManager;
}
