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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.*;
import android.widget.FrameLayout;

public class SamsungAppWidgetView extends FrameLayout
{
    class CheckForLongPress
        implements Runnable
    {

        public void rememberWindowAttachCount()
        {
            mOriginalWindowAttachCount = getWindowAttachCount();
        }

        public void run()
        {
            if(
// JavaClassFileOutputException: get_constant: invalid tag

        private int mOriginalWindowAttachCount;
        final SamsungAppWidgetView this$0;

        CheckForLongPress()
        {
            this$0 = SamsungAppWidgetView.this;
            Object();
        }
    }


    public SamsungAppWidgetView(Context context)
    {
        SamsungAppWidgetView(context, null);
    }

    public SamsungAppWidgetView(Context context, AttributeSet attributeset)
    {
        FrameLayout(context, attributeset);
        init();
    }

    private void init()
    {
        mInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
    }

    private void postCheckForLongClick()
    {
        mHasPerformedLongPress = false;
        if(mPendingCheckForLongPress == null)
            mPendingCheckForLongPress = new CheckForLongPress();
        mPendingCheckForLongPress.rememberWindowAttachCount();
        postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
    }

    public void cancelLongPress()
    {
        cancelLongPress();
        mHasPerformedLongPress = false;
        if(mPendingCheckForLongPress != null)
            removeCallbacks(mPendingCheckForLongPress);
    }

    protected void dispatchDraw(Canvas canvas)
    {
        dispatchDraw(canvas);
    }

    protected void dispatchRestoreInstanceState(SparseArray sparsearray)
    {
    }

    protected void dispatchSaveInstanceState(SparseArray sparsearray)
    {
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        return drawChild(canvas, view, l);
    }

    public View getErrorView()
    {
        return mInflater.inflate(0x7f030008, this, false);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        if(!mHasPerformedLongPress) goto _L2; else goto _L1
_L1:
        boolean flag;
        mHasPerformedLongPress = false;
        flag = true;
_L4:
        return flag;
_L2:
        switch(motionevent.getAction())
        {
        case 2: // '\002'
        default:
            break;

        case 0: // '\0'
            break; /* Loop/switch isn't completed */

        case 1: // '\001'
        case 3: // '\003'
            break;
        }
        break MISSING_BLOCK_LABEL_64;
_L5:
        flag = false;
        if(true) goto _L4; else goto _L3
_L3:
        postCheckForLongClick();
          goto _L5
        mHasPerformedLongPress = false;
        if(mPendingCheckForLongPress != null)
            removeCallbacks(mPendingCheckForLongPress);
          goto _L5
    }

    private boolean mHasPerformedLongPress;
    private LayoutInflater mInflater;
    private CheckForLongPress mPendingCheckForLongPress;





/*
    static boolean access$202(SamsungAppWidgetView samsungappwidgetview, boolean flag)
    {
        samsungappwidgetview.mHasPerformedLongPress = flag;
        return flag;
    }

*/

}
