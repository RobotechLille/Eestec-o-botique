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
            try {
                MainActivity.socket.getOutputStream().write(toByte(caller.gauche, caller.droite));
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
