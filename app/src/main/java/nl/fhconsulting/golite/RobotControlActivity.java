/*
 * Copyright (C) 2015 FH Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.fhconsulting.golite;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import java.util.Random;
import java.util.UUID;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class RobotControlActivity extends Activity {
    private final static String TAG = RobotControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothLeService mBluetoothLeService;

    private boolean mConnected = false;


    byte[] soundByeBye     = new byte[] {24, 83, 89, 83, 84, 66, 79, 95, 86, 55, 95, 86, 65, 82, 73, 14, 80}; //BO_V7_VARI
    byte[] soundHorn       = new byte[] {24, 83, 89, 83, 84, 72, 65, 80, 80, 89, 95, 72, 79, 78, 75, 14, 70}; //HAPPY_HONK
    byte[] soundSiren      = new byte[] {24, 83, 89, 83, 84, 88, 95, 83, 73, 82, 69, 78, 95, 48, 50, 14, 70}; //X_SIREN_02


    byte[] soundTireSqueel = new byte[] {24, 83, 89, 83, 84, 84, 73, 82, 69, 83, 81, 85, 69, 65, 76, 14, 70}; //T, I, R, E, S, Q, U, E, A, L
    byte[] soundCow        = new byte[] {24, 83, 89, 83, 84, 67, 79, 87, 95, 77, 79, 79, 49, 49, 65, 14, 70}; //C, O, W, _, M, O, O, 1, 1, A
    byte[] soundDog        = new byte[] {24, 83, 89, 83, 84, 70, 88, 95, 68, 79, 71, 95, 48, 50,  0, 14, 70}; //F, X, _, D, O, G, _, 0, 2,
    byte[] soundElefant    = new byte[] {24, 83, 89, 83, 84, 69, 76, 69, 80, 72, 65, 78, 84, 95, 48, 14, 70}; //E, L, E, P, H, A, N, T, _, 0
    byte[] soundOhYeah     = new byte[] {24, 83, 89, 83, 84, 66, 82, 65, 71, 71, 73, 78, 71, 49, 65, 14, 70}; //B, R, A, G, G, I, N, G, 1, A
    byte[] soundOhNo       = new byte[] {24, 83, 89, 83, 84, 86, 55, 95, 79, 72, 78, 79, 95, 48, 57, 14, 70}; //V, 7, _, O, H, N, O, _, 0, 9
    byte[] soundWow        = new byte[] {24, 83, 89, 83, 84, 67, 79, 78, 70, 85, 83, 69, 68, 95, 56, 14, 70}; //C, O, N, F, U, S, E, D, _, 8

    byte[] soundHi         = new byte[] {24, 83, 89, 83, 84, 68, 65, 83, 72, 95, 72, 73, 95, 86, 79, 14, 80}; //D, A, S, H, _, H, I, _, V, O

    byte[] colorYellow     = new byte[] {28, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    byte[] colorGreen      = new byte[] {28, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    byte[] colorOrange     = new byte[] {28, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    byte[] colorBlue       = new byte[] {28, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    byte[] colorRed        = new byte[] {28, 3, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    byte[] colorPurple     = new byte[] {28, 3, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    UUID UUID_WW2 = UUID.fromString(SampleGattAttributes.WW2);
    BluetoothGattCharacteristic chara = new BluetoothGattCharacteristic(UUID_WW2, 0, 0);

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
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
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
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                showUI(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                showUI(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.robot_control);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);

        ImageView sirenImage = (ImageView) findViewById(R.id.siren_image);
        sirenImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundSiren);
            }
        });



        ImageView tireImage = (ImageView) findViewById(R.id.tire_image);
        tireImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundTireSqueel);
            }
        });


        ImageView hornImage = (ImageView) findViewById(R.id.horn_image);
        hornImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundHorn);
            }
        });

        ImageView cowImage = (ImageView) findViewById(R.id.cow_image);
        cowImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundCow);
            }
        });

        ImageView elefantImage = (ImageView) findViewById(R.id.elephant_image);
        elefantImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundElefant);
            }
        });

        ImageView dogImage = (ImageView) findViewById(R.id.dog_image);
        dogImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundDog);
            }
        });

        ImageView excitedImage = (ImageView) findViewById(R.id.excited_image);
        excitedImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundOhYeah);
            }
        });

        ImageView tornadoImage = (ImageView) findViewById(R.id.tah_dah_image);
        tornadoImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundOhNo);
            }
        });


        ImageView wowImage = (ImageView) findViewById(R.id.whuh_oh_image);
        wowImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playSound(soundWow);
            }
        });


        Button lightShow = (Button) findViewById(R.id.lightShowButton);
        lightShow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLightShow();
            }
        });

        Button rollEye = (Button) findViewById(R.id.rollEyeButton);
        rollEye.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rollEye(10000);
            }
        });

        ImageView changeYellow = (ImageView)findViewById(R.id.colorYellow);
        changeYellow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorYellow);
            }
        });
        ImageView changeGreen = (ImageView)findViewById(R.id.colorGreen);
        changeGreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorGreen);
            }
        });
        ImageView changeOrange = (ImageView)findViewById(R.id.colorOrange);
        changeOrange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorOrange);
            }
        });
        ImageView changeBlue = (ImageView)findViewById(R.id.colorBlue);
        changeBlue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorBlue);
            }
        });
        ImageView changeRed = (ImageView)findViewById(R.id.colorRed);
        changeRed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorRed);
            }
        });
        ImageView changePurple = (ImageView)findViewById(R.id.colorPurple);
        changePurple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeColor(colorPurple);
            }
        });

        Button moveHead = (Button) findViewById(R.id.moveHeadButton);
        if (mDeviceName.equalsIgnoreCase("Dash")) {
            moveHead.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    moveHead(5000);
                }
            });
        } else {
            moveHead.setVisibility(View.INVISIBLE);
        }

        sayHi();


        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result = " + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                sayByeBye();
                //mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }



    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void playSound(byte[] paramString) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
    }

    public void sendCommand(byte[] paramString, int delay) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
        for (int i=0; i < delay; i++) {
            Log.d(TAG, "Waiting after command...");
        }
    }

    public void sendLight(byte[] paramString) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  new byte[] {-56, 4});
    }

    public void sayByeBye() {
        playSound(soundByeBye);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Time to say goodbye...");
                mBluetoothLeService.disconnect();
            }
        }, 1000);
    }

    public void sayHi() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Time to say hello...");
                playSound(soundHi);
            }
        }, 1000);
    }

    private void moveHead(int delay) {

        sendCommand(new byte[] {6, -1, -38, 7, -1, -8}, delay);
        sendCommand(new byte[] {6, -2, -85, 7, -1, -59}, delay);
        sendCommand(new byte[] {6, -4, 115, 7, -1, -109}, delay);
        sendCommand(new byte[] {6, -7, -16, 7, -1, 113}, delay);
        sendCommand(new byte[] {6, -11, -14, 7, -1, 80}, delay);
        sendCommand(new byte[] {6, -15, 55, 7, -1, 54}, delay);
        sendCommand(new byte[] {6, -19, 19, 7, -1, 29}, delay);
        sendCommand(new byte[] {6, -26, -73, 7, -1, 12}, delay);
        sendCommand(new byte[] {6, -31, 24, 7, -1, 21}, delay);
        sendCommand(new byte[] {6, -36, -87, 7, -1, 21}, delay);
        sendCommand(new byte[] {6, -39, -39, 7, -1, 21}, delay);
        sendCommand(new byte[] {6, -40, -48, 7, -1, 12}, delay);
        sendCommand(new byte[] {6, -41, -19, 7, -1, 4}, delay);
        sendCommand(new byte[] {6, -41, 124, 7, -2, -5}, delay);
        sendCommand(new byte[] {6, -41, -94, 7, -2, -13}, delay);
        sendCommand(new byte[] {6, -39, -114, 7, -2, -13}, delay);
        sendCommand(new byte[] {6, -35, -40, 7, -1, 21}, delay);
        sendCommand(new byte[] {6, -29, 118, 7, -1, 46}, delay);
        sendCommand(new byte[] {6, -22, -113, 7, -1, 63}, delay);
        sendCommand(new byte[] {6, -16, 46, 7, -1, 63}, delay);
        sendCommand(new byte[] {6, -11, -52, 7, -1, 71}, delay);
        sendCommand(new byte[] {6, -6, -121, 7, -1, 105}, delay);
        sendCommand(new byte[] {6, -1, 67, 7, -1, -109}, delay);
        sendCommand(new byte[] {6, 3, -101, 7, -1, -67}, delay);
        sendCommand(new byte[] {6, 9, -32, 7, -1, -17}, delay);
        sendCommand(new byte[] {6, 14, -108, 7, -1, -8}, delay);
        sendCommand(new byte[] {6, 19, -63, 7, -1, -17}, delay);
        sendCommand(new byte[] {6, 21, 122, 7, -1, -34}, delay);
        sendCommand(new byte[] {6, 22, 27, 7, -1, -42}, delay);
        sendCommand(new byte[] {6, 22, 27, 7, -1, -50}, delay);
        sendCommand(new byte[] {6, 22, 27, 7, -1, -59}, delay);
        sendCommand(new byte[] {6, 22, 27, 7, -1, -67}, delay);
        sendCommand(new byte[] {6, 0, 0, 7, 0, 0}, delay);
    }

    private void changeColor(byte[] color) {
        sendLight(color);
    }

    private void playLightShow() {
        for (int i=0; i < 30; i++) {
            Random r = new Random();
            int rand = r.nextInt(6) + 1;
            switch (rand) {
                case 1:
                    changeColor(colorYellow);
                    break;
                case 2:
                    changeColor(colorGreen);
                    break;
                case 3:
                    changeColor(colorOrange);
                    break;
                case 4:
                    changeColor(colorBlue);
                    break;
                case 5:
                    changeColor(colorRed);
                    break;
                case 6:
                    changeColor(colorPurple);
                    break;
                default:
                    break;
            };
            for (int j=0; j < 15000; j++) {
              Log.d(TAG, "waiting in lightshow...");
            }
        }
    }

    private void showUI(boolean bool) {
        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollview);
        if (bool) {
            scrollView.setVisibility(View.VISIBLE);
        } else {
            scrollView.setVisibility(View.INVISIBLE);
        }
    }

    private void rollEye(int delay) {
        sendCommand(new byte[] {8, 88, 9, 15,   -2}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,   -4}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,   -8}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,  -16}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,  -32}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,  -64}, delay);
        sendCommand(new byte[] {8, 88, 9, 15, -128}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,    0}, delay);
        sendCommand(new byte[] {8, 88, 9, 14,    0}, delay);
        sendCommand(new byte[] {8, 88, 9, 12,    0}, delay);
        sendCommand(new byte[] {8, 88, 9,  8,    0}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,    0}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,    1}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,    3}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,    7}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,   15}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,   31}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,   63}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,  127}, delay);
        sendCommand(new byte[] {8, 88, 9,  0,   -1}, delay);
        sendCommand(new byte[] {8, 88, 9,  1,   -1}, delay);
        sendCommand(new byte[] {8, 88, 9,  3,   -1}, delay);
        sendCommand(new byte[] {8, 88, 9,  7,   -1}, delay);
        sendCommand(new byte[] {8, 88, 9, 15,   -1}, delay);
    }

}

