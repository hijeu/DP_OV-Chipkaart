import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;
    private ProductDAO pdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setRdao (ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    public void setPdao (ProductDAO pdao) {
        this.pdao = pdao;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        int recordsSaved = 0;

        String q = "insert into ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            pst.setDate(2, ovChipkaart.getGeldigTot());
            pst.setInt(3, ovChipkaart.getKlasse());
            pst.setDouble(4, ovChipkaart.getSaldo());
            pst.setInt(5, ovChipkaart.getReiziger().getReizigernummer());
            recordsSaved = pst.executeUpdate();

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) " +
                    "values (? , ?)";

            List<Product> producten = ovChipkaart.getProducten();
            for (Product product : producten) {
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

        return recordsSaved > 0;
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        int recordsUpdated = 0;

        String q = "update ov_chipkaart set geldig_tot = ?, " +
                "klasse = ?, " +
                "saldo = ?, " +
                "reiziger_id = ? " +
                "where kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setDate(1, ovChipkaart.getGeldigTot());
            pst.setInt(2, ovChipkaart.getKlasse());
            pst.setDouble(3, ovChipkaart.getSaldo());
            pst.setInt(4, ovChipkaart.getReiziger().getReizigernummer());
            pst.setInt(5, ovChipkaart.getKaartNummer());
            pst.executeUpdate();
            recordsUpdated = pst.getUpdateCount();

            q = "delete from ov_chipkaart_product where kaart_nummer = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst2.setInt(1, ovChipkaart.getKaartNummer());
            } catch (Exception e) {
                e.printStackTrace();
            }

            q = "insert into ov_chipkaart_product (kaart_nummer, product_nummer) " +
                    "values (" + ovChipkaart.getKaartNummer() + ", ?)";

            List<Product> producten = pdao.findByOVChipkaart(ovChipkaart);
            for (Product product : producten) {
                try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                    pst2.setInt(1, product.getProductnummer());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                pst2.setInt(5, ovChipkaart.getReiziger().getReizigernummer());
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
            ovChipkaart.setReiziger(rdao.findById(rs.getInt("reiziger_id")));
            ovChipkaart.setProducten(pdao.findByOVChipkaart(ovChipkaart));
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
            pst.setInt(1, reiziger.getReizigernummer());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));
                ovChipkaart.setReiziger(reiziger);
                ovChipkaart.setProducten(pdao.findByOVChipkaart(ovChipkaart));
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
            pst.setInt(1, product.getProductnummer());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));
                ovChipkaart.setReiziger(rdao.findById(rs.getInt("reiziger_id")));
                ovChipkaart.setProducten(pdao.findByOVChipkaart(ovChipkaart));
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
                ovChipkaart.setReiziger(rdao.findById(rs.getInt("reiziger_id")));
                ovChipkaart.setProducten(pdao.findByOVChipkaart(ovChipkaart));
                ovChipkaarten.add(ovChipkaart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ovChipkaarten;
    }
}
