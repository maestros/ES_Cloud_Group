package client;

import org.opennebula.client.OneResponse;

/**
 * VirtualMachineDeployment
 *
 * 20 March 2013
 * @author Alex Darer
 */

public class VirtualMachineDeployment {
	
	public static void main(String[] args) {
		VirtualMachineDeployment vmd = VirtualMachineDeployment.VirtualMachineDeploymentFactory("oneadmin:password", "http://localhost:2633/RPC2");
		
		try {
			System.out.println(vmd.deployVM(VirtualMachineDeployment.template1));
		} catch (IllegalMachineStateException e) {
			e.printStackTrace();
		}
	}

	public static final String template1 = "CPU=\"1\" DISK=[ IMAGE=\"dsl_root\", IMAGE_UNAME=\"oneadmin\", TARGET=\"hda\" ] GRAPHICS=[ LISTEN=\"localhost\", PASSWD=\"password\", PORT=\"6666\", TYPE=\"vnc\" ] MEMORY=\"512\" NAME=\"DSL_Template_01\" OS=[ ARCH=\"i686\", BOOT=\"hd\" ] RAW=[ TYPE=\"kvm\" ] TEMPLATE_ID=\"15\" VCPU=\"1\"";

	private OpenNebulaConnection conn;

	private VirtualMachineDeployment(OpenNebulaConnection conn) {
		this.conn = conn;
	}

	/**
	 * Returns a new VirtualMachineDeployment object.
	 * 
	 * @param secret
	 * @param target
	 * @return
	 */
	public static VirtualMachineDeployment VirtualMachineDeploymentFactory(
			String secret, String target) {
		OpenNebulaConnection conn = OpenNebulaConnection
				.openNebulaConnectionFactory(secret, target);
		return new VirtualMachineDeployment(conn);
	}

	/**
	 * Will attempt to deploy the VM in the given template.
	 * 
	 * @param template
	 * @return true if VM is deployed
	 * @throws IllegalMachineStateException 
	 */
	public boolean deployVM(String template) throws IllegalMachineStateException {
		OneResponse res = conn.deployVM(template);
		
		if (res.isError()) {
			throw new IllegalMachineStateException("VM not allocated correctly: " + res.getErrorMessage());
		}
		
		return true;
	}
	
	/**
	 * Deletes the specified VM and releases its resources.
	 * 
	 * @param vmID
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public boolean deleteVM(int vmID) throws IllegalMachineStateException {
		OneResponse res = conn.deleteVM(vmID);
		
		if (res.isError()) {
			throw new IllegalMachineStateException("VM could not be deleted: " + res.getErrorMessage());
		}
		
		return true;
	}

	/**
	 * Shuts down the specified VM.
	 * 
	 * @param vmID
	 * @return
	 * @throws IllegalMachineStateException
	 */
	public boolean shutdownVM(int vmID) throws IllegalMachineStateException {
		OneResponse res = conn.shutdownVM(vmID);
		
		if (res.isError()) {
			throw new IllegalMachineStateException("VM could not be shutdown: " + res.getErrorMessage());
		}
		
		return true;
	}
	
}
