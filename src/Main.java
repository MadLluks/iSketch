import controler.Client;

public class Main {
	public static void main(String[] args) {
		Client client =  new Client();
		Thread tClient = new Thread(client);
		
		tClient.start();
		try {
			tClient.join();
		} catch (InterruptedException e) {e.printStackTrace();}
	}
}
