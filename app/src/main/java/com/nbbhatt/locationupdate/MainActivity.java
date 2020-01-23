package com.nbbhatt.locationupdate;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnDSListener {

    static EditText txt_time;
    Switch message_btn_switch,location_switch_btn,listenbtn;
    String Latitude,Longitude;
    static String fullAddress = "address";
    static String SpeakText ;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    List<Address> addresses;
    Geocoder geocoder;
    String geoUri;

    SmsClass smsClass;
    String address,area,city,country,postalcode;
    String text;

    static EditText number_et1,number_et2,number_et3;
    Button contact_btn1,contact_btn2,contact_btn3;

    private DroidSpeech droidSpeech;

    private static final int MY_PERMISSIONS_REQUEST = 100;
    private static final int RESULT_PICK_CONTACT1 = 10;
    private static final int RESULT_PICK_CONTACT2 = 20;
    private static final int RESULT_PICK_CONTACT3 = 30;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_main);

        txt_time = findViewById(R.id.txt_time);
        message_btn_switch = findViewById(R.id.message_switch_btn);
        location_switch_btn = findViewById(R.id.location_switch_btn);
        listenbtn = findViewById(R.id.listenbtn);
        number_et1 = findViewById(R.id.number_et1);
        number_et2 = findViewById(R.id.number_et2);
        number_et3 = findViewById(R.id.number_et3);
        contact_btn1 =  findViewById(R.id.contact_btn1);
        contact_btn2 = findViewById(R.id.contact_btn2);
        contact_btn3 = findViewById(R.id.contact_btn3);


        smsClass = new SmsClass(MainActivity.this);
        //create fusedLocationClient........
    //    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        permission();

        droidSpeech = new DroidSpeech(this, null);
        droidSpeech.setOnDroidSpeechListener(this);

        location_switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    stopService();
                  //  startService();
                    buildLocationRequest();
                    buildLocationCallback();
                    Toast.makeText(MainActivity.this, "location on", Toast.LENGTH_SHORT).show();
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
                }else{
                    stopService();
                    Toast.makeText(MainActivity.this, "location off", Toast.LENGTH_SHORT).show();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                }
            }
        });

        message_btn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    smsClass.startThread();
                }else{
                    smsClass.stopThread();
                }
            }
        });


        listenbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    droidSpeech.startDroidSpeechRecognition();
                    Toast.makeText(MainActivity.this, "speech on", Toast.LENGTH_SHORT).show();
                }else{
                   droidSpeech.closeDroidSpeechOperations();
                    Toast.makeText(MainActivity.this, "speech off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contact_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_contact(RESULT_PICK_CONTACT1);
            }
        });

        contact_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_contact(RESULT_PICK_CONTACT2);
            }
        });

        contact_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_contact(RESULT_PICK_CONTACT3);
            }
        });


    }

    public void startService(){
        String combine_address = address+","+area+","+city+","+country+","+postalcode;

        if(number_et1== null){
            return;
        }else{
             text = "Sms Send to "+number_et1.getText().toString();
             text = " ";
        }

        if(number_et2== null){
            return;
        }else{
           text = "Sms Send to "+number_et2.getText().toString();
            text = " ";
        }

        if(number_et3== null){
            return;
        }else{
            text = "Sms Send to "+number_et3.getText().toString();
            text = " ";
        }

        Intent serviceIntent = new Intent(this,NotificationService.class);
        serviceIntent.putExtra("LatLong",Latitude+","+Longitude);
        serviceIntent.putExtra("Address",combine_address);
        serviceIntent.putExtra("Number",text);
        serviceIntent.putExtra("SpeakText",SpeakText);
        ContextCompat.startForegroundService(this,serviceIntent);
    }

    public void stopService(){
        Intent serviceIntent = new Intent(this,NotificationService.class);
        stopService(serviceIntent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
       startService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       stopService();
    }

    private boolean permission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED&& ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
             requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS,Manifest.permission.RECORD_AUDIO,
              Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST);
            return true;
        }
        return false;

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                permission();
            }
        }
    }

    public void get_contact(int RESULT_PICK_CONTACT) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }


    public void get_Address(Double latitude,Double longitude){

         geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
             address = addresses.get(0).getAddressLine(0);
             area = addresses.get(0).getLocality();
             city = addresses.get(0).getAdminArea();
             country = addresses.get(0).getCountryName();
             postalcode = addresses.get(0).getPostalCode();

            geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
            fullAddress = (address + "\n" + area + "\n" + city + "\n" + country + "\n" + postalcode + "\n " + geoUri);
            smsClass.setAddress(fullAddress);
          //  Toast.makeText(this, city, Toast.LENGTH_SHORT).show();
           // txt_time.setText(address_tv);

        }catch (Exception e){
            Toast.makeText(this, "exception on getting address", Toast.LENGTH_SHORT).show();
        }

    }


    private void buildLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location:locationResult.getLocations())
                {
                    Latitude = String.valueOf(location.getLatitude());
                    Longitude = String.valueOf(location.getLongitude());
                    Toast.makeText(MainActivity.this, Latitude+","+Longitude, Toast.LENGTH_SHORT).show();
                    //getting address................

                    while(location != null){
                        get_Address(Double.parseDouble(Latitude),Double.parseDouble(Longitude));

                        break;
                    }
                }
                startService();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(0);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT1:
                    Cursor cursor1 = null;
                    try {
                        String phoneNo = null;
                        Uri uri = data.getData();
                        cursor1 = getContentResolver().query(uri, null, null, null, null);
                        cursor1.moveToFirst();
                        int phoneIndex = cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor1.getString(phoneIndex);
                        number_et1.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case RESULT_PICK_CONTACT2:
                    Cursor cursor2 = null;
                    try {
                        String phoneNo = null;
                        Uri uri = data.getData();
                        cursor2 = getContentResolver().query(uri, null, null, null, null);
                        cursor2.moveToFirst();
                        int phoneIndex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor2.getString(phoneIndex);

                        number_et2.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case RESULT_PICK_CONTACT3:
                    Cursor cursor3 = null;
                    try {
                        String phoneNo = null;
                        Uri uri = data.getData();
                        cursor3 = getContentResolver().query(uri, null, null, null, null);
                        cursor3.moveToFirst();
                        int phoneIndex = cursor3.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor3.getString(phoneIndex);

                        number_et3.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }

    }


    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {

    }

    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {

    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {

    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        Toast.makeText(this, finalSpeechResult, Toast.LENGTH_SHORT).show();
        SpeakText = finalSpeechResult;

        if(finalSpeechResult.equals("call")){
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+number_et1.getText().toString()));
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
            Toast.makeText(this,"call send",Toast.LENGTH_SHORT).show();
        }

        if(finalSpeechResult.equals("help")){
            MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.song);
            mediaPlayer.start();
        }

        if(finalSpeechResult.equals("stop")){
            droidSpeech.closeDroidSpeechOperations();
            listenbtn.setChecked(false);
        }
    }

    @Override
    public void onDroidSpeechClosedByUser() {

    }

    @Override
    public void onDroidSpeechError(String errorMsg) {

    }
}
