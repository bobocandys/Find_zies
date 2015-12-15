package com.example.liny33.find_zies;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;

/*
    Shiying Xu, Weila Xu, Youying Lin
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PLACE_PICKER_REQUEST = 1;

    private static final double CSE_LAT = 47.653475;
    private static final double CSE_LNG = -122.303498;

    private static final String SERVER_IP = "173.250.179.117";
    private static final int SERVER_PORT = 1236;

    private GoogleApiClient mLocationClient;
    private GoogleMap mMap; // m for member variable
    private Socket socket;
    private MenuItem findMenuItem;
    private MenuItem notifyAllMenuItem;
    private String userName;
    private boolean isOrganizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_user_location);
            new Thread(new ClientThread()).start();

            if (initMap()) {
                gotoLocation(CSE_LAT, CSE_LNG, 15);
                LatLng cse = new LatLng(CSE_LAT, CSE_LNG);
                mMap.addMarker(new MarkerOptions().position(cse).title("Paul G. Allen"));

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();

                mLocationClient.connect();

                //mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Map not connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        findMenuItem = menu.getItem(menu.size() - 1);
        notifyAllMenuItem = menu.getItem(menu.size() - 2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "can't connect to mapping service.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }
        return (mMap != null);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);

        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();
        Toast.makeText(this, "Searching: " + searchString, Toast.LENGTH_SHORT).show();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, "Found: " + locality, Toast.LENGTH_SHORT).show();

            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, 15);

            MarkerOptions options = new MarkerOptions()
                    .title(locality)
                    .position(new LatLng(lat, lng));
            mMap.addMarker(options);
        }
    }

    public void showCurrentLocation(MenuItem item) {

        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    latLng, 15
            );
            mMap.animateCamera(update);
        }

    }

    public void showCSELocation(MenuItem item) {
        mMap.clear();

        LatLng latLng = new LatLng(
                CSE_LAT,
                CSE_LNG
        );
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                latLng, 15
        );
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Paul G. Allen"));
        mMap.animateCamera(update);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Ready to map.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private StringBuffer getUserInformation() {
        StringBuffer res = new StringBuffer();
        res.append(userName + "\n");
        res.append(isOrganizer + "\n");
        return res;
    }

    public void showPlaces(MenuItem item) {
        try {
            isOrganizer = true;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            LatLng latLng1 = new LatLng(CSE_LAT - 0.05, CSE_LNG - 0.05);
            LatLng latLng2 = new LatLng(CSE_LAT + 0.05, CSE_LNG + 0.05);

            builder.setLatLngBounds(new LatLngBounds(latLng1, latLng2));
            Intent intent = builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == PLACE_PICKER_REQUEST) {
            // This result is from the PlacePicker dialog.

            if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data, this);

                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getAttributions(data);
                if(attribution == null){
                    attribution = "";
                }

                // Put the picked place information to the textview and
                // make it visible.
                TextView pickedPlaceView = (TextView) findViewById(R.id.textView);
                String intro = "Let's meet at:\n";
                StringBuffer placeInfo = new StringBuffer();
                //placeInfo.append(intro + "\n");
                placeInfo.append(name + "\n");
                placeInfo.append(address + "\n");
                placeInfo.append(phone + "\n");
                placeInfo.append(placeId + "\n");
                pickedPlaceView.setText(intro + placeInfo);
                pickedPlaceView.setVisibility(View.VISIBLE);

                // Updates the picked place at the map.
                LatLng latLng = place.getLatLng();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15
                );

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Picked place"));
                mMap.animateCamera(update);

                StringBuffer msg = getUserInformation();
                msg.append(placeInfo);
                sendToServer(msg);
                System.out.println("Message sent is:\n" + msg);

            } else {
                // User has not selected a place, hide the card.
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }

    private void sendToServer(StringBuffer placeInfo) {
        //TODO
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            out.println(placeInfo);
            System.out.println("Message sent to Server is " + placeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inputName(View view) {
        TextView tv = (TextView) findViewById(R.id.editText1);
        userName = tv.getText().toString();
        updateUserName();
    }

    private void updateUserName() {
        TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText("Welcome " + userName + " !");
    }

    public void notifyAll(MenuItem item) {
        Toast.makeText(this, "Notifying all users...", Toast.LENGTH_SHORT).show();
        StringBuffer message = new StringBuffer("NotifyAll\n");
        sendToServer(message);
        System.out.println("ready to wait for server");
        new ServiceTask().execute();
    }

    public void joinAsParticipant(MenuItem menuItem) {
        isOrganizer = false;
        findMenuItem.setEnabled(false);
        notifyAllMenuItem.setEnabled(false);
        if (userName != null) {
            StringBuffer userInfo = getUserInformation();
            sendToServer(userInfo);
            new ClientTask().execute();
        }
    }

//    private void waitForServer() {
        // Set a timer
//        if (isOrganizer) {
//            System.out.println("is organizer." + isOrganizer);
//            try {
//               // while (true) {
//                    BufferedReader input;
//                    System.out.print("here");
//                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    System.out.print("there");
//                    char[] buffer = new char[1024];
//                    int read = input.read(buffer);
//                    while (read > 0) {
//                        System.out.print(buffer);
//                        read = input.read(buffer);
//                    }
//                //    break;
//               // }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("is Participant.");
//            try {
//                BufferedReader input;
//                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                char[] buffer = new char[1024];
//                int read = input.read(buffer);
//                while(read > 0) {
//                    System.out.print(buffer);
//                    read = input.read(buffer);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVER_PORT);
                System.out.println("Socket is ready");

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    public class ServiceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            BufferedReader br = null;
            StringBuffer sb = new StringBuffer("");

            try {
                System.out.println("Processing in background...");
                InputStream is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                System.out.println("Start reading...");
                String str = br.readLine();
                while(str != null){
                    sb.append(str);
                    sb.append("\n");
                    str = br.readLine();
                }
                System.out.println("Input message \n" + sb.toString());

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error 3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.append("\n" + result);
        }
    }

    public class ClientTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            BufferedReader br = null;
            StringBuffer sb = new StringBuffer("");

            try {
                System.out.println("Client Processing in background...");
                InputStream is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                System.out.println("Client Start reading...");
                String str = br.readLine();
                while(str != null){
                    sb.append(str);
                    sb.append("\n");
                    str = br.readLine();
                }
                System.out.println("Input message \n" + sb.toString());

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error 3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.append("\n" + result);
        }
    }
}
