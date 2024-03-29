package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smartpark.smartpark.model.SearchViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ParCarActivity extends AppCompatActivity {

    LinearLayout layoutCarPark;
    EditText et_number_plate, et_parking_bay_number, et_country_code, et_phone_number, et_email_id;
    TextView tv_submit;
    EditText et_number_plate1, et_parking_bay_number1, et_country_code1, et_phone_number1, et_email_id1, et_start_date, et_end_date, et_amount_collected, et_currency, et_amount_owed;
    TextView tv_update, text_unpaid_car_park;
    String s_number_plate, s_parking_bay_number, s_country_code, s_phone_number, s_email_id;
    String s_number_plate1, s_parking_bay_number1, s_country_code1, s_phone_number1, s_email_id1, s_start_date, s_end_date, s_amount_collected, s_currency = "USD", s_amount_owed;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String token = "", parkingMarshalID = "", precinctID, supervisorID, type = "";
    TextView tv_new, tv_view, text_search, search_viewbtn;
    LinearLayout layoutViewCarPark, layoutUpdateCarPark;
    RecyclerView recyclerView;
    List<ParkingDetailsModel> parkingDetailsModelList = new ArrayList<>();
    ParkingDetailsModel parkingDetailsModel = new ParkingDetailsModel();
    String car_parking_id = "";
    LinearLayout layout_home, layout_profile, layout_invoice, layout_logout;
    ImageView iv_scan;
    ListPopupWindow listPopupWindowCashOptions;
    List<String> cashOptionsList = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    private int hr;
    private int min;
    Calendar calendar;
    String dateToStr, dateToStr1;
    String parkingFee = "1";
    String zimCurrency = "", randsCurrency = "", botswanaCurrency = "";
    ParkingDetailsModel parkingDetailsModel1 = new ParkingDetailsModel();
    String s_hours = "";
    String status = "";
    RadioButton radioButton;
    RadioButton radiopostpaid1;
    RadioButton radioprepaid1, radioprepaid, radiocash, radiortgs;
    RadioGroup radioGroup;
    RadioGroup radioGroup1, radioGroupPaymentTpe;
    LinearLayout linear_hours;
    ImageView pluse, minus;
    TextView txt_count, valid_until, valid_until1;
    int convert = 0;
    LinearLayout linearPaid, linearunpaid, paymentTypeLinear, linearpaymentMode, searchLinearview,
            linearCurrentBooking, linearNotBooking, linearUnpaidBooking;
    TextView text_paid, text_paymenttype, txt_currentbooking, txt_notbookigdetail, txt_unpaidbookigdetail;
    RecyclerView current_booking_recyclerview, notpaid_booking_recyclerview, unpaid_booking_recyclerview;
    List<SearchViewModel> currentList = new ArrayList<>();
    List<SearchViewModel> notpaidList = new ArrayList<>();
    List<SearchViewModel> unPaidList = new ArrayList<>();
    EditText edtPlateNo;
    RelativeLayout relativelauout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_car);

        text_paid = findViewById(R.id.text_paid);
        linearPaid = findViewById(R.id.linearPaid);
        linearunpaid = findViewById(R.id.linearunpaid);
        text_paymenttype = findViewById(R.id.text_paymenttype);
        linearpaymentMode = findViewById(R.id.linearpaymentMode);
        paymentTypeLinear = findViewById(R.id.paymentTypeLinear);
        layoutCarPark = findViewById(R.id.layout_add_park_car);
        layoutViewCarPark = findViewById(R.id.layout_view_car_park);
        layoutUpdateCarPark = findViewById(R.id.layout_update_car_park);
        et_number_plate = findViewById(R.id.edittext_number_plate);
        et_parking_bay_number = findViewById(R.id.edittext_parking_bay_no);
        et_country_code = findViewById(R.id.edittext_country_code);
        et_phone_number = findViewById(R.id.edittext_phone_number);
        et_email_id = findViewById(R.id.edittext_email_id);
        et_number_plate1 = findViewById(R.id.edittext_number_plate1);
        et_parking_bay_number1 = findViewById(R.id.edittext_parking_bay_no1);
        et_country_code1 = findViewById(R.id.edittext_country_code1);
        et_phone_number1 = findViewById(R.id.edittext_phone_number1);
        et_email_id1 = findViewById(R.id.edittext_email_id1);
        et_start_date = findViewById(R.id.edittext_start_date);
        et_end_date = findViewById(R.id.edittext_end_date);
        et_amount_collected = findViewById(R.id.edittext_amount_to_be_collected);
        et_currency = findViewById(R.id.edittext_currency);
        et_amount_owed = findViewById(R.id.edittext_amount_owed);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroupPaymentTpe = (RadioGroup) findViewById(R.id.radioGroupPaymentTpe);
        radiopostpaid1 = (RadioButton) findViewById(R.id.radiopostpaid1);
        radioprepaid1 = (RadioButton) findViewById(R.id.radioprepaid1);
        radioprepaid = (RadioButton) findViewById(R.id.radioprepaid);

        radiocash = (RadioButton) findViewById(R.id.radiocash);
        radiortgs = (RadioButton) findViewById(R.id.radiortgs);
        linear_hours = (LinearLayout) findViewById(R.id.linear_hours);
        pluse = (ImageView) findViewById(R.id.pluse);
        minus = (ImageView) findViewById(R.id.minus);
        txt_count = (TextView) findViewById(R.id.txt_count);
        valid_until = (TextView) findViewById(R.id.valid_until);
        valid_until1 = (TextView) findViewById(R.id.valid_until1);

        tv_submit = findViewById(R.id.text_submit_car_park);
        tv_update = findViewById(R.id.text_update_car_park);
        text_unpaid_car_park = findViewById(R.id.text_unpaid_car_park);
        recyclerView = findViewById(R.id.recyclerview_park_car_list);

        tv_new = findViewById(R.id.text_new);
        tv_view = findViewById(R.id.text_view);
        text_search = findViewById(R.id.text_search);

        layout_home = findViewById(R.id.layout_home);
        layout_profile = findViewById(R.id.layout_profile);
        layout_invoice = findViewById(R.id.layout_invoice);
        layout_logout = findViewById(R.id.layout_logout);
        iv_scan = findViewById(R.id.image_scan);
        searchLinearview = findViewById(R.id.searchLinearview);
        linearCurrentBooking = findViewById(R.id.linearCurrentBooking);
        linearNotBooking = findViewById(R.id.linearNotBooking);
        linearUnpaidBooking = findViewById(R.id.linearUnpaidBooking);
        txt_currentbooking = findViewById(R.id.txt_currentbooking);
        txt_notbookigdetail = findViewById(R.id.txt_notbookigdetail);
        txt_unpaidbookigdetail = findViewById(R.id.txt_unpaidbookigdetail);
        current_booking_recyclerview = findViewById(R.id.current_booking_recyclerview);
        notpaid_booking_recyclerview = findViewById(R.id.notpaid_booking_recyclerview);
        unpaid_booking_recyclerview = findViewById(R.id.unpaid_booking_recyclerview);
        search_viewbtn = findViewById(R.id.search_view);
        edtPlateNo = findViewById(R.id.edtPlateNo);
        relativelauout = findViewById(R.id.relativelauout);


        listPopupWindowCashOptions = new ListPopupWindow(this);

        parkingDetailsModel1 = (ParkingDetailsModel) getIntent().getSerializableExtra("ParkingDetail");
        search_viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtPlateNo.getText().length() > 0) {
                    currentList.clear();
                    unPaidList.clear();
                    notpaidList.clear();
                    layoutUpdateCarPark.setVisibility(View.GONE);
                    linearCurrentBooking.setVisibility(View.GONE);
                    linearNotBooking.setVisibility(View.GONE);
                    linearUnpaidBooking.setVisibility(View.GONE);
                    getsearchparking();
                } else {
                    showAlertDialog("Enter Valid Number Plate");
                }

            }
        });
        getPriceDetail();
        radioprepaid1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                linear_hours.setVisibility(View.GONE);
                et_end_date.setVisibility(View.VISIBLE);

            }
        });
        radiopostpaid1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                linear_hours.setVisibility(View.VISIBLE);
                et_end_date.setVisibility(View.GONE);

            }
        });

        pluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String count = txt_count.getText().toString();
                int cont = Integer.valueOf(count);
                if (cont >= 1 || cont >= 24) {
                    cont = cont + 1;
                    txt_count.setText(String.valueOf(cont));
                    //convert=Integer.valueOf(convrtstr);
                    s_hours = String.valueOf(cont);
                    int finalcont = convert + cont;
                    valid_until.setText(String.valueOf(finalcont));
                    et_amount_collected.setText(String.valueOf(cont * Double.valueOf(parkingFee)));
                    et_amount_owed.setText(String.valueOf(cont * Double.valueOf(parkingFee)));
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String count = txt_count.getText().toString();
                int cont = Integer.valueOf(count);
                if (cont > 1 || cont > 24) {
                    cont = cont - 1;
                    txt_count.setText(String.valueOf(cont));
                    txt_count.setText(String.valueOf(cont));
                    //convert=Integer.valueOf(convrtstr);
                    int finalcont = convert + cont;
                    s_hours = String.valueOf(cont);
                    et_amount_collected.setText(String.valueOf(cont * Double.valueOf(parkingFee)));
                    et_amount_owed.setText(String.valueOf(cont * Double.valueOf(parkingFee)));
                    valid_until.setText(String.valueOf(finalcont));
                }
            }
        });
        cashOptionsList.add("USD");
        cashOptionsList.add("Zim $");
        cashOptionsList.add("Rands");
        cashOptionsList.add("Botswana Pula");

        listPopupWindowCashOptions.setAdapter(new ArrayAdapter(
                ParCarActivity.this,
                R.layout.layout_item_list, cashOptionsList));
        listPopupWindowCashOptions.setAnchorView(et_currency);


        et_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listPopupWindowCashOptions.show();
            }
        });
        listPopupWindowCashOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_currency.setText(cashOptionsList.get(position));
                s_currency = et_currency.getText().toString().trim();


                s_amount_collected = et_amount_collected.getText().toString().trim();

                if (s_currency.equals("Zim $")) {
                    s_currency = "Zim";
                }

                if (s_currency.equals("Zim")) {
                    Float amountOwed = Float.valueOf(s_amount_collected) * Float.valueOf(zimCurrency);
                    et_amount_owed.setText(String.valueOf(amountOwed));
                }
                if (s_currency.equals("Rands")) {
                    Float amountOwed = Float.valueOf(s_amount_collected) * Float.valueOf(randsCurrency);
                    et_amount_owed.setText(String.valueOf(amountOwed));
                }
                if (s_currency.equals("Botswana Pula")) {
                    Float amountOwed = Float.valueOf(s_amount_collected) * Float.valueOf(botswanaCurrency);
                    et_amount_owed.setText(String.valueOf(amountOwed));
                }
                if (s_currency.equals("USD")) {
                    et_amount_owed.setText(s_amount_collected);
                }

                listPopupWindowCashOptions.dismiss();
            }
        });


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hr = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        et_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = new DatePickerDialog(ParCarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                //  date.setText(day + "/" + (month + 1) + "/" + year);
                                //   dateToStr=String.valueOf(day)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year);
                                if (day < 10 && month + 1 < 10) {
                                    dateToStr = "0" + String.valueOf(day) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_start_date = String.valueOf(year) + "-" + "0" + String.valueOf(month + 1) + "-" + "0" + String.valueOf(day);
                                } else if (day < 10 && month + 1 > 10) {
                                    dateToStr = "0" + String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_start_date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + "0" + String.valueOf(day);
                                } else if (day > 10 && month + 1 < 10) {
                                    dateToStr = String.valueOf(day) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_start_date = String.valueOf(year) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                                } else {
                                    s_start_date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                                    dateToStr = String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                }
                                createdDialog().show();

                            }
                        }, year, month, dayOfMonth);
                // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();


            }
        });
        et_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = new DatePickerDialog(ParCarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                //  date.setText(day + "/" + (month + 1) + "/" + year);
                                //   dateToStr=String.valueOf(day)+"-"+String.valueOf(month+1)+"-"+String.valueOf(year);
                                if (day < 10 && month + 1 < 10) {
                                    dateToStr1 = "0" + String.valueOf(day) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_end_date = String.valueOf(year) + "-" + "0" + String.valueOf(month + 1) + "-" + "0" + String.valueOf(day);
                                } else if (day < 10 && month + 1 > 10) {
                                    dateToStr1 = "0" + String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_end_date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + "0" + String.valueOf(day);
                                } else if (day > 10 && month + 1 < 10) {
                                    dateToStr1 = String.valueOf(day) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                    s_end_date = String.valueOf(year) + "-" + "0" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                                } else {
                                    s_end_date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                                    dateToStr1 = String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
                                }
                                createdDialog1().show();

                            }
                        }, year, month, dayOfMonth);
                // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();


            }
        });

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
                    final Dialog dialog = new Dialog(ParCarActivity.this, R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    // Setting dialogview
                    Window window = dialog.getWindow();
                    window.setGravity(Gravity.CENTER);

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

        preferences = getSharedPreferences(StringConstants.prefMySharedPreference, Context.MODE_PRIVATE);
        editor = preferences.edit();

        token = preferences.getString(StringConstants.prefToken, "");
        type = preferences.getString(StringConstants.prefType, "");
        if (type.equals("parkingmarshal")) {
            parkingMarshalID = preferences.getString(StringConstants.prefParkingMarshalID, "");
        } else if (type.equals("supervisor")) {
            parkingMarshalID = preferences.getString(StringConstants.prefUserID, "");
        }

        precinctID = preferences.getString(StringConstants.prefPrecinctID, "");
        supervisorID = preferences.getString(StringConstants.prefUserID, "");


        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_number_plate = et_number_plate.getText().toString().trim();
                s_parking_bay_number = et_parking_bay_number.getText().toString().trim();
                s_country_code = et_country_code.getText().toString().trim();
                s_phone_number = et_phone_number.getText().toString().trim();
                s_email_id = et_email_id.getText().toString().trim();

                if (!s_number_plate.isEmpty()) {
                    if (!s_parking_bay_number.isEmpty()) {

                        addParkCar();

                    } else {
                        showAlertDialog("Please enter parking bay number");
                    }
                } else {
                    showAlertDialog("Please enter number plate");
                }

            }
        });
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_number_plate1 = et_number_plate1.getText().toString().trim();
                s_parking_bay_number1 = et_parking_bay_number1.getText().toString().trim();
                s_country_code1 = et_country_code1.getText().toString().trim();
                s_phone_number1 = et_phone_number1.getText().toString().trim();
                s_email_id1 = et_email_id1.getText().toString().trim();

                s_amount_collected = et_amount_collected.getText().toString().trim();
                s_amount_owed = et_amount_owed.getText().toString().trim();

                if (s_currency.equals("Zim $")) {
                    s_currency = "Zim";
                }


                if (!s_number_plate1.isEmpty()) {
                    if (!s_parking_bay_number1.isEmpty()) {
                        showAlertDialogfornotpaid("Do you want to confirm ?", "paid");

                    } else {
                        showAlertDialog("Please enter parking bay number");
                    }
                } else {
                    showAlertDialog("Please enter number plate");
                }

            }
        });
        text_unpaid_car_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_number_plate1 = et_number_plate1.getText().toString().trim();
                s_parking_bay_number1 = et_parking_bay_number1.getText().toString().trim();
                s_country_code1 = et_country_code1.getText().toString().trim();
                s_phone_number1 = et_phone_number1.getText().toString().trim();
                s_email_id1 = et_email_id1.getText().toString().trim();

                s_amount_collected = et_amount_collected.getText().toString().trim();
                s_amount_owed = et_amount_owed.getText().toString().trim();

                if (s_currency.equals("Zim $")) {
                    s_currency = "Zim";
                }


                if (!s_number_plate1.isEmpty()) {
                    if (!s_parking_bay_number1.isEmpty()) {

                        showAlertDialogfornotpaid("Do you want to confirm ?", "unpaid");

                    } else {
                        showAlertDialog("Please enter parking bay number");
                    }
                } else {
                    showAlertDialog("Please enter number plate");
                }

            }
        });


        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativelauout.setVisibility(View.VISIBLE);
                searchLinearview.setVisibility(View.GONE);
                tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                tv_view.setTextColor(getResources().getColor(R.color.black));
                tv_new.setTextColor(getResources().getColor(R.color.white));
                layoutCarPark.setVisibility(View.VISIBLE);
                layoutViewCarPark.setVisibility(View.GONE);
                layoutUpdateCarPark.setVisibility(View.GONE);
                text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                text_search.setTextColor(getResources().getColor(R.color.black));

            }
        });
        tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativelauout.setVisibility(View.VISIBLE);
                searchLinearview.setVisibility(View.GONE);
                tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                tv_view.setTextColor(getResources().getColor(R.color.white));
                tv_new.setTextColor(getResources().getColor(R.color.black));
                layoutCarPark.setVisibility(View.GONE);
                layoutViewCarPark.setVisibility(View.VISIBLE);
                layoutUpdateCarPark.setVisibility(View.GONE);
                text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                text_search.setTextColor(getResources().getColor(R.color.black));

            }
        });
        text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativelauout.setVisibility(View.GONE);
                searchLinearview.setVisibility(View.VISIBLE);
                tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                text_search.setTextColor(getResources().getColor(R.color.white));
                tv_new.setTextColor(getResources().getColor(R.color.black));
                tv_view.setTextColor(getResources().getColor(R.color.black));
                layoutCarPark.setVisibility(View.GONE);
                layoutViewCarPark.setVisibility(View.VISIBLE);
                layoutUpdateCarPark.setVisibility(View.GONE);

            }
        });

        getParkingDetails();
        getConversationDetail();


    }

    public long get_count_of_days(String Created_date_String, String Expire_date_String) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long minutes = 0;
        long hours = 1;
        Date Created_convertedDate = null, Expire_CovertedDate = null, todayWithZeroTime = null;
        try {
            Created_convertedDate = dateFormat.parse(Created_date_String);
            Expire_CovertedDate = dateFormat.parse(Expire_date_String);

            long diff = Expire_CovertedDate.getTime() - Created_convertedDate.getTime();
//get time in seconds
            long seconds = diff / 1000;
//and so on
            minutes = seconds / 60;

            hours = minutes / 60;

            if (hours < 1) {
                hours = 1;
            }

            s_hours = String.valueOf(hours);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int c_year = 0, c_month = 0, c_day = 0;

        if (Created_convertedDate.after(todayWithZeroTime)) {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(Created_convertedDate);
            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar c_cal = Calendar.getInstance();
            c_cal.setTime(todayWithZeroTime);
            c_year = c_cal.get(Calendar.YEAR);
            c_month = c_cal.get(Calendar.MONTH);
            c_day = c_cal.get(Calendar.DAY_OF_MONTH);
        }


/*Calendar today_cal = Calendar.getInstance();
int today_year = today_cal.get(Calendar.YEAR);
int today = today_cal.get(Calendar.MONTH);
int today_day = today_cal.get(Calendar.DAY_OF_MONTH);
*/
        Calendar e_cal = Calendar.getInstance();
        e_cal.setTime(Expire_CovertedDate);

        int e_year = e_cal.get(Calendar.YEAR);
        int e_month = e_cal.get(Calendar.MONTH);
        int e_day = e_cal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(c_year, c_month, c_day);
        date2.clear();
        date2.set(e_year, e_month, e_day);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (60 * 1000);

        return hours;
    }

    protected Dialog createdDialog() {
        return new TimePickerDialog(ParCarActivity.this, timePickerListener, hr, min, false);
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            s_start_date = s_start_date + " " + String.valueOf(hr) + ":" + String.valueOf(minutes) + ":00";
            updateTime(hr, min);
        }
    };

    private void updateTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();

        et_start_date.setText(dateToStr + " " + aTime);
    }

    protected Dialog createdDialog1() {
        return new TimePickerDialog(ParCarActivity.this, timePickerListener1, hr, min, false);
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            s_end_date = s_end_date + " " + String.valueOf(hr) + ":" + String.valueOf(minutes) + ":00";
            int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
            System.out.println("dateDifference: " + dateDifference);

            if (dateDifference == 0) {

                et_amount_collected.setText(parkingFee);
                et_amount_owed.setText(parkingFee);

            } else {
                et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
            }
            updateTime1(hr, min);
        }
    };

    private void updateTime1(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();

        et_end_date.setText(dateToStr1 + " " + aTime);
    }

    public void showAlertDialog(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ParCarActivity.this);
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

    public void addParkCar() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Adding..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

                                    Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), ParCarActivity.class);
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
                MyData.put("method", "carparking_add");
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("enterby_type", type);
                MyData.put("precinct_id", precinctID);
                MyData.put("supervisor_id", supervisorID);
                MyData.put("bay_no", s_parking_bay_number);
                MyData.put("number_plate", s_number_plate);
                MyData.put("country_code", s_country_code);
                MyData.put("phoneno", s_phone_number);
                MyData.put("email", s_email_id);
                MyData.put("token", token);
                MyData.put("type", radioButton.getText().toString().toLowerCase());
                MyData.put("datetime", currentDate + " " + currentTime);
                Log.i("carparking_add", MyData.toString());
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void getPriceDetail() {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

                                    parkingFee = object.getString("parking_fees");

                                    if (parkingDetailsModel1 != null) {
                                        tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                        tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                                        tv_view.setTextColor(getResources().getColor(R.color.white));
                                        tv_new.setTextColor(getResources().getColor(R.color.black));
                                        layoutCarPark.setVisibility(View.GONE);
                                        layoutViewCarPark.setVisibility(View.GONE);
                                        layoutUpdateCarPark.setVisibility(View.VISIBLE);
                                        relativelauout.setVisibility(View.VISIBLE);
                                        searchLinearview.setVisibility(View.GONE);
                                        text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                        text_search.setTextColor(getResources().getColor(R.color.black));


                                        car_parking_id = parkingDetailsModel1.getId();
                                        et_number_plate1.setText(parkingDetailsModel1.getPlateNo());
                                        et_parking_bay_number1.setText(parkingDetailsModel1.getBayNo());
                                        et_country_code1.setText(parkingDetailsModel1.getCountryCode());
                                        et_phone_number1.setText(parkingDetailsModel1.getPhoneNo());
                                        et_email_id1.setText(parkingDetailsModel1.getEmailID());
                                        et_start_date.setText(parkingDetailsModel1.getDateFormat());
                                        s_start_date = parkingDetailsModel1.getDateTime();

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd yyyy");
                                        final String currentDate = sdf.format(new Date());
                                        final String currentDate1 = sdf1.format(new Date());
                                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                                        DateFormat df1 = new SimpleDateFormat("hh:mm a");
                                        final String currentTime = df.format(Calendar.getInstance().getTime());
                                        final String currentTime1 = df1.format(Calendar.getInstance().getTime());
                                        if (parkingDetailsModel1.getType().equalsIgnoreCase("postpaid")) {
                                            et_end_date.setVisibility(View.VISIBLE);
                                            radioprepaid1.setChecked(false);
                                            radiopostpaid1.setChecked(true);
                                            linear_hours.setVisibility(View.GONE);
                                            et_end_date.setEnabled(false);
                                            et_start_date.setEnabled(false);
                                           /* String splitz[]=parkingDetailsModel1.getTimeFormat().split(":");
                                            String convrtstr=splitz[0].toString();
                                            convert=Integer.valueOf(convrtstr);
                                           int finalcont= convert+1;*/
                                            //valid_until.setText(String.valueOf(finalcont));
                                            if (parkingDetailsModel1.getStatus().equals("notpaid") || parkingDetailsModel1.getStatus().equals("paid")) {
                                                et_end_date.setText(parkingDetailsModel1.getEnddateformat());
                                                tv_update.setVisibility(View.GONE);
                                                text_unpaid_car_park.setVisibility(View.GONE);
                                                et_amount_collected.setText(parkingFee);
                                                et_amount_owed.setText(parkingFee);
                                                linearPaid.setVisibility(View.VISIBLE);
                                                linearunpaid.setVisibility(View.GONE);
                                                text_paid.setText(parkingDetailsModel1.getType());
                                                valid_until1.setText("Valid until: " + parkingDetailsModel1.getValid_until());
                                                et_amount_collected.setText(parkingDetailsModel1.getAmount_collect_USD());
                                                et_amount_owed.setText(parkingDetailsModel1.getAmount_owned());
                                                linearpaymentMode.setVisibility(View.VISIBLE);
                                                paymentTypeLinear.setVisibility(View.GONE);
                                                //text_paymenttype.setText("Payment Type: "+parkingDetailsModel1.getPayment_type());
                                                if (!parkingDetailsModel1.getPayment_type().equals("null")) {
                                                    text_paymenttype.setText("Payment Type: " + parkingDetailsModel1.getPayment_type());
                                                } else {
                                                    linearpaymentMode.setVisibility(View.GONE);
                                                }

                                            } else {
                                                linearPaid.setVisibility(View.GONE);
                                                linearunpaid.setVisibility(View.VISIBLE);
                                                paymentTypeLinear.setVisibility(View.VISIBLE);
                                                linearpaymentMode.setVisibility(View.GONE);
                                                s_end_date = currentDate + " " + currentTime;

                                                et_end_date.setText(currentDate1 + " - " + currentTime1);


                                                int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
                                                System.out.println("dateDifference: " + dateDifference);
                                                System.out.println("dateDifference: " + parkingFee);

                                                if (dateDifference == 0) {

                                                    et_amount_collected.setText(parkingFee);
                                                    et_amount_owed.setText(parkingFee);

                                                } else {
                                                    et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                    et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                }
                                            }
                                        } else {
                                            et_end_date.setVisibility(View.GONE);
                                            radioprepaid1.setChecked(true);
                                            radiopostpaid1.setChecked(false);
                                            et_end_date.setEnabled(false);
                                            if (parkingDetailsModel1.getStatus().equals("notpaid") || parkingDetailsModel1.getStatus().equals("paid")) {
                                                et_end_date.setText(parkingDetailsModel1.getEnddateformat());
                                                tv_update.setVisibility(View.GONE);
                                                text_unpaid_car_park.setVisibility(View.GONE);
                                                et_amount_collected.setText(parkingFee);
                                                et_amount_owed.setText(parkingFee);
                                                linearPaid.setVisibility(View.VISIBLE);
                                                linear_hours.setVisibility(View.GONE);
                                                linearunpaid.setVisibility(View.GONE);
                                                et_start_date.setEnabled(false);
                                                text_paid.setText(parkingDetailsModel1.getType());
                                                valid_until1.setText("Valid until: " + parkingDetailsModel1.getValid_until());
                                                et_amount_collected.setText(parkingDetailsModel1.getAmount_collect_USD());
                                                et_amount_owed.setText(parkingDetailsModel1.getAmount_owned());
                                                linearpaymentMode.setVisibility(View.VISIBLE);
                                                paymentTypeLinear.setVisibility(View.GONE);
                                                //text_paymenttype.setText("Payment Type: "+parkingDetailsModel1.getPayment_type());
                                                if (!parkingDetailsModel1.getPayment_type().equals("null")) {
                                                    text_paymenttype.setText("Payment Type: " + parkingDetailsModel1.getPayment_type());
                                                } else {
                                                    linearpaymentMode.setVisibility(View.GONE);
                                                }
                                            } else {
                                                linearPaid.setVisibility(View.GONE);
                                                linearunpaid.setVisibility(View.VISIBLE);
                                                paymentTypeLinear.setVisibility(View.VISIBLE);
                                                linear_hours.setVisibility(View.VISIBLE);
                                                linearPaid.setVisibility(View.GONE);
                                                s_end_date = currentDate + " " + currentTime;

                                                et_end_date.setText(currentDate1 + " - " + currentTime1);


                                                int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
                                                System.out.println("dateDifference: " + dateDifference);
                                                System.out.println("dateDifference: " + parkingFee);

                                                if (dateDifference == 0) {

                                                    et_amount_collected.setText(parkingFee);
                                                    et_amount_owed.setText(parkingFee);

                                                } else {
                                                    et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                    et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                }
                                            }
                                        }


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
                MyData.put("method", "getparkingfees");
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void getConversationDetail() {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

                                    zimCurrency = object.getString("Zim");
                                    randsCurrency = object.getString("Rands");
                                    botswanaCurrency = object.getString("BotswanaPula");

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
                MyData.put("method", "getexchangerate");
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void updateParkCar(String status) {
        int selectedId = radioGroup1.getCheckedRadioButtonId();
        int paymentTypeID = radioGroupPaymentTpe.getCheckedRadioButtonId();
        radiopostpaid1 = (RadioButton) findViewById(selectedId);
        radiocash = (RadioButton) findViewById(paymentTypeID);
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Updating..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

                                    Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), ParCarActivity.class);
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

                if (status.equals("unpaid")) {
                    MyData.put("method", "update_notpaid_carparking");
                } else {
                    MyData.put("method", "update_carparking");
                }
                MyData.put("car_parking_id", car_parking_id);
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("supervisor_id", supervisorID);
                MyData.put("enterby_type", type);
                MyData.put("precinct_id", precinctID);
                MyData.put("bay_no", s_parking_bay_number1);
                MyData.put("number_plate", s_number_plate1);
                MyData.put("country_code", s_country_code1);
                MyData.put("phoneno", s_phone_number1);
                MyData.put("email", s_email_id1);
                MyData.put("startdatetime", s_start_date);
                MyData.put("enddatetime", s_end_date);
                MyData.put("amount_collect_USD", s_amount_collected);
                MyData.put("currency", s_currency);
                MyData.put("hours", s_hours);
                MyData.put("amount_owned", s_amount_owed);
                MyData.put("token", token);
                MyData.put("type", radiopostpaid1.getText().toString().toLowerCase());
                MyData.put("payment_type", radiocash.getText().toString());
                MyData.put("datetime", currentDate + " " + currentTime);
                Log.i("update_carparking", MyData.toString());
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public void logout(String logoutPin) {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Submitting..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

    public void getParkingDetails() {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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

                                    if (object.has("car_parking_list")) {
                                        JSONArray parkingListArray = object.getJSONArray("car_parking_list");
                                        parkingDetailsModelList = new ArrayList<>();
                                        for (int i = 0; i < parkingListArray.length(); i++) {
                                            JSONObject parkingObject = parkingListArray.getJSONObject(i);
                                            parkingDetailsModel = new ParkingDetailsModel();
                                            parkingDetailsModel.setId(parkingObject.getString("id"));
                                            parkingDetailsModel.setDateTime(parkingObject.getString("datetime"));
                                            parkingDetailsModel.setDateFormat(parkingObject.getString("datetimeformat"));
                                            parkingDetailsModel.setPlateNo(parkingObject.getString("plateno"));
                                            parkingDetailsModel.setBayNo(parkingObject.getString("bayno"));
                                            parkingDetailsModel.setStatus(parkingObject.getString("status"));
                                            parkingDetailsModel.setCountryCode(parkingObject.getString("country_code"));
                                            parkingDetailsModel.setPhoneNo(parkingObject.getString("phoneno"));
                                            parkingDetailsModel.setAmount_collect_USD(parkingObject.getString("amount_collect_USD"));
                                            parkingDetailsModel.setAmount_owned(parkingObject.getString("amount_owned"));
                                            parkingDetailsModel.setCurrency(parkingObject.getString("currency"));
                                            parkingDetailsModel.setEmailID(parkingObject.getString("email"));
                                            parkingDetailsModel.setType(parkingObject.getString("type"));
                                            parkingDetailsModel.setValid_until(parkingObject.getString("valid_until"));
                                            parkingDetailsModel.setPayment_type(parkingObject.getString("payment_type"));
                                            if (parkingObject.getString("status").equals("paid") || parkingObject.getString("status").equals("notpaid")) {
                                                parkingDetailsModel.setEnddateformat(parkingObject.getString("datetimeformatend"));
                                            }
                                            parkingDetailsModelList.add(parkingDetailsModel);

                                        }
                                    }

                                    SiteManagerAdapter enforceAdapter = new SiteManagerAdapter(ParCarActivity.this, parkingDetailsModelList);
                                    LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(horizontalLayoutManager1);
                                    recyclerView.setAdapter(enforceAdapter);

                                   /* Toast.makeText(getApplicationContext(),"Added successfully",Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(getApplicationContext(),ParCarActivity.class);
                                    startActivity(i);
                                    finish();*/
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
                MyData.put("method", "carparking_list");
                MyData.put("parking_marshal_id", parkingMarshalID);
                MyData.put("token", token);
                MyData.put("datetime", currentDate + " " + currentTime);
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

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
                    .inflate(R.layout.layout_park_car_list_row, parent, false);

            return new SiteManagerAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SiteManagerAdapter.MyViewHolder holder, int position) {
            final ParkingDetailsModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_date_time.setText(siteManagerModel.getDateFormat());
            holder.tv_plate_no.setText(siteManagerModel.getPlateNo());
            holder.tv_bay_no.setText(siteManagerModel.getBayNo());


            if (siteManagerModel.getStatus().equalsIgnoreCase("unpaid")) {
                holder.tv_status.setText("Unpaid");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.orange));
            } else if (siteManagerModel.getStatus().equalsIgnoreCase("paid")) {
                holder.tv_status.setText("Paid");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
            } else if (siteManagerModel.getStatus().equalsIgnoreCase("notpaid")) {
                holder.tv_status.setText("Not Paid");
                holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
            }

            holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    layoutCarPark.setVisibility(View.GONE);
                    layoutViewCarPark.setVisibility(View.GONE);
                    layoutUpdateCarPark.setVisibility(View.VISIBLE);
                    car_parking_id = siteManagerModel.getId();
                    et_number_plate1.setText(siteManagerModel.getPlateNo());
                    et_parking_bay_number1.setText(siteManagerModel.getBayNo());
                    et_country_code1.setText(siteManagerModel.getCountryCode());
                    et_phone_number1.setText(siteManagerModel.getPhoneNo());
                    et_email_id1.setText(siteManagerModel.getEmailID());
                    et_start_date.setText(siteManagerModel.getDateFormat());
                    s_start_date = siteManagerModel.getDateTime();
                    et_amount_collected.setText(siteManagerModel.getAmount_collect_USD());
                    et_amount_owed.setText(siteManagerModel.getAmount_owned());
                    et_currency.setText(siteManagerModel.getCurrency());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd yyyy");
                    final String currentDate = sdf.format(new Date());
                    final String currentDate1 = sdf1.format(new Date());
                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    DateFormat df1 = new SimpleDateFormat("hh:mm a");
                    final String currentTime = df.format(Calendar.getInstance().getTime());
                    final String currentTime1 = df1.format(Calendar.getInstance().getTime());


                    s_end_date = currentDate + " " + currentTime;

                    et_end_date.setText(currentDate1 + " - " + currentTime1);

                    int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
                    System.out.println("dateDifference: " + dateDifference);
                    System.out.println("dateDifference: " + parkingFee);

                    if (siteManagerModel.getType().equalsIgnoreCase("prepaid")) {
                        radioprepaid1.setChecked(true);
                        radiopostpaid1.setChecked(false);
                        et_end_date.setEnabled(false);
                        et_end_date.setVisibility(View.GONE);
                        if (siteManagerModel.getStatus().equalsIgnoreCase("unpaid")) {
                            linearunpaid.setVisibility(View.VISIBLE);
                            linear_hours.setVisibility(View.VISIBLE);
                            linearPaid.setVisibility(View.GONE);
                            tv_update.setVisibility(View.VISIBLE);
                            text_unpaid_car_park.setVisibility(View.VISIBLE);
                            et_currency.setText("USD");
                            valid_until1.setVisibility(View.GONE);
                            if (dateDifference == 0) {

                                et_amount_collected.setText(parkingFee);
                                et_amount_owed.setText(parkingFee);

                            } else {
                                et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                            }
                            linearpaymentMode.setVisibility(View.GONE);
                            paymentTypeLinear.setVisibility(View.VISIBLE);
                            // text_paymenttype.setText(parkingDetailsModel1.getPayment_type());
                        } else if (siteManagerModel.getStatus().equalsIgnoreCase("notpaid") || siteManagerModel.getStatus().equalsIgnoreCase("paid")) {
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_start_date.setText(siteManagerModel.getDateFormat());
                            et_end_date.setText(siteManagerModel.getEnddateformat());

                            et_end_date.setText(siteManagerModel.getEnddateformat());
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_amount_collected.setText(parkingFee);
                            et_amount_owed.setText(parkingFee);
                            linearPaid.setVisibility(View.VISIBLE);
                            linear_hours.setVisibility(View.GONE);
                            linearunpaid.setVisibility(View.GONE);
                            et_start_date.setEnabled(false);
                            text_paid.setText(siteManagerModel.getType());
                            valid_until1.setVisibility(View.VISIBLE);
                            valid_until1.setText("Valid until: " + siteManagerModel.getValid_until());
                            et_amount_collected.setText(siteManagerModel.getAmount_collect_USD());
                            et_amount_owed.setText(siteManagerModel.getAmount_owned());
                            linearpaymentMode.setVisibility(View.VISIBLE);
                            paymentTypeLinear.setVisibility(View.GONE);
                            if (!siteManagerModel.getPayment_type().equals("null")) {
                                text_paymenttype.setText("Payment Type: " + siteManagerModel.getPayment_type());
                            } else {
                                linearpaymentMode.setVisibility(View.GONE);
                            }
                        } else {
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_start_date.setText(siteManagerModel.getDateFormat());
                            et_end_date.setText(siteManagerModel.getEnddateformat());
                        }
                    } else {
                        radioprepaid1.setChecked(false);
                        radiopostpaid1.setChecked(true);
                        linear_hours.setVisibility(View.GONE);
                        et_end_date.setEnabled(false);
                        et_start_date.setEnabled(false);
                        et_end_date.setVisibility(View.VISIBLE);
                        if (siteManagerModel.getStatus().equalsIgnoreCase("unpaid")) {
                            valid_until1.setVisibility(View.GONE);
                            linearPaid.setVisibility(View.GONE);
                            linearunpaid.setVisibility(View.VISIBLE);
                            tv_update.setVisibility(View.VISIBLE);
                            text_unpaid_car_park.setVisibility(View.VISIBLE);
                            et_currency.setText("USD");
                            if (dateDifference == 0) {

                                et_amount_collected.setText(parkingFee);
                                et_amount_owed.setText(parkingFee);

                            } else {
                                et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                            }
                            linearpaymentMode.setVisibility(View.GONE);
                            paymentTypeLinear.setVisibility(View.VISIBLE);
                        } else if (siteManagerModel.getStatus().equalsIgnoreCase("notpaid") || siteManagerModel.getStatus().equalsIgnoreCase("paid")) {
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_start_date.setText(siteManagerModel.getDateFormat());
                            et_end_date.setText(siteManagerModel.getEnddateformat());
                            et_end_date.setText(siteManagerModel.getEnddateformat());
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_amount_collected.setText(parkingFee);
                            et_amount_owed.setText(parkingFee);
                            linearPaid.setVisibility(View.VISIBLE);
                            linearunpaid.setVisibility(View.GONE);
                            text_paid.setText(siteManagerModel.getType());
                            valid_until1.setVisibility(View.VISIBLE);
                            valid_until1.setText("Valid until: " + siteManagerModel.getValid_until());
                            et_amount_collected.setText(siteManagerModel.getAmount_collect_USD());
                            et_amount_owed.setText(siteManagerModel.getAmount_owned());
                            linearpaymentMode.setVisibility(View.VISIBLE);
                            paymentTypeLinear.setVisibility(View.GONE);
                            if (!siteManagerModel.getPayment_type().equals("null")) {
                                text_paymenttype.setText("Payment Type: " + siteManagerModel.getPayment_type());
                            } else {
                                linearpaymentMode.setVisibility(View.GONE);
                            }

                        } else {
                            tv_update.setVisibility(View.GONE);
                            text_unpaid_car_park.setVisibility(View.GONE);
                            et_start_date.setText(siteManagerModel.getDateFormat());
                            et_end_date.setText(siteManagerModel.getEnddateformat());
                        }
                    }


                }
            });


        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date_time, tv_plate_no, tv_bay_no, tv_status;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_date_time = (TextView) view.findViewById(R.id.text_date_time);
                tv_plate_no = (TextView) view.findViewById(R.id.text_plate_no);
                tv_status = (TextView) view.findViewById(R.id.text_status);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);

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
    public void onBackPressed() {

        if (layoutUpdateCarPark.getVisibility() == View.VISIBLE) {
            layoutViewCarPark.setVisibility(View.VISIBLE);
            layoutUpdateCarPark.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    public void showAlertDialogfornotpaid(String message, String status) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ParCarActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Smart Park");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                        updateParkCar(status);
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void getsearchparking() {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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
                                    JSONArray current_booking_details1 = object.getJSONArray("current_booking_details");
                                    JSONArray current_booking_details2 = object.getJSONArray("notpaid_booking_details");
                                    JSONArray current_booking_details3 = object.getJSONArray("unpaid_booking_details");
                                    if (object.has("current_booking_details")) {

                                        JSONArray current_booking_details = object.getJSONArray("current_booking_details");
                                        hideKeyboard(ParCarActivity.this);
                                        if (current_booking_details.length() > 0) {
                                            linearCurrentBooking.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < current_booking_details.length(); i++) {
                                                SearchViewModel searchViewModel = new SearchViewModel();
                                                JSONObject jsonObject1 = current_booking_details.getJSONObject(i);
                                                String id = jsonObject1.getString("id");
                                                String bay_no = jsonObject1.getString("bay_no");
                                                String start_datetime = jsonObject1.getString("start_datetime");
                                                String end_datetime = jsonObject1.getString("end_datetime");
                                                String amount = jsonObject1.getString("amount");
                                                searchViewModel.setId(id);
                                                searchViewModel.setBay_no(bay_no);
                                                searchViewModel.setStart_datetime(start_datetime);
                                                searchViewModel.setEnd_datetime(end_datetime);
                                                searchViewModel.setAmount(amount);
                                                currentList.add(searchViewModel);

                                            }
                                            CurrentBooingDetailAdapter enforceAdapter = new CurrentBooingDetailAdapter(ParCarActivity.this, currentList);
                                            LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                            current_booking_recyclerview.setLayoutManager(horizontalLayoutManager1);
                                            current_booking_recyclerview.setAdapter(enforceAdapter);
                                        }
                                    }
                                    if (object.has("notpaid_booking_details")) {


                                        JSONArray current_booking_details = object.getJSONArray("notpaid_booking_details");
                                        if (current_booking_details.length() > 0) {
                                            linearNotBooking.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < current_booking_details.length(); i++) {
                                                SearchViewModel searchViewModel = new SearchViewModel();
                                                JSONObject jsonObject1 = current_booking_details.getJSONObject(i);
                                                String id = jsonObject1.getString("id");
                                                String bay_no = jsonObject1.getString("bay_no");
                                                String start_datetime = jsonObject1.getString("start_datetime");
                                                String end_datetime = jsonObject1.getString("end_datetime");
                                                String amount = jsonObject1.getString("amount");
                                                searchViewModel.setId(id);
                                                searchViewModel.setBay_no(bay_no);
                                                searchViewModel.setStart_datetime(start_datetime);
                                                searchViewModel.setEnd_datetime(end_datetime);
                                                searchViewModel.setAmount(amount);
                                                notpaidList.add(searchViewModel);

                                            }
                                            NotPaidBooingDetailAdapter enforceAdapter = new NotPaidBooingDetailAdapter(ParCarActivity.this, notpaidList);
                                            LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                            notpaid_booking_recyclerview.setLayoutManager(horizontalLayoutManager1);
                                            notpaid_booking_recyclerview.setAdapter(enforceAdapter);
                                        }
                                    }
                                    if (object.has("unpaid_booking_details")) {

                                        JSONArray current_booking_details = object.getJSONArray("unpaid_booking_details");
                                        if (current_booking_details.length() > 0) {
                                            linearUnpaidBooking.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < current_booking_details.length(); i++) {
                                                SearchViewModel searchViewModel = new SearchViewModel();
                                                JSONObject jsonObject1 = current_booking_details.getJSONObject(i);
                                                String id = jsonObject1.getString("id");
                                                String bay_no = jsonObject1.getString("bay_no");
                                                String start_datetime = jsonObject1.getString("start_datetime");
                                                String end_datetime = jsonObject1.getString("end_datetime");
                                                String amount = jsonObject1.getString("amount");
                                                searchViewModel.setId(id);
                                                searchViewModel.setBay_no(bay_no);
                                                searchViewModel.setStart_datetime(start_datetime);
                                                searchViewModel.setEnd_datetime(end_datetime);
                                                searchViewModel.setAmount(amount);
                                                unPaidList.add(searchViewModel);

                                            }
                                            UnpaidBooingDetailAdapter enforceAdapter = new UnpaidBooingDetailAdapter(ParCarActivity.this, unPaidList);
                                            LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                                            unpaid_booking_recyclerview.setLayoutManager(horizontalLayoutManager1);
                                            unpaid_booking_recyclerview.setAdapter(enforceAdapter);
                                        }

                                        if(current_booking_details1.length()==0&&current_booking_details2.length()==0&&current_booking_details3.length()==0){
                                            et_number_plate.setText(edtPlateNo.getText().toString());

                                            relativelauout.setVisibility(View.VISIBLE);
                                            searchLinearview.setVisibility(View.GONE);
                                            tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                                            tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                            tv_view.setTextColor(getResources().getColor(R.color.black));
                                            tv_new.setTextColor(getResources().getColor(R.color.white));
                                            layoutCarPark.setVisibility(View.VISIBLE);
                                            layoutViewCarPark.setVisibility(View.GONE);
                                            layoutUpdateCarPark.setVisibility(View.GONE);
                                            text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                            text_search.setTextColor(getResources().getColor(R.color.black));
                                        }

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
                MyData.put("method", "parkinglist_by_numberplate");
                MyData.put("number_plate", edtPlateNo.getText().toString());
                MyData.put("datetime", currentDate + " " + currentTime);
                MyData.put("token", token);
                Log.i("kjsjfssff",MyData.toString());
                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }

    public class CurrentBooingDetailAdapter extends RecyclerView.Adapter<CurrentBooingDetailAdapter.MyViewHolder> {

        private List<SearchViewModel> siteManagerModelList;
        private List<SearchViewModel> filteredSiteManagerModelList;

        Context context;
        int row_index = -1;

        public CurrentBooingDetailAdapter(Context context, List<SearchViewModel> siteManagerModelList) {
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList = siteManagerModelList;

        }

        @NonNull
        @Override
        public CurrentBooingDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_booking_layout, parent, false);

            return new CurrentBooingDetailAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final CurrentBooingDetailAdapter.MyViewHolder holder, int position) {
            final SearchViewModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_date_time.setText(siteManagerModel.getStart_datetime());
            holder.text_end_date.setText(siteManagerModel.getEnd_datetime());
            holder.tv_bay_no.setText(siteManagerModel.getBay_no());
            holder.text_amount.setText(siteManagerModel.getAmount());
            holder.tv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativelauout.setVisibility(View.VISIBLE);
                    searchLinearview.setVisibility(View.GONE);
                    tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                    tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                    tv_view.setTextColor(getResources().getColor(R.color.black));
                    tv_new.setTextColor(getResources().getColor(R.color.white));
                    layoutCarPark.setVisibility(View.VISIBLE);
                    layoutViewCarPark.setVisibility(View.GONE);
                    layoutUpdateCarPark.setVisibility(View.GONE);
                    text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                    text_search.setTextColor(getResources().getColor(R.color.black));
                    et_number_plate.setText(edtPlateNo.getText().toString());
                   // et_parking_bay_number.setText(siteManagerModel.getBay_no());
                }
            });


        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date_time, text_end_date, tv_bay_no, tv_status, text_amount;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_date_time = (TextView) view.findViewById(R.id.text_date_time);
                text_end_date = (TextView) view.findViewById(R.id.text_end_date);
                tv_status = (TextView) view.findViewById(R.id.text_status);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
                text_amount = (TextView) view.findViewById(R.id.text_amount);


            }
        }


    }

    public class UnpaidBooingDetailAdapter extends RecyclerView.Adapter<UnpaidBooingDetailAdapter.MyViewHolder> {

        private List<SearchViewModel> siteManagerModelList;
        private final List<SearchViewModel> filteredSiteManagerModelList;

        Context context;
        int row_index = -1;

        public UnpaidBooingDetailAdapter(Context context, List<SearchViewModel> siteManagerModelList) {
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList = siteManagerModelList;

        }

        @NonNull
        @Override
        public UnpaidBooingDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_booking_layout, parent, false);

            return new UnpaidBooingDetailAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final UnpaidBooingDetailAdapter.MyViewHolder holder, int position) {
            final SearchViewModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_date_time.setText(siteManagerModel.getStart_datetime());
            holder.text_end_date.setText(siteManagerModel.getEnd_datetime());
            holder.tv_bay_no.setText(siteManagerModel.getBay_no());
            holder.text_amount.setText(siteManagerModel.getAmount());
            holder.tv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSearchParkingDetails(siteManagerModel.getId());
                }
            });

        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date_time, text_end_date, tv_bay_no, tv_status, text_amount;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_date_time = (TextView) view.findViewById(R.id.text_date_time);
                text_end_date = (TextView) view.findViewById(R.id.text_end_date);
                tv_status = (TextView) view.findViewById(R.id.text_status);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
                text_amount = (TextView) view.findViewById(R.id.text_amount);


            }
        }


    }

    public class NotPaidBooingDetailAdapter extends RecyclerView.Adapter<NotPaidBooingDetailAdapter.MyViewHolder> {

        private List<SearchViewModel> siteManagerModelList;
        private List<SearchViewModel> filteredSiteManagerModelList;

        Context context;
        int row_index = -1;

        public NotPaidBooingDetailAdapter(Context context, List<SearchViewModel> siteManagerModelList) {
            this.siteManagerModelList = siteManagerModelList;
            this.context = context;
            this.filteredSiteManagerModelList = siteManagerModelList;

        }

        @NonNull
        @Override
        public NotPaidBooingDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_booking_layout, parent, false);

            return new NotPaidBooingDetailAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final NotPaidBooingDetailAdapter.MyViewHolder holder, int position) {
            final SearchViewModel siteManagerModel = filteredSiteManagerModelList.get(position);
            holder.tv_date_time.setText(siteManagerModel.getStart_datetime());
            holder.text_end_date.setText(siteManagerModel.getEnd_datetime());
            holder.tv_bay_no.setText(siteManagerModel.getBay_no());
            holder.text_amount.setText(siteManagerModel.getAmount());
            holder.tv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getSearchParkingDetails(siteManagerModel.getId());
                }
            });

        }

        @Override
        public int getItemCount() {
            return filteredSiteManagerModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_date_time, text_end_date, tv_bay_no, tv_status, text_amount;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_date_time = (TextView) view.findViewById(R.id.text_date_time);
                text_end_date = (TextView) view.findViewById(R.id.text_end_date);
                tv_status = (TextView) view.findViewById(R.id.text_status);
                tv_bay_no = (TextView) view.findViewById(R.id.text_bay_no);
                text_amount = (TextView) view.findViewById(R.id.text_amount);


            }
        }


    }

    public void getSearchParkingDetails(String carparkingID) {
        final ProgressDialog pDialog = new ProgressDialog(ParCarActivity.this);
        pDialog.setMessage("Getting Details..");
        pDialog.setCancelable(false);
        pDialog.setTitle("");
        pDialog.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String currentDate = sdf.format(new Date());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        final String currentTime = df.format(Calendar.getInstance().getTime());

        RequestQueue requestQueue = Volley.newRequestQueue(ParCarActivity.this);
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
                        List<ParkingDetailsModel> parkingDetailsModel1 = new ArrayList<>();
                        if (responseArray.length() > 0) {
                            JSONObject parkingObject = responseArray.getJSONObject(0);
                            if (parkingObject.has("status")) {
                                String status = parkingObject.getString("status");
                                if (status.equals("success")) {
                                    ParkingDetailsModel parkingDetailsModel = new ParkingDetailsModel();
                                    parkingDetailsModel = new ParkingDetailsModel();
                                    parkingDetailsModel.setId(parkingObject.getString("id"));
                                    parkingDetailsModel.setDateTime(parkingObject.getString("datetime"));
                                    parkingDetailsModel.setDateFormat(parkingObject.getString("datetimeformat"));
                                    parkingDetailsModel.setPlateNo(parkingObject.getString("plateno"));
                                    parkingDetailsModel.setBayNo(parkingObject.getString("bayno"));
                                    parkingDetailsModel.setStatus(parkingObject.getString("status"));
                                    parkingDetailsModel.setCountryCode(parkingObject.getString("country_code"));
                                    parkingDetailsModel.setPhoneNo(parkingObject.getString("phoneno"));
                                    parkingDetailsModel.setAmount_collect_USD(parkingObject.getString("amount_collect_USD"));
                                    parkingDetailsModel.setAmount_owned(parkingObject.getString("amount_owned"));
                                    parkingDetailsModel.setCurrency(parkingObject.getString("currency"));
                                    parkingDetailsModel.setEmailID(parkingObject.getString("email"));
                                    parkingDetailsModel.setType(parkingObject.getString("type"));
                                    parkingDetailsModel.setValid_until(parkingObject.getString("valid_until"));
                                    parkingDetailsModel.setPayment_type(parkingObject.getString("payment_type"));
                                    if (parkingObject.getString("status").equals("paid") || parkingObject.getString("status").equals("notpaid")) {
                                        parkingDetailsModel.setEnddateformat(parkingObject.getString("datetimeformatend"));
                                    }
                                    parkingDetailsModel1.add(parkingDetailsModel);
                                    tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                    tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                                    tv_view.setTextColor(getResources().getColor(R.color.white));
                                    tv_new.setTextColor(getResources().getColor(R.color.black));
                                    layoutCarPark.setVisibility(View.GONE);
                                    layoutViewCarPark.setVisibility(View.GONE);
                                    linearCurrentBooking.setVisibility(View.GONE);
                                    linearNotBooking.setVisibility(View.GONE);
                                    linearUnpaidBooking.setVisibility(View.GONE);
                                    layoutUpdateCarPark.setVisibility(View.VISIBLE);
                                    searchLinearview.setVisibility(View.GONE);
                                    relativelauout.setVisibility(View.VISIBLE);
                                    relativelauout.setVisibility(View.VISIBLE);
                                    searchLinearview.setVisibility(View.GONE);
                                    tv_new.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                    tv_view.setBackground(getResources().getDrawable(R.drawable.rectangle_red));
                                    text_search.setBackground(getResources().getDrawable(R.drawable.rectangle_gray));
                                    tv_view.setTextColor(getResources().getColor(R.color.white));
                                    tv_new.setTextColor(getResources().getColor(R.color.black));
                                    text_search.setTextColor(getResources().getColor(R.color.black));
                                    car_parking_id = parkingDetailsModel1.get(0).getId();
                                    et_number_plate1.setText(parkingDetailsModel1.get(0).getPlateNo());
                                    et_parking_bay_number1.setText(parkingDetailsModel1.get(0).getBayNo());
                                    et_country_code1.setText(parkingDetailsModel1.get(0).getCountryCode());
                                    et_phone_number1.setText(parkingDetailsModel1.get(0).getPhoneNo());
                                    et_email_id1.setText(parkingDetailsModel1.get(0).getEmailID());
                                    et_start_date.setText(parkingDetailsModel1.get(0).getDateFormat());
                                    s_start_date = parkingDetailsModel1.get(0).getDateTime();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd yyyy");
                                    final String currentDate = sdf.format(new Date());
                                    final String currentDate1 = sdf1.format(new Date());
                                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                                    DateFormat df1 = new SimpleDateFormat("hh:mm a");
                                    final String currentTime = df.format(Calendar.getInstance().getTime());
                                    final String currentTime1 = df1.format(Calendar.getInstance().getTime());
                                    if (parkingDetailsModel1.get(0).getType().equalsIgnoreCase("postpaid")) {
                                        et_end_date.setVisibility(View.VISIBLE);
                                        radioprepaid1.setChecked(false);
                                        radiopostpaid1.setChecked(true);
                                        linear_hours.setVisibility(View.GONE);
                                        et_end_date.setEnabled(false);
                                        et_start_date.setEnabled(false);
                                           /* String splitz[]=parkingDetailsModel1.getTimeFormat().split(":");
                                            String convrtstr=splitz[0].toString();
                                            convert=Integer.valueOf(convrtstr);
                                           int finalcont= convert+1;*/
                                        //valid_until.setText(String.valueOf(finalcont));
                                        if (parkingDetailsModel1.get(0).getStatus().equals("notpaid") || parkingDetailsModel1.get(0).getStatus().equals("paid")) {
                                            et_end_date.setText(parkingDetailsModel1.get(0).getEnddateformat());
                                            tv_update.setVisibility(View.GONE);
                                            text_unpaid_car_park.setVisibility(View.GONE);
                                            et_amount_collected.setText(parkingFee);
                                            et_amount_owed.setText(parkingFee);
                                            linearPaid.setVisibility(View.VISIBLE);
                                            linearunpaid.setVisibility(View.GONE);
                                            text_paid.setText(parkingDetailsModel1.get(0).getType());
                                            valid_until1.setText("Valid until: " + parkingDetailsModel1.get(0).getValid_until());
                                            et_amount_collected.setText(parkingDetailsModel1.get(0).getAmount_collect_USD());
                                            et_amount_owed.setText(parkingDetailsModel1.get(0).getAmount_owned());
                                            linearpaymentMode.setVisibility(View.VISIBLE);
                                            paymentTypeLinear.setVisibility(View.GONE);
                                            //text_paymenttype.setText("Payment Type: "+parkingDetailsModel1.getPayment_type());
                                            if (!parkingDetailsModel1.get(0).getPayment_type().equals("null")) {
                                                text_paymenttype.setText("Payment Type: " + parkingDetailsModel1.get(0).getPayment_type());
                                            } else {
                                                linearpaymentMode.setVisibility(View.GONE);
                                            }

                                        } else {
                                            linearPaid.setVisibility(View.GONE);
                                            linearunpaid.setVisibility(View.VISIBLE);
                                            paymentTypeLinear.setVisibility(View.VISIBLE);
                                            linearpaymentMode.setVisibility(View.GONE);
                                            s_end_date = currentDate + " " + currentTime;

                                            et_end_date.setText(currentDate1 + " - " + currentTime1);


                                            int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
                                            System.out.println("dateDifference: " + dateDifference);
                                            System.out.println("dateDifference: " + parkingFee);

                                            if (dateDifference == 0) {

                                                et_amount_collected.setText(parkingFee);
                                                et_amount_owed.setText(parkingFee);

                                            } else {
                                                et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                            }
                                        }
                                    } else {
                                        et_end_date.setVisibility(View.GONE);
                                        radioprepaid1.setChecked(true);
                                        radiopostpaid1.setChecked(false);
                                        et_end_date.setEnabled(false);
                                        if (parkingDetailsModel1.get(0).getStatus().equals("notpaid") || parkingDetailsModel1.get(0).getStatus().equals("paid")) {
                                            et_end_date.setText(parkingDetailsModel1.get(0).getEnddateformat());
                                            tv_update.setVisibility(View.GONE);
                                            text_unpaid_car_park.setVisibility(View.GONE);
                                            et_amount_collected.setText(parkingFee);
                                            et_amount_owed.setText(parkingFee);
                                            linearPaid.setVisibility(View.VISIBLE);
                                            linear_hours.setVisibility(View.GONE);
                                            linearunpaid.setVisibility(View.GONE);
                                            et_start_date.setEnabled(false);
                                            text_paid.setText(parkingDetailsModel1.get(0).getType());
                                            valid_until1.setText("Valid until: " + parkingDetailsModel1.get(0).getValid_until());
                                            et_amount_collected.setText(parkingDetailsModel1.get(0).getAmount_collect_USD());
                                            et_amount_owed.setText(parkingDetailsModel1.get(0).getAmount_owned());
                                            linearpaymentMode.setVisibility(View.VISIBLE);
                                            paymentTypeLinear.setVisibility(View.GONE);
                                            //text_paymenttype.setText("Payment Type: "+parkingDetailsModel1.getPayment_type());
                                            if (!parkingDetailsModel1.get(0).getPayment_type().equals("null")) {
                                                text_paymenttype.setText("Payment Type: " + parkingDetailsModel1.get(0).getPayment_type());
                                            } else {
                                                linearpaymentMode.setVisibility(View.GONE);
                                            }
                                        } else {
                                            linearPaid.setVisibility(View.GONE);
                                            linearunpaid.setVisibility(View.VISIBLE);
                                            paymentTypeLinear.setVisibility(View.VISIBLE);
                                            linear_hours.setVisibility(View.VISIBLE);
                                            linearPaid.setVisibility(View.GONE);
                                            s_end_date = currentDate + " " + currentTime;

                                            et_end_date.setText(currentDate1 + " - " + currentTime1);


                                            int dateDifference = (int) get_count_of_days(s_start_date, s_end_date);
                                            System.out.println("dateDifference: " + dateDifference);
                                            System.out.println("dateDifference: " + parkingFee);

                                            if (dateDifference == 0) {

                                                et_amount_collected.setText(parkingFee);
                                                et_amount_owed.setText(parkingFee);

                                            } else {
                                                et_amount_collected.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                                et_amount_owed.setText(String.valueOf(dateDifference * Double.valueOf(parkingFee)));
                                            }
                                        }
                                    }




                                   /* Toast.makeText(getApplicationContext(),"Added successfully",Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(getApplicationContext(),ParCarActivity.class);
                                    startActivity(i);
                                    finish();*/
                                } else {
                                    showAlertDialog(parkingObject.getString("message"));
                                }
                            } else {
                                showAlertDialog(parkingObject.getString("message"));
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
                MyData.put("method", "carparking_details");
                MyData.put("carparking_id", carparkingID);
                MyData.put("token", token);

                return MyData;
            }
        };

        requestQueue.add(MyStringRequest);

    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}