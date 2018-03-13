package PkgDekoderBER;
import PkgKoderBER.koderBER;
import java.util.*;

public class dekoderBER {
    private Map<Long, String> tagNumberOfSimpleTag = new HashMap<Long, String>();
    private Map<Long, String> visibilityOfObject = new HashMap<Long, String>();


    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    int keySize;

    public  BitSet getBitSet(String key) {
        char[] cs = new StringBuilder(key).toString().toCharArray();
//        BitSet result = new BitSet(keySize);
        BitSet result = new BitSet(keySize);
        int m = Math.min(keySize, cs.length);

        for (int i = 0; i < m; i++)
            if (cs[i] == '1') result.set(i);

        return result;
    }

    public  BitSet getReverseBitSet(String key) {
        char[] cs = new StringBuilder(key).reverse().toString().toCharArray();
        BitSet result = new BitSet(keySize);
        int m = Math.min(keySize, cs.length);

        for (int i = 0; i < m; i++)
            if (cs[i] == '1') result.set(i);

        return result;
    }



    public dekoderBER() {
        tagNumberOfSimpleTag.put((long) 2, "INTEGER");
        tagNumberOfSimpleTag.put((long) 3, "BIT STRING");
        tagNumberOfSimpleTag.put((long) 4, "OCTET STRING");
        tagNumberOfSimpleTag.put((long) 5, "NULL");
        tagNumberOfSimpleTag.put((long) 6, "OBJECT IDENTIFIER");
        tagNumberOfSimpleTag.put((long) 16, "SEQUENCE");
        tagNumberOfSimpleTag.put((long) 16, "SEQUENCE OF");
        tagNumberOfSimpleTag.put((long) 17, "SET");
        tagNumberOfSimpleTag.put((long) 17, "SET OF");
        tagNumberOfSimpleTag.put((long) 19, "PrintableString");
        tagNumberOfSimpleTag.put((long) 20, "TG1String");
        tagNumberOfSimpleTag.put((long) 22, "IA5String");
        tagNumberOfSimpleTag.put((long) 23, "UTCTime");

        visibilityOfObject.put((long) 0, "universal");
        visibilityOfObject.put((long) 1, "application");
        visibilityOfObject.put((long) 2, "context-specific");
        visibilityOfObject.put((long) 3, "private");

    }

    public String decodeVisibility(BitSet firstOctet) {
        String result = null;
        StringBuilder str = new StringBuilder();
        str.append(firstOctet.get(0, 2));
        String visibilityTag = koderBER.convertBitSetToString(firstOctet);
        visibilityTag = visibilityTag.substring(0, 2);
//        System.out.println("visibilityTag: " + visibilityTag);
        keySize = visibilityTag.length();
//        System.out.println(this.convert(this.getReverseBitSet(visibilityTag)));

        long key = this.convert(this.getReverseBitSet(visibilityTag));
        result = visibilityOfObject.get(key);
//        System.out.println("Key: " + key + " value: " + result);
        return result;
    }

    private String decodeComplexity(BitSet firstOctet) {
        String result =  null;
        if(firstOctet.get(2)) {
            result = "Constructed";
        } else {
            result = "Primitive";
        }
        return result;
    }

