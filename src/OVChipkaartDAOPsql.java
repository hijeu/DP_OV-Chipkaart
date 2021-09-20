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

        String q = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            pst.setDate(2, ovChipkaart.getGeldigTot());
            pst.setInt(3, ovChipkaart.getKlasse());
            pst.setDouble(4, ovChipkaart.getSaldo());
            pst.setInt(5, ovChipkaart.getReizigerId());
            recordsSaved = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsSaved > 0;
    }

    // Update Update
    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        int recordsUpdated = 0;

        return recordsUpdated > 0;
    }

    // Delete Delete
    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        int recordsDeleted = 0;

        return recordsDeleted > 0;
    }


    // Read Select (find methods)

    @Override
    public OVChipkaart findById (int id) {
        OVChipkaart ovChipkaart = new OVChipkaart();

        String q = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q);
             ResultSet rs = pst.executeQuery()) {
            pst.setInt(1, id);
            rs.next();
            ovChipkaart.setKaartNummer(rs.getInt("kaart_nummer"));
            ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
            ovChipkaart.setKlasse(rs.getInt("klasse"));
            ovChipkaart.setSaldo(rs.getDouble("saldo"));

            Reiziger reiziger = rdao.findByOVChipkaart(ovChipkaart);
            ovChipkaart.setReizigerId(reiziger.getId());
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

        try (PreparedStatement pst = conn.prepareStatement(q);
             ResultSet rs = pst.executeQuery()) {
            pst.setInt(1, reiziger.getId());

            while (rs.next()) {

            }
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
                rs.close();
                Reiziger reiziger = rdao.findByOVChipkaart(ovChipkaart);
                ovChipkaart.setReizigerId(reiziger.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ovChipkaarten;
    }
}
