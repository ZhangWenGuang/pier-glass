package com.qstech.pierglass.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qstech.pierglass.R;

import java.util.List;

/**
 * Created by admin on 2017/1/3.
 */

public class WifiListAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<ScanResult> mList;
    public int level;

    public WifiListAdapter(Context context, List<ScanResult> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        view = mInflater.inflate(R.layout.wifi_listitem, null);
        ScanResult scanResult = mList.get(position);
        TextView wifi_ssid = (TextView) view.findViewById(R.id.ssid);
        TextView wifi_level = (TextView) view.findViewById(R.id.wifi_level);
        wifi_ssid.setText(scanResult.SSID);
        Log.i("wifi_list", "scanResult.SSID=" + scanResult);
        level = WifiManager.calculateSignalLevel(scanResult.level, 5);
        if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("PSK") || scanResult.capabilities.contains("EAP")) {
            wifi_level.setText("lock");
        } else {
            wifi_level.setText("open");
        }
        //判断信号强度，显示对应的指示图标
        wifi_level.setText("强度：" + level);
        return view;
    }
}
