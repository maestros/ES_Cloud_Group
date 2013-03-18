package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private int PORT = 9070;
	private String HOST = "localhost";
    private Socket socket = null;
    private BufferedReader in;
    private BufferedWriter out;
    private static Client _instance = null;
    
    private Client(){
		try {
			socket = new Socket(HOST,PORT);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public static Client getInstance(){
    	if (_instance==null)
    		_instance = new Client();
    	
    	return _instance;
    }
    
    public void sendToServer(String s){
    	try {
			out.write(s);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
