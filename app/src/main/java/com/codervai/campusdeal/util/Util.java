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
}
