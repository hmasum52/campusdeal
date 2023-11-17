package com.codervai.campusdeal.util;

import com.codervai.campusdeal.R;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    // name of the categories
    public  static final List<String> CATEGORY_LIST = new ArrayList<String>() {{
        add("Books");
        add("Stationery");
        add("Electronics and Gadgets");
        add("Clothes");
        add("Sports");
        add("Musical Instruments");
        add("Others");
    }};

    // make a list of profile menu options
    // options: my ads, my wishlist, edit profile
    public static final  List<ProfileMenuItem> PROFILE_MENU_OPTIONS = new ArrayList<ProfileMenuItem>() {
        {
            add(new ProfileMenuItem("My Ads", R.drawable.price_tag_rotate_svgrepo_com, "View your ads"));
            add(new ProfileMenuItem("My Wishlist", R.drawable.baseline_favorite_24, "View your wishlist"));
            add(new ProfileMenuItem("Edit Profile", R.drawable.baseline_edit_24, "Edit your profile"));
        }
    };

    // firebase fire store constants
    public static final String USER_COLLECTION = "users";
}
