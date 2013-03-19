package client;

import java.util.Map;
import model.Blade;
import model.Cloud;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * @author Apostolos Giannakidis
 */

public class Main {
    
	private static final Logger LOG = Logger.getLogger(Main.class.getCanonicalName());
	private static Cloud cloud = null;
	private static Client client = null;
	
	public static void main(String[] args) {
			
		setupLogger(args);
		
		setupServer();
		
		setupCloud();
		
		client.sendToServer(cloud);
		/*
		Gson gson = new Gson();
		
		String s = gson.toJson(cloud);
		
		LOG.info(s);
		
		Cloud cloud2 = gson.fromJson(s, Cloud.class)*/
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
	
	private static void setupServer(){
		client = Client.getInstance();
	}

	private static void setupCloud(){
		cloud = new Cloud();
		
		/***** Simulation FIXME ******/
		long id = 1;
		double memory_total = 100;
		double memoryUsage_current_MB = 50;
		double disk_total_GB = 100;
		double diskUsage_current_GB = 50;
		double networkBandwidthUsed_KBs = 100;
		boolean on = true;
		double maximumNetworkBandwidth_KBs = 1000;
		
		Blade blade = new Blade(id, memory_total,
				memoryUsage_current_MB,
				disk_total_GB,
				diskUsage_current_GB,
				networkBandwidthUsed_KBs,
				on,
				maximumNetworkBandwidth_KBs);
		
		cloud.setBlade(id, blade);
		/****************************/
	}
	
	public static Level getLogLevel() {
		return LOG.getLevel();
	}
}
