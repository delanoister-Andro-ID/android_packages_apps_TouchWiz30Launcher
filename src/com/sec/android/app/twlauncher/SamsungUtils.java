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
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.telephony.gsm.stk.StkEventDownload;

final class SamsungUtils
{

    private SamsungUtils()
    {
    }

    static void acquireDVFSlock(int i, int j)
    {
    }

    static void broadcastStkIntent(Context context)
    {
        if(SystemProperties.getBoolean("gsm.sim.screenEvent", false))
        {
            StkEventDownload stkeventdownload = new StkEventDownload(5);
            Intent intent = new Intent("android.intent.action.stk.event");
            intent.putExtra("STK EVENT", stkeventdownload);
            context.sendBroadcast(intent);
            Log.i("SamsungUtils", (new StringBuilder()).append("sendBroadcast intent!!!!!!!!!!!!!!! = ").append(intent).toString());
        }
    }

    static void setWallpaperVisibility(WallpaperManager wallpapermanager, boolean flag)
    {
    }
}
