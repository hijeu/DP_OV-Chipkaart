import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            String url = "jdbc:postgresql://localhost:5432/ovchip";
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "postgres");
            Connection conn = DriverManager.getConnection(url, props);

//        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=postgres&ssl=true"; Alternatieve methode verbinding DB
//        Connection conn = DriverManager.getConnection(url);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM reiziger");

            System.out.println("Alle reizigers:");
            int i = 0;
            while (rs.next()) {
                i += 1;
                System.out.print("     #" + i +  ": " + rs.getString("voorletters") + ".");
                if (rs.getString("tussenvoegsel") != null) {
                    System.out.print(" " + rs.getString("tussenvoegsel"));
                }
                System.out.print(" " + rs.getString("achternaam"));
                System.out.print(" (" + rs.getString("geboortedatum") + ")\n");
            }
            rs.close();
            st.close();

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
