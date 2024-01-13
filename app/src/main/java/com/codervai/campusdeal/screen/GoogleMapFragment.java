package com.codervai.campusdeal.screen;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codervai.campusdeal.R;
import com.codervai.campusdeal.databinding.FragmentGoogleMapBinding;
import com.codervai.campusdeal.databinding.FragmentOnBoardingBinding;
import com.codervai.campusdeal.model.MyLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GoogleMapFragment extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    private FragmentGoogleMapBinding mVB;

    private GoogleMap mMap;

    private Geocoder geocoder;

    private MyLocation selectedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentGoogleMapBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        // init google map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // pick location button click
        mVB.pickLocationBtn.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            if(navController.getPreviousBackStackEntry() != null){
                navController.getPreviousBackStackEntry()
                        .getSavedStateHandle()
                        .set("location", Parcels.wrap(selectedLocation));
                // navigate to caller fragment
                navController.popBackStack();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraIdleListener(this);

        // Add a marker in Sydney and move the camera
        LatLng tsc = new LatLng(23.729385688619754, 90.40337686367819);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tsc,16));
        updateAddress(tsc);
    }

    // ui
    private void updateAddress(LatLng latLng){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = createFullAddress(address);
                selectedLocation = new MyLocation(latLng, address.getAdminArea(),addressText);
                mVB.addressTv.setText(addressText);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private String createFullAddress(Address address){
        StringBuilder builder = new StringBuilder("");

        if(address.getAddressLine(0)!=null){
            builder.append(address.getAddressLine(0)).append(",");
        }

        if(address.getLocality()!=null){
            builder.append(address.getLocality()).append(",");
        }

        if(address.getSubAdminArea()!=null){
            builder.append(address.getSubAdminArea()).append(",");
        }

        if(address.getAdminArea()!=null){
            builder.append(address.getAdminArea());
        }

        return builder.toString();
    }

    @Override
    public void onCameraMoveStarted(int i) {
        mVB.mapPin.setVisibility(View.VISIBLE);
    }


    @Override
    public void onCameraIdle() {
        mVB.mapPin.setVisibility(View.GONE);
        LatLng focus = mMap.getCameraPosition().target;
        updateAddress(focus);
    }


}