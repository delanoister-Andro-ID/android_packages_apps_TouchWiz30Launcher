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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

// Referenced classes of package com.sec.android.app.twlauncher:
//            FolderIcon, Launcher, LiveFolderInfo, Utilities, 
//            FastBitmapDrawable, FolderInfo, DragSource

public class LiveFolderIcon extends FolderIcon
{

    public LiveFolderIcon(Context context)
    {
        super(context);
    }

    public LiveFolderIcon(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    static LiveFolderIcon fromXml(int i, Launcher launcher, ViewGroup viewgroup, LiveFolderInfo livefolderinfo)
    {
        LiveFolderIcon livefoldericon = (LiveFolderIcon)LayoutInflater.from(launcher).inflate(i, viewgroup, false);
        Resources resources = launcher.getResources();
        android.graphics.Bitmap bitmap = livefolderinfo.icon;
        if(bitmap == null)
            bitmap = Utilities.createIconBitmap(resources.getDrawable(0x7f020073), launcher);
        livefoldericon.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(bitmap), null, null);
        livefoldericon.setText(((FolderInfo) (livefolderinfo)).title);
        livefoldericon.setTag(livefolderinfo);
        livefoldericon.setOnClickListener(launcher);
        return livefoldericon;
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        return false;
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }
}
