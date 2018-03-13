package SnmpPackage;

public class PDU {
    String typePDU;
    Long requestID;
    Integer errorStatus;
    Long errorIndex;
    Varbind variableBindingsList;

    public PDU() {
        this.typePDU = null;
        this.requestID = null;
        this.errorStatus = null;
        this.errorIndex = null;
        this.variableBindingsList = new Varbind();
    }
}
