package com.arkas.smarthomecontroller;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.arkas.smarthomecontroller.fragments.ColorpickerFragment;
import com.arkas.smarthomecontroller.fragments.EffectsFragment;
import com.arkas.smarthomecontroller.fragments.SettingsFragment;
import com.arkas.smarthomecontroller.fragments.StateFragment;
import com.arkas.smarthomecontroller.mqtt.MqttHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StateFragment.OnListFragmentInteractionListener {

    private int currentFragmentId = 0;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //make sure this gets called only once
        MqttHelper.init(getApplicationContext());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        navigationView.setCheckedItem(R.id.nav_colorpicker);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onNavigationItemSelected(navigationView.getMenu().getItem(3));
            navigationView.setCheckedItem(R.id.nav_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if(id != currentFragmentId){
            if (id == R.id.nav_colorpicker) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, new ColorpickerFragment()).commit();
            } else if (id == R.id.nav_effects) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, new EffectsFragment()).commit();
            } else if (id == R.id.nav_state) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, new StateFragment()).commit();
            } else if (id == R.id.nav_settings) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, new SettingsFragment()).commit();
            }
            currentFragmentId = id;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(LedListItem item) {

    }
}
