package com.kesari.tkfops.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.kesari.tkfops.BikerDashboard.BikerDashboardActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.SelectLogin.SelectLoginActivity;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.VehicleDashboard.VehicleDashboardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(3000);

                    startApp();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    public void startApp()
    {
        if (SharedPrefUtil.getToken(SplashActivity.this) != null) {
            if(!SharedPrefUtil.getToken(SplashActivity.this).isEmpty())
            {

                if (SharedPrefUtil.getKeyLoginType(SplashActivity.this) != null) {

                    if(SharedPrefUtil.getKeyLoginType(SplashActivity.this).equalsIgnoreCase("Vehicle"))
                    {
                        Intent startMainActivity = new Intent(getApplicationContext(),VehicleDashboardActivity.class);
                        startActivity(startMainActivity);
                        finish();
                    }
                    else if(SharedPrefUtil.getKeyLoginType(SplashActivity.this).equalsIgnoreCase("Biker"))
                    {
                        Intent startMainActivity = new Intent(getApplicationContext(),BikerDashboardActivity.class);
                        startActivity(startMainActivity);
                        finish();
                    }
                }
            }
            else
            {
                Intent startMainActivity = new Intent(getApplicationContext(),SelectLoginActivity.class);
                startActivity(startMainActivity);
                finish();
            }
        }
        else
        {
            Intent startMainActivity = new Intent(getApplicationContext(),SelectLoginActivity.class);
            startActivity(startMainActivity);
            finish();
        }
    }
}
