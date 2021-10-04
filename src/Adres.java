public class Adres {
    private int adresNummer;
    private String postcode;
    private String huisnummer;
    private String straat;
    private String woonplaats;
    private Reiziger reiziger;

    public Adres(int adresNummer, String postcode, String huisnummer, String straat, String woonplaats, Reiziger reiziger) {
        this.adresNummer = adresNummer;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reiziger = reiziger;
    }

    public Adres() {
    }

    public int getAdresNummer() {
        return adresNummer;
    }

    public void setId(int adresNummer) {
        this.adresNummer = adresNummer;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public String toString() {
        return String.format(" Adres {#%d %s %s, %s, %s}",
                getAdresNummer(),
                getStraat(),
                getHuisnummer(),
                getPostcode(),
                getWoonplaats());
    }
}
