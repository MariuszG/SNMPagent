package sharedClass;

import sharedClass.TypeOfData;

public class ObjectType {
    public  String name;
    public  TypeOfData syntax = new TypeOfData();
    public  String access;
    public  String status;
    public  String description;
    public  String nameBranch;
    public  String nrBranch;


   public ObjectType() {}

   public  ObjectType(String root) {
        this.name = root;
        this.nrBranch = "1";
    }

    public ObjectType(String name, String nrBranch) {
        this.name = name;
//        this.syntax = null;
        this.access = null;
        this.status = null;
        this.description = null;
        this.nameBranch = name;
        this.nrBranch = nrBranch;
    }

    public ObjectType(String sName, TypeOfData sSyntax, String sAccess, String sStatus, String sDesc, String sNameBranch, String sNrBranch) {
//            , ObjectIdentifier sOIParent, ObjectIdentifier sOIChild ) {
        this.name = sName;
        this.syntax = sSyntax;
        this.access = sAccess;
        this.status = sStatus;
        this.description = sDesc;
        this.nameBranch = sNameBranch;
        this.nrBranch = sNrBranch;
//        this.parents.add(sOIParent);
//        this.childrens.add(sOIChild);
    }

   public void present() {
        System.out.println("\tname: " + this.name);
//        System.out.println(this.syntax);
       syntax.present();
        System.out.println("\taccess: " + this.access);
        System.out.println("\tstatus: " + this.status);
        System.out.println("\tdescription: " + this.description);
        System.out.println("\tnameBranch: " + this.nameBranch);
        System.out.println("\tnrBranch: " + this.nrBranch);
    }

   public String returnData(String tabbing) {
        return "name: " + this.name + "\n" +
                syntax.returnData(tabbing)  +
                tabbing + "\taccess: " + this.access + "\n" +
                tabbing + "\tstatus: " + this.status + "\n" +
                tabbing + "\tdescription: " + this.description + "\n" +
                tabbing + "\tnameBranch: " + this.nameBranch + "\n" +
                tabbing + "\tnrBranch: " + this.nrBranch + "\n";

    }
}
