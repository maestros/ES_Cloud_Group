package model;

import java.util.*;

public class Blade {

    private Long ID;
    private boolean on;

    private final double memory_total_Installed_MB;
    private final double disk_total_GB;
    private final double maximumNetworkBandwidth_KBs;

    private List<Double> cpuUsage_history;
    private List<Double> networkUsage_history;

    private double memoryUsage_current_MB;
    private double diskUsage_current_GB;

    private Map<Long, VM> vms;

    public Blade(Long ID, double memory_total, double memoryUsage_current_MB, double disk_total_GB, double diskUsage_current_GB, double networkBandwidthUsed_KBs, boolean on, double maximumNetworkBandwidth_KBs) {
        this.ID = ID;
        this.memory_total_Installed_MB = memory_total;
        this.memoryUsage_current_MB = memoryUsage_current_MB;
        this.disk_total_GB = disk_total_GB;
        this.diskUsage_current_GB = diskUsage_current_GB;
        this.on = on;
        this.vms = new HashMap<Long, VM>();
        this.maximumNetworkBandwidth_KBs = maximumNetworkBandwidth_KBs;
        this.cpuUsage_history = new MetricQueue<Double>(VM.windowSize);
        this.networkUsage_history = new MetricQueue<Double>(VM.windowSize);
    }

    public void applyUpdate(Blade blade) {
        this.cpuUsage_history = blade.cpuUsage_history;
        this.networkUsage_history = blade.networkUsage_history;
        this.memoryUsage_current_MB = blade.memoryUsage_current_MB;
        this.diskUsage_current_GB = blade.diskUsage_current_GB;
        this.vms = blade.vms;
        this.on = blade.on;
    }
    public List<VM> getDecSortedVMList(Overload factor) {
        switch (factor) {
            case CPU:
                return getDecSortedVMList_CPU();
            case NETWORK:
                return getDecSortedVMList_NETWORK();
            case MEMORY:
                return getDecSortedVMList_MEMORY();
            case DISK:
                return getDecSortedVMList_DISK();
            default:
                System.out.println("PROBLEM !!");
                return null;

        }
    }

    private List<VM> getDecSortedVMList_CPU() {
        List<VM> list = new ArrayList<VM>(this.getVMs().values());
        Collections.sort(list, new Comparator<VM>() {
            public int compare(VM vm1, VM vm2) {
                if (vm1.getAvCpuUsage() > vm2.getAvCpuUsage())
                    return 1;
                else
                    return -1;
            }

        });
        Collections.reverse(list);
        return list;
    }

    private List<VM> getDecSortedVMList_NETWORK() {
        List<VM> list = new ArrayList<VM>(this.getVMs().values());
        Collections.sort(list, new Comparator<VM>() {
            public int compare(VM vm1, VM vm2) {
                if (vm1.getAvNetworkUsage() > vm2.getAvNetworkUsage())
                    return 1;
                else
                    return -1;
            }

        });
        Collections.reverse(list);
        return list;
    }

    private List<VM> getDecSortedVMList_MEMORY() {
        List<VM> list = new ArrayList<VM>(this.getVMs().values());
        Collections.sort(list, new Comparator<VM>() {
            public int compare(VM vm1, VM vm2) {
                if (vm1.getMemoryUsage_MB() > vm2.getMemoryUsage_MB())
                    return 1;
                else
                    return -1;
            }

        });
        Collections.reverse(list);
        return list;
    }

    private List<VM> getDecSortedVMList_DISK() {
        List<VM> list = new ArrayList<VM>(this.getVMs().values());
        Collections.sort(list, new Comparator<VM>() {
            public int compare(VM vm1, VM vm2) {
                if (vm1.getDiskUsage_GB() > vm2.getDiskUsage_GB())
                    return 1;
                else
                    return -1;
            }

        });
        Collections.reverse(list);
        return list;
    }

    public boolean isMoveOk(VM vm) {

        if (this.isOn() == false)
            return false;

        if (this.getAvCpuUsage() + vm.getAvCpuUsage() < 0.70
                && this.getAvNetworkUsage() + vm.getAvNetworkUsage() < this.getMaximumNetworkBandwidth_KBs()
                && this.getMemoryUsage_current_MB() + vm.getMemoryUsage_MB() < this.getMemory_total_Installed_MB()
                && this.getDiskUsage_current_GB() + vm.getDiskUsage_GB() < this.getDisk_total_GB()) {
            return true;
        } else {
            System.out.println("couldnt move vm " + vm.getID() + " to blade " + this.getID());
            return false;
        }
    }

    public double getBandwidthUsedPercentage() {
        return 100 * (getAvNetworkUsage() / maximumNetworkBandwidth_KBs);
    }

    public double getMemoryUsedPercentage() {
        return 100 * (memoryUsage_current_MB / memory_total_Installed_MB);
    }

    public double getDiskUsagePercentage() {
        return 100 * (diskUsage_current_GB / disk_total_GB);
    }

    public double getMaximumNetworkBandwidth_KBs() {
        return maximumNetworkBandwidth_KBs;
    }

    public void addCpuDataPoint(Double cpuUsage_current) {
        cpuUsage_history.add(cpuUsage_current);
    }

    public double getAvCpuUsage() {
        if (cpuUsage_history.isEmpty())
            return 0;

        double total = 0;
        for (Double value : cpuUsage_history) {
            total += value;
        }

        return total / cpuUsage_history.size();
    }

    public void addNetworkUsageDataPoint(Double networkUsage_current) {
        networkUsage_history.add(networkUsage_current);
    }

    public Double getAvNetworkUsage() {
        if (networkUsage_history.isEmpty())
            return new Double(0);

        Double total = new Double(0);
        for (Double value : networkUsage_history)
            total += value;

        return total / networkUsage_history.size();
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long iD) {
        ID = iD;
    }

    public double getMemory_total_Installed_MB() {
        return memory_total_Installed_MB;
    }

    public double getMemoryUsage_current_MB() {
        return memoryUsage_current_MB;
    }

    public void setMemoryUsage_current_MB(double memoryUsage_current_MB) {
        this.memoryUsage_current_MB = memoryUsage_current_MB;
    }

    public double getDisk_total_GB() {
        return disk_total_GB;
    }

    public double getDiskUsage_current_GB() {
        return diskUsage_current_GB;
    }

    public void setDiskUsage_current_GB(double diskUsage_current_GB) {
        this.diskUsage_current_GB = diskUsage_current_GB;
    }


    public void addVM(VM vm) {
        this.vms.put(vm.getID(), vm);
    }

    public VM removeVM(Long vmId) {
        return this.vms.remove(vmId);
    }

    public boolean hasVMs() {
        return !this.vms.isEmpty();
    }

    public Map<Long, VM> getVMs() {
        return this.vms;
    }

}