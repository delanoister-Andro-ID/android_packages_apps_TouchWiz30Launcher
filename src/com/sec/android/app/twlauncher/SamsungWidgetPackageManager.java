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

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.*;
import android.content.pm.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

// Referenced classes of package com.sec.android.app.twlauncher:
//            SamsungAppWidgetItem, Launcher, LauncherModel, SamsungAppWidgetInfo

public class SamsungWidgetPackageManager
{
    private class SamsungWidgetsLoader
        implements Runnable
    {

        boolean isRunning()
        {
            return mRunning;
        }

        public void run()
        {
            Context context;
            Log.d("Launcher.SWidgetPkgMgr", "  ----> running samsung widgets loader");
            Process.setThreadPriority(0);
            context = (Context)mContext.get();
            if(context != null) goto _L2; else goto _L1
_L1:
            Log.d("Launcher.SWidgetPkgMgr", "context is a null");
_L4:
            return;
_L2:
            Iterator iterator;
            List list = loadWidgets(context);
            mSamsungAppWidgets.clear();
            iterator = list.iterator();
_L5:
label0:
            {
                ResolveInfo resolveinfo;
                if(iterator.hasNext())
                {
                    resolveinfo = (ResolveInfo)iterator.next();
                    if(!mStopped)
                        break label0;
                }
                SamsungAppWidgetItem samsungappwidgetitem;
                if(!mStopped)
                {
                    mIsWidgetLoaded = true;
                    Log.d("Launcher.SWidgetPkgMgr", "  ----> samsungwidgets loader completed");
                } else
                {
                    Log.d("Launcher.SWidgetPkgMgr", "  ----> samsungwidgets loader stopped");
                }
                mRunning = false;
            }
            if(true) goto _L4; else goto _L3
_L3:
            samsungappwidgetitem = makeWidgetItem(context, resolveinfo);
            if(samsungappwidgetitem != null && (!resolveinfo.activityInfo.applicationInfo.packageName.equals("com.sec.android.widgetapp.stockclock") || loadYahooWidgetForCsc()))
                mSamsungAppWidgets.add(samsungappwidgetitem);
              goto _L5
        }

        void stop()
        {
            mStopped = true;
        }

        private final WeakReference mContext;
        private volatile boolean mRunning;
        private volatile boolean mStopped;
        final SamsungWidgetPackageManager this$0;

        SamsungWidgetsLoader(Context context)
        {
            this$0 = SamsungWidgetPackageManager.this;
            Object();
            mRunning = true;
            mContext = new WeakReference(context);
        }
    }


    public SamsungWidgetPackageManager()
    {
        mSamsungAppWidgets = new ArrayList(10);
    }

