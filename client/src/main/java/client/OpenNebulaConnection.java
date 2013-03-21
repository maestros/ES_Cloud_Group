package client;

import java.util.HashMap;
import java.util.Iterator;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

/**
 * OpenNebulaConnection
 * 
 * 19 March 2013
 * 
 * @author Alex Darer
 */

public class OpenNebulaConnection {

	private Client cli;
	private VirtualMachinePool macPool;
	private HostPool hostPool;
	private static OpenNebulaConnection _instance;

	// private String secret = "oneadmin:password";

	private OpenNebulaConnection(String secret, String target) {
		try {
			cli = new Client(secret, target);
			macPool = new VirtualMachinePool(cli);
			hostPool = new HostPool(cli);

			// String s =
			// "CPU=\"1\" DISK=[ IMAGE=\"dsl_root\", IMAGE_UNAME=\"oneadmin\", TARGET=\"hda\" ] GRAPHICS=[ LISTEN=\"localhost\", PASSWD=\"password\", PORT=\"6666\", TYPE=\"vnc\" ] MEMORY=\"512\" NAME=\"DSL_Template_01\" OS=[ ARCH=\"i686\", BOOT=\"hd\" ] RAW=[ TYPE=\"kvm\" ] TEMPLATE_ID=\"15\" VCPU=\"1\"";
			// System.out.println(VirtualMachine.allocate(cli,
			// s).getErrorMessage());

			// System.out.println(hostPool.monitoring().getMessage());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a new open nebula connection.
	 * 
	 * @param secret
	 * @param target
	 * @return
	 */
	public static OpenNebulaConnection openNebulaConnectionFactory(
			String secret, String target) {
		if (_instance == null)
			_instance = new OpenNebulaConnection(secret, target);
		return _instance;
	}

	/**
	 * Will deploy the given template as a VM.
	 * 
	 * @param template
	 * @return
	 */
	public OneResponse deployVM(String template) {
		return VirtualMachine.allocate(cli, template);
	}

	/**
	 * Deletes a VM and releases its resources.
	 * 
	 * @param vmID
	 * @return
	 */
	public OneResponse deleteVM(int vmID) {
		macPool.info();
		return macPool.getById(vmID).finalizeVM();
	}

	/**
	 * Shut downs a VM.
	 * 
	 * @param vmID
	 * @return
	 */
	public OneResponse shutdownVM(int vmID) {
		macPool.info();
		return macPool.getById(vmID).shutdown();
	}

	/**
	 * Will migrate a VM to the new specified host. Can specifiy a live
	 * migration.
	 * 
	 * @param vmID
	 * @param newHostID
	 * @param live
	 * @return
	 */
	public OneResponse migrateVM(int vmID, int newHostID, boolean live) {
		macPool.info();
		if (live) {
			return macPool.getById(vmID).liveMigrate(newHostID);
		} else {
			return macPool.getById(vmID).migrate(newHostID);
		}
	}

	/**
	 * Returns a VM with the specified ID.
	 * 
	 * @param id
	 * @return
	 */
	public VirtualMachine getVM(int id) {
		macPool.info();
		return macPool.getById(id);
	}

	/**
	 * Returns an iterator over all the virtual machines in the cloud.
	 * 
	 * @return
	 */
	public Iterator<VirtualMachine> getAllVMs() {
		macPool.info();
		return macPool.iterator();
	}

	/**
	 * Returns the response of the monitoring poll for the VMs.
	 * 
	 * @return
	 */
	public OneResponse getAllVMStates() {
		return macPool.info();
		// return macPool.monitoring(VirtualMachinePool.ALL_VM);
	}

	/**
	 * Returns the specified host.
	 * 
	 * @param id
	 * @return
	 */
	public Host getHost(int id) {
		hostPool.info();
		return hostPool.getById(id);
	}

	/**
	 * Returns an iterator over all the hosts in the cloud.
	 * 
	 * @return
	 */
	public Iterator<Host> getAllHosts() {
		hostPool.info();
		return hostPool.iterator();
	}

	/**
	 * Returns the response of the monitoring poll for the hosts.
	 * 
	 * @return
	 */
	public OneResponse getAllHostStates() {
		return hostPool.info();
		// return hostPool.monitoring();
	}

	/**
	 * Disables a host.
	 * 
	 * @param hostID
	 * @return
	 */
	public OneResponse disableHost(int hostID) {
		hostPool.info();
		return hostPool.getById(hostID).disable();
	}

	/**
	 * Enables a host.
	 * 
	 * @param hostID
	 * @return
	 */
	public OneResponse enableHost() {
		OneResponse res = hostPool.info();

		if (!res.isError()) {
			HashMap<Integer, HashMap<String, String>> parsedXML = XMLParser
					.parseXMLString(res.getMessage());
			for (int key : parsedXML.keySet()) {
				// HashMap<String, String> host = parsedXML.get(key);
				if (!this.getHost(key).isEnabled()) {
					return this.getHost(key).enable();
				}
			}
		}

		return res;

		// Iterator<Host> it = hostPool.iterator();
		//
		// while (it.hasNext()) {
		// Host h = it.next();
		// if (!h.isEnabled()) {
		// return h.enable();
		// }
		// }
		//
		// return null;
	}

}
