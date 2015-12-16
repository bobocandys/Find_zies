package com.example.liny33.find_zies;

import android.os.Bundle;
import android.app.Activity;


public class Bridge extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge);

        home homeFragment = new home();
        getFragmentManager().beginTransaction().replace(R.id.bb, homeFragment).commit();
    }
}
