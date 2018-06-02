package com.example.swainstha.dronefly;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.*;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,CheckListAdapter.PictureClickListener {

    private GoogleMap mMap;
    Marker marker;
    private Socket socket;

//    private final String urlString = "http://192.168.1.119:3000";
    private final String urlString = "https://nicwebpage.herokuapp.com";

    AdapterView statusListView;
    ArrayList<StatusData> statusList;
    StatusListAdapter statusListAdapter;

    ListView checkListView;
    ArrayList<CheckList> checkList;
    CheckListAdapter checkListAdapter;

    ListView idListView;
    ArrayList<IdList> idList;
    IdListAdapter idListAdapter;

    boolean markerChanged = false; //to change the marker color from red to green on arm and green to red on disarm
    boolean makeDronePath = false; //starting making the drone path after the fly command is sent
    boolean loadCurrentPosition = false; //to load the map and position on first receive of status
    boolean firstMissionLoad = true; //to load mission at first to initialize the preLatLngMission
    boolean flyFlag = false; //to fly only when all the checkboxes are checked by the user

    JSONObject data;
    JSONObject mission;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayout2;
    boolean showHide = false; //for status button

    Circle circle1;
    Circle circle2;
    Circle circle3;
    Polyline lineH;
    Polyline lineV;
    ArrayList<Polyline> dronePath;
    ArrayList<Polyline> missionPath;
    boolean circleUnCirle = false; //for circle button

    Button show;
    Button circle;
    Button fly;
    Button downMission;
    Button clearMission;
    Spinner circleSpinner;
    Integer circleRadius;

    LatLng home; //current position
    LatLng prevLatLng;
    LatLng prevLatLngMission;
    ArrayList<Marker> missionMarker; //for markers of mission waypoints

    AlertDialog.Builder builder; //dialog builder to ask for confirmation after fly button is pressed

    @Override
    public void onPictureClick(boolean c){

        if(c) {
            fly.setBackgroundResource(R.drawable.fly_green);
            flyFlag = true;
        }
        else {
            fly.setBackgroundResource((R.drawable.fly_red));
            flyFlag = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //initialize socket by sending joinAndroid event
        initSocket();

        //markers, mission path and drone path arraylist
        missionMarker = new ArrayList<>();
        dronePath = new ArrayList<>();
        missionPath = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //list for status
        statusList = new ArrayList<>();
        statusListView = this.findViewById(R.id.statusListView);
        relativeLayout = findViewById(R.id.cell1);
        relativeLayout.setVisibility(View.GONE);

        checkList = new ArrayList<>();
        checkListView = this.findViewById(R.id.checkListView);
        relativeLayout2 = this.findViewById(R.id.cell2);
        relativeLayout2.setVisibility(View.GONE);

        idList = new ArrayList<>();
        idListView = this.findViewById(R.id.idListView);


        circleSpinner = findViewById(R.id.circle_radius);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.radius_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        circleSpinner.setAdapter(adapter);

        circleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                circleRadius = Integer.parseInt((String) adapterView.getItemAtPosition(i));
                Log.i("INFO", circleRadius + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                circleRadius = 50;
            }
        });
        //showing the status
        show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!showHide) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout2.setVisibility(View.VISIBLE);
                    show.setText("HIDE");
                    showHide = true;
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayout2.setVisibility(View.GONE);
                    show.setText("SHOW");
                    showHide = false;
                }
            }
        });

        //making three circles and cross lines with the home location at center
        circle = findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("INFO","HIDDEN");
                if(!circleUnCirle) {
                    LatLng latLng = home;
                    int radius;
                    try {
                        radius = circleRadius;
                    } catch(NumberFormatException e){
                        radius = 50;
                    }

                    //making 3 circles
                    circle1 = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(radius).strokeWidth(1)
                            .strokeColor(Color.WHITE));
                    circle2 = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(2 * radius).strokeWidth(1)
                            .strokeColor(Color.WHITE));
                    circle3 = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(3 * radius).strokeWidth(1)
                            .strokeColor(Color.WHITE));

                    //calculate the four points for making cross section lines
                    Geo geo = new Geo(latLng,3 * radius);
                    ArrayList<Double> list = geo.calculate();
                    Log.i("Info",list.toString());

                    lineH = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(list.get(0), list.get(1)), new LatLng(list.get(4), list.get(5)))
                            .width(1)
                            .color(Color.WHITE));
                    lineV = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(list.get(2), list.get(3)), new LatLng(list.get(6), list.get(7)))
                            .width(1)
                            .color(Color.WHITE));
                    circleUnCirle = true;
                    circle.setText("CLEAR");
                } else {
                    circle1.remove();
                    circle2.remove();
                    circle3.remove();
                    lineH.remove();
                    lineV.remove();
                    circleUnCirle = false;
                    circle.setText("CIRCLE");
                }
            }
        });

        //sending fly command
        fly = findViewById(R.id.fly);
        fly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flyFlag) {
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {
                    Toast.makeText(MapsActivity.this, "Make sure all checks are completed", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //send download mission command
        downMission = findViewById(R.id.download_mission);
        downMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearMission();
                SendCommand sendCommand = new SendCommand();
                try {
                    JSONObject obj = new JSONObject();
                    obj.accumulate("mission","1");
                    obj.accumulate("device","android");
                    Log.i("INFO",obj.toString());
                    String res = sendCommand.execute("getMission",obj.toString()).get();
                    Log.i("INFO", res);
                } catch(ExecutionException e) {
                    Log.i("INFO","Execution exception");
                } catch(InterruptedException i) {
                    Log.i("INFO","Interrupted exception");
                } catch(JSONException j) {
                    Log.i("INFO","Json exception");
                }
            }
        });

        //clear the loaded mission
        clearMission = findViewById(R.id.clear_mission);
        clearMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    dronePath.remove();
