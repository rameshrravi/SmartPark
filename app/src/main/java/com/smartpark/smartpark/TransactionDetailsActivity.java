package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
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

public class TransactionDetailsActivity extends AppCompatActivity {

    LinearLayout layout_home,layout_profile,layout_invoice,layout_logout;
    ImageView iv_scan;
    RecyclerView recyclerView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    String token;
    String bay_id="";
    String parkingMarshalID;
    List<NFCModel> nfcModelList = new ArrayList<>();
    NFCModel nfcModel = new NFCModel();
    private NfcAdapter mNfcAdapter;
    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    public static final String TAG = "NfcDemo";
    String longitude="0.0",latitude="0.0";
    GpsTracker gpsTracker;
    TextView tv_bay_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        layout_home=findViewById(R.id.layout_home);
        layout_profile=findViewById(R.id.layout_profile);
        layout_invoice=findViewById(R.id.layout_invoice);
        layout_logout=findViewById(R.id.layout_logout);
        iv_scan=findViewById(R.id.image_scan);
        recyclerView=findViewById(R.id.recyclerview_park_car_list);
        tv_bay_no=findViewById(R.id.text_bay_no);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");

        gpsTracker=new GpsTracker(TransactionDetailsActivity.this);

        recyclerView = findViewById(R.id.recyclerview_nfc_transaction_list);

        bay_id=getIntent().getStringExtra("BayID");

        tv_bay_no.setText(getIntent().getStringExtra("BayNumber"));


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
                    final Dialog dialog = new Dialog(TransactionDetailsActivity.this,R.style.Theme_Dialog);
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

        getNFCList();
    }

    public void logout(String logoutPin){
        final ProgressDialog pDialog=new ProgressDialog(TransactionDetailsActivity.this);
        pDialog.setMessage("Submitting..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(TransactionDetailsActivity.this);
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
        final ProgressDialog pDialog=new ProgressDialog(TransactionDetailsActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(TransactionDetailsActivity.this);
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

                                    if(object.has("bay_scan_list")){
                                        JSONArray recentArray=object.getJSONArray("bay_scan_list");
                                        for(int i=0;i<recentArray.length();i++){

                                            JSONObject jsonObject1=recentArray.getJSONObject(i);
                                            nfcModel=new NFCModel();
                                            nfcModel.setId(jsonObject1.getString("id"));
                                            nfcModel.setDate(jsonObject1.getString("date"));
                                            nfcModel.setTime(jsonObject1.getString("time"));
                                            nfcModel.setStatus(jsonObject1.getString("type"));

                                            nfcModelList.add(nfcModel);
                                        }

                                        SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(TransactionDetailsActivity.this, nfcModelList);
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
                MyData.put("method", "bay_scan_list");

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);
                MyData.put("bay_id", bay_id);
                MyData.put("date", currentDate);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TransactionDetailsActivity.this);
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
                    .inflate(R.layout.layout_nfc_transaction_row, parent, false);

            return new SiteManagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SiteManagerAdapter.MyViewHolder holder, int position) {
            final NFCModel nfcModel = filterednfcModelList.get(position);


            holder.tv_date.setText(nfcModel.getDate()+" "+nfcModel.getTime());
           // holder.tv_time.setText(nfcModel.getTime());
            holder.tv_status.setText(nfcModel.getStatus());


        }

        @Override
        public int getItemCount() {
            return filterednfcModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date,tv_time,tv_status;
            LinearLayout linearLayoutContainer;
            ImageView iv_scan;

            public MyViewHolder(View view) {
                super(view);

                tv_date = (TextView) view.findViewById(R.id.text_date);
                tv_time = (TextView) view.findViewById(R.id.text_time);
                tv_status = (TextView) view.findViewById(R.id.text_status);


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

}