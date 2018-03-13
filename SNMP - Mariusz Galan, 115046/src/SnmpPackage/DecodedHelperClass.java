package SnmpPackage;

import PkgDekoderBER.DecodedObjectFromBitStream;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class DecodedHelperClass {
    DecodedObjectFromBitStream decodedObject;
    List<BitSet> processedListBitSet;
    Integer lowIndex;
    Integer highIndex;

    DecodedHelperClass() {
        this.decodedObject = new DecodedObjectFromBitStream();
        this.processedListBitSet = new ArrayList<>();
        this.lowIndex = null;
        this.highIndex = null;
    }

}
