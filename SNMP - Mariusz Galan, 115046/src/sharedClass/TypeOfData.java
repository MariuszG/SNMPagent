package sharedClass;

import PkgKoderBER.DataStructure;

import java.util.ArrayList;
import java.util.List;

public class TypeOfData {
    public String name;
    public String visibility;
    public String typeId;
    public List<DataStructure> parentTypes = new ArrayList<DataStructure>();
    public String keyWord;
    public DataTypeConstrains constrains = new DataTypeConstrains();
    private String value;

    TypeOfData() {}

//    aplication
    public TypeOfData(String name, String visibility, String typeId, String keyWord, List<DataStructure> listTypes, String byteCon ) {
        this.name = name;
        this.visibility = visibility;
        this.typeId = typeId;
        this.parentTypes.addAll(listTypes);
        this.keyWord = keyWord;
        this.constrains.byteConstrain = byteCon;
        this.value = null;
    }

    public TypeOfData(String name, String visibility, String typeId, String keyWord, List<DataStructure> listTypes, String minVal, String maxVal ) {
        this.name = name;
        this.visibility = visibility;
        this.typeId = typeId;
        this.parentTypes.addAll(listTypes);
        this.keyWord = keyWord;
        this.constrains.minValConstrain = minVal;
        this.constrains.maxValConstrain = maxVal;
        this.value = null;
    }


//    sequence
    public TypeOfData(String name, List<DataStructure> listTypes) {
        this.name = name;
        this.visibility = "universal";
        this.typeId = null;
        this.parentTypes.addAll(listTypes);
        this.keyWord = "EXPLICIT";
//        this.constrains = null;
    }
    public TypeOfData(String name, List<DataStructure> listTypes, String byteCon) {
        this.name = name;
        this.visibility = "universal";
        this.typeId = null;
        this.parentTypes.addAll(listTypes);
        this.keyWord = "EXPLICIT";
        this.constrains.byteConstrain = byteCon;
    }

    public TypeOfData(String name, String visibility, List<DataStructure> listTypes) {
        this.name = name;
        this.visibility = visibility;
        this.typeId = null;
        this.parentTypes.addAll(listTypes);
        this.keyWord = "EXPLICIT";
        this.constrains.byteConstrain = null;
        this.value = null;
    }

    public TypeOfData(String name, String visibility, List<DataStructure> listTypes, String byteCon) {
        this.name = name;
        this.visibility = visibility;
        this.typeId = null;
        this.parentTypes.addAll(listTypes);
        this.keyWord = "EXPLICIT";
        this.constrains.byteConstrain = byteCon;
    }

    public TypeOfData(String nameGenericType, String byteCon, boolean isGenericType) {
        this.name = nameGenericType;
        this.visibility = "universal";
        this.typeId = null;
        this.parentTypes = new ArrayList<>();
        this.keyWord = "EXPLICIT";
        this.constrains.byteConstrain = byteCon;
        this.value = null;
    }

    public TypeOfData(String nameGenericType, String minCon, String maxCon, boolean isGenericType) {
        this.name = nameGenericType;
        this.visibility = "universal";
        this.typeId = null;
        this.parentTypes = new ArrayList<>();
        this.keyWord = "EXPLICIT";
        this.constrains.minValConstrain = minCon;
        this.constrains.maxValConstrain = maxCon;
    }

    public TypeOfData(String nameParentType, String byteCon) {
        this.name = nameParentType;
        this.visibility = null;
        this.typeId = null;
        this.parentTypes.add(new DataStructure(nameParentType));
        this.keyWord = "EXPLICIT";
        this.constrains.byteConstrain = byteCon;
        this.value = null;
    }

    public TypeOfData(String nameParentType, String minCon, String maxCon) {
        this.name = nameParentType;
        this.visibility = null;
        this.typeId = null;
        this.parentTypes.add(new DataStructure(nameParentType));
        this.keyWord = "EXPLICIT";
        this.constrains.minValConstrain = minCon;
        this.constrains.maxValConstrain = maxCon;
    }

    // generic types
    public List<DataStructure> getParentTypes() {
        return parentTypes;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }


    public void present() {
        System.out.println("sharedClass.TypeOfData:");
        System.out.println("\tname: " + this.name);
        System.out.println("\tvisibility: " + this.visibility);
        System.out.println("\ttypeId: " + this.typeId);
        for (DataStructure x : parentTypes) {
            System.out.println("\tparent Type: name: " + x.name + ", genericType: " + x.genericType);
        }
        System.out.println("\tkeyWord: " + this.keyWord);
        System.out.println("\tconstrains byte: " + this.constrains.byteConstrain + " minVal: " + this.constrains.minValConstrain + " maxVal: " + this.constrains.maxValConstrain);
        System.out.println("\tvalue: " + this.value);
    }

    public String returnData(String tabinng) {
        String parentTypeStructure = new String();
        if (parentTypes.size() != 0) {
            for (DataStructure x : parentTypes) {
//            System.out.println("\tparent Type: name: " + x.name + ", genericType: " + x.genericType);
                parentTypeStructure = parentTypeStructure + "name: " + x.name + ", genericType: " + x.genericType;
            }
        } else {
            parentTypeStructure = null;
        }
        return tabinng + "\tsharedClass.TypeOfData:\n" +
                tabinng + "\t\tname: " + this.name + "\n" +
                tabinng + "\t\tvisibility: " + this.visibility + "\n" +
                tabinng + "\t\ttypeId: " + this.typeId + "\n" +
                tabinng + "\t\tparent Type: "  + parentTypeStructure + "\n" +
                tabinng + "\t\tkeyWord: " + this.keyWord + "\n" +
                tabinng + "\t\tconstrains byte: " + this.constrains.byteConstrain + " minVal: " + this.constrains.minValConstrain + " maxVal: " + this.constrains.maxValConstrain + "\n" +
                tabinng + "\t\tvalue: " + this.value + "\n";

    }

}
