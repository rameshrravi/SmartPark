package com.smartpark.smartpark.ui.home;

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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smartpark.smartpark.GpsTracker;
import com.smartpark.smartpark.Listener;
import com.smartpark.smartpark.LoginActivity;
import com.smartpark.smartpark.NFCReadFragment;
import com.smartpark.smartpark.NFCWriteFragment;
import com.smartpark.smartpark.NfcActivity;
import com.smartpark.smartpark.ParCarActivity;
import com.smartpark.smartpark.ParkingDetailsActivity;
import com.smartpark.smartpark.ParkingDetailsModel;
import com.smartpark.smartpark.R;
import com.smartpark.smartpark.ScanActivity;
import com.smartpark.smartpark.SiteManagerActivity;
import com.smartpark.smartpark.StringConstants;
import com.smartpark.smartpark.SupervisorLoginActivity1;
import com.smartpark.smartpark.databinding.FragmentHomeBinding;

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

public class HomeFragment extends Fragment {
RecyclerView recyclerView;
List<ParkingDetailsModel> parkingDetailsModelList=new ArrayList<>();
ParkingDetailsModel parkingDetailsModel=new ParkingDetailsModel();
TextView tv_last_checkin_office,tv_last_checkin_precinct,tv_last_hnadover,tv_office_checkin,tv_precinct_checkin,tv_handover;
  View view;
  String officeLastCheckinStatus="",precinctLastCheckinStatus="",lastHandoverStatus="";
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    String token;
    String parkingMarshalID,precinctID;
    String longitude="0.0",latitude="0.0";
    TextView tv_view_all;
    TextView tv_parking_marshal_name;
    GpsTracker gpsTracker;
    String type="";
    ImageView iv_scan;

    private NFCReadFragment mNfcReadFragment;
    private boolean isDialogDisplayed = false;
    public static final String TAG = "NfcDemo";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView=view.findViewById(R.id.recyclerview_recent_activities);
        tv_last_checkin_office=view.findViewById(R.id.text_last_checkin_office);
        tv_last_checkin_precinct=view.findViewById(R.id.text_last_checkin_precinct);
        tv_last_hnadover=view.findViewById(R.id.text_last_handover);
        tv_office_checkin=view.findViewById(R.id.text_office_checkin);
        tv_precinct_checkin=view.findViewById(R.id.text_precinct_checkin);
        tv_parking_marshal_name=view.findViewById(R.id.text_parking_marshal_name);
        tv_handover=view.findViewById(R.id.text_handover);
        tv_view_all=view.findViewById(R.id.text_view_all);
        iv_scan=view.findViewById(R.id.image_scan);

        preferences =getActivity().getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");
        precinctID=preferences.getString(StringConstants.prefPrecinctID,"");
        
        type=preferences.getString(StringConstants.prefType,"");
        
        if(type.equals("parkingmarshal")){
            tv_parking_marshal_name.setText("Hi, "+preferences.getString(StringConstants.prefParkingMarshalFirstName,"")+" "+preferences.getString(StringConstants.prefParkingMarshalLastName,""));
        }else if(type.equals("supervisor")){
            tv_parking_marshal_name.setText("Hi, "+preferences.getString(StringConstants.prefSupervisorName,""));
        }
        
        



        gpsTracker=new GpsTracker(getContext());

        /*final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                System.out.println("myHandler: here!"); // Do your work here
                handler.postDelayed(this, delay);

            }
        }, delay);
*/

