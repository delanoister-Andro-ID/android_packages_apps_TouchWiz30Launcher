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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LauncherApplication, ApplicationInfo, Launcher, MenuItemView, 
//            FastBitmapDrawable, BadgeCache

public class ApplicationsAdapter extends ArrayAdapter
{

    public ApplicationsAdapter(Context context, ArrayList arraylist)
    {
        super(context, 0, arraylist);
        mInflater = LayoutInflater.from(context);
        mBadgeCache = ((LauncherApplication)context.getApplicationContext()).getBadgeCache();
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        ApplicationInfo applicationinfo = (ApplicationInfo)getItem(i);
        MenuItemView menuitemview;
        ImageView imageview;
        if(view == null)
            if(Launcher.USE_MAINMENU_ICONMODE)
                view = mInflater.inflate(0x7f030003, viewgroup, false);
            else
                view = mInflater.inflate(0x7f030002, viewgroup, false);
        menuitemview = (MenuItemView)view;
        menuitemview.setImageDrawable(new FastBitmapDrawable(applicationinfo.iconBitmap));
        menuitemview.setText(applicationinfo.title);
        if(applicationinfo.intent != null && applicationinfo.intent.getComponent() != null)
        {
            applicationinfo.intent.getComponent().getPackageName();
            applicationinfo.intent.getComponent().getClassName();
        }
        if(Launcher.USE_MAINMENU_CONCENTRATION_EFFECT)
            if(applicationinfo.componentName.getPackageName().startsWith("") || applicationinfo.componentName.getPackageName().startsWith("com."))
                menuitemview.setBackgroundResource(0x7f0200a6);
            else
            if(i % 2 == 0)
                menuitemview.setBackgroundResource(0x7f0200db);
            else
                menuitemview.setBackgroundResource(0x7f0200a8);
        imageview = (ImageView)menuitemview.findViewById(0x7f06000a);
        if(imageview.getScaleType() == android.widget.ImageView.ScaleType.MATRIX && applicationinfo.iconBitmap != null)
        {
            float f = applicationinfo.iconBitmap.getWidth();
            float f1 = applicationinfo.iconBitmap.getHeight();
            float f2 = getContext().getResources().getDimensionPixelSize(0x7f090013);
            float f3 = getContext().getResources().getDimensionPixelSize(0x7f090015);
            Matrix matrix = imageview.getImageMatrix();
            Bitmap bitmap;
            BitmapDrawable bitmapdrawable;
            if(matrix == null)
            {
                Matrix matrix1 = new Matrix();
                matrix1.preScale(f3 / f, f3 / f1);
                matrix1.postTranslate((f2 - f3) / 2.0F, (f2 - f3) / 2.0F);
                imageview.setImageMatrix(matrix1);
            } else
            {
                matrix.preScale(f3 / f, f3 / f1);
                matrix.postTranslate((f2 - f3) / 2.0F, (f2 - f3) / 2.0F);
            }
        }
        if(applicationinfo.intent != null && applicationinfo.intent.getComponent() != null)
        {
            applicationinfo.badgeCount = mBadgeCache.getBadgeCount(applicationinfo.intent.getComponent());
            if(Launcher.USE_MAINMENU_ICONMODE)
            {
                bitmap = mBadgeCache.getBadgeIcon(applicationinfo.intent.getComponent());
                if(bitmap != null)
                {
                    bitmapdrawable = new BitmapDrawable(getContext().getResources(), bitmap);
                    if(bitmapdrawable != null)
                        menuitemview.setBackgroundDrawable(bitmapdrawable);
                }
            }
        }
        view.setTag(applicationinfo);
        return view;
    }

    private final BadgeCache mBadgeCache;
    private final LayoutInflater mInflater;
}
