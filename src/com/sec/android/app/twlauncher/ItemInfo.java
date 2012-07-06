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
import android.graphics.Bitmap;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class ItemInfo
{

    ItemInfo()
    {
        id = -1L;
        container = -1L;
        screen = -1;
        cellX = -1;
        cellY = -1;
        spanX = 1;
        spanY = 1;
        isGesture = false;
        packageName = null;
    }

    ItemInfo(ItemInfo iteminfo)
    {
        id = -1L;
        container = -1L;
        screen = -1;
        cellX = -1;
        cellY = -1;
        spanX = 1;
        spanY = 1;
        isGesture = false;
        packageName = null;
        id = iteminfo.id;
        cellX = iteminfo.cellX;
        cellY = iteminfo.cellY;
        spanX = iteminfo.spanX;
        spanY = iteminfo.spanY;
        screen = iteminfo.screen;
        itemType = iteminfo.itemType;
        container = iteminfo.container;
        packageName = iteminfo.packageName;
    }

    static byte[] flattenBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(4 * (bitmap.getWidth() * bitmap.getHeight()));
        byte abyte1[];
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
        bytearrayoutputstream.flush();
        bytearrayoutputstream.close();
        abyte1 = bytearrayoutputstream.toByteArray();
        byte abyte0[] = abyte1;
_L2:
        return abyte0;
        IOException ioexception;
        ioexception;
        Log.w("Favorite", "Could not write icon");
        abyte0 = null;
        if(true) goto _L2; else goto _L1
_L1:
    }

    static void writeBitmap(ContentValues contentvalues, Bitmap bitmap)
    {
        if(bitmap != null)
            contentvalues.put("icon", flattenBitmap(bitmap));
    }

    void onAddToDatabase(ContentValues contentvalues)
    {
        contentvalues.put("itemType", Integer.valueOf(itemType));
        if(!isGesture)
        {
            contentvalues.put("container", Long.valueOf(container));
            contentvalues.put("screen", Integer.valueOf(screen));
            contentvalues.put("cellX", Integer.valueOf(cellX));
            contentvalues.put("cellY", Integer.valueOf(cellY));
            contentvalues.put("spanX", Integer.valueOf(spanX));
            contentvalues.put("spanY", Integer.valueOf(spanY));
        }
    }

    public String toString()
    {
        return (new StringBuilder()).append("Item(").append(id).append("|").append(container).append("|").append(screen).append("|").append(cellX).append("|").append(cellY).append("|").append(itemType).append(")").toString();
    }

    void unbind()
    {
    }

    int cellX;
    int cellY;
    long container;
    long id;
    boolean isGesture;
    int itemType;
    String packageName;
    int screen;
    int spanX;
    int spanY;
}
