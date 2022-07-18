package com.smartpark.smartpark;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity {

    TextView tv_login_id,tv_password,tv_first_name,tv_last_name,tv_email_id,tv_phone_number,tv_logout_pin;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    LinearLayout layout_home,layout_profile,layout_invoice,layout_logout;
    ImageView iv_scan;
    String token="",parkingMarshalID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        tv_login_id=findViewById(R.id.text_login_id);
        tv_password=findViewById(R.id.text_password);
        tv_first_name=findViewById(R.id.text_first_name);
        tv_last_name=findViewById(R.id.text_last_name);
        tv_email_id=findViewById(R.id.text_email_id);
        tv_phone_number=findViewById(R.id.text_phone_number);
        tv_logout_pin=findViewById(R.id.text_logout_pin);

        layout_home=findViewById(R.id.layout_home);
        layout_profile=findViewById(R.id.layout_profile);
        layout_invoice=findViewById(R.id.layout_invoice);
        layout_logout=findViewById(R.id.layout_logout);
        iv_scan=findViewById(R.id.image_scan);

        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),HomePageActivity.class);
                startActivity(i);

            }
        });
        layout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                startActivity(i);
                finish();
            }
        });
        layout_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),InvoiceActivity.class);
                startActivity(i);
            }
        });
        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    final Dialog dialog = new Dialog(PersonalInfoActivity.this,R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Setting dialogview
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.CENTER);

                    dialog.setContentView(R.layout.layout_logout_dialog);

                    TextView tv_no=dialog.findViewById(R.id.text_no);
                    TextView tv_yes=dialog.findViewById(R.id.text_yes);
                    EditText et_logout_pin=dialog.findViewById(R.id.edittext_logout_pin);
                    tv_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });
                    tv_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if(!et_logout_pin.getText().toString().trim().isEmpty()){

                                dialog.dismiss();
                                logout(et_logout_pin.getText().toString().trim());


                            }else {
                                Toast.makeText(getApplicationContext(),"Please enter logout pin",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    dialog.show();


                }
            }
        });
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),ParCarActivity.class);
                startActivity(i);

            }
        });

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();
        token=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");

        tv_login_id.setText(preferences.getString(StringConstants.prefParkingMarshalLoginID,""));
        tv_password.setText(preferences.getString(StringConstants.prefParkingMarshalPassword,""));
        tv_first_name.setText(preferences.getString(StringConstants.prefParkingMarshalFirstName,""));
        tv_last_name.setText(preferences.getString(StringConstants.prefParkingMarshalLastName,""));
        tv_email_id.setText(preferences.getString(StringConstants.prefParkingMarshalEmail,""));
        tv_phone_number.setText(preferences.getString(StringConstants.prefParkingMarshalPhoneNo,""));
        tv_logout_pin.setText(preferences.getString(StringConstants.prefParkingMarshalLogoutPin,""));
    }
    public void logout(String logoutPin){
        final ProgressDialog pDialog=new ProgressDialog(PersonalInfoActivity.this);
        pDialog.setMessage("Submitting..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(PersonalInfoActivity.this);
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
                                    editor.clear();
                                    editor.apply();
                                    editor.commit();
                                    Intent i=new Intent(getApplicationContext(),SiteManagerLoginActivity.class);
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
                MyData.put("method", "parkingmarshallogout");
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);
                MyData.put("logoutpin", logoutPin);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalInfoActivity.this);
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

    public void backPressed(View view){

        onBackPressed();
    }

}