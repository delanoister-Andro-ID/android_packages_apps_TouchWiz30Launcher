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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import java.lang.ref.WeakReference;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Folder, LiveFolderInfo, LiveFolderAdapter, Launcher, 
//            FolderInfo

public class LiveFolder extends Folder
{
    static class FolderLoadingTask extends AsyncTask
    {

        protected transient Cursor doInBackground(LiveFolderInfo alivefolderinfo[])
        {
            LiveFolder livefolder = (LiveFolder)mFolder.get();
            Cursor cursor;
            if(livefolder != null)
            {
                mInfo = alivefolderinfo[0];
                cursor = LiveFolderAdapter.query(((Folder) (livefolder)).mLauncher, mInfo);
            } else
            {
                cursor = null;
            }
            return cursor;
        }

        protected volatile Object doInBackground(Object aobj[])
        {
            return doInBackground((LiveFolderInfo[])aobj);
        }

        protected void onPostExecute(Cursor cursor)
        {
            if(isCancelled()) goto _L2; else goto _L1
_L1:
            if(cursor != null)
            {
                LiveFolder livefolder = (LiveFolder)mFolder.get();
                if(livefolder != null)
                    livefolder.setContentAdapter(new LiveFolderAdapter(((Folder) (livefolder)).mLauncher, mInfo, cursor));
            }
_L4:
            return;
_L2:
            if(cursor != null)
                cursor.close();
            if(true) goto _L4; else goto _L3
_L3:
        }

        protected volatile void onPostExecute(Object obj)
        {
            onPostExecute((Cursor)obj);
        }

        private final WeakReference mFolder;
        private LiveFolderInfo mInfo;

        FolderLoadingTask(LiveFolder livefolder)
        {
            mFolder = new WeakReference(livefolder);
        }
    }


    public LiveFolder(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    static LiveFolder fromXml(Context context, FolderInfo folderinfo)
    {
        int i;
        if(isDisplayModeList(folderinfo))
            i = 0x7f030011;
        else
            i = 0x7f03000f;
        return (LiveFolder)LayoutInflater.from(context).inflate(i, null);
    }

    private static boolean isDisplayModeList(FolderInfo folderinfo)
    {
        boolean flag;
        if(((LiveFolderInfo)folderinfo).displayMode == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    void bind(FolderInfo folderinfo)
    {
        super.bind(folderinfo);
        if(mLoadingTask != null && mLoadingTask.getStatus() == android.os.AsyncTask.Status.RUNNING)
            mLoadingTask.cancel(true);
        FolderLoadingTask folderloadingtask = new FolderLoadingTask(this);
        LiveFolderInfo alivefolderinfo[] = new LiveFolderInfo[1];
        alivefolderinfo[0] = (LiveFolderInfo)folderinfo;
        mLoadingTask = folderloadingtask.execute(alivefolderinfo);
    }

    void onClose()
    {
        super.onClose();
        if(mLoadingTask != null && mLoadingTask.getStatus() == android.os.AsyncTask.Status.RUNNING)
            mLoadingTask.cancel(true);
        LiveFolderAdapter livefolderadapter = (LiveFolderAdapter)super.mContent.getAdapter();
        if(livefolderadapter != null)
            livefolderadapter.cleanup();
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        LiveFolderAdapter.ViewHolder viewholder = (LiveFolderAdapter.ViewHolder)view.getTag();
        if(!viewholder.useBaseIntent) goto _L2; else goto _L1
_L1:
        Intent intent = ((LiveFolderInfo)super.mInfo).baseIntent;
        if(intent != null)
        {
            Intent intent1 = new Intent(intent);
            intent1.setData(intent.getData().buildUpon().appendPath(Long.toString(viewholder.id)).build());
            super.mLauncher.startActivitySafely(intent1, (new StringBuilder()).append("(position=").append(i).append(", id=").append(l).append(")").toString());
        }
_L4:
        return;
_L2:
        if(viewholder.intent != null)
            super.mLauncher.startActivitySafely(viewholder.intent, (new StringBuilder()).append("(position=").append(i).append(", id=").append(l).append(")").toString());
        if(true) goto _L4; else goto _L3
_L3:
    }

    public boolean onItemLongClick(AdapterView adapterview, View view, int i, long l)
    {
        return false;
    }

    void onOpen()
    {
        super.onOpen();
        requestFocus();
    }

    private AsyncTask mLoadingTask;
}
