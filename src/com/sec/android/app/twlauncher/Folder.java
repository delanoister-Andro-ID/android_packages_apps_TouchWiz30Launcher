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
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragSource, FolderInfo, Launcher, Workspace, 
//            UserFolderInfo, ItemInfo, LiveFolderInfo, ShortcutInfo, 
//            DragController

public class Folder extends LinearLayout
    implements android.view.View.OnClickListener, android.view.View.OnLongClickListener, android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener, DragSource
{

    public Folder(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
        setAlwaysDrawnWithCacheEnabled(false);
    }

    void bind(FolderInfo folderinfo)
    {
        mInfo = folderinfo;
        mCloseButton.setText(folderinfo.title);
    }

    FolderInfo getInfo()
    {
        return mInfo;
    }

    void notifyDataSetChanged()
    {
        ((BaseAdapter)mContent.getAdapter()).notifyDataSetChanged();
    }

    public void onClick(View view)
    {
        mLauncher.closeFolder(this);
    }

    void onClose()
    {
        Workspace workspace = mLauncher.getWorkspace();
        workspace.getChildAt(workspace.getCurrentScreen()).requestFocus();
    }

    void onCloseInNavy()
    {
        ViewGroup viewgroup;
        int i;
        int j;
        Workspace workspace = mLauncher.getWorkspace();
        viewgroup = (ViewGroup)workspace.getChildAt(workspace.getCurrentScreen());
        i = -1;
        j = 0;
_L9:
        if(j >= viewgroup.getChildCount()) goto _L2; else goto _L1
_L1:
        Object obj = viewgroup.getChildAt(j).getTag();
        if(!(obj instanceof FolderInfo)) goto _L4; else goto _L3
_L3:
        FolderInfo folderinfo = (FolderInfo)obj;
        if(!(folderinfo instanceof UserFolderInfo)) goto _L6; else goto _L5
_L5:
        if(((ItemInfo) ((UserFolderInfo)folderinfo)).id != ((ItemInfo) (mInfo)).id) goto _L4; else goto _L7
_L7:
        i = j;
_L2:
        if(i != -1)
            viewgroup.getChildAt(i).requestFocus();
        return;
_L6:
        if(!(folderinfo instanceof LiveFolderInfo) || ((ItemInfo) ((LiveFolderInfo)folderinfo)).id != ((ItemInfo) (mInfo)).id)
            break; /* Loop/switch isn't completed */
        i = j;
        if(true) goto _L2; else goto _L4
_L4:
        j++;
        if(true) goto _L9; else goto _L8
_L8:
    }

    public void onDropCompleted(View view, boolean flag, Object obj)
    {
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mContent = (AbsListView)findViewById(0x7f060025);
        mContent.setOnItemClickListener(this);
        mContent.setOnItemLongClickListener(this);
        mCloseButton = (Button)findViewById(0x7f060024);
        mCloseButton.setOnClickListener(this);
        mCloseButton.setOnLongClickListener(this);
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        ShortcutInfo shortcutinfo = (ShortcutInfo)adapterview.getItemAtPosition(i);
        int ai[] = new int[2];
        view.getLocationOnScreen(ai);
        shortcutinfo.intent.setSourceBounds(new Rect(ai[0], ai[1], ai[0] + view.getWidth(), ai[1] + view.getHeight()));
        mLauncher.startActivitySafely(shortcutinfo.intent, shortcutinfo);
    }

    public boolean onItemLongClick(AdapterView adapterview, View view, int i, long l)
    {
        boolean flag;
        if(!view.isInTouchMode())
        {
            flag = false;
        } else
        {
            ShortcutInfo shortcutinfo = (ShortcutInfo)adapterview.getItemAtPosition(i);
            mDragger.startDrag(view, this, shortcutinfo, 1);
            mLauncher.closeFolder(this);
            mDragItem = shortcutinfo;
            flag = true;
        }
        return flag;
    }

    public boolean onLongClick(View view)
    {
        mLauncher.closeFolder(this);
        mLauncher.showRenameDialog(mInfo);
        return true;
    }

    void onOpen()
    {
        mContent.requestLayout();
    }

    void setContentAdapter(BaseAdapter baseadapter)
    {
        mContent.setAdapter(baseadapter);
    }

    public void setDragger(DragController dragcontroller)
    {
        mDragger = dragcontroller;
    }

    void setLauncher(Launcher launcher)
    {
        mLauncher = launcher;
    }

    protected Button mCloseButton;
    protected AbsListView mContent;
    protected ShortcutInfo mDragItem;
    protected DragController mDragger;
    protected FolderInfo mInfo;
    protected Launcher mLauncher;
}
