package client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import client.MachineMonitor.MachineState;
import model.Blade;
import model.Cloud;

/**
 * CloudState
 *
 * 18 March 2013
 * @author Apostolos Giannakidis
 */

public class CloudState {
	
	private static CloudState _instance;
	private static MachineMonitor vmMonitor;
	private static Cloud cloud;
	private static final String secret = "oneadmin:password";
	private static final String target = "http://localhost:2633/RPC2";
	private static Logger LOG;
	private List<MachineState> vmStates;
	private List<MachineState> hostStates;
	
	{
		LOG = Logger.getLogger(CloudState.class.getCanonicalName());
		LOG.setLevel(Main.getLogLevel());
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private CloudState(){
		cloud = new Cloud();
		vmMonitor = MachineMonitor.VMMonitorFactory(secret, target);
	}
	
	public static CloudState getInstance(){
		if (_instance==null)
			_instance = new CloudState();
		return _instance;
	}

	private void updateVMStates(){
		try {
			vmStates = vmMonitor.getVMStates();
		} catch (IllegalMachineStateException e) {
			e.printStackTrace();
		}
	}
	
	public void printVMStates(){
		System.out.println("\nVMS:\n");
		updateVMStates();
		for (MachineState ms : vmStates) {
			System.out.println(ms);
		}
	}
	
	private void updateHostStates(){
		try {
			hostStates = vmMonitor.getHostStates();
		} catch (IllegalMachineStateException e) {
			e.printStackTrace();
		}
	}
	
	public void printHostStates(){
		System.out.println("\nHOSTS:\n");
		updateHostStates();
		for (MachineState ms : hostStates) {
			System.out.println(ms);
		}
	}
	
	
	public void updateState(){
		LOG.info("Cloud State updated");
		_instance.updateHostStates();
		_instance.updateVMStates();
		
		// Update Blade States
		cloud.updateBlades(hostStates, vmStates);
	}
	
	public void setBlade(Long id, Blade blade){
		cloud.setBlade(id, blade);
	}
	
	public Cloud getCloud(){
		return cloud;
	}
}
