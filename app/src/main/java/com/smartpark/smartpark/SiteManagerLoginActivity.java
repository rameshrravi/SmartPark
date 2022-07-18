package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class SiteManagerLoginActivity extends AppCompatActivity {

    EditText et_login_id,et_password;
    TextView tv_site_manager_name;
    TextView tv_login;
    String s_login_id,s_password,token="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    SiteManagerModel siteManagerModel=new SiteManagerModel();
Window window;
Boolean isSiteManagerLogin=false;
    Boolean isLogin=false;
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
        setContentView(R.layout.activity_site_manager_login);

        et_login_id=findViewById(R.id.edittext_login_id);
        et_password=findViewById(R.id.edittext_password);
        tv_login=findViewById(R.id.text_login);
        tv_site_manager_name=findViewById(R.id.text_site_manager_name);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        isSiteManagerLogin=preferences.getBoolean("SiteManagerLogin",false);

        isLogin=preferences.getBoolean("ParkingMarshalLogin",false);

        if(isLogin){
            Intent i=new Intent(getApplicationContext(),HomePageActivity.class);
            startActivity(i);
            finish();
        }

/*
        if(isSiteManagerLogin){
            Intent i=new Intent(getApplicationContext(),PrecinctsListActivity.class);
            startActivity(i);
            finish();
        }
*/


       // siteManagerModel= (SiteManagerModel) getIntent().getSerializableExtra("Site");

       // tv_site_manager_name.setText(siteManagerModel.getFirstName()+" "+siteManagerModel.getLastName());



      //  et_login_id.setText(siteManagerModel.getLoginID());

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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiteManagerLoginActivity.this);
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
        final ProgressDialog pDialog=new ProgressDialog(SiteManagerLoginActivity.this);
        pDialog.setMessage("Authenticating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(SiteManagerLoginActivity.this);
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

                                    editor.putString(StringConstants.prefSiteManagerToken,object.getString("token"));
                                    editor.putString(StringConstants.prefSiteManagerFirstName,object.getString("first_name"));
                                    editor.putString(StringConstants.prefSiteManagerLastName,object.getString("last_name"));
                                    editor.putString(StringConstants.prefSiteManagerEmail,object.getString("email"));
                                    editor.putString(StringConstants.prefSiteManagerID,object.getString("id"));
                                    editor.putString(StringConstants.prefSiteManagerPhoneNo,object.getString("phoneno"));
                                    editor.putString(StringConstants.prefSiteManagerLogoutPin,object.getString("logoutpin"));
                                    editor.putBoolean("SiteManagerLogin",true);
                                    editor.apply();
                                    editor.commit();


                                    Intent i=new Intent(getApplicationContext(),SupervisiorActivity.class);
                                    startActivity(i);
                                    finish();
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
                MyData.put("method", "sitemanagerlogin");
                MyData.put("loginid", s_login_id);
                MyData.put("password", s_password);
                MyData.put("fcm_token", token);
                MyData.put("reg_datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }


}