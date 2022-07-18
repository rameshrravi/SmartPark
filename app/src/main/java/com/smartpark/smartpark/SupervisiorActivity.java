package com.smartpark.smartpark;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SupervisiorActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<SiteManagerModel> siteManagerModelList=new ArrayList<>();
    SiteManagerModel siteManagerModel=new SiteManagerModel();
    String token;
    SharedPreferences preferences;
    SharedPreferences.Editor editor ;
    SearchView searchView;
    SiteManagerAdapter  enforceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisior);

        preferences =getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token = preferences.getString(StringConstants.prefSuperVisorToken,"");

        recyclerView = findViewById(R.id.recyclerview_site_managers);

        searchView=(SearchView)findViewById(R.id.search_view);

        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Supervisor Name");


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
        final ProgressDialog pDialog=new ProgressDialog(SupervisiorActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(SupervisiorActivity.this);
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

                                    if(object.has("supervisor_list")){
                                        siteManagerModelList=new ArrayList<>();
                                        JSONArray siteManagersArray=object.getJSONArray("supervisor_list");
                                        for(int i=0;i<siteManagersArray.length();i++){
                                            siteManagerModel=new SiteManagerModel();
                                            JSONObject siteManagerObject=siteManagersArray.getJSONObject(i);
                                            siteManagerModel.setFirstName(siteManagerObject.getString("first_name"));
                                            siteManagerModel.setLastName(siteManagerObject.getString("last_name"));
                                            siteManagerModel.setLoginID(siteManagerObject.getString("login_id"));
                                            siteManagerModel.setPassword(siteManagerObject.getString("password"));
                                            siteManagerModel.setPhoneNo(siteManagerObject.getString("phoneno"));
                                            siteManagerModel.setLogoutPin(siteManagerObject.getString("logoutpin"));

                                            siteManagerModelList.add(siteManagerModel);
                                        }

                                          enforceAdapter = new SiteManagerAdapter(SupervisiorActivity.this, siteManagerModelList);
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
                MyData.put("method", "get_supervisor_manager");

                MyData.put("token", token);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public void showAlertDialog(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SupervisiorActivity.this);
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

        private List<SiteManagerModel> siteManagerModelList;
        private List<SiteManagerModel> filteredSiteManagerModelList;

        Context context;
        int row_index=-1;

        public SiteManagerAdapter(Context context, List<SiteManagerModel> siteManagerModelList){
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList =siteManagerModelList;

        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_site_manager_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final SiteManagerModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_site_manager_name.setText(siteManagerModel.getFirstName()+" "+siteManagerModel.getLastName());
            holder.tv_login_id.setText(siteManagerModel.getLoginID());

            holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i =new Intent(context,SupervisorLoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("Site",siteManagerModel);
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
                        List<SiteManagerModel> filteredList = new ArrayList<>();
                        for (SiteManagerModel row : siteManagerModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if ( row.getFirstName().toLowerCase().contains(charSequence)|| row.getLastName().toLowerCase().contains(charString.toLowerCase())) {
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
                    filteredSiteManagerModelList = (ArrayList<SiteManagerModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }


}