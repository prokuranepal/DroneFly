package com.example.swainstha.dronefly;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button flyButton;
    Button statusButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.fragmentContainer, new FlyFragment());
        ft.commit();

    }

    private void openFragment()
    {
        //PASS OVER THE BUNDLE TO OUR FRAGMENT
        StatusFragment statusFragment = new StatusFragment();
        //THEN NOW SHOW OUR FRAGMENT
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,statusFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //determine who started this activity
        try {
            final String sender = this.getIntent().getExtras().getString("Start");

            if (sender.equals("Yes")) {
                openFragment();
                Log.i("INFO","Resumed from fly Fragment");
                Toast.makeText(this, "Start status fragment", Toast.LENGTH_SHORT).show();

            }
        } catch(NullPointerException e) {
            Log.i("INFO","Not resumed from fragment");
        }

    }

}
