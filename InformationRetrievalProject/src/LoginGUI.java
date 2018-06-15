
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Color;


public class LoginGUI {

	protected JFrame loginFrame;
	private JTextField userNameTF;
	private JPasswordField passwordTF;
	protected ParseFile pf;
	protected static int fileIndex;
	protected ListFilesGUI listFileswindow; 
	protected UserGUI userWindow; 
	protected AdminGUI adminWindow; 

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public LoginGUI() throws Exception {
		initialize();
		fileIndex = 4;		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {		
		
		loginFrame = new JFrame("Login");
		loginFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
		loginFrame.setBounds(600, 200, 800, 500);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		loginFrame.getContentPane().setLayout(null);
		
		pf = new ParseFile();
		userWindow = new UserGUI(loginFrame, pf);	
		listFileswindow = new ListFilesGUI(pf, userWindow); 
		adminWindow = new AdminGUI(loginFrame, pf, listFileswindow, userWindow);	
		
		JLabel loginLbl = new JLabel("Login");
		loginLbl.setForeground(Color.BLACK);
		loginLbl.setFont(new Font("Mangal", Font.BOLD, 29));
		loginLbl.setBounds(319, 32, 169, 39);
		loginFrame.getContentPane().add(loginLbl);
		
		JLabel userNameLbl = new JLabel("Username:");
		userNameLbl.setForeground(Color.BLACK);
		userNameLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		userNameLbl.setBounds(253, 82, 101, 14);
		loginFrame.getContentPane().add(userNameLbl);
		
		JLabel passwordLbl = new JLabel("Password:");
		passwordLbl.setForeground(Color.BLACK);
		passwordLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		passwordLbl.setBounds(253, 123, 101, 14);
		loginFrame.getContentPane().add(passwordLbl);
		
		userNameTF = new JTextField();
		userNameTF.setBounds(378, 82, 86, 20);
		loginFrame.getContentPane().add(userNameTF);
		userNameTF.setColumns(10);
		PromptSupport.setPrompt("Username", userNameTF);
		
		passwordTF = new JPasswordField();
		passwordTF.setBounds(378, 122, 86, 20);
		PromptSupport.setPrompt("Password", passwordTF);
		loginFrame.getContentPane().add(passwordTF);
		
		JButton LoginBtn = new JButton("Login");
		LoginBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		LoginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(userNameTF.getText().equals("user") && Arrays.equals(passwordTF.getPassword(), new char[]{'u','s','e','r'}))
				{
					System.out.println("User GUI Logged in!");
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								userWindow.userFrame.setVisible(true);
								userNameTF.setText("");
								passwordTF.setText("");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					loginFrame.setVisible(false);
				}
				else if(userNameTF.getText().equals("admin") && Arrays.equals(passwordTF.getPassword(), new char[]{'a','d','m','i','n'}))
				{
					System.out.println("Manager GUI Logged in!");
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								adminWindow.adminFrame.setVisible(true);
								userNameTF.setText("");
								passwordTF.setText("");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					loginFrame.setVisible(false);
				}
				else
					JOptionPane.showMessageDialog(null, "Incorrect Username/Password.\n           Please try again!", "Error", 0);
			}
		});
		LoginBtn.setBounds(319, 168, 89, 23);
		loginFrame.getContentPane().add(LoginBtn);
		
		JButton btnNewButton = new JButton("admin");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Manager GUI Logged in!");
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							adminWindow.adminFrame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				loginFrame.setVisible(false);
			}
		});
		btnNewButton.setBounds(319, 212, 89, 23);
		btnNewButton.setVisible(false);
		loginFrame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("user");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("User GUI Logged in!");
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							userWindow.userFrame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				loginFrame.setVisible(false);
			}
		});
		btnNewButton_1.setBounds(319, 255, 89, 23);
		btnNewButton_1.setVisible(false);
		loginFrame.getContentPane().add(btnNewButton_1);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\LoginBackground.jpg"));
		lblNewLabel.setBounds(0, 0, 784, 440);
		loginFrame.getContentPane().add(lblNewLabel);
		
		JMenuBar menuBar = new JMenuBar();
		loginFrame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "1. Enter username and user password, then press 'Login' button to move to user screen.\n"
				 		+ "2. Enter username and admin password, then press 'Login' button to move to admin screen.\n"
				 		+ "3. If the username or password are incorrect, an error message will popup.", "Admin Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mntmHelp.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\help.png"));
		mnFile.add(mntmHelp);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.LIGHT_GRAY);
		mnFile.add(separator_2);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This project was created by: \n Matan Nabatian \n Leon Benjamin", "About", 3);
			}
		});
		mntmAbout.setIcon(new ImageIcon("C:\\Users\\Leon\\workspace\\InformationRetrievalProject\\src\\resources\\about.png"));
		mnFile.add(mntmAbout);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(Color.LIGHT_GRAY);
		mnFile.add(separator_3);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(JFrame.EXIT_ON_CLOSE);
			}
		});
		mnFile.add(mntmExit);
				
	}
}
