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

import android.content.ContentValues;
import android.content.Context;
import android.content.res.*;
import android.util.Log;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

final class LauncherConfig
{

    LauncherConfig()
    {
    }

    static int getColumnNo(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 4;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_144;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.MenuManager);
            contentvalues.clear();
        } while(!"columnno".equals(s));
        l = typedarray.getInt(0, 4);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static int getDefaultScreenCount(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 7;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        l = typedarray.getInt(2, i);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static int getItemNoOfPage(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 16;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_146;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.MenuManager);
            contentvalues.clear();
        } while(!"itemno".equals(s));
        l = typedarray.getInt(1, 16);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static String getProductModel(Context context)
    {
        ContentValues contentvalues;
        String s;
        contentvalues = new ContentValues();
        s = null;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        String s3;
        String s2;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_143;
            } while(j != 2);
            s2 = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Product);
            contentvalues.clear();
        } while(!"product".equals(s2));
        s3 = typedarray.getString(1);
        s = s3;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L2:
        IOException ioexception;
        String s1;
        if(s == null)
            s1 = "";
        else
            s1 = s;
        return s1;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
          goto _L2
    }

    static String getProductModelFamily(Context context)
    {
        ContentValues contentvalues;
        String s;
        contentvalues = new ContentValues();
        s = null;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        String s3;
        String s2;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_143;
            } while(j != 2);
            s2 = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Product);
            contentvalues.clear();
        } while(!"product".equals(s2));
        s3 = typedarray.getString(0);
        s = s3;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L2:
        IOException ioexception;
        String s1;
        if(s == null)
            s1 = "";
        else
            s1 = s;
        return s1;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
          goto _L2
    }

    static int getScreenCount(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 7;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        l = typedarray.getInt(1, i);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    public static boolean getUse16BitWindow(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_144;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        flag1 = typedarray.getBoolean(5, flag);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static boolean getUseIconMenu(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        flag1 = typedarray.getBoolean(9, false);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    public static boolean getUseMainMenuConcentrationEffect(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_144;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        flag1 = typedarray.getBoolean(4, flag);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static boolean getUseMainMenuListMode(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        flag1 = typedarray.getBoolean(6, flag);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static int getWorkspaceCellsX(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 4;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        l = typedarray.getInteger(7, i);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    static int getWorkspaceCellsY(Context context)
    {
        ContentValues contentvalues;
        int i;
        contentvalues = new ContentValues();
        i = 4;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int j;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        j = xmlresourceparser.getDepth();
_L1:
        int l;
        String s;
        TypedArray typedarray;
        do
        {
            int k;
            do
            {
                k = xmlresourceparser.next();
                if(k == 3 && xmlresourceparser.getDepth() <= j || k == 1)
                    break MISSING_BLOCK_LABEL_145;
            } while(k != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        l = typedarray.getInteger(8, i);
        i = l;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return i;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    public static boolean landscapeScreen_isUseFullScreenQuickView(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_144;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"launcher".equals(s));
        flag1 = typedarray.getBoolean(3, flag);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }

    public static boolean pageIndicator_getUseLargeDrawablesOnly(Context context)
    {
        ContentValues contentvalues;
        boolean flag;
        contentvalues = new ContentValues();
        flag = false;
        XmlResourceParser xmlresourceparser;
        android.util.AttributeSet attributeset;
        int i;
        xmlresourceparser = context.getResources().getXml(0x7f050004);
        attributeset = Xml.asAttributeSet(xmlresourceparser);
        XmlUtils.beginDocument(xmlresourceparser, "config");
        i = xmlresourceparser.getDepth();
_L1:
        boolean flag1;
        String s;
        TypedArray typedarray;
        do
        {
            int j;
            do
            {
                j = xmlresourceparser.next();
                if(j == 3 && xmlresourceparser.getDepth() <= i || j == 1)
                    break MISSING_BLOCK_LABEL_144;
            } while(j != 2);
            s = xmlresourceparser.getName();
            typedarray = context.obtainStyledAttributes(attributeset, R.styleable.Launcher);
            contentvalues.clear();
        } while(!"pageindicator".equals(s));
        flag1 = typedarray.getBoolean(0, flag);
        flag = flag1;
          goto _L1
        XmlPullParserException xmlpullparserexception;
        xmlpullparserexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", xmlpullparserexception);
_L3:
        return flag;
        IOException ioexception;
        ioexception;
        Log.w("Launcher.LauncherConfig", "Got exception parsing config.", ioexception);
        if(true) goto _L3; else goto _L2
_L2:
    }
}
