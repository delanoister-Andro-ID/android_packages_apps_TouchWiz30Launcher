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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import java.util.HashMap;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherApplication, Launcher

public class BadgeCache
{
    private static class CacheEntry
    {

        public int badgeCount;
        public Bitmap icon;

        private CacheEntry()
        {
        }

    }


    public BadgeCache(LauncherApplication launcherapplication)
    {
        mContext = launcherapplication;
        mPackageManager = launcherapplication.getPackageManager();
    }

    private CacheEntry cacheLocked(ComponentName componentname)
    {
        return (CacheEntry)mCache.get(componentname);
    }

    public int getBadgeCount(ComponentName componentname)
    {
        int i = 0;
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        int j;
        if(componentname == null)
        {
            j = 0;
        } else
        {
            CacheEntry cacheentry = cacheLocked(componentname);
            if(cacheentry != null)
                i = cacheentry.badgeCount;
            j = i;
        }
        return j;
    }

    public int getBadgeCount(Intent intent)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        android.content.pm.ResolveInfo resolveinfo = mPackageManager.resolveActivity(intent, 0);
        ComponentName componentname = intent.getComponent();
        int i;
        if(resolveinfo == null || componentname == null)
        {
            i = 0;
        } else
        {
            CacheEntry cacheentry = cacheLocked(componentname);
            int j;
            if(cacheentry != null)
                j = cacheentry.badgeCount;
            else
                j = 0;
            i = j;
        }
        return i;
    }

    public Bitmap getBadgeIcon(ComponentName componentname)
    {
        Bitmap bitmap = null;
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        Bitmap bitmap1;
        if(componentname == null)
        {
            bitmap1 = null;
        } else
        {
            CacheEntry cacheentry = cacheLocked(componentname);
            if(cacheentry != null)
                bitmap = cacheentry.icon;
            bitmap1 = bitmap;
        }
        return bitmap1;
    }

    public void setBadgeCount(ComponentName componentname, int i, Bitmap bitmap)
    {
        HashMap hashmap = mCache;
        hashmap;
        JVM INSTR monitorenter ;
        CacheEntry cacheentry;
        Bitmap bitmap1;
        cacheentry = cacheLocked(componentname);
        if(i <= 0)
            break MISSING_BLOCK_LABEL_252;
        bitmap1 = null;
        if(Launcher.USE_MAINMENU_ICONMODE && bitmap != null)
        {
            Drawable drawable = mContext.getResources().getDrawable(0x7f02008d);
            int j = drawable.getIntrinsicWidth();
            int k = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, j, k);
            bitmap1 = Bitmap.createBitmap(j, k, android.graphics.Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap1);
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_ATOP));
            drawable.draw(canvas);
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, j, k, false), 0.0F, 0.0F, paint);
        }
        if(cacheentry != null) goto _L2; else goto _L1
_L1:
        CacheEntry cacheentry1 = new CacheEntry();
        cacheentry1.badgeCount = i;
        if(Launcher.USE_MAINMENU_ICONMODE && bitmap1 != null)
            cacheentry1.icon = bitmap1;
        else
            cacheentry1.icon = bitmap;
        mCache.put(componentname, cacheentry1);
_L3:
        hashmap;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
_L2:
        cacheentry.badgeCount = i;
        if(Launcher.USE_MAINMENU_ICONMODE && bitmap1 != null)
            cacheentry.icon = bitmap1;
        else
            cacheentry.icon = bitmap;
          goto _L3
        if(cacheentry != null)
            mCache.remove(componentname);
          goto _L3
    }

    private final HashMap mCache = new HashMap(50);
    private final LauncherApplication mContext;
    private final PackageManager mPackageManager;
}
