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

    public static final List<String> DEALS_TAB_MENU_LIST = new ArrayList<String>() {
        {
            add("Request for me"); // deal_request/<adId>
            add("My request");
            add("My purchase"); // users/<uid>/deals
            add("My sell"); // users/<uid>/buy_history
        }
    };

    // category icons
    public  static  final List<Integer> CATEGORY_ICON_LIST = new ArrayList<Integer>() {{
        add(R.drawable.cat_books_svgrepo_com);
        add(R.drawable.cat_stationery_compass_svgrepo_com);
        add(R.drawable.cat_electronic_game_console_psp_svgrepo_com);
        add(R.drawable.cat_clothes_black_turtleneck_svgrepo_com);
        add(R.drawable.cat_sports_man_bouncing_ball_svgrepo_com);
        add(R.drawable.cat_musical_guitar_svgrepo_com);
        add(R.drawable.baseline_category_24);
    }};

    // make a list of profile menu options
    // options: my ads, my wishlist, edit profile
    public static final  List<ProfileMenuItem> PROFILE_MENU_OPTIONS = new ArrayList<ProfileMenuItem>() {
        {
            add(new ProfileMenuItem("My Products", R.drawable.price_tag_rotate_svgrepo_com, "View your ads"));
            add(new ProfileMenuItem("My Wishlist", R.drawable.baseline_favorite_24, "View your wishlist"));
            add(new ProfileMenuItem("Edit Profile", R.drawable.baseline_edit_24, "Edit your profile"));
        }
    };

    // firebase fire store constants
    public static final String USER_COLLECTION = "users";
    public static final String PRODUCT_COLLECTION = "products";

    public static final String WISHLIST_COLLECTION = "wishlist";

    public static final String DEAL_REQUEST_COLLECTION = "deal_request";
}
