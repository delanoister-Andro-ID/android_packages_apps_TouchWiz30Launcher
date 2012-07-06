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

import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import java.lang.ref.SoftReference;
import java.net.URISyntaxException;
import java.util.*;

// Referenced classes of package com.sec.android.app.twlauncher:
//            LiveFolderInfo, Launcher, FastBitmapDrawable, Utilities

class LiveFolderAdapter extends CursorAdapter
{
    static class ViewHolder
    {

        TextView description;
        int descriptionIndex;
        ImageView icon;
        int iconBitmapIndex;
        int iconPackageIndex;
        int iconResourceIndex;
        long id;
        int idIndex;
        Intent intent;
        int intentIndex;
        TextView name;
        int nameIndex;
        boolean useBaseIntent;

        ViewHolder()
        {
            descriptionIndex = -1;
            intentIndex = -1;
            iconBitmapIndex = -1;
            iconResourceIndex = -1;
            iconPackageIndex = -1;
        }
    }


    LiveFolderAdapter(Launcher launcher, LiveFolderInfo livefolderinfo, Cursor cursor)
    {
        super(launcher, cursor, true);
        boolean flag;
        if(livefolderinfo.displayMode == 2)
            flag = true;
        else
            flag = false;
        mIsList = flag;
        mInflater = LayoutInflater.from(launcher);
        mLauncher = launcher;
        mLauncher.startManagingCursor(getCursor());
    }

    private Drawable loadIcon(Context context, Cursor cursor, ViewHolder viewholder)
    {
        Object obj;
        byte abyte0[];
        obj = null;
        abyte0 = null;
        if(viewholder.iconBitmapIndex != -1)
            abyte0 = cursor.getBlob(viewholder.iconBitmapIndex);
        if(abyte0 == null) goto _L2; else goto _L1
_L1:
        SoftReference softreference = (SoftReference)mCustomIcons.get(Long.valueOf(viewholder.id));
        if(softreference != null)
            obj = (Drawable)softreference.get();
        if(obj == null)
        {
            obj = new FastBitmapDrawable(Utilities.createBitmapThumbnail(BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length), mContext));
            mCustomIcons.put(Long.valueOf(viewholder.id), new SoftReference(obj));
        }
_L4:
        return ((Drawable) (obj));
_L2:
        if(viewholder.iconResourceIndex != -1 && viewholder.iconPackageIndex != -1)
        {
            String s = cursor.getString(viewholder.iconResourceIndex);
            obj = (Drawable)mIcons.get(s);
            if(obj == null)
                try
                {
                    Resources resources = context.getPackageManager().getResourcesForApplication(cursor.getString(viewholder.iconPackageIndex));
                    obj = Utilities.createIconThumbnail(resources.getDrawable(resources.getIdentifier(s, null, null)), mContext);
                    mIcons.put(s, obj);
                }
                catch(Exception exception) { }
        }
        if(true) goto _L4; else goto _L3
_L3:
    }

    static Cursor query(Context context, LiveFolderInfo livefolderinfo)
    {
        return context.getContentResolver().query(livefolderinfo.uri, null, null, null, "name ASC");
    }

    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder viewholder;
        viewholder = (ViewHolder)view.getTag();
        viewholder.id = cursor.getLong(viewholder.idIndex);
        Drawable drawable = loadIcon(context, cursor, viewholder);
        viewholder.name.setText(cursor.getString(viewholder.nameIndex));
        if(!mIsList)
        {
            viewholder.name.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        } else
        {
            boolean flag;
            ImageView imageview;
            int i;
            if(drawable != null)
                flag = true;
            else
                flag = false;
            imageview = viewholder.icon;
            if(flag)
                i = 0;
            else
                i = 8;
            imageview.setVisibility(i);
            if(flag)
                viewholder.icon.setImageDrawable(drawable);
            if(viewholder.descriptionIndex != -1)
            {
                String s = cursor.getString(viewholder.descriptionIndex);
                if(s != null)
                {
                    viewholder.description.setText(s);
                    viewholder.description.setVisibility(0);
                } else
                {
                    viewholder.description.setVisibility(8);
                }
            } else
            {
                viewholder.description.setVisibility(8);
            }
        }
        if(viewholder.intentIndex == -1) goto _L2; else goto _L1
_L1:
        viewholder.intent = Intent.parseUri(cursor.getString(viewholder.intentIndex), 0);
_L4:
        return;
_L2:
        viewholder.useBaseIntent = true;
        continue; /* Loop/switch isn't completed */
        URISyntaxException urisyntaxexception;
        urisyntaxexception;
        if(true) goto _L4; else goto _L3
_L3:
    }

    void cleanup()
    {
        Cursor cursor;
        for(Iterator iterator = mIcons.values().iterator(); iterator.hasNext(); ((Drawable)iterator.next()).setCallback(null));
        mIcons.clear();
        Iterator iterator1 = mCustomIcons.values().iterator();
        do
        {
            if(!iterator1.hasNext())
                break;
            Drawable drawable = (Drawable)((SoftReference)iterator1.next()).get();
            if(drawable != null)
                drawable.setCallback(null);
        } while(true);
        mCustomIcons.clear();
        cursor = getCursor();
        if(cursor == null)
            break MISSING_BLOCK_LABEL_128;
        cursor.close();
        mLauncher.stopManagingCursor(cursor);
        return;
        Exception exception;
        exception;
        mLauncher.stopManagingCursor(cursor);
        throw exception;
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        ViewHolder viewholder = new ViewHolder();
        View view;
        if(!mIsList)
        {
            view = mInflater.inflate(0x7f030002, viewgroup, false);
        } else
        {
            view = mInflater.inflate(0x7f030006, viewgroup, false);
            viewholder.description = (TextView)view.findViewById(0x7f06000c);
            viewholder.icon = (ImageView)view.findViewById(0x7f06000a);
        }
        viewholder.name = (TextView)view.findViewById(0x7f06000b);
        viewholder.idIndex = cursor.getColumnIndexOrThrow("_id");
        viewholder.nameIndex = cursor.getColumnIndexOrThrow("name");
        viewholder.descriptionIndex = cursor.getColumnIndex("description");
        viewholder.intentIndex = cursor.getColumnIndex("intent");
        viewholder.iconBitmapIndex = cursor.getColumnIndex("icon_bitmap");
        viewholder.iconResourceIndex = cursor.getColumnIndex("icon_resource");
        viewholder.iconPackageIndex = cursor.getColumnIndex("icon_package");
        view.setTag(viewholder);
        return view;
    }

    private final HashMap mCustomIcons = new HashMap();
    private final HashMap mIcons = new HashMap();
    private LayoutInflater mInflater;
    private boolean mIsList;
    private final Launcher mLauncher;
}
