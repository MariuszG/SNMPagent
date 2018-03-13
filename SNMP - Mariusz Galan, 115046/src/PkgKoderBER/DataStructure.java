package PkgKoderBER;

public class DataStructure {
    public String name;
    public String genericType;

    public DataStructure(String name, String genericType) {
        this.name = name;
        this.genericType = genericType;
    }
    public DataStructure(String genericType) {
        this.name = null;
        this.genericType = genericType;
    }
}
