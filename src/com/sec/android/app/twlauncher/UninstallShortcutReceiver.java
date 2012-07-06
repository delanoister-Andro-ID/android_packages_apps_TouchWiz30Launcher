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
import java.net.URISyntaxException;

public class UninstallShortcutReceiver extends BroadcastReceiver
{

    public UninstallShortcutReceiver()
    {
    }

    public void onReceive(Context context, Intent intent)
    {
        if("com.android.launcher.action.UNINSTALL_SHORTCUT".equals(intent.getAction())) goto _L2; else goto _L1
_L1:
        return;
_L2:
        Intent intent1;
        String s;
        boolean flag;
        intent1 = (Intent)intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        s = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        flag = intent.getBooleanExtra("duplicate", true);
        if(intent1 == null || s == null) goto _L1; else goto _L3
_L3:
        ContentResolver contentresolver;
        Cursor cursor;
        int i;
        int j;
        boolean flag1;
        contentresolver = context.getContentResolver();
        android.net.Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        String as[] = new String[2];
        as[0] = "_id";
        as[1] = "intent";
        String as1[] = new String[1];
        as1[0] = s;
        cursor = contentresolver.query(uri, as, "title=?", as1, null);
        i = cursor.getColumnIndexOrThrow("intent");
        j = cursor.getColumnIndexOrThrow("_id");
        flag1 = false;
_L7:
        boolean flag2 = cursor.moveToNext();
        if(!flag2) goto _L5; else goto _L4
_L4:
        if(!intent1.filterEquals(Intent.parseUri(cursor.getString(i), 0))) goto _L7; else goto _L6
_L6:
        contentresolver.delete(LauncherSettings.Favorites.getContentUri(cursor.getLong(j), false), null, null);
        flag1 = true;
        if(flag) goto _L7; else goto _L5
_L5:
        cursor.close();
        if(flag1)
        {
            contentresolver.notifyChange(LauncherSettings.Favorites.CONTENT_URI, null);
            Object aobj[] = new Object[1];
            aobj[0] = s;
            Toast.makeText(context, context.getString(0x7f0a0020, aobj), 0).show();
        }
          goto _L1
        Exception exception;
        exception;
        cursor.close();
        throw exception;
        URISyntaxException urisyntaxexception;
        urisyntaxexception;
          goto _L7
    }
}
