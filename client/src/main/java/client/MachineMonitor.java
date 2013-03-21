package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.DecisionBuilder;

import org.apache.log4j.Logger;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.vm.VirtualMachine;

/**
 * MachineMonitor
 * 
 * 18 March 2013
 * 
 * @author Alex Darer
 */

public class MachineMonitor {

	private static MachineMonitor _MMinstance;
	private OpenNebulaConnection conn;

	private static final String VM_HOST_ID = "HID";
	private static final String VM_CPU_USAGE = "CPU";
	private static final String VM_RAM_USAGE = "MEMORY";
	private static final String VM_HD_USAGE = "DS_ID";
	private static final String HOST_CPU_USAGE = "USED_CPU";
	private static final String HOST_RAM_USAGE = "USED_MEM";
	private static final String HOST_HD_USAGE = "USED_DISK";
	private static final String HOST_CPU_FREE = "FREE_CPU";
	private static final String HOST_RAM_FREE = "FREE_MEM";
	private static final String HOST_HD_FREE = "FREE_DISK";
	private static final String MAX_CPU = "MAX_CPU";
	private static final String MAX_RAM = "MAX_MEM";
	private static final String MAX_HD = "MAX_DISK";

	private static Logger LOG;

	{
		LOG = Logger.getLogger(MachineMonitor.class.getCanonicalName());
		LOG.setLevel(Main.getLogLevel());
	}

	public static void main(String[] args) {
		// VMMonitor vmm = VMMonitor.VMMonitorFactory("oneadmin:password",
		// "http://147.188.195.213:2633/RPC2");
		MachineMonitor vmm = MachineMonitor.VMMonitorFactory(
				"oneadmin:password", "http://localhost:2633/RPC2");

		OpenNebulaConnection conn = OpenNebulaConnection
				.openNebulaConnectionFactory("oneadmin:password",
						"http://localhost:2633/RPC2");
		System.out.println(conn.getAllVMStates().getMessage());

		try {
			System.out.println("\nVMS:\n");
			List<MachineState> vmStates = vmm.getVMStates();
			for (MachineState ms : vmStates) {
				System.out.println(ms);
			}
		} catch (IllegalMachineStateException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("\nHOSTS:\n");
			List<MachineState> hostStates = vmm.getHostStates();
			for (MachineState ms : hostStates) {
				System.out.println(ms);
			}
		} catch (IllegalMachineStateException e) {
			e.printStackTrace();
		}
	}

	private MachineMonitor(OpenNebulaConnection conn) {
		this.conn = conn;
	}

	/**
	 * Returns a new VMMonitor object.
	 * 
	 * @param secret
	 * @param target
	 * @return
	 */
	public static MachineMonitor VMMonitorFactory(String secret, String target) {
		OpenNebulaConnection conn = OpenNebulaConnection
				.openNebulaConnectionFactory(secret, target);

		if (_MMinstance == null)
			_MMinstance = new MachineMonitor(conn);
		return _MMinstance;
	}

	public static MachineMonitor getInstance() {
		return _MMinstance;
	}

	public boolean migrateVM(int vmID, int newHostID, boolean live)
			throws IllegalMachineStateException {
		OneResponse res = conn.migrateVM(vmID, newHostID, live);

		if (res.isError()) {
			throw new IllegalMachineStateException("VM failed to migrate: "
					+ res.getErrorMessage());
		}

		return true;
	}

