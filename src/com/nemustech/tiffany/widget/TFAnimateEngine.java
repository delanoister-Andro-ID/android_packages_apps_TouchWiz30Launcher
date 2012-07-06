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

package com.nemustech.tiffany.widget;

import android.graphics.Rect;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class TFAnimateEngine
{

    public TFAnimateEngine()
    {
        mInterpolator = DEFAULT_INTERPOLATOR;
    }

    public Rect[] getRect(Rect arect[], Rect arect1[], Rect arect2[], float f)
    {
        Rect arect3[];
        if(arect == null || arect1 == null)
            arect3 = null;
        else
        if(arect.length != arect1.length)
        {
            arect3 = null;
        } else
        {
            float f1 = Math.max(0.0F, Math.min(1.0F, f));
            int i = arect.length;
            Rect arect4[];
            float f2;
            if(arect2 == null || arect2.length != i)
                arect4 = new Rect[i];
            else
                arect4 = arect2;
            f2 = mInterpolator.getInterpolation(f1);
            for(int j = 0; j < i; j++)
            {
                if(arect4[j] == null)
                    arect4[j] = new Rect();
                arect4[j].left = arect[j].left + (int)(f2 * (float)(arect1[j].left - arect[j].left));
                arect4[j].right = arect[j].right + (int)(f2 * (float)(arect1[j].right - arect[j].right));
                arect4[j].top = arect[j].top + (int)(f2 * (float)(arect1[j].top - arect[j].top));
                arect4[j].bottom = arect[j].bottom + (int)(f2 * (float)(arect1[j].bottom - arect[j].bottom));
            }

            arect3 = arect4;
        }
        return arect3;
    }

    public void setInterpolator(Interpolator interpolator)
    {
        if(interpolator == null)
            interpolator = DEFAULT_INTERPOLATOR;
        mInterpolator = interpolator;
    }

    private static final Interpolator DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
    private Interpolator mInterpolator;

}
