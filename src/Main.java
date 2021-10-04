import java.sql.*;
import java.util.ArrayList;
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
            ProductDAOPsql pDAOPsql = new ProductDAOPsql(conn);

            rDAOPsql.setAdao(aDAOPsql);
            rDAOPsql.setOVCdao(ovcDAOPsql);

            testReizigerDAO(rDAOPsql);
            testAdresDAO(aDAOPsql, rDAOPsql);
            testOVChipkaartDAO(ovcDAOPsql);
            testProductEnOVChipkaartDAO(pDAOPsql, ovcDAOPsql);

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

        // Verwijder de 2 aangemaakte reizigers uit de database
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
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
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
        Reiziger gert = rdao.findById(5);
        Adres adresGert = adao.findByReiziger(gert);
        System.out.print(String.format("[Test] Huisnummer van adres #%d was %s, na AdresDAO.update() is het ",
                adresGert.getId(),
                adresGert.getHuisnummer()));
        adresGert.setHuisnummer("551");
        adao.update(adresGert);
        System.out.println(adao.findByReiziger(gert).getHuisnummer() + ".");
        adresGert.setHuisnummer("22");
        adao.update(adresGert);

        // Delete aangemaakte adres
        System.out.print("\n[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        adao.delete(adres);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        String gbdatum = "1999-03-13";
        Reiziger hieu = new Reiziger(6, "C.H.M", "van de", "Buurt", java.sql.Date.valueOf(gbdatum));
        rdao.delete(hieu);
    }

    /**
     * P4. OVChipkaart DAO: persistentie van twee klassen met een één-op-veel-relatie
     * <p>
     * Deze methode test de CRUD-functionaliteit van de OVChipkaart DAO
     *
     * @throws SQLException
     */
    private static void testOVChipkaartDAO(OVChipkaartDAO ovcdao) throws SQLException {
        System.out.println("\n---------- Test OVChipkaartDAO -------------");

        // Haal alle ovchipkaarten op uit de database
        List<OVChipkaart> ovChipkaarten = ovcdao.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende adressen:");
        for (OVChipkaart ovChipkaart : ovChipkaarten) {
            System.out.println(ovChipkaart);
        }
        System.out.println();

        // Maak een ovchipkaart aan en persisteer deze in de database
        Date geligheidsdatum = java.sql.Date.valueOf("2022-01-01");
        OVChipkaart ovChipkaart = new OVChipkaart(1, geligheidsdatum, 1, 100.00, 5);
        System.out.print("[Test] Eerst " + ovChipkaarten.size() + " ovchipkaarten, na OVChipkaartDAO.save() ");
        ovcdao.save(ovChipkaart);
        ovChipkaarten = ovcdao.findAll();
        System.out.println(ovChipkaarten.size() + " ovchipkaarten\n");

        // Update saldo van een ovchipkaart gevonden op kaart_nummer in de database
        ovChipkaart = ovcdao.findById(1);
        System.out.print(String.format("[Test] Saldo van ovchipkaart #%d was €%.2f, na OVChipkaartDAO.update() is het ",
                ovChipkaart.getKaartNummer(),
                ovChipkaart.getSaldo()));
        ovChipkaart.setSaldo(500.00);
        ovcdao.update(ovChipkaart);
        System.out.println(String.format("€%.2f", ovcdao.findById(1).getSaldo()) + ".");

        // Delete aangemaakte ovChipkaart
        System.out.print("\n[Test] Eerst " + ovChipkaarten.size() + " ovchipkaarten, na OVChipkaartDAO.delete() ");
        ovcdao.delete(ovChipkaart);
        ovChipkaarten = ovcdao.findAll();
        System.out.println(ovChipkaarten.size() + " ovchipkaarten\n");
    }

    /**
     * P5. Product DAO & OVChipkaart DAO: persistentie van twee klassen met een veel-op-veel-relatie
     * <p>
     * Deze methode test de CRUD-functionaliteit van de Product DAO & OVChipkaart DAO
     *
     * @throws SQLException
     */
    private static void testProductEnOVChipkaartDAO(ProductDAO pdao, OVChipkaartDAO ovcdao) throws SQLException {
        System.out.println("\n---------- Test ProductDAO -------------");
        // Haal alle producten op uit de database
        List<Product> producten = pdao.findAll();
        System.out.println("[Test] ProductDAO.findAll() geeft de volgende producten:");
        for (Product product : producten) {
            System.out.println(product);
        }
        System.out.println();

        // Haal alle producten op uit de database van OVChipkaart #35283
        OVChipkaart ovChipkaart = ovcdao.findById(35283);
        List<Product> productenOpOVChipkaart35283 = pdao.findByOVChipkaart(ovChipkaart);
        System.out.println("[Test] ProductDAO.findByOVChipkaart() geeft de volgende producten:");
        for (Product product : productenOpOVChipkaart35283) {
            System.out.println(product);
        }
        System.out.println();

        // Maak een product aan, zet deze op OVChipkaart #35283 en persisteer deze in de database
        producten = pdao.findAll();
        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.save() ");
        Product product = new Product(7, "Gratis reizen", "24/7 onbeperkt gratis reizen door het hele land", 200.00);
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        ovChipkaarten.add(ovChipkaart);
        product.setOvChipkaarten(ovChipkaarten);
        pdao.save(product);
        producten = pdao.findAll();
        System.out.println(producten.size() + " producten\n");

        System.out.print("[Test] Eerst " + productenOpOVChipkaart35283.size() + " producten op OVChipkaart #35283, na ProductDAO.save() ");
        productenOpOVChipkaart35283 = pdao.findByOVChipkaart(ovChipkaart);
        System.out.println(productenOpOVChipkaart35283.size() + " producten\n");

        // Update een product in de database
        System.out.println("[Test] Gegevens van product #7 in de database voor de update was:");
        product = pdao.findById(7);
        System.out.println(product);
        product.setNaam("75% Korting");
        product.setBeschrijving("24/7 75% korting op reizen door het hele land");
        pdao.update(product);
        System.out.println("Gegevens van product #7 in de database na de update is:");
        product = pdao.findById(7);
        System.out.println(product + "\n");

        // Verwijder een product uit de database
        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.delete() ");
        pdao.delete(product);
        producten = pdao.findAll();
        System.out.println(producten.size() + " producten\n");

        System.out.print("[Test] Eerst " + productenOpOVChipkaart35283.size() + " producten op OVChipkaart #35283, na ProductDAO.delete() ");
        productenOpOVChipkaart35283 = pdao.findByOVChipkaart(ovChipkaart);
        System.out.println(productenOpOVChipkaart35283.size() + " producten\n");

        System.out.println("\n---------- Test OVChipkaartDAO (findByProduct) -------------");
        // Haal alle OVChipkaarten uit de database waar een product op staat
        Product productGezochtOpId = pdao.findById(6);
        ovChipkaarten = ovcdao.findByProduct(productGezochtOpId);
        System.out.println("[Test] OVChipkaartDAO.findByProduct() geeft de volgende OVChipkaarten:");
        for (OVChipkaart ovChipkaartFoundByProduct : ovChipkaarten) {
            System.out.println(ovChipkaartFoundByProduct);
        }
    }
}
