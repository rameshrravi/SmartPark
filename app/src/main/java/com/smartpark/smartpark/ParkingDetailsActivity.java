package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smartpark.smartpark.ui.home.HomeFragment;

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

public class ParkingDetailsActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    String token;
    String parkingMarshalID;
    RecyclerView recyclerView;
    List<ParkingDetailsModel> parkingDetailsModelList=new ArrayList<>();
    ParkingDetailsModel parkingDetailsModel=new ParkingDetailsModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_details);
        recyclerView=findViewById(R.id.recyclerview_parking_details);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token=preferences.getString(StringConstants.prefToken,"");
        parkingMarshalID=preferences.getString(StringConstants.prefParkingMarshalID,"");

        getRecentActivities();


    }

    public void getRecentActivities(){
        final ProgressDialog pDialog=new ProgressDialog(ParkingDetailsActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParkingDetailsActivity.this);
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

                                    if(object.has("parking_list")){
                                        JSONArray recentArray=object.getJSONArray("parking_list");
                                        for(int i=0;i<recentArray.length();i++){

                                            JSONObject jsonObject1=recentArray.getJSONObject(i);
                                            parkingDetailsModel=new ParkingDetailsModel();
                                            parkingDetailsModel.setId(jsonObject1.getString("id"));
                                            parkingDetailsModel.setDateTime(jsonObject1.getString("datetime"));
                                            parkingDetailsModel.setTimeFormat(jsonObject1.getString("timeformat"));
                                            parkingDetailsModel.setPlateNo(jsonObject1.getString("plateno"));
                                            parkingDetailsModel.setBayNo(jsonObject1.getString("bayno"));
                                            parkingDetailsModel.setCountryCode(jsonObject1.getString("country_code"));
                                            parkingDetailsModel.setPhoneNo(jsonObject1.getString("phoneno"));
                                            parkingDetailsModel.setEmailID(jsonObject1.getString("email"));
                                            parkingDetailsModel.setStatus(jsonObject1.getString("status"));
                                            parkingDetailsModel.setDateFormat(jsonObject1.getString("datetimeformat"));
                                            parkingDetailsModel.setEndtimeformat(jsonObject1.getString("endtimeformat"));
                                            parkingDetailsModel.setEnddateformat(jsonObject1.getString("datetimeformatend"));
                                            parkingDetailsModel.setType(jsonObject1.getString("type"));
                                            parkingDetailsModel.setValid_until(jsonObject1.getString("valid_until"));
                                            parkingDetailsModel.setAmount_collect_USD(jsonObject1.getString("amount_collect_USD"));
                                            parkingDetailsModel.setAmount_owned(jsonObject1.getString("amount_owned"));
                                            parkingDetailsModel.setPayment_type(jsonObject1.getString("payment_type"));
                                            parkingDetailsModelList.add(parkingDetailsModel);
                                        }

                                        SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(ParkingDetailsActivity.this, parkingDetailsModelList);
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
                MyData.put("method", "viewall_parking_list");

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);
Log.i("ajdhjh",MyData.toString());
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ParkingDetailsActivity.this);
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


    public void backPressed(View view){

        onBackPressed();
    }

}