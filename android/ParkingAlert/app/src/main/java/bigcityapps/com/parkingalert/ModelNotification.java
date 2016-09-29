package bigcityapps.com.parkingalert;

/**
 * Created by Sistem1 on 14/09/2016.
 */
public class ModelNotification {
    String poza;
    String titlu;
    String detalii;
    String mesaj;
    String ora;
    int tip;

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

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }



    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getPoza() {
        return poza;
    }

    public void setPoza(String poza) {
        this.poza = poza;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getDetalii() {
        return detalii;
    }

    public void setDetalii(String detalii) {
        this.detalii = detalii;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    String badge;
}
