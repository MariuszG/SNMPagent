package SnmpPackage;

import ParseMIB.MyTree;
import ParseMIB.*;
import PkgDekoderBER.DecodedObjectFromBitStream;
import PkgDekoderBER.dekoderBER;
import PkgKoderBER.*;
import sharedClass.ObjectType;
import sharedClass.TypeOfData;
import sharedMetods.*;


import java.util.*;

import static ParseMIB.MyTree.Tree;
import static ParseMIB.MyTree.dataTypeMap;
import static PkgKoderBER.koderBER.getBitSet;
import static PkgKoderBER.koderBER.getDataToFrameString;
import static PkgKoderBER.koderBER.reverseBitSet;
import static sharedMetods.convertStringStreamToBitSetList8.convertStringStreamToBitSetList8;


public class PackageSNMP {

    private static Map<Long, WrapperClassForSNMPMessage> RecivedSNMPMessage = new HashMap<>();
    private static final Map<Long, String> TypePDUs = new HashMap<>();
    private static final Map<Long, String> TypeErrorStatus = new HashMap<>();
    private static convertStringStreamToBitSetList8 convertStringStreamToBitSetList8 = new convertStringStreamToBitSetList8();
    static{
        TypePDUs.put((long) 0, "GetRequest");
        TypePDUs.put((long) 1, "GetNextRequest");
        TypePDUs.put((long) 2, "GetResponse");
        TypePDUs.put((long) 3, "SetRequest");

        TypeErrorStatus.put((long) 0, "noError");
        TypeErrorStatus.put((long) 1, "tooBig");
        TypeErrorStatus.put((long) 2, "noSuchName");
        TypeErrorStatus.put((long) 3, "badValue");
        TypeErrorStatus.put((long) 4, "readOnly");
        TypeErrorStatus.put((long) 5, "genErr");


    }


