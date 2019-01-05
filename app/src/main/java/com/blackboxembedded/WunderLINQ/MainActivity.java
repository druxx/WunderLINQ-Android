package com.blackboxembedded.WunderLINQ;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "MainActivity";

    private ActionBar actionBar;
    private ImageButton backButton;
    private ImageButton forwardButton;
    private ImageButton otherButton;
    private ImageButton dataButton;
    private ImageButton faultButton;
    private ImageButton btButton;
    private TextView navbarTitle;

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView1Label;
    private TextView textView2Label;
    private TextView textView3Label;
    private TextView textView4Label;
    private TextView textView5Label;
    private TextView textView6Label;
    private TextView textView7Label;
    private TextView textView8Label;
    private TextView textViewAppName;

    private SharedPreferences sharedPrefs;

    static boolean hasSensor = false;
    static boolean itsDark = false;
    private long darkTimer = 0;
    private long lightTimer = 0;

    private static Context mContext;

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private Intent gattServiceIntent;
    public static BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public static BluetoothGattCharacteristic gattCommandCharacteristic;
    List<BluetoothGattCharacteristic> gattCharacteristics;
    private String mDeviceAddress;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SETTINGS_CHECK = 10;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public final static UUID UUID_MOTORCYCLE_SERVICE =
            UUID.fromString(GattAttributes.WUNDERLINQ_SERVICE);

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 100;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 101;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 102;
    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 112;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 122;
    private PopupMenu mPopupMenu;
    private PopupMenu mOtherMenu;
    private Menu otherMenu;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"In onCreate");

        AppUtils.adjustDisplayScale(this, getResources().getConfiguration());

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orientation = sharedPrefs.getString("prefOrientation", "0");
        if (!orientation.equals("0")){
            if(orientation.equals("1")){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (orientation.equals("2")){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        View view;
        if (sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
            setContentView(R.layout.activity_main_other);
            view = findViewById(R.id.layout_main_other);
        } else {
            setContentView(R.layout.activity_main);
            view = findViewById(R.id.layout_main);
        }

        view.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                Intent backIntent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(backIntent);
            }
            @Override
            public void onSwipeRight() {
                Intent backIntent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(backIntent);
            }
        });

        mContext = this;

        showActionBar();

        if (((MyApplication) this.getApplication()).getitsDark() || sharedPrefs.getString("prefNightModeCombo", "0").equals("1")){
            updateColors(true);
        } else {
            updateColors(false);
        }
        mHandler = new Handler();

        // Sensor Stuff
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.toast_ble_not_supported, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Brand: " + Build.BRAND);
            Log.d(TAG, "Device: " + Build.DEVICE);
            //Only quit on real device
            if(!(Build.BRAND.startsWith("Android") && Build.DEVICE.startsWith("generic"))) {
                finish();
            } else {
                Log.d(TAG,"Running in the emulator");
            }
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.toast_error_bluetooth_not_supported, Toast.LENGTH_LONG).show();

            Log.d(TAG, "Brand: " + Build.BRAND);
            Log.d(TAG, "Device: " + Build.DEVICE);
            //Only quit if on a real device
            if(!(Build.BRAND.startsWith("Android") && Build.DEVICE.startsWith("generic"))){
                finish();
            } else {
                Log.d(TAG,"Running in the emulator");
            }

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check Read Contacts permissions
            if (this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.contacts_alert_title));
                builder.setMessage(getString(R.string.contacts_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
                    }
                });
                builder.show();
            }
            // Check Camera permissions
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.camera_alert_title));
                builder.setMessage(getString(R.string.camera_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    }
                });
                builder.show();
            }
            // Check Call phone permissions
            if (this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.call_alert_title));
                builder.setMessage(getString(R.string.call_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                    }
                });
                builder.show();
            }
            // Check Read Audio permissions
            if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.record_audio_alert_title));
                builder.setMessage(getString(R.string.record_audio_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
                    }
                });
                builder.show();
            }
            // Check Write permissions
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.write_alert_title));
                builder.setMessage(getString(R.string.write_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_STORAGE);
                    }
                });
                builder.show();
            }
            // Check Location permissions
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.location_alert_title));
                builder.setMessage(getString(R.string.location_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
                    }
                });
                builder.show();
            }
            // Check overlay permissions
            if (!Settings.canDrawOverlays(this)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.overlay_alert_title));
                builder.setMessage(getString(R.string.overlay_alert_body));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(23)
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }
        // Check read notification permissions
        if (Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners") == null
                || !Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.notification_alert_title));
            builder.setMessage(getString(R.string.notification_alert_body));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(23)
                public void onDismiss(DialogInterface dialog) {
                    startActivity(new Intent(
                            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
            builder.show();
        }
        //Check usage stats permissions
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, myUid(), this.getPackageName());
        if (mode != MODE_ALLOWED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.usagestats_alert_title));
            builder.setMessage(getString(R.string.usagestats_alert_body));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(23)
                public void onDismiss(DialogInterface dialog) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
            builder.show();
        }

        // Daily Disclaimer Warning
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final String currentDate = sdf.format(new Date());
        if (sharedPrefs.getString("LAST_LAUNCH_DATE","nodate").contains(currentDate)){
            // Date matches. User has already Launched the app once today. So do nothing.
        }
        else
        {
            // Display dialog text here......
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.disclaimer_alert_title));
            builder.setMessage(getString(R.string.disclaimer_alert_body));
            builder.setPositiveButton(R.string.disclaimer_ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Set the last Launched date to today.
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putString("LAST_LAUNCH_DATE", currentDate);
                            editor.commit();
                            dialog.cancel();
                        }
                    });
            builder.setNegativeButton(R.string.disclaimer_quit,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // End App
                            finishAndRemoveTask();
                        }
                    });
            builder.show();

        }

        registerReceiver(mBondingBroadcast,new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
        startService(new Intent(MainActivity.this, BluetoothLeService.class));

        if (!sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
            updateDisplay();
        }
    }

    private void showActionBar(){
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_nav_main, null);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled (false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setCustomView(v);

        backButton = (ImageButton) findViewById(R.id.action_back);
        forwardButton = (ImageButton) findViewById(R.id.action_forward);
        otherButton = (ImageButton) findViewById(R.id.action_other);
        dataButton = (ImageButton) findViewById(R.id.action_data);
        faultButton = (ImageButton) findViewById(R.id.action_faults);
        btButton = (ImageButton) findViewById(R.id.action_connect);

        navbarTitle = (TextView) findViewById(R.id.action_title);
        navbarTitle.setText(R.string.main_title);

        backButton.setOnClickListener(mClickListener);
        forwardButton.setOnClickListener(mClickListener);
        faultButton.setOnClickListener(mClickListener);
        otherButton.setOnClickListener(mClickListener);
        dataButton.setOnClickListener(mClickListener);
        btButton.setOnClickListener(mClickListener);

        faultButton.setVisibility(View.GONE);

        mPopupMenu = new PopupMenu(this, dataButton);
        MenuInflater menuInflater = mPopupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.data_menu, mPopupMenu.getMenu());
        mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_trip_logs:
                        Intent tripsIntent = new Intent(MainActivity.this, TripsActivity.class);
                        startActivity(tripsIntent);
                        break;
                    case R.id.action_waypoints:
                        Intent waypointsIntent = new Intent(MainActivity.this, WaypointActivity.class);
                        startActivity(waypointsIntent);
                        break;
                }
                return true;
            }
        });

        mOtherMenu = new PopupMenu(this, otherButton);
        MenuInflater menuOtherInflater = mOtherMenu.getMenuInflater();
        menuOtherInflater.inflate(R.menu.other_menu, mOtherMenu.getMenu());
        otherMenu = mOtherMenu.getMenu();
        otherMenu.findItem(R.id.action_hwsettings).setVisible(false);
        mOtherMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_connect:
                        if(!(Build.BRAND.startsWith("Android") && Build.DEVICE.startsWith("generic"))) {
                            setupBLE();
                        } else {
                            Log.d(TAG,"Running in the emulator");
                        }
                        break;
                    case R.id.action_settings:
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(settingsIntent, SETTINGS_CHECK);
                        break;
                    case R.id.action_hwsettings:
                        Intent hwSettingsIntent = new Intent(MainActivity.this, FWConfigActivity.class);
                        startActivity(hwSettingsIntent);
                        break;
                    case R.id.action_about:
                        Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.action_exit:
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.action_back:
                    Intent backIntent = new Intent(MainActivity.this, TaskActivity.class);
                    startActivity(backIntent);
                    break;
                case R.id.action_forward:
                    Intent forwardIntent = new Intent(MainActivity.this, MusicActivity.class);
                    startActivity(forwardIntent);
                    break;
                case R.id.action_faults:
                    Intent faultIntent = new Intent(MainActivity.this, FaultActivity.class);
                    startActivity(faultIntent);
                    break;
                case R.id.action_data:
                    mPopupMenu.show();
                    break;
                case R.id.action_other:
                    mOtherMenu.show();
                    break;
            }
        }
    };

    // Listens for light sensor events
    private final SensorEventListener sensorEventListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (sharedPrefs.getString("prefNightModeCombo", "0").equals("2")) {
                int delay = (Integer.parseInt(sharedPrefs.getString("prefAutoNightModeDelay", "30")) * 1000);
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    float currentReading = event.values[0];
                    double darkThreshold = 20.0;  // Light level to determine darkness
                    if (currentReading < darkThreshold) {
                        lightTimer = 0;
                        if (darkTimer == 0) {
                            darkTimer = System.currentTimeMillis();
                        } else {
                            long currentTime = System.currentTimeMillis();
                            long duration = (currentTime - darkTimer);
                            if ((duration >= delay) && (!itsDark)) {
                                itsDark = true;
                                Log.d(TAG, "Its dark");
                                // Update colors
                                updateColors(true);
                            }
                        }
                    } else {
                        darkTimer = 0;
                        if (lightTimer == 0) {
                            lightTimer = System.currentTimeMillis();
                        } else {
                            long currentTime = System.currentTimeMillis();
                            long duration = (currentTime - lightTimer);
                            if ((duration >= delay) && (itsDark)) {
                                itsDark = false;
                                Log.d(TAG, "Its light");
                                // Update colors
                                updateColors(false);
                            }
                        }
                    }
                }
            }
        }
    };

    public void updateColors(boolean itsDark){
        ((MyApplication) this.getApplication()).setitsDark(itsDark);
        if (!sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
            LinearLayout lLayout = (LinearLayout) findViewById(R.id.layout_main);
            if (lLayout != null){
                textView1 = (TextView) findViewById(R.id.textView1);
                textView2 = (TextView) findViewById(R.id.textView2);
                textView3 = (TextView) findViewById(R.id.textView3);
                textView4 = (TextView) findViewById(R.id.textView4);
                textView5 = (TextView) findViewById(R.id.textView5);
                textView6 = (TextView) findViewById(R.id.textView6);
                textView7 = (TextView) findViewById(R.id.textView7);
                textView8 = (TextView) findViewById(R.id.textView8);
                textView1Label = (TextView) findViewById(R.id.textView1label);
                textView2Label = (TextView) findViewById(R.id.textView2label);
                textView3Label = (TextView) findViewById(R.id.textView3label);
                textView4Label = (TextView) findViewById(R.id.textView4label);
                textView5Label = (TextView) findViewById(R.id.textView5label);
                textView6Label = (TextView) findViewById(R.id.textView6label);
                textView7Label = (TextView) findViewById(R.id.textView7label);
                textView8Label = (TextView) findViewById(R.id.textView8label);
                LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout_1);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout_2);
                LinearLayout layout3 = (LinearLayout) findViewById(R.id.layout_3);
                LinearLayout layout4 = (LinearLayout) findViewById(R.id.layout_4);
                LinearLayout layout5 = (LinearLayout) findViewById(R.id.layout_5);
                LinearLayout layout6 = (LinearLayout) findViewById(R.id.layout_6);
                LinearLayout layout7 = (LinearLayout) findViewById(R.id.layout_7);
                LinearLayout layout8 = (LinearLayout) findViewById(R.id.layout_8);
                if (itsDark) {
                    Log.d(TAG,"Settings things for dark");
                    //Set Brightness to defaults
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    layoutParams.screenBrightness = -1;
                    getWindow().setAttributes(layoutParams);

                    lLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                    navbarTitle.setTextColor(getResources().getColor(R.color.white));
                    backButton.setColorFilter(getResources().getColor(R.color.white));
                    forwardButton.setColorFilter(getResources().getColor(R.color.white));
                    dataButton.setColorFilter(getResources().getColor(R.color.white));
                    otherButton.setColorFilter(getResources().getColor(R.color.white));
                    textView1.setTextColor(getResources().getColor(R.color.white));
                    textView2.setTextColor(getResources().getColor(R.color.white));
                    textView3.setTextColor(getResources().getColor(R.color.white));
                    textView4.setTextColor(getResources().getColor(R.color.white));
                    textView5.setTextColor(getResources().getColor(R.color.white));
                    textView6.setTextColor(getResources().getColor(R.color.white));
                    textView7.setTextColor(getResources().getColor(R.color.white));
                    textView8.setTextColor(getResources().getColor(R.color.white));
                    textView1Label.setTextColor(getResources().getColor(R.color.white));
                    textView2Label.setTextColor(getResources().getColor(R.color.white));
                    textView3Label.setTextColor(getResources().getColor(R.color.white));
                    textView4Label.setTextColor(getResources().getColor(R.color.white));
                    textView5Label.setTextColor(getResources().getColor(R.color.white));
                    textView6Label.setTextColor(getResources().getColor(R.color.white));
                    textView7Label.setTextColor(getResources().getColor(R.color.white));
                    textView8Label.setTextColor(getResources().getColor(R.color.white));
                    layout1.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout2.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout3.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout4.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout5.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout6.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout7.setBackground(getResources().getDrawable(R.drawable.border_white));
                    layout8.setBackground(getResources().getDrawable(R.drawable.border_white));
                } else {
                    Log.d(TAG, "Settings things for light");
                    if (sharedPrefs.getBoolean("prefBrightnessOverride", false)) {
                        //Set Brightness to 100%
                        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                        layoutParams.screenBrightness = 1;
                        getWindow().setAttributes(layoutParams);
                    }

                    lLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                    navbarTitle.setTextColor(getResources().getColor(R.color.black));
                    backButton.setColorFilter(getResources().getColor(R.color.black));
                    forwardButton.setColorFilter(getResources().getColor(R.color.black));
                    dataButton.setColorFilter(getResources().getColor(R.color.black));
                    otherButton.setColorFilter(getResources().getColor(R.color.black));
                    textView1.setTextColor(getResources().getColor(R.color.black));
                    textView2.setTextColor(getResources().getColor(R.color.black));
                    textView3.setTextColor(getResources().getColor(R.color.black));
                    textView4.setTextColor(getResources().getColor(R.color.black));
                    textView5.setTextColor(getResources().getColor(R.color.black));
                    textView6.setTextColor(getResources().getColor(R.color.black));
                    textView7.setTextColor(getResources().getColor(R.color.black));
                    textView8.setTextColor(getResources().getColor(R.color.black));
                    textView1Label.setTextColor(getResources().getColor(R.color.black));
                    textView2Label.setTextColor(getResources().getColor(R.color.black));
                    textView3Label.setTextColor(getResources().getColor(R.color.black));
                    textView4Label.setTextColor(getResources().getColor(R.color.black));
                    textView5Label.setTextColor(getResources().getColor(R.color.black));
                    textView6Label.setTextColor(getResources().getColor(R.color.black));
                    textView7Label.setTextColor(getResources().getColor(R.color.black));
                    textView8Label.setTextColor(getResources().getColor(R.color.black));
                    layout1.setBackground(getResources().getDrawable(R.drawable.border));
                    layout2.setBackground(getResources().getDrawable(R.drawable.border));
                    layout3.setBackground(getResources().getDrawable(R.drawable.border));
                    layout4.setBackground(getResources().getDrawable(R.drawable.border));
                    layout5.setBackground(getResources().getDrawable(R.drawable.border));
                    layout6.setBackground(getResources().getDrawable(R.drawable.border));
                    layout7.setBackground(getResources().getDrawable(R.drawable.border));
                    layout8.setBackground(getResources().getDrawable(R.drawable.border));
                }
            }
        } else {
            ConstraintLayout cLayout = (ConstraintLayout) findViewById(R.id.layout_main_other);
            if (itsDark) {
                Log.d(TAG,"Settings things for dark");
                cLayout.setBackgroundColor(getResources().getColor(R.color.black));
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
                navbarTitle.setTextColor(getResources().getColor(R.color.white));
                backButton.setColorFilter(getResources().getColor(R.color.white));
                forwardButton.setColorFilter(getResources().getColor(R.color.white));
                dataButton.setColorFilter(getResources().getColor(R.color.white));
                otherButton.setColorFilter(getResources().getColor(R.color.white));
                textViewAppName = (TextView) findViewById(R.id.tvAppName);
                textViewAppName.setTextColor(getResources().getColor(R.color.white));
            } else {
                Log.d(TAG,"Settings things for light");
                cLayout.setBackgroundColor(getResources().getColor(R.color.white));
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
                navbarTitle.setTextColor(getResources().getColor(R.color.black));
                backButton.setColorFilter(getResources().getColor(R.color.black));
                forwardButton.setColorFilter(getResources().getColor(R.color.black));
                dataButton.setColorFilter(getResources().getColor(R.color.black));
                otherButton.setColorFilter(getResources().getColor(R.color.black));
                textViewAppName = (TextView) findViewById(R.id.tvAppName);
                textViewAppName.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"In onResume");
        super.onResume();

        registerReceiver(mBondingBroadcast,new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService == null) {
            Log.d(TAG,"mBluetoothLeService is null");
            //Only use BLE if on a real device
            if(!(Build.BRAND.startsWith("Android") && Build.DEVICE.startsWith("generic"))) {
                setupBLE();
            } else {
                Log.d(TAG,"Running in the emulator");
            }
        } else {
            Log.d(TAG,"mBluetoothLeService is NOT null");
            //mBluetoothLeService.connect(mDeviceAddress,getString(R.string.device_name));
        }
        sensorManager.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //updateDisplay();
        if (((MyApplication) this.getApplication()).getitsDark()){
            updateColors(true);
        } else {
            updateColors(false);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"In onDestroy");
        super.onDestroy();
        try {
            unregisterReceiver(mGattUpdateReceiver);
            unregisterReceiver(mBondingBroadcast);
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){

        }
        mBluetoothLeService = null;
        sensorManager.unregisterListener(sensorEventListener, lightSensor);
    }

    @Override
    public void onStop() {
        Log.d(TAG,"In onStop");
        super.onStop();
        try {
            unregisterReceiver(mGattUpdateReceiver);
            unregisterReceiver(mBondingBroadcast);
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){

        }
        mBluetoothLeService = null;
        sensorManager.unregisterListener(sensorEventListener, lightSensor);
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"In onPause");
        super.onPause();
        try {
            unregisterReceiver(mGattUpdateReceiver);
            unregisterReceiver(mBondingBroadcast);
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e){

        }
        mBluetoothLeService = null;
        sensorManager.unregisterListener(sensorEventListener, lightSensor);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG,"In onConfigChange");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if (requestCode == SETTINGS_CHECK) {
            Log.d(TAG,"onActivityResult");
            this.recreate();
            View view;
            if (sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
                setContentView(R.layout.activity_main_other);
                view = findViewById(R.id.layout_main_other);
            } else {
                setContentView(R.layout.activity_main);
                view = findViewById(R.id.layout_main);
            }
            view.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    Intent backIntent = new Intent(MainActivity.this, MusicActivity.class);
                    startActivity(backIntent);
                }
                @Override
                public void onSwipeRight() {
                    Intent backIntent = new Intent(MainActivity.this, TaskActivity.class);
                    startActivity(backIntent);
                }
            });
            if (sharedPrefs.getString("prefNightModeCombo", "0").equals("1")){
                updateColors(true);
            } else {
                updateColors(false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            if (mBluetoothLeService.mConnectionState == BluetoothLeService.STATE_DISCONNECTED) {
                Log.e(TAG, "In onServiceCOnnected Disconnected,reconnected");
                mBluetoothLeService.connect(mDeviceAddress, getString(R.string.device_name));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void setupBLE() {
        Log.d(TAG,"In setupBLE()");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice devices : pairedDevices) {
                if (devices.getName().equals(getString(R.string.device_name))){
                    Log.d(TAG,"WunderLINQ previously paired");
                    mDeviceAddress = devices.getAddress();
                    Log.d(TAG,"Address: " + mDeviceAddress);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    scanLeDevice(false);
                    return;
                }
            }
        }
        Log.d(TAG, "Previously Paired WunderLINQ not found");
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            Log.d(TAG,"In scanLeDevice() Scanning On");
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothLeScanner != null) {
                        bluetoothLeScanner.stopScan(mLeScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            //scan specified devices only with ScanFilter
            ScanFilter scanFilter =
                    new ScanFilter.Builder()
                            .setDeviceName(getString(R.string.device_name))
                            .build();
            List<ScanFilter> scanFilters = new ArrayList<>();
            scanFilters.add(scanFilter);

            ScanSettings scanSettings =
                    new ScanSettings.Builder().build();

            try {
                bluetoothLeScanner.startScan(scanFilters, scanSettings, mLeScanCallback);
            } catch (NullPointerException e){
                //Testing
                Log.d(TAG,"NullPointerException: " + e.toString());
            }

        } else {
            Log.d(TAG,"In scanLeDevice() Scanning Off");
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String device = result.getDevice().getName();
            if (device != null) {
                if (device.contains(getString(R.string.device_name))) {
                    Log.d(TAG, "WunderLINQ Device Found: " + device);
                    result.getDevice().createBond();
                    scanLeDevice(false);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    BroadcastReceiver mBondingBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);

            switch (state) {
                case BluetoothDevice.BOND_BONDING:
                    Log.d("Bondind Status:", " Bonding...");
                    break;

                case BluetoothDevice.BOND_BONDED:
                    Log.d("Bondind Status:", "Bonded!!");
                    setupBLE();
                    break;

                case BluetoothDevice.BOND_NONE:
                    Log.d("Bondind Status:", "Fail");

                    break;
            }
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG,"GATT_CONNECTED");
                mBluetoothLeService.discoverServices();
                //checkGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"GATT_DISCONNECTED");
                Data.clear();
                if (!sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
                    updateDisplay();
                }
                btButton.setColorFilter(getResources().getColor(R.color.motorrad_red));
                btButton.setEnabled(true);
                otherMenu.findItem(R.id.action_hwsettings).setVisible(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG,"GATT_SERVICE_DISCOVERED");
                checkGattServices(mBluetoothLeService.getSupportedGattServices());
                btButton.setColorFilter(getResources().getColor(R.color.motorrad_blue));
                btButton.setEnabled(false);
                otherMenu.findItem(R.id.action_hwsettings).setVisible(true);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //Log.d(TAG,"GATT_DATA_AVAILABLE");
                btButton.setColorFilter(getResources().getColor(R.color.motorrad_blue));
                btButton.setEnabled(false);
                otherMenu.findItem(R.id.action_hwsettings).setVisible(true);
                if (!sharedPrefs.getString("prefMotorcycleType", "0").equals("0")){
                    updateDisplay();
                }
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void checkGattServices(List<BluetoothGattService> gattServices) {
        Log.d(TAG,"In checkGattServices");
        if (gattServices == null) return;
        String uuid;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            Log.d(TAG,"In checkGattServices: for loop");
            if (UUID_MOTORCYCLE_SERVICE.equals(gattService.getUuid())){
                uuid = gattService.getUuid().toString();
                Log.d(TAG,"Motorcycle Service Found: " + uuid);
                gattCharacteristics = gattService.getCharacteristics();
                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    Log.d(TAG,"Characteristic Found: " + uuid);
                    if (UUID.fromString(GattAttributes.WUNDERLINQ_MESSAGE_CHARACTERISTIC).equals(gattCharacteristic.getUuid())) {
                        int charaProp = gattCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(gattCharacteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = gattCharacteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    gattCharacteristic, true);
                        }
                    } else if (UUID.fromString(GattAttributes.WUNDERLINQ_COMMAND_CHARACTERISTIC).equals(gattCharacteristic.getUuid())){
                        gattCommandCharacteristic = gattCharacteristic;
                    }
                }
            }
        }
    }

    // Update Display
    private void updateDisplay(){
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);
        textView8 = (TextView) findViewById(R.id.textView8);
        //Check for active faults
        FaultStatus faults;
        faults = (new FaultStatus(this));
        ArrayList<String> faultListData = faults.getallActiveDesc();
        if (!faultListData.isEmpty()){
            faultButton.setVisibility(View.VISIBLE);
        } else {
            faultButton.setVisibility(View.GONE);
        }

        String pressureUnit = "bar";
        String pressureFormat = sharedPrefs.getString("prefPressureF", "0");
        if (pressureFormat.contains("1")) {
            // KPa
            pressureUnit = "KPa";
        } else if (pressureFormat.contains("2")) {
            // Kg-f
            pressureUnit = "Kg-f";
        } else if (pressureFormat.contains("3")) {
            // Psi
            pressureUnit = "psi";
        }
        String temperatureUnit = "C";
        String temperatureFormat = sharedPrefs.getString("prefTempF", "0");
        if (temperatureFormat.contains("1")) {
            // F
            temperatureUnit = "F";
        }
        String distanceUnit = "km";
        String distanceFormat = sharedPrefs.getString("prefDistance", "0");
        if (distanceFormat.contains("1")) {
            distanceUnit = "mi";
        }
        if(Data.getFrontTirePressure() != null){
            Double rdcFront = Data.getFrontTirePressure();
            if (pressureFormat.contains("1")) {
                // KPa
                rdcFront = Utils.barTokPa(rdcFront);
            } else if (pressureFormat.contains("2")) {
                // Kg-f
                rdcFront = Utils.barTokgf(rdcFront);
            } else if (pressureFormat.contains("3")) {
                // Psi
                rdcFront = Double.valueOf(Utils.oneDigit.format(Utils.barToPsi(rdcFront)));
            }
            textView1.setText(rdcFront + " " + pressureUnit);
        } else {
            textView1.setText(getString(R.string.blank_field));
        }
        if(Data.getRearTirePressure() != null){
            Double rdcRear = Data.getRearTirePressure();
            if (pressureFormat.contains("1")) {
                // KPa
                rdcRear = Utils.barTokPa(rdcRear);
            } else if (pressureFormat.contains("2")) {
                // Kg-f
                rdcRear = Utils.barTokgf(rdcRear);
            } else if (pressureFormat.contains("3")) {
                // Psi
                rdcRear = Double.valueOf(Utils.oneDigit.format(Utils.barToPsi(rdcRear)));
            }
            textView5.setText(rdcRear + " " + pressureUnit);
        } else {
            textView5.setText(getString(R.string.blank_field));
        }
        if(Data.getGear() != null){
            textView3.setText(Data.getGear());
        } else {
            textView3.setText(getString(R.string.blank_field));
        }
        if(Data.getEngineTemperature() != null ){
            Double engineTemp = Data.getEngineTemperature();
            if (temperatureFormat.contains("1")) {
                // F
                engineTemp = Utils.celsiusToFahrenheit(engineTemp);
            }
            textView2.setText((int) Math.round(engineTemp) + " " + temperatureUnit);
        } else {
            textView2.setText(getString(R.string.blank_field));
        }
        if(Data.getAmbientTemperature() != null ){
            Double ambientTemp = Data.getAmbientTemperature();
            if (temperatureFormat.contains("1")) {
                // F
                ambientTemp = Utils.celsiusToFahrenheit(ambientTemp);
            }
            textView6.setText((int) Math.round(ambientTemp) + " " + temperatureUnit);
        } else {
            textView6.setText(getString(R.string.blank_field));
        }
        if(Data.getOdometer() != null){
            Double odometer = Data.getOdometer();
            if (distanceFormat.contains("1")) {
                odometer = Utils.kmToMiles(odometer);
            }
            textView7.setText(Math.round(odometer) + " " + distanceUnit);
        } else {
            textView7.setText(getString(R.string.blank_field));
        }
        if(Data.getTripOne() != null && Data.getTripTwo() != null) {
            Double trip1 = Data.getTripOne();
            Double trip2 = Data.getTripTwo();
            if (distanceFormat.contains("1")) {
                trip1 = Utils.kmToMiles(trip1);
                trip2 = Utils.kmToMiles(trip2);
            }
            textView4.setText(Math.round(trip1) + " " + distanceUnit);
            textView8.setText(Math.round(trip2) + " " + distanceUnit);
        } else {
            textView4.setText(getString(R.string.blank_field));
            textView8.setText(getString(R.string.blank_field));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Camera permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_camera_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
            case PERMISSION_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Call Phone permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_call_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
            case PERMISSION_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Call Phone permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_contacts_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
            case PERMISSION_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Record Audio permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_record_audio_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
            case PERMISSION_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG, "Write to storage permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_write_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.negative_alert_title));
                    builder.setMessage(getString(R.string.negative_location_alert_body));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
            }
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "Keycode: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                Intent backIntent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(backIntent);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Intent forwardIntent = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(forwardIntent);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public static Context getContext(){
        return mContext;
    }

}
