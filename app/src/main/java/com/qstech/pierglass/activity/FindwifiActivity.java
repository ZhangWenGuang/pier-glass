package com.qstech.pierglass.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.qstech.pierglass.R;
import com.qstech.pierglass.adapter.WifiListAdapter;
import com.qstech.pierglass.utils.Utility;
import com.qstech.pierglass.utils.WifiUtils;

import java.util.List;

public class FindWifiActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "FindWifiActivity";
    private Button check_wifi,open_wifi,close_wifi,scan_wifi;
    private ListView mListView;
    protected WifiUtils mWifiUtils;
    private List<ScanResult> mWifiList;
    public int level;
    protected String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findwifi);
        mWifiUtils = new WifiUtils(FindWifiActivity.this);
        initViews();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);

    }

    private void initViews() {
        check_wifi = (Button) findViewById(R.id.btn_check_wifi);
        open_wifi = (Button) findViewById(R.id.btn_open_wifi);
        close_wifi = (Button) findViewById(R.id.btn_close_wifi);
        scan_wifi = (Button) findViewById(R.id.btn_scan_wifi);
        mListView = (ListView) findViewById(R.id.lv_wifi);
        check_wifi.setOnClickListener(this);
        open_wifi.setOnClickListener(this);
        close_wifi.setOnClickListener(this);
        scan_wifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_wifi:
                mWifiUtils.checkState(FindWifiActivity.this);
                break;
            case R.id.btn_open_wifi:
                mWifiUtils.openWifi(FindWifiActivity.this);
                break;
            case R.id.btn_close_wifi:
                mWifiUtils.closeWifi(FindWifiActivity.this);
                break;
            case R.id.btn_scan_wifi:
                mWifiUtils.startScan(FindWifiActivity.this);
                mWifiList = mWifiUtils.getWifiList();
                if (mWifiList != null) {
                    mListView.setAdapter(new WifiListAdapter(this, mWifiList));
                    new Utility().setListViewHeightBasedOnChildren(mListView);
                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //NetworkInfo wifiInfo = manager.getActiveNetworkInfo();
            if (wifiInfo.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo().getSSID();
                Toast.makeText(context, wifiSSID + "连接成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