    private static String convertBitSetToString5(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            s.append(bitSet.get(i) == true ? 1 : 0);
        }
        return s.toString();
    }

    public static long convert(BitSet bits) {
        long value = 0L;
        int bitLenght = bits.length();
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    private static List<BitSet> splitBitSetList(List<BitSet> oriList, int lowIndex, int highIndex) {
        List<BitSet> splitedList = new ArrayList<>();

        for(int i = lowIndex; i <= highIndex; i++){
            splitedList.add(oriList.get(i));
        }

        return splitedList;
    }




    private static String getPDUType(BitSet firstOctet) {
        String reurnedType = null;
        koderBER.keySize = 5;
        String shortString = convertBitSetToString5(firstOctet.get(3, 8));
        BitSet shortBitSet = koderBER.getBitSet(shortString);
        BitSet revShortBitSet = koderBER.getReverseBitSet(convertBitSetToString5(shortBitSet));

        reurnedType = TypePDUs.get(convert(revShortBitSet));

        return reurnedType;
    }

    private BitSet setPDUType(String typePDUFormObject){
        koderBER.keySize = 8;
        BitSet result = new BitSet();

            if(typePDUFormObject.equals("GetRequest")) {
                result = koderBER.getBitSet("10100010");
            } else if (typePDUFormObject.equals("GetNextRequest")){
                result = koderBER.getBitSet("10100010");
            }

        return result;
    }

    private List<BitSet> validateValue() {
        List<BitSet> resultList = new ArrayList<>();



        return resultList;
    }

    private static List<List<BitSet>> errorRespond(MyTreeNode<ObjectType> foundedObject, String setVal) {
        List<BitSet> ErrorStatusList = new ArrayList<>();
        List<BitSet> ErrorIndexList = new ArrayList<>();
        List<BitSet> valueList = new ArrayList<>();
        List<List<BitSet>> errorRespond = new ArrayList<>();

            ErrorStatusList.add(getBitSet("00000010")); // 2
            ErrorStatusList.add(getBitSet("00000001")); // 1
            ErrorStatusList.add(getBitSet("00000000")); // 0  // noErr

            ErrorIndexList.add(getBitSet("00000010")); // 2
            ErrorIndexList.add(getBitSet("00000001")); // 1
            ErrorIndexList.add(getBitSet("00000000")); // 0



        if (foundedObject.getData().syntax.name == null || !foundedObject.isLeaf()) {
            ErrorStatusList.removeAll(ErrorStatusList);
            ErrorIndexList.removeAll(ErrorIndexList);

            ErrorStatusList.add(getBitSet("00000010")); // 2
            ErrorStatusList.add(getBitSet("00000001")); // 1
            ErrorStatusList.add(getBitSet("00000101")); // 3 //genErr

            ErrorIndexList.add(getBitSet("00000010")); // 2
            ErrorIndexList.add(getBitSet("00000001")); // 1
            ErrorIndexList.add(getBitSet("00000001")); // 1 // ID

            valueList.add(getBitSet("00000101"));
            valueList.add(getBitSet("00000000"));

        } else if (setVal != null) {
            if (foundedObject.getData().syntax.name.trim().equals("INTEGER")) {
//                Object minCon = foundedObject.getData().syntax.constrains.minValConstrain;
                if (foundedObject.getData().syntax.constrains.minValConstrain != null || foundedObject.getData().syntax.constrains.maxValConstrain != null) {
                    if (Integer.valueOf(foundedObject.getData().syntax.constrains.minValConstrain) < Integer.valueOf(setVal) || Integer.valueOf(setVal) > Integer.valueOf(foundedObject.getData().syntax.constrains.maxValConstrain)) {
                        ErrorStatusList.removeAll(ErrorStatusList);
                        ErrorIndexList.removeAll(ErrorIndexList);

                        ErrorStatusList.add(getBitSet("00000010")); // 2
                        ErrorStatusList.add(getBitSet("00000001")); // 1
                        ErrorStatusList.add(getBitSet("00000011")); // 3  // badValue

                        ErrorIndexList.add(getBitSet("00000010")); // 2
                        ErrorIndexList.add(getBitSet("00000001")); // 1
                        ErrorIndexList.add(getBitSet("00000000")); // 0

                        valueList.add(getBitSet("00000101"));
                        valueList.add(getBitSet("00000000"));
                    } else {
                        foundedObject.getData().syntax.setValue(setVal);
                        valueList.addAll(koderBER.codeFrameSimple(dataTypeMap.get(foundedObject.getData().syntax.name.trim()), foundedObject.getData().syntax.getValue()));
                    }
                } else if (foundedObject.getData().syntax.constrains.byteConstrain != null) {
                    if (!koderBER.validInputData(Integer.valueOf(setVal), Integer.valueOf(foundedObject.getData().syntax.constrains.byteConstrain))) {
                        ErrorStatusList.removeAll(ErrorStatusList);
                        ErrorIndexList.removeAll(ErrorIndexList);

                        ErrorStatusList.add(getBitSet("00000010")); // 2
                        ErrorStatusList.add(getBitSet("00000001")); // 1
                        ErrorStatusList.add(getBitSet("00000001")); // 1  // tooBig

                        ErrorIndexList.add(getBitSet("00000010")); // 2
                        ErrorIndexList.add(getBitSet("00000001")); // 1
                        ErrorIndexList.add(getBitSet("00000001")); // 1 ID

                        valueList.add(getBitSet("00000101"));
                        valueList.add(getBitSet("00000000"));
                    } else {
                        foundedObject.getData().syntax.setValue(setVal);
                        valueList.addAll(koderBER.codeFrameSimple(dataTypeMap.get(foundedObject.getData().syntax.name.trim()), foundedObject.getData().syntax.getValue()));
                    }
                } else {
                    foundedObject.getData().syntax.setValue(setVal);
                    valueList.addAll(koderBER.codeFrameSimple(dataTypeMap.get(foundedObject.getData().syntax.name.trim()), foundedObject.getData().syntax.getValue()));
                }
            } else if (foundedObject.getData().syntax.name.trim().equals("OCTET-STRING")) {
                if (setVal.length() > Integer.valueOf(foundedObject.getData().syntax.constrains.byteConstrain)) {
                    ErrorStatusList.removeAll(ErrorStatusList);
                    ErrorIndexList.removeAll(ErrorIndexList);

                    ErrorStatusList.add(getBitSet("00000010")); // 2
                    ErrorStatusList.add(getBitSet("00000001")); // 1
                    ErrorStatusList.add(getBitSet("00000001")); // 1  // tooBig

                    ErrorIndexList.add(getBitSet("00000010")); // 2
                    ErrorIndexList.add(getBitSet("00000001")); // 1
                    ErrorIndexList.add(getBitSet("00000001")); // 1 ID

                    valueList.add(getBitSet("00000101"));
                    valueList.add(getBitSet("00000000"));
                } else {
                    foundedObject.getData().syntax.setValue(setVal);
                    valueList.addAll(getDataToFrameString(foundedObject.getData().syntax.constrains.byteConstrain, setVal));
                }
            } else {
                foundedObject.getData().syntax.setValue(setVal);
                valueList.addAll(getDataToFrameString(foundedObject.getData().syntax.constrains.byteConstrain, setVal));
            }
        } else {
            valueList.addAll(koderBER.codeFrameSimple(dataTypeMap.get(foundedObject.getData().syntax.name.trim()), foundedObject.getData().syntax.getValue()));
        }


        errorRespond.add(ErrorStatusList);
        errorRespond.add(ErrorIndexList);
        errorRespond.add(valueList);

        return errorRespond;
    }

    private static String getNextOID(String actualOIDPath){
        StringBuilder nextOIDPath = new StringBuilder();

        String actualOIDPathString[] = actualOIDPath.split("\\.");
        int maxIndexInStringTable = actualOIDPathString.length-1;
        actualOIDPathString[maxIndexInStringTable-1] = String.valueOf(Integer.parseInt(actualOIDPathString[maxIndexInStringTable-1]) + 1);

        for(int i = 0; i <= maxIndexInStringTable; i++){
            if(i == maxIndexInStringTable) {
                nextOIDPath.append(actualOIDPathString[i]);
            } else {
                nextOIDPath.append(actualOIDPathString[i]).append(".");
            }
        }

        return nextOIDPath.toString();
    }


    private static List<BitSet> codeGetResponseNoSuchName(WrapperClassForSNMPMessage wrappedMsg){
        List<BitSet> codingResult = new ArrayList<>();
        List<BitSet> codingResultTemp = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        List<BitSet> varbindType = new ArrayList<>();
        List<BitSet> varbindTypeTemp = new ArrayList<>();
        List<BitSet> varbindTypeList = new ArrayList<>();
        List<BitSet> snmpPDUList = new ArrayList<>();
        koderBER koderBER = new koderBER();
        PkgKoderBER.koderBER.keySize = 8;
        System.out.println("Wystąpił wyjątek Null Point Exception!");

        u2List.add(PkgKoderBER.koderBER.getBitSet("00000101"));
        u2List.add(PkgKoderBER.koderBER.getBitSet("00000000"));

        varbindTypeTemp.addAll(wrappedMsg.decodedOIDPath.processedListBitSet);
        varbindTypeTemp.addAll(u2List);

        varbindType.add(PkgKoderBER.koderBER.getBitSet("00110000"));
        varbindType.addAll(koderBER.lengthFrame(String.valueOf(varbindTypeTemp.size())));
        varbindType.addAll(varbindTypeTemp);

        varbindTypeList.add(PkgKoderBER.koderBER.getBitSet("00110000"));
        varbindTypeList.addAll(koderBER.lengthFrame(String.valueOf(varbindType.size())));
        varbindTypeList.addAll(varbindType);

        snmpPDUList.addAll(wrappedMsg.decodedPDUReqID.processedListBitSet);
//            snmpPDUList.addAll(wrappedMsg.decodedPDUErrorStatus.processedListBitSet);
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000010"));  // errStatus tag
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000001"));  // errStatus len
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000010"));  // errStatus data
//            snmpPDUList.addAll(wrappedMsg.decodedPDUErrorIndex.processedListBitSet);
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000010"));  // errStatus tag
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000001"));  // errStatus len
        snmpPDUList.add(PkgKoderBER.koderBER.getBitSet("00000001"));  // errStatus data             //todo podac id obiektu ktorego nie mozna bylo odnaleźć

        snmpPDUList.addAll(varbindTypeList);

        codingResultTemp.addAll(wrappedMsg.decodedSnmpMsg.processedListBitSet);
        codingResultTemp.addAll(wrappedMsg.decodedCommunityStr.processedListBitSet);
        codingResultTemp.add(PkgKoderBER.koderBER.getBitSet("10100010"));
        codingResultTemp.addAll(koderBER.lengthFrame(String.valueOf(snmpPDUList.size())));
        codingResultTemp.addAll(snmpPDUList);

        codingResult.add(PkgKoderBER.koderBER.getBitSet("00110000"));
        codingResult.addAll(koderBER.lengthFrame(String.valueOf(codingResultTemp.size())));
        codingResult.addAll(codingResultTemp);

        for (BitSet x : codingResult) {
            System.out.print(Long.toHexString(convert(reverseBitSet(x))) + " ");
        }

        System.out.println("");
        System.out.println("------------------codeGetRequest -> createObjectSNMPMessage------------------");
        createObjectSNMPMessage(codingResult);
        return codingResult;
    }

    private static List<BitSet> codeGetRequest(WrapperClassForSNMPMessage wrappedMsg) {
        List<BitSet> codingResult = new ArrayList<>();
        List<BitSet> codingResultTemp = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        List<BitSet> varbindType = new ArrayList<>();
        List<BitSet> varbindTypeTemp = new ArrayList<>();
        List<BitSet> varbindTypeList = new ArrayList<>();
        List<BitSet> snmpPDUList = new ArrayList<>();
        List<List<BitSet>> errorRespond = new ArrayList<>();
        koderBER koderBER = new koderBER();
        PkgKoderBER.koderBER.keySize = 8;
        String OIDpath = "";


        switch (wrappedMsg.snmpMessage.snmpPDU.typePDU) {
            case "GetRequest":
                OIDpath = wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID;
                break;
            case "GetNextRequest":
                OIDpath = getNextOID(wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID);
                break;

            case "SetRequest":
                OIDpath = wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID;
                break;
            default:
                System.out.println("Brak dopasowania w Switch'u.");
        }
        MyTreeNode<ObjectType> foundedOID = new MyTree().findObjectInTree(Tree.get(0), OIDpath);


        try {

//            if (foundedOID.getData().syntax.name == null) {
//                System.out.println("Nie posiada typu do zakodowania!");
//                System.out.println(foundedOID.getData().syntax.name);
//
//            } else {
//                System.out.println(foundedOID.getData().syntax.name);
//                dataTypeMap.get(foundedOID.getData().syntax.name.trim()).present();
//                u2List.addAll(koderBER.codeFrameSimple(dataTypeMap.get(foundedOID.getData().syntax.name.trim()), foundedOID.getData().syntax.getValue()));
//            }
            if(wrappedMsg.snmpMessage.snmpPDU.typePDU.equals("SetRequest")) {
                errorRespond.addAll(errorRespond(foundedOID, wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.value.data)); //todo sprawdzic setRequest
            } else {
                foundedOID.getData().syntax.setValue("2380");
                errorRespond.addAll(errorRespond(foundedOID, null));                                                    //todo sprawdzic setRequest
            }

            varbindTypeTemp.addAll(wrappedMsg.decodedOIDPath.processedListBitSet);
//            varbindTypeTemp.addAll(u2List);
            varbindTypeTemp.addAll(errorRespond.get(2));                                                                        // value

            varbindType.add(PkgKoderBER.koderBER.getBitSet("00110000"));
            varbindType.addAll(koderBER.lengthFrame(String.valueOf(varbindTypeTemp.size())));
            varbindType.addAll(varbindTypeTemp);

            varbindTypeList.add(PkgKoderBER.koderBER.getBitSet("00110000"));
            varbindTypeList.addAll(koderBER.lengthFrame(String.valueOf(varbindType.size())));
            varbindTypeList.addAll(varbindType);

            snmpPDUList.addAll(wrappedMsg.decodedPDUReqID.processedListBitSet);
//            snmpPDUList.addAll(wrappedMsg.decodedPDUErrorStatus.processedListBitSet);
//            snmpPDUList.addAll(wrappedMsg.decodedPDUErrorIndex.processedListBitSet);
            snmpPDUList.addAll(errorRespond.get(0));                                                                           // errorStatus
            snmpPDUList.addAll(errorRespond.get(1));                                                                           // errorIndex
            snmpPDUList.addAll(varbindTypeList);

            codingResultTemp.addAll(wrappedMsg.decodedSnmpMsg.processedListBitSet);
            codingResultTemp.addAll(wrappedMsg.decodedCommunityStr.processedListBitSet);
            codingResultTemp.add(PkgKoderBER.koderBER.getBitSet("10100010"));
            codingResultTemp.addAll(koderBER.lengthFrame(String.valueOf(snmpPDUList.size())));
            codingResultTemp.addAll(snmpPDUList);

            codingResult.add(PkgKoderBER.koderBER.getBitSet("00110000"));
            codingResult.addAll(koderBER.lengthFrame(String.valueOf(codingResultTemp.size())));
            codingResult.addAll(codingResultTemp);

            for (BitSet x : codingResult) {
                System.out.print(Long.toHexString(convert(reverseBitSet(x))) + " ");
            }
            System.out.println("");

            System.out.println("------------------codeGetRequest -> createObjectSNMPMessage------------------");
            createObjectSNMPMessage(codingResult);

            return codingResult;

        } catch (NullPointerException e) {
            return codeGetResponseNoSuchName(wrappedMsg);

        }
    }

//    private List<BitSet> codeGetNextResponse(WrapperClassForSNMPMessage wrappedMsg) {
//        List<BitSet> codingResult = new ArrayList<>();
//
////        String getNextOID = wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID;
//        String getNextOID = getNextOID(wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID);
//
//        MyTreeNode<ObjectType> foundedOID = new MyTree().findObjectInTree(Tree.get(0), getNextOID(wrappedMsg.snmpMessage.snmpPDU.variableBindingsList.OID));
//        try{
//
//        } catch (NullPointerException e) {
//            codeGetResponseNoSuchName(wrappedMsg);
//        }
//
//
//        return codingResult;
//    }


    // odebranie ramki i przetworzenie na obiekt
    public static WrapperClassForSNMPMessage createObjectSNMPMessage(List<BitSet> reciveMsg) {
        WrapperClassForSNMPMessage wrapperClassForSNMPMessage = new WrapperClassForSNMPMessage();


//        DecodedObjectFromBitStream decodedSnmpMsg = new DecodedObjectFromBitStream();
        dekoderBER dekoderBER = new dekoderBER();

        wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject.visibility = dekoderBER.decodeVisibility(reciveMsg.get(0));
        wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject.lenght = dekoderBER.decodeLength(reciveMsg, wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject);

        wrapperClassForSNMPMessage.decodedSnmpMsg.lowIndex = wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject.numberLastTagOctet+1;
        wrapperClassForSNMPMessage.decodedSnmpMsg.highIndex = wrapperClassForSNMPMessage.decodedSnmpMsg.lowIndex + 2;
        wrapperClassForSNMPMessage.decodedSnmpMsg.processedListBitSet = splitBitSetList(reciveMsg, wrapperClassForSNMPMessage.decodedSnmpMsg.lowIndex, wrapperClassForSNMPMessage.decodedSnmpMsg.highIndex);
        wrapperClassForSNMPMessage.snmpMessage.version = Integer.parseInt(dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.decodedSnmpMsg.processedListBitSet).data);


////        DecodedObjectFromBitStream decodedCommunityStr = new DecodedObjectFromBitStream();
        wrapperClassForSNMPMessage.decodedCommunityStr.lowIndex = wrapperClassForSNMPMessage.decodedSnmpMsg.highIndex + 1;
        wrapperClassForSNMPMessage.decodedCommunityStr.highIndex = wrapperClassForSNMPMessage.decodedCommunityStr.lowIndex + dekoderBER.decodeLength(splitBitSetList(reciveMsg, wrapperClassForSNMPMessage.decodedCommunityStr.lowIndex,reciveMsg.size()-1), wrapperClassForSNMPMessage.decodedCommunityStr.decodedObject) + 1;
        wrapperClassForSNMPMessage.decodedCommunityStr.processedListBitSet = splitBitSetList(reciveMsg, wrapperClassForSNMPMessage.decodedCommunityStr.lowIndex , wrapperClassForSNMPMessage.decodedCommunityStr.highIndex);
        wrapperClassForSNMPMessage.snmpMessage.communityName = dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.decodedCommunityStr.processedListBitSet).data;


