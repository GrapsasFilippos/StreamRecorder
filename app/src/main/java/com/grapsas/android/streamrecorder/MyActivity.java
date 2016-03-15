package com.grapsas.android.streamrecorder;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        this.getApplication().registerActivityLifecycleCallbacks( App.getInstance() );
    }
}
