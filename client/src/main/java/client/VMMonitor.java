package client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opennebula.client.vm.VirtualMachine;

public class VMMonitor {
	
	public static void main(String[] args) {
//		VMMonitor vmm = VMMonitor.VMMonitorFactory("oneadmin:password", "http://147.188.195.213:2633/RPC2");
		VMMonitor vmm = VMMonitor.VMMonitorFactory("oneadmin:password", "http://localhost:2633/RPC2");

	}
	
	private OpenNebulaConnection conn;
	
	private VMMonitor(OpenNebulaConnection conn) {
		this.conn = conn;
		
		//System.out.println(conn.getAllVMs().next().migrate(25).getMessage());
//		System.out.println(conn.getAllVMs().next().monitoring().getMessage());
		System.out.println(conn.getVM(115).monitoring().getMessage());
		System.out.println(conn.getVM(115).monitoring().getErrorMessage());
	}
	
	/**
	 * Returns a new VMMonitor object.
	 * 
	 * @param secret
	 * @param target
	 * @return
	 */
	public static VMMonitor VMMonitorFactory(String secret, String target) {
		OpenNebulaConnection conn = OpenNebulaConnection.openNebulaConnectionFactory(secret, target);
		return new VMMonitor(conn);
	}
	
	public Object parseOneResponseXML(String attr) {
		return null;
	}
	
	public double getCPUUsage(int vmID) {
		return 0;
	}
	
	public List<VMState> getCPUUsage() {
		ArrayList<VMState> vmStates = new ArrayList<VMState>();
		
		Iterator<VirtualMachine> it = conn.getAllVMs();
		VirtualMachine tmp;
		
		while (it.hasNext()) {
			tmp = it.next();
		}
		
		return vmStates;
	}
	
	public double getRAMUsage(int vmID) {
		return 0;
	}
	
	public double getHDUsage(int vmID) {
		return 0;
	}
	
	/**
	 * returns a new VMstat representation.
	 * 
	 * @param ID
	 * @param cpuUsage
	 * @param ramUsage
	 * @param hdUsage
	 * @return
	 */
	public VMState newVMState(int ID, double cpuUsage, double ramUsage, double hdUsage) {
		return new VMState(ID, cpuUsage, ramUsage, hdUsage);
	}
	
	/**
	 * Represents the state of a VM.
	 * 
	 * @author darer121
	 *
	 */
	class VMState {
		public final int ID;
		public final double cpuUsage;
		public final double ramUsage;
		public final double hdUsage;
		
		private VMState(final int ID, final double cpuUsage, final double ramUsage, final double hdUsage) {
			this.ID = ID;
			this.cpuUsage = cpuUsage;
			this.ramUsage = ramUsage;
			this.hdUsage = hdUsage;
		}
	}

}
