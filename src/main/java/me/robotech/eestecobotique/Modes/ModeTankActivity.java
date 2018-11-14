package me.robotech.eestecobotique.Modes;

import android.os.Bundle;
import android.widget.SeekBar;

import me.robotech.eestecobotique.ConnectionBluetooth;
import me.robotech.eestecobotique.R;

public class ModeTankActivity extends ModeActivity {
   private ConnectionBluetooth connectionBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_tank);

        final SeekBar gaucheSeekBar = findViewById(R.id.gauche);
        final SeekBar droiteSeekBar = findViewById(R.id.droite);

        gaucheSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gauche = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gaucheSeekBar.setProgress(0x7F);
                gauche = 0x7F;
            }
        });
        droiteSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                droite = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                droiteSeekBar.setProgress(0x7F);
                droite = 0x7F;
            }
        });

        connectionBluetooth = new ConnectionBluetooth(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionBluetooth.stop();
    }
}
