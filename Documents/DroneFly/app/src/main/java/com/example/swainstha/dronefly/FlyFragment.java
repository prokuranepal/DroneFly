package com.example.swainstha.dronefly;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlyFragment extends Fragment {

    private View view;
    private Button flyButton;
    private Button statusButton;
    //private final String urlString = "https://nicwebpage.herokuapp.com/android?fly=1";
    private final String urlString = "http://192.168.1.119:3000";
    private String result = "";
    private Socket socket;

    public FlyFragment() {
        // Required empty public constructor
    }


    //sending data to the node js server so it can forward it, to the raspberry pi in drone to start the flight
    public class SendFlyCommand extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            Thread.currentThread().setName("Fly");
            try {

//                result = "";
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


//                urlConnection.setRequestMethod("GET");
//                urlConnection.setDoInput(true);
//                urlConnection.setDoOutput(true);
//                urlConnection.connect();
//                urlConnection.getOutputStream().write( ("fly=1").getBytes());

//                InputStream input = urlConnection.getInputStream(); //getting the input stream
//                InputStreamReader reader = new InputStreamReader(input);  //be able to read the input stream
//                int data = reader.read();
//                while(data != -1) {
//
//                    char current = (char) data;
//                    result += current;
//                    data = reader.read();
//                }


                //sending message to server
                socket.emit("fly", "1");
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("INFO","Failed Sending. May be no internet connection");
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i("INFO",result);
            result = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_fly, container, false);

//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        flyButton = view.findViewById(R.id.flyButton);

        flyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //class which contains send function to the server as async task
                SendFlyCommand sendFlyCommand = new SendFlyCommand();
                try {

                    //check for internet connectivity
                    if(!isOnline()) {
                        throw new Exception();
                    }

                    //execute worker thread to send data
                    sendFlyCommand.execute().get();

                }catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.i("INFO","Failed Sending");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Log.i("INFO","Failed Sending");
                } catch(Exception e) {
                    Log.i("INFO","No internet connection may be");
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //status fragment to display the web page content of status of drone
        statusButton = view.findViewById(R.id.statusButton);

        //callback function for status button to start status fragment
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Drone Status", Toast.LENGTH_SHORT).show();
                openStatusView();
            }
        });


        //creating a socket to connect to the node js server using socket.io.client
        try {

            //check for internet connection
            if(!isOnline()) {
                throw new Exception();
            }

            //generating a random number for join id in the server
            Random rand = new Random();
            int  id = rand.nextInt(50) + 1;

            socket = IO.socket(urlString); //specifying the url
            socket.connect(); //connecting to the server
            socket.emit("joinAndroid",Integer.toString(id) );  //specifying the join group to the server

            //callback functions for socket connected, message received and socket disconnected

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                }

            }).on("success", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.i("INFO",args[0].toString());
                    final String res = args[0].toString();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(getContext(), args[0].toString(), Toast.LENGTH_SHORT).show();

                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                }

            });
        } catch(URISyntaxException e) {
            Log.i("INFO","Uri syntax exception");
        } catch(Exception e) {
            Log.i("INFO", "No internet connection");
            Toast.makeText(getContext(), "No Internet", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public void openStatusView() {

        //get intent from status fragment
        Intent i = new Intent(getActivity().getBaseContext(),MainActivity.class);
        i.putExtra("Start", "Yes");
        getActivity().startActivity(i);
    }


    //check if the device is connected to the internet
    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //check if the connection id wifi or mobile data
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isConnected;

    }

}
