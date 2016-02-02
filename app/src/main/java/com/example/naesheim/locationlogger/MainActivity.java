package com.example.naesheim.locationlogger;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;


public class MainActivity extends Activity {

    Button btn;
    TextView res;
    private static final String TAG = "MyActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        res = (TextView) findViewById(R.id.txt1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gps;
                gps = new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()) {
                    double lat = gps.getLatitude();
                    double lon = gps.getLongitude();
                    Toast.makeText(getApplicationContext(), "lat" + lat + " lon" + lon, Toast.LENGTH_LONG).show();
                    UpdateDB udb = new UpdateDB();
                    udb.execute(lat, lon);
                    Log.d(TAG,"initiate");
                } else {
                    gps.showSettingsAlert();
                }

            }

        });
    }
            class UpdateDB extends AsyncTask<Double, JSONObject, String> {

                private URL url = null;
                String txt;

                protected String doInBackground(Double... args) {

                    double lat = args[0];
                    double lon = args[1];
                    Log.d(TAG,"starting do in background"+lat+lon);
                    JSONObject json = new JSONObject();
                    Date date = new java.util.Date();
                    Timestamp time = new java.sql.Timestamp(date.getTime());
                        try {
                            json.put("serial_no","my_phone");
                            json.put("time",time);
                            json.put("lat",lat);
                            json.put("lon",lon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    Log.d(TAG,"JSON object created"+json.toString());
                    publishProgress(json);
                    OutputStreamWriter writer;
                    BufferedReader reader;
                    HttpURLConnection urlConnection;

                    try {
                        url = new URL("http://hgnaesheim.com/LocationLogger/createInstance.php");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                        urlConnection.setRequestProperty("charset", "utf-8");
                        urlConnection.setUseCaches(false);
                        urlConnection.setConnectTimeout(10000);
                        Log.d(TAG,"Connection success");


                        writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        writer.write(json.toString());
                        writer.flush();
                        writer.close();

                        reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        reader.close();
                        txt = sb.toString();

                        } catch (IOException e) {

                        }

                    return txt;
                }

                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    res.setText(result);
                }

                protected void onProgressUpdate(JSONObject s){
                    super.onProgressUpdate(s);
                    Toast.makeText(getApplicationContext(),s.toString(),Toast.LENGTH_LONG);
                }
            }
}
