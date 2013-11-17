package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

import controler.Client;

/**
 * Affichage principal du programme/
 * @author Alexandre CANTAIS
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GraphicalView extends JFrame {

	private final boolean DEBUG = false;
	private final int X_SIZE = 16;
	private final int Y_SIZE = 16;

	private int x1, y1;
	private int x2, y2;

	private Client client;

	private JTextArea console;
	private JScrollPane consolePanel;
	private JButton connectBtn;
	private JButton logBtn;
	private JButton disconnectBtn;
	private JButton statsButton;
	private DrawListener drawListener;
	private JPanel chatPanel;
	private JTextArea chat;
	private JScrollPane chatViewPanel;
	private JPanel chatInPanel;
	private JTextArea chatIn;
	private JButton chatBtn;
	private JPanel drawPanel;

	// Fenêtre d'interaction
	private ConnectDialog connectDial;

	private Graphics currentGraph;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel westPanel;
	private JPanel centerPanel;
	private JPanel eastPanel;
	private JTextArea words;
	private JScrollPane wordsViewPanel;
	
	private BufferedImage img;

	public void error(String mess){
		System.err.println(mess + "\n");	
	}

	public GraphicalView(Client client){
		this.setTitle("iSketch - It's a revolution");

		this.client = client;
		this.drawListener = new DrawListener(client, this);

		this.topPanel = new JPanel();
		this.westPanel = new JPanel();
		this.centerPanel = new JPanel();
		this.eastPanel = new JPanel();
		this.mainPanel = new JPanel(new BorderLayout());

		this.setSize(new Dimension(1300,740));

		// panel console
		this.console = new JTextArea(10, 50);
		this.console.setSize(this.getWidth(), 100);
		this.console.setBackground(Color.BLACK);
		this.console.setForeground(Color.WHITE);
		this.console.setLineWrap(true);
		this.console.setEnabled(false);
		this.consolePanel = new JScrollPane(this.console);
		this.consolePanel.setSize(this.console.getSize());
		this.consolePanel.setVisible(true);

		// auto scrolling console
		DefaultCaret caret = (DefaultCaret)this.console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		this.mainPanel.add(this.consolePanel, BorderLayout.SOUTH);

		/* ============= TOP PANEL ===================== */
		// panel boutons commande
		this.connectBtn = new JButton("Rejoindre");
		this.logBtn = new JButton("Se connecter");
		this.disconnectBtn = new JButton("Déconnexion");
		this.statsButton = new JButton("Statistiques");

		this.connectBtn.addMouseListener(this.drawListener);
		this.logBtn.addMouseListener(this.drawListener);
		this.disconnectBtn.addMouseListener(this.drawListener);
		this.statsButton.addMouseListener(this.drawListener);

		this.connectBtn.setToolTipText("Connexion anonyme à un serveur");
		this.logBtn.setToolTipText("S'authentifier sur un serveur");
		this.disconnectBtn.setToolTipText("Se déconnecter du serveur");
		this.statsButton.setToolTipText("Voir ses statistiques");

		this.disconnectBtn.setEnabled(false);
		this.statsButton.setEnabled(false);

		this.topPanel.add(this.connectBtn);
		this.topPanel.add(this.logBtn);
		this.topPanel.add(this.disconnectBtn);
		this.topPanel.add(this.statsButton);
		
		this.mainPanel.add(this.topPanel, BorderLayout.NORTH);

		/* ====================================================== */

		// panel affichage des mots
		this.words = new JTextArea(25, 25);
		this.words.setSize(50, this.getHeight()-this.consolePanel.getHeight());
		this.words.setLineWrap(true);
		this.words.setEnabled(false);
		this.wordsViewPanel = new JScrollPane(this.words);

		// auto scrolling chat
		DefaultCaret caretWords = (DefaultCaret)this.words.getCaret();
		caretWords.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		Dimension wordDim = 
				new Dimension(this.getWidth()/4 -10,
						this.getHeight()-this.console.getHeight());
		this.westPanel.setPreferredSize(wordDim);
		
		this.westPanel.add(this.wordsViewPanel);
		
		this.mainPanel.add(this.westPanel, BorderLayout.WEST);



		/* ====================== Panel dessin ================== */
		this.drawPanel = new JPanel();
		System.out.println(this.getHeight());
		System.out.println(this.topPanel.getHeight());
		System.out.println(this.consolePanel.getHeight());
		this.drawPanel.setPreferredSize(new Dimension(650,(this.getHeight() - this.console.getHeight() - 130)));

		this.drawPanel.setBackground(new Color(255, 255, 255));
		//this.drawPanel.setBorder(BorderFactory.createRaisedBevelBorder());HEIGHT
		this.drawPanel.setFocusable(true);

		this.drawPanel.addMouseListener(drawListener);

		this.drawPanel.setName("drawPanel");
		this.centerPanel.add(this.drawPanel);
		this.centerPanel.setPreferredSize(new Dimension(650,650));
		
		this.mainPanel.add(centerPanel);
		/* ====================================================== */

		/* ====================== panel chat ==================== */
		this.chatPanel = new JPanel();
		//this.chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));

		// panel affichage chat
		this.chat = new JTextArea(25, 25);
		this.chat.setSize(100, this.getHeight()-this.consolePanel.getHeight());
		this.chat.setLineWrap(true);
		this.chat.setEnabled(false);
		this.chatViewPanel = new JScrollPane(this.chat);

		// auto scrolling chat
		DefaultCaret caretChat = (DefaultCaret)this.chat.getCaret();
		caretChat.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// panel entrée chat
		this.chatInPanel = new JPanel();
		this.chatInPanel.setPreferredSize(new Dimension(300, 150));
		this.chatIn = new JTextArea(3, 25);
		this.chatIn.setVisible(true);
		this.chatBtn = new JButton("Envoyer");
		this.chatBtn.addMouseListener(this.drawListener);
		this.chatInPanel.add(this.chatIn);
		this.chatInPanel.add(this.chatBtn);	

		this.chatPanel.add(this.chatViewPanel);
		this.chatPanel.add(this.chatInPanel);
		Dimension chatDim = 
				new Dimension(this.getWidth()/4 -10,
						this.getHeight()-this.console.getHeight());
		this.chatPanel.setPreferredSize(chatDim);
		this.mainPanel.add(this.chatPanel, BorderLayout.EAST);
		/* ====================================================== */

		this.setContentPane(this.mainPanel);
		//this.getContentPane().add(this.bottomPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		this.currentGraph = this.drawPanel.getGraphics();
		this.drawPanel.addMouseMotionListener(drawListener);
		
		img = new BufferedImage(this.drawPanel.getWidth(), this.drawPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	public BufferedImage getBufferedImage(){
		return this.img;
	}
	
	public void setBufferedImage(BufferedImage img){
		this.img = img;
	}

	public void reinit() {
		this.console.setText("#### CONSOLE ####\n");
		this.chat.setText("### CHAT ####\n");
	}

	/**
	 * Ecrit un message à caractère informatif dans la console
	 * @param mess le message
	 */
	public void write(String mess){
		this.console.append(mess + "\n");
	}

	public void setCoord(Point p, boolean firstPoint) {
		this.console.append("X: " + p.x + " Y : " + p.y + "\n");
		if(firstPoint){
			this.x1 = p.x;
			this.y1 = p.y;
		}
		else{
			this.x2 = p.x;
			this.y2 = p.y;
			this.console.append("Debut de peinture\n");
			this.paintComponent(currentGraph);
		}
	}

	public void paintComponent(Graphics g){
		//super.paintComponents(g);
		g.drawLine(x1, y1, x2, y2);
		this.console.append(Integer.toString(this.drawPanel.getWidth()) + "\n");
	}

	public void consoleView(String text){
		this.console.append(text + "\n");
	}

	/**
	 * Ouvre le fenêtre de connexion
	 */
	public void openConnectionFrame(){
		this.connectDial = new ConnectDialog(this, drawListener);
		this.connectDial.setVisible(true);
	}

	public String readConnectionInfoFromUser() {
		String infos = "";
		infos += this.readNameFromUser(this.connectDial.getNameTxt());
		infos += '#';
		infos += this.readHostFromUser(this.connectDial.getHostTxt());
		infos += '#';
		infos += this.readPortFromUser(this.connectDial.getPortTxt());
		return infos;
	}

	private String readPortFromUser(JTextField field) {
		String regex = "\\d{1,5}";
		String sInput = field.getText();

		if (sInput.matches(regex) || sInput.isEmpty())
			field.setBackground(Color.WHITE);
		else
			field.setBackground(Color.RED);

		if(sInput.isEmpty())
			sInput = "2012";

		field.repaint();
		return sInput;
	}

	private String readHostFromUser(JTextField field) {
		String regex = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
		String sInput = field.getText();

		if (sInput.matches(regex) || sInput.isEmpty())
			field.setBackground(Color.WHITE);
		else
			field.setBackground(Color.RED);

		field.repaint();
		return sInput;
	}

	private String readNameFromUser(JTextField field) {
		String sInput = field.getText();

		if(sInput.isEmpty())
			field.setBackground(Color.RED);
		else
			field.setBackground(Color.WHITE);

		field.repaint();
		return sInput;
	}

	/**
	 * Retourne le fenêtre de connexion de la vue
	 * @return la fenêtre de connection
	 */
	public ConnectDialog getConnectDial() {
		return connectDial;
	}

	/**
	 * Renvoie le bouton de connexion anonyme
	 * @return le bouton de connexion
	 */
	public JButton getConnectBtn() {
		return connectBtn;
	}

	/**
	 * Renvoie le bouton d'authetification au serveur
	 * @return le bouton d'authetification
	 */
	public JButton getLogBtn() {
		return logBtn;
	}

	/**
	 * Renvoie le bouton de déconnexion du serveur
	 * @return le bouton de déconnexion
	 */
	public JButton getDisconnectBtn() {
		return disconnectBtn;
	}

	/**
	 * Ferme la fenêtre de connexion et réinitialise son affichage
	 */
	public void closeConnectionFrame(){
		this.connectDial.dispose();
	}

	/**
	 * Affiche une fenêtre d'information si le serveur se déconnecte
	 */
	public void notResponding() {
		JOptionPane.showMessageDialog(this,
				"Le serveur ne répond pas.\nVous allez être déconnecté.",
				"Erreur du serveur",
				JOptionPane.WARNING_MESSAGE);
		disconnect();
	}

	public void disconnect(){
		this.logBtn.setEnabled(true);
		this.connectBtn.setEnabled(true);
		this.disconnectBtn.setEnabled(false);
		this.statsButton.setEnabled(false);

		if(this.connectDial != null)
			this.connectDial.dispose();
	}
}
