package com.smartpark.smartpark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SupervisorLoginActivity1 extends AppCompatActivity {

    EditText et_login_id,et_password;
    TextView tv_site_manager_name;
    TextView tv_login;
    String s_login_id,s_password,token="",token1="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    SiteManagerModel siteManagerModel=new SiteManagerModel();
Window window;
Boolean isSiteManagerLogin=false;
    String parkingMarshalID,supervisorID;
    String longitude="0.0",latitude="0.0";
    String handoverType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_supervisor_login1);

        et_login_id=findViewById(R.id.edittext_login_id);
        et_password=findViewById(R.id.edittext_password);
        tv_login=findViewById(R.id.text_login);
        tv_site_manager_name=findViewById(R.id.text_site_manager_name);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token1=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");


        isSiteManagerLogin=preferences.getBoolean("SiteManagerLogin",false);

        handoverType=getIntent().getStringExtra("HandoverType");



/*
        if(isSiteManagerLogin){
            Intent i=new Intent(getApplicationContext(),PrecinctsListActivity.class);
            startActivity(i);
            finish();
        }
*/


      //  siteManagerModel= (SiteManagerModel) getIntent().getSerializableExtra("Site");

        tv_site_manager_name.setText(preferences.getString(StringConstants.prefSupervisorName,""));



        et_login_id.setText(preferences.getString(StringConstants.prefSupervisorLoginID,""));

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", msg);
                        // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_login_id=et_login_id.getText().toString().trim();
                s_password=et_password.getText().toString().trim();

                if(!s_login_id.isEmpty()){

                    if(!s_password.isEmpty()){

                        login();

                    }else {
                        showAlertDialog("Please enter password");
                    }

                }else {
                    showAlertDialog("Please enter login ID");
                }

            }
        });


    }
    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SupervisorLoginActivity1.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Smart Park");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void login(){
        final ProgressDialog pDialog=new ProgressDialog(SupervisorLoginActivity1.this);
        pDialog.setMessage("Authenticating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(SupervisorLoginActivity1.this);
        requestQueue.getCache().clear();

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, StringConstants.mainUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Response",response);

                try {

                    JSONObject jsonObject=new JSONObject(response.trim());
                    if(jsonObject.has("response")){

                        JSONArray responseArray=jsonObject.getJSONArray("response");

                        if(responseArray.length()>0){
                            JSONObject object=responseArray.getJSONObject(0);
                            if(object.has("status")){
                                String status = object.getString("status");
                                if(status.equals("success")){

                                    editor.putString(StringConstants.prefSuperVisorToken,object.getString("token"));
                                    editor.putString(StringConstants.prefFirstName,object.getString("first_name"));
                                    editor.putString(StringConstants.prefLastName,object.getString("last_name"));
                                    editor.putString(StringConstants.prefEmailID,object.getString("email"));
                                    editor.putString(StringConstants.prefUserID,object.getString("id"));
                                    editor.putString(StringConstants.prefMobileNumber,object.getString("phoneno"));
                                    editor.putString(StringConstants.prefLogoutPin,object.getString("logoutpin"));
                                    editor.putString(StringConstants.prefPassword,s_password);
                                    editor.putString(StringConstants.prefSupervisorLoginID,s_login_id);
                                    editor.putString(StringConstants.prefType,"supervisor");
                                    editor.putBoolean("isLogin",true);
                                    editor.apply();
                                    editor.commit();

                                    supervisorID=object.getString("id");

                                    handover();


                                }else {
                                    showAlertDialog(object.getString("message"));
                                }
                            }else {
                                showAlertDialog(object.getString("message"));
                            }
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                pDialog.dismiss();
                String errorMessage=StringConstants.ErrorMessage(error);

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("method", "supervisorlogin");
                MyData.put("loginid", s_login_id);
                MyData.put("password", s_password);
                MyData.put("fcm_token", token);
                MyData.put("reg_datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void handover(){
        final ProgressDialog pDialog=new ProgressDialog(SupervisorLoginActivity1.this);
        pDialog.setMessage("Updating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(SupervisorLoginActivity1.this);
        requestQueue.getCache().clear();

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, StringConstants.mainUrl , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Response",response);

                try {

                    JSONObject jsonObject=new JSONObject(response.trim());
                    if(jsonObject.has("response")){

                        JSONArray responseArray=jsonObject.getJSONArray("response");

                        if(responseArray.length()>0){
                            JSONObject object=responseArray.getJSONObject(0);
                            if(object.has("status")){
                                String status = object.getString("status");
                                if(status.equals("success")){


                                    if(handoverType.equals("end")){
                                        Toast.makeText(SupervisorLoginActivity1.this,"Handover success",Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(getApplicationContext(),HomePageActivity.class);
                                        startActivity(i);
                                        finish();
                                    }else {
                                        Toast.makeText(SupervisorLoginActivity1.this,"Handover success",Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(getApplicationContext(),ParkingMarshalLoginActivity.class);
                                        startActivity(i);
                                        finish();
                                    }



                                }else {
                                    showAlertDialog(object.getString("message"));
                                }
                            }else {
                                showAlertDialog(object.getString("message"));
                            }
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                pDialog.dismiss();
                String errorMessage=StringConstants.ErrorMessage(error);

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("method", "handover");
                MyData.put("latitude", latitude);
                MyData.put("longitude", longitude);
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("supervisor_id", supervisorID);
                MyData.put("token", token1);
                MyData.put("datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }


}