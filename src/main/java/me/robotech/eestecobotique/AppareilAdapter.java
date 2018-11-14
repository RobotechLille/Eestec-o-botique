package me.robotech.eestecobotique;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class AppareilAdapter extends ArrayAdapter<BluetoothDevice> {
    private Context context;
    private ArrayList<BluetoothDevice> appareils;

    AppareilAdapter(ArrayList<BluetoothDevice> appareils, Context context){
        super(context, R.layout.device_item, appareils);
        this.context = context;
        this.appareils = appareils;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false);

        if(appareils.size() <= position)
            return listItem;
        BluetoothDevice appareil = appareils.get(position);
        if(appareil == null)
            return listItem;

        TextView nom = listItem.findViewById(R.id.appareil_nom);
        TextView mac = listItem.findViewById(R.id.appareil_mac);

        nom.setText(appareil.getName());
        mac.setText(appareil.getAddress());

        return listItem;
    }

    public BluetoothDevice getAppareil(int position){
        if(position < 0 || position >= appareils.size())
            return null;
        return appareils.get(position);
    }

    public void add(BluetoothDevice nouveau) {
        // anti-doublon
        for(BluetoothDevice vieu : appareils){
            if(nouveau.getAddress().equals(vieu.getAddress()))
                return;
        }
        appareils.add(nouveau);
        notifyDataSetChanged();
    }

    public static ArrayList<BluetoothDevice> setToArrayList(Set<BluetoothDevice> appareilsSet){
        ArrayList<BluetoothDevice> appareilsArray = new ArrayList<>();
        appareilsArray.addAll(appareilsSet);
        return appareilsArray;
    }
}
