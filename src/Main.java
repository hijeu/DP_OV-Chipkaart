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
        // update
        // delete
        // findById
        // findByGbdatum
    }
}