//        DecodedObjectFromBitStream decodedSnmpPDU = new DecodedObjectFromBitStream();
        wrapperClassForSNMPMessage.decodedSnmpPDU.lowIndex = wrapperClassForSNMPMessage.decodedCommunityStr.highIndex + 1;
        wrapperClassForSNMPMessage.decodedSnmpPDU.highIndex = reciveMsg.size()-1;
        List<BitSet> snmpPDUList = splitBitSetList(reciveMsg, wrapperClassForSNMPMessage.decodedSnmpPDU.lowIndex, wrapperClassForSNMPMessage.decodedSnmpPDU.highIndex);
        wrapperClassForSNMPMessage.decodedSnmpPDU.processedListBitSet = snmpPDUList;
//        wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.visibility = dekoderBER.decodeVisibility(snmpPDUList.get(0));
//        wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.lenght = dekoderBER.decodeLength(snmpPDUList, wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject);
        wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject = dekoderBER.decodeBitStream(snmpPDUList);
        wrapperClassForSNMPMessage.decodedSnmpPDU.highIndex = wrapperClassForSNMPMessage.decodedSnmpPDU.lowIndex + wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.lenght + 1;

//        wrapperClassForSNMPMessage.decodedSnmpPDU.processedListBitSet = splitBitSetList(reciveMsg, wrapperClassForSNMPMessage.decodedSnmpPDU.lowIndex, wrapperClassForSNMPMessage.decodedSnmpPDU.highIndex);
//        wrapperClassForSNMPMessage.decodedSnmpPDU.processedListBitSet = splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedSnmpPDU.lowIndex, wrapperClassForSNMPMessage.decodedSnmpPDU.highIndex);

        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.typePDU = getPDUType(wrapperClassForSNMPMessage.decodedSnmpPDU.processedListBitSet.get(0));


