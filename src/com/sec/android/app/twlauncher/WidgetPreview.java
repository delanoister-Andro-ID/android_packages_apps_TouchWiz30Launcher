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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class WidgetPreview extends FrameLayout
{

    public WidgetPreview(Context context)
    {
        this(context, null);
    }

    public WidgetPreview(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public WidgetPreview(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        init();
    }

    private void init()
    {
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        return false;
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        if(getChildCount() > 0)
        {
            View view = getChildAt(0);
            int i1 = view.getMeasuredWidth();
            int j1 = view.getMeasuredHeight();
            int k1 = (getMeasuredWidth() - i1) / 2;
            int l1 = (getMeasuredHeight() - j1) / 2;
            view.layout(k1, l1, k1 + i1, l1 + j1);
        }
    }
}
