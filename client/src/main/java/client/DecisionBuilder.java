package client;

/**
 * Decision Builder
 *
 * 20 March 2013
 * @author Apostolos Giannakidis
 */
import model.Decisions;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DecisionBuilder {
    private static DecisionBuilder _instance = new DecisionBuilder();
    private static Logger LOG;
    private static MachineMonitor vmMonitor = MachineMonitor.getInstance();
    
	{
		LOG = Logger.getLogger(DecisionBuilder.class.getCanonicalName());
		LOG.setLevel(Main.getLogLevel());
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
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
	    	CloudClient.setActive(true);
	    	LOG.info("Active = true");
	    	for (int i = 0; i < jsonArray.length(); ++i) {
    	    	JSONObject rec = jsonArray.getJSONObject(i);
				decision = Decisions.valueOf(rec.getString("action"));
				
				switch(decision){
					case Move:
						vmID = Integer.parseInt(rec.getString("VmID"));
						toId = Integer.parseInt(rec.getString("ToBladeID"));
						try {
							vmMonitor.enableHost();
							vmMonitor.migrateVM(vmID, toId, true);
						} catch (IllegalMachineStateException e) {
							LOG.error("Move VM failed.");
							e.printStackTrace();
						}
						
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						break;
					case OpenNewBlade:
						try {
							vmMonitor.enableHost();
						} catch (IllegalMachineStateException e) {
							LOG.error("EnableHost failed.");
							e.printStackTrace();
						}
						break;
					case ShutdownBlade:
						try {
							bladeId = Integer.parseInt(rec.getString("BladeID"));
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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally{
			CloudClient.setActive(false);
			LOG.info("Active = false");
		}
    }

}
