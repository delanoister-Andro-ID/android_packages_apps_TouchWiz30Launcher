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
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ItemInfo, IconCache, ShortcutInfo

class ApplicationInfo extends ItemInfo
{

    ApplicationInfo()
    {
        topNum = 65535;
        pageNum = 65535;
        cellNum = 65535;
        editTopNum = 65535;
        editPageNum = 65535;
        editCellNum = 65535;
        super.itemType = 1;
    }

    public ApplicationInfo(ResolveInfo resolveinfo, IconCache iconcache)
    {
        topNum = 65535;
        pageNum = 65535;
        cellNum = 65535;
        editTopNum = 65535;
        editPageNum = 65535;
        editCellNum = 65535;
        componentName = new ComponentName(resolveinfo.activityInfo.applicationInfo.packageName, resolveinfo.activityInfo.name);
        super.container = -1L;
        setActivity(componentName, 0x10200000);
        iconcache.getTitleAndIcon(this, resolveinfo);
    }

    public ShortcutInfo makeShortcut()
    {
        return new ShortcutInfo(this);
    }

    final void setActivity(ComponentName componentname, int i)
    {
        intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentname);
        intent.setFlags(i);
        super.itemType = 0;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ApplicationInfo(title=").append(title.toString()).append(")").toString();
    }

    int badgeCount;
    int cellNum;
    ComponentName componentName;
    int editCellNum;
    int editPageNum;
    int editTopNum;
    Bitmap iconBgBitmap;
    Bitmap iconBitmap;
    Intent intent;
    boolean isUpdated;
    int pageNum;
    boolean systemApp;
    CharSequence title;
    Bitmap titleBitmap;
    int topNum;
}