    private String decodeTag(List<BitSet> bitStreamToDecode, DecodedObjectFromBitStream decodedObjectFromBitStream ){
        String result;
        BitSet shortBitSet;
        boolean application = false;
        if(decodedObjectFromBitStream.visibility.equals("application")){
            application = true;
        }

        String shortB = koderBER.convertBitSetToString(bitStreamToDecode.get(0).get(3, 8));
        shortB = shortB.substring(0, 5);
        shortBitSet = koderBER.getReverseBitSet(shortB);
        long tagValue = convert(shortBitSet);

        if(tagValue != 31){
            result = tagNumberOfSimpleTag.get(tagValue);
            if(result == null){
                if(application) {
                    result = String.valueOf(tagValue);
                } else {
                    result = "BRAK TAGU O NA LISCIE O PODANEJ WARTOSCI: " + tagValue + "!";
                }
            }
        } else {
//            todo tagi posiadajace wartosci wieksze niz 31
            StringBuilder str = new StringBuilder();
            String temp;
            for( decodedObjectFromBitStream.numberLastTagOctet = 1; decodedObjectFromBitStream.numberLastTagOctet <= bitStreamToDecode.size()-1; decodedObjectFromBitStream.numberLastTagOctet++) {
//                jeżeli pierwszy bit octetu jest 1
                    temp = koderBER.convertBitSetToString(bitStreamToDecode.get(decodedObjectFromBitStream.numberLastTagOctet));
                    temp = temp.substring(1, 8);
//                    System.out.println("temp: " + temp);
                    str.append(temp);
//                    System.out.println(str);
//                    System.out.println(k);
                if (!bitStreamToDecode.get(decodedObjectFromBitStream.numberLastTagOctet).get(0)) {
                    break;
                }
            }
            //todo działa - sprawdzic poprawnosc dla wiekszej ilosci danych - działa poprawnie!
//            System.out.println(str.toString());
            this.keySize = str.length();
//            System.out.println("Wartosc tagu wieksza niz 31: " + convert(this.getReverseBitSet(str.toString())));
//            System.out.println("Wartosc tagu wieksza niz 31: " + this.getReverseBitSet(str.toString()));
            tagValue =  convert(this.getReverseBitSet(str.toString()));
            result = tagNumberOfSimpleTag.get(tagValue);
            if(result == null){
                if(application) {
                    result = String.valueOf(tagValue);
                } else {
                    result = "BRAK TAGU O NA LISCIE O PODANEJ WARTOSCI: " + tagValue + "!";
                }
            }
        }
        return result;
    }

    public int decodeLength(List<BitSet> bitStreamToDecode, DecodedObjectFromBitStream decodedObjectFromBitStream){
        int resultLength = -1;
        StringBuilder lengthOctets = new StringBuilder();
        if(!bitStreamToDecode.get(decodedObjectFromBitStream.numberLastTagOctet+1).get(0)){
            decodedObjectFromBitStream.numberLastTagOctet += 1;
            //todo pierwszy bit w octecie rowny '0'/dlugosc danych mniejsza niz 127
            lengthOctets.append(koderBER.convertBitSetToString(bitStreamToDecode.get(decodedObjectFromBitStream.numberLastTagOctet)));
//            System.out.println("StringBuilder Length: " + lengthOctets.toString());
            keySize = lengthOctets.length();
            resultLength = (int) convert(this.getReverseBitSet(lengthOctets.toString()));
//            System.out.println("Przekonwertowana wartosc dlugosci danych: " + resultLength);
        } else {
            decodedObjectFromBitStream.numberLastTagOctet += 1;
            lengthOctets.append(koderBER.convertBitSetToString(bitStreamToDecode.get(decodedObjectFromBitStream.numberLastTagOctet)));
//            System.out.println("StringBuilder Length: " + lengthOctets.toString());
            String K;
            K = lengthOctets.toString().substring(1,8);
//            System.out.println("K: " + K);
            resultLength = (int) convert(this.getReverseBitSet(K));
            if(resultLength == 0){
                //todo EOC
                resultLength = 0;

            } else {
                //todo dane okreslonej dlugosci
                lengthOctets.setLength(0);
                int indexOfLastOctet = (resultLength + decodedObjectFromBitStream.numberLastTagOctet);
                for(int l = decodedObjectFromBitStream.numberLastTagOctet+1; l <= indexOfLastOctet; l++) {
                    lengthOctets.append(koderBER.convertBitSetToString(bitStreamToDecode.get(l)));
                    decodedObjectFromBitStream.numberLastTagOctet = l;
                }
                keySize = lengthOctets.length();
                resultLength = (int) convert(this.getReverseBitSet(lengthOctets.toString()));
//                System.out.println("StringBuilder K oktetów: " + lengthOctets.toString());
            }



        }

        return resultLength;
    }

