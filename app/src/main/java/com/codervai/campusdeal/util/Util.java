package com.codervai.campusdeal.util;

import android.location.Location;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Util {
    public static String calculateTimeAgo(Date uploadDate) {
        long diff = new Date().getTime() - uploadDate.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays  = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
        long diffMonths = diff / (30L * 24 * 60 * 60 * 1000);
        long diffYears = diff / (365L * 24 * 60 * 60 * 1000);

        if(diffSeconds < 60){
            return diffSeconds+" sec ago";
        }else if(diffMinutes < 60){
            return diffMinutes+" min ago";
        }else if(diffHours < 24){
            return diffHours+" hours ago";
        }else if(diffDays < 7){
            return diffDays+" days ago";
        }else if(diffWeeks < 4){
            return diffWeeks+" weeks ago";
        }else if(diffMonths < 12){
            return diffMonths+" months ago";
        }else {
            return diffYears+" years ago";
        }
    }

        /// return distance in km
    // https://stackoverflow.com/a/17983974/13877490
    public static double calculateDistance(LatLng fromLatLng, LatLng toLatLng){
        Location fromLocation = new Location("");
        fromLocation.setLatitude(fromLatLng.latitude);
        fromLocation.setLongitude(fromLatLng.longitude);

        Location toLocation = new Location("");
        toLocation.setLatitude(toLatLng.latitude);
        toLocation.setLongitude(toLatLng.longitude);

        return fromLocation.distanceTo(toLocation)/1000.0;
    }

    public static int getViewPagerFragmentIndex(Fragment fragment, int numberOfFragment){
        //get fragment tag set by the view pager
        // https://stackoverflow.com/questions/55728719/get-current-fragment-with-viewpager2
        int startIndex = fragment.toString().indexOf("tag=") + 4; // Start index of the tag value
        if(startIndex<0) return -1;
        int endIndex = fragment.toString().indexOf(")", startIndex); // End index of the tag value
        String fragmentTag = fragment.toString().substring(startIndex, endIndex);
        Log.d("Util.getViewPagerFragmentIndex", "onViewCreated: fragment tag = "+fragmentTag);
        int index = Integer.parseInt(fragmentTag.substring(1));
        if(index<0 || index>=numberOfFragment) return -1;
        return index;
    }
}
