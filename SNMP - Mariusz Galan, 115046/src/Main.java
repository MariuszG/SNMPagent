import SnmpPackage.PackageSNMP;
import ParseMIB.MyTree;
import PkgDekoderBER.dekoderBER;
import PkgKoderBER.koderBER;
import SnmpPackage.WrapperClassForSNMPMessage;
import sharedMetods.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static ParseMIB.MyTree.Tree;
import static ParseMIB.MyTree.findObjectInTree;
import static sharedMetods.convertStringStreamToBitSetList8.convertStringStreamToBitSetList8;

public class Main {





    public static void main(String[] args) throws IOException {
        MyTree tree = new MyTree();
//        List<BitSet> bitStreamToDecode;
        tree.parseMIB("RFC1213-MIB");
//
//
//        System.out.println("------------------DEKODER PDU------------------");
//        String snmpMsg = "302C"; // 302C
//        String snmpVer = "020100";
//        String snmpComStr = "040770726976617465";
//        String snmpPDU = "A01E"; // A01E
//        String pduReqID = "020101";
//        String pduErr = "020100";
//        String pduErrIndex = "020100";
////        String pduVarbindList = "3013";
////        String pduVarbind = "3011";
////        String varbindObjectIdentifier = "060D2B060104019478010207030200";
////        String pduVarbindList = "3008";
////        String pduVarbind = "3006";
////        String varbindObjectIdentifier = "06062B0601040100"; //1.3.6.1.4.1.0
//
//        String pduVarbindList = "300A";
//        String pduVarbind = "3008";
//        String varbindObjectIdentifier = "06082B06010201020100"; //ifNumber 1.3.6.1.2.1.2.1.0
////        String varbindObjectIdentifier = "06082B07010201020300"; //NIE ISTNIEJE 1.3.6.1.2.1.2.3.0
////
//        String varbindValue = "05000000";
//
//        bitStreamToDecode = convertStringStreamToBitSetList8(snmpMsg + snmpVer + snmpComStr + snmpPDU + pduReqID + pduErr + pduErrIndex + pduVarbindList + pduVarbind + varbindObjectIdentifier + varbindValue); //
//
//        PackageSNMP packageSNMP = new PackageSNMP();
////        System.out.println(bitStreamToDecode.size());
//
//
//
//        System.out.println("------------------decodeGetRequest------------------");
//        WrapperClassForSNMPMessage decodedSNMPMsg = packageSNMP.createObjectSNMPMessage(bitStreamToDecode);
//
//        System.out.println("------------------codeGetRequest------------------");
//        packageSNMP.createPackageSNMPMessage(decodedSNMPMsg);
//
//
//
//
//
//        System.out.println("------------------decodeSetRequest------------------");
//        List<BitSet> setRequestList;
//        String snmpMsgSet = "302A"; // 302A
//        String snmpVerSet = "020100";
//        String snmpComStrSet = "04067075626c6963";
//        String snmpPDUSet = "A31D"; // A31D setReq
//        String pduReqIDSet = "02042C5D6FE2";
//        String pduErrSet = "020100";
//        String pduErrIndexSet = "020100";
//        String pduVarbindListSet = "300F";
//        String pduVarbindSet = "300D";
//        String varbindObjectIdentifierSet = "06082B06010201010700"; //  1.3.6.1.2.1.1.7.0
//        String varbindValueSet = "020132";
//
//        setRequestList = convertStringStreamToBitSetList8(snmpMsgSet + snmpVerSet + snmpComStrSet + snmpPDUSet + pduReqIDSet + pduErrSet + pduErrIndexSet + pduVarbindListSet + pduVarbindSet + varbindObjectIdentifierSet + varbindValueSet);
//        WrapperClassForSNMPMessage decodedSNMPSetReq = PackageSNMP.createObjectSNMPMessage(setRequestList);
//
//
//        System.out.println("------------------codeSetRequest------------------");
//        PackageSNMP.createPackageSNMPMessage(decodedSNMPSetReq);
//        tree.BER();

//        findObjectInTree(Tree.get(0), "1.3.6.1.2.1.1.7.0");
        findObjectInTree(Tree.get(0), "1.3.6.1.2.1.5.5.0");


    }
}

//todo POPRAWIC ALGORYTM PRZESZUKIWANIA DRZEWA!
//todo metoda populacji wartosci value w lisciach drzewa

