package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
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
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.smartpark.smartpark.async.AsyncBluetoothEscPosPrint;
import com.smartpark.smartpark.async.AsyncEscPosPrint;
import com.smartpark.smartpark.async.AsyncEscPosPrinter;

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
import java.util.UUID;

public class InvoiceActivity extends AppCompatActivity {

    LinearLayout layout_home, layout_profile, layout_invoice, layout_logout;
    ImageView iv_scan;
    RecyclerView recyclerView;
    List<ParkingDetailsModel> parkingDetailsModelList = new ArrayList<>();
    ParkingDetailsModel parkingDetailsModel = new ParkingDetailsModel();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String token;
    String parkingMarshalID;
    String precinctID;
    String msg;
    Bitmap bitmap;
    WebView webView;
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    TextView mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;
    private BluetoothConnection selectedDevice;
    String id = "";
    String amount = "";
    String baynumber = "";
    String dateformat = "";
    String bulawayo = "";
    String bulawayo1 = "";
    String precinctt = "";
    String amount_collect_USD = "";
    String marshal = "";
    String time_vechicleparked = "";
    String endTime = "";
    String endDateFormat = "";
    String startTime = "";
    String vihicleNod = "";
    String parkingfess = "";
    String vat_amount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        layout_home = findViewById(R.id.layout_home);
        layout_profile = findViewById(R.id.layout_profile);
        layout_invoice = findViewById(R.id.layout_invoice);
        layout_logout = findViewById(R.id.layout_logout);
        iv_scan = findViewById(R.id.image_scan);
        recyclerView = findViewById(R.id.recyclerview_park_car_list);

