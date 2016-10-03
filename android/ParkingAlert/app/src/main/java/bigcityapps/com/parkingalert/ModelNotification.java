package bigcityapps.com.parkingalert;

/**
 * Created by Sistem1 on 14/09/2016.
 */
public class ModelNotification {
    String image;
    String title;
    String details;
    String message;
    String hour;
    int type;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    boolean read;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    String badge;
}
