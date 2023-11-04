package com.sandro.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */


public class EventUtils {
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = "Unknown IP";
        if (request != null) {
            ipAddress = request.getHeader("X-FORWARDED_FOR");
            if (ipAddress == null || ipAddress.equals("")) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder()
                .hideMatcherLoadStats().withCache(1000)
                .build();
        UserAgent agent = analyzer.parse(request.getHeader(UserAgent.USERAGENT_HEADER));
        return agent.getValue(UserAgent.AGENT_NAME) + " - " + agent.getValue(UserAgent.DEVICE_NAME);
    }
}
