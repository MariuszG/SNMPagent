package SnmpPackage;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class WrapperClassForSNMPMessage {
    SNMPMessage snmpMessage;
    List<BitSet> codedMessage;
    DecodedHelperClass decodedSnmpMsg;
    DecodedHelperClass decodedCommunityStr;
    DecodedHelperClass decodedSnmpPDU;
    DecodedHelperClass decodedPDUReqID;
    DecodedHelperClass decodedPDUErrorStatus;
    DecodedHelperClass decodedPDUErrorIndex;
    DecodedHelperClass decodedSnmpPDUVarbindList;
    DecodedHelperClass decodedSnmpPDUVarbind;
    DecodedHelperClass decodedOIDPath;
    List<BitSet> varbindValueList;

    WrapperClassForSNMPMessage() {
        this.snmpMessage = new SNMPMessage();
        this.codedMessage = new ArrayList<>();
        this.decodedSnmpMsg = new DecodedHelperClass();
        this.decodedCommunityStr = new DecodedHelperClass();
        this.decodedSnmpPDU = new DecodedHelperClass();
        this.decodedPDUReqID = new DecodedHelperClass();
        this.decodedPDUErrorStatus = new DecodedHelperClass();
        this.decodedPDUErrorIndex = new DecodedHelperClass();
        this.decodedSnmpPDUVarbindList = new DecodedHelperClass();
        this.decodedSnmpPDUVarbind = new DecodedHelperClass();
        this.decodedOIDPath = new DecodedHelperClass();
        this.varbindValueList = new ArrayList<>();

    }

    WrapperClassForSNMPMessage(SNMPMessage snmpMsg, List<BitSet> listBitSet) {
        this.snmpMessage = snmpMsg;
        this.codedMessage = listBitSet;
    }

//    WrapperClassForSNMPMessage(SNMPMessage snmpMsg, List<BitSet> listBitSet, DecodedHelperClass decodedHelperClass) {
//        this.snmpMessage = snmpMsg;
//        this.codedMessage = listBitSet;
//        this.decodedHelperClass = decodedHelperClass;
//
//    }
}
