package client;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.Cloud;

import org.apache.log4j.*;

import com.google.gson.Gson;

public class CloudClient {
	
	private int PORT = 2111;
	private String HOST = "localhost";
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private static CloudClient _CloudClientinstance;
	private static final Logger LOG = Logger.getLogger(CloudClient.class.getCanonicalName());
	private static CloudState cloudState;
	private Gson gson = null;
    private static final int EXECUTOR_DELAY = 2000;//*60*10;	//CONSTANT, 10 minutes
    private static ScheduledExecutorService executor;	//the reference for the Game State thread executor
    
	{
		LOG.setLevel(Main.getLogLevel());
	}
	
    private CloudClient(){
    	initialiseClient();
    	LOG.info("Client started...");
    }
    
    private void initialiseClient(){
		try {
			socket = new Socket(HOST,PORT);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			LOG.error("Host: "+HOST+" is unknown. Terminating.");
			System.exit(1);
		} catch(ConnectException e){
			LOG.error("Host: "+HOST+":"+PORT+" refused connection. Terminating.");
			System.exit(1);
		} catch (IOException e){
			e.printStackTrace();
		}
		gson = new Gson();
		cloudState = CloudState.getInstance();
		
    	executor = Executors.newSingleThreadScheduledExecutor(); //The singleton instance of the executor thread
        // schedule the executor Thread to be executed every EXECUTOR_DELAY milliseconds
       
        Runnable updateCloudState = new Runnable() {	//The thread that updates Cloud state and sends it to the Drools server
            public void run() {
            	try {
            		cloudState.updateState();	//when runs it updates the Cloud state
            		sendToServer(cloudState.getCloud());
            	}
            	catch(Exception e){
            		LOG.error("Error executing: updateGameStateTask. It will no longer run!");
                    e.printStackTrace();
                    throw new RuntimeException(e);
            	}
            }
        };
        
    	executor.scheduleAtFixedRate(updateCloudState, 0, EXECUTOR_DELAY, TimeUnit.MILLISECONDS);
    }
    
    public static CloudClient getInstance(){
    	if (_CloudClientinstance==null)
    		_CloudClientinstance = new CloudClient();
    	
    	return _CloudClientinstance;
    }
    
    public void sendToServer(Cloud cloud){
    	String s = null;
    	try {
    		if (socket.isConnected()){
    			s = gson.toJson(cloud);
    			out.write(s);
    			out.newLine();
    			out.flush();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
