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

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

// Referenced classes of package com.sec.android.app.twlauncher:
//            FolderInfo, ItemInfo

class LiveFolderInfo extends FolderInfo
{

    LiveFolderInfo()
    {
        super.itemType = 3;
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        super.onAddToDatabase(contentvalues);
        contentvalues.put("title", super.title.toString());
        contentvalues.put("uri", uri.toString());
        if(baseIntent != null)
            contentvalues.put("intent", baseIntent.toUri(0));
        contentvalues.put("iconType", Integer.valueOf(0));
        contentvalues.put("displayMode", Integer.valueOf(displayMode));
        if(iconResource != null)
        {
            contentvalues.put("iconPackage", iconResource.packageName);
            contentvalues.put("iconResource", iconResource.resourceName);
        }
    }

    Intent baseIntent;
    int displayMode;
    Bitmap icon;
    android.content.Intent.ShortcutIconResource iconResource;
    Uri uri;
}
