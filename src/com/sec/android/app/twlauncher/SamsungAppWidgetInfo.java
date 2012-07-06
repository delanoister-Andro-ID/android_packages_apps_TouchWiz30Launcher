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

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import com.sec.android.touchwiz.appwidget.IWidgetObserver;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ItemInfo, SamsungAppWidgetItem, Launcher, SamsungAppWidgetView

class SamsungAppWidgetInfo extends ItemInfo
{

    public SamsungAppWidgetInfo()
    {
        widgetId = -1;
        widgetView = null;
        state = 0;
        super.itemType = 5;
    }

    static SamsungAppWidgetInfo makeSamsungWidget(Context context, SamsungAppWidgetItem samsungappwidgetitem, int i)
    {
        return makeSamsungWidget(context, samsungappwidgetitem, i, null);
    }

    static SamsungAppWidgetInfo makeSamsungWidget(Context context, SamsungAppWidgetItem samsungappwidgetitem, int i, SamsungAppWidgetInfo samsungappwidgetinfo)
    {
        int k;
        android.view.View view;
        Activity activity;
        Window window2;
        String s1;
        android.view.View view1;
        SamsungAppWidgetInfo samsungappwidgetinfo2;
        int j;
        LocalActivityManager localactivitymanager;
        Context context1;
        Intent intent1;
        StringBuilder stringbuilder;
        android.view.View view2;
        Window window3;
        Configuration configuration;
        Context context2;
        if(samsungappwidgetinfo == null)
        {
            SamsungAppWidgetInfo samsungappwidgetinfo1 = new SamsungAppWidgetInfo();
            samsungappwidgetinfo1.widgetId = i;
            samsungappwidgetinfo2 = samsungappwidgetinfo1;
        } else
        {
            if(samsungappwidgetinfo.widgetId == -1)
                samsungappwidgetinfo.widgetId = i;
            samsungappwidgetinfo2 = samsungappwidgetinfo;
        }
        j = 0;
        if(!(context instanceof ActivityGroup) || samsungappwidgetitem.mPackageName == null || samsungappwidgetitem.mClassName == null)
            break MISSING_BLOCK_LABEL_738;
        localactivitymanager = ((ActivityGroup)context).getLocalActivityManager();
        context1 = null;
        context2 = context.createPackageContext(samsungappwidgetitem.mPackageName, 3);
        context1 = context2;
_L11:
        if(context1 != null && (context instanceof Launcher))
        {
            j = ((Launcher)context).getResOrientation();
            configuration = context1.getResources().getConfiguration();
            if(configuration.orientation != j)
            {
                configuration.orientation = j;
                context1.getResources().updateConfiguration(configuration, context1.getResources().getDisplayMetrics());
            }
        }
        intent1 = new Intent();
        intent1.setClassName(samsungappwidgetitem.mPackageName, samsungappwidgetitem.mClassName);
        activity = ((ActivityGroup)context).getLocalActivityManager().getActivity(Integer.toString(i));
        Log.d("SamsungAppWidgetInfo", (new StringBuilder()).append("activity:").append(activity).append("  widgetId:").append(samsungappwidgetinfo2.widgetId).toString());
        if(activity != null) goto _L2; else goto _L1
_L1:
        window3 = localactivitymanager.startActivity(Integer.toString(i), intent1);
        window2 = window3;
_L12:
        stringbuilder = (new StringBuilder()).append("[makeSamsungWidget] check window : ");
        if(window2 != null) goto _L4; else goto _L3
_L3:
        s1 = "null";
_L13:
        Log.d("SamsungAppWidgetInfo", stringbuilder.append(s1).toString());
        if(window2 == null) goto _L6; else goto _L5
_L5:
        view1 = window2.findViewById(0x1020002);
_L14:
        if(view1 == null) goto _L8; else goto _L7
_L7:
        view2 = ((ViewGroup)view1).getChildAt(0);
        view = view2;
_L15:
        if(view == null) goto _L10; else goto _L9
_L9:
        ((ViewGroup)view1).removeView(view);
_L22:
        window2;
_L23:
        samsungappwidgetinfo2.setIntent(samsungappwidgetitem.mPackageName, samsungappwidgetitem.mClassName);
        k = j;
_L26:
        Exception exception1;
        Window window;
        SamsungAppWidgetView samsungappwidgetview = new SamsungAppWidgetView(context);
        android.widget.FrameLayout.LayoutParams layoutparams = new LayoutParams(samsungappwidgetitem.getWidth(k), samsungappwidgetitem.getHeight(k));
        layoutparams.gravity = 17;
        android.content.pm.PackageManager.NameNotFoundException namenotfoundexception;
        Window window1;
        String s;
        if(view != null)
            samsungappwidgetview.addView(view, layoutparams);
        else
            samsungappwidgetview.addView(samsungappwidgetview.getErrorView(), layoutparams);
        samsungappwidgetinfo2.widgetView = samsungappwidgetview;
        return samsungappwidgetinfo2;
        namenotfoundexception;
        namenotfoundexception.printStackTrace();
          goto _L11
_L2:
        window1 = activity.getWindow();
        window2 = window1;
          goto _L12
_L4:
        s = window2.toString();
        s1 = s;
          goto _L13
_L6:
        view1 = null;
          goto _L14
_L8:
        view = null;
          goto _L15
_L10:
        Log.d("SamsungAppWidgetInfo", (new StringBuilder()).append("[makeSamsungWidget] mainView : ").append(view1).toString());
        Log.d("SamsungAppWidgetInfo", (new StringBuilder()).append("[makeSamsungWidget] contentView : ").append(view).toString());
        if(activity == null) goto _L17; else goto _L16
_L16:
        activity.onConfigurationChanged(context.getResources().getConfiguration());
        if(view1 == null) goto _L19; else goto _L18
_L18:
        view = ((ViewGroup)view1).getChildAt(0);
_L24:
        if(view == null) goto _L21; else goto _L20
_L20:
        ((ViewGroup)view1).removeView(view);
          goto _L22
        exception1;
        window = window2;
_L25:
        Log.e("SamsungAppWidgetInfo", (new StringBuilder()).append("failed startActivity(").append(exception1).append(")").toString());
        exception1.printStackTrace();
        window;
          goto _L23
_L19:
        view = null;
          goto _L24
_L21:
        Log.e("SamsungAppWidgetInfo", (new StringBuilder()).append("[makeSamsungWidget] failed get widget view(").append(view1).append(", ").append(view).append(")").toString());
          goto _L22
_L17:
        Log.e("SamsungAppWidgetInfo", "[makeSamsungWidget] activity is still null.");
          goto _L22
        Exception exception;
        exception;
        exception1 = exception;
        window = null;
        view = null;
          goto _L25
        Exception exception2;
        exception2;
        exception1 = exception2;
        window = window2;
        view = null;
          goto _L25
        k = 0;
        view = null;
          goto _L26
    }

