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


import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

public class CommandStrings {
    public static UUID UUID_WW2 = UUID.fromString(SampleGattAttributes.WW2);
    public static BluetoothGattCharacteristic chara = new BluetoothGattCharacteristic(UUID_WW2, 0, 0);
    private static BluetoothLeService mBluetoothLeService;
    private final static String TAG = CommandStrings.class.getSimpleName();

    public static byte[] soundByeBye     = new byte[] {24, 83, 89, 83, 84, 66, 79, 95, 86, 55, 95, 86, 65, 82, 73, 14, 80}; //BO_V7_VARI
    public static byte[] soundHorn       = new byte[] {24, 83, 89, 83, 84, 72, 65, 80, 80, 89, 95, 72, 79, 78, 75, 14, 70}; //HAPPY_HONK
    public static byte[] soundSiren      = new byte[] {24, 83, 89, 83, 84, 88, 95, 83, 73, 82, 69, 78, 95, 48, 50, 14, 70}; //X_SIREN_02


    public static byte[] soundTireSqueel = new byte[] {24, 83, 89, 83, 84, 84, 73, 82, 69, 83, 81, 85, 69, 65, 76, 14, 70}; //T, I, R, E, S, Q, U, E, A, L
    public static byte[] soundCow        = new byte[] {24, 83, 89, 83, 84, 67, 79, 87, 95, 77, 79, 79, 49, 49, 65, 14, 70}; //C, O, W, _, M, O, O, 1, 1, A
    public static byte[] soundDog        = new byte[] {24, 83, 89, 83, 84, 70, 88, 95, 68, 79, 71, 95, 48, 50,  0, 14, 70}; //F, X, _, D, O, G, _, 0, 2,
    public static byte[] soundElefant    = new byte[] {24, 83, 89, 83, 84, 69, 76, 69, 80, 72, 65, 78, 84, 95, 48, 14, 70}; //E, L, E, P, H, A, N, T, _, 0
    public static byte[] soundOhYeah     = new byte[] {24, 83, 89, 83, 84, 66, 82, 65, 71, 71, 73, 78, 71, 49, 65, 14, 70}; //B, R, A, G, G, I, N, G, 1, A
    public static byte[] soundOhNo       = new byte[] {24, 83, 89, 83, 84, 86, 55, 95, 79, 72, 78, 79, 95, 48, 57, 14, 70}; //V, 7, _, O, H, N, O, _, 0, 9
    public static byte[] soundWow        = new byte[] {24, 83, 89, 83, 84, 67, 79, 78, 70, 85, 83, 69, 68, 95, 56, 14, 70}; //C, O, N, F, U, S, E, D, _, 8

    public static byte[] soundHi         = new byte[] {24, 83, 89, 83, 84, 68, 65, 83, 72, 95, 72, 73, 95, 86, 79, 14, 80}; //D, A, S, H, _, H, I, _, V, O

    public static byte[] colorYellow     = new byte[] {28, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] colorGreen      = new byte[] {28, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] colorOrange     = new byte[] {28, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] colorBlue       = new byte[] {28, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] colorRed        = new byte[] {28, 3, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] colorPurple     = new byte[] {28, 3, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public static void playSound(byte[] paramString) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
    }

    public static void sendCommand(byte[] paramString, int delay) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
        for (int i=0; i < delay; i++) {
            Log.d(TAG, "Waiting after command...");
        }
    }

    public static void sendLight(byte[] paramString) {
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  paramString);
        BluetoothLeService.writeCharacteristic(mBluetoothLeService.UUID_WW_SERVICE, chara,  new byte[] {-56, 4});
    }

    public static void sayByeBye() {
        playSound(soundByeBye);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Time to say goodbye...");
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                }
            }
        }, 1000);
    }

    public static void sayHi() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Time to say hello...");
                CommandStrings.playSound(CommandStrings.soundHi);
            }
        }, 1000);
    }
}
