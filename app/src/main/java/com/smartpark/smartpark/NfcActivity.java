package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcActivity extends AppCompatActivity implements Listener{
    LinearLayout layout_home,layout_profile,layout_invoice,layout_logout;
    ImageView iv_scan;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    String token;
    String parkingMarshalID;
    List<NFCModel> nfcModelList = new ArrayList<>();
    NFCModel nfcModel = new NFCModel();
    private NfcAdapter mNfcAdapter;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    public static final String TAG = "NfcDemo";
    String longitude="0.0",latitude="0.0";
    GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        layout_home=findViewById(R.id.layout_home);
        layout_profile=findViewById(R.id.layout_profile);
        layout_invoice=findViewById(R.id.layout_invoice);
        layout_logout=findViewById(R.id.layout_logout);
        iv_scan=findViewById(R.id.image_scan);
        recyclerView=findViewById(R.id.recyclerview_park_car_list);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");

        gpsTracker=new GpsTracker(NfcActivity.this);

        recyclerView = findViewById(R.id.recyclerview_nfc_list);


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
            }
        });
        layout_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),InvoiceActivity.class);
                startActivity(i);
                finish();
            }
        });
        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    final Dialog dialog = new Dialog(NfcActivity.this,R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Setting dialogview
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);

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
initNFC();
        getNFCList();
    }

    public void logout(String logoutPin){
        final ProgressDialog pDialog=new ProgressDialog(NfcActivity.this);
        pDialog.setMessage("Submitting..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(NfcActivity.this);
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
    public void getNFCList(){
        final ProgressDialog pDialog=new ProgressDialog(NfcActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(NfcActivity.this);
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

                                    if(object.has("bay_list")){
                                        JSONArray recentArray=object.getJSONArray("bay_list");
                                        for(int i=0;i<recentArray.length();i++){

                                            JSONObject jsonObject1=recentArray.getJSONObject(i);
                                            nfcModel=new NFCModel();
                                            nfcModel.setId(jsonObject1.getString("id"));
                                            nfcModel.setParkingBay(jsonObject1.getString("bay_name"));
                                            nfcModel.setStreet(jsonObject1.getString("street"));

                                            nfcModelList.add(nfcModel);
                                        }

                                        SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(NfcActivity.this, nfcModelList);
                                        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                        recyclerView.setLayoutManager(horizontalLayoutManager1);
                                        recyclerView.setAdapter(enforceAdapter);

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
                MyData.put("method", "nfcbaylist");

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NfcActivity.this);
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
    public class SiteManagerAdapter extends RecyclerView.Adapter<SiteManagerAdapter.MyViewHolder> implements Filterable {

        private List<NFCModel> nfcModelList;
        private List<NFCModel> filterednfcModelList;

        Context context;
        int row_index=-1;

        public SiteManagerAdapter(Context context, List<NFCModel> nfcModelList){
            this.nfcModelList = nfcModelList;
            this.context = context;
            this.filterednfcModelList =nfcModelList;

        }
        @NonNull
        @Override
        public SiteManagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_nfc_list_row, parent, false);

            return new SiteManagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SiteManagerAdapter.MyViewHolder holder, int position) {
            final NFCModel nfcModel = filterednfcModelList.get(position);


            holder.tv_bay_no.setText(nfcModel.getParkingBay()+" - "+nfcModel.getStreet());

            holder.iv_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    editor.putString(StringConstants.prefBayID,nfcModel.getId());
                    editor.apply();
                    editor.commit();

                    showReadFragment();
                }
            });

            holder.tv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    longitude = String.valueOf(gpsTracker.getLongitude());
                    latitude = String.valueOf(gpsTracker.getLatitude());
                    getRecentActivities("",nfcModel.getId());
                }
            });

            holder.iv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context,TransactionDetailsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("BayID",nfcModel.getId());
                    i.putExtra("BayNumber",nfcModel.getParkingBay());
                    context.startActivity(i);

                }
            });




        }

        @Override
        public int getItemCount() {
            return filterednfcModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_amount,tv_plate_no,tv_bay_no,tv_status;
            LinearLayout linearLayoutContainer;
            ImageView iv_scan,iv_view;

            public MyViewHolder(View view) {
                super(view);

                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
                tv_status = (TextView) view.findViewById(R.id.text_status);
                iv_scan = (ImageView) view.findViewById(R.id.image_scan);
                iv_view = (ImageView) view.findViewById(R.id.image_view);

                linearLayoutContainer = (LinearLayout) view.findViewById(R.id.layout_container);



            }
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filterednfcModelList = nfcModelList;
                    } else {
                        List<NFCModel> filteredList = new ArrayList<>();
                        for (NFCModel row : nfcModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if ( row.getParkingBay().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }
                        filterednfcModelList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterednfcModelList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterednfcModelList = (ArrayList<NFCModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }
    public void backPressed(View view){

        onBackPressed();
    }

    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            // finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "NFC is enabled.", Toast.LENGTH_LONG).show();
            //mTextView.setText("Read Content");
        }


    }

    public void getRecentActivities(String tag,String bayID){
        final ProgressDialog pDialog=new ProgressDialog(NfcActivity.this);
        pDialog.setMessage("Updating please wait..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());


        RequestQueue requestQueue = Volley.newRequestQueue(NfcActivity.this);
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

                                    Toast.makeText(getApplicationContext(),"Updated successfully",Toast.LENGTH_LONG).show();



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
                MyData.put("method", "manualbayentry");
                MyData.put("datetime", currentDate+" "+currentTime);

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("nfc_no", tag);
                MyData.put("latitude", latitude);
                MyData.put("longitude", longitude);
                MyData.put("token", token);
                MyData.put("type", "occupied");
                MyData.put("bay_id", bayID);
                MyData.put("mode", "Manual");

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }


    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getSupportFragmentManager(),NFCReadFragment.TAG);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        /*super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: "+intent.getAction());

        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);


                if (isWrite) {

                    String messageToWrite = mEtMessage.getText().toString();
                    mNfcWriteFragment = (NFCWriteFragment) getSupportFragmentManager().findFragmentByTag(NFCWriteFragment.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite);

                } else {

                    handleIntent(intent);
                }
            }*/

        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if (tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                mNfcReadFragment = (NFCReadFragment) getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);
                mNfcReadFragment.onNfcDetected(ndef);
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        //setupForegroundDispatch(this, mNfcAdapter);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);


    }
    @Override
    protected void onPause() {
        //  stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }
    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
    }


}