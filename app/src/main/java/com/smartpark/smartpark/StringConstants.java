package com.smartpark.smartpark;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class StringConstants {

   // public static String mainUrl="https://demosmartpark.caploc.io/mapiv2/index.php";
    //public static String mainUrl="https://smartpark.caploc.io/mapiv2/";
    public static String mainUrl="https://smartpark.caploc.io/mapiv3/";
    public static String loginUrl="index.php";
    public static String getAccessTokenUrl="get_token.php?";

    public static String prefToken ="UserToken";
    public static String prefSuperVisorToken ="SuperVisorToken";
    public static String prefSiteManagerToken ="SiteManagerToken";
    public static String prefUserID ="UserID";
    public static String prefEmailID ="EmailID";
    public static String prefFirstName ="FirstName";
    public static String prefLastName ="LastName";
    public static String prefMobileNumber ="MobileNumber";
     public static String prefLogoutPin ="LogoutPin";
    public static String prefSupervisorLoginID ="SupervisorLoginID";
    public static String prefSupervisorName ="SupervisorName";
    public static String prefPassword ="Password";


    public static String prefSiteManagerID ="SiteManagerID";
    public static String prefSiteManagerFirstName ="SiteManagerFirstName";
    public static String prefSiteManagerLastName ="SiteManagerLastName";
    public static String prefSiteManagerEmail ="SiteManagerEmail";
    public static String prefSiteManagerPhoneNo ="SiteManagerPhoneNumber";
    public static String prefSiteManagerLogoutPin ="SiteManagerLogoutPin";

 public static String prefPrecinctID ="PrecinctID";
 public static String prefParkingMarshalID ="ParkingMarshaID";
 public static String prefType ="Type";
 public static String prefBayID ="BayID";
 public static String prefParkingMarshalLoginID ="ParkingMarshalLoginID";
    public static String prefParkingMarshalFirstName ="ParkingMarshalFirstName";
    public static String prefParkingMarshalLastName ="ParkingMarshalLastName";
    public static String prefParkingMarshalEmail ="ParkingMarshalEmail";
    public static String prefParkingMarshalPhoneNo ="ParkingMarshalPhoneNumber";
    public static String prefParkingMarshalLogoutPin ="ParkingMarshalLogoutPin";
    public static String prefParkingMarshalPassword ="ParkingMarshalPassword";

    public static String prefCategory ="Category";
    public static String prefZoneList ="ZoneList";

    public static String prefMySharedPreference="SmartParkPreference";



    public static String ErrorMessage(VolleyError volleyError) {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
// Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
// Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
// Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
// Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
//Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
//Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        //   Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        return message;
    }

}
