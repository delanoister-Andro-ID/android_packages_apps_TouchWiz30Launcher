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
import android.graphics.Bitmap;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ItemInfo, ApplicationInfo, IconCache

class ShortcutInfo extends ItemInfo
{

    ShortcutInfo()
    {
        super.itemType = 1;
    }

    public ShortcutInfo(ApplicationInfo applicationinfo)
    {
        super(applicationinfo);
        title = applicationinfo.title.toString();
        intent = new Intent(applicationinfo.intent);
        customIcon = false;
        badgeCount = applicationinfo.badgeCount;
    }

    public Bitmap getIcon(IconCache iconcache)
    {
        if(mIcon == null)
            mIcon = iconcache.getIcon(intent);
        return mIcon;
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        super.onAddToDatabase(contentvalues);
        String s;
        String s1;
        if(title != null)
            s = title.toString();
        else
            s = null;
        contentvalues.put("title", s);
        if(intent != null)
            s1 = intent.toUri(0);
        else
            s1 = null;
        contentvalues.put("intent", s1);
        if(!customIcon) goto _L2; else goto _L1
_L1:
        contentvalues.put("iconType", Integer.valueOf(1));
        writeBitmap(contentvalues, mIcon);
_L4:
        return;
_L2:
        if(onExternalStorage && !usingFallbackIcon)
            writeBitmap(contentvalues, mIcon);
        contentvalues.put("iconType", Integer.valueOf(0));
        if(iconResource != null)
        {
            contentvalues.put("iconPackage", iconResource.packageName);
            contentvalues.put("iconResource", iconResource.resourceName);
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    final void setActivity(ComponentName componentname, int i)
    {
        intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentname);
        intent.setFlags(i);
        super.itemType = 0;
    }

    public void setIcon(Bitmap bitmap)
    {
        mIcon = bitmap;
    }

    public String toString()
    {
        return (new StringBuilder()).append("ShortcutInfo(title=").append(title.toString()).append(")").toString();
    }

    void unbind()
    {
        super.unbind();
    }

    int badgeCount;
    boolean customIcon;
    android.content.Intent.ShortcutIconResource iconResource;
    Intent intent;
    private Bitmap mIcon;
    boolean onExternalStorage;
    CharSequence title;
    boolean usingFallbackIcon;
}
