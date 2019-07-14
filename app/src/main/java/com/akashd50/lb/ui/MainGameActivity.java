package com.akashd50.lb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class MainGameActivity extends Activity {
    private MainGameSurfaceView glRendererView;
    public static Context context;
    public static MainGameActivity instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = MainGameActivity.this;
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        glRendererView = new MainGameSurfaceView(getApplication());
        setContentView(glRendererView);
    }

    @Override
    protected void onPause() {
        glRendererView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        glRendererView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        glRendererView.onBackPressed();
        finish();
        try {
            finalize();
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
}
