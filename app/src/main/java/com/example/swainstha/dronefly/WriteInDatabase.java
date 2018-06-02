package com.example.swainstha.dronefly;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by swainstha on 6/2/18.
 */

public class WriteInDatabase extends AsyncTask<Object, Void, Boolean> {

    StatusDbHelper mDbHelper;
    @Override
    protected Boolean doInBackground(Object... objects) {

        mDbHelper = new StatusDbHelper((Context)objects[1]);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        JSONObject data = (JSONObject)objects[0];
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        try {
            values.put("lat",data.getDouble("lat"));
            values.put("lng",data.getDouble("lng"));
            values.put("alt",data.getDouble("alt"));
            values.put("head",data.getInt("head"));
            values.put("arm",data.getBoolean("arm"));
            values.put("status",data.getString("status"));

        } catch(JSONException j) {
            Log.i("INFO","Error parsing received data");
        }


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(StatusSave.StatusEntry.TABLE_NAME, null, values);
        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mDbHelper.close();

    }

}
