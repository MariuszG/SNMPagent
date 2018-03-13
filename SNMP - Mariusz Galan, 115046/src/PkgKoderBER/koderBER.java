package PkgKoderBER;

import sharedClass.TypeOfData;

import java.util.*;

import static ParseMIB.MyTree.dataTypeMap;
import static java.lang.Math.max;
import static java.lang.Math.pow;


// TODO kodowanie typow sekwencji
// TODO wybieranie object-typow do kodowania
// TODO dodac zwracanie argumentow z funkcji


public class koderBER {
    public static List<TypeOfData> parsedTypeOfData = new ArrayList<TypeOfData>();
    public static Map<String, BitSet> classTypeList = new HashMap<String, BitSet>();
    public static Map<String, BitSet> tagTypeList = new HashMap<>();
//    Map<String, Byte> classTypeList = new HashMap<String, Byte>();
//    Map<String, Byte> tagTypeList = new HashMap<String, Byte>();

    public static int keySize = 8;

    public static String convertBitSetToString(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            s.append(bitSet.get(i) == true ? 1 : 0);
        }
        return s.toString();
    }

    public String convertBitSetToStringRev(BitSet bitSet) {
        StringBuilder s = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            s.append(bitSet.get(i) == true ? 1 : 0);
        }
        return s.toString();
    }

    public static BitSet reverseBitSet(BitSet bitSet) {
        BitSet reversedBitSet = new BitSet(8);
        for (int i = 0; i < 8; i++) {
            if (bitSet.get(i) == true) {
                reversedBitSet.set(7 - i);
            }
        }
        return reversedBitSet;
    }

    public static BitSet getReverseBitSet(String key) {
        char[] cs = new StringBuilder(key).reverse().toString().toCharArray();
        BitSet result = new BitSet(keySize);
        int m = Math.min(keySize, cs.length);

        for (int i = 0; i < m; i++)
            if (cs[i] == '1') result.set(i);

        return result;
    }

    public static BitSet getBitSet(String key) {
        char[] cs = new StringBuilder(key).toString().toCharArray();
//        BitSet result = new BitSet(keySize);
        BitSet result = new BitSet(keySize);
        int m = Math.min(keySize, cs.length);

        for (int i = 0; i < m; i++)
            if (cs[i] == '1') result.set(i);

        return result;
    }

    public static List<BitSet> getBitSetStream(String keyStream){
        List<BitSet> resultList = new ArrayList<>();
        char keyStreamChar[] = keyStream.toCharArray();

        for(char x : keyStreamChar){
            for(BitSet y : getU2Value((long) x)){
                resultList.add(reverseBitSet(y));
            }
        }

        return resultList;
    }

    public static String getBitSet(BitSet key) {
        StringBuilder sb = new StringBuilder();
        int m = Math.min(keySize, key.size());

        for (int i = 0; i < m; i++)
            sb.append(key.get(i) ? '1' : '0');

        return sb.reverse().toString();
    }

    static {
        classTypeList.put("universal", getBitSet("00000000"));
        classTypeList.put("APPLICATION", getBitSet("01000000"));
        classTypeList.put("context-specific", getBitSet("10000000"));
        classTypeList.put("private", getBitSet("11000000"));

        tagTypeList.put("INTEGER", getBitSet("00000010"));
        tagTypeList.put("OCTET STRING", getBitSet("00000100"));
        tagTypeList.put("NULL", getBitSet("00000101"));
        tagTypeList.put("OBJECT IDENTIFIER", getBitSet("00000110"));
        tagTypeList.put("SEQUENCE", getBitSet("00010000")); // sequence
        tagTypeList.put("SEQUENCE OF", getBitSet("00010000")); // sequence of
    }

    public koderBER() {

        //        classTypeList.put("universal", new Byte("00"));
//        classTypeList.put("application-specific", new Byte("01"));
//        classTypeList.put("context-specific", new Byte("10"));
//        classTypeList.put("private", new Byte("11"));




// classTypeList.put("universal", new BitSet(8).set(););
//        classTypeList.put("application-specific", new Byte("01"));
//        classTypeList.put("context-specific", new Byte("10"));
//        classTypeList.put("private", new Byte("11"));





    }

    public koderBER(List<TypeOfData> parsedTypeOfData) {
        new koderBER();
        this.parsedTypeOfData = parsedTypeOfData;

    }

    public static BitSet convert(long value) {
        return convert(value, -1);
    }

    public static BitSet convert(long value, int length) {
        BitSet bits;
        if (length == -1) {
            bits = new BitSet();
        } else {
            bits = new BitSet(length);
        }
        int index = 0;
        while (value != 0L) {
            if (value % 2L != 0) {
                bits.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        return bits;
    }

    public static List<BitSet> getU2Value(Long value) {
        List<BitSet> u2 = new ArrayList<>();
        BitSet val = convert(value);
        if (value >= 0) {
            for (int i = 0; i < value; i++) {
                BitSet set = convert(value & 255);
                value >>= 8;
                u2.add(0, set);
            }
            if (val.length() % 8 == 0) {
                u2.add(0, convert(0, 8));
            }
        } else {
            do {
                BitSet set = convert(value & 255);
                value >>= 8;
                u2.add(0, set);
            } while ((value & 255) != 255);
            if ((value & 255) == 255) {
                BitSet set = convert(value & 255);
                u2.add(0, set);
            }
        }
        return u2;
    }

    public void addDataTypeList(List<TypeOfData> parsedTypeOfData) {
        this.parsedTypeOfData.addAll(parsedTypeOfData);
    }

    public static TypeOfData findTypeOfData(String name) {
        TypeOfData foundedTypeOfData = null;
        for (TypeOfData x : parsedTypeOfData) {
            if (x.name.equals(name)) {
                foundedTypeOfData = x;
                System.out.println("findDataType:");
                x.present();
            }
        }
        return foundedTypeOfData;
    }

    public static BitSet codeIdentyfiyFrame(TypeOfData msgToEncrypt) {
        BitSet identyfiyFrame = new BitSet(8);

        // universal, application etc.
        if (classTypeList.get(msgToEncrypt.visibility) != null) {
            identyfiyFrame.or(classTypeList.get(msgToEncrypt.visibility));
        } else {
            identyfiyFrame.or(classTypeList.get("universal"));
        }
        // typ prosty
        if (tagTypeList.containsKey(msgToEncrypt.name)) {
            // kodowanie genericType
//            if (msgToEncrypt.parentTypes.size() == 0 ) {
            System.out.println("parent type size 0");
            System.out.println(convertBitSetToString(classTypeList.get(msgToEncrypt.visibility)) + " " + "0" + " " + convertBitSetToString(tagTypeList.get(msgToEncrypt.name)));
            identyfiyFrame.or(tagTypeList.get(msgToEncrypt.name));
            // typ zlozony '1'

        } else {
            //identyfiyFrame.set(2);
//            System.out.println("wartosc zwracana przez convertBit(complex): " + convertBitSetToString(complexFrame(msgToEncrypt)));
            if (msgToEncrypt.parentTypes.size() == 0) {
                identyfiyFrame.or(complexFrame(msgToEncrypt));
            } else {
                identyfiyFrame.or(complexFrame(msgToEncrypt));
// TODO parent type
//                for(int i = 0; i < msgToEncrypt.getParentTypes().size(); i++) {
//                    System.out.println("parentType struct: " + msgToEncrypt.parentTypes.get(i).name);
//                    System.out.println("parentType struct: " + msgToEncrypt.parentTypes.get(i).genericType);
//                    codeIdentyfiyFrame(findTypeOfData(msgToEncrypt.parentTypes.get(i).genericType.trim()));
//                }
            }
//            System.out.println(convertBitSetToString(classTypeList.get(msgToEncrypt.visibility)) + " " + "1" + " " +  convertBitSetToString(tagTypeList.get(msgToEncrypt.name)));
        }
        System.out.println("Tag Frame " + msgToEncrypt.name + " " + convertBitSetToString(identyfiyFrame));
        return identyfiyFrame;
    }

    private static BitSet complexFrame(TypeOfData objToEncrypt) {
        BitSet result = new BitSet(8);
        StringBuilder s = new StringBuilder();
        if (objToEncrypt.visibility != null || (objToEncrypt.visibility != null &&  objToEncrypt.visibility.equals("APPLICATION")) ) {
            if (objToEncrypt.typeId != null) {
//                System.out.println("Konwersja typeId: " + Integer.toBinaryString(Integer.parseInt(objToEncrypt.typeId)));
//                System.out.println(Integer.toBinaryString(Integer.parseInt(objToEncrypt.typeId)));
//                result = getBitSet(Integer.toBinaryString(Integer.parseInt(objToEncrypt.typeId)));
                int size = Integer.toBinaryString(Integer.parseInt(objToEncrypt.typeId)).length();
                for (int i = 0; i < 8 - size; i++) {
                    s.append("0");
                }
                s.append(Integer.toBinaryString(Integer.parseInt(objToEncrypt.typeId)));
//                System.out.println("String builder: " + s);
                result = getBitSet(s.toString());
                if(!objToEncrypt.keyWord.equals("IMPLICIT")) {
                    result.set(2);
                }
//                System.out.println("complex result: " + convertBitSetToString(result));
            } else {
                System.out.println("Brak type id");
                result.set(2);
            }
        } else if (objToEncrypt.getParentTypes().size() > 1) {
            result = tagTypeList.get("SEQUENCE");
            result.set(2);
        } else if (classTypeList.get(objToEncrypt.visibility) != null) {
            // universal, application etc.
                result.or(classTypeList.get(objToEncrypt.visibility));
                result.set(2);
        } else {
            result = tagTypeList.get("OBJECT IDENTIFIER");
//            System.out.println("Nie mozliwe kodowanie podanego typu zlozonego!");
        }

        return result;
    }

    public static List<BitSet> lengthFrame(String size) {
        List<BitSet> lenghtBS = new ArrayList<>();// = new BitSet(8);
        List<BitSet> retLenghtBS = new ArrayList<>();// = new BitSet(8);

        int sizeInt = Integer.parseInt(size);

//        lenghtBS = convert((long) Long.parseLong(size));
//          lenghtBS = reverseBitSet(BitSet.valueOf(new long[]{sizeInt}));


        int L = sizeInt;
        if (L < 128) { // powyzej 2^1008?
            lenghtBS.add(convert(L));
        } else {
            int K = (L / 255) + 1;
            BitSet bs = convert(128 + K);
            lenghtBS.add(bs);
            for (int i = 0; i < K; i++) {
                BitSet set = convert(L & 255);
                L >>= 8;
                lenghtBS.add(1, set);
            }
        }

        System.out.print("length: ");
        for (BitSet x : lenghtBS) {
            retLenghtBS.add(reverseBitSet(x));
            System.out.print(convertBitSetToString(reverseBitSet(x)) + " ");
        }
        System.out.println("");
        return retLenghtBS;
    }

    public static boolean validInputData(int data, int byteCon) {
        boolean retVal = false;
        double power = (((8 * byteCon)));
        if (data <= ((pow(2, power) - 1))) {
            retVal = true;
        } else {
            System.out.println("Wprowadzona wartość jest niepoprawna! Wprowadz nową");
            retVal = false;
        }
        return retVal;
    }

    public int convertIntToNumberBits(long number) {
        int numberBit = -1;
        int numberPow = -1;

//        System.out.println((int)(pow(2.0, 31)-1));
        while (number != (long) (pow(2.0, numberPow) - 1)) {
            numberPow += 1;
//                System.out.println(numberBit);
        }

        numberBit = convertByteToNumberBits(numberPow);
//        System.out.println("nbBit: " + numberBit);
        return numberBit;

    }

    public int convertByteToNumberBits(int number) {
        int numberBit = -1;
        int numberPow = -1;

//        System.out.println((int)(pow(2.0, 31)-1));

        while (number != (int) (pow(2.0, numberBit))) {
            if (numberBit <= number) {
                numberBit += 1;
            } else {
                numberBit = -1;
                break;
            }
//                System.out.println(numberBit);
        }

        return numberBit;

    }

    public List<BitSet> countConstrains(TypeOfData objToEncrypt) {
        List<BitSet> returnVal = new ArrayList<>();
        if (objToEncrypt.constrains.byteConstrain != null) {
            System.out.println("byte cons");
            returnVal.addAll(lengthFrame(objToEncrypt.constrains.byteConstrain));
        } else if (objToEncrypt.constrains.minValConstrain != null) {
            System.out.println("min max cons");
            returnVal.addAll(lengthFrame(objToEncrypt.constrains.maxValConstrain));
        }
        return returnVal;
    }

    public static String getDataFromUserInput() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Wprowadz dane do zakodowania: ");
        String userInput = scan.next();
        char charTable[] = userInput.toCharArray();

//        for (char x: charTable) {
//            System.out.println("Ascii: " + (int) x);
//        }

//        System.out.println("Konwersja na double");
//        Long.parseLong(userInput);
//        Double.parseDouble(userInput);


        return userInput;
    }

    public static List<BitSet> getDataToFrame() {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;

        System.out.println("Wprowadz dane do zakodowania: ");
//            String s = scan.next();
        int input = scan.nextInt();

        u2ListToReverse = getU2Value((long) input);

//        if(!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }

        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public static List<BitSet> getDataToFrame(String byteCon) {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;

//        System.out.println("Wprowadz dane do zakodowania: ");
//            String s = scan.next();
        int input = -1;

        while (!valiData) {
//            input = scan.nextInt();
            input = Integer.parseInt(getDataFromUserInput());
            valiData = validInputData(input, Integer.parseInt(byteCon));
        }

        u2ListToReverse = getU2Value((long) input);

        if (!byteCon.equals("null")) {
            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
                BitSet temp = new BitSet(8);
                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
                    u2List.add(temp);
                }
            }
        }
        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public static List<BitSet> getDataToFrameSimple(String typeObjectValue) {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;

//        System.out.println("Wprowadz dane do zakodowania: ");
//            String s = scan.next();
        int input = -1;

//        while (!valiData) {
//            input = scan.nextInt();
            input = Integer.parseInt(typeObjectValue);
//            valiData = validInputData(input, Integer.parseInt(byteCon));
//        }

        u2ListToReverse = getU2Value((long) input);

//        if (!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }
        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public static List<BitSet> getDataToFrameString(String byteCon) {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;
        List<Integer> inputIntList = new ArrayList<Integer>();



        while (!valiData) {
//            input = scan.nextInt();
           char input[] = getDataFromUserInput().toCharArray();

            if ((input.length > Integer.parseInt(byteCon)) || (input.length < Integer.parseInt(byteCon))) {
                System.out.println("Wprowadz dane o poprawnej długości.");
                valiData = false;
            } else {
                valiData = true;
                for (char x: input) {
//                    System.out.println("Ascii: " + (int) x);
                    inputIntList.add((int) x);
                }
            }
        }

            for (int x : inputIntList) {
                u2ListToReverse.addAll(getU2Value((long) x));
            }
//        if (!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }

        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public static List<BitSet> getDataToFrameString(String byteCon, String setVal) {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;
        List<Integer> inputIntList = new ArrayList<Integer>();



        while (!valiData) {
//            input = scan.nextInt();
           char input[] = setVal.toCharArray();

            if ((input.length > Integer.parseInt(byteCon)) || (input.length < Integer.parseInt(byteCon))) {
                System.out.println("Wprowadz dane o poprawnej długości.");
                valiData = false;
            } else {
                valiData = true;
                for (char x: input) {
//                    System.out.println("Ascii: " + (int) x);
                    inputIntList.add((int) x);
                }
            }
        }

            for (int x : inputIntList) {
                u2ListToReverse.addAll(getU2Value((long) x));
            }
//        if (!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }

        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public List<BitSet> getDataToFrameString() {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;
        List<Integer> inputIntList = new ArrayList<Integer>();



        for (int x : inputIntList) {
            u2ListToReverse.addAll(getU2Value((long) x));
        }
//        if (!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }

        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
//            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public static List<BitSet> getDataToFrame(String minCon, String maxCon) {
        Scanner scan = new Scanner(System.in);
        List<BitSet> u2ListToReverse = new ArrayList<>();
        List<BitSet> u2List = new ArrayList<>();
        boolean valiData = false;

//        System.out.println("Wprowadz dane do zakodowania: ");
//            String s = scan.next();
        int input = -1;



        long lowBorder = Long.parseLong(minCon);
        long highBorder = Long.parseLong(maxCon);


        while (!valiData) {
            System.out.println("Wprowadź dane do zakodowania: ");
            input = scan.nextInt();
            if (input > lowBorder && input < highBorder) {
                valiData = true;
            } else {
                System.out.println("Wprowadzona wartość jest niepoprawna!");
                valiData = false;
            }

        }

        u2ListToReverse = getU2Value((long) input);

//        if(!byteCon.equals("null")) {
//            if (u2ListToReverse.size() < Integer.parseInt(byteCon)) {
//                BitSet temp = new BitSet(8);
//                for (int i = 0; i < (Integer.parseInt(byteCon) - u2ListToReverse.size()); i++) {
//                    u2List.add(temp);
//                }
//            }
//        }

        for (BitSet x : u2ListToReverse) {
            u2List.add(reverseBitSet(x));
            System.out.println(convertBitSetToString(reverseBitSet(x)));
        }
//        for(BitSet x : u2List) {
////                System.out.print(convertBitSetToStringRev(x) + " " );
//            frame.add(x);
//        }
//        System.out.println("");
        return u2List;
    }

    public List<BitSet> addEOC() {
        List<BitSet> eocList = new ArrayList<>();
        BitSet empty = new BitSet(8);
        eocList.add(empty);
        eocList.add(empty);
        return eocList;
    }

    private static boolean byteConstrainsBool(TypeOfData objectTypeOfData) {
        return objectTypeOfData.constrains.byteConstrain != null || (objectTypeOfData.constrains.byteConstrain != null && !objectTypeOfData.constrains.byteConstrain.equals("null"));
    }

    private static boolean minMaxConstrainsBool(TypeOfData objectTypeOfData) {
        boolean empty = (objectTypeOfData.constrains.minValConstrain != null && objectTypeOfData.constrains.maxValConstrain != null);
        boolean minEmpty = (objectTypeOfData.constrains.minValConstrain != null && !objectTypeOfData.constrains.minValConstrain.equals("null"));
        boolean maxEmpty = (objectTypeOfData.constrains.maxValConstrain != null && !objectTypeOfData.constrains.maxValConstrain.equals("null"));
//        boolean emptyString = (!objectTypeOfData.constrains.minValConstrain.equals("null") && !objectTypeOfData.constrains.maxValConstrain.equals("null"));

//        System.out.println(minEmpty);
//        System.out.println(maxEmpty);
//        System.out.println((minEmpty && maxEmpty));
        return ((minEmpty && maxEmpty));
    }

    private static List<BitSet> getDataWithConstrains(TypeOfData selectedTypeOfData) {
        List<BitSet> u2List = new ArrayList<>();
        if (byteConstrainsBool(selectedTypeOfData)) {
            u2List.addAll(getDataToFrame(selectedTypeOfData.constrains.byteConstrain));
        } else if (minMaxConstrainsBool(selectedTypeOfData)) {
            u2List.addAll(getDataToFrame(selectedTypeOfData.constrains.minValConstrain, selectedTypeOfData.constrains.maxValConstrain));
        } else {
            u2List.addAll(getDataToFrame());
        }

        return u2List;
    }

    public List<BitSet> codeFrame() {
        Scanner scan = new Scanner(System.in);
        TypeOfData selectedTypeOfData;
        List<BitSet> u2List = new ArrayList<>();
        List<BitSet> frame = new ArrayList<>();

        System.out.println("Wybierz nr typu do zakodowania: ");
        int nbType = scan.nextInt();

        selectedTypeOfData = parsedTypeOfData.get(nbType);
//        selectedTypeOfData = dataTypeMap.get(nbType);
        selectedTypeOfData.present();
        minMaxConstrainsBool(selectedTypeOfData);

        if (selectedTypeOfData.getParentTypes().size() != 0) {
            for (DataStructure x : selectedTypeOfData.getParentTypes()) {
                TypeOfData nestedTypeOfData = findTypeOfData(x.genericType.trim());
                List<BitSet> nestedU2List = new ArrayList<>();
                List<BitSet> nestedU2ListData = new ArrayList<>();


                if (!nestedTypeOfData.name.equals("NULL")) {
                    if(!nestedTypeOfData.name.equals("OCTET STRING")) {
                        nestedU2ListData.addAll(getDataWithConstrains(nestedTypeOfData));
                    } else {
                        // kodowanie object string
                        nestedU2ListData.addAll(getDataToFrameString(nestedTypeOfData.constrains.byteConstrain));
                    }
                } else {
                    //todo EOC
//                    nestedU2ListData.addAll(addEOC());
//                    nestedU2List.addAll(lengthFrame("0"));
                }

                if (selectedTypeOfData.keyWord.equals("EXPLICIT")) {
//                  u2List.add(codeIdentyfiyFrame(findTypeOfData(x.genericType.trim())));
                    nestedU2List.add(codeIdentyfiyFrame(nestedTypeOfData));
                    if(!nestedTypeOfData.name.equals("NULL")) {
                        nestedU2List.addAll(lengthFrame(Integer.toString(nestedU2ListData.size())));
                    } else {
                        nestedU2List.addAll(lengthFrame("0"));
                    }
                }
//                nestedU2List.addAll(lengthFrame(Integer.toString(nestedU2ListData.size())));
                nestedU2List.addAll(nestedU2ListData);

                u2List.addAll(nestedU2List);
            }
        } else {
            if (!selectedTypeOfData.name.equals("NULL")) {
                if(!selectedTypeOfData.name.equals("OCTET STRING")) {
                    u2List.addAll(getDataWithConstrains(selectedTypeOfData));
                } else {
                    // kodowanie object string
                    u2List.addAll(getDataToFrameString(selectedTypeOfData.constrains.byteConstrain));
                }
            } else {
                //todo EOC
//                u2List.addAll(addEOC());
            }
        }


        frame.add(codeIdentyfiyFrame(selectedTypeOfData));              // TAG
        if(!selectedTypeOfData.name.equals("NULL")) {
            frame.addAll(lengthFrame(Integer.toString(u2List.size())));           // LENGTH
        } else {
            frame.addAll(lengthFrame("0"));                                 // LENGTH
        }
        frame.addAll(u2List);                                           // DATA

        System.out.println(frame.size());
        System.out.println("Zakodowana ramka danych: ");
        for (BitSet x : frame) {
            System.out.print(convertBitSetToString(x) + " ");
        }
        System.out.println("");

        return frame;
    }

    //todo kodowanie sekwencji z OID
    public static List<BitSet> codeFrame(TypeOfData passedTypeOfData) {
        Scanner scan = new Scanner(System.in);
        TypeOfData selectedTypeOfData;
        List<BitSet> u2List = new ArrayList<>();
        List<BitSet> frame = new ArrayList<>();

//        System.out.println("Wybierz nr typu do zakodowania: ");
//        int nbType = scan.nextInt();

//        selectedTypeOfData = parsedTypeOfData.get(nbType);
        selectedTypeOfData = passedTypeOfData;
        selectedTypeOfData.present();

        if (selectedTypeOfData.getParentTypes().size() != 0) {
            for (DataStructure x : selectedTypeOfData.getParentTypes()) {
                TypeOfData nestedTypeOfData = findTypeOfData(x.genericType.trim());
                List<BitSet> nestedU2List = new ArrayList<>();
                List<BitSet> nestedU2ListData = new ArrayList<>();

                if (nestedTypeOfData.getParentTypes().size() > 0) {
                    nestedU2List.addAll(codeFrame(nestedTypeOfData));  //todo podwojne wprowadzanie danych dla typow zagniezdzonych
                }

                if (nestedTypeOfData.keyWord.equals("EXPLICIT")) {
//                  u2List.add(codeIdentyfiyFrame(findTypeOfData(x.genericType.trim())));
                    nestedU2List.add(codeIdentyfiyFrame(nestedTypeOfData));
                }
                if (!selectedTypeOfData.name.equals("NULL")) {

                    if(!selectedTypeOfData.name.equals("OCTET STRING")) {
                        nestedU2ListData.addAll(getDataWithConstrains(selectedTypeOfData));
                    } else {
                        // kodowanie object string
                        nestedU2ListData.addAll(getDataToFrameString(selectedTypeOfData.constrains.byteConstrain));
                    }

                } else {
//                    nestedU2ListData.addAll(addEOC());
                }
                nestedU2List.addAll(lengthFrame(Integer.toString(nestedU2ListData.size())));
                nestedU2List.addAll(nestedU2ListData);

                u2List.addAll(nestedU2List);
            }
        } else {
            if (!selectedTypeOfData.name.equals("NULL")) {
                if(!selectedTypeOfData.name.equals("OCTET STRING")) {
                    u2List.addAll(getDataWithConstrains(selectedTypeOfData));
                } else {
                    // kodowanie object string
                    u2List.addAll(getDataToFrameString(selectedTypeOfData.constrains.byteConstrain));
                }
            } else {
//                u2List.addAll(addEOC());
            }
        }


        frame.add(codeIdentyfiyFrame(selectedTypeOfData));
        frame.addAll(lengthFrame(Integer.toString(u2List.size())));

        frame.addAll(u2List);

        System.out.println(frame.size());
        System.out.println("Zakodowana ramka danych: ");
        for (BitSet x : frame) {
            System.out.print(convertBitSetToString(x) + " ");
        }
        System.out.println("");

        return u2List;
    }


    public static List<BitSet> codeFrameSimple(TypeOfData passedTypeOfData, String valueToCode) {
        Scanner scan = new Scanner(System.in);
        TypeOfData selectedTypeOfData;
        List<BitSet> u2List = new ArrayList<>();
        List<BitSet> frame = new ArrayList<>();

//        System.out.println("Wybierz nr typu do zakodowania: ");
//        int nbType = scan.nextInt();

//        selectedTypeOfData = parsedTypeOfData.get(nbType);
        selectedTypeOfData = passedTypeOfData;
        selectedTypeOfData.present();

//        if (selectedTypeOfData.getParentTypes().size() != 0) {
//            for (DataStructure x : selectedTypeOfData.getParentTypes()) {
//                TypeOfData nestedTypeOfData = findTypeOfData(x.genericType.trim());
//                List<BitSet> nestedU2List = new ArrayList<>();
//                List<BitSet> nestedU2ListData = new ArrayList<>();
//
//                if (nestedTypeOfData.getParentTypes().size() > 0) {
//                    nestedU2List.addAll(codeFrame(nestedTypeOfData));  //todo podwojne wprowadzanie danych dla typow zagniezdzonych
//                }
//
//                if (nestedTypeOfData.keyWord.equals("EXPLICIT")) {
////                  u2List.add(codeIdentyfiyFrame(findTypeOfData(x.genericType.trim())));
//                    nestedU2List.add(codeIdentyfiyFrame(nestedTypeOfData));
//                }
//                if (!selectedTypeOfData.name.equals("NULL")) {
//
//                    if(!selectedTypeOfData.name.equals("OCTET STRING")) {
//                        nestedU2ListData.addAll(getDataWithConstrains(selectedTypeOfData));
//                    } else {
//                        // kodowanie object string
//                        nestedU2ListData.addAll(getDataToFrameString(selectedTypeOfData.constrains.byteConstrain));
//                    }
//
//                } else {
////                    nestedU2ListData.addAll(addEOC());
//                }
//                nestedU2List.addAll(lengthFrame(Integer.toString(nestedU2ListData.size())));
//                nestedU2List.addAll(nestedU2ListData);
//
//                u2List.addAll(nestedU2List);
//            }
//        } else {
//            if (!selectedTypeOfData.name.equals("NULL")) {
//                if(!selectedTypeOfData.name.equals("OCTET STRING")) {
//                    u2List.addAll(getDataWithConstrains(selectedTypeOfData));
//                } else {
//                    // kodowanie object string
//                    u2List.addAll(getDataToFrameString(selectedTypeOfData.constrains.byteConstrain));
//                }
//            } else {
////                u2List.addAll(addEOC());
//            }
//        }
        if(passedTypeOfData.name.equals("NULL")){
            frame.addAll(codeFrame(passedTypeOfData));
        } else {


            u2List.addAll(getDataToFrameSimple(valueToCode));

            frame.add(codeIdentyfiyFrame(selectedTypeOfData));
            frame.addAll(lengthFrame(Integer.toString(u2List.size())));

            frame.addAll(u2List);
        }
        System.out.println(frame.size());
        System.out.println("Zakodowana ramka danych: ");
        for (BitSet x : frame) {
            System.out.print(convertBitSetToString(x) + " ");
        }
        System.out.println("");

        return frame;
    }

    //todo kodowanie scieżki do OID
    public List<BitSet> codePathToOID(TypeOfData passedTypeOfData, String pathToOID) {
        List<BitSet> frame = new ArrayList<>();
        List<BitSet> codedOIDPath = new ArrayList<>();
        List<BitSet> u2ListToReverse = new ArrayList<>();
        String stringPathWithoutDots[] = pathToOID.split("\\.");
//        List<Character> characterPathWithoutDots = new ArrayList<>();
        List<Integer> integerPathWithoutDots = new ArrayList<>();

        for (String x : stringPathWithoutDots) {
//            characterPathWithoutDots.add(x.charAt(0));
            integerPathWithoutDots.add(Integer.parseInt(x));
        }
        for(int i = 0; i <= integerPathWithoutDots.size()-1; i++) {
            if (i == 0 ) {
                long firstOctet = 40*integerPathWithoutDots.get(0) + integerPathWithoutDots.get(1);
                u2ListToReverse.addAll(getU2Value(firstOctet));
                i++;
            } else {
                u2ListToReverse.addAll(getU2Value((long) integerPathWithoutDots.get(i)));
            }
        }

        for(BitSet y: u2ListToReverse) {
            codedOIDPath.add(reverseBitSet(y));
        }

        frame.add(codeIdentyfiyFrame(passedTypeOfData));                        // TAG
        frame.addAll(lengthFrame(Integer.toString(codedOIDPath.size())));       // LENGTH
        frame.addAll(codedOIDPath);                                             // PATH



        System.out.println(frame.size());
        System.out.println("Zakodowana ramka danych: ");
        for (BitSet x : frame) {
            System.out.print(convertBitSetToString(x) + " ");
        }
        System.out.println("");


        return frame;
    }

}



//todo alias context-specific
//PhysAddress ::=
//      OCTET STRING

//todo dodac parsowanie CHOICE - zrobione!
//todo dodac parsowanie aliasow - zrobione!
//todo sprawdzic dlaczego nie parsuje elementow z sciezki 1.3.6.1.2.1.8.5.1.13 - zrobione!

//todo kodowanie ścieżki drzewa do OIDa - zrobione!
//todo sprawdzic dlaczego przy kodownaiu typow nie dziala walidacja poprawnosci danych - zależy od długości liczby wporwadzonej przez użytkownika
//todo dodac zapis stringow dla octet-string - zrobione!