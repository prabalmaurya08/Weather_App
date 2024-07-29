package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }


    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);
        //for temperature
        holder.TemperatureTV.setText(model.getTemperature()+"°C");
        //for conditon we will get url from API
        Picasso.get().load("http".concat(model.getIcon())).into(holder.ConditionIV);
        //for wind
        holder.WindTV.setText(model.getWindspeed()+"Km/h");
        //for time
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(model.getTime());
            holder.TimeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }


    }
    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TemperatureTV, TimeTV, WindTV;
        private ImageView ConditionIV;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            TemperatureTV = itemView.findViewById(R.id.idTVTemperature);
            TimeTV = itemView.findViewById(R.id.idTVTime);
            WindTV = itemView.findViewById(R.id.idTVWindspeed);
            ConditionIV = itemView.findViewById(R.id.idTVCondition);
        }
    }
}
