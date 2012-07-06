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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Folder, DropTarget, ItemInfo, ShortcutsAdapter, 
//            UserFolderInfo, ApplicationInfo, LauncherModel, ShortcutInfo, 
//            DragSource, FolderInfo

public class UserFolder extends Folder
    implements DropTarget
{

    public UserFolder(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    static UserFolder fromXml(Context context)
    {
        return (UserFolder)LayoutInflater.from(context).inflate(0x7f030014, null);
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        ItemInfo iteminfo = (ItemInfo)obj;
        int i1 = iteminfo.itemType;
        boolean flag;
        if((i1 == 0 || i1 == 1) && iteminfo.container != ((ItemInfo) (super.mInfo)).id)
            flag = true;
        else
            flag = false;
        return flag;
    }

    void bind(FolderInfo folderinfo)
    {
        super.bind(folderinfo);
        setContentAdapter(new ShortcutsAdapter(mContext, ((UserFolderInfo)folderinfo).contents));
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
        ItemInfo iteminfo = (ItemInfo)obj;
        ShortcutInfo shortcutinfo;
        if(iteminfo instanceof ApplicationInfo)
            shortcutinfo = ((ApplicationInfo)iteminfo).makeShortcut();
        else
            shortcutinfo = (ShortcutInfo)iteminfo;
        ((ShortcutsAdapter)super.mContent.getAdapter()).add(shortcutinfo);
        LauncherModel.addOrMoveItemInDatabase(super.mLauncher, shortcutinfo, ((ItemInfo) (super.mInfo)).id, 0, 0, 0);
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
        if(flag)
            ((ShortcutsAdapter)super.mContent.getAdapter()).remove(super.mDragItem);
    }

    void onOpen()
    {
        super.onOpen();
        requestFocus();
    }
}
