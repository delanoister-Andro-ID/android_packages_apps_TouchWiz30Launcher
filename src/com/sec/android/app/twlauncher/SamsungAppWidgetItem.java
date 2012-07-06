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


public class SamsungAppWidgetItem
{

    public SamsungAppWidgetItem(String s)
    {
        mVerticalWidth = 0;
        mVerticalHeight = 0;
        mHorizontalWidth = 0;
        mHorizontalHeight = 0;
        mPackageName = s;
    }

    public int getHeight(int i)
    {
        int j;
        if(i == 2)
            j = mHorizontalHeight;
        else
            j = mVerticalHeight;
        return j;
    }

    public int getWidth(int i)
    {
        int j;
        if(i == 2)
            j = mHorizontalWidth;
        else
            j = mVerticalWidth;
        return j;
    }

    public String mClassName;
    public int mHorizontalHeight;
    public int mHorizontalWidth;
    public String mPackageName;
    public int mVerticalHeight;
    public int mVerticalWidth;
    public String mWidgetTitle;
}
