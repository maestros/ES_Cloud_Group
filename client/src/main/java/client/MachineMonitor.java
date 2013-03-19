package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;

public class MachineMonitor {
	
	public static void main(String[] args) {
//		VMMonitor vmm = VMMonitor.VMMonitorFactory("oneadmin:password", "http://147.188.195.213:2633/RPC2");
		MachineMonitor vmm = MachineMonitor.VMMonitorFactory("oneadmin:password", "http://localhost:2633/RPC2");

	}
	
	private static final String CPU_USAGE = "CPU";
	private static final String RAM_USAGE = "RAM";
	private static final String HD_USAGE = "HD";
	
	private OpenNebulaConnection conn;
	
	private MachineMonitor(OpenNebulaConnection conn) {
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
	public static MachineMonitor VMMonitorFactory(String secret, String target) {
		OpenNebulaConnection conn = OpenNebulaConnection.openNebulaConnectionFactory(secret, target);
		return new MachineMonitor(conn);
	}
	
	/**
	 * Returns the CPU usage of the specified VM.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getVMCPUUsage(int vmID) throws IllegalMachineStateException{
		VirtualMachine vm = conn.getVM(vmID);
		OneResponse res = vm.monitoring();
		
		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from VM: " + res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser.parseXMLString(res.getMessage());
			HashMap<String, String> vmState = parsedXML.get(vmID);
			if (vmState == null) {
				throw new IllegalMachineStateException("VM: " + vmID +" DOES NOT EXIST");
			} else {
				return Double.parseDouble(vmState.get(CPU_USAGE));
			}
		}
	}
	
	/**
	 * Returns the CPU usage of all VMs.
	 * 
	 * @return
	 */
	public List<MachineState> getVMCPUUsage() {
		ArrayList<MachineState> vmStates = new ArrayList<MachineState>();
		
		Iterator<VirtualMachine> it = conn.getAllVMs();
		VirtualMachine tmp;
		
		while (it.hasNext()) {
			tmp = it.next();
		}
		
		return vmStates;
	}
	
	/**
	 * Returns the RAM usage of the specified VM.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getVMRAMUsage(int vmID) throws IllegalMachineStateException {
		VirtualMachine vm = conn.getVM(vmID);
		OneResponse res = vm.monitoring();
		
		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from VM: " + res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser.parseXMLString(res.getMessage());
			HashMap<String, String> vmState = parsedXML.get(vmID);
			if (vmState == null) {
				throw new IllegalMachineStateException("VM: " + vmID +" DOES NOT EXIST");
			} else {
				return Double.parseDouble(vmState.get(RAM_USAGE));
			}
		}
	}
	
	/**
	 * Returns the HD usage of the specified VM.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getVMHDUsage(int vmID) throws IllegalMachineStateException{
		VirtualMachine vm = conn.getVM(vmID);
		OneResponse res = vm.monitoring();
		
		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from VM: " + res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser.parseXMLString(res.getMessage());
			HashMap<String, String> vmState = parsedXML.get(vmID);
			if (vmState == null) {
				throw new IllegalMachineStateException("VM: " + vmID +" DOES NOT EXIST");
			} else {
				return Double.parseDouble(vmState.get(HD_USAGE));
			}
		}
	}
	
	/**
	 * Returns the CPU usage of the specified Host.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getHostCPUUsage(int vmID) {
		return 0;
	}
	
	/**
	 * Returns the CPU usage of all hosts.
	 * 
	 * @return
	 */
	public List<MachineState> getHostCPUUsage() {
		ArrayList<MachineState> vmStates = new ArrayList<MachineState>();
		
		Iterator<VirtualMachine> it = conn.getAllVMs();
		VirtualMachine tmp;
		
		while (it.hasNext()) {
			tmp = it.next();
		}
		
		return vmStates;
	}
	
	/**
	 * Returns the RAM usage of the specified Host.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getHostRAMUsage(int vmID) {
		return 0;
	}
	
	/**
	 * Returns the HD usage of the specified Host.
	 * 
	 * @param vmID
	 * @return
	 */
	public double getHostHDUsage(int vmID) {
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
	public MachineState newVMState(int ID, double cpuUsage, double ramUsage, double hdUsage) {
		return new MachineState(ID, cpuUsage, ramUsage, hdUsage);
	}
	
	/**
	 * Represents the state of a VM.
	 * 
	 * @author darer121
	 *
	 */
	class MachineState {
		public final int ID;
		public final double cpuUsage;
		public final double ramUsage;
		public final double hdUsage;
		
		private MachineState(final int ID, final double cpuUsage, final double ramUsage, final double hdUsage) {
			this.ID = ID;
			this.cpuUsage = cpuUsage;
			this.ramUsage = ramUsage;
			this.hdUsage = hdUsage;
		}
	}

}
