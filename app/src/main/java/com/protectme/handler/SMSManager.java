package com.protectme.handler;

import android.telephony.SmsManager;

/**
 * Created by IshanFx on 1/12/2016.
 */
public class SMSManager {

    public void sendSMS(String Details){
        SmsManager smsManager = SmsManager.getDefault();
       // smsManager.sendTextMessage("0716097337", null, Details, null, null);
    }
}
