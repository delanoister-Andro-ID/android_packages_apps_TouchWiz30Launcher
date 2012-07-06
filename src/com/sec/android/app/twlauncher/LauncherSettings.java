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

import android.net.Uri;
import android.provider.BaseColumns;

class LauncherSettings
{
    static final class Apps
        implements BaseColumns
    {

        static Uri getContentUri(long l)
        {
            return Uri.parse((new StringBuilder()).append("content://com.sec.android.app.twlauncher.settings/apps/").append(l).toString());
        }

        static final Uri CONTENT_URI = Uri.parse("content://com.sec.android.app.twlauncher.settings/apps");


        Apps()
        {
        }
    }

    static final class Favorites
        implements BaseLauncherColumns
    {

        static Uri getContentUri(long l, boolean flag)
        {
            return Uri.parse((new StringBuilder()).append("content://com.sec.android.app.twlauncher.settings/favorites/").append(l).append("?").append("notify").append("=").append(flag).toString());
        }

        static final Uri CONTENT_URI = Uri.parse("content://com.sec.android.app.twlauncher.settings/favorites?notify=true");
        static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://com.sec.android.app.twlauncher.settings/favorites?notify=false");


        Favorites()
        {
        }
    }

    public static interface BaseLauncherColumns
        extends BaseColumns
    {
    }


    LauncherSettings()
    {
    }
}