//        DecodedObjectFromBitStream decodedPDUReqID = dekoderBER.decodeBitStream(splitBitSetList(snmpPDUList, lowIndexPDU, highIndexPDU));
        wrapperClassForSNMPMessage.decodedPDUReqID.lowIndex = wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.numberLastTagOctet + 1;
        wrapperClassForSNMPMessage.decodedPDUReqID.highIndex = wrapperClassForSNMPMessage.decodedPDUReqID.lowIndex + dekoderBER.decodeLength(splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUReqID.lowIndex, snmpPDUList.size()-1), new DecodedObjectFromBitStream())+1;
        wrapperClassForSNMPMessage.decodedPDUReqID.processedListBitSet = splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUReqID.lowIndex, wrapperClassForSNMPMessage.decodedPDUReqID.highIndex);
        wrapperClassForSNMPMessage.decodedPDUReqID.decodedObject = dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.decodedPDUReqID.processedListBitSet);
        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.requestID = Long.parseLong(wrapperClassForSNMPMessage.decodedPDUReqID.decodedObject.data);


        wrapperClassForSNMPMessage.decodedPDUErrorStatus.lowIndex = wrapperClassForSNMPMessage.decodedPDUReqID.highIndex + 1;
        wrapperClassForSNMPMessage.decodedPDUErrorStatus.highIndex = wrapperClassForSNMPMessage.decodedPDUErrorStatus.lowIndex + dekoderBER.decodeLength(splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUErrorStatus.lowIndex, snmpPDUList.size()-1), new DecodedObjectFromBitStream())+1;
        wrapperClassForSNMPMessage.decodedPDUErrorStatus.processedListBitSet = splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUErrorStatus.lowIndex, wrapperClassForSNMPMessage.decodedPDUErrorStatus.highIndex);
        wrapperClassForSNMPMessage.decodedPDUErrorStatus.decodedObject = dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.decodedPDUErrorStatus.processedListBitSet);
        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.errorStatus = Integer.parseInt(wrapperClassForSNMPMessage.decodedPDUErrorStatus.decodedObject.data);
