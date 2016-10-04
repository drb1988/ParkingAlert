package Model;

/**
 * Created by Sistem1 on 19/09/2016.
 */
public class CarModel {
    String mImage;
    String mMaker;
    String mCarName;

    public String getmCarName() {
        return mCarName;
    }

    public void setmCarName(String mCarName) {
        this.mCarName = mCarName;
    }

    public String getProducator() {
        return producator;
    }

    public void setProducator(String producator) {
        this.producator = producator;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    String producator;
    String model;
    String an;

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmMaker() {
        return mMaker;
    }

    public void setmMaker(String mMaker) {
        this.mMaker = mMaker;
    }

    String nr;
}
