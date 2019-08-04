package com.akashd50.lb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akashd50.lb.R;
import com.akashd50.lb.logic.TouchController;
import com.akashd50.lb.objects.BoardData;
import com.akashd50.lb.objects.Button;

import org.w3c.dom.Text;

public class MainGameActivity extends Activity {
    private MainGameSurfaceView glRendererView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.gl_surface_view_layout);

        int bid = getIntent().getIntExtra("bid",-1);
        String name = getIntent().getStringExtra("name");
        int x = getIntent().getIntExtra("bx",21);
        int y = getIntent().getIntExtra("by",21);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        glRendererView = new MainGameSurfaceView(getApplication(), bid, name, x, y);
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
    }
}
