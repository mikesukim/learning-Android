package edu.csce4623.ahnelson.uncleroyallaroundyou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkItem;
import edu.csce4623.ahnelson.uncleroyallaroundyou.data.MarkItemRepository;
import util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MarkContract.View {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    MarkContract.Presenter markPresenter;
    private Button myLocationButton;
    private FusedLocationProviderClient mFusedLocationClient;

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    private boolean getLocationSucceed = false;
    private double currentLatitude;
    private double currentLongitude;
    private String currentPhotoPath;
    private long currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mapFragment != null) {
            transaction.add(R.id.cfFrameLayout, mapFragment);
            transaction.commit();
        }
        mapFragment.getMapAsync(this);



        //Connect to Presenter
        MarkPresenter markPresenter = new MarkPresenter(MarkItemRepository.getInstance(new AppExecutors(),getApplicationContext()), this);
    }



    // Google Map Overrides Functions
    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {

            // Add Button Listener
            myLocationButton = (Button) findViewById(R.id.btnGetLocation);
            myLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLocationAndLog();
                    dispatchTakePictureIntent();
                }
            });


            mMap = googleMap;
            enableMyLocation();
            getLocationAndLog();


            // Configure InfoWindow of Mark
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.mark_window, null);

                    // Getting the position from the marker
                    LatLng latLng = marker.getPosition();
                    TextView latitudeTextView = (TextView) v.findViewById(R.id.latitudeTextView);
                    TextView longitudeTextView = (TextView) v.findViewById(R.id.longitudeTextView);
                    TextView markDateTextView = (TextView) v.findViewById(R.id.markDateTextView);
                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
                    latitudeTextView.setText("Latitude: " + latLng.latitude);
                    longitudeTextView.setText("Longitude: "+ latLng.longitude);

                    ArrayList<String> snippetArray = new ArrayList<String>(Arrays.asList(marker.getSnippet().split(",")));
                    String imageURI = snippetArray.get(0);
                    String markDate = snippetArray.get(1);


                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm");
                    String strDate = dateFormat.format( new Date(Long.valueOf(markDate)));


                    markDateTextView.setText("MarkDate: " + strDate);
                    setPic(imageView,imageURI);

                    return v;
                }
            });

            // Load MarkItems
            markPresenter.loadMarkItems();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    enableMyLocation();
                } else {
                    // permission denied
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            //Put marker
            MarkItem markItem = new MarkItem();
            markItem.setLatitude(currentLatitude);
            markItem.setLongitude(currentLongitude);
            markItem.setImage(currentPhotoPath);
            markItem.setMarkDate(currentDate);
            markItem.setTitle("Title");

            markPresenter.createMarkItem(markItem);
            LatLng currentLocation = new LatLng(currentLatitude,currentLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }



    // Contract Overrides Functions
    @Override
    public void setPresenter(MarkContract.Presenter presenter) {
        markPresenter = presenter;
    }

    @Override
    public void showMarkItems(List<MarkItem> markItems) {

        if(markItems != null){

            mMap.clear();
            for (int i = 0; i < markItems.size(); i++) {
                MarkItem markItem = markItems.get(i);
                String snippet = "";
                snippet += markItem.getImage() + "," + String.valueOf(markItem.getMarkDate());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(markItem.getLatitude(), markItem.getLongitude()))
                        .title(markItem.getTitle())
                        .snippet(snippet)
                );

            }




        }
    }


    // Local Functions
    private void getLocationAndLog() {
        if (mFusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MapsActivity", "No Location Permission");
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("MapsActivity", "" + location.getLatitude() + ":" + location.getLongitude());
                        getLocationSucceed = true;
                        currentLatitude =  location.getLatitude();
                        currentLongitude =  location.getLongitude();
                    }
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("MainActivity",ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),"edu.csce4623.ahnelson.uncleroyallaroundyou.fileprovider",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        Date date = new Date();
        String timeStamp = simpleDateFormat.format(date);
        currentDate = date.getTime();

        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic(ImageView myImageView, String uri) {
        // Get the dimensions of the View
//        int targetW = myImageView.getWidth();
//        int targetH = myImageView.getHeight();

        int targetW = 200;
        int targetH = 200;
        Log.d("YOLO", String.valueOf(targetW));
        Log.d("YOLO", String.valueOf(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(uri, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uri, bmOptions);
        myImageView.setImageBitmap(bitmap);
    }


}


