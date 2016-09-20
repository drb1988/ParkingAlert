package Model;

/**
 * Created by Sistem1 on 19/09/2016.
 */
public class MasiniModel {
    String poza;
    String proprietar;
    String nume_masina;

    public String getNume_masina() {
        return nume_masina;
    }

    public void setNume_masina(String nume_masina) {
        this.nume_masina = nume_masina;
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

    public String getPoza() {
        return poza;
    }

    public void setPoza(String poza) {
        this.poza = poza;
    }

    public String getProprietar() {
        return proprietar;
    }

    public void setProprietar(String proprietar) {
        this.proprietar = proprietar;
    }

    String nr;
}
