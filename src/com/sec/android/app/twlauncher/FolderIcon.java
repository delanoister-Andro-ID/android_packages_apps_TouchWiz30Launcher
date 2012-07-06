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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

// Referenced classes of package com.sec.android.app.twlauncher:
//            BubbleTextView, DropTarget, Launcher, FolderInfo, 
//            ItemInfo, ApplicationInfo, UserFolderInfo, LauncherModel, 
//            ShortcutInfo, DragSource

public class FolderIcon extends BubbleTextView
    implements DropTarget
{

    public FolderIcon(Context context)
    {
        super(context);
    }

    public FolderIcon(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    static FolderIcon fromXml(int i, Launcher launcher, ViewGroup viewgroup, UserFolderInfo userfolderinfo)
    {
        FolderIcon foldericon = (FolderIcon)LayoutInflater.from(launcher).inflate(i, viewgroup, false);
        Resources resources = launcher.getResources();
        Drawable drawable = resources.getDrawable(0x7f020073);
        foldericon.mCloseIcon = drawable;
        foldericon.mOpenIcon = resources.getDrawable(0x7f020075);
        foldericon.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        foldericon.setText(((FolderInfo) (userfolderinfo)).title);
        foldericon.setTag(userfolderinfo);
        foldericon.setOnClickListener(launcher);
        foldericon.mInfo = userfolderinfo;
        foldericon.mLauncher = launcher;
        return foldericon;
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        ItemInfo iteminfo = (ItemInfo)obj;
        int i1 = iteminfo.itemType;
        boolean flag;
        if((i1 == 0 || i1 == 1) && iteminfo.container != ((ItemInfo) (mInfo)).id)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        setCompoundDrawablesWithIntrinsicBounds(null, mOpenIcon, null, null);
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        ItemInfo iteminfo = (ItemInfo)obj;
        ShortcutInfo shortcutinfo;
        if(iteminfo instanceof ApplicationInfo)
        {
            shortcutinfo = ((ApplicationInfo)iteminfo).makeShortcut();
        } else
        {
            shortcutinfo = (ShortcutInfo)obj;
            mLauncher.removeShortcut(shortcutinfo);
        }
        mInfo.add(shortcutinfo);
        LauncherModel.addOrMoveItemInDatabase(mLauncher, shortcutinfo, ((ItemInfo) (mInfo)).id, 0, 0, 0);
    }

    private Drawable mCloseIcon;
    private UserFolderInfo mInfo;
    private Launcher mLauncher;
    private Drawable mOpenIcon;
}
