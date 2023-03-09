package org.ssd.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;

public class Utils {
    public static byte[] toByteArray(List<byte[]> bytes) {
        int totalLength = 0;
        for (byte[] array : bytes) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : bytes) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }

    public static byte[] toByteArray(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

    // todo: check if this works
    public static String getIpv4Address() {
        try{
            Enumeration<NetworkInterface> netInts = NetworkInterface.getNetworkInterfaces();
            while(netInts.hasMoreElements()) {
                NetworkInterface netInt = netInts.nextElement();
                if(!netInt.isLoopback()){
                    Enumeration<InetAddress> addresses = netInt.getInetAddresses();
                    while(addresses.hasMoreElements()){
                        InetAddress address = addresses.nextElement();
                        if(address instanceof Inet4Address){
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch(SocketException e){
            e.printStackTrace();
        }

        return null;
    }
}
