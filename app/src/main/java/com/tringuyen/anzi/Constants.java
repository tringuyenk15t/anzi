package com.tringuyen.anzi;

/**
 * Created by Tri Nguyen on 11/26/2016.
 */

public class Constants {

    public static final String TOOLBAR_TITLE = "ANZI";
    public static final String GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/";
    public static final String GOOGLE_PALCE_API_KEY = "AIzaSyC_SieEFJm4VPiOJa8T0D8P8-RgzPutuIg";

    public static final String LOCATION_ID = "LocationID";
    //TODO update http builder later
    public static final String TEMP_IMAGE_URL =
            "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyC_SieEFJm4VPiOJa8T0D8P8-RgzPutuIg&maxheight=300&maxwidth=300&photoreference=";
    public static final String TEMP_DETAIL_IMAGE_URL =
            "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyC_SieEFJm4VPiOJa8T0D8P8-RgzPutuIg&maxheight=500&maxwidth=800&photoreference=";

    public static final String INITIAL_LAT_LOCATION = "iniLat";
    public static final String INITIAL_LNG_LOCATION = "iniLng";

    public static final int PERMISSION_ACCESS_FINE_LOCATION = 70;

    public static final int DETAIL_TO_MAP_FLAG = 1;
    public static final int SEARCH_TO_MAP_FLAG = 0;
    public static final String MAP_FLAG = "mapFlag";
}
