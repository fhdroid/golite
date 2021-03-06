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
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class CalculatorActivity extends Activity {
    private Button againButton;
    Button level1;
    Button level2;
    Button level3;

    private static final int PLUS  = 1;
    private static final int MINUS = 2;
    private static final int TIMES = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);
        againButton = (Button)findViewById(R.id.againButton);
        againButton.setVisibility(View.INVISIBLE);
        level1 = (Button)findViewById(R.id.level1);
        level2 = (Button)findViewById(R.id.level2);
        level3 = (Button)findViewById(R.id.level3);

        final Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);
        setUp(level);
        level1.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("level", 1);
                finish();
                startActivity(intent);
            }
        }));
        level2.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("level", 2);
                finish();
                startActivity(intent);
            }
        }));
        level3.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                intent.putExtra("level", 3);
                finish();
                startActivity(intent);
            }
        }));


        //FIXME: get BLE info and reconnect if needed
    }

    private void setUp(int level) {
        Random r = new Random();
        int factor = 10;
        final int operator;
        final EditText input = (EditText)findViewById(R.id.inputField);
        switch (level) {
            case 1:
                factor=10;
                level1.setBackground(getResources().getDrawable(R.drawable.roundedbutton_green) );
                level2.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                level3.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                operator = PLUS; //simple +
                //max input
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
                break;
            case 2:
                factor=100;
                level1.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                level2.setBackground(getResources().getDrawable(R.drawable.roundedbutton_green) );
                level3.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                operator = r.nextInt(2) + 1; // + or -
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
                break;
            case 3:
                factor=500;
                level1.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                level2.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                level3.setBackground(getResources().getDrawable(R.drawable.roundedbutton_green) );
                operator = r.nextInt(3) + 1; //+, - and *
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
                break;
            default:
                factor=10;
                level1.setBackground(getResources().getDrawable(R.drawable.roundedbutton_green) );
                level2.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                level3.setBackground(getResources().getDrawable(R.drawable.roundedbutton_red) );
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(2)});
                operator = PLUS;
        }
        final int firstValue  = r.nextInt(factor - 2) + 1;
        int secondValueTemp = r.nextInt(factor - 2) + 1;
        if ( operator == MINUS && (secondValueTemp >= firstValue) ) {
            do {
                secondValueTemp = r.nextInt(factor - 2) + 1;
            }
            while (secondValueTemp >= firstValue);
        }
        final int secondValue = secondValueTemp;
        TextView firstValueText = (TextView)findViewById(R.id.firstValue);
        firstValueText.setText(Integer.toString(firstValue));
        TextView secondValueText = (TextView)findViewById(R.id.secondValue);
        secondValueText.setText(Integer.toString(secondValue));
        TextView operatorText = (TextView)findViewById(R.id.operator);
        switch (operator) {
            case PLUS:
                operatorText.setText("+");
            break;
            case MINUS:
                operatorText.setText("-");
            break;
            case TIMES:
                operatorText.setText("x");
            break;
            default:
                operatorText.setText("+");
            break;

        }


        Button checkCalcButton = (Button)findViewById(R.id.checkCalcButton);
        checkCalcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //if ( (!input.getText().toString().equals("")) && (firstValue + secondValue) ==  Integer.parseInt(input.getText().toString())) {
                if (!input.getText().toString().equals("") && (checkSum(firstValue, secondValue, operator, Integer.parseInt(input.getText().toString())))) {
                   CommandStrings.playSound(CommandStrings.soundOhYeah);
                   againButton.setVisibility(View.VISIBLE);
                   againButton.setOnClickListener(new View.OnClickListener() {
                       public void onClick(View v) {
                           finish();
                           startActivity(getIntent());
                       }
                   });
               } else {
                   CommandStrings.playSound(CommandStrings.soundOhNo);
               }
            }
        });
    }

    private boolean checkSum(int i, int j, int operator, int answer) {
        boolean bool = false;
        switch(operator) {
            case PLUS:
                if ( i + j == answer) {
                    bool = true;
                }
                break;
            case MINUS:
                if ( i - j == answer) {
                    bool = true;
                }
                break;
            case TIMES:
                if ( i * j == answer) {
                    bool = true;
                }
                break;

        }
        return bool;
    }

}
