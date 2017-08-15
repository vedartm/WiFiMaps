package com.venky97vp.android.googlemapsdemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
//import com.lemmingapex.trilateration.TrilaterationFunction;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import com.venky97vp.android.googlemapsdemo.dummy.DummyContent;

//import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
//import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
//import org.apache.commons.math3.linear.RealMatrix;
//import org.apache.commons.math3.linear.RealVector;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, LocationFragment.OnListFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    GoogleMap gMap;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    FloatingSearchView mSearchView;
    FloatingActionButton direction;
    ProgressBar progressBar;

    private Marker homeMarker;
    private Marker chithMarker;
    private Marker tifacMarker;
    private Marker hospitalMarker;

    private LatLng from;
    private LatLng to;
    private LatLng sastra;

    BottomSheetBehavior bottomSheetBehavior;
    static final String API_KEY = "AIzaSyBimm1t9r-4-iUQ0dfpO771V6JVGaLgEgY";
    TextView titleOfLocation;
    TextView desciptionOfLocation;
    ArrayList<LatLng> directionPositionList;
    ArrayList<Position> positions;
    Polyline polyline;
    WifiManager wifi;

    ArrayList<WiFiApi> apis;
    private LatLng currentLocation;

    public void setWifiApis(){
        apis = new ArrayList<>();
        apis.add(ApiObjects.wiFiApi1);
        apis.add(ApiObjects.wiFiApi2);
        apis.add(ApiObjects.wiFiApi3);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private boolean isThere(Location location1, Location location2) {
        double x1 = location1.getLatitude();
        double y1 = location1.getLongitude();
        double x2 = location2.getLatitude();
        double y2 = location2.getLongitude();
        double distance = distance(x1, y1, x2, y2);
        return distance <= 10;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        hasPermissions(getApplicationContext(),PERMISSIONS);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.attachNavigationDrawerToMenuButton(drawer);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                //mSearchView.swapSuggestions(newSuggestions);
            }
        });
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(, 16));
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.location_button);
        fab.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setBottomSheet();
        setWifiApis();

        direction.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        wifiWork();
    }

    private void setBottomSheet() {
        RelativeLayout llBottomSheet = (RelativeLayout) findViewById(R.id.bottom_sheet);
        direction = (FloatingActionButton) llBottomSheet.findViewById(R.id.get_direction);
        titleOfLocation = (TextView) llBottomSheet.findViewById(R.id.location_title);
        desciptionOfLocation = (TextView) llBottomSheet.findViewById(R.id.location_description);
        progressBar = (ProgressBar) llBottomSheet.findViewById(R.id.progressBar2);
        progressBar.setScaleY(1.5f);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Log.d(TAG, "setBottomSheet: BottomSheet hidden");

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "onStateChanged: changed");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "onStateChanged: sliding : " + slideOffset);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setOnMarkerClickListener(this);
        setLocations();
    }

    private void setLocations() {
        from = new LatLng(10.730501, 79.016631);
        sastra = new LatLng(10.728434, 79.018800);
        LatLng tifac = new LatLng(10.729361, 79.020278);
        LatLng chith = new LatLng(10.729056, 79.019581);
        LatLng home = new LatLng(10.775194, 79.118222);
        LatLng hospital = new LatLng(10.729267, 79.018966);

        positions = new ArrayList<>();

        positions.add(new Position("Sastra", sastra));
        positions.add(new Position("Chith Vihar", chith));
        positions.add(new Position("Home", home));
        positions.add(new Position("Tifac Core", tifac));
        positions.add(new Position("Aarokya Vaidhya Sala", hospital));

        chithMarker = gMap.addMarker(new MarkerOptions()
                .position(chith)
                .title("Chith Vihar"));
//        gMap.addCircle(new CircleOptions()
//                .center(chith)
//                .radius(50)
//                .strokeWidth(0f)
//                .fillColor(0x550000FF));
//        gMap.addCircle(new CircleOptions()
//                .center(home)
//                .radius(5)
//                .strokeWidth(0f)
//                .fillColor(0x550000FF));
        homeMarker = gMap.addMarker(new MarkerOptions()
                .position(home)
                .title("Home"));
        tifacMarker = gMap.addMarker(new MarkerOptions()
                .position(tifac)
                .title("Tifac Core"));
        hospitalMarker = gMap.addMarker(new MarkerOptions()
                .position(hospital)
                .title("Aarokya Vaidhya Sala"));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sastra, 16));

    }

    public void isGPSEnable() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Log.d(TAG, "onBackPressed: hidden");
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sastra, 16));
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_satellite:
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.nav_sketch:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.nav_places:
                //fragment = new LocationFragment();
                break;
            case R.id.nav_feedback:
                //fragment = new AddFragment();
                break;
            case R.id.nav_help:
                //fragment = new AddFragment();
                break;
            case R.id.nav_settings:
                //fragment = new AddFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.map, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(homeMarker)) {
            setLocationBottomSheet(positions.get(2));
            to = positions.get(2).getPosition();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 18));
            return true;
        } else if (marker.equals(chithMarker)) {
            setLocationBottomSheet(positions.get(1));
            to = positions.get(1).getPosition();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 18));
            return true;
        } else if (marker.equals(tifacMarker)) {
            setLocationBottomSheet(positions.get(3));
            to = positions.get(3).getPosition();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 18));
            return true;
        } else if (marker.equals(hospitalMarker)) {
            setLocationBottomSheet(positions.get(4));
            to = positions.get(4).getPosition();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 18));
            return true;
        }
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 18));
        return false;
    }

    private void setLocationBottomSheet(Position l) {
        if (l != null) {
            titleOfLocation.setText(l.getTitle());
            getDuration();
//            String sol = getDuration();
//            int x = sol.indexOf('-');
//            l.setDistance(sol.substring(0,x));
//            l.setDuration(sol.substring(x+1,sol.length()));
//            desciptionOfLocation.setText(l.getDistance()+"\n"+l.getDuration());
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_direction:
                if (from != null && to != null) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                    DirectionGetTask task = new DirectionGetTask();
                    task.execute(null,null,null);
                } else {
                    Log.d("MapsActivity", "getDirection: from or to is null");
                }
                break;
            case R.id.location_button:
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                gMap.setMyLocationEnabled(true);
//                gMap.getUiSettings().setMyLocationButtonEnabled(false);
                if (wifi != null) {
                    wifi.startScan();
                    List<ScanResult> results = wifi.getScanResults();
                    ArrayList<ResultApi> nearbyApis = new ArrayList<>();
                    Log.d(TAG, "onClick: got the results");
                    for (ScanResult s : results) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        //Log.d(TAG, s.SSID + ": " + s.level + ", d: " + df.format(calculateDistance((double) s.level, s.frequency)) + "m");
                        if (s.SSID.length()==5 && s.SSID.substring(0,4).equals("WiPi")) {
                            double distance = calculateDistance((double) s.level, s.frequency);
                            Log.d(TAG, s.SSID + ": " + s.level + ", d: " + df.format(distance) + "m");
                            nearbyApis.add(new ResultApi(s,distance));
                        }
                    }
                    currentLocation = getApiLocation(nearbyApis);
                    gMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title("My Location"));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));
