package com.protectme.handler;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.protectme.dao.Family;
import com.protectme.database.RealMAdapter;

import java.util.List;

/**
 * Created by IshanFx on 1/12/2016.
 */
public class SMSManager {
    RealMAdapter realMAdapter;
    public void sendSMS(String Details,Context context){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            realMAdapter = new RealMAdapter(context);
            Log.d("SMS", "ssss");
            List<Family> families = realMAdapter.getFamily();
            Log.d("SMS", families.get(0).getNumber().toString());
            smsManager.sendTextMessage(families.get(0).getNumber().toString(), null, Details, null, null);
        }
        catch (Exception ex){
            Log.d("SMS", ex.toString());
        }
    }


}
