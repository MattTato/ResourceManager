package com.example.tatoczenkom.resourcemanager;

import java.util.Comparator;

/**
 * APPGROUP DATA COMPARATOR
 *
 * This utility class may be used to sort groups of
 * like-UID apps based on their network traffic. AppGroup
 * A, B are ordered when total network traffic of B does
 * not exceed that of A.
 */
public class AppGroupDataComparator implements Comparator<AppGroup> {

    /**
     * Compares two AppGroup for use in Collections'
     * 'sort' functionality.
     *
     * @param a control AppGroup
     * @param b test AppGroup
     *
     * @return traffic comparison
     */
    public int compare(AppGroup a, AppGroup b) {
        return (0 - Long.compare(trafficVolume(a), trafficVolume(b))); // return descending-ordered traffic volume
    }

    /**
     * Calculate the total network traffic volume for
     * a given App.
     *
     * @param g subject AppGroup
     *
     * @return total TCP traffic volume of app since device boot
     */
    private long trafficVolume(AppGroup g) {
        return g.getNetworkUsage().total();
    }
}