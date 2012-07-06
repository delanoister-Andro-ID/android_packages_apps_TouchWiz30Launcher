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
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

// Referenced classes of package com.sec.android.app.twlauncher:
//            FlatRateAlertActivity

public class BootupListener extends BroadcastReceiver
{

    public BootupListener()
    {
    }

    private String getIccid(TelephonyManager telephonymanager)
    {
        String s = null;
        if(telephonymanager != null)
            s = telephonymanager.getSimSerialNumber();
        return s;
    }

    private String getIccidPreference(SharedPreferences sharedpreferences)
    {
        String s;
        if(sharedpreferences != null)
            s = sharedpreferences.getString("sim_iccid", null);
        else
            s = null;
        return s;
    }

    public void onReceive(Context context, Intent intent)
    {
        Log.v("bootupListener", (new StringBuilder()).append("IMSI ").append(SystemProperties.get("ril.IMSI")).append(" intent ").append(intent.getAction()).toString());
        if(("AMN".equals(salesCode) || "ORA".equals(salesCode) || "FTM".equals(salesCode) || "XEC".equals(salesCode)) && !SystemProperties.get("ril.IMSI").equals("999999999999999") && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
        {
            TelephonyManager telephonymanager = (TelephonyManager)context.getSystemService("phone");
            if(telephonymanager.getSimState() != 1)
            {
                SharedPreferences sharedpreferences = context.getSharedPreferences("pref_first_Time_boot", 0);
                android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
                String s = getIccidPreference(sharedpreferences);
                String s1;
                boolean flag;
                if(s == null)
                    Log.d("bootupListener", "========= iccidPref is null =============");
                else
                    Log.d("bootupListener", (new StringBuilder()).append("========= iccidPref ============= ").append(s).toString());
                s1 = getIccid(telephonymanager);
                if(s1 == null)
                    Log.d("bootupListener", "========= iccid is null =============");
                else
                    Log.d("bootupListener", (new StringBuilder()).append("========= iccid ============= ").append(s1).toString());
                if(s == null || s1 != null && !s.equals(s1))
                {
                    Log.d("bootupListener", "========= New SIM =============");
                    flag = true;
                } else
                {
                    flag = false;
                }
                if(flag && s1 != null)
                {
                    editor.putString("sim_iccid", s1);
                    Log.d("bootupListener", "========= New SIM ICCID added to preferences =============");
                    editor.commit();
                    Intent intent1 = new Intent(context, com/sec/android/app/twlauncher/FlatRateAlertActivity);
                    intent1.setFlags(0x10000000);
                    context.startActivity(intent1);
                }
            }
        }
    }

    private final String FACTORYMODE_KEY = "999999999999999";
    private final String IMSI_key = "ril.IMSI";
    private final String salesCode = SystemProperties.get("ro.csc.sales_code");
}