//
        wrapperClassForSNMPMessage.decodedPDUErrorIndex.lowIndex = wrapperClassForSNMPMessage.decodedPDUErrorStatus.highIndex + 1;
        wrapperClassForSNMPMessage.decodedPDUErrorIndex.highIndex = wrapperClassForSNMPMessage.decodedPDUErrorIndex.lowIndex + dekoderBER.decodeLength(splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUErrorStatus.lowIndex, snmpPDUList.size()-1), new DecodedObjectFromBitStream())+1;
        wrapperClassForSNMPMessage.decodedPDUErrorIndex.processedListBitSet = splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUErrorIndex.lowIndex, wrapperClassForSNMPMessage.decodedPDUErrorIndex.highIndex);
        wrapperClassForSNMPMessage.decodedPDUErrorIndex.decodedObject =  dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.decodedPDUErrorIndex.processedListBitSet);
        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.errorIndex = Long.parseLong(wrapperClassForSNMPMessage.decodedPDUErrorIndex.decodedObject.data);


        wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.processedListBitSet = splitBitSetList(snmpPDUList, wrapperClassForSNMPMessage.decodedPDUErrorIndex.highIndex + 1, snmpPDUList.size()-1);
        wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject.visibility = dekoderBER.decodeVisibility(wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.processedListBitSet.get(0));
        wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject.lenght = dekoderBER.decodeLength(wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.processedListBitSet,wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject);

        wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.processedListBitSet = splitBitSetList(wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.processedListBitSet, wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject.numberLastTagOctet+1, wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.processedListBitSet.size()-1);