    private List findWidgetForPackage(Context context, String s)
    {
        Intent intent = new Intent();
        intent.setAction("com.samsung.sec.android.SAMSUNG_APP_WIDGET_ACTION");
        intent.addCategory("com.samsung.sec.android.SAMSUNG_APP_WIDGET");
        List list = context.getPackageManager().queryIntentActivities(intent, 0);
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

    public static SamsungWidgetPackageManager getInstance()
    {
        if(sWidgetPackageManager == null)
            sWidgetPackageManager = new SamsungWidgetPackageManager();
        return sWidgetPackageManager;
    }

    private List loadWidgets(Context context)
    {
        Intent intent = new Intent();
        intent.setAction("com.samsung.sec.android.SAMSUNG_APP_WIDGET_ACTION");
        intent.addCategory("com.samsung.sec.android.SAMSUNG_APP_WIDGET");
        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    private boolean loadYahooWidgetForCsc()
    {
        DocumentBuilderFactory documentbuilderfactory = DocumentBuilderFactory.newInstance();
        NodeList nodelist = documentbuilderfactory.newDocumentBuilder().parse(new File("/system/csc/others.xml")).getDocumentElement().getChildNodes();
        if(nodelist == null) goto _L2; else goto _L1
_L1:
        int i;
        int j;
        i = nodelist.getLength();
        j = 0;
_L14:
        if(j >= i) goto _L2; else goto _L3
_L3:
        Node node = nodelist.item(j);
        if(!"AppWidget".equals(node.getNodeName())) goto _L5; else goto _L4
_L4:
        NodeList nodelist1 = node.getChildNodes();
        if(nodelist1 == null) goto _L5; else goto _L6
_L6:
        int k;
        int l;
        k = nodelist1.getLength();
        l = 0;
_L13:
        if(l >= k) goto _L5; else goto _L7
_L7:
        Node node1 = nodelist1.item(l);
        if(!"Yahoo".equals(node1.getNodeName())) goto _L9; else goto _L8
_L8:
        if(!node1.getFirstChild().getNodeValue().equals("On")) goto _L11; else goto _L10
_L10:
        boolean flag = true;
          goto _L12
_L11:
        boolean flag1 = node1.getFirstChild().getNodeValue().equals("Off");
        if(flag1)
            flag = false;
        else
            flag = true;
          goto _L12
_L9:
        l++;
          goto _L13
_L5:
        j++;
          goto _L14
        ParserConfigurationException parserconfigurationexception;
        parserconfigurationexception;
        Log.e("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("ParserConfigurationException:").append(parserconfigurationexception).toString());
_L2:
        flag = true;
        break; /* Loop/switch isn't completed */
        SAXException saxexception;
        saxexception;
        Log.e("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("SAXException: ").append(saxexception).toString());
        continue; /* Loop/switch isn't completed */
        IOException ioexception;
        ioexception;
        Log.e("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("IOException: ").append(ioexception).toString());
        if(true) goto _L2; else goto _L12
_L12:
        return flag;
    }

    private SamsungAppWidgetItem makeWidgetItem(Context context, ResolveInfo resolveinfo)
    {
        ComponentName componentname = new ComponentName(resolveinfo.activityInfo.packageName, resolveinfo.activityInfo.name);
        SamsungAppWidgetItem samsungappwidgetitem = (SamsungAppWidgetItem)mItemCache.get(componentname);
        SamsungAppWidgetItem samsungappwidgetitem2;
        if(samsungappwidgetitem == null)
        {
            SamsungAppWidgetItem samsungappwidgetitem1 = new SamsungAppWidgetItem(resolveinfo.activityInfo.packageName);
            if(updateWidgetItem(context, samsungappwidgetitem1, resolveinfo))
            {
                mItemCache.put(componentname, samsungappwidgetitem1);
                samsungappwidgetitem2 = samsungappwidgetitem1;
            } else
            {
                samsungappwidgetitem2 = null;
            }
        } else
        {
            samsungappwidgetitem2 = samsungappwidgetitem;
        }
        return samsungappwidgetitem2;
    }

    /**
     * @deprecated Method stopAndWaitForWidgetsLoader is deprecated
     */

    private void stopAndWaitForWidgetsLoader()
    {
        this;
        JVM INSTR monitorenter ;
        if(mWidgetLoader == null || !mWidgetLoader.isRunning())
            break MISSING_BLOCK_LABEL_44;
        Log.d("Launcher.SWidgetPkgMgr", "  --> wait for widgets loader ");
        mWidgetLoader.stop();
        Exception exception;
        try
        {
            mWidgetLoaderThread.join(5000L);
        }
        catch(InterruptedException interruptedexception) { }
        this;
        JVM INSTR monitorexit ;
        return;
        exception;
        throw exception;
    }

    private boolean updateWidgetItem(Context context, SamsungAppWidgetItem samsungappwidgetitem, ResolveInfo resolveinfo)
    {
        PackageManager packagemanager = context.getPackageManager();
        Context context1 = context.createPackageContext(resolveinfo.activityInfo.packageName, 3);
        if(context1 != null) goto _L2; else goto _L1
_L1:
        boolean flag = false;
          goto _L3
_L2:
        Configuration configuration;
        int i;
        configuration = context1.getResources().getConfiguration();
        if(context instanceof Launcher)
        {
            int i4 = ((Launcher)context).getResOrientation();
            if(configuration.orientation != i4)
            {
                configuration.orientation = i4;
                context1.getResources().updateConfiguration(configuration, context1.getResources().getDisplayMetrics());
            }
        }
        i = context1.getResources().getIdentifier("plug_in_class", "array", resolveinfo.activityInfo.packageName);
        if(i == 0) goto _L5; else goto _L4
_L4:
        String as[] = context1.getResources().getStringArray(i);
        if(as != null && as.length > 0 && as[0] != null && as[0].length() > 0 && as[0].equals(resolveinfo.activityInfo.name)) goto _L7; else goto _L6
_L7:
        int j;
        int k;
        DisplayMetrics displaymetrics;
        int i2;
        int j2;
        int i3;
        int j3;
        int k3;
        int l3;
        j = context1.getResources().getIdentifier("min_width", "string", resolveinfo.activityInfo.packageName);
        k = context1.getResources().getIdentifier("min_height", "string", resolveinfo.activityInfo.packageName);
        int l = context1.getResources().getIdentifier("plug_in_name", "string", resolveinfo.activityInfo.packageName);
        String s;
        int i1;
        int j1;
        if(l != 0)
            s = context1.getResources().getString(l);
        else
            s = resolveinfo.loadLabel(packagemanager).toString();
        samsungappwidgetitem.mClassName = as[0];
        samsungappwidgetitem.mWidgetTitle = s;
        displaymetrics = context.getResources().getDisplayMetrics();
        i1 = configuration.orientation;
        j1 = 2;
        if(i1 != 2) goto _L9; else goto _L8
_L8:
        if(j == 0) goto _L11; else goto _L10
_L10:
        k3 = (int)((float)Integer.valueOf(context1.getResources().getString(j)).intValue() * displaymetrics.density);
_L35:
        samsungappwidgetitem.mHorizontalWidth = k3;
        if(k == 0)
            break MISSING_BLOCK_LABEL_934;
        l3 = (int)((float)Integer.valueOf(context1.getResources().getString(k)).intValue() * displaymetrics.density);
_L36:
        samsungappwidgetitem.mHorizontalHeight = l3;
        j1 = 1;
_L23:
        configuration.orientation = j1;
        context1.getResources().updateConfiguration(configuration, context1.getResources().getDisplayMetrics());
        i2 = context1.getResources().getIdentifier("min_width", "string", resolveinfo.activityInfo.packageName);
        j2 = context1.getResources().getIdentifier("min_height", "string", resolveinfo.activityInfo.packageName);
        if(j1 != 2) goto _L13; else goto _L12
_L12:
        if(i2 == 0) goto _L15; else goto _L14
_L14:
        i3 = (int)((float)Integer.valueOf(context1.getResources().getString(i2)).intValue() * displaymetrics.density);
_L26:
        samsungappwidgetitem.mHorizontalWidth = i3;
        if(j2 == 0) goto _L17; else goto _L16
_L16:
        j3 = (int)((float)Integer.valueOf(context1.getResources().getString(j2)).intValue() * displaymetrics.density);
_L27:
        samsungappwidgetitem.mHorizontalHeight = j3;
_L32:
        configuration.orientation = i1;
        context1.getResources().updateConfiguration(configuration, context1.getResources().getDisplayMetrics());
        Log.d("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("  -----> widget title=").append(samsungappwidgetitem.mWidgetTitle).append(" vertical width=").append(samsungappwidgetitem.mVerticalWidth).append(" height=").append(samsungappwidgetitem.mVerticalHeight).toString());
        Log.d("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("  -----> widget title=").append(samsungappwidgetitem.mWidgetTitle).append(" horizontal width=").append(samsungappwidgetitem.mHorizontalWidth).append(" height=").append(samsungappwidgetitem.mHorizontalHeight).toString());
          goto _L18
_L9:
        if(j == 0) goto _L20; else goto _L19
_L19:
        int k1 = (int)((float)Integer.valueOf(context1.getResources().getString(j)).intValue() * displaymetrics.density);
_L24:
        samsungappwidgetitem.mVerticalWidth = k1;
        if(k == 0) goto _L22; else goto _L21
_L21:
        int l1 = (int)((float)Integer.valueOf(context1.getResources().getString(k)).intValue() * displaymetrics.density);
_L25:
        samsungappwidgetitem.mVerticalHeight = l1;
          goto _L23
        android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
        namenotfoundexception;
        namenotfoundexception.printStackTrace();
          goto _L18
_L20:
        k1 = 0;
          goto _L24
_L22:
        l1 = 0;
          goto _L25
_L15:
        i3 = 0;
          goto _L26
_L17:
        j3 = 0;
          goto _L27
_L13:
        if(i2 == 0) goto _L29; else goto _L28
_L28:
        int k2 = (int)((float)Integer.valueOf(context1.getResources().getString(i2)).intValue() * displaymetrics.density);
_L33:
        samsungappwidgetitem.mVerticalWidth = k2;
        if(j2 == 0) goto _L31; else goto _L30
_L30:
        int l2 = (int)((float)Integer.valueOf(context1.getResources().getString(j2)).intValue() * displaymetrics.density);
_L34:
        samsungappwidgetitem.mVerticalHeight = l2;
          goto _L32
        android.content.res.Resources.NotFoundException notfoundexception;
        notfoundexception;
        Log.e("Launcher.SWidgetPkgMgr", "Resource not found exception :");
        notfoundexception.printStackTrace();
        flag = false;
          goto _L3
_L29:
        k2 = 0;
          goto _L33
_L31:
        l2 = 0;
          goto _L34
_L3:
        return flag;
_L6:
        flag = false;
        continue; /* Loop/switch isn't completed */
_L5:
        flag = false;
        continue; /* Loop/switch isn't completed */
_L18:
        flag = true;
        if(true) goto _L3; else goto _L11
_L11:
        k3 = 0;
          goto _L35
        l3 = 0;
          goto _L36
    }

    /**
     * @deprecated Method addPackage is deprecated
     */

    void addPackage(Context context, String s)
    {
        this;
        JVM INSTR monitorenter ;
        List list;
        Log.d("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("addPackage=").append(s).toString());
        if(s != null && s.length() > 0)
        {
            list = findWidgetForPackage(context, s);
            if(list.size() > 0)
            {
                if(mWidgetLoader == null || !mWidgetLoader.isRunning())
                    break MISSING_BLOCK_LABEL_82;
                scanPackage(context);
            }
        }
_L1:
        this;
        JVM INSTR monitorexit ;
        return;
        Iterator iterator = list.iterator();
        while(iterator.hasNext()) 
        {
            SamsungAppWidgetItem samsungappwidgetitem = makeWidgetItem(context, (ResolveInfo)iterator.next());
            if(samsungappwidgetitem != null)
                mSamsungAppWidgets.add(samsungappwidgetitem);
        }
          goto _L1
        Exception exception;
        exception;
        throw exception;
    }

    public SamsungAppWidgetInfo createWidget(Context context, SamsungAppWidgetItem samsungappwidgetitem)
    {
        return SamsungAppWidgetInfo.makeSamsungWidget(context, samsungappwidgetitem, LauncherModel.allocWidgetId(context));
    }

    public SamsungAppWidgetInfo createWidget(Context context, SamsungAppWidgetItem samsungappwidgetitem, SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        int i;
        if(samsungappwidgetinfo.widgetId == -1)
            i = LauncherModel.allocWidgetId(context);
        else
            i = samsungappwidgetinfo.widgetId;
        return SamsungAppWidgetInfo.makeSamsungWidget(context, samsungappwidgetitem, i, samsungappwidgetinfo);
    }

    public void destroyWidget(ActivityGroup activitygroup, SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        long l = samsungappwidgetinfo.widgetId;
        activitygroup.getLocalActivityManager().destroyActivity(Long.toString(l), true);
    }

    public SamsungAppWidgetItem findWidget(String s, String s1)
    {
        int i;
        int j;
        i = mSamsungAppWidgets.size();
        j = 0;
_L3:
        SamsungAppWidgetItem samsungappwidgetitem1;
        if(j >= i)
            break MISSING_BLOCK_LABEL_68;
        samsungappwidgetitem1 = (SamsungAppWidgetItem)mSamsungAppWidgets.get(j);
        if(!samsungappwidgetitem1.mPackageName.equals(s) || !samsungappwidgetitem1.mClassName.equals(s1)) goto _L2; else goto _L1
_L1:
        SamsungAppWidgetItem samsungappwidgetitem = samsungappwidgetitem1;
_L4:
        return samsungappwidgetitem;
_L2:
        j++;
          goto _L3
        samsungappwidgetitem = null;
          goto _L4
    }

    public ArrayList getWidgetItems()
    {
        return mSamsungAppWidgets;
    }

    public void pauseWidget(ActivityGroup activitygroup, SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        samsungappwidgetinfo.fireOnPause(activitygroup);
    }

    /**
     * @deprecated Method removePackage is deprecated
     */

    void removePackage(Context context, String s)
    {
        this;
        JVM INSTR monitorenter ;
        int i;
        ArrayList arraylist;
        Log.d("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("removePackage=").append(s).toString());
        if(s == null || s.length() <= 0)
            break MISSING_BLOCK_LABEL_141;
        i = mSamsungAppWidgets.size();
        arraylist = new ArrayList();
        Exception exception;
        HashMap hashmap;
        Iterator iterator;
        SamsungAppWidgetItem samsungappwidgetitem;
        for(int j = 0; j < i; j++)
        {
            SamsungAppWidgetItem samsungappwidgetitem1 = (SamsungAppWidgetItem)mSamsungAppWidgets.get(j);
            if(s.equals(samsungappwidgetitem1.mPackageName))
                arraylist.add(samsungappwidgetitem1);
            break MISSING_BLOCK_LABEL_251;
        }

        if(arraylist.size() <= 0 || mWidgetLoader == null || !mWidgetLoader.isRunning())
            break MISSING_BLOCK_LABEL_144;
        mItemCache.clear();
        scanPackage(context);
_L1:
        this;
        JVM INSTR monitorexit ;
        return;
        hashmap = mItemCache;
        iterator = arraylist.iterator();
        while(iterator.hasNext()) 
        {
            samsungappwidgetitem = (SamsungAppWidgetItem)iterator.next();
            mSamsungAppWidgets.remove(samsungappwidgetitem);
            hashmap.remove(new ComponentName(samsungappwidgetitem.mPackageName, samsungappwidgetitem.mClassName));
            Log.e("aaa", (new StringBuilder()).append("s remove=").append(samsungappwidgetitem.mPackageName).toString());
        }
          goto _L1
        exception;
        throw exception;
    }

    public void resumeWidget(ActivityGroup activitygroup, SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        samsungappwidgetinfo.fireOnResume(activitygroup);
    }

    /**
     * @deprecated Method scanPackage is deprecated
     */

    void scanPackage(Context context)
    {
        this;
        JVM INSTR monitorenter ;
        mIsWidgetLoaded = false;
        stopAndWaitForWidgetsLoader();
        mWidgetLoader = new SamsungWidgetsLoader(context);
        mWidgetLoaderThread = new Thread(mWidgetLoader, "SamsungWidgets Loader");
        mWidgetLoaderThread.start();
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    /**
     * @deprecated Method start is deprecated
     */

    void start(Context context, boolean flag)
    {
        this;
        JVM INSTR monitorenter ;
        if(flag) goto _L2; else goto _L1
_L1:
        boolean flag1 = mIsWidgetLoaded;
        if(!flag1) goto _L2; else goto _L3
_L3:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        stopAndWaitForWidgetsLoader();
        if(flag)
            mItemCache.clear();
        scanPackage(context);
        if(true) goto _L3; else goto _L4
_L4:
        Exception exception;
        exception;
        throw exception;
    }

    public void unbind()
    {
        stopAndWaitForWidgetsLoader();
    }

    /**
     * @deprecated Method updatePackage is deprecated
     */

    void updatePackage(Context context, String s)
    {
        this;
        JVM INSTR monitorenter ;
        Log.d("Launcher.SWidgetPkgMgr", (new StringBuilder()).append("updatePackage=").append(s).toString());
        if(mWidgetLoader == null || !mWidgetLoader.isRunning()) goto _L2; else goto _L1
_L1:
        scanPackage(context);
_L4:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        if(s == null) goto _L4; else goto _L3
_L3:
        if(s.length() <= 0) goto _L4; else goto _L5
_L5:
        int i;
        SamsungAppWidgetItem samsungappwidgetitem;
        int j;
        i = mSamsungAppWidgets.size();
        samsungappwidgetitem = null;
        j = 0;
_L8:
        if(j >= i)
            continue; /* Loop/switch isn't completed */
        SamsungAppWidgetItem samsungappwidgetitem1 = (SamsungAppWidgetItem)mSamsungAppWidgets.get(j);
        if(!s.equals(samsungappwidgetitem1.mPackageName))
            break MISSING_BLOCK_LABEL_185;
        samsungappwidgetitem = samsungappwidgetitem1;
        if(samsungappwidgetitem == null) goto _L4; else goto _L6
_L6:
        List list = findWidgetForPackage(context, s);
        if(list.size() <= 0) goto _L4; else goto _L7
_L7:
        Iterator iterator = list.iterator();
        boolean flag;
        do
        {
            if(!iterator.hasNext())
                break;
            flag = updateWidgetItem(context, samsungappwidgetitem, (ResolveInfo)iterator.next());
        } while(!flag);
          goto _L4
        j++;
          goto _L8
        Exception exception;
        exception;
        throw exception;
          goto _L4
    }

    private static SamsungWidgetPackageManager sWidgetPackageManager;
    private boolean mIsWidgetLoaded;
    private final HashMap mItemCache = new HashMap(10);
    private ArrayList mSamsungAppWidgets;
    private SamsungWidgetsLoader mWidgetLoader;
    private Thread mWidgetLoaderThread;






/*
    static boolean access$402(SamsungWidgetPackageManager samsungwidgetpackagemanager, boolean flag)
    {
        samsungwidgetpackagemanager.mIsWidgetLoaded = flag;
        return flag;
    }

*/
}
