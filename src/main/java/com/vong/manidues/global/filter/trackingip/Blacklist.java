package com.vong.manidues.global.filter.trackingip;

import java.util.HashSet;
import java.util.Set;

public class Blacklist {
    public static Set<String> blacklistedIps = new HashSet<>();

    public static String getBlacklistedIps() {
        StringBuilder list = new StringBuilder();
        for (String ip : blacklistedIps) {
            list.append(ip).append("\n\t");
        }
        return list.toString();
    }
}
