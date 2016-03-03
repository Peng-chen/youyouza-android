package com.youyouza.pedmeter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button startBtn;
    private Button stopBtn;
    private TextView showMessageTev;
    private TextView showPedmeterTev;
    private static final String stringExtraCalc="data";
    private static final String stringExtraPedemeter="Pedemeter";


    private static  final String BROADCAST_ACTION_DATA="com.youyouza.updateUI.data";
    private static  final String BROADCAST_ACTION_PEDEMETER="com.youyouza.updateUI.pedemeter";



    private MyBroadcastReceiver myBroadcastReceiver;
    private MyBroadcastReceiver myBroadcastReceiver_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //content to get button and add actionListener

         showMessageTev=(TextView)findViewById(R.id.showMessageTev);
        showPedmeterTev=(TextView)findViewById(R.id.showPedmeterTev);

        startBtn=(Button)findViewById(R.id.startBtn);
        stopBtn=(Button)findViewById(R.id.stopBtn);

        startBtn.setOnClickListener(listener);
        stopBtn.setOnClickListener(listener);



//        in this section register broadcastReceiver
//
//        IntentFilter filter_data=new IntentFilter(BROADCAST_ACTION_DATA);
//
//         myBroadcastReceiver_data=new MyBroadcastReceiver(stringExtraCalc,showMessageTev);
//
//        this.registerReceiver(myBroadcastReceiver_data, filter_data);


        IntentFilter filter=new IntentFilter(BROADCAST_ACTION_PEDEMETER);

         myBroadcastReceiver=new MyBroadcastReceiver(stringExtraPedemeter,showPedmeterTev);

        this.registerReceiver(myBroadcastReceiver, filter);


//     through  unregisterReceiver(myBroadcastReceiver);  to unregister




    }




    private View.OnClickListener listener=new View.OnClickListener(){


        @Override
        public void onClick(View v) {

            Log.v("in the mainActiviry","onclick it");

            Intent intent=new Intent(MainActivity.this,PedometerService.class);
            switch (v.getId()){

                case R.id.startBtn:
                    Log.v("startservice:","in the mainActivity,service start");
                startService(intent);break;
                case R.id.stopBtn:
                    Log.v("stopservice:","in the mainActivity,service stop");
                    stopService(intent);break;
                default:break;
            }

        }
    };







    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Setting  button in the MainActivity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.v("menu","onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //indicate  the setting button

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.v("menu","onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            //finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

            Intent intent = new Intent();
            intent.setClass(this, SettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(myBroadcastReceiver_data);
        super.onDestroy();

    }









    // in this section get broadcastreceiver


//    also update UI

    class MyBroadcastReceiver extends BroadcastReceiver{

        private String extra;
        private TextView textView;

        public MyBroadcastReceiver(String extra,TextView textView){
        this.extra=extra;
            this.textView=textView;

        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String message=intent.getStringExtra(extra);

//            showMessageTev.append(message);

            textView.setText(message);





        }
    }






}