//                    registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                } else {
                    Log.d(TAG, "onClick: wifi is null");
                }
                break;
        }
    }

    String result;

    private void getDuration() {
        if (from != null && to != null) {
            GoogleDirection.withServerKey(API_KEY)
                    .from(from)
                    .to(to)
                    .transportMode(TransportMode.WALKING)
                    .unit(Unit.METRIC)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            Info duration = leg.getDuration();
                            Info distance = leg.getDistance();
                            result = distance.getText() + "-" + duration.getText();
                            int x = result.indexOf('-');
                            desciptionOfLocation.setText("Distance : " + result.substring(0, x) + "\n" + "Duration : " + result.substring(x + 1, result.length()));
                            //directionPositionList = leg.getDirectionPoint();
//                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 6, 0x440000FF);
//                            gMap.addPolyline(polylineOptions);
                            //List<StepList> = leg.getStepList();
                            //Toast.makeText(getApplicationContext(),"Working : "+direction.getStatus(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error : " + t.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Log.d("MapsActivity", "getDirection: from or to is null");
        }
    }

    private class DirectionGetTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            GoogleDirection.withServerKey(API_KEY)
                    .from(from)
                    .to(to)
                    .transportMode(TransportMode.WALKING)
                    .unit(Unit.METRIC)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 6, 0x440000FF);
                            polyline = gMap.addPolyline(polylineOptions);
                            Info duration = leg.getDuration();
                            Info distance = leg.getDistance();
                            Log.d(TAG, "onDirectionSuccess: " + distance.getText() + "-" + duration.getText());
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(to);
                            builder.include(from);
                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 16));
                            //List<StepList> = leg.getStepList();
                            //Toast.makeText(getApplicationContext(),"Working : "+direction.getStatus(),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
