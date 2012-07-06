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

import android.os.*;
import java.util.LinkedList;

public class DeferredHandler
{
    private class IdleRunnable
        implements Runnable
    {

        public void run()
        {
            mRunnable.run();
        }

        Runnable mRunnable;
    }

    private class Impl extends Handler
        implements android.os.MessageQueue.IdleHandler
    {

        public void handleMessage(Message message)
        {
            Runnable runnable;
            synchronized(mQueue)
            {
                if(mQueue.size() == 0)
                    break MISSING_BLOCK_LABEL_90;
                runnable = (Runnable)mQueue.removeFirst();
            }
            runnable.run();
            LinkedList linkedlist1 = mQueue;
            linkedlist1;
            JVM INSTR monitorenter ;
            scheduleNextLocked();
            break MISSING_BLOCK_LABEL_90;
            exception;
            linkedlist;
            JVM INSTR monitorexit ;
            throw exception;
        }

        public boolean queueIdle()
        {
            handleMessage(null);
            return false;
        }

        final DeferredHandler this$0;

        private Impl()
        {
            this$0 = DeferredHandler.this;
            super();
        }

    }


    public DeferredHandler()
    {
        mQueue = new LinkedList();
        mMessageQueue = Looper.myQueue();
        mHandler = new Impl();
    }

    public void cancel()
    {
        LinkedList linkedlist = mQueue;
        linkedlist;
        JVM INSTR monitorenter ;
        mQueue.clear();
        return;
    }

    public void post(Runnable runnable)
    {
        LinkedList linkedlist = mQueue;
        linkedlist;
        JVM INSTR monitorenter ;
        mQueue.add(runnable);
        if(mQueue.size() == 1)
            scheduleNextLocked();
        return;
    }

    void scheduleNextLocked()
    {
        if(mQueue.size() > 0)
            if((Runnable)mQueue.getFirst() instanceof IdleRunnable)
                mMessageQueue.addIdleHandler(mHandler);
            else
                mHandler.sendEmptyMessage(1);
    }

    private Impl mHandler;
    private MessageQueue mMessageQueue;
    private LinkedList mQueue;

}
