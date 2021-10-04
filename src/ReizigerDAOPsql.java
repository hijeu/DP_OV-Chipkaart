import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;
    private AdresDAO adao;
    private OVChipkaartDAO ovcdao;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setAdao (AdresDAO adao) {
        this.adao = adao;
    }

    public void setOVCdao (OVChipkaartDAO ovcdao) {
        this.ovcdao = ovcdao;
    }

    @Override
    public boolean save(Reiziger reiziger) {
        int recordsSaved = 0;

        String q = "insert into reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) values (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getReizigernummer());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());
            recordsSaved = pst.executeUpdate();

            if (reiziger.getAdres() != null) {
                adao.save(reiziger.getAdres());
            }

            List<OVChipkaart> ovChipkaarten = reiziger.getOVChipkaarten();
            if (!ovChipkaarten.isEmpty()) {
                for (OVChipkaart ovChipkaart : ovChipkaarten) {
                    ovcdao.save(ovChipkaart);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsSaved > 0;
    }

    @Override
    public boolean update(Reiziger reiziger) {
        int recordsUpdated = 0;

        String q = "update reiziger set voorletters = ?, " +
                "tussenvoegsel = ?, " +
                "achternaam = ?, " +
                "geboortedatum = ? " +
                "where reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setString(1, reiziger.getVoorletters());
            pst.setString(2, reiziger.getTussenvoegsel());
            pst.setString(3, reiziger.getAchternaam());
            pst.setDate(4, reiziger.getGeboortedatum());
            pst.setInt(5, reiziger.getReizigernummer());
            pst.executeUpdate();
            recordsUpdated = pst.getUpdateCount();

            Adres adres = reiziger.getAdres();
            if (adres != null) {
                if (adao.findByReiziger(reiziger) == null) {
                    adao.save(adres);
                } else {
                    adao.update(adres);
                }
            }

            List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                ovcdao.delete(ovChipkaart);
            }
            ovChipkaarten = reiziger.getOVChipkaarten();
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                ovcdao.save(ovChipkaart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsUpdated > 0;
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        int recordsDeleted = 0;

        String q = "delete from reiziger where (reiziger_id = ? and " +
                "voorletters = ? and " +
                "tussenvoegsel = ? and " +
                "achternaam = ? and " +
                "geboortedatum = ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getReizigernummer());
            pst.setString(2, reiziger.getVoorletters());
            pst.setString(3, reiziger.getTussenvoegsel());
            pst.setString(4, reiziger.getAchternaam());
            pst.setDate(5, reiziger.getGeboortedatum());
            recordsDeleted = pst.executeUpdate();

            Adres adres = reiziger.getAdres();
            if (adres != null) {
                adao.delete(adres);
            }

            List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                ovcdao.delete(ovChipkaart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recordsDeleted > 0;
    }

    @Override
    public Reiziger findById(int id) {
        Reiziger reiziger = new Reiziger();

        String q = "select * from reiziger where reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();
            reiziger.setReizigernummer(rs.getInt("reiziger_id"));
            reiziger.setVoorletters(rs.getString("voorletters"));
            reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
            reiziger.setAchternaam(rs.getString("achternaam"));
            reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
            rs.close();
            reiziger.setAdres(adao.findByReiziger(reiziger));
            reiziger.setOVChipkaarten(ovcdao.findByReiziger(reiziger));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reiziger;
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) {
        List<Reiziger> reizigers = new ArrayList<>();

        String q = "select * from reiziger where geboortedatum = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
             pst.setDate(1, java.sql.Date.valueOf(datum));
             ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Reiziger reiziger = new Reiziger();
                reiziger.setReizigernummer(rs.getInt("reiziger_id"));
                reiziger.setVoorletters(rs.getString("voorletters"));
                reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
                reiziger.setAchternaam(rs.getString("achternaam"));
                reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
                reiziger.setAdres(adao.findByReiziger(reiziger));
                reiziger.setOVChipkaarten(ovcdao.findByReiziger(reiziger));
                reizigers.add(reiziger);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reizigers;
    }

    @Override
    public Reiziger findByOVChipkaart(OVChipkaart ovChipkaart) {
        Reiziger reiziger = new Reiziger();

        String q = "select * from ov_chipkaart where kaart_nummer = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet rs = pst.executeQuery();
            rs.next();
            int reizigerId = rs.getInt("reiziger_id");
            rs.close();

            q = "select * from reiziger where reiziger_id = ?";

            try (PreparedStatement pst2 = conn.prepareStatement(q)) {
                pst.setInt(1, reizigerId);
                rs = pst2.executeQuery();
                rs.next();
                reiziger.setReizigernummer(rs.getInt("reiziger_id"));
                reiziger.setVoorletters(rs.getString("voorletters"));
                reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
                reiziger.setAchternaam(rs.getString("achternaam"));
                reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
                rs.close();
                Adres adres = adao.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
                reiziger.setOVChipkaarten(ovChipkaarten);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reiziger;
    }

    @Override
    public List<Reiziger> findAll() {
        List<Reiziger> reizigers = new ArrayList<>();

        try (Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM reiziger")) {
            while (rs.next()) {
                Reiziger reiziger = new Reiziger();
                reiziger.setReizigernummer(rs.getInt("reiziger_id"));
                reiziger.setVoorletters(rs.getString("voorletters"));
                reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
                reiziger.setAchternaam(rs.getString("achternaam"));
                reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
                Adres adres = adao.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
                reiziger.setOVChipkaarten(ovChipkaarten);
                reizigers.add(reiziger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reizigers;
    }
}
