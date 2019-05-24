package com.sny.tangyong.common.view.engine;

import android.app.Activity;


public class AbsAutoSurfaceAcvitiy extends Activity {

    protected AutoSurfaceView view;

    @Override
    protected void onResume() {
        super.onResume();
        if (view != null) {
            view.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (view != null) {
            view.pause();
        }
    }
}
