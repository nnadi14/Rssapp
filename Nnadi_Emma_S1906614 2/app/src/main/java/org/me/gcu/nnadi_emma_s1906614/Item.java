package org.me.gcu.nnadi_emma_s1906614;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String author, category, description, link, pubDate, title, road, region, county, latitude, longitude;

    public Item() {
    }

    protected Item(Parcel in) {
        author = in.readString();
        category = in.readString();
        description = in.readString();
        link = in.readString();
        pubDate = in.readString();
        title = in.readString();
        road = in.readString();
        region = in.readString();
        county = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(category);
        parcel.writeString(description);
        parcel.writeString(link);
        parcel.writeString(pubDate);
        parcel.writeString(title);
        parcel.writeString(road);
        parcel.writeString(region);
        parcel.writeString(county);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
    }

    @Override
    public String toString() {
        return "Item{" +
                "author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", title='" + title + '\'' +
                ", road='" + road + '\'' +
                ", region='" + region + '\'' +
                ", county='" + county + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
