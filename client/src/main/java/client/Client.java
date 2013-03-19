package client;

import java.io.*;
import java.net.*;

import model.Cloud;

import org.apache.log4j.*;

import com.google.gson.Gson;

public class Client {
	
	private int PORT = 2111;
	private String HOST = "localhost";
    private Socket socket = null;
    private BufferedReader in;
    private BufferedWriter out;
    private static Client _instance = null;
	private static final Logger LOG = Logger.getLogger(Client.class.getCanonicalName());
	private Gson gson = null;
	
	{
		LOG.setLevel(Main.getLogLevel());
	}
	
    private Client(){
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
    }
    
    public static Client getInstance(){
    	if (_instance==null)
    		_instance = new Client();
    	
    	return _instance;
    }
    
    public void sendToServer(Cloud cloud){
    	String s = null;
    	try {
    		if (socket.isConnected()){
    			s = gson.toJson(cloud);
    			out.write(s);
    			out.flush();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
