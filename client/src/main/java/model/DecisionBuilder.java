package model;

/**
 * Decision Builder
 *
 * 20 March 2013
 * @author Apostolos Giannakidis
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DecisionBuilder {
    private JSONArray commandFromServer;
    private static DecisionBuilder _instance = new DecisionBuilder();
    
    private DecisionBuilder() {
        this.commandFromServer = new JSONArray();
    }
    
    public static DecisionBuilder getInstance(){
    	return _instance;
    }

    public void makeDecision(JSONArray json){
    	commandFromServer = json;
    	
    }


    public void shutdownBlade(Blade blade) {
        try {
            JSONObject node = new JSONObject();
            node.put("action", Decisions.ShutDownBlade);
            node.put("BladeID", blade.getID());
            commandFromServer.put(node);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void tryOpenNewBlade() {
        try {
            JSONObject node = new JSONObject();
            node.put("action", Decisions.OpenNewBlade);
            commandFromServer.put(node);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void moveVm2Blade(VM vm, Blade toBlade, Blade fromBlade) {
        try {
            JSONObject node = new JSONObject();
            node.put("action", Decisions.Move);
            node.put("VmID", vm.getID());
            node.put("FromBladeID", fromBlade.getID());
            node.put("ToBladeID", toBlade.getID());
            commandFromServer.put(node);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCommandToClient(JSONArray commandToClient) {
        this.commandFromServer = commandToClient;
    }

    public JSONArray getCommandToClient() {
        return commandFromServer;
    }
}
