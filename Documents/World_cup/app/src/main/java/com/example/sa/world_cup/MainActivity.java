package com.example.sa.world_cup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {


    ImageView imageView;
    EditText username;
    EditText password;
    Button sign;

    private Socket socket;
    private String urlString="http://192.168.1.119:3001";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            socket = IO.socket(urlString);
            socket.connect();
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

        socket.emit("username",username.getText().toString() );
        //socket.emit("password",password.getText().toString());
    }
}
