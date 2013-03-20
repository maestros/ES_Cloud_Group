package model;

/**
 * Decision Builder
 *
 * 20 March 2013
 * @author Apostolos Giannakidis
 */
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import client.CloudClient;
import client.IllegalMachineStateException;
import client.MachineMonitor;
import client.Main;

public class DecisionBuilder {
    private static DecisionBuilder _instance = new DecisionBuilder();
    private static Logger LOG = Logger.getLogger(DecisionBuilder.class.getCanonicalName());
    private static MachineMonitor vmMonitor = MachineMonitor.getInstance();
    private static CloudClient cloudclient = CloudClient.getInstance();
    
	{
		LOG.setLevel(Main.getLogLevel());
	}
	
    private DecisionBuilder() {
    }
    
    public static DecisionBuilder getInstance(){
    	return _instance;
    }

    public void makeDecision(JSONArray jsonArray){
    	Decisions decision = null;
    	int bladeId;
    	int vmID;
    	int toId;
    	
	    try {
	    	cloudclient.setActive(true);
	    	for (int i = 0; i < jsonArray.length(); ++i) {
    	    	JSONObject rec = jsonArray.getJSONObject(i);
				decision = Decisions.valueOf(rec.getString("action"));
				bladeId = Integer.parseInt(rec.getString("BladeID"));
				
				switch(decision){
					case Move:
						vmID = Integer.parseInt(rec.getString("VmID"));
						toId = Integer.parseInt(rec.getString("ToBladeID"));
						try {
							vmMonitor.migrateVM(vmID, toId, true);
						} catch (IllegalMachineStateException e) {
							LOG.error("Move VM failed.");
							e.printStackTrace();
						}
						break;
					case OpenNewBlade:
						try {
							vmMonitor.enableHost(bladeId);
						} catch (IllegalMachineStateException e) {
							LOG.error("EnableHost failed.");
							e.printStackTrace();
						}
						break;
					case ShutDownBlade:
						try {
							vmMonitor.disableHost(bladeId);
						} catch (IllegalMachineStateException e) {
							LOG.error("DisableHost failed.");
							e.printStackTrace();
						}
						break;
					default:
						LOG.error("Unknown action");
						break;
				}
	    	}
	    	cloudclient.setActive(false);
		} catch (JSONException e) {
			e.printStackTrace();
		}   
    }

}
