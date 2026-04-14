package com.myapplication.matapp2.tourist_packages;

import java.io.Serializable;
import java.util.List;

public class TouristPackage implements Serializable {
    private String name;
    private String rating;
    private String price;
    private String duration;
    private String tags;
    private String description;
    private String emoji;
    private List<String> inclusions;
    private List<String> itinerary;

    public TouristPackage(String name, String rating, String price, String duration,
                          String tags, String description, String emoji,
                          List<String> inclusions, List<String> itinerary) {
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.duration = duration;
        this.tags = tags;
        this.description = description;
        this.emoji = emoji;
        this.inclusions = inclusions;
        this.itinerary = itinerary;
    }

    public String getName()        { return name; }
    public String getRating()      { return rating; }
    public String getPrice()       { return price; }
    public String getDuration()    { return duration; }
    public String getTags()        { return tags; }
    public String getDescription() { return description; }
    public String getEmoji()       { return emoji; }
    public List<String> getInclusions() { return inclusions; }
    public List<String> getItinerary()  { return itinerary; }
}
