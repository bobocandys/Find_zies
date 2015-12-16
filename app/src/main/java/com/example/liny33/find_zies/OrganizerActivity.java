package com.example.liny33.find_zies;

import android.os.Bundle;
import android.view.Menu;

/**
 * Created by liny33 on 12/15/15.
 */
public class OrganizerActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        joinMenuItem.setEnabled(false);
        return true;
    }
}
