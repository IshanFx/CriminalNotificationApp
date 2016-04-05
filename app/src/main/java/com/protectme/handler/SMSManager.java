package com.protectme.handler;

import android.content.Context;
import android.telephony.SmsManager;

import com.protectme.dao.Family;
import com.protectme.database.RealMAdapter;

import java.util.List;

/**
 * Created by IshanFx on 1/12/2016.
 */
public class SMSManager {

    public void sendSMS(String Details,Context context){
        SmsManager smsManager = SmsManager.getDefault();
        List<Family> families = new RealMAdapter(context).getFamily();
        smsManager.sendTextMessage(families.get(0).getNumber(), null, Details, null, null);
    }


}
