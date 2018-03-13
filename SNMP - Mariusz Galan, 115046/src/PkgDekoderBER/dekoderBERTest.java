package PkgDekoderBER;

import PkgKoderBER.koderBER;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class dekoderBERTest {

    int keySize = 8;

    public  BitSet getBitSet(String key) {
        char[] cs = new StringBuilder(key).toString().toCharArray();
//        BitSet result = new BitSet(keySize);
        BitSet result = new BitSet(keySize);
        int m = Math.min(keySize, cs.length);

        for (int i = 0; i < m; i++)
            if (cs[i] == '1') result.set(i);

        return result;
    }


    @BeforeClass
    public static void Into() {
        System.out.println("Plik testów jednostkowych!");
    }


    @Test
    public void decodeInteger() {
        PkgDekoderBER.dekoderBER dekoderBER = new dekoderBER();
        PkgDekoderBER.DecodedObjectFromBitStream decodedObject;
        PkgDekoderBER.DecodedObjectFromBitStream expectedObject = new PkgDekoderBER.DecodedObjectFromBitStream("universal", "Primitive", "INTEGER", 4, "56");
        List<BitSet> objectToDecode = new ArrayList<>();

        objectToDecode.add(getBitSet("00000010"));
        objectToDecode.add(getBitSet("00000100"));
        objectToDecode.add(getBitSet("00000000"));
        objectToDecode.add(getBitSet("00000000"));
        objectToDecode.add(getBitSet("00000000"));
        objectToDecode.add(getBitSet("00111000"));

        decodedObject = dekoderBER.decodeBitStream(objectToDecode);

        Assert.assertEquals(expectedObject.visibility, decodedObject.visibility);
        Assert.assertEquals(expectedObject.complexity, decodedObject.complexity);
        Assert.assertEquals(expectedObject.identityTag, decodedObject.identityTag);
        Assert.assertEquals(expectedObject.lenght, decodedObject.lenght);
        Assert.assertEquals(expectedObject.data, decodedObject.data);


    }

    @Test
    public void decodeOctetString() {
        PkgDekoderBER.dekoderBER dekoderBER = new dekoderBER();
        PkgDekoderBER.DecodedObjectFromBitStream decodedObject;
        PkgDekoderBER.DecodedObjectFromBitStream expectedObject = new PkgDekoderBER.DecodedObjectFromBitStream("universal", "Primitive", "OCTET STRING", 4, "Jane");
        List<BitSet> objectToDecode = new ArrayList<>();
        //PkgKoderBER PkgKoderBER = new PkgKoderBER();

        objectToDecode.add(getBitSet("00000100"));
        objectToDecode.add(getBitSet("00000100"));
        objectToDecode.add(getBitSet("01001010"));
        objectToDecode.add(getBitSet("01100001"));
        objectToDecode.add(getBitSet("01101110"));
        objectToDecode.add(getBitSet("01100101"));

        decodedObject = dekoderBER.decodeBitStream(objectToDecode);

        Assert.assertEquals(expectedObject.visibility, decodedObject.visibility);
        Assert.assertEquals(expectedObject.complexity, decodedObject.complexity);
        Assert.assertEquals(expectedObject.identityTag, decodedObject.identityTag);
        Assert.assertEquals(expectedObject.lenght, decodedObject.lenght);
        Assert.assertEquals(expectedObject.data, decodedObject.data);
    }

    @Test
    public void longTagAndLongLength() {
        PkgDekoderBER.dekoderBER dekoderBER = new dekoderBER();
        PkgDekoderBER.DecodedObjectFromBitStream decodedObject;
        PkgDekoderBER.DecodedObjectFromBitStream expectedObject = new PkgDekoderBER.DecodedObjectFromBitStream("universal", "Primitive", "BRAK TAGU O NA LISCIE O PODANEJ WARTOSCI: 168!", 4, "1247899237");
        List<BitSet> objectToDecode = new ArrayList<>();
        koderBER koderBER = new koderBER();

        objectToDecode.add(getBitSet("00011111"));
        objectToDecode.add(getBitSet("10000001"));
        objectToDecode.add(getBitSet("00101000")); //end tag
        objectToDecode.add(getBitSet("10000010"));
        objectToDecode.add(getBitSet("00000000"));
        objectToDecode.add(getBitSet("00000100")); // end length
        objectToDecode.add(getBitSet("01001010")); // start data
        objectToDecode.add(getBitSet("01100001"));
        objectToDecode.add(getBitSet("01101110"));
        objectToDecode.add(getBitSet("01100101"));

        decodedObject = dekoderBER.decodeBitStream(objectToDecode);

        Assert.assertEquals(expectedObject.visibility, decodedObject.visibility);
        Assert.assertEquals(expectedObject.complexity, decodedObject.complexity);
        Assert.assertEquals(expectedObject.identityTag, decodedObject.identityTag);
        Assert.assertEquals(expectedObject.lenght, decodedObject.lenght);
        Assert.assertEquals(expectedObject.data, decodedObject.data);
    }

}
