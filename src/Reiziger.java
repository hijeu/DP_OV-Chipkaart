import java.sql.Date;
import java.util.Objects;

public class Reiziger {
    private int id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;
    private Adres adres;

    public Reiziger(int id, String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum) {
        this.id = id;
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

    public Reiziger() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletters() {
        return voorletters;
    }

    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public String toString() {
        String s;

        if (adres == null) {
            s = "#" + getId() + ": " + getVoorletters() + ". ";
            if (!Objects.equals(getTussenvoegsel(), "") && getTussenvoegsel() != null) {
                s += getTussenvoegsel() + " ";
            }
            s += getAchternaam() + " (" + getGeboortedatum() + ")";
        } else {
            s = String.format("Reiziger {#%d %s. ",
                    getId(),
                    getVoorletters());
            if (!Objects.equals(getTussenvoegsel(), "") && getTussenvoegsel() != null) {
                s += getTussenvoegsel() + " ";
            }
            s += String.format("%s, geb. ", getAchternaam()) + getGeboortedatum();
            s += String.format(", Adres {#%d %s %s, %s, %s}}",
                    adres.getId(),
                    adres.getStraat(),
                    adres.getHuisnummer(),
                    adres.getPostcode(),
                    adres.getWoonplaats());
        }
        return s;
    }
}
