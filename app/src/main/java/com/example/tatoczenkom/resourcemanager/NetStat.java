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

    // Unit-flag constants
    public static final int BYTES = 0;  // 1024^0 bytes in BYTES
    public static final int KILOS = 1;  // 1024^1 bytes in KILOS
    public static final int MEGAS = 2;  // 1024^2 bytes in MEGAS
    public static final int GIGAS = 3;  // 1024^3 bytes in GIGAS
    public static final int TERAS = 4;

    public static final int MIN_UNIT = BYTES, MAX_UNIT = TERAS;

    /**
     * CONSTRUCTOR
     * Builds a network usage statistic for an app.
     * @param uid identifier of app for which to build stat
     */
    public NetStat(int uid) {
        this.sent = TrafficStats.getUidTxBytes(uid);
        this.received = TrafficStats.getUidRxBytes(uid);
    }

    /**
     * Reports the volume of data sent (in bytes)
     * @return volume of data sent
     */
    public long sent() {
        return this.sent;
    }

    /**
     * Reports the volume of data sent (units specified).
     * @param unitsFlag NetStat units-flag constant
     * @return volume of data sent
     */
    public long sent(int unitsFlag) {
        return convert(this.sent(), unitsFlag);
    }

    /**
     * Reports the volume of data received (in bytes).
     * @return volume of data received
     */
    public long received() {
        return this.received;
    }

    /**
     * Reports the volume of data received (units specified).
     * @param unitsFlag NetStat units-flag constant
     * @return volume of data received
     */
    public long received(int unitsFlag) {
        return convert(this.received(), unitsFlag);
    }

    /**
     * Computes the total bytes sent and received for
     * this network statistic.
     *
     * @return total bytes received/sent
     */
    public long total() {
        return (this.sent + this.received());
    }

    /**
     * Indicates whether this network statistic is a valid
     * data point.
     *
     * @return TRUE ==> valid; FALSE ==> no guarantee of validity
     */
    public boolean valid() {
        return ((this.sent > 0) && (this.received > 0));
    }

    /**
     * Returns the long-representation of the byte-count in the specified units.
     *
     * REQUIRES 0 <= unitsFlag <= MAX_FLAG
     * @param bytes
     * @param unitsFlag NetStat units-flag constant
     * @return
     */
    public static long convert(long bytes, int unitsFlag) {

        int power = unitsFlag;
        long divisor = 1;
        for (int i=1; i<power; i++) {
            divisor *= 1024;
        }

        return bytes / divisor;
    }

    /**
     * Returns the units-flag of the greatest order
     * of magnitude that may express the network statistic.
     *
     * @return unit-flag for largest informative unit
     */
    public int biggestInformativeUnit() {

        // Get the constraining data point
        long constraint = this.received;        // assume 'received' data to be the smallest
        if (this.sent < this.received()) {      // use 'sent' if it's actually the smaller
            constraint = this.sent;
        }

        // Find the largest unit flag capable of expressing the constraint
        int flag = MAX_UNIT;
        while (flag > MIN_UNIT) {

            // If this flag is informative, break
            if (convert(constraint, flag) > 0) {
                break;
            }

            // Otherwise, use the next-smallest unit flag
            flag--;
        }

        return flag;
    }

    /**
     * Returns a string abbreviation of the specified units of byte-volume.
     * @param unitsFlag
     * @return
     */
    public static String unitAbbv(int unitsFlag) {
        String abbv = "";
        switch(unitsFlag) {
            case (BYTES) :
                abbv = "B";
                break;
            case (KILOS) :
                abbv = "KB";
                break;
            case (MEGAS) :
                abbv = "MB";
                break;
            case (GIGAS) :
                abbv = "GB";
                break;
            case (TERAS) :
                abbv = "TB";
                break;
        }

        return abbv;
    }
}
