package dev.ky3he4ik.battleship;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useAccelerometer = false;
        config.useCompass = false;
//		config.useGLSurfaceView20API18 = android.os.Build.VERSION.SDK_INT < 10;

        initialize(new MyGdxGame(new PlatformSpecificAndroid(getContext())), config);
    }
}
