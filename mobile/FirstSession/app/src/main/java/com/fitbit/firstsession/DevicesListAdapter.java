package com.fitbit.firstsession;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Binds the devices data to the views displayed in the RecyclerView.
 * <br>
 * When you modify the data, you only need to notify this adapter using the notify* method specific
 * to your change.
 */
public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.ViewHolder> {

    private List<String> devicesList;
    private LayoutInflater layoutInflater;

    DevicesListAdapter(Context context, List<String> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.devicesList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.devices_list_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data to the UI element of each row, in this case a TextView for displaying
     * the device information.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String deviceInfo = devicesList.get(position);
        holder.myTextView.setText(deviceInfo);
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.device_info);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // do nothing in our case
        }
    }

}
