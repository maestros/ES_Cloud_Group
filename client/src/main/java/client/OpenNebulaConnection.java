package client;

import java.util.Iterator;

import org.opennebula.client.Client;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

public class OpenNebulaConnection {
	
	private Client cli;
	private VirtualMachinePool macPool;
	
//	private String secret = "oneadmin:password";
	
	private OpenNebulaConnection(String secret, String target) {
		try {
			cli = new Client(secret, target);
			macPool = new VirtualMachinePool(cli);
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
	public static OpenNebulaConnection openNebulaConnectionFactory(String secret, String target) {
		return new OpenNebulaConnection(secret, target);
	}
	
	/**
	 * Returns a VM with the specified ID.
	 * 
	 * @param id
	 * @return
	 */
	public VirtualMachine getVM(int id) {
		return macPool.getById(id);
	}
	
	/**
	 * Returns an iterator over all the virtual machines in the cloud.
	 * 
	 * @return
	 */
	public Iterator<VirtualMachine> getAllVMs() {
		return macPool.iterator();
	}
	
}
