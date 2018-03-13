package PkgDekoderBER;

public class DecodedObjectFromBitStream {
    public String visibility;
    public String complexity;
    public String identityTag;
    public int lenght;
    public String data;
    public int numberLastTagOctet;


    public DecodedObjectFromBitStream(){}

    DecodedObjectFromBitStream(String visibility, String complexity, String identityTag, int lenght, String data) {
        this.visibility = visibility;
        this.complexity = complexity;
        this.identityTag = identityTag;
        this.lenght = lenght;
        this.data = data;
        this.numberLastTagOctet = 0;
    }

    public void present() {
        System.out.println("Decoded object: \n" +
                "visibility: " + this.visibility + "\n" +
                "complexity: " + this.complexity + "\n" +
                "identityTag: " + this.identityTag + "\n" +
                "lenght: " + this.lenght + "\n" +
                "data: " + this.data);
    }

}
