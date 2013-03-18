package client;

/**
 * @author Apostolos Giannakidis
 */

public class Main {
    
	public static void main(String[] args) {
		System.out.println("Started.");
		
		Client client = Client.getInstance();
		
		client.sendToServer("test");
		
	}

}
