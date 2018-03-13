package UDPServer;

import ParseMIB.MyTree;
import PkgKoderBER.koderBER;
import SnmpPackage.PackageSNMP;
import SnmpPackage.WrapperClassForSNMPMessage;


import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static PkgKoderBER.koderBER.getBitSet;
import static PkgKoderBER.koderBER.keySize;
import static sharedMetods.convertStringStreamToBitSetList8.convertStringStreamToBitSetList8;

class UDPServer
{
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.length()/8+1];
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }

    public static void main(String args[]) throws Exception
    {
        DatagramSocket serverSocket = new DatagramSocket(161);
        byte[] receiveData = new byte[1024];
//        byte[] sendData = new byte[1024];
        StringBuilder strBuild = new StringBuilder();
//        List<BitSet> agentRespondList = new ArrayList<>();
        MyTree tree = new MyTree();
        tree.parseMIB("RFC1213-MIB");


        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String hexString = bytesToHex(receiveData);
            System.out.println("hexString: " + hexString);
            List<BitSet> bitStreamToDecode = convertStringStreamToBitSetList8(hexString);
            WrapperClassForSNMPMessage decodedSNMPMsg = PackageSNMP.createObjectSNMPMessage(bitStreamToDecode);

            List<BitSet> agentRespondList = new ArrayList<>(PackageSNMP.createPackageSNMPMessage(decodedSNMPMsg));

            byte[] sendData = new byte[agentRespondList.size()];
            for(int l=0; l < agentRespondList.size(); l++) {

                byte[] toByteArrayTemp = toByteArray(koderBER.reverseBitSet(agentRespondList.get(l)));
                if(toByteArrayTemp.length > 1 ){
                    sendData[l] = toByteArray(koderBER.reverseBitSet(agentRespondList.get(l)))[1];
                } else {
                    sendData[l] = toByteArray(koderBER.reverseBitSet(agentRespondList.get(l)))[0];
                }
            }

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
}