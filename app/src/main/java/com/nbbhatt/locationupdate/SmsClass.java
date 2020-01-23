package com.nbbhatt.locationupdate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import static com.nbbhatt.locationupdate.MainActivity.number_et1;
import static com.nbbhatt.locationupdate.MainActivity.number_et2;
import static com.nbbhatt.locationupdate.MainActivity.number_et3;

public class SmsClass {

    private Context context;
    private int time;
    private String address;
    private int percentage;

    SmsClass(Context context){
        this.context = context;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time = Integer.parseInt(MainActivity.txt_time.getText().toString());
            time = time*1000;
            Toast.makeText(context,address,Toast.LENGTH_SHORT).show();
            Send_Message_Contact();
            handler.postDelayed(runnable,time);
        }
    };

    public void startThread(){
        runnable.run();
    }

    public void stopThread(){
        handler.removeCallbacks(runnable);
    }




    public void Send_Message_Contact(){

        getBattery_percentage();
        if(percentage <20){
            send_message(number_et1.getText().toString().replace(" ",""),"battery is "+percentage);
        }


        if(number_et1.getText().toString().replace(" ","").matches("") ){
            Toast.makeText(context,"number 1 is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            send_message(number_et1.getText().toString().replace(" ",""),address);
        }

        if(number_et2.getText().toString().replace(" ","").matches("")){
            Toast.makeText(context,"number 2 is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            send_message(number_et2.getText().toString().replace(" ",""),address);
        }

        if(number_et3.getText().toString().replace(" ","").matches("")){
            Toast.makeText(context,"number 3 is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            send_message(number_et3.getText().toString().replace(" ",""),address);
        }
    }


    public void send_message(String number,String text) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, text, null, null);
        Toast.makeText(context, "SMS sent to "+number, Toast.LENGTH_SHORT).show();
    }

    public void getBattery_percentage()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        percentage = Math.round(batteryPct * 100);
    }

}
