package com.codervai.campusdeal.util;

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

    // firebase fire store constants
    public static final String USER_COLLECTION = "users";
}
