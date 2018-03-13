package sharedMetods;

import PkgKoderBER.koderBER;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class convertStringStreamToBitSetList8 {
    public static List<BitSet> convertStringStreamToBitSetList8(String inputStream) {
        List<BitSet> convertedStream = new ArrayList<>();
        koderBER.keySize = 8;
        if((inputStream.length()%2) != 0) {
            System.out.println("Nie poprawna ilosc bitow.");
        } else {
            for(int i = 0; i <= inputStream.length()-1; i += 2) {
                String binaryString;
//                System.out.println(inputStream.substring(i, i+2));

                binaryString = new BigInteger(inputStream.substring(i, i+2),16).toString(2);

                int length = binaryString.length();
                if(length < 8) {
                    for (int k = 0; k < 8 - length; k++) {
                        binaryString = "0" + binaryString;
                    }
                }
//                System.out.println(koderBER.getBitSet(binaryString));
                convertedStream.add(koderBER.getBitSet(binaryString));
            }

        }

//        System.out.println(convertedStream.size());
        return convertedStream;
    }
}
