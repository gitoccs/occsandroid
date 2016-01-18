package com.occs.ldsoft.occs;

import android.app.Activity;
import android.content.Context;

/**
 * Created by yeliu on 15/7/30.
 * 所有web API的内容
 */
public class WebLinkStatic {
    public static final String WEBAPPLINK = "http://api.test.occs.com.cn/";

    public static final String MOBILEAPILINK = WEBAPPLINK + "MobileAPI.asmx/";
    public static final String LOGINAPI = MOBILEAPILINK + "TelLogin";
    public static final String LOGOUTAPI = MOBILEAPILINK + "TelLogout";
    public static final String GETPHONECODEREG = MOBILEAPILINK + "RegisterSend";
    public static final String GETPHONECODE = MOBILEAPILINK + "Send";
    public static final String TELREGISTER = MOBILEAPILINK + "TelRegister";
    public static final String CODEVALIDATE = MOBILEAPILINK + "Validate";
    public static final String FINDPHONE = MOBILEAPILINK + "Sendphone";
    public static final String SENDEMAILCODE = MOBILEAPILINK + "SendEmailcode";
    public static final String CHANGEPASSWORD = MOBILEAPILINK + "UpdatePassword";
    public static final String PERSONINFO = MOBILEAPILINK + "GetUserInfo";
    public static final String COMPANYINFO = MOBILEAPILINK + "GetCorperation";
    public static final String CHANGEPHONE = MOBILEAPILINK + "UpdateMobile";
    public static final String CHANGEEMAIL = MOBILEAPILINK + "UpdateEmail";
    public static final String ISEMAILUSED = MOBILEAPILINK + "EmailisUsed";
    public static final String UPLOADAVATAR = MOBILEAPILINK + "uploadAvatar";
    public static final String UPLOADUSERINDO = MOBILEAPILINK + "UpdateUserInfo";
    public static final String GETALLWORKCOUNT = MOBILEAPILINK + "GetAllWorkCount";
    public static final String GETVERSION = MOBILEAPILINK + "GetVersion";
    public static final String GETAPPMESSAGE = MOBILEAPILINK + "GetAppMessage";
    public static final String GETPROJECTDETAILS = MOBILEAPILINK + "GetProjectDetailsForV1";

    public static final String KEYWORD = "dhccpass";

    public static final String GONGDANLINK = WEBAPPLINK + "gongdan.html";
    public static final String DEMANDLINK = WEBAPPLINK + "demand.html";
    public static final String SENDDEMANDLINK = WEBAPPLINK + "sendDemand.html";
    public static final String MYACCOUNT = WEBAPPLINK + "myAccount.html";
    public static final String JOBLINK = WEBAPPLINK + "job.html";
    public static final String PERSONMAIN = WEBAPPLINK + "personMain.html";
    public static final String GREENHANDHELP = WEBAPPLINK + "helpIndex.html";
    public static final String ABOUTOCCS = WEBAPPLINK + "aboutOccs.html";
    public static final String CONTACTSERVICE = WEBAPPLINK + "contactService.html";
    public static final String ACTIVITYPAGE = WEBAPPLINK + "activity.html";
    public static final String NOTETEMPLATE1 = WEBAPPLINK + "noteTemplate01.html";
    public static final String SINGLESUITABLEGONGDAN = WEBAPPLINK + "singleSuitableGongdan.html";
}
