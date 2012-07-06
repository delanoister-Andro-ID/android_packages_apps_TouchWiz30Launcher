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


// Referenced classes of package com.sec.android.app.twlauncher:
//            BgMap

public class BgMapFactory
{

    public BgMapFactory()
    {
    }

    public static BgMap[] getBgMap()
    {
        return sBgMap;
    }

    static final BgMap sBgMap[];

    static 
    {
        BgMap abgmap[] = new BgMap[51];
        abgmap[0] = new BgMap("com.android.alarmclock", null, 0x7f020095);
        abgmap[1] = new BgMap("com.sec.android.app.dlna", null, 0x7f020098);
        abgmap[2] = new BgMap("com.android.browser", null, 0x7f0200a4);
        abgmap[3] = new BgMap("com.android.calendar", null, 0x7f02010f);
        abgmap[4] = new BgMap("com.sec.android.app.camera", null, 0x7f0200ac);
        abgmap[5] = new BgMap("com.android.contacts", "com.sec.android.app.contacts.PhoneBookSplitTopMenuActivity", 0x7f0200b2);
        abgmap[6] = new BgMap("com.android.contacts", "com.android.contacts.PhoneContactsActivity", 0x7f0200dd);
        abgmap[7] = new BgMap("com.sec.android.widgetapp.infoalarm", null, 0x7f0200b4);
        abgmap[8] = new BgMap("com.sec.android.app.digitalframe", null, 0x7f0200b9);
        abgmap[9] = new BgMap("com.samsung.ebook.reader", null, 0x7f0200bd);
        abgmap[10] = new BgMap("com.aldiko.android.samsung", null, 0x7f0200bd);
        abgmap[11] = new BgMap("com.android.email", null, 0x7f0200bf);
        abgmap[12] = new BgMap("com.cooliris.media", null, 0x7f0200c5);
        abgmap[13] = new BgMap("com.google.android.gm", null, 0x7f0200c8);
        abgmap[14] = new BgMap("com.google.android.googlequicksearchbox", null, 0x7f0200c9);
        abgmap[15] = new BgMap("com.google.android.apps.maps", null, 0x7f0200cb);
        abgmap[16] = new BgMap("com.android.vending", null, 0x7f0200e0);
        abgmap[17] = new BgMap("com.sec.android.app.memo", null, 0x7f0200e3);
        abgmap[18] = new BgMap("com.android.mms", null, 0x7f0200e5);
        abgmap[19] = new BgMap("com.sec.android.app.music", null, 0x7f0200ed);
        abgmap[20] = new BgMap("com.android.music", null, 0x7f0200ed);
        abgmap[21] = new BgMap("com.sec.android.app.myfiles", null, 0x7f0200ef);
        abgmap[22] = new BgMap("com.google.android.apps.genie.geniewidget", null, 0x7f020120);
        abgmap[23] = new BgMap("com.sec.android.app.samsungapps", null, 0x7f0200fe);
        abgmap[24] = new BgMap("com.android.settings", null, 0x7f020105);
        abgmap[25] = new BgMap("com.google.android.talk", null, 0x7f0200cd);
        abgmap[26] = new BgMap("com.sec.android.app.controlpanel", null, 0x7f020110);
        abgmap[27] = new BgMap("com.tf.thinkdroid.sstablet", null, 0x7f020113);
        abgmap[28] = new BgMap("com.tf.thinkdroid.samsung", null, 0x7f020113);
        abgmap[29] = new BgMap("com.sec.android.app.videoplayer", null, 0x7f0200eb);
        abgmap[30] = new BgMap("com.android.voicedialer", null, 0x7f02011b);
        abgmap[31] = new BgMap("com.google.android.voicesearch", null, 0x7f02011f);
        abgmap[32] = new BgMap("com.sec.android.app.worldclock", null, 0x7f020122);
        abgmap[33] = new BgMap("com.google.android.youtube", null, 0x7f020126);
        abgmap[34] = new BgMap("com.seven.Z7.app", "com.tecace.app.im.ImMainActivity", 0x7f020102);
        abgmap[35] = new BgMap("com.sec.android.app.imageeditor", null, 0x7f0200d5);
        abgmap[36] = new BgMap("com.sec.android.app.calculator", null, 0x7f0200a8);
        abgmap[37] = new BgMap("com.sec.android.app.dialertab", null, 0x7f0200b7);
        abgmap[38] = new BgMap("com.sec.android.app.task", null, 0x7f02010f);
        abgmap[39] = new BgMap("com.samsung.app.video.editor", null, 0x7f020118);
        abgmap[40] = new BgMap("com.sec.android.app.voicerecorder", null, 0x7f02011d);
        abgmap[41] = new BgMap("com.sec.android.app.fm", null, 0x7f0200c3);
        abgmap[42] = new BgMap("com.sec.android.app.minidiary", null, 0x7f0200e8);
        abgmap[43] = new BgMap("com.sec.android.app.writeandgo", null, 0x7f020124);
        abgmap[44] = new BgMap("com.aldiko.android", null, 0x7f0200bd);
        abgmap[45] = new BgMap("com.sec.android.app.clockpackage", null, 0x7f0200b0);
        abgmap[46] = new BgMap("com.sec.android.app.unifiedinbox", null, 0x7f02010a);
        abgmap[47] = new BgMap("com.seven.Z7", "com.seven.Z7.app.email.EmailFront", 0x7f020100);
        abgmap[48] = new BgMap("com.layar", null, 0x7f0200db);
        abgmap[49] = new BgMap("com.android.stk", null, 0x7f020107);
        abgmap[50] = new BgMap("com.sec.android.app.readershub", null, 0x7f0200fb);
        sBgMap = abgmap;
    }
}
