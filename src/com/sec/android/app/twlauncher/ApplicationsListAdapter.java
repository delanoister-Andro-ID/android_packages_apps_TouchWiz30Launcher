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
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherApplication, ApplicationInfo, FastBitmapDrawable, Utilities, 
//            BadgeCache

public class ApplicationsListAdapter extends ArrayAdapter
{

    public ApplicationsListAdapter(Context context, ArrayList arraylist)
    {
        super(context, 0, arraylist);
        mInflater = LayoutInflater.from(context);
        mBadgeCache = ((LauncherApplication)context.getApplicationContext()).getBadgeCache();
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        ApplicationInfo applicationinfo = (ApplicationInfo)getItem(i);
        if(view == null)
            view = mInflater.inflate(0x7f030007, viewgroup, false);
        LinearLayout linearlayout = (LinearLayout)view;
        ((TextView)linearlayout.findViewById(0x7f06000b)).setText(applicationinfo.title);
        ImageView imageview = (ImageView)linearlayout.findViewById(0x7f06000a);
        imageview.setImageDrawable(new FastBitmapDrawable(applicationinfo.iconBitmap));
        String s = null;
        String s1 = null;
        if(applicationinfo.intent != null && applicationinfo.intent.getComponent() != null)
        {
            s = applicationinfo.intent.getComponent().getPackageName();
            s1 = applicationinfo.intent.getComponent().getClassName();
        }
        imageview.setBackgroundDrawable(Utilities.getDrawableIconBg(s, s1, getContext(), applicationinfo.systemApp));
        if(applicationinfo.intent != null && applicationinfo.intent.getComponent() != null)
            applicationinfo.badgeCount = mBadgeCache.getBadgeCount(applicationinfo.intent.getComponent());
        view.setTag(applicationinfo);
        return view;
    }

    private final BadgeCache mBadgeCache;
    private final LayoutInflater mInflater;
}
