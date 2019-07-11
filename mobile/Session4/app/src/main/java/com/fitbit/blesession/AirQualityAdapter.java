package com.fitbit.blesession;

import com.fitbit.blesession.data.entities.AirQualityEntity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AirQualityAdapter extends RecyclerView.Adapter<AirQualityAdapter.ViewHolder> {
    private final Context context;
    private  List<AirQualityEntity> entities;

    private int co2Threshold;
    private int vocThreshold;

    AirQualityAdapter(Context context, List<AirQualityEntity> entities, int co2Threshold, int vocThreshold) {
        this.context = context;
        this.entities = entities;
        this.co2Threshold = co2Threshold;
        this.vocThreshold = vocThreshold;
    }

    @Override
    public AirQualityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.air_quality_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AirQualityAdapter.ViewHolder holder, int position) {
        AirQualityEntity entity = entities.get(position);
        holder.deviceTv.setText(String.format(
            context.getString(R.string.device_entry_value), entity.getDeviceId()
        ));
        int co2 = entity.getCo2();
        int voc = entity.getVoc();

        holder.co2Tv.setText(
            String.format(context.getString(R.string.co2_entry_value), co2)
        );

        holder.vocTv.setText(
            String.format(context.getString(R.string.voc_entry_value), voc)
        );

        holder.co2Tv.setTextColor(co2 > co2Threshold ? Color.RED : Color.GREEN);
        holder.vocTv.setTextColor(voc > vocThreshold ? Color.RED : Color.GREEN);

    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    public void setEntities(List<AirQualityEntity> list) {
        entities = list;
        notifyDataSetChanged();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceTv;
        TextView co2Tv;
        TextView vocTv;

        ViewHolder(View itemView) {
            super(itemView);
            deviceTv = itemView.findViewById(R.id.tv_device);
            co2Tv = itemView.findViewById(R.id.tv_co2);
            vocTv = itemView.findViewById(R.id.tv_voc);
        }
    }

}
