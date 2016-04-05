package com.protectme.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.protectme.database.RealMAdapter;

/**
 * Created by IshanFx on 1/30/2016.
 */
public class NetworkManager {

    public static final String hostIPPort  = "192.168.42.200:8082/ProtectApp/public";
    //public static final String hostIPPort  = "192.168.42.254:8082/ProtectApp/public";
    //public static final String hostIPPort  = "protectmelkapp.xyz";

    public static final String url_saveCaseLocation     = "http://"+hostIPPort+"/usersavelocation";
    public static final String url_saveEvidenceLocation = "http://"+hostIPPort+"/usersaveevidence";
    public static final String url_getHistroryLocation  = "http://"+hostIPPort+"/userhistory";


    public static final String url_getCaseDetails       = "http://"+hostIPPort+"/policeopencase";
    public static final String url_getLoginVerify       = "http://"+hostIPPort+"/userlogin";
    public static final String url_closeCase            = "http://"+hostIPPort+"/userclosecase";
    public static final String url_upload               = "http://"+hostIPPort+"/fileupload";
    public static String url_getOneCrime          = "http://"+hostIPPort+"/user/crime/";


    public static boolean checkNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
