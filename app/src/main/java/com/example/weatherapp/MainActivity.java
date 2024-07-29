package com.example.weatherapp;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private ImageView iconIV,backIV,searchIV;
    private TextView citynameTV,conditionTV,temperatureTV;
    private TextInputEditText cityedt;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    //to grant permission we use
    private LocationManager locationManager;
    private int  PERMISSION_CODE=1;
    private String cityname;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to make our application full screen we use
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL=findViewById(R.id.idRLHome);
        loadingPB=findViewById(R.id.idPBloading);
        iconIV=findViewById(R.id.idIVIcon);
        searchIV=findViewById(R.id.idIVsearch);
       weatherRV=findViewById(R.id.idRVWeather);
        citynameTV=findViewById(R.id.idTVCityname);
        conditionTV=findViewById(R.id.idTVCondition);
       temperatureTV=findViewById(R.id.idTVTemperature);
        cityedt=findViewById(R.id.idEdtCity);
        backIV=findViewById(R.id.idIVBack);
        weatherRVModelArrayList=new ArrayList<>();
        weatherRVAdapter=new WeatherRVAdapter(this,weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        // to check whether the user has granted the Location
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //this method will provide a city name
        assert location != null;
        cityname=getCityName(location.getLongitude(),location.getLatitude());
        getweatherinfo(cityname);
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city= Objects.requireNonNull(cityedt.getText()).toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter City name", Toast.LENGTH_SHORT).show();
                }
                else{
                    citynameTV.setText(cityname);
                    getweatherinfo(cityname);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // to know city name we have to give longitude and latitude
    private String getCityName(double longi,double lati){
        String Cityname="Not found";
        Geocoder gcd =new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses=gcd.getFromLocation(longi,lati,10);

            assert addresses != null;
            for(Address adr:addresses){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null && !city.equals("")){
                        Cityname=city;
                    }else{
                        Log.d("TAG", "City Not Found ");
                        Toast.makeText(this, "User city not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return Cityname;

    }
    private void getweatherinfo(String cityname){
        String url="http://api.weatherapi.com/v1/forecast.json?key=5172ce180e3f41f5a9e211914230208&q="+cityname+"&days=1&aqi=yes&alerts=yes";
        citynameTV.setText(cityname);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();
                try{
                    String tempertaure=response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(tempertaure+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String conditon=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    conditionTV.setText(conditon);
                    if(isDay==1){//morning
                        Picasso.get().load("https://images.unsplash.com/photo-1571080648416-3fda23702c51?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1887&q=80").into(backIV);
                    }
                    else{
                        //night
                        Picasso.get().load("https://images.unsplash.com/photo-1505322022379-7c3353ee6291?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1500&q=80").into(backIV);
                    }
                    JSONObject forecastobj=response.getJSONObject("forecast");
                    JSONObject forecast0=forecastobj.getJSONArray("forecastday").getJSONObject(0);

                    JSONArray hourarray=forecast0.getJSONArray("hour");
                    for(int i=0;i<hourarray.length();i++){
                        JSONObject hourobj=hourarray.getJSONObject(i);
                        String time=hourobj.getString("time");
                        String temper=hourobj.getString("temp_c");

                        String img=hourobj.getJSONObject("condition").getString("icon");

                        String wind=hourobj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temper,img,wind));

                    }
                    weatherRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Enter valid City", Toast.LENGTH_SHORT).show();
            }
        });


    }
}