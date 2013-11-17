package controler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import view.GraphicalView;

/**
 * Gère toute la partie client qui traitera avec le serveur
 * et servira de liaison avec la vue.
 * @author Alexandre CANTAIS
 * @version 1.0
 */
public class Client extends Thread {
	private final boolean DEBUG = true;

	// Connexion
	private int port = 2013;
	private InetSocketAddress socket;
	private InetAddress serverAddress;
	private SocketChannel channel;
	private boolean connected;

	// Communication
	private final CharsetEncoder enc;
	private	ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	private String inMessage;
	private String outMessage;

	private GraphicalView view;
	private boolean running;
	private String playerName;

	public Client(){
		// la vue
		this.view = new GraphicalView(this);

		// buffers de communication
		this.enc = Charset.forName("UTF-8").newEncoder();
		try {
			this.writeBuffer = this.enc.encode(CharBuffer.wrap(""));
		} catch (CharacterCodingException e) {e.printStackTrace();}
		this.readBuffer = ByteBuffer.allocate(2048);
	}
	
	private void init() {
		this.playerName = "";
		this.port = 2012;
		this.view.reinit();
		this.inMessage = "";
	}

	public void openChannel(){
		// Création de la socket
		this.socket = new InetSocketAddress(this.port);

		// Ouverture du canal sur la socket
		SocketChannel sc = null;
		try{
			sc = SocketChannel.open();
			sc.connect(this.socket);
		} catch (IOException e){
			this.view.error("Erreur d'ouverture de la socket");
		}

		if(DEBUG)
			this.view.write("Socket \'" + sc.toString() +"\'");

		this.channel = sc;
	}

	/**
	 * Envoie un message au serveur. Ce message est contenu dans outMessage
	 */
	private void send(){
		try{
			this.writeBuffer.clear();

			this.view.write("Client: '" + this.outMessage + "'");

			// envoi du message avec ajout d'un '\n' à la fin (protocol)
			this.writeBuffer = 
					this.enc.encode(CharBuffer.wrap(this.outMessage + "\n"));

			// si aucun charactère n'est envoyé -> erreur
			if(this.channel.write(this.writeBuffer) == 0){
				this.view.error("Erreur d'envoi: '" + this.outMessage + "'");
			}
		}catch (IOException e){
			this.view.error("Erreur d'écriture dans le canal");
		}
		if(DEBUG)
			this.view.write("Message envoyé: '" + this.outMessage + "'");

		this.writeBuffer.clear();
	}

	/**
	 * Reçoit un message du serveur. Le message lu est enregistré dans inMessage
	 */
	private void read(){
		this.inMessage = "";
		try{
			this.readBuffer.clear();

			// lecture dans le buffer
			this.channel.read(this.readBuffer);
			this.readBuffer.flip();

			// contruction de la chaîne de réponse à partir du buffer (bytes)
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i<this.readBuffer.limit(); i++){
				byte[] byteArr = new byte[readBuffer.remaining()];
				readBuffer.get(byteArr);
				sb.append(new String(byteArr));
			}

			if(DEBUG)
				this.view.write("Message lu: '" + sb + "'");

			// suppression du characater '\n' si à la fin du message
			if(sb.toString().endsWith("/\n"))
				sb.delete(sb.length()-2, sb.length());

			this.inMessage = sb.toString();

		}catch (IOException e){
			this.view.error("Erreur de lecture dans le canal");
		}
		if(DEBUG)
			this.view.write("Message enregistré: '" + this.inMessage + "'");

		this.readBuffer.clear();
	}

	/**
	 * Moteur principal du client. Initialise les données, attend une demande
	 * de connexion pour se connecter et attend les instructions du serveur
	 */
	public void run(){
		this.running = true;

		while(this.running){
			this.init();
			waitForConnection();
			this.connected = connect();
			int timeOut = 0;

			while(/*this.connected*/true){
				read();
				// le canal est rompu (réception de messages vides en boucle)
				if(this.inMessage.isEmpty()){
					timeOut++;
					try {
						Thread.sleep(1000); // on attend une seconde
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				// le serveur a envoyé un message non vide
				else{
					treatInMessage();
					timeOut = 0;
				}
			}
		}
	}
	
	private boolean connect() {
		int i = 0;

		send(); // on envoie le message de connexion

		try {
			while(this.inMessage == "" && i < 5){
				read();
				Thread.sleep(1000);
				i++;
			}
			if(i == 5){
				this.view.notResponding();
				return false;
			}
			else if(this.inMessage.contains("ACCESSDENIED")){
				this.view.error("Erreur: Le serveur a refusé la connexion");
				return false;
			}
			else if(!this.inMessage.contains("WELCOME")){
				this.view.error("Erreur: réponse incorrecte du serveur");
				return false;
			}
		}catch (InterruptedException e) {e.printStackTrace();}
		this.view.write("Serveur: '" + this.inMessage + "'");

		String splitString[] = this.inMessage.split("/");
		this.playerName = splitString[1];
		this.view.setTitle("(" + this.playerName + ") " + this.view.getTitle());

		return true;
	}

	/**
	 * Décide de l'action à mener en fonction du message reçu dans inMessage
	 */
	private void treatInMessage() {
		System.out.println(this.inMessage);
	}
	
	/**
	 * Met le client en attente le temps que le joueur demande à ouvrir
	 * une connexion avec le serveur
	 */
	synchronized private void waitForConnection() {
		try {
			wait();
		} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	/**
	 * Créé une nouvelle connexion avec le server selon les informations transmises
	 * @param infos les informations nécessaires à la connexion (nom, adresse, port)
	 */
	synchronized public void newConnection(String infos) {
		String[] infoTab = infos.split("#");

		// nom du joueur
		this.playerName = infoTab[0];
		this.outMessage = "CONNECT/" + this.playerName +"/";

		// adresse de l'hôte
		String hostName = infoTab[1];
		InetAddress addr = null;
		try {
			if (hostName.isEmpty()){ // on choisit l'adresse par défaut
				addr = InetAddress.getLocalHost();
			}
			else{
				addr = InetAddress.getByName(hostName);
			}

		}catch (UnknownHostException e) {
			this.view.error("Erreur: l'hôte est injoignable");
			e.printStackTrace();
		}
		this.serverAddress = addr;

		// port de connexion à l'hôte
		this.port = Integer.valueOf(infoTab[2]);	

		if(DEBUG)
			this.view.write(this.playerName + "/" + 
					this.serverAddress.toString() + "/" +
					String.valueOf(this.port));

		// ouverture de canal
		openChannel();

		// réveille du client pour continuer le traitement
		notifyAll();
	}
}