package com.myapplication.matapp2;

public class TouristDestination {
    private String name;
    private String location;
    private String rating;
    private String priceTitle;
    private String priceRange;
    private String priceSubtext;
    private int imageResId;
    private Class<?> targetActivity;
    private boolean isUnesco;

    public TouristDestination(String name, String location, String rating, 
                              String priceTitle, String priceRange, String priceSubtext, 
                              int imageResId, Class<?> targetActivity, boolean isUnesco) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.priceTitle = priceTitle;
        this.priceRange = priceRange;
        this.priceSubtext = priceSubtext;
        this.imageResId = imageResId;
        this.targetActivity = targetActivity;
        this.isUnesco = isUnesco;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getRating() { return rating; }
    public String getPriceTitle() { return priceTitle; }
    public String getPriceRange() { return priceRange; }
    public String getPriceSubtext() { return priceSubtext; }
    public int getImageResId() { return imageResId; }
    public Class<?> getTargetActivity() { return targetActivity; }
    public boolean isUnesco() { return isUnesco; }
}
