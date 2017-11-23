package project.MapImplementation;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.lang.Double.parseDouble;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //with training
    public static final String LONDON_DRUGS_ADDRESS = "New Westminster London Drugs 100–555 6th Street";
    public static final String SAFEWAY_MCBRIDE_ADDRESS = "New Westminster Safeway 800 McBride Boulevard";
    public static final String SAFEWAY_CARNARVON_ADDRESS = "New Westminster Safeway 220-800 Carnarvon Street";

    //Without training
    public static final String PHARMASAVE_ADDRESS = "New Westminster Pharmasave 130-1005 Columbia Street";
    public static final String SAVEONFOODS_ADDRESS = "New Westminister Save-On-Foods 270 E Columbia Street";



    private GoogleMap mMap;
    Button btnShowCoord;
    EditText edtAddress;
    TextView txtCoord;
    double lat = 0;
    double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnShowCoord = (Button)findViewById(R.id.btnShowCoordinates);
        edtAddress = (EditText)findViewById(R.id.edtAddress);
        txtCoord = (TextView)findViewById(R.id.txtCoordinates);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnShowCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetCoordinates().execute(edtAddress.getText().toString().replace(" ","+"));
            }
        });
    }
    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                lat = parseDouble(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString());

                lng = parseDouble(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString());

                txtCoord.setText(String.format("Coordinates : %s / %s ",lat,lng));
                // System.out.println("OVER HERE MY MAN  " + parseDouble(lat) + "   " + parseDouble(lng));

                onMapReady(mMap);
                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        mMap = googleMap;
        mMap.clear();
        // Add a marker in Sydney and move the camera
        LatLng closest = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(closest).title("Home"));
        mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(this, LONDON_DRUGS_ADDRESS)).title("1"));
        mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(this, SAFEWAY_MCBRIDE_ADDRESS)).title("1"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closest, 13));

    }

    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        LatLng p1 = null;
        List<Address> address;
        try
        {
            address = coder.getFromLocationName(strAddress, 2);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;
    }
}
