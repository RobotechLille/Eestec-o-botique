package me.robotech.eestecobotique;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import me.robotech.eestecobotique.Modes.ModeJoystickActivity;
import me.robotech.eestecobotique.Modes.ModeTankActivity;
import me.robotech.eestecobotique.Modes.ModeVaisseauActivity;
import me.robotech.eestecobotique.Modes.ModeVoitureActivity;

/**TODO list :
 *  - rien
 */
public class MainActivity extends AppCompatActivity {
    private static final int BLUETOOTH_ON_REQUEST = 21;

    private BluetoothAdapter bluetoothAdapter;
    private AppareilAdapter appareilAdapter;
    private ListView appareilListView;
    private TextView status;
    private ProgressBar progressBar;

    public static SeekBar limiteur;
    public static BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // bluetooth supporté ?
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // bluetooth non supporté
            setContentView(R.layout.activity_main_no_bluetooth);
            return;
        }
        // bluetooth supporté
        setContentView(R.layout.activity_main);

        appareilListView = findViewById(R.id.liste_appareils);
        status = findViewById(R.id.status);
        progressBar = findViewById(R.id.progressBar);
        limiteur = findViewById(R.id.limiteur);

        // bluetooth inactif ?
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ON_REQUEST);
            return;
        }

        suiteInit();
    }

    private void suiteInit(){
        // construit la liste
        ArrayList<BluetoothDevice> appareilsJumle = AppareilAdapter.setToArrayList(bluetoothAdapter.getBondedDevices());
        appareilAdapter = new AppareilAdapter(appareilsJumle, this);
        appareilListView.setAdapter(appareilAdapter);

        // liste listener
        appareilListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice appareil = appareilAdapter.getAppareil(position);
                new ConnectionAsync(MainActivity.this, appareil, bluetoothAdapter).execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == BLUETOOTH_ON_REQUEST) {
            if (resultCode != RESULT_OK) {
                // impossible de démarrer le bluetooth
                // affiche une erreur qui ferme l'application à sa fermeture
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Impossible de démarrer le bluetooth")
                        .setMessage("Le bluetooth ne c'est pas activé.\nAutorisez l'application à utiliser le bluetooth pour continuer.")
                        .setCancelable(false)
                        .setPositiveButton("FERMER L'APPLICATION", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                System.exit(1);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }else{
                suiteInit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.tank:
                intent = new Intent(this, ModeTankActivity.class);
                startActivity(intent);
                return true;
            case R.id.voiture:
                intent = new Intent(this, ModeVoitureActivity.class);
                startActivity(intent);
                return true;
            case R.id.joystick:
                intent = new Intent(this, ModeJoystickActivity.class);
                startActivity(intent);
                return true;
            case R.id.vaisseau:
                intent = new Intent(this, ModeVaisseauActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void changeStatus(final String text, final boolean indeterminate, final boolean progress){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Status : " + text);
                progressBar.setIndeterminate(indeterminate);
                progressBar.setProgress(progress ? 100 : 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception ignored) {}
        socket = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // actualise la liste des appareils
        ArrayList<BluetoothDevice> appareilsJumle = AppareilAdapter.setToArrayList(bluetoothAdapter.getBondedDevices());
        appareilAdapter = new AppareilAdapter(appareilsJumle, this);
        appareilListView.setAdapter(appareilAdapter);
        // test la connection
        if(socket == null || !socket.isConnected())
            changeStatus("non connecté", false, false);
    }
}
