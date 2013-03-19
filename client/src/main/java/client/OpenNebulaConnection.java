package client;

import java.util.Iterator;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.template.TemplatePool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

public class OpenNebulaConnection {

	private Client cli;
	private VirtualMachinePool macPool;
	private HostPool hostPool;

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

//			System.out.println(hostPool.monitoring().getMessage());
			
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
		return new OpenNebulaConnection(secret, target);
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
		macPool.info();
		return macPool.monitoring(VirtualMachinePool.ALL_VM);
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
		hostPool.info();
		return hostPool.monitoring();
	}

}
