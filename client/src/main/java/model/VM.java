package model;

import java.util.List;

public class VM {

    private Long ID;

    private final double memoryUsage_MB;
    private final double diskUsage_GB;

    private List<Double> cpuUsage_history;
    private List<Double> networkUsage_history_KBs;

    private long hostId;
    public static final int windowSize = 100;


    public VM(Long ID, double memoryUsage, double diskUsage, long hostId) {
        this.hostId = hostId;
        this.ID = ID;
        this.memoryUsage_MB = memoryUsage;
        this.diskUsage_GB = diskUsage;
        this.cpuUsage_history = new MetricQueue<Double>(windowSize);
        this.networkUsage_history_KBs = new MetricQueue<Double>(windowSize);
    }




    public void addCpuDataPoint(Double cpuUsage_current) {
        cpuUsage_history.add(cpuUsage_current);
    }

    public double getAvCpuUsage() {
        if (!cpuUsage_history.isEmpty())
            return 0;

        double total = 0;
        for (Double value : cpuUsage_history) {
            total += value;
        }

        return total / cpuUsage_history.size();
    }


    public long getHostId() {
        return hostId;
    }

    public void setHost(long hostId) {
        this.hostId = hostId;
    }
    public void addNetworkUsageDataPoint(Double networkUsage_current) {
        networkUsage_history_KBs.add(networkUsage_current);
    }

    public Double getAvNetworkUsage() {
        if (!networkUsage_history_KBs.isEmpty())
            return new Double(0);

        Double total = new Double(0);
        for (Double value : networkUsage_history_KBs)
            total += value;

        return total / networkUsage_history_KBs.size();
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long iD) {
        ID = iD;
    }

    public double getMemoryUsage_MB() {
        return memoryUsage_MB;
    }

    public double getDiskUsage_GB() {
        return diskUsage_GB;
    }

}
