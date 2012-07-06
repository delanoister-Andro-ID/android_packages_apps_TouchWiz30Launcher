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

import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;

public class DragAnimation
{

    DragAnimation()
    {
        mAnimationState = 3;
    }

    DragAnimation(Interpolator interpolator)
    {
        mAnimationState = 3;
        mInterpolator = interpolator;
    }

    boolean draw(Canvas canvas)
    {
        boolean flag = false;
        if(mAnimationView != null)
        {
            if(mAnimationState == 1)
            {
                mAnimationStartTime = SystemClock.uptimeMillis();
                mAnimationState = 2;
            }
            if(mAnimationState == 2)
            {
                float f = (float)(SystemClock.uptimeMillis() - mAnimationStartTime) / (float)mAnimationDuration;
                float f1;
                float f2;
                if(f >= 1.0F)
                {
                    mAnimationState = 3;
                    f = 1.0F;
                } else
                if(mInterpolator != null)
                    f = mInterpolator.getInterpolation(f);
                f1 = mAnimationFromX + f * (mAnimationToX - mAnimationFromX);
                f2 = mAnimationFromY + f * (mAnimationToY - mAnimationFromY);
                canvas.save();
                canvas.translate(f1, f2);
                mAnimationView.draw(canvas);
                canvas.restore();
                if(mAnimationState != 3)
                {
                    flag = true;
                } else
                {
                    mAnimationView.setVisibility(0);
                    mAnimationView = null;
                    flag = true;
                }
            }
        }
        return flag;
    }

    public void setAnimation(View view, float f, float f1, float f2, float f3, float f4, float f5, 
            int i)
    {
        mAnimationView = view;
        mAnimationFromX = f;
        mAnimationToX = f1;
        mAnimationFromY = f2;
        mAnimationToY = f3;
        mXOffset = f4;
        mYOffset = f5;
        mAnimationState = 1;
        mAnimationDuration = i;
    }

    int mAnimationDuration;
    float mAnimationFromX;
    float mAnimationFromY;
    long mAnimationStartTime;
    int mAnimationState;
    float mAnimationToX;
    float mAnimationToY;
    View mAnimationView;
    private Interpolator mInterpolator;
    float mXOffset;
    float mYOffset;
}
