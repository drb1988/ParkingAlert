package bigcityapps.com.parkingalert;

/**
 * Created by Sistem1 on 14/09/2016.
 */
public class ModelNotification {
    String mImage;
    String mTitle;
    String mDetails;
    String mMessage;
    String mHour;
    int mType;
    boolean extended;
    boolean receiverRead;

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    boolean feedback;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    String picture;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    String lat, lng;

    public boolean isSenderRead() {
        return senderRead;
    }

    public void setSenderRead(boolean senderRead) {
        this.senderRead = senderRead;
    }

    boolean senderRead;

    public String getExtension_time() {
        return extension_time;
    }

    public void setExtension_time(String extension_time) {
        this.extension_time = extension_time;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    String extension_time;


    public boolean isReceiverRead() {
        return receiverRead;
    }

    public void setReceiverRead(boolean receiverRead) {
        this.receiverRead = receiverRead;
    }



    public String getNr_car() {
        return nr_car;
    }

    public void setNr_car(String nr_car) {
        this.nr_car = nr_car;
    }

    String nr_car;

    public String getEstimeted_time() {
        return estimeted_time;
    }

    public void setEstimeted_time(String estimeted_time) {
        this.estimeted_time = estimeted_time;
    }

    String estimeted_time;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }



    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getmDetails() {
        return mDetails;
    }

    public void setmDetails(String mDetails) {
        this.mDetails = mDetails;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmHour() {
        return mHour;
    }

    public void setmHour(String mHour) {
        this.mHour = mHour;
    }

    String badge;
}
