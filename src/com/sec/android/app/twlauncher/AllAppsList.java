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
import android.content.pm.*;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ApplicationInfo, IconCache

class AllAppsList
{

    public AllAppsList(IconCache iconcache)
    {
        data = new ArrayList(42);
        added = new ArrayList(42);
        removed = new ArrayList();
        modified = new ArrayList();
        mIconCache = iconcache;
    }

    private static List findActivitiesForPackage(Context context, String s)
    {
        PackageManager packagemanager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN", null);
        intent.addCategory("android.intent.category.LAUNCHER");
        List list = packagemanager.queryIntentActivities(intent, 0);
        ArrayList arraylist = new ArrayList();
        if(list != null)
        {
            int i = list.size();
            for(int j = 0; j < i; j++)
            {
                ResolveInfo resolveinfo = (ResolveInfo)list.get(j);
                if(s.equals(resolveinfo.activityInfo.packageName))
                    arraylist.add(resolveinfo);
            }

        }
        return arraylist;
    }

    private static boolean findActivity(ArrayList arraylist, ComponentName componentname)
    {
        int i;
        int j;
        i = arraylist.size();
        j = 0;
_L3:
        if(j >= i)
            break MISSING_BLOCK_LABEL_42;
        if(!((com.sec.android.app.twlauncher.ApplicationInfo)arraylist.get(j)).componentName.equals(componentname)) goto _L2; else goto _L1
_L1:
        boolean flag = true;
_L4:
        return flag;
_L2:
        j++;
          goto _L3
        flag = false;
          goto _L4
    }

    private static boolean findActivity(List list, ComponentName componentname)
    {
        String s;
        Iterator iterator;
        s = componentname.getClassName();
        iterator = list.iterator();
_L4:
        if(!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        if(!((ResolveInfo)iterator.next()).activityInfo.name.equals(s)) goto _L4; else goto _L3
_L3:
        boolean flag = true;
_L6:
        return flag;
_L2:
        flag = false;
        if(true) goto _L6; else goto _L5
_L5:
    }

    private com.sec.android.app.twlauncher.ApplicationInfo findApplicationInfoLocked(String s, String s1)
    {
        Iterator iterator = data.iterator();
_L4:
        if(!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        com.sec.android.app.twlauncher.ApplicationInfo applicationinfo1;
        ComponentName componentname;
        applicationinfo1 = (com.sec.android.app.twlauncher.ApplicationInfo)iterator.next();
        componentname = applicationinfo1.intent.getComponent();
        if(!s.equals(componentname.getPackageName()) || !s1.equals(componentname.getClassName())) goto _L4; else goto _L3
_L3:
        com.sec.android.app.twlauncher.ApplicationInfo applicationinfo = applicationinfo1;
_L6:
        return applicationinfo;
_L2:
        applicationinfo = null;
        if(true) goto _L6; else goto _L5
_L5:
    }

    public void add(com.sec.android.app.twlauncher.ApplicationInfo applicationinfo)
    {
        if(!findActivity(data, applicationinfo.componentName))
        {
            data.add(applicationinfo);
            added.add(applicationinfo);
        }
    }

    public void addPackage(Context context, String s)
    {
        List list = findActivitiesForPackage(context, s);
        if(list.size() > 0)
        {
            for(Iterator iterator = list.iterator(); iterator.hasNext(); add(new com.sec.android.app.twlauncher.ApplicationInfo((ResolveInfo)iterator.next(), mIconCache)));
        }
    }

    public void clear()
    {
        data.clear();
        added.clear();
        removed.clear();
        modified.clear();
    }

    public com.sec.android.app.twlauncher.ApplicationInfo get(int i)
    {
        return (com.sec.android.app.twlauncher.ApplicationInfo)data.get(i);
    }

    public void removePackage(String s)
    {
        ArrayList arraylist = data;
        for(int i = arraylist.size() - 1; i >= 0; i--)
        {
            com.sec.android.app.twlauncher.ApplicationInfo applicationinfo = (com.sec.android.app.twlauncher.ApplicationInfo)arraylist.get(i);
            if(s.equals(applicationinfo.intent.getComponent().getPackageName()))
            {
                removed.add(applicationinfo);
                arraylist.remove(i);
            }
        }

        mIconCache.flush();
    }

    public int size()
    {
        return data.size();
    }

    public void updatePackage(Context context, String s)
    {
        List list = findActivitiesForPackage(context, s);
        if(list.size() > 0)
        {
            for(int i = data.size() - 1; i >= 0; i--)
            {
                com.sec.android.app.twlauncher.ApplicationInfo applicationinfo1 = (com.sec.android.app.twlauncher.ApplicationInfo)data.get(i);
                ComponentName componentname = applicationinfo1.intent.getComponent();
                if(s.equals(componentname.getPackageName()) && !findActivity(list, componentname))
                {
                    removed.add(applicationinfo1);
                    mIconCache.remove(componentname);
                    data.remove(i);
                }
            }

            int j = list.size();
            int k = 0;
            while(k < j) 
            {
                ResolveInfo resolveinfo = (ResolveInfo)list.get(k);
                com.sec.android.app.twlauncher.ApplicationInfo applicationinfo = findApplicationInfoLocked(resolveinfo.activityInfo.applicationInfo.packageName, resolveinfo.activityInfo.name);
                if(applicationinfo == null)
                {
                    add(new com.sec.android.app.twlauncher.ApplicationInfo(resolveinfo, mIconCache));
                } else
                {
                    mIconCache.remove(applicationinfo.componentName);
                    mIconCache.getTitleAndIcon(applicationinfo, resolveinfo);
                    modified.add(applicationinfo);
                }
                k++;
            }
        }
    }

    public ArrayList added;
    public ArrayList data;
    private IconCache mIconCache;
    public ArrayList modified;
    public ArrayList removed;
}
