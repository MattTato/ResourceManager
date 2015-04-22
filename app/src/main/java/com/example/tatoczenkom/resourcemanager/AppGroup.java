package com.example.tatoczenkom.resourcemanager;

import java.util.HashSet;
import java.util.Set;

/**
 * APP GROUP
 *
 * This class wraps a set of like-UID App. This is
 * useful because network statistics are be polled
 * by UID, and apps may share UID. Because of this,
 * the network traffic statistics reported are the
 * aggregation of those for each constituent app
 * within the set of apps of like-UID.
 */
public class AppGroup {

    private int uid;
    private NetStat netstat;

    public Set<App> apps;

    /**
     * CONSTRUCTOR
     *
     * @param uid UID common to each app within group
     */
    AppGroup(int uid) {
        this.uid = uid;
        this.apps = new HashSet<>();
    }

    /**
     * Accesses the NetStat associated with
     * this AppGroup's UID.
     *
     * @return NetStat data structure for this AppGroup
     */
    public NetStat getNetworkUsage() {
        if (this.netstat == null) {
            this.netstat = new NetStat(this.uid);
        }
        return this.netstat;
    }

    /**
     * Returns either the String-encoded UID of the
     * group, or, if the group has only one app, the
     * name of the single app.
     *
     * @return group name
     */
    public String name() {

        if (this.apps.size() == 1) {
            return this.apps.iterator().next().name();
        }

        return "UID " + Integer.toString(this.uid);
    }
}
