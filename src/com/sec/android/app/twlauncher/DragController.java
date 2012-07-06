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

import android.view.View;

// Referenced classes of package com.sec.android.app.twlauncher:
//            DragSource

public interface DragController
{
    public static interface DragListener
    {

        public abstract int getDragAnimationXOffset(View view);

        public abstract void onDragEnd();

        public abstract void onDragStart(View view, DragSource dragsource, Object obj, int i);
    }


    public abstract void startAnimation(View view, float f, float f1, float f2, float f3, float f4, float f5, 
            int i);

    public abstract void startDrag(View view, DragSource dragsource, Object obj, int i);
}
