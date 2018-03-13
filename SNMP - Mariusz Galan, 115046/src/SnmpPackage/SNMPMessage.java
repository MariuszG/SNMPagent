package SnmpPackage;

public class SNMPMessage {
    int version;
    String communityName;
    PDU snmpPDU;

    SNMPMessage() {
        this.communityName = null;
        this.snmpPDU = new PDU();

    }

}
