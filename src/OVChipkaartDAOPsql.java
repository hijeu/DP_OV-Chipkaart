import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setRdao(ReizigerDAO rdao) {
        this.rdao = rdao;
    }

    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> oVChipkaarten = new ArrayList<>();



        return oVChipkaarten;
    }
}
