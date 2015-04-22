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

    // Inclusive unit-flag bounds
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
     * CONSTRUCTOR
     * One-time definition of a NetStat point
     * @param sent volume of data sent over network (bytes)
     * @param recd volume of data received over network (bytes)
     */
    public NetStat(long sent, long recd) {
        this.sent = sent;
        this.received = recd;
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
     * @return total bytes received and sent
     */
    public long total() {
        return (this.sent + this.received());
    }

    /**
     * Computes the total bytes in terms of specified units.
     *
     * @return network traffic volume
     */
    public long total(int unitsFlag) {
        return convert(this.total(), unitsFlag);
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
     * Returns the long-representation of the byte-count
     * in terms of the specified units specified by the
     * units-constant flag.
     *
     * REQUIRES 0 <= unitsFlag <= MAX_FLAG
     * @param bytes
     * @param unitsFlag NetStat units-flag constant
     * @return
     */
    public static long convert(long bytes, int unitsFlag) {

        long divisor = 1;
        for (int i=1; i<=unitsFlag; i++) {
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
    public static int biggestInformativeUnit(long bytes) {

        // Find the largest unit flag capable of expressing the constraint
        int flag = MAX_UNIT;
        while (flag > MIN_UNIT) {

            // If this flag is informative, break
            if (convert(bytes, flag) > 0) {
                break;
            }

            // Otherwise, use the next-smallest unit flag
            flag--;
        }

        return flag;
    }

    /**
     * Returns the units-constant flag which may be used to
     * informatively express the smaller of data received
     * and data sent.
     *
     * @return units-constant flag
     */
    public int biggestInformativeUnit() {
        long constraint = this.received;
        if (this.sent < constraint) {
            constraint = this.sent;
        }

        return biggestInformativeUnit(constraint);
    }

    /**
     * Returns a string abbreviation corresponding to the
     * specified units flag.
     *
     * @param unitsFlag units-constant flag
     * @return string abbreviation of units; empty if unrecognized flag
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
