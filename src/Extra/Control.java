package Extra;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Control {
    Database connection = new Database();
    public void insert(Model mod){
        try{
            connection.connect();
            PreparedStatement pst = connection.link.prepareStatement("INSERT INTO person (id, firstname, lastname) values (?, ?, ?)");
            pst.setInt(1, mod.getId());
            pst.setString(2, mod.getFirst_name());
            pst.setString(3, mod.getLast_name());
            pst.executeUpdate();
            System.out.println("first name is " + mod.getFirst_name());
            connection.disconnect();
        }catch(SQLException ex){
            System.out.println("Error: " + ex);
        }
    }
         
}
