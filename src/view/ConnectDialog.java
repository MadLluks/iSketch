package view;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Fenêtre de paramètrage de connexion au serveur
 * @author Alexandre CANTAIS
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ConnectDialog extends JDialog {

	private JPanel mainPanel;
	private JLabel nameLbl;
	private JLabel hostLbl;
	private JLabel portLbl;
	private JTextField nameTxt;
	private JTextField hostTxt;
	private JTextField portTxt;
	private JButton enterBtn;
	private JButton cancelBtn;
	
	/**
	 * Constructeur, génère l'affichage de la fenêtre
	 * @param frame la frame à laquelle la fenêtre est rattachée
	 * @param btlListener l'observateur d'événements
	 */
	public ConnectDialog(JFrame frame, DrawListener listener){
		super(frame);
		this.setTitle("Connexion");
		this.mainPanel = new JPanel(new GridLayout(4, 2));
		
		this.nameLbl = new JLabel("Nom");
		this.hostLbl = new JLabel("Addresse");
		this.portLbl = new JLabel("Port");
		
		this.nameTxt = new JTextField("Player");
		this.hostTxt = new JTextField("127.0.0.1");
		this.portTxt = new JTextField("2013");
		
		this.enterBtn = new JButton("Entrer");
		this.enterBtn.addMouseListener(listener);
		this.cancelBtn = new JButton("Annuler");
		this.cancelBtn.addMouseListener(listener);
		
		this.mainPanel.add(this.nameLbl);
		this.mainPanel.add(this.nameTxt);
		this.mainPanel.add(this.hostLbl);
		this.mainPanel.add(this.hostTxt);
		this.mainPanel.add(this.portLbl);
		this.mainPanel.add(this.portTxt);
		this.mainPanel.add(this.enterBtn);
		this.mainPanel.add(this.cancelBtn);
		
		this.mainPanel.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);	
		this.setContentPane(this.mainPanel);
		this.pack();
		this.setLocationRelativeTo(frame);
	}

	/**
	 * Retourne la zone de texte correspondant au nom
	 * @return zone de texte du nom
	 */
	public JTextField getNameTxt() {
		return nameTxt;
	}

	/**
	 * Retourne la zone de texte correspondant à l'adresse du serveur
	 * @return zone de texte de l'adresse
	 */
	public JTextField getHostTxt() {
		return hostTxt;
	}

	/**
	 * Retourne la zone de texte correspondant au port de connexion du serveur
	 * @return zone de texte du port
	 */
	public JTextField getPortTxt() {
		return portTxt;
	}

}
