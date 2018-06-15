import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.language.Soundex;

import net.proteanit.sql.DbUtils;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ParseFile {
	
	private Connection con;
	private PreparedStatement create;
	private Statement statement; 
	private SqlInformation sqlInfo;
	private static int fileNumber;
	private Soundex sndx;
	private DefaultTableModel dataModel;
	
	public ParseFile() throws Exception
	{
		//Create connection to the database
		con = getConnection();
		create = null;
		statement = con.createStatement();
		sqlInfo = new SqlInformation();
		
		//Populate the database with data
		sqlInfo.createDataBase(con, create, statement);
		fileNumber = 0;
		
		sndx = new Soundex(); 
		dataModel = new DefaultTableModel();
	}
	
	//Parsing and inserting into database
	protected void createBasicData(File tmpFile)
	{		 
		fileNumber++;
		Scanner sc2 = null;
	    try {
	        sc2 = new Scanner(tmpFile);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();  
	    }
	    while (sc2.hasNextLine()) {
	            Scanner s2 = new Scanner(sc2.nextLine());
	        while (s2.hasNext()) {
	            String tmpWord = s2.next();
	            tmpWord = tmpWord.replaceAll("\\W", ""); //replace all special characters with ""
	            tmpWord = tmpWord.replaceAll("\\d", ""); //replace all digits with ""
	            tmpWord = tmpWord.toLowerCase();
	            if(!(tmpWord.equals("")))
	            {
					try {
							//Inserting into location_order table
						  String query = "insert ignore into location_order (word, docID, file_name, visible)" + " values (?, ?, ?, ?)";
					      // create the mysql insert preparedstatement
					      PreparedStatement preparedStmt = con.prepareStatement(query);
					      preparedStmt.setString (1,tmpWord);
					      preparedStmt.setInt (2, fileNumber);	
					      preparedStmt.setString (3,tmpFile.getName());
					      preparedStmt.setInt (4, 1);	
					      // execute the preparedstatement
					      preparedStmt.executeUpdate();
					      con.commit();
					      
					      //Inserting into soundex table
					      query = "insert ignore into tmp_soundex (word, soundex, docID, file_name, visible)" + " values (?, ?, ?, ?, ?)";
					      // insert preparedstatement
					      preparedStmt = con.prepareStatement(query);
					      preparedStmt.setString (1,tmpWord);
					      preparedStmt.setString (2,sndx.encode(tmpWord));
					      preparedStmt.setInt (3, fileNumber);	
					      preparedStmt.setString (4,tmpFile.getName());
					      preparedStmt.setInt (5, 1);	
					      // execute the preparedstatement
					      preparedStmt.executeUpdate();
					      con.commit();
					      
					      preparedStmt.close();
					      System.out.println("Inserted the word: "+tmpWord+" | DocID is: "+fileNumber +"|" +" Name:: "+tmpFile.getName());
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(null, "Invalid data input!", "Error!", 2);
					}
	            }
	        }
	        s2.close();
	    }
	    sc2.close();
	}
		
	protected void createAlphabeticOrder()
	{
		try {
			  String deleteTableQuery = "drop table if exists alphabetic_order";
			// create the mysql preparedstatement
			  PreparedStatement preStmt = con.prepareStatement(deleteTableQuery);
			// execute the preparedstatement
			  preStmt.executeUpdate();
			  con.commit();
			  preStmt.close();
			  
			  String query = "CREATE TABLE IF NOT EXISTS alphabetic_order select * from location_order order by word";
		      // create the mysql preparedstatement
		      PreparedStatement preparedStmt = con.prepareStatement(query);		      
		      // execute the preparedstatement
		      preparedStmt.executeUpdate();
		      con.commit();
		      preparedStmt.close();		 
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch(NumberFormatException nfe){
		}
	}
	
	protected void createInvertedFile()
	{
		try {
		  String deleteTableQuery = "drop table if exists inverted_file";
		// create the mysql preparedstatement
		  PreparedStatement preStmt = con.prepareStatement(deleteTableQuery);
		// execute the preparedstatement
		  preStmt.executeUpdate();
		  con.commit();
		  preStmt.close();
		  
		  String query2 = "CREATE TABLE IF NOT EXISTS inverted_file select word_NO, word, docID, count(word) as tf, file_name, visible from alphabetic_order group by word,docID order by word";
		// create the mysql preparedstatement
		PreparedStatement preparedStmt = con.prepareStatement(query2);		      
		// execute the preparedstatement
		preparedStmt.executeUpdate();
		con.commit();
		preparedStmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch(NumberFormatException nfe){
		}
	}
	
	protected void createDictionaryFile()
	{
		try {
		  String deleteTableQuery = "drop table if exists dictionary_file";
		// create the mysql preparedstatement
		  PreparedStatement preStmt = con.prepareStatement(deleteTableQuery);
		// execute the preparedstatement
		  preStmt.executeUpdate();
		  con.commit();
		  preStmt.close();
		  
		  String query2 = "CREATE TABLE IF NOT EXISTS dictionary_file select word_NO, word, count(docID) as Hits from inverted_file group by word order by word";
		// create the mysql preparedstatement
		PreparedStatement preparedStmt = con.prepareStatement(query2);		      
		// execute the preparedstatement
		preparedStmt.executeUpdate();
		con.commit();
		preparedStmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch(NumberFormatException nfe){
		}
	}
	
	protected void createOrderedSoundexTable()
	{
		try {
		  String deleteTableQuery = "drop table if exists soundex_table";
		// create the mysql preparedstatement
		  PreparedStatement preStmt = con.prepareStatement(deleteTableQuery);
		// execute the preparedstatement
		  preStmt.executeUpdate();
		  con.commit();
		  preStmt.close();
		  
		  String query2 = "CREATE TABLE IF NOT EXISTS soundex_table select word_NO, word, soundex, docID, count(soundex) as tf, file_name, visible from tmp_soundex group by word,docID order by soundex";
		// create the mysql preparedstatement
		PreparedStatement preparedStmt = con.prepareStatement(query2);		      
		// execute the preparedstatement
		preparedStmt.executeUpdate();
		con.commit();		
		preparedStmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch(NumberFormatException nfe){
		}
	}
	
	protected void createSoundexDictionaryFile()
	{
		try {
		  String deleteTableQuery = "drop table if exists soundex_dictionary_file";
		// create the mysql preparedstatement
		  PreparedStatement preStmt = con.prepareStatement(deleteTableQuery);
		// execute the preparedstatement
		  preStmt.executeUpdate();
		  con.commit();
		  preStmt.close();
		  
		  String query2 = "CREATE TABLE IF NOT EXISTS soundex_dictionary_file select word_NO, soundex, count(docID) as Hits from soundex_table group by soundex order by soundex";
		// create the mysql preparedstatement
		PreparedStatement preparedStmt = con.prepareStatement(query2);		      
		// execute the preparedstatement
		preparedStmt.executeUpdate();
		con.commit();
		preparedStmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch(NumberFormatException nfe){
		}
	}
	
	protected void updateVisibility(List<Integer> fileIndexes, int visibilityFlag) throws SQLException
	{
		for(int i=0; i<fileIndexes.size(); i++)
		{
			String query = "update inverted_file set visible=? where docID="+fileIndexes.get(i);
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, visibilityFlag);
			pst.executeUpdate();
			con.commit();	
			
			query = "update soundex_table set visible=? where docID="+fileIndexes.get(i);
			pst = con.prepareStatement(query);
			pst.setInt(1, visibilityFlag);
			pst.executeUpdate();
			con.commit();	
			
			pst.close();
		}
	}
	
	protected void requestDataInApostrophes(JTable jt, String searchTerm, File[] fileNames) throws SQLException
	{
		String[] columnNames = {"File","Document_ID"};
		DefaultTableModel tableModel = new DefaultTableModel(columnNames,0);
		List<String> fileNamesAndId = new ArrayList<String>();
		
		//Reads all the content of a file into a string variable then check if the file contains the search term. if it does, add the file name to filesNamesAndId list.
		for(int i=0; i<fileNames.length; i++)
		{
			if(!(fileNames[i].getName().equals("Storage Images")))
			{
			    String content = "";
			    try
			    {
			        content = new String ( Files.readAllBytes( Paths.get(fileNames[i].getPath()) ) );
			        content = content.toLowerCase();
			    }
			    catch (IOException e)
			    {
			        e.printStackTrace();
			    }
			    if(content.contains(searchTerm))
			    {
			    	fileNamesAndId.add(fileNames[i].getName());
			    }	
			}
		}	

		//Check if the file visible = 1 then add that file data(name,id) to the result table
		for(int i=0; i<fileNamesAndId.size(); i++)
		{
			String fileName = fileNamesAndId.get(i).replaceAll("\\d", "").replaceFirst(".", "");
			String fileNumber = fileNamesAndId.get(i).replaceAll("\\W", "").replaceAll("[^\\d.]", "");
			
			String query = "select docID,visible from inverted_file where docID='"+fileNumber+"' group by docID";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();//then execute the statement
			rs.next();
			
			if(rs.getInt(2) == 1)
			{
				tableModel.addRow(new String[]{fileName, fileNumber});
			    rs.beforeFirst();
			    pstmt.close();
			}		
		}
		
		//if the string wasn't found in any of the files then print an error message and return from the function
		if(tableModel.getRowCount() == 0)
		{
			jt.setModel(dataModel);
			dataModel.setRowCount(0);
			JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//Show the results in the table
		jt.setModel(tableModel);
	}
	
	protected void requestedData(JTable jt,String searchTerm, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query = "select file_name as File, tf as Term_Frequency, docID as Document_ID from inverted_file where word='"+searchTerm+"' AND visible=1 group by file_name order by word";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs.beforeFirst();
			    pstmt.close();
			    return;
			} 
			rs.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs));	
			pstmt.close();
		}
		else
		{
			String query = "select word as Word, soundex as Soundex, file_name as File, tf as Term_Frequency, docID as Document_ID from soundex_table where soundex= ? AND visible=1 group by file_name order by soundex";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm));
			ResultSet rs = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs.next() ) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs.beforeFirst();
			    pstmt.close();
			    return;
			} 
			rs.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs));	
			pstmt.close();
		}
	}
	
	protected void requestedSubStringData(JTable jt,String searchTerm, boolean soundex) throws SQLException
	{		
		if(!soundex)
		{
			String query = "select file_name as File, tf as Term_Frequency, docID as Document_ID from inverted_file where word LIKE '%"+searchTerm+"%'  AND visible=1 group by file_name order by word";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs.beforeFirst();
			    pstmt.close();
			    return;
			} 
			rs.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs));	
			pstmt.close();
		}
		else
		{
			String query = "select word as Word, soundex as Soundex, file_name as File, tf as Term_Frequency, docID as Document_ID from soundex_table where soundex= ? AND visible=1 group by file_name order by soundex";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm));
			ResultSet rs = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs.next() ) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs.beforeFirst();
			    pstmt.close();
			    return;
			} 
			rs.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs));	
			pstmt.close();
		}
	}
	
	/* AND Operator */
	protected void requestedAndData(JTable jt,String searchTerm1, String searchTerm2, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word = '"+searchTerm1+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement		
			if (!rs1.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word = '"+searchTerm2+"' AND docID ="+rs1.getInt(1)+") AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();		
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm1));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where soundex= ? AND docID ="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, searchTerm2);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	/* OR Operator */
	protected void requestedORData(JTable jt,String searchTerm1, String searchTerm2, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm1+"' OR word='"+searchTerm2+"') AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement


			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? OR soundex= ?) AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm1));
			pstmt.setString(2, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	/* NOT Operator */
	protected void requestedNotData(JTable jt,String searchTerm, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word = '"+searchTerm+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from inverted_file where visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement

			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
			pstmt2.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
			pstmt2.close();
		}
	}
	
	/* str1 NOT str2 Operator */
	protected void requestedTermNotTermData(JTable jt,String searchTerm1, String searchTerm2, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
		String query2 = "select docID from inverted_file where word = '"+searchTerm2+"' group by docID";
		PreparedStatement pstmt2 = con.prepareStatement(query2);
		ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
		if (!rs1.next()) {
			String query = "select file_name as File, docID as Document_ID from inverted_file where word='"+searchTerm1+"' AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		    rs1.beforeFirst();
		    pstmt2.close();
		    return;
		} 
		
		String query = "select file_name as File, docID as Document_ID from inverted_file where (docID !="+rs1.getInt(1)+" AND word='"+searchTerm1+"') AND visible=1 group by file_name";
		PreparedStatement pstmt = con.prepareStatement(query);
		ResultSet rs2 = pstmt.executeQuery();//then execute the statement

		//Checking if the term found in the database
		if (!rs2.next()) {
			jt.setModel(dataModel);
			dataModel.setRowCount(0);
			JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
		    rs2.beforeFirst();
		    pstmt.close();
		    return;
		} 
		
		rs2.beforeFirst();
		jt.setModel(DbUtils.resultSetToTableModel(rs2));	
		pstmt.close();
		pstmt2.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm2));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where soundex=? AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm1));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (docID !="+rs1.getInt(1)+" AND soundex=?) AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm1));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
			pstmt2.close();
		}
		
	}
	
	/* NOT str1 OR str2 Operator */
	protected void requestedNotOrData(JTable jt,String searchTerm1, String searchTerm2, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
		String query2 = "select docID from inverted_file where word = '"+searchTerm1+"' group by docID";
		PreparedStatement pstmt2 = con.prepareStatement(query2);
		ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
		if (!rs1.next()) {
			String query = "select file_name as File, docID as Document_ID from inverted_file where word='"+searchTerm2+"' AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		    rs1.beforeFirst();
		    pstmt2.close();
		    return;
		} 
		
		String query = "select file_name as File, docID as Document_ID from inverted_file where (docID !="+rs1.getInt(1)+" OR word='"+searchTerm2+"') AND visible=1 group by file_name";
		PreparedStatement pstmt = con.prepareStatement(query);
		ResultSet rs2 = pstmt.executeQuery();//then execute the statement

		//Checking if the term found in the database
		if (!rs2.next()) {
			jt.setModel(dataModel);
			dataModel.setRowCount(0);
			JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
		    rs2.beforeFirst();
		    pstmt.close();
		    return;
		} 
		
		rs2.beforeFirst();
		jt.setModel(DbUtils.resultSetToTableModel(rs2));	
		pstmt.close();
		pstmt2.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm1));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where soundex=? AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm2));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (docID !="+rs1.getInt(1)+" OR soundex=?) AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
			pstmt2.close();
		}
		
	}
	
	/* NOT str1 AND str2 Operator */
	protected void requestedNotAndData(JTable jt,String searchTerm1, String searchTerm2, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
		String query2 = "select docID from inverted_file where word = '"+searchTerm1+"' group by docID";
		PreparedStatement pstmt2 = con.prepareStatement(query2);
		ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
		if (!rs1.next()) {
			String query = "select file_name as File, docID as Document_ID from inverted_file where word='"+searchTerm2+"' AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		    rs1.beforeFirst();
		    pstmt2.close();
		    return;
		} 
		
		String query = "select file_name as File, docID as Document_ID from inverted_file where (docID !="+rs1.getInt(1)+" AND word='"+searchTerm2+"') AND visible=1 group by file_name";
		PreparedStatement pstmt = con.prepareStatement(query);
		ResultSet rs2 = pstmt.executeQuery();//then execute the statement

		//Checking if the term found in the database
		if (!rs2.next()) {
			jt.setModel(dataModel);
			dataModel.setRowCount(0);
			JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
		    rs2.beforeFirst();
		    pstmt.close();
		    return;
		} 
		
		rs2.beforeFirst();
		jt.setModel(DbUtils.resultSetToTableModel(rs2));	
		pstmt.close();
		pstmt2.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm1));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where soundex=? AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm2));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (docID !="+rs1.getInt(1)+" AND soundex=?) AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
			pstmt2.close();
		}	
	}
	
	/* (str1 OR str2) not str3 Operator */
	protected void requestedOrNotData(JTable jt,String searchTerm1, String searchTerm2, String searchTerm3, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word = '"+searchTerm3+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm1+"' OR word='"+searchTerm2+"') AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm1+"' OR word='"+searchTerm2+"') AND docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement


			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm3));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? OR soundex= ?) AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm1));
				pstmt.setString(2, sndx.encode(searchTerm2));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? OR soundex= ?) AND docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm1));
			pstmt.setString(2, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	/* (str1 AND str2) not str3 Operator */
	protected void requestedAndNotData(JTable jt,String searchTerm1, String searchTerm2, String searchTerm3, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word = '"+searchTerm3+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query3 = "select docID from inverted_file where word = '"+searchTerm1+"' group by docID";
				PreparedStatement pstmt3 = con.prepareStatement(query3);
				ResultSet rs3 = pstmt3.executeQuery();//then execute the statement
				if(!rs3.next())
				{
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
					rs3.beforeFirst();
					pstmt3.close();
					return;
				}
				
				String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm2+"' AND docID="+rs3.getInt(1)+") AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query3 = "select docID from inverted_file where word = '"+searchTerm1+"' group by docID";
			PreparedStatement pstmt3 = con.prepareStatement(query3);
			ResultSet rs3 = pstmt3.executeQuery();//then execute the statement
			if (!rs3.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs3.beforeFirst();
			    pstmt3.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm2+"' AND docID="+rs3.getInt(1)+") AND docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement


			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm3));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query3 = "select docID from soundex_table where soundex = ? group by docID";
				PreparedStatement pstmt3 = con.prepareStatement(query3);
				pstmt3.setString(1, sndx.encode(searchTerm1));
				ResultSet rs3 = pstmt3.executeQuery();//then execute the statement
				if(!rs3.next())
				{
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
					rs3.beforeFirst();
					pstmt3.close();
					return;
				}
				
				String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? AND docID="+rs3.getInt(1)+") AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm2));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query3 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt3 = con.prepareStatement(query3);
			pstmt3.setString(1, sndx.encode(searchTerm1));
			ResultSet rs3 = pstmt3.executeQuery();//then execute the statement
			if (!rs3.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs3.beforeFirst();
			    pstmt3.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? AND docID="+rs3.getInt(1)+") AND docID !="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	//(str1 OR str2) AND str3 Operator
	protected void requestedOrAndData(JTable jt,String searchTerm1, String searchTerm2, String searchTerm3, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word = '"+searchTerm3+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm1+"' OR word='"+searchTerm2+"') AND docID="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement


			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm3));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? OR soundex= ?) AND docID="+rs1.getInt(1)+" AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm1));
			pstmt.setString(2, sndx.encode(searchTerm2));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	//(str1 AND str2) OR str3 Operator
	protected void requestedAndOrData(JTable jt,String searchTerm1, String searchTerm2, String searchTerm3, boolean soundex) throws SQLException
	{
		if(!soundex)
		{
			String query2 = "select docID from inverted_file where word='"+searchTerm2+"' group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from inverted_file where word='"+searchTerm3+"' AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from inverted_file where (word='"+searchTerm1+"' AND docID="+rs1.getInt(1)+" AND visible=1) OR word='"+searchTerm3+"' AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement


			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
		else
		{
			String query2 = "select docID from soundex_table where soundex = ? group by docID";
			PreparedStatement pstmt2 = con.prepareStatement(query2);
			pstmt2.setString(1, sndx.encode(searchTerm2));
			ResultSet rs1 = pstmt2.executeQuery();//then execute the statement
			if (!rs1.next()) {
				String query = "select file_name as File, docID as Document_ID from soundex_table where soundex= ? AND visible=1 group by file_name";
				PreparedStatement pstmt = con.prepareStatement(query);
				pstmt.setString(1, sndx.encode(searchTerm3));
				ResultSet rs2 = pstmt.executeQuery();//then execute the statement
				
				if (!rs2.next()) {
					jt.setModel(dataModel);
					dataModel.setRowCount(0);
					JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
				    rs2.beforeFirst();
				    pstmt.close();
				    return;
				} 
				
				rs2.beforeFirst();
				jt.setModel(DbUtils.resultSetToTableModel(rs2));	
				pstmt.close();
			    rs1.beforeFirst();
			    pstmt2.close();
			    return;
			} 
			
			String query = "select file_name as File, docID as Document_ID from soundex_table where (soundex= ? AND docID="+rs1.getInt(1)+" AND visible=1) OR soundex= ? AND visible=1 group by file_name";
			PreparedStatement pstmt = con.prepareStatement(query);
			pstmt.setString(1, sndx.encode(searchTerm1));
			pstmt.setString(2, sndx.encode(searchTerm3));
			ResultSet rs2 = pstmt.executeQuery();//then execute the statement
			
			//Checking if the term found in the database
			if (!rs2.next()) {
				jt.setModel(dataModel);
				dataModel.setRowCount(0);
				JOptionPane.showMessageDialog(null, "Term not found!", "Error!", JOptionPane.ERROR_MESSAGE);
			    rs2.beforeFirst();
			    pstmt.close();
			    return;
			} 
			
			rs2.beforeFirst();
			jt.setModel(DbUtils.resultSetToTableModel(rs2));	
			pstmt.close();
		}
	}
	
	//Function to create connection to the database
	public static Connection getConnection() throws Exception{
		try{
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/informationretrival?autoReconnect=true&useSSL=false";
			String username = "root";
			String password = "951753leon";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url,username,password);
			System.out.println("Connected");
			return conn;
		} catch(Exception e)
		{
			System.out.println(e);
		}
		return null;
	}
}



