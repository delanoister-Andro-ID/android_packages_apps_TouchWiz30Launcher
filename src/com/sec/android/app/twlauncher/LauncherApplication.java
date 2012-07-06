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

import android.app.Application;
import android.content.*;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;
import dalvik.system.VMRuntime;

// Referenced classes of package com.sec.android.app.twlauncher:
//            IconCache, BadgeCache, LauncherModel, Launcher

public class LauncherApplication extends Application
{

    public LauncherApplication()
    {
    }

    BadgeCache getBadgeCache()
    {
        return mBadgeCache;
    }

    IconCache getIconCache()
    {
        return mIconCache;
    }

    LauncherModel getModel()
    {
        return mModel;
    }

    public void onCreate()
    {
        VMRuntime.getRuntime().setMinimumHeapSize(0x800000L);
        super.onCreate();
        mIconCache = new IconCache(this);
        mBadgeCache = new BadgeCache(this);
        mModel = new LauncherModel(this, mIconCache, mBadgeCache);
        IntentFilter intentfilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentfilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentfilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentfilter.addDataScheme("package");
        registerReceiver(mModel, intentfilter);
        IntentFilter intentfilter1 = new IntentFilter();
        intentfilter1.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        intentfilter1.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        registerReceiver(mModel, intentfilter1);
        getContentResolver().registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true, mFavoritesObserver);
        IntentFilter intentfilter2 = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent)
            {
                mSystemShuttingDown = true;
            }

            final LauncherApplication this$0;

            
            {
                this$0 = LauncherApplication.this;
                super();
            }
        }
, intentfilter2);
    }

    public void onTerminate()
    {
        super.onTerminate();
        unregisterReceiver(mModel);
        getContentResolver().unregisterContentObserver(mFavoritesObserver);
    }

    LauncherModel setLauncher(Launcher launcher)
    {
        mModel.initialize(launcher);
        return mModel;
    }

    public BadgeCache mBadgeCache;
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {

        public void onChange(boolean flag)
        {
            if(mSystemShuttingDown)
                Log.d("LauncherApplication", "Ignoring favorite change, because the system is shutting down");
            else
                mModel.startLoader(LauncherApplication.this, true);
        }

        final LauncherApplication this$0;

            
            {
                this$0 = LauncherApplication.this;
                super(handler);
            }
    }
;
    public IconCache mIconCache;
    public LauncherModel mModel;
    boolean mSystemShuttingDown;
}
