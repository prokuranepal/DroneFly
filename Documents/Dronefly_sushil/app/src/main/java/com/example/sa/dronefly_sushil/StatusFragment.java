package com.example.sa.dronefly_sushil;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private View view;
    private Bundle webViewBundle;
    private WebView webStatusView;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_status_layout, container, false);

        webStatusView = view.findViewById(R.id.webStatusDrone);

        webStatusView.setWebViewClient(new WebViewClient());

       // webStatusView.loadUrl("https://nicwebpage.herokuapp.com/status");
        webStatusView.loadUrl("http://192.168.1.131:3000/status");

        //javascript should be enable true to show maps otherwise the maps wont load
        webStatusView.getSettings().setJavaScriptEnabled(true);
//        webStatusView.loadUrl("https://maps.google.com/?ll=37.0625,-95.677068&spn=29.301969,56.513672&t=h&z=4");
        Log.i("INFO", "loaded from url");


        return view;
    }







}
