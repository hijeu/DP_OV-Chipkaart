import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private double prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product(int productNummer, String naam, String beschrijving, double prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public Product() {
    }

    public int getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public void setOvChipkaarten(List<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        if (!ovChipkaarten.contains(ovChipkaart)) {
            ovChipkaarten.add(ovChipkaart);
        }
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
            ovChipkaarten.remove(ovChipkaart);
    }

    public boolean equals(Object andereObject) {
        boolean gelijkeObjecten = false;

        if (andereObject instanceof Product) {
             Product andereProduct = (Product) andereObject;

            if (this.productNummer == andereProduct.getProductNummer() &&
                this.naam.equals(andereProduct.getNaam()) &&
                this.beschrijving.equals(andereProduct.getBeschrijving()) &&
                this.prijs == andereProduct.getPrijs() &&
                this.ovChipkaarten.equals(andereProduct.getOvChipkaarten())) {
                gelijkeObjecten = true;
            }
        }

        return gelijkeObjecten;
    }

    public String toString() {
        return "";
    }
}
