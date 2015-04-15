package com.example.tatoczenkom.resourcemanager;

import android.net.TrafficStats;

/**
 * Read-only class which, for a UID, captures
 * its interesting network statistics.
 */
public class NetStat {

    // Network traffic volume
    private long sent;          // TCP bytes sent
    private long received;      // TCP bytes recd

    public NetStat(int uid) {
        this.sent = TrafficStats.getUidTxBytes(uid);
        this.received = TrafficStats.getUidRxBytes(uid);
    }

    /**
     * For a UID, sets TCP sent/received since boot
     * @param uid UID for which to find packet stats
     * @return Flags whether or not both send/receive were read well.
     */
    private boolean getByUid(int uid) {

        sent = TrafficStats.getUidTxPackets(uid);
        received = TrafficStats.getUidRxPackets(uid);

        if ((this.sent > 0) && (this.received > 0)) {
            return true;
        }

        return false;
    }

    public long sent() {
        return this.sent;
    }

    public long received() {
        return this.received;
    }

    public boolean valid() {
        return ((this.sent > 0) && (this.received > 0));
    }
}
