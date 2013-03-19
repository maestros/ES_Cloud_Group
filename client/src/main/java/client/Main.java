package client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Apostolos Giannakidis
 */

public class Main {
    
	private static final Logger LOG = Logger.getLogger(Main.class.getCanonicalName());

	public static void main(String[] args) {
			
		setupLogger(args);
		
		Client client = Client.getInstance();
		
		client.sendToServer("test");
	}
	
	private static void setupLogger(String[] args){
		BasicConfigurator.configure();
		if(args.length==2)
			LOG.setLevel(Level.toLevel(args[1]));
		else
			LOG.setLevel(Level.INFO);
		LOG.info("Client initialising...");
		LOG.info("Log level is set to: "+LOG.getLevel());
	}

	public static Level getLogLevel() {
		return LOG.getLevel();
	}
}