//        DecodedObjectFromBitStream decodedSnmpPDUVarbind = new DecodedObjectFromBitStream();
        wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject.visibility = dekoderBER.decodeVisibility(wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.processedListBitSet.get(0));
        wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject.lenght = dekoderBER.decodeLength(wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.processedListBitSet, wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject);

        wrapperClassForSNMPMessage.decodedOIDPath.lowIndex = wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject.numberLastTagOctet + 1;
        List<BitSet> OIDPathList = splitBitSetList(wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.processedListBitSet, wrapperClassForSNMPMessage.decodedOIDPath.lowIndex, wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.processedListBitSet.size()-1);
        wrapperClassForSNMPMessage.decodedOIDPath.decodedObject = dekoderBER.decodedOID(OIDPathList);
        wrapperClassForSNMPMessage.decodedOIDPath.processedListBitSet = splitBitSetList(OIDPathList, 0, wrapperClassForSNMPMessage.decodedOIDPath.decodedObject.lenght+1);
        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.variableBindingsList.OID = wrapperClassForSNMPMessage.decodedOIDPath.decodedObject.data;
//
        wrapperClassForSNMPMessage.varbindValueList = splitBitSetList(OIDPathList, wrapperClassForSNMPMessage.decodedOIDPath.decodedObject.numberLastTagOctet+wrapperClassForSNMPMessage.decodedOIDPath.decodedObject.lenght+1, OIDPathList.size()-1);
        wrapperClassForSNMPMessage.snmpMessage.snmpPDU.variableBindingsList.value = dekoderBER.decodeBitStream(wrapperClassForSNMPMessage.varbindValueList);


        System.out.println("=================================");
        System.out.println("decodedSnmpMsg.visibility: " + wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject.visibility);
        System.out.println("decodedSnmpMsg.lenght: " + wrapperClassForSNMPMessage.decodedSnmpMsg.decodedObject.lenght);
        System.out.println("---------------------------------");
        System.out.println("snmpMsg.version: " + wrapperClassForSNMPMessage.snmpMessage.version);
        System.out.println("snmpMsg.communityName: " + wrapperClassForSNMPMessage.snmpMessage.communityName);
        System.out.println("---------------------------------");
        System.out.println("decodedSnmpPDU.visibility: " + wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.visibility);
        System.out.println("decodedSnmpPDU.lenght: " + wrapperClassForSNMPMessage.decodedSnmpPDU.decodedObject.lenght);
        System.out.println("---------------------------------");
        System.out.println("snmpMsg.snmpPDU.typePDU: " + wrapperClassForSNMPMessage.snmpMessage.snmpPDU.typePDU);
        System.out.println("---------------------------------");
        System.out.println("snmpMsg.snmpPDU.requestID: " + wrapperClassForSNMPMessage.snmpMessage.snmpPDU.requestID);
        System.out.println("snmpMsg.snmpPDU.errorStatus: " + TypeErrorStatus.get((long)wrapperClassForSNMPMessage.snmpMessage.snmpPDU.errorStatus) + "("+ wrapperClassForSNMPMessage.snmpMessage.snmpPDU.errorStatus + ")");
        System.out.println("snmpMsg.snmpPDU.errorIndex: " +  wrapperClassForSNMPMessage.snmpMessage.snmpPDU.errorIndex);
        System.out.println("---------------------------------");
        System.out.println("decodedSnmpPDUVarbindList.visibility: " + wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject.visibility);
        System.out.println("decodedSnmpPDUVarbindList.lenght: " + wrapperClassForSNMPMessage.decodedSnmpPDUVarbindList.decodedObject.lenght);
        System.out.println("---------------------------------");
        System.out.println("decodedSnmpPDUVarbind.visibility: " + wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject.visibility);
        System.out.println("decodedSnmpPDUVarbind.lenght: " + wrapperClassForSNMPMessage.decodedSnmpPDUVarbind.decodedObject.lenght);
        System.out.println("---------------------------------");
        System.out.println("snmpMsg.snmpPDU.variableBindingsList.OID: " + wrapperClassForSNMPMessage.snmpMessage.snmpPDU.variableBindingsList.OID);
        System.out.println("---------------------------------");
        System.out.println("snmpMsg.snmpPDU.variableBindingsList.value: ");                                                 //todo wartosci value jako sekwencje
                            wrapperClassForSNMPMessage.snmpMessage.snmpPDU.variableBindingsList.value.present();
        System.out.println("=================================");
//
//        WrapperClassForSNMPMessage wrapp = new WrapperClassForSNMPMessage(snmpMsg, reciveMsg);
        RecivedSNMPMessage.put((long) wrapperClassForSNMPMessage.snmpMessage.snmpPDU.requestID, wrapperClassForSNMPMessage);

        return wrapperClassForSNMPMessage;
    }

    // kodowanie
    public static List<BitSet> createPackageSNMPMessage(WrapperClassForSNMPMessage decodedSNMPMsg) {
        List<BitSet> snmpPkg = new ArrayList<>();

        snmpPkg.addAll(codeGetRequest(decodedSNMPMsg));





        return snmpPkg;
    }

}



//todo dodac sprawdzenie czy ramka nie jest dluzsza niz 484 oktety
//todo 1.3.6.1.2.1.2.2.1.2.0