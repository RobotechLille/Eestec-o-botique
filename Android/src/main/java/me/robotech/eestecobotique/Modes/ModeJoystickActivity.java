package me.robotech.eestecobotique.Modes;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import me.robotech.eestecobotique.ConnectionBluetooth;
import me.robotech.eestecobotique.R;

public class ModeJoystickActivity extends ModeActivity {
    private static final double density = Resources.getSystem().getDisplayMetrics().density;

    private ConnectionBluetooth connectionBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_joystick);

        RelativeLayout joystickPanel = findViewById(R.id.joystickPanel);
        final ImageView joystick = findViewById(R.id.joystick);

        //noinspection AndroidLintClickableViewAccessibility
        joystickPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        joystick.setLayoutParams(getParams(100 * density, 100 * density));
                        majGaucheDroite(100 * density, 100 * density);
                        break;
                    default:
                        joystick.setLayoutParams(getParams(event.getX(), event.getY()));
                        majGaucheDroite(event.getX(), event.getY());
                        break;
                }
                return true;
            }
        });

        connectionBluetooth = new ConnectionBluetooth(this);
    }

    private static RelativeLayout.LayoutParams getParams(double x, double y){
        // taille du joystick
        x -= 25 * density;
        y -= 25 * density;

        // bordures
        if(x < 0)
            x = 0;
        else if(x > 150 * density) // 200 - 50
            x = 150 * density;
        if(y < 0)
            y = 0;
        else if(y > 150 * density) // 200 - 50
            y = 150 * density;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) x, (int) y, 0, 0);
        return params;
    }

    private void majGaucheDroite(double x, double y){
        x /= density;
        y /= density;

        // taille du joystick
        x -= 25;
        y -= 25;

        // bordures
        if(x < 0)
            x = 0;
        else if(x > 150) // 200 - 50
            x = 150;
        if(y < 0)
            y = 0;
        else if(y > 150) // 200 - 50
            y = 150;

        x = (x / 75d) - 1d;
        y = (y / 75d) - 1d;

        // cadran 1
        double g = 0;
        double d = 0;
        if(x <= 0 && y <= 0){
            g = x - y;
            if(x >= y)
                d = -y;
            else
                d = -x;
        }
        // cadran 2
        else if(x < 0 && y > 0){
            if(-x >= y){
                g = y + x;
                d = -x - 2*y;
            }else{
                g = -x - y;
                d = -y;
            }
        }
        // cadran 3
        else if(x > 0 && y < 0){
            d = -x - y;
            if(x <= -y)
                g = -y;
            else
                g = x;
        }
        // cadran 4
        else if(x > 0 && y > 0){
            if(x >= y){
                g = x - 2*y;
                d = -x + y;
            }else{
                g = -y;
                d = -y + x;
            }
        }
        gauche = (int) ((g * 127) + 128);
        droite = (int) ((d * 127) + 128);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionBluetooth.stop();
    }
}
