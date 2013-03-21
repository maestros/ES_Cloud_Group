package client;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Main
 *
 * 15 March 2013
 * @author Apostolos Giannakidis
 */

public class Main {
    
	private static final Logger LOG = Logger.getLogger(Main.class.getCanonicalName());
	private static CloudClient cloudClient = null;
	
	public static void main(String[] args) {
		setupLogger(args);
		
		setupCloudClient();
	}
	
	private static void setupLogger(String[] args){
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		
		if(args.length==2)
			LOG.setLevel(Level.toLevel(args[1]));
		else
			LOG.setLevel(Level.INFO);
		LOG.info("Client initialising...");
		LOG.info("Log level is set to: "+LOG.getLevel());
	}
	
	private static void setupCloudClient(){
		cloudClient = CloudClient.getInstance();
	}
	
	public static Level getLogLevel() {
		return LOG.getLevel();
	}
}
