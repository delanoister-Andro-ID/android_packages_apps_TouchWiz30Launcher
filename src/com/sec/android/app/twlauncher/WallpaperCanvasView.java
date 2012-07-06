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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Workspace

public class WallpaperCanvasView extends View
{

    public WallpaperCanvasView(Context context)
    {
        super(context);
        mCanvasRect = new Rect();
        mStatusBarHeight = context.getResources().getDimensionPixelSize(0x1050004);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(mWorkspace != null)
        {
            SystemClock.uptimeMillis();
            mCanvasRect.left = 0;
            mCanvasRect.top = 0;
            mCanvasRect.right = getWidth();
            mCanvasRect.bottom = getHeight();
            Activity activity = (Activity)getContext();
            boolean flag = false;
            if((0x400 & activity.getWindow().getAttributes().flags) != 0)
                flag = true;
            if(!flag)
            {
                Rect rect = mCanvasRect;
                rect.top = rect.top - mStatusBarHeight;
            }
            mWorkspace.drawWallpaperImage(canvas, mCanvasRect);
            SystemClock.uptimeMillis();
        }
    }

    public void setWorkspace(Workspace workspace)
    {
        mWorkspace = workspace;
    }

    private Rect mCanvasRect;
    private int mStatusBarHeight;
    private Workspace mWorkspace;
}
