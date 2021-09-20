import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public AdresDAOPsql (Connection conn) {
        this.conn = conn;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    @Override
    public boolean save(Adres adres) {
        int recordsSaved = 0;

        String q = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, adres.getId());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReizigerId());
            recordsSaved = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsSaved > 0;
    }

    @Override
    public boolean update(Adres adres) {
        int recordsUpdated = 0;

        String q = "UPDATE adres SET postcode = ?, " +
                "huisnummer = ?, " +
                "straat = ?, " +
                "woonplaats = ? " +
                "WHERE adres_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, adres.getPostcode());
            pst.setString(2, adres.getHuisnummer());
            pst.setString(3, adres.getStraat());
            pst.setString(4, adres.getWoonplaats());
            pst.setInt(5, adres.getId());
            pst.executeUpdate();
            recordsUpdated = pst.getUpdateCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsUpdated > 0;
    }

    @Override
    public boolean delete(Adres adres) {
        int recordsDeleted = 0;

        String q = "DELETE FROM adres WHERE (adres_id = ? AND " +
                "postcode = ? AND " +
                "huisnummer = ? AND " +
                "straat = ? AND " +
                "woonplaats = ? AND " +
                "reiziger_id = ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, adres.getId());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReizigerId());
            recordsDeleted = Math.abs(pst.executeUpdate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsDeleted > 0;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        Adres adres = new Adres();

        String q = "SELECT * FROM adres WHERE reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q);
             ResultSet rs = pst.executeQuery()) {
            pst.setInt(1, reiziger.getId());
            if (rs.next()) {
                adres.setId(rs.getInt("adres_id"));
                adres.setPostcode(rs.getString("postcode"));
                adres.setHuisnummer(rs.getString("huisnummer"));
                adres.setStraat(rs.getString("straat"));
                adres.setWoonplaats(rs.getString("woonplaats"));
                adres.setReizigerId(rs.getInt("reiziger_id"));
            } else {
                adres = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return adres;
    }

    @Override
    public List<Adres> findAll() {
        List<Adres> adressen = new ArrayList<>();

        try (Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM adres")) {
            while (rs.next()) {
                Adres adres = new Adres();
                adres.setId(rs.getInt("adres_id"));
                adres.setPostcode(rs.getString("postcode"));
                adres.setHuisnummer(rs.getString("huisnummer"));
                adres.setStraat(rs.getString("straat"));
                adres.setWoonplaats(rs.getString("woonplaats"));
                adres.setReizigerId(rs.getInt("reiziger_id"));
                adressen.add(adres);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return adressen;
    }
}
