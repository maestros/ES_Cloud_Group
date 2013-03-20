package client;

import java.util.List;

import org.apache.log4j.Logger;

import client.MachineMonitor.MachineState;
import model.Blade;
import model.Cloud;

public class CloudState {
	
	private static CloudState _instance = new CloudState();
	private static MachineMonitor vmMonitor;
	private static Cloud cloud;
	private static final String secret = "oneadmin:password";
	private static final String target = "http://localhost:2633/RPC2";
	private static Logger LOG;
	private List<MachineState> vmStates;
	private List<MachineState> hostStates;
	
	private List<Blade> blades;
	
	{
		LOG = Logger.getLogger(CloudState.class.getCanonicalName());
		LOG.setLevel(Main.getLogLevel());
	}
	
	private CloudState(){
		cloud = new Cloud();
		vmMonitor = MachineMonitor.VMMonitorFactory(secret, target);
		
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
	
	public static CloudState getInstance(){
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
		
	}
	
	public void setBlade(Long id, Blade blade){
		cloud.setBlade(id, blade);
	}
	
	public Cloud getCloud(){
		return cloud;
	}
}
