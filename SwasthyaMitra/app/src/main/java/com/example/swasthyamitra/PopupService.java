package com.example.swasthyamitra;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class    PopupService extends Service {

    private WindowManager windowManager;
    private LinearLayout popupLayout;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Inflate your popup layout
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupLayout = (LinearLayout) layoutInflater.inflate(R.layout.popup_menu_layout, null);

        // Set up layout parameters
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Important for system-level popup
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        // Add the view to the window
        windowManager.addView(popupLayout, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popupLayout != null) {
            windowManager.removeView(popupLayout);
        }
    }
}
