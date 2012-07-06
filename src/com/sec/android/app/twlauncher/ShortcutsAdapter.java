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

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherApplication, ShortcutInfo, MenuItemView, FastBitmapDrawable, 
//            BadgeCache, IconCache

public class ShortcutsAdapter extends ArrayAdapter
{

    public ShortcutsAdapter(Context context, ArrayList arraylist)
    {
        super(context, 0, arraylist);
        mInflater = LayoutInflater.from(context);
        mIconCache = ((LauncherApplication)context.getApplicationContext()).getIconCache();
        mBadgeCache = ((LauncherApplication)context.getApplicationContext()).getBadgeCache();
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        ShortcutInfo shortcutinfo = (ShortcutInfo)getItem(i);
        if(view == null)
            view = mInflater.inflate(0x7f030002, viewgroup, false);
        MenuItemView menuitemview = (MenuItemView)view;
        menuitemview.setImageDrawable(new FastBitmapDrawable(shortcutinfo.getIcon(mIconCache)));
        menuitemview.setText(shortcutinfo.title);
        menuitemview.setTag(shortcutinfo);
        shortcutinfo.badgeCount = mBadgeCache.getBadgeCount(shortcutinfo.intent);
        return view;
    }

    private final BadgeCache mBadgeCache;
    private final IconCache mIconCache;
    private final LayoutInflater mInflater;
}
