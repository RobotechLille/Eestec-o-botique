package me.robotech.eestecobotique;

import android.os.Handler;
import android.util.Log;

import me.robotech.eestecobotique.Modes.ModeActivity;

public class ConnectionBluetooth {
    private ModeActivity caller;

    private static final int DELAI_MS = 30;
    private boolean go;
    private Handler envoieHandler;
    private Runnable envoieDonnee = new Runnable() {
        @Override
        public void run() {
            // envoie les données
            double gauche = caller.gauche;
            double droite = caller.droite;

            // calibrage gauche-droite
            double calib = 0.5d;
            if(MainActivity.calibrage != null)
                calib = ((double) MainActivity.calibrage.getProgress()) / 100d;
            if(calib > 0.5d){ // - gauche
                calib = 1d - ((calib - 0.5d)); //<= calib = ((1d - ((calib - 0.5d) * 2d)) * 0.5d) + 0.5d;
                gauche = ((gauche - 127d) * calib) + 127d;
            }else{ // - droite
                calib = calib + 0.5d; //<= (calib * 2d * 0.5d) + 0.5d
                droite = ((droite - 127d) * calib) + 127d;
            }

            // limite de vitesse
            double limitVitesse = 1d;
            if(MainActivity.limiteurVitesse != null)
                limitVitesse = ((double) MainActivity.limiteurVitesse.getProgress()) / 100d;
            gauche = ((gauche - 127d) * limitVitesse) + 127d;
            droite = ((droite - 127d) * limitVitesse) + 127d;

            // limite de direction
            double limitDirection = 0d;
            if(MainActivity.limiteurDirection != null)
                limitDirection = 1d - (((double) MainActivity.limiteurDirection.getProgress()) / 100d);
            double dirGauche = droite * limitDirection;
            double dirDroite = gauche * limitDirection;
            if(gauche < dirGauche)
                gauche = dirGauche;
            if(droite < dirDroite)
                droite = dirDroite;

            Log.d("ConnectionBluetooth", String.valueOf("-----------"));
            Log.d("ConnectionBluetooth", String.valueOf(gauche));
            Log.d("ConnectionBluetooth", String.valueOf(droite));

            try {
                MainActivity.socket.getOutputStream().write(toByte((int) gauche, (int) droite));
            } catch (Exception e) {
                if(!(e instanceof NullPointerException))
                    e.printStackTrace();
            }

            // repeter
            if(go)
                envoieHandler.postDelayed(envoieDonnee, DELAI_MS);
        }
    };

    public ConnectionBluetooth(ModeActivity caller){
        this.caller = caller;

        this.go = true;
        envoieHandler = new Handler();
        envoieHandler.postDelayed(envoieDonnee, DELAI_MS);
    }

    public void stop(){this.go = false;}

    private byte[] toByte(int gauche, int droite){
        if(gauche > 255)
            gauche = 255;
        else if(gauche < 0)
            gauche = 0;

        if(droite > 255)
            droite = 255;
        else if(droite < 0)
            droite = 0;

        return new byte[]{(byte) gauche, (byte) droite};
    }
}
