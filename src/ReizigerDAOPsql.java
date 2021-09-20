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

        String q = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reiziger.getId());
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

            Adres adres = reiziger.getAdres();
            if (adres != null) {
                if (CheckIfAdresExistsInDb(adres)) {
                    adao.save(adres);
                } else {
                    adao.update(adres);
                }
            }

            List<OVChipkaart> ovChipkaarten = reiziger.getOVChipkaarten();
            if (!ovChipkaarten.isEmpty()) {
                for (OVChipkaart ovChipkaart : ovChipkaarten) {
                    if (!CheckIfOVChipkaartExistsInDb(ovChipkaart)) {
                        ovcdao.update(ovChipkaart);
                    }
                }
            }
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
            recordsDeleted = pst.executeUpdate();

            Adres adres = reiziger.getAdres();
            if (adres != null) {
                if (CheckIfAdresExistsInDb(adres)) {
                    adao.delete(adres);
                }
            }

            List<OVChipkaart> ovChipkaarten = reiziger.getOVChipkaarten();
            if (!ovChipkaarten.isEmpty()) {
                for (OVChipkaart ovChipkaart : ovChipkaarten) {
                    if (CheckIfOVChipkaartExistsInDb(ovChipkaart)) {
                        ovcdao.delete(ovChipkaart);
                    }
                }
            }
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
            Adres adres = adao.findByReiziger(reiziger);
            reiziger.setAdres(adres);
            List <OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
            reiziger.setOVChipkaarten(ovChipkaarten);
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
                Adres adres = adao.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
                List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
                reiziger.setOVChipkaarten(ovChipkaarten);
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
        int reizigerId = 0;

        String q = "SELECT * FROM ov_chipkaart WHERE kaart_nummer ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, ovChipkaart.getKaartNummer());
            ResultSet rs = pst.executeQuery();
            rs.next();
            reizigerId = rs.getInt("reiziger_id");
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        q = "SELECT * FROM reiziger WHERE reiziger_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(q)) {
            pst.setInt(1, reizigerId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            reiziger.setId(rs.getInt("reiziger_id"));
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

        return reiziger;
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
                Adres adres = adao.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
                List<OVChipkaart> ovChipkaarten = ovcdao.findByReiziger(reiziger);
                reiziger.setOVChipkaarten(ovChipkaarten);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reizigers;
    }

    public boolean CheckIfAdresExistsInDb(Adres adres) {
        boolean booleanCheckIfAdresExistsInDb = false;

        String qCheckIfAdresExistsInDb = "SELECT * FROM adres WHERE adres_id = ?";
        try (PreparedStatement pstCheckIfAdresExistsInDb = conn.prepareStatement(qCheckIfAdresExistsInDb);
             ResultSet rs = pstCheckIfAdresExistsInDb.executeQuery()) {
            pstCheckIfAdresExistsInDb.setInt(1, adres.getId());
            if (rs.next()) {
                booleanCheckIfAdresExistsInDb = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return booleanCheckIfAdresExistsInDb;
    }

    public boolean CheckIfOVChipkaartExistsInDb(OVChipkaart ovChipkaart) {
        boolean booleanOVChipkaartExistsInDb = false;

        String qCheckIfOVChipkaartExistsInDb = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
        try (PreparedStatement pstCheckIfOVChipkaartExistsInDb = conn.prepareStatement(qCheckIfOVChipkaartExistsInDb);
             ResultSet rs = pstCheckIfOVChipkaartExistsInDb.executeQuery()) {
            pstCheckIfOVChipkaartExistsInDb.setInt(1, ovChipkaart.getKaartNummer());
            if (rs.next()) {
                booleanOVChipkaartExistsInDb = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return booleanOVChipkaartExistsInDb;
    }
}
