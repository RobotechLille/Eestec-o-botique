package me.robotech.eestecobotique;

import android.os.Handler;

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
            double limit = 1d;
            if(MainActivity.limiteur != null)
                limit = ((double) MainActivity.limiteur.getProgress()) / 100d;
            int gauche = (int) ((((double) caller.gauche) - 127d) * limit) + 127;
            int droite = (int) ((((double) caller.droite) - 127d) * limit) + 127;
            try {
                MainActivity.socket.getOutputStream().write(toByte(gauche, droite));
            } catch (Exception ignored) {}

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
        else if(gauche > 0)
            gauche = 0;

        if(droite > 255)
            droite = 255;
        else if(droite > 0)
            droite = 0;

        return new byte[]{(byte) gauche, (byte) droite};
    }
}
