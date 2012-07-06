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
import android.content.ContentValues;

// Referenced classes of package com.sec.android.app.twlauncher:
//            ItemInfo

class LauncherAppWidgetInfo extends ItemInfo
{

    LauncherAppWidgetInfo(int i)
    {
        hostView = null;
        super.itemType = 4;
        appWidgetId = i;
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        super.onAddToDatabase(contentvalues);
        contentvalues.put("appWidgetId", Integer.valueOf(appWidgetId));
    }

    public String toString()
    {
        return (new StringBuilder()).append("AppWidget(id=").append(Integer.toString(appWidgetId)).append(")").toString();
    }

    void unbind()
    {
        super.unbind();
        hostView = null;
    }

    int appWidgetId;
    AppWidgetHostView hostView;
}