//                }catch(NullPointerException npe) {
//                    Log.i("INFO","Drone path not defined");
//                }
                clearMission();

            }
        });

        //crating a dialog box to confirm fly
        builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                SendCommand sendCommand = new SendCommand();
                try {
                    String res = sendCommand.execute("fly", "1").get();
                    makeDronePath = true;
                    Log.i("INFO", res);
                } catch (ExecutionException e) {
                    Log.i("INFO", "Execution exception");
                } catch (InterruptedException i) {
                    Log.i("INFO", "Interrupted exception");
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        //creating listview for status
        statusList.add(new StatusData("Lat: ","0"));
        statusList.add(new StatusData("Long: ","0"));
        statusList.add(new StatusData("AltR: ","0"));
        statusList.add(new StatusData("Alt: ","0"));
        statusList.add(new StatusData("Sat: ","0"));
        statusList.add(new StatusData("HDOP: ","0"));
        statusList.add(new StatusData("FIX: ","0"));
        statusList.add(new StatusData("Head: ","0"));
        statusList.add(new StatusData("GS: ","0"));
        statusList.add(new StatusData("AS: ","0"));
        statusList.add(new StatusData("Mode: ","0"));
        statusList.add(new StatusData("Arm: ","0"));
        statusList.add(new StatusData("EKF: ","0"));
        statusList.add(new StatusData("Status: ","0"));
        statusList.add(new StatusData("LiDar: ","0"));
        statusList.add(new StatusData("Volt: ","0"));
        statusList.add(new StatusData("Conn: ","0"));

        statusListAdapter = new StatusListAdapter(this,statusList);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(R.layout.status_list_view,statusListView, false);

        statusListView.setAdapter(statusListAdapter);

        checkList.add(new CheckList("GPS Lock",false));
        checkList.add(new CheckList("Battery",false));
        checkList.add(new CheckList("Horizon",false));
        checkList.add(new CheckList("Mag",false));

        checkListAdapter = new CheckListAdapter(this, checkList);
        LayoutInflater inflater1 = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater1.inflate(R.layout.check_list_header,
                checkListView, false);
        checkListView.addHeaderView(header);
        checkListView.setAdapter(checkListAdapter);
        checkListAdapter.setPictureClickListener(this);

        idList.add(new IdList("Drone Id: ","0"));
        idList.add(new IdList("Mission Id: ","0"));
        idList.add(new IdList("Flight Time: ","0"));

        idListAdapter = new IdListAdapter(this, idList);
        LayoutInflater inflater2 = getLayoutInflater();
        inflater2.inflate(R.layout.id_list_view,idListView, false);
        idListView.setAdapter(idListAdapter);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Kupondole.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        home = new LatLng(27.686328887939453, 85.3176498413086);
        marker = mMap.addMarker(new MarkerOptions().position(home).title("Marker in Kupondole")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red)).alpha(0.7f).flat(true)
                .rotation(0.0f));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 17.0f));

        //Move the camera to the user's location and zoom in!
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nic, 17.0f));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }

    public void initSocket() {

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

            SendCommand sendCommand = new SendCommand();
            try {

                //check for internet connectivity
                if(!isOnline()) {
                    throw new Exception();
                }

                //execute worker thread to send data
                sendCommand.execute("joinAndroid","").get();

            }catch (InterruptedException e) {
                e.printStackTrace();
                Log.i("INFO","Failed Sending");
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.i("INFO","Failed Sending");
            }

            //callback functions for socket connected, message received and socket disconnected

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                }

            }).on("copter-data", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    final String res = args[0].toString();
                    Log.i("INFO",res);

                    try {
                        data = new JSONObject(res);
                    } catch(JSONException e) {
                        Log.i("INFO","Json Exception");
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {

                                int i = 0;
                                Iterator iter = data.keys();
                                while(iter.hasNext()){
                                    String key = (String)iter.next();
                                    String value = data.getString(key);
                                    statusList.get(i).setValue(value);
                                    i++;
                                }
//                                for (int i = 0; i < statusList.size(); i++) {
//                                    statusList.get(i).setValue(data.names().get(i).toString());
//                                }
                                statusListAdapter.notifyDataSetChanged();
                                LatLng currentLatLng = new LatLng(Double.parseDouble(data.getString("lat").toString()),
                                        Double.parseDouble(data.getString("lng").toString()));


                                //load the map and current position on first receive of `
                                if(loadCurrentPosition == false) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17.0f));
                                    loadCurrentPosition = true;
                                    prevLatLng = currentLatLng;
                                    home = currentLatLng;
                                }

                                //change color of markers based on arm
                                if(data.getString("arm").toString() == "true" && markerChanged) {
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.green));
                                    markerChanged = false;
                                } else if(data.getString("arm").toString() == "false" && !markerChanged){
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.red));
                                    markerChanged = true;
                                }

                                //set the rotation based on heading
                                marker.setRotation(Float.parseFloat(data.getString("head").toString()));

                                //set the location
                                marker.setPosition(currentLatLng);

                                //start making the flight path after the fly command is sent
                                if(makeDronePath) {
                                    dronePath.add(mMap.addPolyline(new PolylineOptions()
                                            .add(prevLatLng, currentLatLng)
                                            .width(1)
                                            .color(Color.GREEN)));
                                }
                                prevLatLng = currentLatLng;

                            } catch(JSONException e) {

                                Log.i("INFO","Json exception in status data receive");
                                e.printStackTrace();

                            }
                        }
                    });
                    //Toast.makeText(getContext(), args[0].toString(), Toast.LENGTH_SHORT).show();
                }

            }).on("Mission", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    final String res = args[0].toString();
                    //final String res = "{\"0\":{\"lat\":27.686328887939453,\"lng\":85.3176498413086},\"1\":{\"lat\":27.687082290649414,\"lng\":85.31800842285156,\"command\":16,\"alt\":10},\"2\":{\"lat\":27.686342239379883,\"lng\":85.31832885742188,\"command\":16,\"alt\":10},\"3\":{\"lat\":27.68666648864746,\"lng\":85.31977844238281,\"command\":16,\"alt\":10},\"4\":{\"lat\":27.687395095825195,\"lng\":85.32050323486328,\"command\":16,\"alt\":10}}";
                    Log.i("INFO",res);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                mission = new JSONObject(res);
                                //getting mission and adding title and snippet based on command number
                                for(int i=0;i < mission.length();i++){
                                    JSONObject j = mission.getJSONObject(Integer.toString(i));
                                    LatLng latLng = new LatLng(j.getDouble("lat"),j.getDouble("lng"));
                                    if(i == 0) {
                                        missionMarker.add(mMap.addMarker(new MarkerOptions().position(latLng).title("TakeOff")
                                                .icon(BitmapDescriptorFactory.defaultMarker())));
                                    } else if(Integer.parseInt(j.getString("command")) == 16) {
                                        missionMarker.add(mMap.addMarker(new MarkerOptions().position(latLng).title("WayPoint " + i).snippet("Alt: " + j.getString("alt"))
                                                .icon(BitmapDescriptorFactory.defaultMarker())));
                                    } else if (Integer.parseInt(j.getString("command")) == 21){
                                        missionMarker.add(mMap.addMarker(new MarkerOptions().position(latLng).title("Land")
                                                .icon(BitmapDescriptorFactory.defaultMarker())));
                                    }

                                    //checking for first mission command
                                    if(firstMissionLoad) {
                                        prevLatLngMission = latLng;
                                        firstMissionLoad = false;
                                    }

                                    //adding mission path
                                    missionPath.add(mMap.addPolyline(new PolylineOptions()
                                            .add(prevLatLngMission, latLng)
                                            .width(1)
                                            .color(Color.RED)));

                                    prevLatLngMission = latLng;
                                }

                            } catch(JSONException e) {
                                Log.i("INFO","Json Exception");
                            }

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
            Toast.makeText(this, "No Internet", Toast.LENGTH_LONG).show();
        }

    }

    //check if the device is connected to the internet
    protected boolean isOnline() {

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //check if the connection id wifi or mobile data
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isConnected;

    }

    //sending data to the node js server so it can forward it, to the raspberry pi in drone to start the flight
    public class SendCommand extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {
                //sending message to server
                socket.emit(urls[0],urls[1]);
                Log.i("INFO",urls[0]+urls[1]);
                return "Success";

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("INFO","Failed Sending. May be no internet connection");
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            //Log.i("INFO",result);
            result = "";
        }
    }

    //clear the mission path and markers
    public void clearMission() {

        firstMissionLoad = true;
        try {
            for(int i=0;i< missionPath.size();i++) {
                missionPath.get(i).remove();
            }
        }catch(NullPointerException npe) {
            Log.i("INFO","Mission path not defined");
        }
        try {
            for(int i=0;i< missionMarker.size();i++) {
                missionMarker.get(i).remove();
            }
        }catch(NullPointerException npe) {
            Log.i("INFO","Mission marker not defined");
        }
    }
}
