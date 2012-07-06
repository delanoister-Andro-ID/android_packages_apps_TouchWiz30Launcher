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
import android.widget.FrameLayout;
import android.widget.ProgressBar;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DropTarget, MenuManager, DragSource

public class MenuDrawer extends FrameLayout
    implements DropTarget
{

    public MenuDrawer(Context context)
    {
        this(context, null);
    }

    public MenuDrawer(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public MenuDrawer(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public boolean acceptDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        return true;
    }

    public void onDragEnter(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mMenuManager.onDragEnter(dragsource, i, j, k, l, obj);
    }

    public void onDragExit(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mMenuManager.onDragExit(dragsource, i, j, k, l, obj);
    }

    public void onDragOver(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mMenuManager.onDragOver(dragsource, i, j, k, l, obj);
    }

    public void onDrop(DragSource dragsource, int i, int j, int k, int l, Object obj)
    {
        mMenuManager.onDrop(dragsource, i, j, k, l, obj);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mProgressBar = (ProgressBar)getChildAt(0);
        mProgressBar.setIndeterminate(true);
        mMenuManager = (MenuManager)getChildAt(1);
        setAnimationCacheEnabled(false);
    }

    public void setBackgroundClose()
    {
    }

    public void setBackgroundImage()
    {
        setBackgroundResource(0x7f07000c);
    }

    public void setEnabledProgress(boolean flag)
    {
        if(flag)
            mProgressBar.setVisibility(0);
        else
            mProgressBar.setVisibility(8);
    }

    private MenuManager mMenuManager;
    private ProgressBar mProgressBar;
}
