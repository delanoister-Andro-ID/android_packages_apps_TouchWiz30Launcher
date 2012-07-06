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

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.*;

public class LauncherAppWidgetHostView extends AppWidgetHostView
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
            if(hasWindowFocus != null && hasWindowFocus() && mOriginalWindowAttachCount == getWindowAttachCount() && !mHasPerformedLongPress && performLongClick())
                mHasPerformedLongPress = true;
        }

        private int mOriginalWindowAttachCount;
        final LauncherAppWidgetHostView this$0;

        CheckForLongPress()
        {
            this$0 = LauncherAppWidgetHostView.this;
            super();
        }
    }


    public LauncherAppWidgetHostView(Context context)
    {
        super(context);
        mInflater = (LayoutInflater)context.getSystemService("layout_inflater");
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
        super.cancelLongPress();
        mHasPerformedLongPress = false;
        if(mPendingCheckForLongPress != null)
            removeCallbacks(mPendingCheckForLongPress);
    }

    protected View getErrorView()
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
    static boolean access$202(LauncherAppWidgetHostView launcherappwidgethostview, boolean flag)
    {
        launcherappwidgethostview.mHasPerformedLongPress = flag;
        return flag;
    }

*/

}
