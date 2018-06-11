package com.example.swainstha.dronefly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.example.swainstha.dronefly.R.color.white;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageView;
    //EditText username;
    EditText password;
    Button sign;
    JSONObject user_detail = new JSONObject();
    Toolbar toolbar;
    Socket socket;
    //private String urlString = "http://192.168.1.119:3001";
    boolean connection_flag = false;
    SharedPreferences shared;
    RequestQueue queue;
    String url = "http://192.168.1.119:3000/android/";
    Spinner username;
    String user_name;
    String[] items = new String[]{"nicdrone", "nicpulchowk", "nicnangi","fusedrone","airbus","boeing","jet"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        username = findViewById(R.id.spinner1);
        username.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
        //username.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
     //   username.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

// Request a string response from the provided URL.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);

        username.setAdapter(adapter);
        username.setOnItemSelectedListener(this);
        imageView = (ImageView) findViewById(R.id.imageView);
        // username = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        sign = (Button) findViewById(R.id.button);


    }

    public void LogInNow(View view) {
        try {

            postRequestVolley(queue);

        } catch (Exception e) {
            Log.i("error", e.toString());
        }

        //socket.emit("password",password.getText().toString());
    }


    public void signUpSignInOption() {
        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
        i.putExtra("place",user_name);
        startActivity(i);

    }


    public void onDestroy() {

        super.onDestroy();

    }

    public void postRequestVolley(RequestQueue queue) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            Log.i("resonse", response.toString());
                            if (response.toString().equals("OK")) {
                                signUpSignInOption();
                                Toast.makeText(getApplicationContext(),"SignIn successful",Toast.LENGTH_SHORT).show();
                                // Display the first 500 characters of the response string.
                            } else {
                                Log.i("info","username or password invalid");
                                Toast.makeText(getApplicationContext(), "Username or Password is not valid!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Username or Password is not valid!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("info", "That didn't work!"+ error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> User = new HashMap<String, String>();
                User.put("username", user_name);
                //Toast.makeText(getApplicationContext(),user_name,Toast.LENGTH_SHORT).show();
                Log.i("user_name",user_name);
                User.put("password", password.getText().toString());
                return User;
            }
        };

        queue.add(stringRequest);
// Add the request to the RequestQueue.

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i){
            case 0:
                user_name="nicdrone";
                break;
            case 1:
                user_name="nicpulchowk";
                break;
            case 2:
                user_name="nicnangi";
                break;
            case 3:
                user_name="nicfusedrone";
                break;
            case 4:
                user_name="airbus";
                break;
            case 5:
                user_name="boeing";
                break;
            case 6:
                user_name="jet";

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

