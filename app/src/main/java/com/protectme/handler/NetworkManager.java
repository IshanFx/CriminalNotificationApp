package com.protectme.handler;

/**
 * Created by IshanFx on 1/30/2016.
 */
public class NetworkManager {
    public static final String hostIPPort  = "192.168.42.200:8082/ProtectApp";

    public static final String url_saveCaseLocation     = "http://"+hostIPPort+"/public/usersavelocation";
    public static final String url_saveEvidenceLocation = "http://"+hostIPPort+"/public/usersaveevidence";
    public static final String url_getHistroryLocation  = "http://"+hostIPPort+"/public/userhistory";


    public static final String url_getCaseDetails       = "http://"+hostIPPort+"/public/policeopencase";
    public static final String url_getLoginVerify       = "http://"+hostIPPort+"/public/userlogin";
    public static final String url_closeCase            = "http://"+hostIPPort+"/public/userclosecase";
    public static final String url_upload               = "http://"+hostIPPort+"/public/fileupload";

}