	/**
	 * Returns the resource usage of all VMs.
	 * 
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public List<MachineState> getVMStates() throws IllegalMachineStateException {
		ArrayList<MachineState> vmStates = new ArrayList<MachineState>();

		OneResponse res = conn.getAllVMStates();

		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from VM: "
					+ res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
					.parseXMLString(res.getMessage());

			HashMap<String, String> vmState = null;

			for (int key : parsedXML.keySet()) {
				vmState = parsedXML.get(key);
				if (vmState == null) {
					throw new IllegalMachineStateException("VM: " + key
							+ " DOES NOT EXIST");
				} else {
					vmStates.add(new MachineState(key, Long.parseLong(vmState
							.get(VM_HOST_ID)), true, Double.parseDouble(vmState
							.get(VM_CPU_USAGE)), Double.parseDouble(vmState
							.get(VM_RAM_USAGE)), Double.parseDouble(vmState
							.get(VM_HD_USAGE)), -1.0, -1.0, -1.0, -1.0, -1.0,
							-1.0));
				}
			}
		}

		return vmStates;
	}

	/**
	 * Returns the resource usage of the specified VM.
	 * 
	 * @param vmID
	 * @return
	 */
	public MachineState getVMState(int vmID)
			throws IllegalMachineStateException {
		VirtualMachine vm = conn.getVM(vmID);
		vm.info();
		OneResponse res = vm.monitoring();

		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from VM: "
					+ res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
					.parseXMLString(res.getMessage());
			HashMap<String, String> vmState = parsedXML.get(vmID);
			if (vmState == null) {
				throw new IllegalMachineStateException("VM: " + vmID
						+ " DOES NOT EXIST");
			} else {
				return new MachineState(vmID, Long.parseLong(vmState
						.get(VM_HOST_ID)), true, Double.parseDouble(vmState
						.get(VM_CPU_USAGE)), Double.parseDouble(vmState
						.get(VM_RAM_USAGE)), Double.parseDouble(vmState
						.get(VM_HD_USAGE)), -1.0, -1.0, -1.0, -1.0, -1.0, -1.0);
			}
		}
	}

	/**
	 * Returns the resource usage of all VMs.
	 * 
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public List<MachineState> getHostStates()
			throws IllegalMachineStateException {
		ArrayList<MachineState> hostStates = new ArrayList<MachineState>();

		OneResponse res = conn.getAllHostStates();

		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from Host: "
					+ res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
					.parseXMLString(res.getMessage());

			HashMap<String, String> hostState = null;

			for (int key : parsedXML.keySet()) {
				hostState = parsedXML.get(key);
				if (hostState == null) {
					throw new IllegalMachineStateException("Host: " + key
							+ " DOES NOT EXIST");
				} else {
					Host h = conn.getHost(key);
					if (h != null) {
						hostStates.add(new MachineState(key, key,
								h.isEnabled(), Double.parseDouble(hostState
										.get(HOST_CPU_USAGE)), Double
										.parseDouble(hostState
												.get(HOST_RAM_USAGE)), Double
										.parseDouble(hostState
												.get(HOST_HD_USAGE)), Double
										.parseDouble(hostState
												.get(HOST_CPU_FREE)), Double
										.parseDouble(hostState
												.get(HOST_RAM_FREE)), Double
										.parseDouble(hostState
												.get(HOST_HD_FREE)), Double
										.parseDouble(hostState.get(MAX_CPU)),
								Double.parseDouble(hostState.get(MAX_RAM)),
								Double.parseDouble(hostState.get(MAX_HD))));
					}
				}
			}
		}

		return hostStates;
	}

	/**
	 * Returns the CPU usage of the specified Host.
	 * 
	 * @param vmID
	 * @return
	 */
	public MachineState getHostState(int hostID)
			throws IllegalMachineStateException {
		Host host = conn.getHost(hostID);
		host.info();
		OneResponse res = host.monitoring();

		if (res.isError()) {
			throw new IllegalMachineStateException("Response error from Host: "
					+ res.getErrorMessage());
		} else {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
					.parseXMLString(res.getMessage());
			HashMap<String, String> hostState = parsedXML.get(hostID);
			if (hostState == null) {
				throw new IllegalMachineStateException("Host: " + hostID
						+ " DOES NOT EXIST");
			} else {
				return new MachineState(hostID, hostID, conn.getHost(hostID)
						.isEnabled(), Double.parseDouble(hostState
						.get(HOST_CPU_USAGE)), Double.parseDouble(hostState
						.get(HOST_RAM_USAGE)), Double.parseDouble(hostState
						.get(HOST_HD_USAGE)), Double.parseDouble(hostState
						.get(HOST_CPU_FREE)), Double.parseDouble(hostState
						.get(HOST_RAM_FREE)), Double.parseDouble(hostState
						.get(HOST_HD_FREE)), Double.parseDouble(hostState
						.get(MAX_CPU)), Double.parseDouble(hostState
						.get(MAX_RAM)), Double.parseDouble(hostState
						.get(MAX_HD)));
			}
		}
	}

	/**
	 * Disables the specified host.
	 * 
	 * @param hostID
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public boolean disableHost(int hostID) throws IllegalMachineStateException {
		OneResponse res = conn.disableHost(hostID);

		if (res.isError()) {
			throw new IllegalMachineStateException(
					"Host could not be disabled: " + res.getErrorMessage());
		}

		return true;
	}

	/**
	 * Enables the specified host.
	 * 
	 * @param hostID
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public int enableHost() throws IllegalMachineStateException {
		OneResponse res = conn.enableHost();

		if (res == null) {
			throw new IllegalMachineStateException(
					"Host could not be enabled: no disabled hosts exist.");
		} else if (res.isError()) {
			throw new IllegalMachineStateException(
					"Host could not be enabled: " + res.getErrorMessage());
		}

		HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
				.parseXMLString(res.getMessage());
		Set<Integer> enabledHosts = parsedXML.keySet();

		if (enabledHosts.size() > 0) {
			return enabledHosts.toArray(new Integer[enabledHosts.size()])[0];
		} else {
			return -1;
		}
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
	public MachineState newVMState(long ID, long HOSTID, boolean enabled,
			double cpuUsage, double ramUsage, double hdUsage, double cpuFree,
			double ramFree, double hdFree, double maxCPU, double maxRam,
			double maxHD) {
		return new MachineState(ID, HOSTID, enabled, cpuUsage, ramUsage,
				hdUsage, cpuFree, ramFree, hdFree, maxCPU, maxRam, maxHD);
	}

	/**
	 * Represents the state of a VM.
	 * 
	 * @author darer121
	 * 
	 */
	public class MachineState {
		public final Long ID;
		public final Long HOSTID;
		public final boolean enabled;

		public final double cpuUsage;
		public final double ramUsage;
		public final double hdUsage;

		public final double cpuFree;
		public final double ramFree;
		public final double hdFree;

		public final double maxCPU;
		public final double maxRam;
		public final double maxHD;

		private MachineState(final long ID, final long HOSTID,
				final boolean enabled, final double cpuUsage,
				final double ramUsage, final double hdUsage,
				final double cpuFree, final double ramFree,
				final double hdFree, final double maxCPU, final double maxRam,
				final double maxHD) {
			this.ID = ID;
			this.HOSTID = HOSTID;
			this.enabled = enabled;

			this.cpuUsage = cpuUsage;
			this.ramUsage = ramUsage;
			this.hdUsage = hdUsage;

			this.cpuFree = cpuFree;
			this.ramFree = ramFree;
			this.hdFree = hdFree;

			this.maxCPU = maxCPU;
			this.maxRam = maxRam;
			this.maxHD = maxHD;
		}

		@Override
		public String toString() {
			return "Machine " + ID + ": CPU_USED=" + cpuUsage + " RAM_USED="
					+ ramUsage + " HD_USED=" + hdUsage + " CPU_FREE=" + cpuFree
					+ " RAM_FREE=" + ramFree + " HD_FREE=" + hdFree;
		}
	}

}
