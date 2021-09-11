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
            ReizigerDAO rDAOPsql = new ReizigerDAOPsql(conn);
            testReizigerDAO(rDAOPsql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * P2. Reiziger DAO: persistentie van een klasse
     *
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
        int reizigerId = 99;
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

        // Verwijder de aangemaakte reizigers uit de vorige tests uit de database
        reizigers = rdao.findAll();
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        rdao.delete(hieu);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
    }
}
