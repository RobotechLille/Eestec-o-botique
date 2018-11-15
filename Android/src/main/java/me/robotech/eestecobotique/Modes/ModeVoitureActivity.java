package me.robotech.eestecobotique.Modes;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import me.robotech.eestecobotique.ConnectionBluetooth;
import me.robotech.eestecobotique.R;

public class ModeVoitureActivity extends ModeActivity {
    private SeekBar direction;
    private boolean acceleration;
    private boolean freinage;

    private ConnectionBluetooth connectionBluetooth;

    private static final double ROUE_LIBRE = -25; // par seconde
    private static final double ACCELERATION = 100; // par secodne
    private static final double FREINAGE = -100; // par seconde
    private double vitesse = 0;

    private boolean go;
    private static final int DELAI_PHYSIQUE_MS = ConnectionBluetooth.DELAI_MS;
    private Handler physiqueHandler;
    private Runnable physique = new Runnable() {
        @Override
        public void run() {
            if(freinage){ // freinage
                vitesse += FREINAGE * ((double) DELAI_PHYSIQUE_MS) / 1000d;
            }else if(acceleration){ // acceleration
                vitesse += ACCELERATION * ((double) DELAI_PHYSIQUE_MS) / 1000d;
            }else{ // roue libre
                vitesse += ROUE_LIBRE * ((double) DELAI_PHYSIQUE_MS) / 1000d;
            }

            // min max
            if(vitesse > 127)
                vitesse = 127;
            else if(vitesse < 0)
                vitesse = 0;

            // direction
            double dir = direction.getProgress();
            if(dir == 50){
                droite = (int) (vitesse + 128d);
                gauche = (int) (vitesse + 128d);
            }else if(dir > 50){ // droiteSeekBar
                droite = (int) (vitesse * ((100d - dir) / 50d)) + 128;
                gauche = (int) (vitesse + 128d);
            }else{ // gaucheSeekBar
                droite = (int) (vitesse + 128d);
                gauche = (int) (vitesse * (dir / 50d)) + 128;
            }

            // repeter
            if(go)
                physiqueHandler.postDelayed(this, DELAI_PHYSIQUE_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_voiture);

        direction = findViewById(R.id.direction);
        final ImageView accelerateur = findViewById(R.id.accelerateur);
        ImageView frein = findViewById(R.id.frein);

        direction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                direction.setProgress(50);
            }
        });
        //noinspection AndroidLintClickableViewAccessibility
        accelerateur.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    acceleration = true;
                if(event.getAction() == MotionEvent.ACTION_UP)
                    acceleration = false;
                return true;
            }
        });
        //noinspection AndroidLintClickableViewAccessibility
        frein.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    freinage = true;
                if(event.getAction() == MotionEvent.ACTION_UP)
                    freinage = false;
                return true;
            }
        });

        connectionBluetooth = new ConnectionBluetooth(this);

        go = true;
        physiqueHandler = new Handler();
        physiqueHandler.postDelayed(physique, DELAI_PHYSIQUE_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        go = false;
        connectionBluetooth.stop();
    }
}
