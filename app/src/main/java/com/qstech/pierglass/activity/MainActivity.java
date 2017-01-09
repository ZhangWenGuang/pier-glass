package com.qstech.pierglass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.qstech.pierglass.R;

public class MainActivity extends AppCompatActivity {

    private Button mListButton;
    private Button mFindWifiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListButton = (Button) findViewById(R.id.btn_main_list);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MediaListActivity.class));
            }
        });

        mFindWifiButton = (Button) findViewById(R.id.btn_main_find_wifi);
        mFindWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FindWifiActivity.class));
            }
        });
    }
}