        preferences = getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token = preferences.getString(StringConstants.prefToken, "");
        parkingMarshalID = preferences.getString(StringConstants.prefParkingMarshalID, "");
        precinctID = preferences.getString(StringConstants.prefPrecinctID, "");
        webView = findViewById(R.id.web_view);

        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), HomePageActivity.class);
                startActivity(i);

            }
        });
        layout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), PersonalInfoActivity.class);
                startActivity(i);
            }
        });
        layout_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), InvoiceActivity.class);
                startActivity(i);
                finish();
            }
        });
        layout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                {
                    final Dialog dialog = new Dialog(InvoiceActivity.this, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Setting dialogview
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);

                    dialog.setContentView(R.layout.layout_logout_dialog);

                    TextView tv_no = dialog.findViewById(R.id.text_no);
                    TextView tv_yes = dialog.findViewById(R.id.text_yes);
                    EditText et_logout_pin = dialog.findViewById(R.id.edittext_logout_pin);
                    tv_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog.dismiss();
                        }
                    });
                    tv_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (!et_logout_pin.getText().toString().trim().isEmpty()) {

                                dialog.dismiss();
                                logout(et_logout_pin.getText().toString().trim());


                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter logout pin", Toast.LENGTH_SHORT).show();
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

                Intent i = new Intent(getApplicationContext(), ParCarActivity.class);
                startActivity(i);

            }
        });

        getRecentActivities();
    }


    public void logout(String logoutPin) {
        final ProgressDialog pDialog = new ProgressDialog(InvoiceActivity.this);
        pDialog.setMessage("Submitting..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(InvoiceActivity.this);
        requestQueue.getCache().clear();

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, StringConstants.mainUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Response", response);

                try {

                    JSONObject jsonObject = new JSONObject(response.trim());
                    if (jsonObject.has("response")) {

                        JSONArray responseArray = jsonObject.getJSONArray("response");

                        if (responseArray.length() > 0) {
                            JSONObject object = responseArray.getJSONObject(0);
                            if (object.has("status")) {
                                String status = object.getString("status");
                                if (status.equals("success")) {
                                    editor.clear();
                                    editor.apply();
                                    editor.commit();
                                    Intent i = new Intent(getApplicationContext(), SiteManagerLoginActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    showAlertDialog(object.getString("message"));
                                }
                            } else {
                                showAlertDialog(object.getString("message"));
                            }
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                pDialog.dismiss();
                String errorMessage = StringConstants.ErrorMessage(error);

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

    public void getRecentActivities() {
        final ProgressDialog pDialog = new ProgressDialog(InvoiceActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(InvoiceActivity.this);
        requestQueue.getCache().clear();

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, StringConstants.mainUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Response", response);

                try {

                    JSONObject jsonObject = new JSONObject(response.trim());
                    if (jsonObject.has("response")) {

                        JSONArray responseArray = jsonObject.getJSONArray("response");

                        if (responseArray.length() > 0) {
                            JSONObject object = responseArray.getJSONObject(0);
                            if (object.has("status")) {
                                String status = object.getString("status");
                                if (status.equals("success")) {

                                    if (object.has("invoice_list")) {
                                        JSONArray recentArray = object.getJSONArray("invoice_list");
                                        for (int i = 0; i < recentArray.length(); i++) {

                                            JSONObject jsonObject1 = recentArray.getJSONObject(i);
                                            parkingDetailsModel = new ParkingDetailsModel();
                                            parkingDetailsModel.setId(jsonObject1.getString("id"));
                                            parkingDetailsModel.setAmount(jsonObject1.getString("amount"));
                                            parkingDetailsModel.setDateTime(jsonObject1.getString("datetime"));
                                            parkingDetailsModel.setDateFormat(jsonObject1.getString("dateformat"));
                                            parkingDetailsModel.setTimeFormat(jsonObject1.getString("timeformat"));
                                            parkingDetailsModel.setPlateNo(jsonObject1.getString("plateno"));
                                            parkingDetailsModel.setBayNo(jsonObject1.getString("bayno"));
                                            parkingDetailsModel.setStatus(jsonObject1.getString("status"));
                                            parkingDetailsModel.setStartdatetimeformat(jsonObject1.getString("startdatetimeformat"));
                                            parkingDetailsModel.setEndtimeformat(jsonObject1.getString("endtimeformat"));
                                            parkingDetailsModel.setEnddateformat(jsonObject1.getString("enddateformat"));
                                            parkingDetailsModel.setCountry_code(jsonObject1.getString("country_code"));
                                            parkingDetailsModel.setHours(jsonObject1.getString("hours"));
                                            parkingDetailsModel.setAmount_collect_USD(jsonObject1.getString("amount_collect_USD"));
                                            parkingDetailsModel.setCurrency(jsonObject1.getString("currency"));
                                            parkingDetailsModel.setAmount_owned(jsonObject1.getString("amount_owned"));
                                            parkingDetailsModel.setParking_marshal_name(jsonObject1.getString("parking_marshal_name"));
                                            parkingDetailsModel.setPrecinct_name(jsonObject1.getString("precinct_name"));
                                            parkingDetailsModel.setSupervisor_name(jsonObject1.getString("supervisor_name"));

                                            parkingDetailsModelList.add(parkingDetailsModel);
                                        }

                                        SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(InvoiceActivity.this, parkingDetailsModelList);
                                        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                        recyclerView.setLayoutManager(horizontalLayoutManager1);
                                        recyclerView.setAdapter(enforceAdapter);

                                    }


                                } else {
                                    showAlertDialog(object.getString("message"));
                                }
                            } else {
                                showAlertDialog(object.getString("message"));
                            }
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                pDialog.dismiss();
                String errorMessage = StringConstants.ErrorMessage(error);

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("method", "invoicelist");

                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void showAlertDialog(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InvoiceActivity.this);
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
        int row_index = -1;

        public SiteManagerAdapter(Context context, List<ParkingDetailsModel> siteManagerModelList) {
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList = siteManagerModelList;

        }

        @NonNull
        @Override
        public SiteManagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_invoice_list_row, parent, false);

            return new SiteManagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SiteManagerAdapter.MyViewHolder holder, int position) {
            final ParkingDetailsModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_amount.setText(siteManagerModel.getAmount());
            // holder.tv_status.setText(siteManagerModel.getStatus());
            holder.tv_plate_no.setText(siteManagerModel.getPlateNo());
            holder.tv_bay_no.setText(siteManagerModel.getBayNo());


            holder.tv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id = siteManagerModel.getId();
                    amount = siteManagerModel.getAmount();
                    baynumber = siteManagerModel.getBayNo();
                    dateformat = siteManagerModel.getDateFormat();
                    bulawayo = siteManagerModel.getAmount();
                    vihicleNod = siteManagerModel.getPlateNo();
                    endTime = siteManagerModel.getEndtimeformat();
                    endTime = siteManagerModel.getEndtimeformat();
                    startTime = siteManagerModel.getStartdatetimeformat();
                    endDateFormat = siteManagerModel.getEnddateformat();
                    amount_collect_USD = siteManagerModel.getAmount_collect_USD();
                    precinctt = siteManagerModel.getPrecinct_name();
                    marshal = siteManagerModel.getParking_marshal_name();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            printBluetooth();
                        }
                    }, 2000);


                    /*Intent i = new Intent(context, PrinterActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("InvoiceID", siteManagerModel.getId());
                    context.startActivity(i);*/

                }
            });


        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_amount, tv_plate_no, tv_bay_no, tv_status;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_amount = (TextView) view.findViewById(R.id.text_amount);
                tv_plate_no = (TextView) view.findViewById(R.id.text_plate_no);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
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
                        filteredSiteManagerModelList = siteManagerModelList;
                    } else {
                        List<ParkingDetailsModel> filteredList = new ArrayList<>();
                        for (ParkingDetailsModel row : siteManagerModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getBayNo().contains(charSequence) || row.getPlateNo().toLowerCase().contains(charString.toLowerCase())) {
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

    public void backPressed(View view) {

        onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case InvoiceActivity.PERMISSION_BLUETOOTH:
                case InvoiceActivity.PERMISSION_BLUETOOTH_ADMIN:
                case InvoiceActivity.PERMISSION_BLUETOOTH_CONNECT:
                case InvoiceActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.printBluetooth();
                    break;
            }
        }
    }

    public void printBluetooth() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, InvoiceActivity.PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, InvoiceActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, InvoiceActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, InvoiceActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            new AsyncBluetoothEscPosPrint(
                    this,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                            Intent intent=new Intent(InvoiceActivity.this,SuccessActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            )
                    .execute(this.getAsyncEscPosPrinter(selectedDevice));
        }
    }

    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);
        return printer.addTextToPrint(
                "[L]\n" +
                        "[C]<font size='big'>SMART PARK</font>\n" +
                        "[L]\n" +
                        "[C]<b> PAY & DISPLAY TICKET </b>\n" +
                        "[C]\n" +
                        "[C]<b>* DISPLAY ON DASHBOARD *</b> \n" +
                        "[L]\n" +
                        "[L]<b>TICKET VALID ON THIS BAY ONLY</b> \n" +
                        "[L]\n" +
                        "[C] Bulawayo\n" +
                        "[L]\n" +
                        "[L]Tax Invoice:"+id+"[R] VAT #10080261\n" +
                        "[L]Tendy Three Investments\n" +
                        "[L]Precinct:"+precinctt+"\n" +
                        "[L]Marshal:"+marshal+" \n" +
                        "[L]Time Vehicle Parked:"+startTime+"\n" +
                        "[L]\n" +
                        "[C]<b>PARK TIME PAID UNTIL</b>\n" +
                        "[L]\n" +
                        "[C]<font size='big'>"+endTime+"</font>\n" +
                        "[C]<b>"+endDateFormat+"</b>\n" +
                        "[C]<b>VEHICLE:"+vihicleNod+"</b>\n" +
                        "[C]<b>BAY NUMBER:"+baynumber+"</b>\n" +
                        "[L]\n" +
                        "[L]PARKING FEE PAID[R]"+amount_collect_USD+"\n" +
                        "[L](including VAT) \n" +
                        "[R] -------\n" +
                        "[L]VAT  [R] US$ 0.00\n" +
                        "[L]DATE PURCHASED:"+startTime+"\n" +
                        "[L]\n" +
                        "[L] The Municipality of this town  or any system participants are  not in any way liable  for damage or loss. Any enquiries Complaints or requests can be lodged to \n" +
                        "[L]Tendy Three Investments(Pvt) Ltd \n" +
                        "[C]<b>Contact Number: 029 227 0048</b>\n" +
                        "[L]\n" +
                        "[C]Please check the ticket and change before leaving  the marshal as mistakes rectified later");
    }
}