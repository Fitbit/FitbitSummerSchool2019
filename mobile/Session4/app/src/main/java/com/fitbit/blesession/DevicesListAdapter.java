package com.fitbit.blesession;

import android.bluetooth.BluetoothDevice;
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
    private List<BluetoothDevice> devicesList;
    private LayoutInflater layoutInflater;
    private RecyclerView recyclerView;
    private ItemClickListener itemClickListener;

    DevicesListAdapter(Context context, List<BluetoothDevice> bluetoothDevices) {
        this.layoutInflater = LayoutInflater.from(context);
        this.devicesList = bluetoothDevices;
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
        BluetoothDevice bluetoothDevice = devicesList.get(position);
        String str = String.format("%s %s", bluetoothDevice.getName(), bluetoothDevice.getAddress());
        holder.myTextView.setText(str);
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
            if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
