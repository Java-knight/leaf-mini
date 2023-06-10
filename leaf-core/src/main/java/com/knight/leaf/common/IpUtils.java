package com.knight.leaf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Ip 工具类
 * 迭代器模式
 * @desc
 * @author knight
 * @date 2023/6/6
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取当前机器局域网下的所有ip地址
     * 默认返回第一个ip地址
     * @return
     */
    public static String getIp() {
        String ip;
        try {
            List<String> ipList = getHostAddress(null);
            // default the first
            ip = (!ipList.isEmpty()) ? ipList.get(0) : "";
        } catch (Exception e) {
            ip = "";
            logger.warn("IpUtils get ip warn", e);
        }
        return ip;
    }

    /**
     * 获取当前机器局域网下的指定的ip地址
     * @param interfaceName
     * @return
     */
    public static String getIp(String interfaceName) {
        String ip;
        try {
            List<String> ipList = getHostAddress(interfaceName);
            // default the first
            ip = (!ipList.isEmpty()) ? ipList.get(0) : "";
        } catch (Exception e) {
            ip = "";
            logger.warn("IpUtils get ip warn", e);
        }
        return ip;
    }

    /**
     * 获取已激活网卡的 Ip 地址[类似于 ifconfig]
     * 注: 跳过 环回地址[loopback address] 和 ipv6
     * @param interfaceName 可指定网卡名称, null则获取全部
     * @return
     */
    private static List<String> getHostAddress(String interfaceName) throws SocketException {
        List<String> ipList = new ArrayList<>(5);
        // 获取当前机器局域网下的ip地址
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> allAddress = ni.getInetAddresses();
            while (allAddress.hasMoreElements()) {
                InetAddress address = allAddress.nextElement();
                if (address.isLoopbackAddress()) {  // 是否是环回地址[127.0.0.1]
                    continue;
                }
                if (address instanceof Inet6Address) {  // 是否是 ipv6 地址
                    continue;
                }
                String hostAddress = address.getHostAddress();
                if (null == interfaceName) {  // 获取全部
                    ipList.add(hostAddress);
                } else if (interfaceName.equals(ni.getDisplayName())) {  // 获取指定网卡地址
                    ipList.add(hostAddress);
                }
            }
        }
        return ipList;
    }
}
