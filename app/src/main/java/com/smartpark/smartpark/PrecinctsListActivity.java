package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class PrecinctsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
List<PrecinctsModel> precinctsModelList=new ArrayList<>();
PrecinctsModel precinctsModel=new PrecinctsModel();
    String token;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    TextView tv_site_manager_name;
    String siteManagerID;
    String supervisorID;
    SearchView searchView;
    PrecinctsAdapter enforceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precincts_list);
        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token = preferences.getString(StringConstants.prefSiteManagerToken,"");
        siteManagerID = preferences.getString(StringConstants.prefSiteManagerID,"");
        supervisorID = preferences.getString(StringConstants.prefUserID,"");

        recyclerView = findViewById(R.id.recyclerview_precincts_list);
        tv_site_manager_name=findViewById(R.id.text_site_manager_name);

        tv_site_manager_name.setText(preferences.getString(StringConstants.prefFirstName,"")+" "+preferences.getString(StringConstants.prefLastName,""));

        searchView=(SearchView)findViewById(R.id.search_view);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Precincts Name");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(enforceAdapter!=null){
                    // filter recycler view when query submitted
                    if(TextUtils.isEmpty(query)){  //Show all data
                        enforceAdapter.notifyDataSetChanged();

                    }else
                    {
                        enforceAdapter.getFilter().filter(query);

                    }

                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if(enforceAdapter!=null){
                    enforceAdapter.getFilter().filter(query);

                }

                return false;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()

        {
            @Override
            public boolean onClose () {

                enforceAdapter.getFilter().filter("");
                // searchView.setVisibility(View.GONE);

                return false;
            }
        });

        getSiteManagersList();

    }
    public void getSiteManagersList(){
        final ProgressDialog pDialog=new ProgressDialog(PrecinctsListActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(PrecinctsListActivity.this);
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

                                    if(object.has("precincts_list")){
                                        JSONArray precinctsArray=object.getJSONArray("precincts_list");
                                        precinctsModelList=new ArrayList<>();
                                        for(int i=0;i<precinctsArray.length();i++){
                                            precinctsModel=new PrecinctsModel();
                                            JSONObject siteManagerObject=precinctsArray.getJSONObject(i);
                                            precinctsModel.setId(siteManagerObject.getString("id"));
                                            precinctsModel.setPrecinctID(siteManagerObject.getString("precinct_id"));
                                            precinctsModel.setPrecinctName(siteManagerObject.getString("precinct_name"));
                                            precinctsModel.setStreetName(siteManagerObject.getString("street_name"));
                                            precinctsModel.setCity(siteManagerObject.getString("city"));
                                            precinctsModel.setState(siteManagerObject.getString("state"));


                                            precinctsModelList.add(precinctsModel);
                                        }

                                         enforceAdapter = new PrecinctsAdapter(PrecinctsListActivity.this, precinctsModelList);
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
                MyData.put("method", "precinctslist");

                MyData.put("token", token);
                MyData.put("supervisor_id", supervisorID);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PrecinctsListActivity.this);
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


    public class PrecinctsAdapter extends RecyclerView.Adapter<PrecinctsAdapter.MyViewHolder> implements Filterable {

        private List<PrecinctsModel> siteManagerModelList;
        private List<PrecinctsModel> filteredSiteManagerModelList;

        Context context;
        int row_index=-1;

        public PrecinctsAdapter(Context context, List<PrecinctsModel> siteManagerModelList){
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList =siteManagerModelList;

        }
        @NonNull
        @Override
        public PrecinctsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_site_manager_row, parent, false);

            return new PrecinctsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final PrecinctsAdapter.MyViewHolder holder, int position) {
            final PrecinctsModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_site_manager_name.setText(siteManagerModel.getPrecinctName());
            holder.tv_login_id.setText(siteManagerModel.getStreetName());

            holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i =new Intent(context,ParkingMarshalListActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("Precincts",siteManagerModel);
                    startActivity(i);
                }
            });




        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_site_manager_name,tv_login_id;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_site_manager_name = (TextView) view.findViewById(R.id.text_site_manager_name);
                tv_login_id = (TextView) view.findViewById(R.id.text_login_id);

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
                        List<PrecinctsModel> filteredList = new ArrayList<>();
                        for (PrecinctsModel row : siteManagerModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if ( row.getPrecinctName().toLowerCase().contains(charSequence)|| row.getStreetName().toLowerCase().contains(charString.toLowerCase())) {
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
                    filteredSiteManagerModelList = (ArrayList<PrecinctsModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }


}