    private String decodeOctetString(List<BitSet> bitStreamToDecode, int indexFirtsOfDataOctet, int lastIndexOfDataOctet) {
        String resultData = null;
        StringBuilder str = new StringBuilder();
//        int indexFirtsOfDataOctet = (bitStreamToDecode.size()-decodedObjectFromBitStream.lenght);
//        int lastIndexOfDataOctet = (bitStreamToDecode.size()-1);

            keySize = 8;
            for(int d = indexFirtsOfDataOctet; d <= lastIndexOfDataOctet; d++){
                String bitSetToRev = koderBER.convertBitSetToString(bitStreamToDecode.get(d));
                int asciiValue = (int) convert(getReverseBitSet(bitSetToRev));
//                    System.out.println("asciiValue: " + asciiValue);
                str.append(Character.toString((char) asciiValue));
            }
            resultData = str.toString();
            return resultData;
    }

    private String decodeInteger(List<BitSet> bitStreamToDecode, int indexFirtsOfDataOctet, int lastIndexOfDataOctet) {
        String resultData = null;
        StringBuilder str = new StringBuilder();
//        int indexFirtsOfDataOctet = (bitStreamToDecode.size()-decodedObjectFromBitStream.lenght);
//        int lastIndexOfDataOctet = (bitStreamToDecode.size()-1);

        for(int d = indexFirtsOfDataOctet; d <= lastIndexOfDataOctet; d++){
            str.append(koderBER.convertBitSetToString(bitStreamToDecode.get(d)));
        }
        keySize = str.length();
        resultData = String.valueOf(convert(getReverseBitSet(str.toString())));

        return resultData;
    }

    private String decodeNotSpecifyDataLength(List<BitSet> bitStreamToDecode, DecodedObjectFromBitStream decodedObjectFromBitStream) {
        String resultData = null;
        int diffrenceBetweenEOCLength = bitStreamToDecode.size() - (decodedObjectFromBitStream.numberLastTagOctet + 3);
        StringBuilder str = new StringBuilder();
        str.append(koderBER.convertBitSetToString(bitStreamToDecode.get(bitStreamToDecode.size() - 1)));
        str.append(koderBER.convertBitSetToString(bitStreamToDecode.get(bitStreamToDecode.size() - 2)));
        keySize = str.length();
        int validEOCOctets = (int) convert(this.getReverseBitSet(str.toString()));

        if (decodedObjectFromBitStream.identityTag.equals("NULL")){
            resultData = "Brak danych do zdekodowania!";
        } else if (validEOCOctets != 0) {
            resultData = "Błędnie odebrana ramka!";
        } else {
            if (diffrenceBetweenEOCLength != 1) {
                int indexFirstDataOctet = decodedObjectFromBitStream.numberLastTagOctet + 1;
                int indexLastDataOctet = bitStreamToDecode.size() - 3;
                if (decodedObjectFromBitStream.identityTag.equals("OCTET STRING")) {
                    resultData = decodeOctetString(bitStreamToDecode, indexFirstDataOctet, indexLastDataOctet);
                } else {
                    resultData = decodeInteger(bitStreamToDecode, indexFirstDataOctet, indexLastDataOctet);
                }
            }
        }

        return resultData;
    }

    private String decodeData(List<BitSet> bitStreamToDecode, DecodedObjectFromBitStream decodedObjectFromBitStream) {
        String resultData = null;
        if(decodedObjectFromBitStream.lenght != 0){

            int indexFirtsOfDataOctet = decodedObjectFromBitStream.numberLastTagOctet + 1;
            int lastIndexOfDataOctet = indexFirtsOfDataOctet + decodedObjectFromBitStream.lenght - 1;

            if (lastIndexOfDataOctet != bitStreamToDecode.size()-1){
                List<BitSet> nestedNestedBitSetList = new ArrayList<>();

                for(int k = lastIndexOfDataOctet+1; k <= bitStreamToDecode.size()-1; k++){
                    nestedNestedBitSetList.add(bitStreamToDecode.get(k));
                }
                decodeBitStream(nestedNestedBitSetList);

            }

            if(decodedObjectFromBitStream.identityTag.equals("OCTET STRING")){
                  resultData = decodeOctetString(bitStreamToDecode, indexFirtsOfDataOctet, lastIndexOfDataOctet);
            } else {
                //todo INTEGER
                resultData = decodeInteger(bitStreamToDecode, indexFirtsOfDataOctet, lastIndexOfDataOctet);
            }
        } else {
            //todo dlugosc pola length wynosi '0'
            resultData = decodeNotSpecifyDataLength(bitStreamToDecode, decodedObjectFromBitStream);
        }
        return resultData;
    }

