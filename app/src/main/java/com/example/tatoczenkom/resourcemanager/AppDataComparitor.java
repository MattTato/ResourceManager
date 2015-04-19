package com.example.tatoczenkom.resourcemanager;

import java.util.Comparator;

/**
 * APPDATACOMPARITOR
 *
 * This class is responsible for app sort by traffic statistics.
 */
class AppDataComparitor implements Comparator<App> {

    /**
     * Compare two apps.
     * @param a
     * @param b
     * @return
     */
    public int compare(App a, App b) {
        return (0 - Long.compare(trafficVolume(a), trafficVolume(b))); // return descending-ordered traffic volume
    }

    /**
     * Calculate the total network traffic volume for
     * a given App.
     * @param a subject App
     * @return total TCP traffic volume of app since device boot
     */
    private long trafficVolume(App a) {
        NetStat n = a.getNetworkUsage();
        return n.total();
    }
}