//                            Toast.makeText(getApplicationContext(), "Error : " + t.toString(), Toast.LENGTH_LONG).show();
                            CoordinatorLayout coordinate = (CoordinatorLayout) findViewById(R.id.coordinate);
                            Snackbar snackbar = Snackbar
                                    .make(coordinate, "Your device is offline", Snackbar.LENGTH_LONG)
                                    .setAction("GO ONLINE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                        }
                                    });

                            snackbar.show();
                            Log.e(TAG, "onDirectionFailure: ", t);
                        }
                    });
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
        }
    }

    //MyBroadcast broadcastReceiver;

    private void wifiWork() {
        Log.d(TAG, "wifiWork: inside");
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
            Log.d(TAG, "wifiWork: wifi enabled");
        } else {
            Log.d(TAG, "wifiWork: already enabled");
        }
        //broadcastReceiver = new MyBroadcast();
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    @Override
    protected void onStop() {
        //unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

//    class MyBroadcast extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            List<ScanResult> results = wifi.getScanResults();
//            ArrayList<ResultApi> nearbyApis = new ArrayList<>();
//            Log.d(TAG, "onClick: got the results");
//            for (ScanResult s : results) {
//                DecimalFormat df = new DecimalFormat("#.##");
//                if (s.SSID.substring(0,5).equals("WiPi")) {
//                    double distance = calculateDistance((double) s.level, s.frequency);
//                    Log.d(TAG, s.BSSID + ": " + s.level + ", d: " + df.format(distance) + "m");
//                    nearbyApis.add(new ResultApi(s,distance));
//                }
//            }
//            currentLocation = getApiLocation(nearbyApis);
//        }
//    }

    public LatLng getApiLocation(ArrayList<ResultApi> list){
        double[][] positions = new double[][] {};
        double[] distances = new double[] {};
        for (int i=0;i<list.size();i++){
            distances[i] = list.get(i).getDistance();
            LatLng l = list.get(i).getLatLng();
            positions[i] = new double[]{l.latitude, l.longitude};
        }
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();

// the answer
        double[] centroid = optimum.getPoint().toArray();

        Log.d(TAG, "getApiLocation: "+centroid[0]+ " " + centroid[1]);

// error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

//        double distanceSum = 0;
//        double x = 0;
//        double y = 0;
//        for (ResultApi s : list) {
//            for (WiFiApi w : apis) {
//                if(s.getScanResult().SSID.equals(w.getName())){
//                    s.setLatLng(w.getLatLng());
//                }
//            }
//            distanceSum+=s.getDistance();
//        }
//        Log.d(TAG, "getApiLocation: sum = "+distanceSum);
//        for (ResultApi s :
//                list) {
//            double dm = s.getDistance()/distanceSum;
//            Log.d(TAG, "getApiLocation: dm = "+dm);
//            Log.d(TAG, "getApiLocation: loc = "+s.getLatLng().latitude * dm +"  "+s.getLatLng().longitude * dm);
//            x+=s.getLatLng().latitude * dm;
//            y+=s.getLatLng().longitude * dm;
//        }
//
//
//        x/=list.size();
//        y/=list.size();
////        double x1 = w.getLatLng().latitude;
////        double x2 = w.getLatLng().longitude;
//        Log.d(TAG, "getApiLocation: latlong = "+x+" "+y);
        return new LatLng(centroid[0],centroid[1]);
    }
}
