import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        int recordsSaved = 0;

        String q = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            pst.setDate(2, ovChipkaart.getGeldigTot());
            pst.setInt(3, ovChipkaart.getKlasse());
            pst.setDouble(4, ovChipkaart.getSaldo());
            pst.setInt(5, ovChipkaart.getReizigerId());
            recordsSaved = pst.executeUpdate();

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) " +
                    "values (? , ?)";

            List<Product> producten = ovChipkaart.getProducten();
            for (Product product : producten) {
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

        return recordsSaved > 0;
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        int recordsUpdated = 0;

        String q = "UPDATE ov_chipkaart SET geldig_tot = ?, " +
                "klasse = ?, " +
                "saldo = ?, " +
                "reiziger_id = ? " +
                "WHERE kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setDate(1, ovChipkaart.getGeldigTot());
            pst.setInt(2, ovChipkaart.getKlasse());
            pst.setDouble(3, ovChipkaart.getSaldo());
            pst.setInt(4, ovChipkaart.getReizigerId());
            pst.setInt(5, ovChipkaart.getKaartNummer());
            pst.executeUpdate();
            recordsUpdated = pst.getUpdateCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsUpdated > 0;
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        int recordsDeleted = 0;

        String q = "delete from ov_chipkaart_product " +
                "where kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            pst.executeUpdate();

            q = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ? AND" +
                    " geldig_tot = ? AND" +
                    " klasse = ? AND " +
                    "saldo = ? AND" +
                    " reiziger_id = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst2.setInt(1, ovChipkaart.getKaartNummer());
                pst2.setDate(2, ovChipkaart.getGeldigTot());
                pst2.setInt(3, ovChipkaart.getKlasse());
                pst2.setDouble(4, ovChipkaart.getSaldo());
                pst2.setInt(5, ovChipkaart.getReizigerId());
                recordsDeleted = pst2.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsDeleted > 0;
    }

    @Override
    public OVChipkaart findById (int id) {
        OVChipkaart ovChipkaart = new OVChipkaart();

        String q = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
            ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
            ovChipkaart.setKlasse(rs.getInt("klasse"));
            ovChipkaart.setSaldo(rs.getDouble("saldo"));
            ovChipkaart.setReizigerId(rs.getInt("reiziger_id"));
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ovChipkaart;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        OVChipkaart ovChipkaart = new OVChipkaart();

        String q = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));
                ovChipkaart.setReizigerId(reiziger.getId());
                ovChipkaarten.add(ovChipkaart);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ovChipkaarten;
    }

    public List<OVChipkaart> findByProduct (Product product) {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        String q = "select ovc.kaart_nummer, ovc.geldig_tot, ovc.klasse, ovc.saldo, ovc.reiziger_id " +
                "from ov_chipkaart ovc " +
                "join ov_chipkaart_product ovcp " +
                "on ovc.kaart_nummer = ovcp.kaart_nummer " +
                "where ovcp.product_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, product.getProductNummer());
            ResultSet rs = pst.executeQuery();
            rs.next();
            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));
                ovChipkaart.setReizigerId(rs.getInt("reiziger_id"));
                ovChipkaarten.add(ovChipkaart);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findAll() {
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        String q = "SELECT * FROM ov_chipkaart";

        try (PreparedStatement pst = conn.prepareStatement(q);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));
                ovChipkaart.setReizigerId(rs.getInt("reiziger_id"));
                ovChipkaarten.add(ovChipkaart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ovChipkaarten;
    }
}
