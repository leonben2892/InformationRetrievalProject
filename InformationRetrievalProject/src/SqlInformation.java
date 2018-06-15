import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlInformation {
	
	//This method will populate the database with data
	public void createDataBase(Connection con, PreparedStatement create, Statement statement) throws SQLException
	{
		con.setAutoCommit(false);
		try {
			//Inverted Index table
			create = con.prepareStatement("CREATE TABLE IF NOT EXISTS location_order"
					+ "(word_NO int AUTO_INCREMENT,"					
					+ "word varchar(255),"
					+ "docID int,"
					+ "file_name varchar(255),"
					+ "visible int,"
					+ "PRIMARY KEY(word_NO))");
			create.executeUpdate();
			
			create = con.prepareStatement("CREATE TABLE IF NOT EXISTS tmp_soundex"
					+ "(word_NO int AUTO_INCREMENT,"					
					+ "word varchar(255),"
					+ "soundex varchar(255),"
					+ "docID int,"
					+ "file_name varchar(255),"
					+ "visible int,"
					+ "PRIMARY KEY(word_NO))");
			create.executeUpdate();
			
			con.commit();
		} catch (SQLException e1) {
			con.rollback();
			System.out.println("Changes are rollbacked and not applied to the database.");
		}		
	}
}
