package me.robotech.eestecobotique.Modes;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import me.robotech.eestecobotique.ConnectionBluetooth;
import me.robotech.eestecobotique.R;

public class ModeVaisseauActivity extends ModeActivity {
    private int direction = 50;
    private int puissance = 127;
    private ConnectionBluetooth connectionBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_vaisseau);

        final SeekBar directionSeekBar = findViewById(R.id.direction);
        final SeekBar puissanceSeekBar = findViewById(R.id.puissance);

        directionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                direction = progress;
                calculGaucheDroite();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                directionSeekBar.setProgress(50);
                direction = 50;
                calculGaucheDroite();
            }
        });
        puissanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                puissance = progress;
                calculGaucheDroite();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                puissanceSeekBar.setProgress(127);
                puissance = 127;
                calculGaucheDroite();
            }
        });

        connectionBluetooth = new ConnectionBluetooth(this);
    }

    private void calculGaucheDroite(){
        if(direction == 50){
            droite = puissance;
            gauche = puissance;
        }else if(direction > 50){ // droiteSeekBar
            droite = (int) ((double) puissance * ((100d - direction) / 50d));
            gauche = puissance;
        }else{ // gaucheSeekBar
            droite = puissance;
            gauche = (int) ((double) puissance * (direction / 50d));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionBluetooth.stop();
    }
}
