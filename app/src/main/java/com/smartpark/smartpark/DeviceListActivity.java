package com.smartpark.smartpark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListActivity extends Activity {
    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    List<DevicesModel> devicesModelList = new ArrayList<>();
    DevicesModel devicesModel=new DevicesModel();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        setResult(Activity.RESULT_CANCELED);
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView mPairedListView = (ListView) findViewById(R.id.paired_devices);
        recyclerView=findViewById(R.id.recyclerview_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        //mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mPairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    mBluetoothAdapter.cancelDiscovery();
                    String mDeviceInfo = ((TextView) view).getText().toString();
                    String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                    Log.v(TAG, "Device_Address " + mDeviceAddress);

                    Bundle mBundle = new Bundle();
                    mBundle.putString("DeviceAddress", mDeviceAddress);
                    Intent mBackIntent = new Intent();
                    mBackIntent.putExtras(mBundle);
                    setResult(Activity.RESULT_OK, mBackIntent);
                    finish();
                } catch (Exception ex) {

                }

            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            devicesModelList=new ArrayList<>();
            for (BluetoothDevice mDevice : mPairedDevices) {
                devicesModel=new DevicesModel();
                devicesModel.setName(mDevice.getName());
                devicesModel.setAddress(mDevice.getAddress());

                devicesModelList.add(devicesModel);
              //  mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }

           DevicesAdapter devicesAdapter = new DevicesAdapter(DeviceListActivity.this, devicesModelList);
            LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(horizontalLayoutManager1);
            recyclerView.setAdapter(devicesAdapter);
        } else {
            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolder> implements Filterable {

        private List<DevicesModel> devicesModelList;
        private List<DevicesModel> filteredDevicesModelList;

        Context context;
        int row_index=-1;

        public DevicesAdapter(Context context, List<DevicesModel> devicesModelList){
            this.devicesModelList = devicesModelList;
            this.context = context;
            this.filteredDevicesModelList =devicesModelList;

        }
        @NonNull
        @Override
        public DevicesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_devices_row, parent, false);

            return new DevicesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final DevicesAdapter.MyViewHolder holder, int position) {
            final DevicesModel siteManagerModel = filteredDevicesModelList.get(position);
            holder.tv_name.setText(siteManagerModel.getName());
            holder.tv_address.setText(siteManagerModel.getAddress());

            holder.linearLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        mBluetoothAdapter.cancelDiscovery();
                    //    String mDeviceInfo = ((TextView) view).getText().toString();
                        String mDeviceAddress = siteManagerModel.getAddress();
                        Log.v(TAG, "Device_Address " + mDeviceAddress);

                        Bundle mBundle = new Bundle();
                        mBundle.putString("DeviceAddress", mDeviceAddress);
                        Intent mBackIntent = new Intent();
                        mBackIntent.putExtras(mBundle);
                        setResult(Activity.RESULT_OK, mBackIntent);
                        finish();
                    } catch (Exception ex) {

                    }

                }
            });




        }

        @Override
        public int getItemCount() {
            return filteredDevicesModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_name,tv_address;
            LinearLayout linearLayoutContainer;

            public MyViewHolder(View view) {
                super(view);


                tv_name = (TextView) view.findViewById(R.id.text_name);
                tv_address = (TextView) view.findViewById(R.id.text_address);

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
                        filteredDevicesModelList = devicesModelList;
                    } else {
                        List<DevicesModel> filteredList = new ArrayList<>();
                        for (DevicesModel row : devicesModelList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if ( row.getName().toLowerCase().contains(charSequence)|| row.getAddress().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        filteredDevicesModelList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredDevicesModelList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredDevicesModelList = (ArrayList<DevicesModel>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {

            try {
                mBluetoothAdapter.cancelDiscovery();
                String mDeviceInfo = ((TextView) mView).getText().toString();
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                Log.v(TAG, "Device_Address " + mDeviceAddress);

                Bundle mBundle = new Bundle();
                mBundle.putString("DeviceAddress", mDeviceAddress);
                Intent mBackIntent = new Intent();
                mBackIntent.putExtras(mBundle);
                setResult(Activity.RESULT_OK, mBackIntent);
                finish();
            } catch (Exception ex) {

            }
        }
    };

}