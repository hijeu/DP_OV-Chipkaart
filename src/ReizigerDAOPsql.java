import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;
    private AdresDAO adao;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setAdao (AdresDAO adao) {
        this.adao = adao;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        int recordsSaved = 0;

        String q = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getId());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());
            recordsSaved = pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsSaved > 0;
    }

    @Override
    public boolean update(Reiziger reiziger) {
        int recordsUpdated = 0;

        String q = "UPDATE reiziger SET voorletters = ?, " +
                "tussenvoegsel = ?, " +
                "achternaam = ?, " +
                "geboortedatum = ? " +
                "WHERE reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, reiziger.getVoorletters());
            pst.setString(2, reiziger.getTussenvoegsel());
            pst.setString(3, reiziger.getAchternaam());
            pst.setDate(4, reiziger.getGeboortedatum());
            pst.setInt(5, reiziger.getId());
            pst.executeUpdate();
            recordsUpdated = pst.getUpdateCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsUpdated > 0;
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        int recordsDeleted = 0;

        String q = "DELETE FROM reiziger WHERE (reiziger_id = ? AND " +
                "voorletters = ? AND " +
                "tussenvoegsel = ? AND " +
                "achternaam = ? AND " +
                "geboortedatum = ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getId());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());
            recordsDeleted = Math.abs(pst.executeUpdate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsDeleted > 0;
    }

    @Override
    public Reiziger findById(int id) {
        Reiziger reiziger = new Reiziger();

        String q = "SELECT * FROM reiziger WHERE reiziger_id=?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            reiziger.setId(rs.getInt("reiziger_id"));
            reiziger.setVoorletters(rs.getString("voorletters"));
            reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
            reiziger.setAchternaam(rs.getString("achternaam"));
            reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reiziger;
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) {
        List<Reiziger> reizigers = new ArrayList<>();

        String q = "SELECT * FROM reiziger WHERE geboortedatum=?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setDate(1, java.sql.Date.valueOf(datum));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Reiziger reiziger = new Reiziger();
                reiziger.setId(rs.getInt("reiziger_id"));
                reiziger.setVoorletters(rs.getString("voorletters"));
                reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
                reiziger.setAchternaam(rs.getString("achternaam"));
                reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
                reizigers.add(reiziger);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reizigers;
    }

    @Override
    public List<Reiziger> findAll() {
        List<Reiziger> reizigers = new ArrayList<>();

        try (Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM reiziger")) {
            while (rs.next()) {
                Reiziger reiziger = new Reiziger();
                reiziger.setId(rs.getInt("reiziger_id"));
                reiziger.setVoorletters(rs.getString("voorletters"));
                reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
                reiziger.setAchternaam(rs.getString("achternaam"));
                reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
                reizigers.add(reiziger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reizigers;
    }
}
