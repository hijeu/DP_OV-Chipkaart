import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAO ovcdao;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setOVCdao(OVChipkaartDAO ovcdao) {
        this.ovcdao = ovcdao;
    }

    @Override
    public boolean save(Product product) {
        int productsSaved = 0;

        String q = "insert into product (product_nummer, naam, beschrijving, prijs) values (?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, product.getProductNummer());
            pst.setString(2, product.getNaam());
            pst.setString(3, product.getBeschrijving());
            pst.setDouble(4, product.getPrijs());
            productsSaved = pst.executeUpdate();

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) values (?, ?)";
            List<OVChipkaart> ovChipkaarten = product.getOvChipkaarten();
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                ovcdao.save(ovChipkaart);
                try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                    pst2.setInt(1, ovChipkaart.getKaartNummer());
                    pst2.setInt(2, product.getProductNummer());
                    pst2.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsSaved > 0;
    }

    //3d. ii
    @Override
    public boolean update(Product product) {
        int productsUpdated = 0;

        String q = "update product set naam = ?, beschrijving = ?, prijs = ? where product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, product.getNaam());
            pst.setString(2, product.getBeschrijving());
            pst.setDouble(3, product.getPrijs());
            pst.setInt(4, product.getProductNummer());
            productsUpdated = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsUpdated > 0;
    }

    @Override
    public boolean delete(Product product) {
        int productsDeleted = 0;

        String q = "delete from product where product_nummer = ? and " +
                "naam = ? and " +
                "beschrijving = ? and " +
                "prijs = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, product.getProductNummer());
            pst.setString(2, product.getNaam());
            pst.setString(3, product.getBeschrijving());
            pst.setDouble(4, product.getPrijs());
            productsDeleted = pst.executeUpdate();

            q = "delete from ov_chipkaart_product where product_nummer = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst2.setInt(1, product.getProductNummer());
                pst2.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsDeleted > 0;
    }

    //3.d iii
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        List<Product> producten = new ArrayList<>();

        return producten;
    }

    //3.d iv
    public List<Product> findAll() {
        List<Product> producten = new ArrayList<>();

        return producten;
    }
}
