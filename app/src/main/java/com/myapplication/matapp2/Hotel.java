package com.myapplication.matapp2;

public class Hotel {
    private String id, name, imageUrl, price, currency, rating, address;

    public Hotel(String id, String name, String imageUrl, String price, String currency, String rating, String address) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.currency = currency;
        this.rating = rating;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getPrice() { return price; }
    public String getCurrency() { return currency; }
    public String getRating() { return rating; }
    public String getAddress() { return address; }
}