package sharedClass;

public class DataTypeConstrains
{
    public String byteConstrain;
    public String minValConstrain;
    public String maxValConstrain;


    DataTypeConstrains() {
        this.byteConstrain = null;
        this.minValConstrain = null;
        this.maxValConstrain = null;
    }

    DataTypeConstrains(String byteCons) {
        this.byteConstrain = byteCons;
        this.minValConstrain = null;
        this.maxValConstrain = null;
    }

    DataTypeConstrains(String min, String max) {
        this.byteConstrain = null;
        this.minValConstrain = min;
        this.maxValConstrain = max;
    }
}
