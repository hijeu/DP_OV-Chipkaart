import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public AdresDAOPsql (Connection conn) {
        this.conn = conn;
    }

    public void setRdao (ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    @Override
    public boolean save(Adres adres) {
        int recordsSaved = 0;

        String q = "insert into adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) " +
                "values (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, adres.getAdresNummer());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReiziger().getReizigernummer());
            recordsSaved = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsSaved > 0;
    }

    @Override
    public boolean update(Adres adres) {
        int recordsUpdated = 0;

        String q = "update adres set postcode = ?, " +
                "huisnummer = ?, " +
                "straat = ?, " +
                "woonplaats = ? " +
                "where adres_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, adres.getPostcode());
            pst.setString(2, adres.getHuisnummer());
            pst.setString(3, adres.getStraat());
            pst.setString(4, adres.getWoonplaats());
            pst.setInt(5, adres.getAdresNummer());
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

        String q = "delete from adres where (adres_id = ? and " +
                "postcode = ? and " +
                "huisnummer = ? and " +
                "straat = ? and " +
                "woonplaats = ? and " +
                "reiziger_id = ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, adres.getAdresNummer());
            pst.setString(2, adres.getPostcode());
            pst.setString(3, adres.getHuisnummer());
            pst.setString(4, adres.getStraat());
            pst.setString(5, adres.getWoonplaats());
            pst.setInt(6, adres.getReiziger().getReizigernummer());
            recordsDeleted = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsDeleted > 0;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        Adres adres = new Adres();

        String q = "select * from adres where reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getReizigernummer());
                ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                adres.setId(rs.getInt("adres_id"));
                adres.setPostcode(rs.getString("postcode"));
                adres.setHuisnummer(rs.getString("huisnummer"));
                adres.setStraat(rs.getString("straat"));
                adres.setWoonplaats(rs.getString("woonplaats"));
                adres.setReiziger(reiziger);
            } else {
                adres = null;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return adres;
    }

    @Override
    public List<Adres> findAll() {
        List<Adres> adressen = new ArrayList<>();

        try (Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select * from adres")) {
            while (rs.next()) {
                Adres adres = new Adres();
                adres.setId(rs.getInt("adres_id"));
                adres.setPostcode(rs.getString("postcode"));
                adres.setHuisnummer(rs.getString("huisnummer"));
                adres.setStraat(rs.getString("straat"));
                adres.setWoonplaats(rs.getString("woonplaats"));
                adres.setReiziger(rdao.findById(rs.getInt("reiziger_id")));
                adressen.add(adres);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return adressen;
    }
}