    public void fireOnPause(Context context)
    {
        Class class1;
        class1 = null;
        break MISSING_BLOCK_LABEL_2;
        while(true) 
        {
            do
                return;
            while(state != 1 || context == null || !(context instanceof ActivityGroup) || widgetId == -1);
            Activity activity = ((ActivityGroup)context).getLocalActivityManager().getActivity(Integer.toString(widgetId));
            if(activity != null)
            {
                Class aclass[] = activity.getClass().getInterfaces();
                for(int i = 0; i < aclass.length; i++)
                    if(aclass[i].toString().equals(com/sec/android/touchwiz/appwidget/IWidgetObserver.toString()))
                        class1 = aclass[i];

                if(class1 != null)
                    try
                    {
                        Method method = class1.getMethod("fireOnPause", null);
                        if(method != null)
                        {
                            state = 2;
                            method.invoke(activity, null);
                        }
                    }
                    catch(SecurityException securityexception)
                    {
                        securityexception.printStackTrace();
                    }
                    catch(NoSuchMethodException nosuchmethodexception)
                    {
                        nosuchmethodexception.printStackTrace();
                    }
                    catch(IllegalArgumentException illegalargumentexception)
                    {
                        illegalargumentexception.printStackTrace();
                    }
                    catch(IllegalAccessException illegalaccessexception)
                    {
                        illegalaccessexception.printStackTrace();
                    }
                    catch(InvocationTargetException invocationtargetexception)
                    {
                        invocationtargetexception.printStackTrace();
                    }
            }
        }
    }

    public void fireOnResume(Context context)
    {
        Class class1;
        class1 = null;
        break MISSING_BLOCK_LABEL_2;
        while(true) 
        {
            do
                return;
            while(state == 1 || context == null || !(context instanceof ActivityGroup) || widgetId == -1);
            Activity activity = ((ActivityGroup)context).getLocalActivityManager().getActivity(Long.toString(widgetId));
            if(activity != null)
            {
                Class aclass[] = activity.getClass().getInterfaces();
                for(int i = 0; i < aclass.length; i++)
                    if(aclass[i].toString().equals(com/sec/android/touchwiz/appwidget/IWidgetObserver.toString()))
                        class1 = aclass[i];

                if(class1 != null)
                    try
                    {
                        Method method = class1.getMethod("fireOnResume", null);
                        if(method != null)
                        {
                            method.invoke(activity, null);
                            state = 1;
                        }
                    }
                    catch(SecurityException securityexception)
                    {
                        securityexception.printStackTrace();
                    }
                    catch(NoSuchMethodException nosuchmethodexception)
                    {
                        nosuchmethodexception.printStackTrace();
                    }
                    catch(IllegalArgumentException illegalargumentexception)
                    {
                        illegalargumentexception.printStackTrace();
                    }
                    catch(IllegalAccessException illegalaccessexception)
                    {
                        illegalaccessexception.printStackTrace();
                    }
                    catch(InvocationTargetException invocationtargetexception)
                    {
                        invocationtargetexception.printStackTrace();
                    }
            }
        }
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        onAddToDatabase(contentvalues);
        String s;
        if(intent != null)
            s = intent.toUri(0);
        else
            s = null;
        contentvalues.put("intent", s);
        contentvalues.put("appWidgetId", Integer.valueOf(widgetId));
    }

    final void setIntent(String s, String s1)
    {
        intent = new Intent();
        intent.setClassName(s, s1);
    }

    public String toString()
    {
        return (new StringBuilder()).append("SamsungAppWidgetInfo. widgetId:").append(Long.toString(widgetId)).append(" title:").append(super.packageName).toString();
    }

    void unbind()
    {
        unbind();
        widgetView = null;
    }

    Intent intent;
    int state;
    int widgetId;
    SamsungAppWidgetView widgetView;
}
