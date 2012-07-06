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

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Launcher

public class AddAdapter extends BaseAdapter
{
    public class ListItem
    {

        public final int actionTag;
        public final Drawable image;
        public final CharSequence text;
        final AddAdapter this$0;

        public ListItem(Resources resources, int i, int j, int k)
        {
            this$0 = AddAdapter.this;
            super();
            text = resources.getString(i);
            if(j != -1)
                image = resources.getDrawable(j);
            else
                image = null;
            actionTag = k;
        }
    }


    public AddAdapter(Launcher launcher)
    {
        mInflater = (LayoutInflater)launcher.getSystemService("layout_inflater");
        Resources resources = launcher.getResources();
        mItems.add(new ListItem(resources, 0x7f0a0018, 0x7f020072, 0));
        mItems.add(new ListItem(resources, 0x7f0a0014, 0x7f020079, 1));
        mItems.add(new ListItem(resources, 0x7f0a0017, 0x7f020070, 2));
        mItems.add(new ListItem(resources, 0x7f0a0019, 0x7f02007a, 3));
    }

    public int getCount()
    {
        return mItems.size();
    }

    public Object getItem(int i)
    {
        return mItems.get(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        ListItem listitem = (ListItem)getItem(i);
        if(view == null)
            view = mInflater.inflate(0x7f030000, viewgroup, false);
        TextView textview = (TextView)view;
        textview.setTag(listitem);
        textview.setText(listitem.text);
        textview.setCompoundDrawablesWithIntrinsicBounds(listitem.image, null, null, null);
        return view;
    }

    private final LayoutInflater mInflater;
    private final ArrayList mItems = new ArrayList();
}
