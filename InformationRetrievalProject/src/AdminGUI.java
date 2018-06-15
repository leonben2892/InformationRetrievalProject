
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminGUI {

	
	protected File[] tmpListOfFiles;//Temp Veriable!!!!!!!!!!!!!!!
	protected JFrame adminFrame;
	protected File[] listOfFiles;
	protected File[] listOfImages;
	protected List<File> selectedFiles;
	private JTextField adminSearchTF;
	private JTable adminTable;
	protected Highlighter.HighlightPainter myHighlightPainter; 
	private DefaultTableModel dataModel;
	private int imageIndex;
	private ArrayList<String> imageList;
	private int adminTableViewFlag;


	/**
	 * Create the application.
	 */
	public AdminGUI(JFrame loginFrame, ParseFile pf, ListFilesGUI listFileWindow, UserGUI userWindow) {
		initialize(loginFrame, pf, listFileWindow, userWindow);
		listOfFiles = null;
		listOfImages = null;
		selectedFiles = null;
		tmpListOfFiles = null;
		myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
		dataModel = new DefaultTableModel();
		imageIndex = 0;
		adminTableViewFlag = 0;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(JFrame loginFrame, ParseFile pf, ListFilesGUI listFileWindow, UserGUI userWindow) {
		getFilesNames("System Files");
		getFilesNames("Storage\\Storage Images");
		adminFrame = new JFrame("Admin");
		adminFrame.getContentPane().setBackground(new Color(0, 85, 128));
		adminFrame.setBounds(600, 200, 800, 500);
		adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		adminFrame.getContentPane().setLayout(null);
		
		JScrollPane adminScrollPaneTFA = new JScrollPane();
		adminScrollPaneTFA.setBounds(36, 161, 430, 222);
		adminFrame.getContentPane().add(adminScrollPaneTFA);
		
		JLabel adminPictureLbl = new JLabel("");
		adminPictureLbl.setBounds(36, 161, 430, 222);
		adminFrame.getContentPane().add(adminPictureLbl);
			
		JTextArea adminShowFileTFA = new JTextArea();
		adminShowFileTFA.setForeground(SystemColor.desktop);
		adminShowFileTFA.setBackground(Color.WHITE);
		adminScrollPaneTFA.setViewportView(adminShowFileTFA);
		
		JProgressBar parseProgressBar = new JProgressBar();
		parseProgressBar.setStringPainted(true);
		parseProgressBar.setBounds(546, 356, 183, 23);
		adminFrame.getContentPane().add(parseProgressBar);
		
		JLabel adminImageCounterLbl = new JLabel("");
		adminImageCounterLbl.setFont(new Font("Tahoma", Font.BOLD, 13));
		adminImageCounterLbl.setForeground(Color.WHITE);
		adminImageCounterLbl.setBounds(230, 402, 46, 14);
		adminFrame.getContentPane().add(adminImageCounterLbl);
		
		JLabel adminPreviousIconLbl = new JLabel("");
		adminPreviousIconLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(imageIndex-1 < 0)
				{
					imageIndex = imageList.size()-1;
					adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
				else
				{
					imageIndex--;
					adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
					
				}
			}
		});
		adminPreviousIconLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\PreviousImage2.png"));
		adminPreviousIconLbl.setBounds(171, 383, 46, 46);
		adminPreviousIconLbl.setVisible(false);
		adminFrame.getContentPane().add(adminPreviousIconLbl);
		
		JLabel adminNextIconLbl = new JLabel("");
		adminNextIconLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(imageIndex+1 >= imageList.size())
				{
					imageIndex = 0;
					adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
				else
				{
					imageIndex++;
					adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
					adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
				}
			}
		});
		adminNextIconLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\NextImage.png"));
		adminNextIconLbl.setBounds(275, 383, 46, 46);
		adminNextIconLbl.setVisible(false);
		adminFrame.getContentPane().add(adminNextIconLbl);
		
		JButton adminChooseFileBtn = new JButton("Files to Parse");
		adminChooseFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new JFXPanel();
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
						FileChooser fileChooser = new FileChooser();
						fileChooser.setInitialDirectory(new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Source Directory"));
						fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT Files", "*.txt"),new ExtensionFilter("DOCX Files", "*.docx"),new ExtensionFilter("DOC Files", "*.doc"),new ExtensionFilter("PDF Files", "*.pdf"));
					    fileChooser.setTitle("Choose Files");
					    selectedFiles = fileChooser.showOpenMultipleDialog(null);
			        }
			   });
			}
		});
		adminChooseFileBtn.setBounds(639, 282, 112, 23);
		adminFrame.getContentPane().add(adminChooseFileBtn);

		JComboBox<String> adminFilesListCB = new JComboBox<String>();
		for(int i=0; i<listOfFiles.length; i++)
		{
			if(!(listOfFiles[i].getName().equals("Storage Images")))
				adminFilesListCB.addItem(listOfFiles[i].getName());
		}
		adminFilesListCB.setBounds(476, 161, 200, 20);
		adminFilesListCB.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	Object obj = e.getSource();
		    	if(obj == adminFilesListCB)
		    	{
		    		adminScrollPaneTFA.setVisible(true);
		    		adminPictureLbl.setVisible(false);
		    		
					adminNextIconLbl.setVisible(false);
					adminPreviousIconLbl.setVisible(false);
					adminImageCounterLbl.setVisible(false);
					
			    	BufferedReader br;
			    	String line;
			    	String fileDescription = "";
			    	
			    	@SuppressWarnings("unused")
					int idx;
			    	
					try {
						br = new BufferedReader(new FileReader("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\System Files\\"+adminFilesListCB.getSelectedItem().toString()));
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
						adminShowFileTFA.setText(fileDescription);
						adminShowFileTFA.setCaretPosition(0);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
		    	}
		    }
		});
		adminFrame.getContentPane().add(adminFilesListCB);
		
		JCheckBox adminSoundexCheckBox = new JCheckBox("Soundex Search");
		JCheckBox adminPicturesCheckBox = new JCheckBox("Pictures Search");
		
		JButton adminBackBtn = new JButton("Back");
		adminBackBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adminTable.setModel(dataModel);
				dataModel.setRowCount(0);
				adminSearchTF.setText("");
				adminShowFileTFA.setText("");
				adminFilesListCB.setSelectedIndex(0);
				adminFrame.setVisible(false);
				loginFrame.setVisible(true);
				adminSoundexCheckBox.setSelected(false);
				adminPicturesCheckBox.setSelected(false);
				adminPreviousIconLbl.setVisible(false);
				adminNextIconLbl.setVisible(false);
				adminImageCounterLbl.setVisible(false);
			}
		});
		adminBackBtn.setBounds(36, 394, 89, 23);
		adminFrame.getContentPane().add(adminBackBtn);
		
		JButton adminParseBtn = new JButton("Parse Files");
		adminParseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    if (selectedFiles != null)
			    {
	                Thread parseThread = new Thread(new Runnable() {
	                	  public void run() {
	        				try {
	        				parseProgressBar.setValue(0);
	        				parseProgressBar.setMaximum(selectedFiles.size());
	      					for(int i=0; i<selectedFiles.size(); i++)
	      					{
	      						pf.createBasicData(selectedFiles.get(i));
	      						parseProgressBar.setValue(i+1);
	      					}
	      					moveAndDeleteOption(selectedFiles,"C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\",1,1);
	      					pf.createAlphabeticOrder();
	      					pf.createInvertedFile();
	      					pf.createDictionaryFile();
	      					pf.createOrderedSoundexTable();
	      					pf.createSoundexDictionaryFile();
	      					userWindow.updateComboBoxAfterParse();
	      					listFileWindow.updateHideFilesList();
	      				} catch (Exception e1) {
	      					e1.printStackTrace();
	      					}
	                	  }	                	  
	                	});
	                parseThread.start();   
			    }
			}
		});
		adminParseBtn.setBounds(639, 322, 112, 23);
		adminFrame.getContentPane().add(adminParseBtn);
		
		JButton adminShowFileBtn = new JButton("Show File");
		adminShowFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	    			adminScrollPaneTFA.setVisible(true);
	    			adminPictureLbl.setVisible(false);
					adminNextIconLbl.setVisible(false);
					adminPreviousIconLbl.setVisible(false);
					adminImageCounterLbl.setVisible(false);
					String fileContent = "";
					BufferedReader br = null;
					try {
						br = new BufferedReader(new FileReader("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\System Files\\"+adminFilesListCB.getSelectedItem().toString()));
							
						for(String line; (line = br.readLine()) != null;)
						{
							if(line.equals("$"))
								break;
							fileContent += line;
							fileContent += "\n";
						}
						br.close();
						adminShowFileTFA.setText(fileContent);
						adminShowFileTFA.setCaretPosition(0);
						if(!adminSearchTF.getText().isEmpty())
							highlight(adminShowFileTFA,adminSearchTF.getText().replaceAll("\"", ""));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});
		adminShowFileBtn.setBounds(476, 203, 200, 23);
		adminFrame.getContentPane().add(adminShowFileBtn);
		
		JButton adminAddFilesBtn = new JButton("Add Files");
		adminAddFilesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new JFXPanel();
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
						FileChooser fileChooser = new FileChooser();
						fileChooser.setInitialDirectory(new File("C:\\Users\\Leon\\Desktop"));
						fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT Files", "*.txt"),new ExtensionFilter("DOCX Files", "*.docx"),new ExtensionFilter("DOC Files", "*.doc"),new ExtensionFilter("PDF Files", "*.pdf"));
					    fileChooser.setTitle("Messi THE GOAT!!!");
					    selectedFiles = fileChooser.showOpenMultipleDialog(null);
					    try {
					    	if(selectedFiles != null)
					    	{
					    		moveAndDeleteOption(selectedFiles,"C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Source Directory\\",0,0);
					    		moveAndDeleteOption(selectedFiles,"C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\System Files\\",0,0);
					    		for(int i=0; i<selectedFiles.size(); i++)
					    		{
					    			adminFilesListCB.addItem(selectedFiles.get(i).getName());
					    		}
					    	}					    		
						} catch (IOException e) {
							e.printStackTrace();
						}
			        }
			   });	
			}
		});
		adminAddFilesBtn.setBounds(506, 322, 112, 23);
		adminFrame.getContentPane().add(adminAddFilesBtn);
		
		JButton adminHideFilesBtn = new JButton("Hide Files");
		adminHideFilesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {						
							listFileWindow.frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		adminHideFilesBtn.setBounds(506, 282, 112, 23);
		adminFrame.getContentPane().add(adminHideFilesBtn);
				
		/* Soundex Check Box */
		adminSoundexCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 adminPicturesCheckBox.setSelected(false);
			}
		});
		adminSoundexCheckBox.setForeground(Color.WHITE);
		adminSoundexCheckBox.setBackground(new Color(0, 85, 128));
		adminSoundexCheckBox.setBounds(583, 5, 145, 23);
		adminFrame.getContentPane().add(adminSoundexCheckBox);
		
		JScrollPane adminScrollPaneTable = new JScrollPane();
		adminScrollPaneTable.setBounds(36, 56, 715, 94);
		adminFrame.getContentPane().add(adminScrollPaneTable);
		
		adminTable = new JTable();
		adminTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = adminTable.getSelectedRow();
				TableModel model = adminTable.getModel();
				if(adminTableViewFlag == 0)
				{
					if(!adminSoundexCheckBox.isSelected())
						adminFilesListCB.setSelectedItem(model.getValueAt(i, 2).toString()+"."+model.getValueAt(i, 0).toString());
					else
						adminFilesListCB.setSelectedItem(model.getValueAt(i, 4).toString()+"."+model.getValueAt(i, 2).toString());
				}
				if(adminTableViewFlag == 1)
				{
					adminFilesListCB.setSelectedItem(model.getValueAt(i, 1).toString()+"."+model.getValueAt(i, 0).toString());
				}
			}
		});
		adminScrollPaneTable.setViewportView(adminTable);
		
		JButton adminSearchBtn = new JButton("Search!");
		adminSearchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!adminSearchTF.getText().isEmpty())
				{
					adminNextIconLbl.setVisible(false);
					adminPreviousIconLbl.setVisible(false);
					adminImageCounterLbl.setVisible(false);
					imageIndex = 0;
					
					if(!(adminPicturesCheckBox.isSelected()))
					{
						adminScrollPaneTFA.setVisible(true);
						adminPictureLbl.setVisible(false);
						
						if(!checkForStopWords(adminSearchTF.getText()))
						{
							String term = adminSearchTF.getText();							
							term = term.toLowerCase();
							highlight(adminShowFileTFA,term);
							try {
								String[] booleanOperators = {"not ", " or ", " and "};
								if(!adminSoundexCheckBox.isSelected())
								{
									if((term.startsWith("\"") && term.endsWith("\"")))
									{
										term = adminSearchTF.getText().replaceAll("\"", "");
										getFilesNames("Storage");
										adminTableViewFlag = 1;
										pf.requestDataInApostrophes(adminTable, term, listOfFiles);		
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
												adminTableViewFlag = 1;
												pf.requestedNotOrData(adminTable,afterNot,afterOr,false);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedOrNotData(adminTable,beforeOr,afterOr,afterNot,false);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												adminTableViewFlag = 1;
												pf.requestedNotAndData(adminTable,afterNot,afterAnd,false);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedAndNotData(adminTable,beforeAnd,afterAnd,afterNot,false);
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
												adminTableViewFlag = 1;
												pf.requestedTermNotTermData(adminTable,beforeNot,afterNot,false);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedNotData(adminTable, after, false);
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
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, afterAnd, false);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, beforeOr, false);
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
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, afterOr, false);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, beforeAnd, false);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											adminTableViewFlag = 1;
											pf.requestedORData(adminTable, before, after, false);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										adminTableViewFlag = 1;
										pf.requestedAndData(adminTable, before, after, false);
									}
									else
									{
										adminTableViewFlag = 0;
										boolean doSubStringSearch = term.endsWith("*");
										if(doSubStringSearch)
										{
											term = term.substring(0,term.length()-1) + "";
											pf.requestedSubStringData(adminTable,term,false);
										}
										else
										{
											pf.requestedData(adminTable, term, false);
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
												adminTableViewFlag = 1;
												pf.requestedNotOrData(adminTable,afterNot,afterOr,true);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedOrNotData(adminTable,beforeOr,afterOr,afterNot,true);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												adminTableViewFlag = 1;
												pf.requestedNotAndData(adminTable,afterNot,afterAnd,true);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedAndNotData(adminTable,beforeAnd,afterAnd,afterNot,true);
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
												adminTableViewFlag = 1;
												pf.requestedTermNotTermData(adminTable,beforeNot,afterNot,true);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedNotData(adminTable, after, true);
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
													adminTableViewFlag = 1;
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, afterAnd, true);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													adminTableViewFlag = 1;
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, beforeOr, true);
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
													adminTableViewFlag = 1;
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, afterOr, true);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													adminTableViewFlag = 1;
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, beforeAnd, true);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											adminTableViewFlag = 1;
											pf.requestedORData(adminTable, before, after, true);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										adminTableViewFlag = 1;
										pf.requestedAndData(adminTable, before, after, true);
									}
									else
									{
										adminTableViewFlag = 0;
										pf.requestedSubStringData(adminTable,term,true);
									}							
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							adminTable.setModel(dataModel);
							dataModel.setRowCount(0);
							JOptionPane.showMessageDialog(null, "Term is in stop list!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
					if(adminPicturesCheckBox.isSelected())
					{
						adminTable.setModel(dataModel);
						dataModel.setRowCount(0);
						
						adminScrollPaneTFA.setVisible(false);
						adminPictureLbl.setVisible(true);
						
						imageList = new ArrayList<String>();					
						int i;
						getFilesNames("Storage\\Storage Images");	
						for(i=0; i<listOfImages.length; i++)
						{
							if(listOfImages[i].getName().toLowerCase().contains(adminSearchTF.getText().toLowerCase()))
							{
								imageList.add(listOfImages[i].getName());
							}
						}
						if(imageList.size() == 0)
						{
							adminScrollPaneTFA.setVisible(true);
							JOptionPane.showMessageDialog(null, "Picture not found!", "Error!", JOptionPane.ERROR_MESSAGE);
						}	
						else
						{
							adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
							if(imageList.size()>1)
							{
								adminNextIconLbl.setVisible(true);
								adminPreviousIconLbl.setVisible(true);
							}
							adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
							adminImageCounterLbl.setVisible(true);
						}
					}
				}	
			}
		});
		adminSearchBtn.setBounds(475, 17, 89, 23);
		adminFrame.getContentPane().add(adminSearchBtn);
		
		JLabel adminOptionsLbl = new JLabel("Admin Options");
		adminOptionsLbl.setForeground(Color.WHITE);
		adminOptionsLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\adminOption.png"));
		adminOptionsLbl.setFont(new Font("Imprint MT Shadow", Font.BOLD, 16));
		adminOptionsLbl.setBounds(546, 237, 183, 34);
		adminFrame.getContentPane().add(adminOptionsLbl);
		
		/* Pictures Check Box */
		adminPicturesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adminSoundexCheckBox.setSelected(false);
			}
		});
		adminPicturesCheckBox.setBounds(583, 26, 125, 23);
		adminPicturesCheckBox.setForeground(Color.WHITE);
		adminPicturesCheckBox.setBackground(new Color(0, 85, 128));
		adminFrame.getContentPane().add(adminPicturesCheckBox);
		
		adminSearchTF = new JTextField();
		adminSearchTF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!adminSearchTF.getText().isEmpty())
				{
					adminNextIconLbl.setVisible(false);
					adminPreviousIconLbl.setVisible(false);
					adminImageCounterLbl.setVisible(false);
					imageIndex = 0;
					
					if(!(adminPicturesCheckBox.isSelected()))
					{
						adminScrollPaneTFA.setVisible(true);
						adminPictureLbl.setVisible(false);
						
						if(!checkForStopWords(adminSearchTF.getText()))
						{
							String term = adminSearchTF.getText();							
							term = term.toLowerCase();
							highlight(adminShowFileTFA,term);
							try {
								String[] booleanOperators = {"not ", " or ", " and "};
								if(!adminSoundexCheckBox.isSelected())
								{
									if((term.startsWith("\"") && term.endsWith("\"")))
									{
										term = adminSearchTF.getText().replaceAll("\"", "");
										getFilesNames("Storage");
										adminTableViewFlag = 1;
										pf.requestDataInApostrophes(adminTable, term, listOfFiles);		
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
												adminTableViewFlag = 1;
												pf.requestedNotOrData(adminTable,afterNot,afterOr,false);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedOrNotData(adminTable,beforeOr,afterOr,afterNot,false);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												adminTableViewFlag = 1;
												pf.requestedNotAndData(adminTable,afterNot,afterAnd,false);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedAndNotData(adminTable,beforeAnd,afterAnd,afterNot,false);
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
												adminTableViewFlag = 1;
												pf.requestedTermNotTermData(adminTable,beforeNot,afterNot,false);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedNotData(adminTable, after, false);
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
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, afterAnd, false);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, beforeOr, false);
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
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, afterOr, false);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, beforeAnd, false);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											adminTableViewFlag = 1;
											pf.requestedORData(adminTable, before, after, false);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										adminTableViewFlag = 1;
										pf.requestedAndData(adminTable, before, after, false);
									}
									else
									{
										adminTableViewFlag = 0;
										boolean doSubStringSearch = term.endsWith("*");
										if(doSubStringSearch)
										{
											term = term.substring(0,term.length()-1) + "";
											pf.requestedSubStringData(adminTable,term,false);
										}
										else
										{
											pf.requestedData(adminTable, term, false);
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
												adminTableViewFlag = 1;
												pf.requestedNotOrData(adminTable,afterNot,afterOr,true);
											}
											else{
												//(str1 OR str2) NOT str3
												String beforeOr = between(term,"(",booleanOperators[1]);
												String afterOr = between(term,booleanOperators[1],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedOrNotData(adminTable,beforeOr,afterOr,afterNot,true);
											}

										}
										else if(term.contains(booleanOperators[2]))
										{
											if(term.indexOf(booleanOperators[0])<term.indexOf(booleanOperators[2]))
											{
												//NOT str1 AND str2 Operator
												String afterNot = between(term,booleanOperators[0],booleanOperators[2]);
												String afterAnd = after(term,booleanOperators[2]);
												adminTableViewFlag = 1;
												pf.requestedNotAndData(adminTable,afterNot,afterAnd,true);
											}
											else
											{
												//(str1 AND str2) NOT str3
												String beforeAnd = between(term,"(",booleanOperators[2]);
												String afterAnd = between(term,booleanOperators[2],")");
												String afterNot = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedAndNotData(adminTable,beforeAnd,afterAnd,afterNot,true);
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
												adminTableViewFlag = 1;
												pf.requestedTermNotTermData(adminTable,beforeNot,afterNot,true);
											}
											else
											{
												//Not str1 Operator
												String after = after(term,booleanOperators[0]);
												adminTableViewFlag = 1;
												pf.requestedNotData(adminTable, after, true);
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
													adminTableViewFlag = 1;
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, afterAnd, true);
												}
												else
												{
													//str1 or (str2 and str3)
													String beforeOr = before(term,booleanOperators[1]);
													String beforeAnd = between(term,"(",booleanOperators[2]);
													String afterAnd = between(term,booleanOperators[2],")");
													//System.out.println(beforeOr + " | "+beforeAnd+" | "+afterAnd);
													adminTableViewFlag = 1;
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, beforeOr, true);
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
													adminTableViewFlag = 1;
													pf.requestedAndOrData(adminTable, beforeAnd, afterAnd, afterOr, true);
												}
												else
												{
													//str1 and (str2 or str3)
													String beforeAnd = before(term,booleanOperators[2]);
													String beforeOr = between(term,"(",booleanOperators[1]);
													String afterOr = between(term,booleanOperators[1],")");
													//System.out.println(beforeAnd + " | "+beforeOr+" | "+afterOr);
													adminTableViewFlag = 1;
													pf.requestedOrAndData(adminTable, beforeOr, afterOr, beforeAnd, true);
												}
											}
										}
										else
										{
											//Or Operator
											String before = before(term,booleanOperators[1]);
											String after = after(term,booleanOperators[1]);
											adminTableViewFlag = 1;
											pf.requestedORData(adminTable, before, after, true);
										}
									}
									else if(term.contains(booleanOperators[2]))
									{
										//And Operator
										String before = before(term,booleanOperators[2]);
										String after = after(term,booleanOperators[2]);
										adminTableViewFlag = 1;
										pf.requestedAndData(adminTable, before, after, true);
									}
									else
									{
										adminTableViewFlag = 0;
										pf.requestedSubStringData(adminTable,term,true);
									}							
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						else
						{
							adminTable.setModel(dataModel);
							dataModel.setRowCount(0);
							JOptionPane.showMessageDialog(null, "Term is in stop list!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
					if(adminPicturesCheckBox.isSelected())
					{
						adminTable.setModel(dataModel);
						dataModel.setRowCount(0);
						
						adminScrollPaneTFA.setVisible(false);
						adminPictureLbl.setVisible(true);
						
						imageList = new ArrayList<String>();					
						int i;
						getFilesNames("Storage\\Storage Images");	
						for(i=0; i<listOfImages.length; i++)
						{
							if(listOfImages[i].getName().toLowerCase().contains(adminSearchTF.getText().toLowerCase()))
							{
								imageList.add(listOfImages[i].getName());
							}
						}
						if(imageList.size() == 0)
						{
							adminScrollPaneTFA.setVisible(true);
							JOptionPane.showMessageDialog(null, "Picture not found!", "Error!", JOptionPane.ERROR_MESSAGE);
						}	
						else
						{
							adminPictureLbl.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage\\Storage Images\\"+imageList.get(imageIndex)));
							if(imageList.size()>1)
							{
								adminNextIconLbl.setVisible(true);
								adminPreviousIconLbl.setVisible(true);
							}
							adminImageCounterLbl.setText(String.valueOf(imageIndex+1)+"/"+String.valueOf(imageList.size()));
							adminImageCounterLbl.setVisible(true);
						}
					}
				}			
			}
		});
		adminSearchTF.setBounds(36, 11, 430, 34);
		adminFrame.getContentPane().add(adminSearchTF);
		adminSearchTF.setColumns(10);
								
		JMenuBar menuBar = new JMenuBar();
		adminFrame.setJMenuBar(menuBar);
		
		JMenu adminMenuFile = new JMenu("File");
		menuBar.add(adminMenuFile);
		
		JMenuItem adminPrintMenuItem = new JMenuItem("Print");
		adminPrintMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					boolean printSuccess = adminShowFileTFA.print();
					if(printSuccess)
						JOptionPane.showMessageDialog(null, "Done Printing","Information",JOptionPane.INFORMATION_MESSAGE);
				} catch (PrinterException e1) {
					JOptionPane.showMessageDialog(null, e1);
				}
			}
		});
		adminPrintMenuItem.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\print.png"));
		adminMenuFile.add(adminPrintMenuItem);
		
		JSeparator separator = new JSeparator();
		adminMenuFile.add(separator);
		
		JMenuItem adminHelpMenuItem = new JMenuItem("Help");
		adminHelpMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog(null, "1. Admin screen has all the options the user screen has.\n"
				 		+ "2. Press 'Hide Files' button to hide or unhide chosen files.\n"
				 		+ "3. Press 'Files to Parse' to choose the files you want to parse.\n"
				 		+ "4. Press 'Parse Files' to parse all the files you have chosen.[Files will be moved to Storage directory and deleted from Source directory]\n"
				 		+ "5. Press 'Add Files' to add more files to Source directory.\n"
				 		+ "6. After pressing 'Parse Files' button, you can watch the progression bar to see the parsing process progress.", "Admin Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		adminHelpMenuItem.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\help.png"));
		adminMenuFile.add(adminHelpMenuItem);
		
		JSeparator separator_1 = new JSeparator();
		adminMenuFile.add(separator_1);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This project was created by: \n Matan Nabatian \n Leon Benjamin", "About", 3);
			}
		});
		mntmAbout.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\about.png"));
		adminMenuFile.add(mntmAbout);
		
		JSeparator separator_2 = new JSeparator();
		adminMenuFile.add(separator_2);
		
		JMenuItem adminExitMenuItem = new JMenuItem("Exit");
		adminExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
		});
		adminMenuFile.add(adminExitMenuItem);
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
	
	private boolean checkForStopWords(String term)
	{	
		String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
		return Arrays.asList(stopwords).contains(term);
	}

	private void getFilesNames(String location)
	{
		if(location.equals("System Files"))
		{
			File folder = new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\"+location);
			listOfFiles = folder.listFiles();
		}
		if(location.equals("Storage\\Storage Images"))
		{
			File folder = new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\"+location);
			listOfImages = folder.listFiles();
		}

	}
	
	/*Function to index,move & delete a file after we parse it*/
	private void moveAndDeleteOption(List<File> tmpFile,String str,int del,int addindex) throws IOException
	{
		File destination = null;
		for(int i=0; i<tmpFile.size(); i++)
		{
			if(addindex==1)
			{
				destination = new File(str+Integer.toString(LoginGUI.fileIndex)+"."+tmpFile.get(i).getName());
				LoginGUI.fileIndex++;
			}		
			else
				destination = new File(str+tmpFile.get(i).getName());
			Files.copy(tmpFile.get(i).toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
			if(del==1)
			{
				Files.delete(tmpFile.get(i).toPath());
			}
		}
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



