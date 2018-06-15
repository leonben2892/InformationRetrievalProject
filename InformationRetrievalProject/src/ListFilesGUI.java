import javax.swing.JFrame;
import javax.swing.JList;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;


public class ListFilesGUI {

	protected JFrame frame;
	protected File[] listOfFiles;
	protected List<String> fileNames;
	protected List<Integer> listIndexes;
	protected int flag;
	protected DefaultListModel<String> hideListModel;
	/**
	 * Create the application.
	 */
	public ListFilesGUI(ParseFile pf, UserGUI userWindow) {
		initialize(pf, userWindow);
		listOfFiles = null;
		fileNames = null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(ParseFile pf, UserGUI userWindow) {
		getFilesNames();
		frame = new JFrame("Hide Files");
		frame.setBounds(100, 100, 900, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		hideListModel = new DefaultListModel<String>();
		for(int i=0; i<listOfFiles.length; i++)
		{
			if(!(listOfFiles[i].getName().equals("Storage Images")))
			{
				hideListModel.addElement(listOfFiles[i].getName());
			}			
		}
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(78, 55, 284, 125);
		frame.getContentPane().add(scrollPane);
		
		JList<String> listFilesHideList = new JList<String>(hideListModel);
		listFilesHideList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fileNames = listFilesHideList.getSelectedValuesList();
			}
		});
		scrollPane.setViewportView(listFilesHideList);
		listFilesHideList.setLayoutOrientation(JList.VERTICAL);
		listFilesHideList.setVisibleRowCount(-1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(516, 55, 282, 123);
		frame.getContentPane().add(scrollPane_1);
		
		DefaultListModel<String> unhideListModel = new DefaultListModel<String>();
		JList<String> listFilesUnhideList = new JList<String>(unhideListModel);
		listFilesUnhideList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fileNames = listFilesUnhideList.getSelectedValuesList();
			}
		});
		scrollPane_1.setViewportView(listFilesUnhideList);
		listFilesUnhideList.setVisibleRowCount(-1);
		listFilesUnhideList.setLayoutOrientation(JList.VERTICAL);
		
		JButton listFilesHideBtn = new JButton("Hide Files");
		listFilesHideBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flag = 1;
				List<Integer> fileIndexes = new ArrayList<Integer>();
				if(fileNames != null)
				{
					for(int i=0; i<fileNames.size(); i++)
					{
						userWindow.removeHiddenFile(fileNames.get(i));
						hideListModel.removeElement(fileNames.get(i));
						unhideListModel.addElement(fileNames.get(i));
						fileIndexes.add(Integer.parseInt(fileNames.get(i).replaceAll("[^\\d]", "")));					
					}
					fileNames.clear();
					try {
						pf.updateVisibility(fileIndexes,0);
						pf.createDictionaryFile();
						pf.createSoundexDictionaryFile();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				listIndexes = fileIndexes;
				listFilesHideList.clearSelection();
			}
		});
		listFilesHideBtn.setBounds(168, 202, 112, 23);
		frame.getContentPane().add(listFilesHideBtn);
		
		JButton listFilesUnhideBtn = new JButton("Unhide Files");
		listFilesUnhideBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Integer> fileIndexes = new ArrayList<Integer>();
				if(fileNames != null)
				{
					for(int i=0; i<fileNames.size(); i++)
					{
						userWindow.addUnhiddenFile(fileNames.get(i));
						hideListModel.addElement(fileNames.get(i));
						unhideListModel.removeElement(fileNames.get(i));
						fileIndexes.add(Integer.parseInt(fileNames.get(i).replaceAll("[^\\d]", "")));
					}
					fileNames.clear();
					try {
						pf.updateVisibility(fileIndexes,1);
						pf.createDictionaryFile();
						pf.createSoundexDictionaryFile();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				listFilesHideList.clearSelection();
			}
		});
		listFilesUnhideBtn.setBounds(605, 202, 112, 23);
		frame.getContentPane().add(listFilesUnhideBtn);
		
		JLabel listOfFilesToHideLbl = new JLabel("Visible Files");
		listOfFilesToHideLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
		listOfFilesToHideLbl.setBounds(177, 11, 149, 33);
		frame.getContentPane().add(listOfFilesToHideLbl);
		
		JLabel listOfFilesToUnhideLbl = new JLabel("Hidden Files");
		listOfFilesToUnhideLbl.setFont(new Font("Tahoma", Font.BOLD, 16));
		listOfFilesToUnhideLbl.setBounds(591, 11, 149, 33);
		frame.getContentPane().add(listOfFilesToUnhideLbl);
	}
	
	protected void getFilesNames()
	{
		File folder = new File("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\Storage");
		listOfFiles = folder.listFiles();
	}
	
	protected void updateHideFilesList()
	{
		getFilesNames();			
		for(int i=0; i<listOfFiles.length; i++)
		{
			int addFileFlag = 1;	
			for(int j=0; j<hideListModel.size(); j++)
			{
				if(listOfFiles[i].getName().equals(hideListModel.getElementAt(j)))
				{
					addFileFlag = 0;
					break;
				}	
			}
			if(addFileFlag == 1)
			{
				if(!(listOfFiles[i].getName().equals("Storage Images")))
					hideListModel.addElement(listOfFiles[i].getName());
			}		
		}
	}
}
