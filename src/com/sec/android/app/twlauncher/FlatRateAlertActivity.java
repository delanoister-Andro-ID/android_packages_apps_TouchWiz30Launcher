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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.android.internal.app.AlertActivity;

public class FlatRateAlertActivity extends AlertActivity
    implements android.widget.RadioGroup.OnCheckedChangeListener
{

    public FlatRateAlertActivity()
    {
        fRateVal = false;
        radioOption2 = null;
    }

    private View createView()
    {
        View view = getLayoutInflater().inflate(0x7f030009, null);
        if("XEC".equals(SystemProperties.get("ro.csc.sales_code")))
        {
            mRadioGroup = (RadioGroup)view.findViewById(0x7f06000f);
            radioOption2 = (RadioButton)view.findViewById(0x7f060011);
            mRadioGroup.setVisibility(0);
            ans = radioOption2.getId();
            mRadioGroup.setOnCheckedChangeListener(this);
        } else
        {
            mCheckBox = (CheckBox)view.findViewById(0x7f06000e);
            mCheckBox.setVisibility(0);
            mCheckBox.setChecked(true);
        }
        ((Button)view.findViewById(0x7f060012)).setOnClickListener(new android.view.View.OnClickListener() {

            public void onClick(View view1)
            {
                if(FlatRateAlertActivity.choice != FlatRateAlertActivity.ans)
                    Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog choice == ans");
                else
                    Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog choice != ans");
                if("XEC".equals(SystemProperties.get("ro.csc.sales_code")))
                {
                    ConnectivityManager connectivitymanager1 = (ConnectivityManager)mContext.getSystemService("connectivity");
                    if(radioOption2.getId() == FlatRateAlertActivity.mRadioGroup.getCheckedRadioButtonId())
                    {
                        Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog onClick item 1");
                        fRateVal = false;
                        Intent intent3 = new Intent("android.intent.action.NETWORK_MODE_INITIATE_CHANGE");
                        intent3.putExtra("state", false);
                        mContext.sendBroadcast(intent3);
                        connectivitymanager1.setMobileDataEnabled(false);
                    } else
                    {
                        Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog onClick item 0");
                        fRateVal = true;
                        Intent intent2 = new Intent("android.intent.action.NETWORK_MODE_INITIATE_CHANGE");
                        intent2.putExtra("state", true);
                        mContext.sendBroadcast(intent2);
                        connectivitymanager1.setMobileDataEnabled(true);
                    }
                } else
                {
                    ConnectivityManager connectivitymanager = (ConnectivityManager)mContext.getSystemService("connectivity");
                    if(!FlatRateAlertActivity.mCheckBox.isChecked())
                    {
                        Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog onClick item 1");
                        fRateVal = false;
                        Intent intent1 = new Intent("android.intent.action.NETWORK_MODE_INITIATE_CHANGE");
                        intent1.putExtra("state", false);
                        mContext.sendBroadcast(intent1);
                        connectivitymanager.setMobileDataEnabled(false);
                    } else
                    {
                        Log.i("FLatRateAlertActivity", "showFlatRateOptionDialog onClick item 0");
                        fRateVal = true;
                        Intent intent = new Intent("android.intent.action.NETWORK_MODE_INITIATE_CHANGE");
                        intent.putExtra("state", true);
                        mContext.sendBroadcast(intent);
                        connectivitymanager.setMobileDataEnabled(true);
                    }
                }
                finish();
            }

            final FlatRateAlertActivity this$0;

            
            {
                this$0 = FlatRateAlertActivity.this;
                super();
            }
        }
);
        return view;
    }

    public void onBackPressed()
    {
    }

    public void onCheckedChanged(RadioGroup radiogroup, int i)
    {
        Log.i("FLatRateAlertActivity", "onCheckedChanged  ---------");
        choice = i;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mContext = getApplicationContext();
        com.android.internal.app.AlertController.AlertParams alertparams = mAlertParams;
        alertparams.mIconId = 0x108009b;
        if("XEC".equals(SystemProperties.get("ro.csc.sales_code")))
        {
            alertparams.mTitle = getString(0x7f0a004a);
            alertparams.mMessage = getString(0x7f0a004b);
        } else
        {
            alertparams.mTitle = getString(0x7f0a0046);
            Object aobj[] = new Object[1];
            aobj[0] = getString(0x7f0a0048);
            alertparams.mMessage = getString(0x7f0a0047, aobj);
        }
        alertparams.mCancelable = false;
        alertparams.mView = createView();
        setupAlert();
    }

    protected static int ans = -1;
    protected static int choice = -2;
    protected static CheckBox mCheckBox;
    protected static RadioGroup mRadioGroup;
    private boolean fRateVal;
    private Context mContext;
    private RadioButton radioOption2;





/*
    static boolean access$202(FlatRateAlertActivity flatratealertactivity, boolean flag)
    {
        flatratealertactivity.fRateVal = flag;
        return flag;
    }

*/
}
