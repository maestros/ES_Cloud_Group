package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.MachineMonitor.MachineState;

public class Cloud {

	private Map<Long, Blade> blades;

	public void setBlade(Long id, Blade blade) {
		blades.put(id, blade);
	}

	public Cloud() {
		this.blades = new HashMap<Long, Blade>();
	}

	public Map<Long, Blade> getBlades() {
		return blades;
	}

	public void updateBlades(List<MachineState> hostStates, List<MachineState> vmStates) {
		for (MachineState ms : hostStates) {
			Blade bl = blades.get(ms.ID);
			if (bl != null) {
				bl.addCpuDataPoint(ms.cpuUsage);
				bl.setDiskUsage_current_GB(ms.hdUsage);
				bl.setMemoryUsage_current_MB(ms.ramUsage);
				bl.addNetworkUsageDataPoint(0.0);
				bl.setOn(ms.enabled);
			} else {
				bl = new Blade(ms.ID, ms.maxRam, ms.ramUsage, ms.maxHD,
						ms.hdUsage, 0, true, 1000);
				bl.addCpuDataPoint(ms.cpuUsage);
				bl.addNetworkUsageDataPoint(0.0);

				blades.put(bl.getID(), bl);
			}
		}
		
		for (MachineState ms : vmStates) {
			Blade bl = blades.get(ms.HOSTID);
			if (bl != null) {
				VM vm = bl.getVMs().get(ms.ID);
				if (vm != null) {
					vm.addCpuDataPoint(ms.cpuUsage);
					vm.addNetworkUsageDataPoint(0.0);
				} else {
					vm = new VM(ms.ID, ms.ramUsage, ms.hdUsage, ms.HOSTID);
					vm.addCpuDataPoint(ms.cpuUsage);
					vm.addNetworkUsageDataPoint(0.0);
					
					bl.getVMs().put(ms.ID, vm);
				}
			}
		}
	}

}