import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAO ovcdao;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setOVCDao (OVChipkaartDAO ovcdao) {
        this.ovcdao = ovcdao;
    }

    @Override
    public boolean save(Product product) {
        int productsSaved = 0;

        String q = "insert into product (product_nummer, naam, beschrijving, prijs) values (?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, product.getProductnummer());
            pst.setString(2, product.getNaam());
            pst.setString(3, product.getBeschrijving());
            pst.setDouble(4, product.getPrijs());
            productsSaved = pst.executeUpdate();

            List<OVChipkaart> ovChipkaarten = product.getOvChipkaarten();

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) values (?, ?)";

            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                if (!ovcdao.findAll().contains(ovChipkaart)) {
                    ovcdao.save(ovChipkaart);
                }
                try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                    pst2.setInt(1, ovChipkaart.getKaartNummer());
                    pst2.setInt(2, product.getProductnummer());
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

    @Override
    public boolean update(Product product) {
        int productsUpdated = 0;

        String q = "update product set naam = ?, beschrijving = ?, prijs = ? where product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, product.getNaam());
            pst.setString(2, product.getBeschrijving());
            pst.setDouble(3, product.getPrijs());
            pst.setInt(4, product.getProductnummer());
            productsUpdated = pst.executeUpdate();

            q = "delete from ov_chipkaart_product where product_nummer = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst2.setInt(1, product.getProductnummer());
            } catch (Exception e) {
                e.printStackTrace();
            }

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) " +
                    "values (?, " + product.getProductnummer() + ")";

            List<OVChipkaart> ovChipkaarten = ovcdao.findByProduct(product);
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                    pst2.setInt(1, ovChipkaart.getKaartNummer());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsUpdated > 0;
    }

    @Override
    public boolean delete(Product product) {
        int productsDeleted = 0;

        String q = "delete from ov_chipkaart_product where product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, product.getProductnummer());
            pst.executeUpdate();

            q = "delete from product where product_nummer = ? and " +
                    "naam = ? and " +
                    "beschrijving = ? and " +
                    "prijs = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst2.setInt(1, product.getProductnummer());
                pst2.setString(2, product.getNaam());
                pst2.setString(3, product.getBeschrijving());
                pst2.setDouble(4, product.getPrijs());
                productsDeleted = pst2.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsDeleted > 0;
    }

    public Product findById(int id) {
        Product product = new Product();

        String q = "select * from product where product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            product.setProductnummer(rs.getInt("product_nummer"));
            product.setNaam(rs.getString("naam"));
            product.setBeschrijving(rs.getString("beschrijving"));
            product.setPrijs(rs.getDouble("prijs"));
            product.setOvChipkaarten(ovcdao.findByProduct(product));
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return product;
    }

    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        List<Product> producten = new ArrayList<>();

        String q = "select p.product_nummer, p.naam, p.beschrijving, p.prijs " +
                "from product p " +
                "join ov_chipkaart_product ovcp " +
                "on p.product_nummer = ovcp.product_nummer " +
                "where ovcp.kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductnummer(rs.getInt("product_nummer"));
                product.setNaam(rs.getString("naam"));
                product.setBeschrijving(rs.getString("beschrijving"));
                product.setPrijs(rs.getDouble("prijs"));
                producten.add(product);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return producten;
    }

    public List<Product> findAll() {
        List<Product> producten = new ArrayList<>();

        String q = "select * from product";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setProductnummer(rs.getInt("product_nummer"));
                product.setNaam(rs.getString("naam"));
                product.setBeschrijving(rs.getString("beschrijving"));
                product.setPrijs(rs.getDouble("prijs"));
                product.setOvChipkaarten(ovcdao.findByProduct(product));
                producten.add(product);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return producten;
    }
}
