package com.example.sa.world_cup_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText username;
    EditText password;
    Button sign;
    JSONObject user_detail= new JSONObject();

    private Socket socket;
    private String urlString="http://192.168.1.119:3001";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            socket = IO.socket(urlString);
            socket.connect();
            socket.on("success", new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                    Log.i("INFO", args[0].toString());
                    final String res = args[0].toString();
                    if(res.equals("True"))
                    {
                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);
                        Log.i("info","new activity started, logged in");
                        startActivity(i);

                    }

                }


            });
        }
        catch (Exception e)
        {
            Log.i("error",e.toString());
        }

        imageView= (ImageView) findViewById(R.id.imageView);
        username= (EditText) findViewById(R.id.editText);
        password= (EditText) findViewById(R.id.editText2);
        sign= (Button) findViewById(R.id.button);

    }

    public void signUpORLogIn(View view){
        try {
            user_detail.put("username", username.getText().toString());
            user_detail.put("password",password.getText().toString());

            socket.emit("login",user_detail);
            //socket.emit("joinAndroid","1");

            //Toast.makeText(getApplicationContext(),"socket",Toast.LENGTH_LONG).show();


        }
        catch (Exception e)
        {
            Log.i("error",e.toString());
        }

        //socket.emit("password",password.getText().toString());
    }
}