    public String decodeOIDPath(List<BitSet> bitStreamToDecode, int indexFirtsOfDataOctet, int lastIndexOfDataOctet) {
        String pathToOID = new String();

        for(int i = indexFirtsOfDataOctet; i <= lastIndexOfDataOctet; i++) {
            if (i == indexFirtsOfDataOctet){
               BitSet splitBitSet =  bitStreamToDecode.get(i);
               splitBitSet.flip(2);
               splitBitSet.flip(4);
               keySize = 8;
               BitSet reversedSplitBitSet = getReverseBitSet(koderBER.convertBitSetToString(splitBitSet));
               pathToOID = "1." + String.valueOf(convert(reversedSplitBitSet)) + ".";
               splitBitSet.flip(2);
               splitBitSet.flip(4);

            } else if(i != lastIndexOfDataOctet) {
                pathToOID = pathToOID + decodeInteger(bitStreamToDecode, i, i) + ".";
            } else {
                pathToOID = pathToOID + decodeInteger(bitStreamToDecode, i, i);
            }
        }
        return pathToOID;
    }


    public DecodedObjectFromBitStream decodeBitStream(List<BitSet> inputCodedObject) {
        DecodedObjectFromBitStream decodedObjectFromBitStream = new DecodedObjectFromBitStream();
            decodedObjectFromBitStream.visibility = decodeVisibility(inputCodedObject.get(0));
            decodedObjectFromBitStream.complexity = decodeComplexity(inputCodedObject.get(0));
            decodedObjectFromBitStream.identityTag = decodeTag(inputCodedObject, decodedObjectFromBitStream);
            decodedObjectFromBitStream.lenght = decodeLength(inputCodedObject, decodedObjectFromBitStream);
            if(decodedObjectFromBitStream.complexity.equals("Primitive")) {
                decodedObjectFromBitStream.data = decodeData(inputCodedObject, decodedObjectFromBitStream);
            } else {
                List<BitSet> nestedObjectBitSetList = new ArrayList<>();
                for(int k = decodedObjectFromBitStream.numberLastTagOctet+1; k <= inputCodedObject.size()-1; k++){
                    nestedObjectBitSetList.add(inputCodedObject.get(k));
                }
                decodeBitStream(nestedObjectBitSetList);
                StringBuilder str = new StringBuilder();
                for(BitSet x : nestedObjectBitSetList){
                    str.append(koderBER.convertBitSetToString(x) + " ");
                }
                System.out.println(str.toString());
            }

            decodedObjectFromBitStream.present();
            System.out.println();


        return decodedObjectFromBitStream;
    }

    public DecodedObjectFromBitStream decodedOID(List<BitSet> inputCodedObject) {
        DecodedObjectFromBitStream decodedObjectFromBitStream = new DecodedObjectFromBitStream();
            decodedObjectFromBitStream.visibility = decodeVisibility(inputCodedObject.get(0));
            decodedObjectFromBitStream.lenght = decodeLength(inputCodedObject, decodedObjectFromBitStream);
            int lowIndex = decodedObjectFromBitStream.numberLastTagOctet + 1;
            int highIndex = lowIndex + decodedObjectFromBitStream.lenght - 1;
            decodedObjectFromBitStream.data = decodeOIDPath(inputCodedObject, lowIndex, highIndex);
        return decodedObjectFromBitStream;
    }
}


//todo kodowanie tagow aplikacji - działa poprawnie!
//todo poprawic kodowanie null - działa poprawnie!
//todo przeniesc wszystkie funkcje konwertujace do osobnego pliku
//todo przy dekodowaniu obiektu application odszukac po tagID jakiego typu dane przechowuje
//todo dekodowanie danych dla sekwencji (nie zgadzają sie indeksy początkowe i koncowe)

//todo dodanie List<List<BitSet>> do kodera i dekodowanie kolejnych bitstreamow
