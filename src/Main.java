import java.sql.*;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/ovchip";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");

        try (Connection conn = DriverManager.getConnection(url, props)) {
//        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=postgres&ssl=true"; Alternatieve methode verbinding DB
//        Connection conn = DriverManager.getConnection(url);
            ReizigerDAOPsql rDAOPsql = new ReizigerDAOPsql(conn);
            AdresDAOPsql aDAOPsql = new AdresDAOPsql(conn);
            OVChipkaartDAOPsql ovcDAOPsql = new OVChipkaartDAOPsql(conn);

            rDAOPsql.setAdao(aDAOPsql);
            rDAOPsql.setOVCdao(ovcDAOPsql);

            aDAOPsql.setRdao(rDAOPsql);

            ovcDAOPsql.setRdao(rDAOPsql);

            testReizigerDAO(rDAOPsql);
            testAdresDAO(aDAOPsql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * P2. Reiziger DAO: persistentie van een klasse
     * <p>
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException
     */
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
        // Maak een nieuwe reiziger aan en persisteer deze in de database. Vind deze bij zijn id.
        gbdatum = "1999-03-13";
        int reizigerId = 6;
        Reiziger hieu = new Reiziger(reizigerId, "C.H.M", "", "Bui", java.sql.Date.valueOf(gbdatum));
        rdao.save(hieu);
        System.out.println(String.format("[Test] ReizigerDao.findById() geeft de volgende reiziger met ID '%d': ", reizigerId) + rdao.findById(reizigerId) + "\n");

        // Verander de naam van de vorige aangemaakte reiziger en update de vorige aangemaakte reiziger in de database en vind deze bij zijn id.
        hieu.setTussenvoegsel("van de");
        hieu.setAchternaam("Buurt");
        rdao.update(hieu);
        System.out.println(String.format("[Test] ReizigerDao.findById() na ReizigerDao.update() geeft de volgende reiziger met ID '%d': ", reizigerId) + rdao.findById(reizigerId) + "\n");

        // Vind alle reizigers met de volgende geboortedatum 2002-12-03
        gbdatum = "2002-12-03";
        reizigers = rdao.findByGbdatum(gbdatum);
        System.out.printf("[Test] ReizigerDao.findByGbdatum() geeft de volgende reizigers met geboortedatum '%s':%n", gbdatum);
        if (reizigers.size() > 0) {
            for (Reiziger reiziger : reizigers) {
                System.out.println(reiziger);
            }
        } else {
            System.out.println("Geen reizigers gevonden met gezochte geboortedatum.");
        }
        System.out.println();

        // Verwijder een aangemaakte reiziger uit de database
        reizigers = rdao.findAll();
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
    }


    /**
     * P3. Adres DAO: persistentie van twee klassen met een één-op-één-relatie
     * <p>
     * Deze methode test de CRUD-functionaliteit van de Adres DAO
     *
     * @throws SQLException
     */
    private static void testAdresDAO(AdresDAO adao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres adres : adressen) {
            System.out.println(adres);
        }
        System.out.println();

        // Maak een adres aan en persisteer deze in de database
        Adres adres = new Adres(6, "3607BL", "556", "Duivenkamp", "Maarssen", 6);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adao.save(adres);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Update huisnummer van een adres gezocht op reiziger
        String gbdatum = "1999-03-13";
        Reiziger hieu = new Reiziger(6, "C.H.M", "van de", "Buurt", java.sql.Date.valueOf(gbdatum));
        System.out.print(String.format("[Test] Huisnummer van adres #%d was %s, na AdresDAO.update() is het ",
                adao.findByReiziger(hieu).getId(),
                adao.findByReiziger(hieu).getHuisnummer()));
        adres.setHuisnummer("551");
        adao.update(adres);
        System.out.println(adao.findByReiziger(hieu).getHuisnummer() + ".");

        // Delete aangemaakte adres
        System.out.print("\n[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        adao.delete(adres);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");
    }
}
