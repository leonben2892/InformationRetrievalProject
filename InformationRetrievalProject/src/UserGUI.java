
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.awt.Font;


public class UserGUI {

	protected JFrame userFrame;
	protected File[] listOfFiles;
	protected File[] listOfImages;
	private JTextField userSearchTF;
	private JTable userTable;
	protected Highlighter.HighlightPainter myHighlightPainter; 
	protected JComboBox<String> userFileListCB;
	protected String[] stopwords;
	private DefaultTableModel dataModel;
	private int imageIndex;
	private ArrayList<String> imageList;
	private int userTableViewFlag;

	/**
	 * Create the application.
	 */
	public UserGUI(JFrame loginFrame, ParseFile pf) {
		initialize(loginFrame, pf);
		listOfFiles = null;
		listOfImages = null;
		myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
		dataModel = new DefaultTableModel();
		imageIndex = 0;
		userTableViewFlag = 0;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(JFrame loginFrame, ParseFile pf) {
		getFilesNames("Storage");
		getFilesNames("Storage\\Storage Images");	
		userFrame = new JFrame("User");
		userFrame.getContentPane().setBackground(new Color(0, 85, 128));
		userFrame.setBounds(600, 200, 800, 500);
		userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userFrame.getContentPane().setLayout(null);
		
		JScrollPane userScrollPaneTFA = new JScrollPane();
		userScrollPaneTFA.setBounds(36, 161, 430, 222);
		userFrame.getContentPane().add(userScrollPaneTFA);
		
		JTextArea userShowFileTFA = new JTextArea();
		userScrollPaneTFA.setViewportView(userShowFileTFA);
		
		JLabel userPictureLbl = new JLabel("");
		userPictureLbl.setBounds(36, 161, 430, 222);
		userFrame.getContentPane().add(userPictureLbl);
				
		JScrollPane userScrollPaneTable = new JScrollPane();
		userScrollPaneTable.setBounds(36, 56, 715, 94);
		userFrame.getContentPane().add(userScrollPaneTable);
		
		JCheckBox userSoundexCheckBox = new JCheckBox("Soundex Search");
		userSoundexCheckBox.setForeground(Color.WHITE);
		userSoundexCheckBox.setBackground(new Color(0, 85, 128));
		JCheckBox userPicturesCheckBox = new JCheckBox("Pictures Search");
		userPicturesCheckBox.setForeground(Color.WHITE);
		userPicturesCheckBox.setBackground(new Color(0, 85, 128));

		JLabel userPreviousIconLbl = new JLabel("");
		userPreviousIconLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\PreviousImage2.png"));
		JLabel userNextIconLbl = new JLabel("");
		
		JLabel userImageCounterLbl = new JLabel("");
		userImageCounterLbl.setForeground(Color.WHITE);
		userImageCounterLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
		userImageCounterLbl.setBounds(230, 402, 46, 14);
		userImageCounterLbl.setVisible(false);
		userFrame.getContentPane().add(userImageCounterLbl);
		
		//Soundex Check box
		userSoundexCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userPicturesCheckBox.setSelected(false);
			}
		});
		userSoundexCheckBox.setBounds(583, 5, 145, 23);
		userFrame.getContentPane().add(userSoundexCheckBox);
		
		userTable = new JTable();
		userTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = userTable.getSelectedRow();
				TableModel model = userTable.getModel();
				if(userTableViewFlag == 0)
				{
					if(!userSoundexCheckBox.isSelected())
						userFileListCB.setSelectedItem(model.getValueAt(i, 2).toString()+"."+model.getValueAt(i, 0).toString());
					else
						userFileListCB.setSelectedItem(model.getValueAt(i, 4).toString()+"."+model.getValueAt(i, 2).toString());
				}
				if(userTableViewFlag == 1)
				{
					userFileListCB.setSelectedItem(model.getValueAt(i, 1).toString()+"."+model.getValueAt(i, 0).toString());
				}
			}
		});
		userScrollPaneTable.setViewportView(userTable);
		
		//Pictures Check Box
		userPicturesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userSoundexCheckBox.setSelected(false);
			}
		});
		userPicturesCheckBox.setBounds(583, 26, 130, 23);
		userFrame.getContentPane().add(userPicturesCheckBox);
		
		JButton userBackBtn = new JButton("Back");
		userBackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userTable.setModel(dataModel);
				dataModel.setRowCount(0);
				userSearchTF.setText("");
				userShowFileTFA.setText("");
				userFileListCB.setSelectedIndex(0);
				userFrame.setVisible(false);
				loginFrame.setVisible(true);
				userSoundexCheckBox.setSelected(false);
				userPicturesCheckBox.setSelected(false);
				userPreviousIconLbl.setVisible(false);
				userNextIconLbl.setVisible(false);
				userImageCounterLbl.setVisible(false);
			}
		});
		userBackBtn.setBounds(36, 394, 89, 23);
		userFrame.getContentPane().add(userBackBtn);
		
		userFileListCB = new JComboBox<String>();
		for(int i=0; i<listOfFiles.length; i++)
		{
			if(!(listOfFiles[i].getName().equals("Storage Images")))
				userFileListCB.addItem(listOfFiles[i].getName());
		}
		userFileListCB.setBounds(476, 161, 200, 20);
		userFileListCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	Object obj = e.getSource();
		    	if(obj == userFileListCB)
		    	{
					userScrollPaneTFA.setVisible(true);
					userPictureLbl.setVisible(false);
					
					userNextIconLbl.setVisible(false);
					userPreviousIconLbl.setVisible(false);
					userImageCounterLbl.setVisible(false);
					
			    	BufferedReader br;
			    	String line;
			    	String fileDescription = "";
			    	
			    	@SuppressWarnings("unused")
					int idx;
			    	
					try {
						br = new BufferedReader(new FileReader("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\"+userFileListCB.getSelectedItem().toString()));
						while((line = br.readLine()) != null)
						{
							 if((idx = line.indexOf("$")) != -1)
							 {
								 while((line = br.readLine()) != null)
								 {
									 fileDescription += line+"\n";
								 }
							 }	   
						}
						userShowFileTFA.setText(fileDescription);
						userShowFileTFA.setCaretPosition(0);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
		    	}
			}
		});
		userFrame.getContentPane().add(userFileListCB);
		
		JButton userShowFileBtn = new JButton("Show File");
		userShowFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userScrollPaneTFA.setVisible(true);
				userPictureLbl.setVisible(false);
				userNextIconLbl.setVisible(false);
				userPreviousIconLbl.setVisible(false);
				userImageCounterLbl.setVisible(false);
				String fileContent = "";
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\"+userFileListCB.getSelectedItem().toString()));
						
					for(String line; (line = br.readLine()) != null;)
					{
						if(line.equals("$"))
							break;
						fileContent += line;
						fileContent += "\n";
					}
					br.close();
					userShowFileTFA.setText(fileContent);
					userShowFileTFA.setCaretPosition(0);
					if(!userSearchTF.getText().isEmpty())
						highlight(userShowFileTFA,userSearchTF.getText().replaceAll("\"", ""));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		userShowFileBtn.setBounds(476, 203, 200, 23);
		userFrame.getContentPane().add(userShowFileBtn);
		
		/* Previous Icon Label */
		userPreviousIconLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(imageIndex-1 < 0)
				{
					imageIndex = imageList.size()-1;
					userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
				else
				{
					imageIndex--;
					userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
					
				}
			}
		});
		userPreviousIconLbl.setBounds(171, 383, 46, 46);
		userPreviousIconLbl.setVisible(false);
		userFrame.getContentPane().add(userPreviousIconLbl);
		
		/* Next Icon Label */
		userNextIconLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(imageIndex+1 >= imageList.size())
				{
					imageIndex = 0;
					userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
				else
				{
					imageIndex++;
					userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
			}
		});
		userNextIconLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\NextImage.png"));
		userNextIconLbl.setBounds(275, 383, 50, 42);
		userNextIconLbl.setVisible(false);
		userFrame.getContentPane().add(userNextIconLbl);
				
		//Search! button Search
		JButton userSearchBtn = new JButton("Search!");
		userSearchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!userSearchTF.getText().isEmpty())
				{
					userNextIconLbl.setVisible(false);
					userPreviousIconLbl.setVisible(false);
					userImageCounterLbl.setVisible(false);
					imageIndex = 0;
					
					if(!(userPicturesCheckBox.isSelected()))
					{
						userScrollPaneTFA.setVisible(true);
						userPictureLbl.setVisible(false);
						
						if(!checkForStopWords(userSearchTF.getText()))
						{
							String term = userSearchTF.getText();							
							term = term.toLowerCase();
							highlight(userShowFileTFA,term);
							try {
								String[] booleanOperators = {"not ", " or ", " and "};
								if(!userSoundexCheckBox.isSelected())
								{
									if((term.startsWith("\"") && term.endsWith("\"")))
									{
										term = userSearchTF.getText().replaceAll("\"", "");
										getFilesNames("Storage");
										userTableViewFlag = 1;
										pf.requestDataInApostrophes(userTable, term, listOfFiles);		
									}
									/* Normal Search */
									else if(term.contains(booleanOperators[0]))
									{
										if(term.contains(booleanOperators[1]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[1]))
											{
												//NOT str1 OR str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[1]);
												String afterOr = after(term,booleanOperators[1]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterOr);
												userTableViewFlag = 1;
												pf.requestedNotOrData(userTable,afterNot,afterOr,false);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before OR: "+beforeOr+" | After OR: "+afterOr+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedOrNotData(userTable,beforeOr,afterOr,afterNot,false);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterAnd);
												userTableViewFlag = 1;
												pf.requestedNotAndData(userTable,afterNot,afterAnd,false);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before AND: "+beforeAnd+" | After And: "+afterAnd+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedAndNotData(userTable,beforeAnd,afterAnd,afterNot,false);
											}
										}
										else
										{
											if(term.indexOf(booleanOperators[0]) > 0)
											{
												//str1 not str2 Operator
												String beforeNot = before(term,booleanOperators[0]);
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println(beforeNot+" | "+afterNot);
												userTableViewFlag = 1;
												pf.requestedTermNotTermData(userTable,beforeNot,afterNot,false);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												userTableViewFlag = 1;
												pf.requestedNotData(userTable, after, false);
											}
										}
									}
									else if(term.contains(booleanOperators[1]))
									{
										if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[1])<term.indexOf(booleanOperators[2]))
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[1]))
												{
													//(str1 or str2) and str3
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													String afterAnd = after(term,booleanOperators[2]);
													userTableViewFlag = 1;
													pf.requestedOrAndData(userTable, beforeOr, afterOr, afterAnd, false);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													userTableViewFlag = 1;
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, beforeOr, false);
												}
											}
											else
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[2]))
												{
													//(str1 and str2) or str3
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													String afterOr = after(term,booleanOperators[1]);
													//System.out.println(beforeAnd+" "+afterAnd+" "+afterOr);
													userTableViewFlag = 1;
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, afterOr, false);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													userTableViewFlag = 1;
													pf.requestedOrAndData(userTable, beforeOr, afterOr, beforeAnd, false);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											userTableViewFlag = 1;
											pf.requestedORData(userTable, before, after, false);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										userTableViewFlag = 1;
										pf.requestedAndData(userTable, before, after, false);
									}
									else
									{
										userTableViewFlag = 0;
										boolean doSubStringSearch = term.endsWith("*");
										if(doSubStringSearch)
										{
											term = term.substring(0,term.length()-1) + "";
											pf.requestedSubStringData(userTable,term,false);
										}
										else
										{
											pf.requestedData(userTable, term, false);
										}
										
									}	
								}
								else
								{
									/* Soundex Search */
									if(term.contains(booleanOperators[0]))
									{
										if(term.contains(booleanOperators[1]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[1]))
											{
												//NOT str1 OR str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[1]);
												String afterOr = after(term,booleanOperators[1]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterOr);
												userTableViewFlag = 1;
												pf.requestedNotOrData(userTable,afterNot,afterOr,true);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before OR: "+beforeOr+" | After OR: "+afterOr+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedOrNotData(userTable,beforeOr,afterOr,afterNot,true);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterAnd);
												userTableViewFlag = 1;
												pf.requestedNotAndData(userTable,afterNot,afterAnd,true);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before AND: "+beforeAnd+" | After And: "+afterAnd+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedAndNotData(userTable,beforeAnd,afterAnd,afterNot,true);
											}
										}
										else
										{
											if(term.indexOf(booleanOperators[0]) > 0)
											{
												//str1 not str2 Operator
												String beforeNot = before(term,booleanOperators[0]);
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println(beforeNot+" | "+afterNot);
												userTableViewFlag = 1;
												pf.requestedTermNotTermData(userTable,beforeNot,afterNot,true);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												userTableViewFlag = 1;
												pf.requestedNotData(userTable, after, true);
											}
										}
									}
									else if(term.contains(booleanOperators[1]))
									{
										if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[1])<term.indexOf(booleanOperators[2]))
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[1]))
												{
													//(str1 or str2) and str3
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													String afterAnd = after(term,booleanOperators[2]);
													pf.requestedOrAndData(userTable, beforeOr, afterOr, afterAnd, true);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, beforeOr, true);
												}
											}
											else
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[2]))
												{
													//(str1 and str2) or str3
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													String afterOr = after(term,booleanOperators[1]);
													//System.out.println(beforeAnd+" "+afterAnd+" "+afterOr);
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, afterOr, true);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													pf.requestedOrAndData(userTable, beforeOr, afterOr, beforeAnd, true);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											userTableViewFlag = 1;
											pf.requestedORData(userTable, before, after, true);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										userTableViewFlag = 1;
										pf.requestedAndData(userTable, before, after, true);
									}
									else
									{
										userTableViewFlag = 0;
										pf.requestedSubStringData(userTable,term,true);
									}							
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							userTable.setModel(dataModel);
							dataModel.setRowCount(0);
							JOptionPane.showMessageDialog(null, "Term is in stop list!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
					if(userPicturesCheckBox.isSelected())
					{
						userTable.setModel(dataModel);
						dataModel.setRowCount(0);
						
						userScrollPaneTFA.setVisible(false);
						userPictureLbl.setVisible(true);
						
						imageList = new ArrayList<String>();					
						int i;
						getFilesNames("Storage\\Storage Images");	
						for(i=0; i<listOfImages.length; i++)
						{
							if(listOfImages[i].getName().toLowerCase().contains(userSearchTF.getText().toLowerCase()))
							{
								imageList.add(listOfImages[i].getName());
							}
						}
						if(imageList.size() == 0)
						{
							userScrollPaneTFA.setVisible(true);
							JOptionPane.showMessageDialog(null, "Picture not found!", "Error!", JOptionPane.ERROR_MESSAGE);
						}	
						else
						{
							userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
							if(imageList.size()>1)
							{
								userNextIconLbl.setVisible(true);
								userPreviousIconLbl.setVisible(true);
							}
							userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
							userImageCounterLbl.setVisible(true);
						}
					}
				}	
			}
		});
		userSearchBtn.setBounds(475, 17, 89, 23);
		userFrame.getContentPane().add(userSearchBtn);
		
		//Text Field Enter button search
		userSearchTF = new JTextField();
		userSearchTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!userSearchTF.getText().isEmpty())
				{
					userNextIconLbl.setVisible(false);
					userPreviousIconLbl.setVisible(false);
					userImageCounterLbl.setVisible(false);
					imageIndex = 0;
					
					if(!(userPicturesCheckBox.isSelected()))
					{
						userScrollPaneTFA.setVisible(true);
						userPictureLbl.setVisible(false);
						
						if(!checkForStopWords(userSearchTF.getText()))
						{
							String term = userSearchTF.getText();							
							term = term.toLowerCase();
							highlight(userShowFileTFA,term);
							try {
								String[] booleanOperators = {"not ", " or ", " and "};
								if(!userSoundexCheckBox.isSelected())
								{
									if((term.startsWith("\"") && term.endsWith("\"")))
									{
										term = userSearchTF.getText().replaceAll("\"", "");
										getFilesNames("Storage");
										userTableViewFlag = 1;
										pf.requestDataInApostrophes(userTable, term, listOfFiles);		
									}
									/* Normal Search */
									else if(term.contains(booleanOperators[0]))
									{
										if(term.contains(booleanOperators[1]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[1]))
											{
												//NOT str1 OR str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[1]);
												String afterOr = after(term,booleanOperators[1]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterOr);
												userTableViewFlag = 1;
												pf.requestedNotOrData(userTable,afterNot,afterOr,false);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before OR: "+beforeOr+" | After OR: "+afterOr+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedOrNotData(userTable,beforeOr,afterOr,afterNot,false);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterAnd);
												userTableViewFlag = 1;
												pf.requestedNotAndData(userTable,afterNot,afterAnd,false);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before AND: "+beforeAnd+" | After And: "+afterAnd+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedAndNotData(userTable,beforeAnd,afterAnd,afterNot,false);
											}
										}
										else
										{
											if(term.indexOf(booleanOperators[0]) > 0)
											{
												//str1 not str2 Operator
												String beforeNot = before(term,booleanOperators[0]);
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println(beforeNot+" | "+afterNot);
												userTableViewFlag = 1;
												pf.requestedTermNotTermData(userTable,beforeNot,afterNot,false);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												userTableViewFlag = 1;
												pf.requestedNotData(userTable, after, false);
											}
										}
									}
									else if(term.contains(booleanOperators[1]))
									{
										if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[1])<term.indexOf(booleanOperators[2]))
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[1]))
												{
													//(str1 or str2) and str3
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													String afterAnd = after(term,booleanOperators[2]);
													userTableViewFlag = 1;
													pf.requestedOrAndData(userTable, beforeOr, afterOr, afterAnd, false);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													userTableViewFlag = 1;
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, beforeOr, false);
												}
											}
											else
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[2]))
												{
													//(str1 and str2) or str3
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													String afterOr = after(term,booleanOperators[1]);
													//System.out.println(beforeAnd+" "+afterAnd+" "+afterOr);
													userTableViewFlag = 1;
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, afterOr, false);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													userTableViewFlag = 1;
													pf.requestedOrAndData(userTable, beforeOr, afterOr, beforeAnd, false);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											userTableViewFlag = 1;
											pf.requestedORData(userTable, before, after, false);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										userTableViewFlag = 1;
										pf.requestedAndData(userTable, before, after, false);
									}
									else
									{
										userTableViewFlag = 0;
										boolean doSubStringSearch = term.endsWith("*");
										if(doSubStringSearch)
										{
											term = term.substring(0,term.length()-1) + "";
											pf.requestedSubStringData(userTable,term,false);
										}
										else
										{
											pf.requestedData(userTable, term, false);
										}
										
									}	
								}
								else
								{
									/* Soundex Search */
									if(term.contains(booleanOperators[0]))
									{
										if(term.contains(booleanOperators[1]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[1]))
											{
												//NOT str1 OR str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[1]);
												String afterOr = after(term,booleanOperators[1]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterOr);
												userTableViewFlag = 1;
												pf.requestedNotOrData(userTable,afterNot,afterOr,true);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before OR: "+beforeOr+" | After OR: "+afterOr+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedOrNotData(userTable,beforeOr,afterOr,afterNot,true);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												//System.out.println("After Not: "+afterNot+" | After Or: "+afterAnd);
												userTableViewFlag = 1;
												pf.requestedNotAndData(userTable,afterNot,afterAnd,true);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println("Before AND: "+beforeAnd+" | After And: "+afterAnd+" | After Not: "+afterNot);
												userTableViewFlag = 1;
												pf.requestedAndNotData(userTable,beforeAnd,afterAnd,afterNot,true);
											}
										}
										else
										{
											if(term.indexOf(booleanOperators[0]) > 0)
											{
												//str1 not str2 Operator
												String beforeNot = before(term,booleanOperators[0]);
												String afterNot = after(term,booleanOperators[0]);
												//System.out.println(beforeNot+" | "+afterNot);
												userTableViewFlag = 1;
												pf.requestedTermNotTermData(userTable,beforeNot,afterNot,true);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												userTableViewFlag = 1;
												pf.requestedNotData(userTable, after, true);
											}
										}
									}
									else if(term.contains(booleanOperators[1]))
									{
										if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[1])<term.indexOf(booleanOperators[2]))
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[1]))
												{
													//(str1 or str2) and str3
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													String afterAnd = after(term,booleanOperators[2]);
													pf.requestedOrAndData(userTable, beforeOr, afterOr, afterAnd, true);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, beforeOr, true);
												}
											}
											else
											{
												if(term.indexOf("(")<term.indexOf(booleanOperators[2]))
												{
													//(str1 and str2) or str3
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													String afterOr = after(term,booleanOperators[1]);
													//System.out.println(beforeAnd+" "+afterAnd+" "+afterOr);
													pf.requestedAndOrData(userTable, beforeAnd, afterAnd, afterOr, true);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													pf.requestedOrAndData(userTable, beforeOr, afterOr, beforeAnd, true);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											userTableViewFlag = 1;
											pf.requestedORData(userTable, before, after, true);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										userTableViewFlag = 1;
										pf.requestedAndData(userTable, before, after, true);
									}
									else
									{
										userTableViewFlag = 0;
										pf.requestedSubStringData(userTable,term,true);
									}							
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							userTable.setModel(dataModel);
							dataModel.setRowCount(0);
							JOptionPane.showMessageDialog(null, "Term is in stop list!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
					if(userPicturesCheckBox.isSelected())
					{
						userTable.setModel(dataModel);
						dataModel.setRowCount(0);
						
						userScrollPaneTFA.setVisible(false);
						userPictureLbl.setVisible(true);
						
						imageList = new ArrayList<String>();					
						int i;
						getFilesNames("Storage\\Storage Images");	
						for(i=0; i<listOfImages.length; i++)
						{
							if(listOfImages[i].getName().toLowerCase().contains(userSearchTF.getText().toLowerCase()))
							{
								imageList.add(listOfImages[i].getName());
							}
						}
						if(imageList.size() == 0)
						{
							userScrollPaneTFA.setVisible(true);
							JOptionPane.showMessageDialog(null, "Picture not found!", "Error!", JOptionPane.ERROR_MESSAGE);
						}	
						else
						{
							userPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
							if(imageList.size()>1)
							{
								userNextIconLbl.setVisible(true);
								userPreviousIconLbl.setVisible(true);
							}
							userImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
							userImageCounterLbl.setVisible(true);
						}
					}
				}		
			}
		});
		userSearchTF.setBounds(36, 11, 430, 34);
		userFrame.getContentPane().add(userSearchTF);
		userSearchTF.setColumns(10);
				
		JMenuBar menuBar = new JMenuBar();
		userFrame.setJMenuBar(menuBar);
		
		JMenu userMenuFile = new JMenu("File");
		userMenuFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		menuBar.add(userMenuFile);
		
		JMenuItem userPrintMenuItem = new JMenuItem("Print");
		userPrintMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					boolean printSuccess = userShowFileTFA.print();
					if(printSuccess)
						JOptionPane.showMessageDialog(null, "Done Printing","Information",JOptionPane.INFORMATION_MESSAGE);
				} catch (PrinterException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}
		});
		userPrintMenuItem.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\print.png"));
		userMenuFile.add(userPrintMenuItem);
		
		JSeparator separator = new JSeparator();
		userMenuFile.add(separator);
		
		JMenuItem userHelpMenuItem = new JMenuItem("Help");
		userHelpMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog(null, "1. Search can be done by pressing 'Search!' button or pressing Enter key.\n"
				 		+ "2. For soundex search, check the 'Soundex Search' checkbox.\n"
				 		+ "3. For pictures search, check the 'Pictures Search' checkbox.\n"
				 		+ "4. Select a file from the combo box to show main infromation on the chosen file.\n"
				 		+ "5. Press 'Show File' button to see all content of the chosen file.\n"
				 		+ "6. Press 'Back' button to go to the login screen.", "User Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		userHelpMenuItem.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\help.png"));
		userMenuFile.add(userHelpMenuItem);
		
		JSeparator separator_1 = new JSeparator();
		userMenuFile.add(separator_1);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This project was created by: \n Matan Nabatian \n Leon Benjamin", "About", 3);
			}
		});
		mntmAbout.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\about.png"));
		userMenuFile.add(mntmAbout);
		
		JSeparator separator_2 = new JSeparator();
		userMenuFile.add(separator_2);
		
		JMenuItem userExitMenuItem = new JMenuItem("Exit");
		userExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
		});
		userMenuFile.add(userExitMenuItem);	
	}
	
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter{
		public MyHighlightPainter(Color color){
			super(color);
		}
	}
	
	public void highlight(JTextComponent textComp, String pattern)
	{
		removeHighlights(textComp);
		try{
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;
			
			while((pos=text.toUpperCase().indexOf(pattern.toUpperCase(),pos)) >= 0)
			{
				hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
				pos += pattern.length();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void removeHighlights(JTextComponent textComp)
	{
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for(int i=0; i<hilites.length; i++)
		{
			if(hilites[i].getPainter() instanceof MyHighlightPainter)
			{
				hilite.removeHighlight(hilites[i]);
			}
		}
	}
	
	public void removeHiddenFile(String fileName)
	{
		userFileListCB.removeItem(fileName);
	}
	
	public void addUnhiddenFile(String fileName)
	{
		userFileListCB.addItem(fileName);
	}
	
	private void getFilesNames(String location)
	{
		if(location.equals("Storage"))
		{
			File folder = new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\"+location);
			listOfFiles = folder.listFiles();
		}
		if(location.equals("Storage\\Storage Images"))
		{
			File folder = new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\"+location);
			listOfImages = folder.listFiles();
		}

	}
	
	public void updateComboBoxAfterParse()
	{
		getFilesNames("Storage");
		for(int i=0; i<listOfFiles.length; i++)
		{
			int addFileFlag = 1;	
			for(int j=0; j<userFileListCB.getItemCount(); j++)
			{
				if(listOfFiles[i].getName().equals(userFileListCB.getItemAt(j)))
				{
					addFileFlag = 0;
					break;
				}	
			}
			if(addFileFlag == 1)
			{
				if(!(listOfFiles[i].getName().equals("Storage Images")))
					userFileListCB.addItem(listOfFiles[i].getName());
			}		
		}
	}
	
	private boolean checkForStopWords(String term)
	{	
		String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
		return Arrays.asList(stopwords).contains(term);
	}
	
    private String before(String value, String a) {
        // Return substring containing all characters before a string.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }
    
    private String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }
    
    private String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }
}
