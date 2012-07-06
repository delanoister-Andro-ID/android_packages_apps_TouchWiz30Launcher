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
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            FolderInfo, ItemInfo, ShortcutInfo

class UserFolderInfo extends FolderInfo
{

    UserFolderInfo()
    {
        contents = new ArrayList();
        super.itemType = 2;
    }

    public void add(ShortcutInfo shortcutinfo)
    {
        contents.add(shortcutinfo);
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        super.onAddToDatabase(contentvalues);
        contentvalues.put("title", super.title.toString());
    }

    public void remove(ShortcutInfo shortcutinfo)
    {
        contents.remove(shortcutinfo);
    }

    ArrayList contents;
}
