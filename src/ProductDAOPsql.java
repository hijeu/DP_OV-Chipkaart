import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productsDeleted > 0;
    }
}
