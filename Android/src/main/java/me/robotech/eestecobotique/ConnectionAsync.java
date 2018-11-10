package me.robotech.eestecobotique;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class ConnectionAsync extends AsyncTask<Void, Void, Boolean> {
    private MainActivity mainActivity;
    private BluetoothDevice appareil;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;

    ConnectionAsync(MainActivity mainActivity, BluetoothDevice appareil, BluetoothAdapter bluetoothAdapter){
        this.mainActivity = mainActivity;
        this.appareil = appareil;
        this.bluetoothAdapter = bluetoothAdapter;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        mainActivity.changeStatus("connection en cours...", true, false);
        bluetoothAdapter.cancelDiscovery();
        try {
            BluetoothSocket socket = appareil.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            this.socket = socket;
            return socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean connecte) {
        if(connecte) {
            MainActivity.socket = this.socket;
            mainActivity.changeStatus("connecté à " + appareil.getAddress(), false, true);
        }else{
            MainActivity.socket = null;
            mainActivity.changeStatus("connection échoué à " + appareil.getAddress(), false, false);
            bluetoothAdapter.startDiscovery();
        }
    }
}