       // showReadFragment();

        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), NfcActivity.class);
                startActivity(i);

            }
        });
        tv_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getContext(), ParkingDetailsActivity.class);
                startActivity(i);
            }
        });

        tv_office_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                latitude = String.valueOf(gpsTracker.getLatitude());
                longitude = String.valueOf(gpsTracker.getLongitude());

                if(officeLastCheckinStatus.equals("checkout")){

                    officeChecking("checkin");
                }else {
                    officeChecking("checkout");
                }


            }
        });
        tv_precinct_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                latitude = String.valueOf(gpsTracker.getLatitude());
                longitude = String.valueOf(gpsTracker.getLongitude());

                if(precinctLastCheckinStatus.equals("checkout")){

                    precinctCheckin("checkin");
                }else {
                    precinctCheckin("checkout");
                }


            }
        });

        tv_handover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // handover();

                Intent i=new Intent(getContext(), SupervisorLoginActivity1.class);
                i.putExtra("HandoverType",lastHandoverStatus);
                startActivity(i);
            }
        });

        getRecentActivities();



        return view;
    }




    private void showReadFragment() {

        mNfcReadFragment = (NFCReadFragment)getActivity(). getSupportFragmentManager().findFragmentByTag(NFCReadFragment.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = NFCReadFragment.newInstance();
        }
        mNfcReadFragment.show(getActivity().getSupportFragmentManager(),NFCReadFragment.TAG);

    }




    public void getRecentActivities(){
        final ProgressDialog pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                    if(object.has("recent_activity")){
                                        parkingDetailsModelList=new ArrayList<>();
                                        JSONArray recentArray=object.getJSONArray("recent_activity");
                                        for(int i=0;i<recentArray.length();i++){

                                            JSONObject jsonObject1=recentArray.getJSONObject(i);
                                            parkingDetailsModel=new ParkingDetailsModel();
                                            parkingDetailsModel.setId(jsonObject1.getString("id"));
                                            parkingDetailsModel.setDateTime(jsonObject1.getString("datetime"));
                                            parkingDetailsModel.setDateFormat(jsonObject1.getString("dateformat"));
                                            parkingDetailsModel.setTimeFormat(jsonObject1.getString("timeformat"));
                                            parkingDetailsModel.setPlateNo(jsonObject1.getString("plateno"));
                                            parkingDetailsModel.setBayNo(jsonObject1.getString("bayno"));
                                            parkingDetailsModel.setCountryCode(jsonObject1.getString("country_code"));
                                            parkingDetailsModel.setPhoneNo(jsonObject1.getString("phoneno"));
                                            parkingDetailsModel.setEmailID(jsonObject1.getString("email"));
                                            parkingDetailsModel.setStatus(jsonObject1.getString("status"));


                                            parkingDetailsModelList.add(parkingDetailsModel);
                                        }

                                        SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(getContext(), parkingDetailsModelList);
                                        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getContext());
                                        recyclerView.setLayoutManager(horizontalLayoutManager1);
                                        recyclerView.setAdapter(enforceAdapter);

                                    }

                                    officeLastCheckinStatus=object.getString("office_last_status");
                                    precinctLastCheckinStatus=object.getString("precinct_last_status");
                                    lastHandoverStatus=object.getString("handover_last_status");

                                    tv_last_checkin_office.setText("Last "+object.getString("office_last_status")+" "+object.getString("office_datetime"));
                                    tv_last_checkin_precinct.setText("Last "+object.getString("precinct_last_status")+" "+object.getString("precinct_datetime"));
                                    tv_last_hnadover.setText("Last Handover "+object.getString("handover_last_status")+" "+object.getString("handover_datetime"));


                                    if(precinctLastCheckinStatus.equals("checkin")){
                                        tv_precinct_checkin.setText("Check-out");
                                        tv_precinct_checkin.setTextColor(getResources().getColor(R.color.black));
                                        tv_precinct_checkin.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                    }else {
                                        tv_precinct_checkin.setText("Check-in");
                                        tv_precinct_checkin.setTextColor(getResources().getColor(R.color.white));
                                        tv_precinct_checkin.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_green));
                                    }

                                    if(officeLastCheckinStatus.equals("checkin")){
                                        tv_office_checkin.setText("Check-out");
                                        tv_office_checkin.setTextColor(getResources().getColor(R.color.black));
                                        tv_office_checkin.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                    }else {
                                        tv_office_checkin.setText("Check-in");
                                        tv_office_checkin.setTextColor(getResources().getColor(R.color.white));
                                        tv_office_checkin.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_green));
                                    }

                                if(lastHandoverStatus.equals("start")){

                                        tv_handover.setText("End Handover");

                                    }else {
                                      tv_handover.setText("Start Handover");
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
                MyData.put("method", "recent_activity");

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void handover(){
        final ProgressDialog pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Updating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                    Toast.makeText(getContext(),"Handover success",Toast.LENGTH_SHORT).show();
                                    getRecentActivities();

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
                MyData.put("token", token);
                MyData.put("datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void officeChecking(String checkinStatus){
        final ProgressDialog pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Updating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                  getRecentActivities();

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
                MyData.put("method", "officecheckin");
                MyData.put("type", checkinStatus);
                MyData.put("latitude", latitude);
                MyData.put("longitude", longitude);
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);
                MyData.put("datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void precinctCheckin(String checkinStatus){
        final ProgressDialog pDialog=new ProgressDialog(getContext());
        pDialog.setMessage("Updating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                    getRecentActivities();

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
                MyData.put("method", "precinctcheckin");
                MyData.put("type", checkinStatus);
                MyData.put("latitude", latitude);
                MyData.put("longitude", longitude);
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("precinct_id", precinctID);
                MyData.put("token", token);
                MyData.put("datetime", currentDate+" "+currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public class SiteManagerAdapter extends RecyclerView.Adapter<SiteManagerAdapter.MyViewHolder> implements Filterable {

        private List<ParkingDetailsModel> siteManagerModelList;
        private List<ParkingDetailsModel> filteredSiteManagerModelList;

        Context context;
        int row_index=-1;

        public SiteManagerAdapter(Context context, List<ParkingDetailsModel> siteManagerModelList){
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList =siteManagerModelList;

        }
        @NonNull
        @Override
        public SiteManagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_parking_details_row, parent, false);

            return new SiteManagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SiteManagerAdapter.MyViewHolder holder, int position) {
            final ParkingDetailsModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_date_time.setText(siteManagerModel.getDateFormat());
            holder.tv_plate_no.setText("Number Plate : "+siteManagerModel.getPlateNo());
            holder.tv_bay_no.setText(siteManagerModel.getBayNo());

            if(siteManagerModel.getStatus().equalsIgnoreCase("notpaid")){
                holder.tv_view.setBackground(context.getResources().getDrawable(R.drawable.rectangle_red_small));
                holder.iv_car.setImageDrawable(context.getResources().getDrawable(R.drawable.car_red));
                holder.tv_bay_no.setTextColor(context.getResources().getColor(R.color.red1));
            }else if(siteManagerModel.getStatus().equalsIgnoreCase("paid")){
                holder.tv_view.setBackground(context.getResources().getDrawable(R.drawable.rectangle_green_small));
                holder.iv_car.setImageDrawable(context.getResources().getDrawable(R.drawable.car_green));
                holder.tv_bay_no.setTextColor(context.getResources().getColor(R.color.green));
            }else if(siteManagerModel.getStatus().equalsIgnoreCase("unpaid")){
                holder.tv_view.setBackground(context.getResources().getDrawable(R.drawable.orange_icon));
                holder.iv_car.setImageDrawable(context.getResources().getDrawable(R.drawable.car_red));
                holder.tv_bay_no.setTextColor(context.getResources().getColor(R.color.orange));
            }



            holder.tv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i=new Intent(context,ParCarActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("ParkingDetail",siteManagerModel);
                    startActivity(i);
                }
            });





        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date_time,tv_plate_no,tv_bay_no,tv_view;
            LinearLayout linearLayoutContainer;
            ImageView iv_car;
            public MyViewHolder(View view) {
                super(view);


                tv_date_time = (TextView) view.findViewById(R.id.text_date_time);
                tv_plate_no = (TextView) view.findViewById(R.id.text_number_plate);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
                tv_view = (TextView) view.findViewById(R.id.text_view);
                iv_car = (ImageView) view.findViewById(R.id.image_car);

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
                        filteredSiteManagerModelList = siteManagerModelList;
                    } else {
                        List<ParkingDetailsModel> filteredList = new ArrayList<>();
                        for (ParkingDetailsModel row : siteManagerModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if ( row.getBayNo().contains(charSequence)|| row.getPlateNo().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        filteredSiteManagerModelList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredSiteManagerModelList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredSiteManagerModelList = (ArrayList<ParkingDetailsModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }

    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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


}