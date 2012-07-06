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
import android.database.Cursor;
import android.widget.Toast;
import java.lang.reflect.Array;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Launcher, CellLayout, LauncherModel, LauncherApplication

public class InstallShortcutReceiver extends BroadcastReceiver
{

    public InstallShortcutReceiver()
    {
    }

    private static boolean findEmptyCell(Context context, int ai[], int i)
    {
        int j;
        int k;
        boolean aflag[][];
        Cursor cursor;
        int l;
        int i1;
        int j1;
        int k1;
        j = Launcher.NUMBER_CELLS_X;
        k = Launcher.NUMBER_CELLS_Y;
        int ai1[] = new int[2];
        ai1[0] = j;
        ai1[1] = k;
        aflag = (boolean[][])Array.newInstance(Boolean.TYPE, ai1);
        ContentResolver contentresolver = context.getContentResolver();
        android.net.Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        String as[] = new String[4];
        as[0] = "cellX";
        as[1] = "cellY";
        as[2] = "spanX";
        as[3] = "spanY";
        String as1[] = new String[1];
        as1[0] = String.valueOf(i);
        cursor = contentresolver.query(uri, as, "screen=?", as1, null);
        l = cursor.getColumnIndexOrThrow("cellX");
        i1 = cursor.getColumnIndexOrThrow("cellY");
        j1 = cursor.getColumnIndexOrThrow("spanX");
        k1 = cursor.getColumnIndexOrThrow("spanY");
_L8:
        int l1;
        int i2;
        int j2;
        int k2;
        int l2;
        if(cursor.moveToNext())
        {
            l1 = cursor.getInt(l);
            i2 = cursor.getInt(i1);
            j2 = cursor.getInt(j1);
            k2 = cursor.getInt(k1);
            l2 = l1;
            continue; /* Loop/switch isn't completed */
        }
          goto _L1
_L4:
        int i3;
        if(i3 >= i2 + k2 || i3 >= k) goto _L3; else goto _L2
_L2:
        aflag[l2][i3] = true;
        i3++;
          goto _L4
_L3:
        l2++;
        continue; /* Loop/switch isn't completed */
        Exception exception1;
        exception1;
        boolean flag;
        cursor.close();
        flag = false;
_L6:
        return flag;
        Exception exception;
        exception;
        cursor.close();
        throw exception;
_L1:
        cursor.close();
        flag = CellLayout.findVacantCell(ai, 1, 1, j, k, aflag);
        if(true) goto _L6; else goto _L5
_L5:
        if(l2 >= l1 + j2 || l2 >= j) goto _L8; else goto _L7
_L7:
        i3 = i2;
          goto _L4
        if(true) goto _L8; else goto _L9
_L9:
    }

    private boolean installShortcut(Context context, Intent intent, int i)
    {
        String s = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        boolean flag;
        if(findEmptyCell(context, mCoordinates, i))
        {
            CellLayout.CellInfo cellinfo = new CellLayout.CellInfo();
            cellinfo.cellX = mCoordinates[0];
            cellinfo.cellY = mCoordinates[1];
            cellinfo.screen = i;
            Intent intent1 = (Intent)intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            if(intent1.getAction() == null)
                intent1.setAction("android.intent.action.VIEW");
            if(intent1 == null || s == null)
            {
                flag = false;
            } else
            {
                if(intent.getBooleanExtra("duplicate", true) || !LauncherModel.shortcutExists(context, s, intent1))
                {
                    ((LauncherApplication)context.getApplicationContext()).getModel().addShortcut(context, intent, cellinfo, true);
                    Object aobj[] = new Object[1];
                    aobj[0] = s;
                    Toast.makeText(context, context.getString(0x7f0a001f, aobj), 0).show();
                } else
                {
                    Object aobj1[] = new Object[1];
                    aobj1[0] = s;
                    Toast.makeText(context, context.getString(0x7f0a0021, aobj1), 0).show();
                }
                flag = true;
            }
        } else
        {
            Toast.makeText(context, context.getString(0x7f0a001e), 0).show();
            flag = false;
        }
        return flag;
    }

    public void onReceive(Context context, Intent intent)
    {
        if("com.android.launcher.action.INSTALL_SHORTCUT".equals(intent.getAction())) goto _L2; else goto _L1
_L1:
        return;
_L2:
        int i = Launcher.getScreen();
        mPrefs = context.getSharedPreferences("launcher", 0);
        int j;
        if(mPrefs == null)
            j = Launcher.SCREEN_COUNT;
        else
            j = mPrefs.getInt("screencount", DEFAULT_SCREEN_COUNT);
        if(!installShortcut(context, intent, i))
        {
            int k = 0;
            while(k < j && (k == i || !installShortcut(context, intent, k))) 
                k++;
        }
        if(true) goto _L1; else goto _L3
_L3:
    }

    static int DEFAULT_SCREEN_COUNT = 7;
    private final int mCoordinates[] = new int[2];
    private SharedPreferences mPrefs;

}
