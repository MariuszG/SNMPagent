package ParseMIB;

import PkgDekoderBER.*;
import PkgKoderBER.*;
import sharedClass.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTree extends JFrame {

    String MIBfile;
    static List<importClass> importList = new ArrayList<importClass>();
    public static List<TypeOfData> dataTypeList = new ArrayList<TypeOfData>();
    public static Map<String, TypeOfData> dataTypeMap = new HashMap<>();
    public static List<MyTreeNode<ObjectType>> Tree = new ArrayList<MyTreeNode<ObjectType>>();
    public List<BitSet> codedObject = new ArrayList<>();

    List<List<BitSet>> codecObjectList = new ArrayList<>();

    static{
//        dataTypeMap.put("INTEGER",new TypeOfData("INTEGER", "4", true));
//        dataTypeMap.put("OCTET STRING",new TypeOfData("OCTET STRING", "4", true));
        dataTypeMap.put("INTEGER",new TypeOfData("INTEGER", null, true));
        dataTypeMap.put("OCTET STRING",new TypeOfData("OCTET STRING", null, true));
        dataTypeMap.put("OBJECT IDENTIFIER",new TypeOfData("OBJECT IDENTIFIER", null, true));
        dataTypeMap.put("NULL",new TypeOfData("NULL", null, true));
    }


    koderBER koderBER = new koderBER();
   public dekoderBER dekoderBER = new dekoderBER();



    public String loadMIB (String file) {
        String string = null;
        try {
            String path = "/Users/Mario/Documents/Bitbucket/Java/helloworld/net-snmp-5.7.3/mibs/";
            String fullPath = path + file + ".txt";
            System.out.println(fullPath);
            String loadedMIB;
            loadedMIB = fullPath;
            BufferedReader br = new BufferedReader(new FileReader(fullPath));
            String sCurrentLine;
            StringBuilder strBuilder = new StringBuilder();

            while ((sCurrentLine = br.readLine()) != null) {
//                sCurrentLine = sCurrentLine.trim();

//                System.out.println(sCurrentLine);
                if (sCurrentLine.contains("--")) {
                    sCurrentLine = sCurrentLine.substring(0, sCurrentLine.indexOf("--"));
//                    System.out.println(sCurrentLine);
                } else {
//                    System.out.println(sCurrentLine);
                }


                strBuilder.append(sCurrentLine + " ");

            }
//            System.out.println(strBuilder.toString());
            string = strBuilder.toString();

//            System.out.println(string);

//            return string;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return string;
        }

    }

    private void populateTreeStructure(MyTreeNode<ObjectType> tree, MyTreeNode<ObjectType> newChild, String parentName) {
        Queue<MyTreeNode<ObjectType>> q = new LinkedList<MyTreeNode<ObjectType>>();
        q.add(tree);
        while (!q.isEmpty()){
            tree = q.remove();
            if (!tree.isLeaf()) {
                q.addAll(tree.getChildren());
            }
//            System.out.println("pętla populateTreeStructure: " + tree.getData().name);
            if (tree.getData().name.equals(parentName)) {
//                System.out.println("Znaleziono rodzica dla: " + newChild.getData().name + ", rodzic: " + tree.getData().name);
                tree.addChild(newChild);
                break;
            }
        }
//        return tree;
    }

    private static MyTreeNode<ObjectType> bfs(MyTreeNode<ObjectType> tree, String objectToFind) {
        MyTreeNode<ObjectType> foundedObject = new MyTreeNode<>(new ObjectType());
        Queue<MyTreeNode<ObjectType>> q = new LinkedList<MyTreeNode<ObjectType>>();
        q.add(tree);
        try {
            while (!q.isEmpty()) {
                tree = q.remove();
                if (!tree.isLeaf()) {
                    q.addAll(tree.getChildren());
                }
                System.out.println("pętla populateTreeStructure: " + tree.getData().name);
                if (tree.getData().name.equals(objectToFind)) {
//                System.out.println("Znaleziono rodzica dla: " + newChild.getData().name + ", rodzic: " + tree.getData().name);
                    foundedObject = tree;
                    break;
                } else if (tree.getData().nrBranch.equals(objectToFind)) {
                    foundedObject = tree;
                    break;
                }
            }
            return foundedObject;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static MyTreeNode<ObjectType> bfsValidateLastReturnedOID(MyTreeNode<ObjectType> tree, String objectToFind, boolean validLastOID) {
        MyTreeNode<ObjectType> foundedObject = new MyTreeNode<>(new ObjectType());
        Queue<MyTreeNode<ObjectType>> q = new LinkedList<MyTreeNode<ObjectType>>();
        try {
            if(!validLastOID) {
                q.add(tree);
            } else {
                q.addAll(tree.getChildren());
            }
            while (!q.isEmpty()) {
                tree = q.remove();
                if (!tree.isLeaf()) {
                    q.addAll(tree.getChildren());
                }
                System.out.println("pętla populateTreeStructure: " + tree.getData().name);
                if (tree.getData().name.equals(objectToFind)) {
//                System.out.println("Znaleziono rodzica dla: " + newChild.getData().name + ", rodzic: " + tree.getData().name);
                    foundedObject = tree;
                    break;
                } else if (tree.getData().nrBranch.equals(objectToFind)) {
                    foundedObject = tree;
                    break;
                }
            }


            return foundedObject;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private List<String> loadImportsFromFile (String mibFile) {
        final String globalImportRegex = "IMPORTS\\s*(.*?)\\;";
        final String firstStepRegex= "([\\w\\W]+?)?FROM\\s+([\\w-]*)";
        final String secondStepRegex= "([a-zA-Z0-9]+)+";
        Pattern importPattern = Pattern.compile(globalImportRegex, Pattern.MULTILINE);
        Matcher importMatcher = importPattern.matcher(mibFile);

        String globalMatch = null;
        List<String> importFilesSource = new ArrayList<>();

        while (importMatcher.find()) {
            globalMatch = importMatcher.group(1);
        }
//        System.out.println(globalMatch);
        if (globalMatch != null) {
            importPattern = Pattern.compile(firstStepRegex, Pattern.MULTILINE);
            importMatcher = importPattern.matcher(globalMatch);

            while (importMatcher.find()) {
                importList.add(new importClass(importMatcher.group(1).trim(), importMatcher.group(2).trim()));
//                System.out.println(importMatcher.group(2).trim());
                importFilesSource.add(importMatcher.group(2).trim());
            }

//        for(ParseMIB.importClass x : importList) {
//            System.out.println(x.rawRegexData + " " + x.sourceFile);
//        }

            importPattern = Pattern.compile(secondStepRegex, Pattern.MULTILINE);
//        for(ParseMIB.importClass x : importList) {
            for (int i = 0; i < importList.size(); i++) {
                if (!importList.get(i).rawRegexData.equals("OBJECT-TYPE")) {
                    importMatcher = importPattern.matcher(importList.get(i).rawRegexData);
//            System.out.println(x.rawRegexData);
                    while (importMatcher.find()) {
                        importList.get(i).addSingleItem(importMatcher.group(1).trim());
                    }
                } else {
                    importList.remove(importList.get(i));
                    importFilesSource.remove(importFilesSource.get(i));
                    System.out.println(importList.size());
                }
            }
//        for(ParseMIB.importClass x : importList) {
//            System.out.println(x.rawRegexData + " " + x.sourceFile);
//            for(String y : x.importedItems) {
//                System.out.println(y);
//            }
//        }


//        while (importMatcher.find()) {
//            importList.add(new ParseMIB.importClass());
//        }
//        System.out.println(globalMatch);
            for (String x : importFilesSource) {
                System.out.println(x);
            }
        }
        return importFilesSource;

    }

    private void loadImportsFromList (List<String> importSource) {
        Queue<String> queue = new LinkedList<>();
        queue.addAll(importSource);
        String tempImportSource;
        while(queue.size() != 0 ) {
            tempImportSource = queue.remove();
//            if(!importSource.isEmpty()) {
//                for(String x : importSource) {
//                    System.out.println(x);
                    if (loadImportsFromFile(tempImportSource) != null) {
                        String loadImportFile = loadMIB(tempImportSource);
//                        System.out.println(loadImportFile);
                        queue.addAll(loadImportsFromFile(loadImportFile));
                        parseTypeOfData(loadImportFile);
                        loadObjectIdentifiers(loadImportFile);
                        loadObjectType(tempImportSource);
//                    }
//                }
            }
        }
    }

    private void loadObjectIdentifiers(String mibFile) {
        final String globalImportRegex = "([a-zA-Z0-9-]*)\\s*OBJECT IDENTIFIER\\s*\\:\\:\\=\\s*\\{(.*?)\\}";
        final String firstStepRegex= "([a-zA-Z0-9\\-\\(\\)]+)+";
        final String secondStepRegex= "([a-zA-Z0-9\\-]+)\\(?([0-9]*)\\)?+";
        final String thirdStepRegex= "([a-zA-Z0-9\\-)]*)\\s([0-9]*)";

        Pattern objectIdentifierPattern = Pattern.compile(globalImportRegex, Pattern.MULTILINE);
        Matcher objectIdentifierMatcher = objectIdentifierPattern.matcher(mibFile);

        List<importObjectIdentifier> temporaryObjectIdentifier = new ArrayList<importObjectIdentifier>();
        String upNode = null;
        System.out.println("LOAD OID: " + mibFile.substring(0, 11));
        while(objectIdentifierMatcher.find()) {

            temporaryObjectIdentifier.add(new importObjectIdentifier(objectIdentifierMatcher.group(1).trim(), objectIdentifierMatcher.group(2).trim()));

        }

        objectIdentifierPattern = Pattern.compile(firstStepRegex, Pattern.MULTILINE);

        for(importObjectIdentifier x : temporaryObjectIdentifier) {
//            System.out.println("First step: " + x.child + " " + x.rawRegex);
            objectIdentifierMatcher = objectIdentifierPattern.matcher(x.rawRegex);
            while (objectIdentifierMatcher.find()) {
                x.importParents.add(objectIdentifierMatcher.group(1));
            }
        }

//        for(ParseMIB.importObjectIdentifier x: temporaryObjectIdentifier) {
//            System.out.println(x.child);
//            for(String y : x.importParents) {
//                System.out.println("\t" + y);
//            }
//        }

        objectIdentifierPattern = Pattern.compile(secondStepRegex, Pattern.MULTILINE);
        MyTreeNode<ObjectType> parent = new MyTreeNode<>(new ObjectType());
        for(importObjectIdentifier x: temporaryObjectIdentifier) {
            if( x.importParents.size() > 2) {
//                System.out.println("Second regex: " + x.child);
                for(int i = 0; i < x.importParents.size(); i++) {
//                    System.out.println("\t" + x.importParents.get(i));
//                    System.out.println("index: " + i);
                    objectIdentifierMatcher = objectIdentifierPattern.matcher(x.importParents.get(i));
                    while (objectIdentifierMatcher.find()) {
//                        if (i != x.importParents.size() - 1) {
//
//                            if (objectIdentifierMatcher.group(2).equals("")) {
////                                System.out.println("root");
//                                Tree.add(new ParseMIB.MyTreeNode<>(new sharedClass.ObjectType(objectIdentifierMatcher.group(1))));
//                            } else {
////                                System.out.println("child");
//                                Tree.get(0).addChild(new sharedClass.ObjectType(objectIdentifierMatcher.group(1), objectIdentifierMatcher.group(2)));
//                            }
//
//                        } else {
////                            System.out.println("latest child");
//                            List<ParseMIB.MyTreeNode<sharedClass.ObjectType>> parentsChild = Tree.get(Tree.size() - 1).getChildren();
//                            ParseMIB.MyTreeNode<sharedClass.ObjectType> lastChildOnList = parentsChild.get(parentsChild.size() - 1);
////                            System.out.println(parentsChild.get(parentsChild.size() - 1).getData().name);
////                            System.out.println(lastChildOnList.getData().name);
////                            System.out.println("x child: " + x.child);
//                            lastChildOnList.addChild(new ParseMIB.MyTreeNode(new sharedClass.ObjectType(x.child, objectIdentifierMatcher.group(1).trim().toString())));
//
//                        }
                        if (Tree.size() == 0 ) {
//                            System.out.println("root: " + objectIdentifierMatcher.group(1));
                            Tree.add(new MyTreeNode<>(new ObjectType(objectIdentifierMatcher.group(1))));
                            upNode = objectIdentifierMatcher.group(1);
                        } else {
//                            System.out.println(x.importParents.get(1));
//                            System.out.println(objectIdentifierMatcher.group(1));
                            if (i > 0 && i != x.importParents.size() - 1) {
//                                System.out.println("if");
//                                System.out.println(x.child);
//                                System.out.println(x.importParents.get(1));
//                                System.out.println(objectIdentifierMatcher.group(1));
//                                System.out.println(objectIdentifierMatcher.group(2));
//                                System.out.println(x.importParents.get(i-1));
                                populateTreeStructure(Tree.get(0),new MyTreeNode<>(new ObjectType(objectIdentifierMatcher.group(1), objectIdentifierMatcher.group(2))), upNode);
                                parent.addChild(new MyTreeNode<>(new ObjectType(objectIdentifierMatcher.group(1), objectIdentifierMatcher.group(2))));
                                upNode = objectIdentifierMatcher.group(1);
                            } else {
//                                System.out.println("else");
//                                System.out.println(x.child);
//                                System.out.println(x.importParents.get(1));
//                                System.out.println(objectIdentifierMatcher.group(0));
//                                System.out.println(x.importParents.get(0));
//                                System.out.println(x.importParents.get(0));
//                                Tree.get(0).addChild(new sharedClass.ObjectType(objectIdentifierMatcher.group(1), objectIdentifierMatcher.group(2)));
                                populateTreeStructure(Tree.get(0),new MyTreeNode(new ObjectType(x.child, objectIdentifierMatcher.group(1).trim())), upNode);

                            }
                        }
                    }
                }
            } else {
//                    System.out.println("raw data: " + Tree.get(0).getData().name + x.child + x.importParents.get(1) + x.importParents.get(0) +" indeks: " + 0);
                populateTreeStructure(Tree.get(0),new MyTreeNode<>(new ObjectType(x.child, x.importParents.get(1))), x.importParents.get(0));
                parent.addChild(new MyTreeNode<>(new ObjectType(x.child, x.importParents.get(1))));

            }
        }

//        for(ParseMIB.MyTreeNode<sharedClass.ObjectType> x : Tree) {
//            System.out.println(x.getData().name);
//            for(ParseMIB.MyTreeNode<sharedClass.ObjectType> y : x.getChildren()) {
//                System.out.println("\t" + y.getData().name + " " + y.getData().nrBranch);
//                for(ParseMIB.MyTreeNode<sharedClass.ObjectType> z : y.getChildren()) {
//                    System.out.println("\t\t" + z.getData().name + " " + z.getData().nrBranch);
//                    for(ParseMIB.MyTreeNode<sharedClass.ObjectType> w : z.getChildren()) {
//                        System.out.println("\t\t\t" + w.getData().name + " " + w.getData().nrBranch);
//                        for(ParseMIB.MyTreeNode<sharedClass.ObjectType> e : w.getChildren()) {
//                            System.out.println("\t\t\t\t" + e.getData().name + " " + e.getData().nrBranch);
//                            for(ParseMIB.MyTreeNode<sharedClass.ObjectType> r : e.getChildren()) {
//                                System.out.println("\t\t\t\t\t" + r.getData().name + " " + r.getData().nrBranch);
//                                for(ParseMIB.MyTreeNode<sharedClass.ObjectType> u : r.getChildren()) {
//                                    System.out.println("\t\t\t\t\t\t" + u.getData().name + " " + u.getData().nrBranch);
//                                    for(ParseMIB.MyTreeNode<sharedClass.ObjectType> i : u.getChildren()) {
//                                        System.out.println("\t\t\t\t\t\t\t" + i.getData().name + " " + i.getData().nrBranch);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

    }

    private void parseTypeOfData(String mibFile) {
        final String globalImportRegex = "([a-zA-Z0-9]*)\\s*\\:\\:\\=\\s*\\[(APPLICATION|UNIVERSAL|APPLICATION|CONTEXT\\-SPECIFIC|PRIVATE)\\s*([0-9]*)\\].*?(IMPLICIT|EXPLICIT)\\s*([a-zA-Z0-9\\s]*)\\s*(\\((SIZE\\s*\\([0-9]*\\)|[0-9]*\\.\\.[0-9]*)?\\))?";
//        final String globalImportRegex = "((?<name>\\w+?)\\s+?::=\\s+?\\[(?<reach>(UNIVERSAL|APPLICATION|CONTEXT-SPECIFIC|PRIVATE))\\s(?<value>\\d+)\\]\\s+?(?<type>(IMPLICIT|EXPLICIT))\\s(?<dataType>(INTEGER|OCTET STRING|OBJECT IDENTIFIER|NULL))(?<constraints>.*))+";
//        final String firstStepRegex= "([a-zA-Z0-9\\-]*)\\s*\\:\\:\\=\\s*SEQUENCE\\s*\\{\\s*(.*?)\\}";
        final String firstStepRegex= "([a-zA-Z0-9\\-]*)\\s*\\:\\:\\=\\s*(SEQUENCE|CHOICE)\\s*\\{\\s*(.*?)\\}";
        final String secondStepRegex=  "(([a-zA-Z0-9\\-]+)\\s*([a-zA-Z0-9\\-\\s]*)(\\([0-9]*\\.\\.[0-9]*\\))?)+\\,?+";
        final String aliasRegex= "(\\w+)\\s*\\:\\:\\=\\s*(OCTET\\s*STRING|INTEGER)";
        Pattern objectIdentifierPattern = Pattern.compile(globalImportRegex, Pattern.MULTILINE);
        Matcher objectIdentifierMatcher = objectIdentifierPattern.matcher(mibFile);

        List<PkgKoderBER.DataStructure> tempDataStructureList = new ArrayList<PkgKoderBER.DataStructure>();

        while(objectIdentifierMatcher.find()) {
//            System.out.println(objectIdentifierMatcher.group(0));
            tempDataStructureList.add(new PkgKoderBER.DataStructure(objectIdentifierMatcher.group(5)));
//            System.out.println("temp" + tempDataStructureList.size());
            if (objectIdentifierMatcher.group(6) != null) {
                Pattern p = Pattern.compile("\\(SIZE\\s*\\(([0-9]*)\\)\\)|\\(([0-9]*)\\.\\.([0-9]*)\\)");
                Matcher m = p.matcher(objectIdentifierMatcher.group(6));
                while (m.find()) {
                    if(m.group(1) != null) {
                        dataTypeList.add(new TypeOfData(objectIdentifierMatcher.group(1),
                                objectIdentifierMatcher.group(2),
                                objectIdentifierMatcher.group(3),
                                objectIdentifierMatcher.group(4),
                                tempDataStructureList,
                                m.group(1)));

                        dataTypeMap.put(objectIdentifierMatcher.group(1), new TypeOfData(objectIdentifierMatcher.group(1),
                                objectIdentifierMatcher.group(2),
                                objectIdentifierMatcher.group(3),
                                objectIdentifierMatcher.group(4),
                                tempDataStructureList,
                                m.group(1)));

                    } else {
                        dataTypeList.add(new TypeOfData(objectIdentifierMatcher.group(1),
                                objectIdentifierMatcher.group(2),
                                objectIdentifierMatcher.group(3),
                                objectIdentifierMatcher.group(4),
                                tempDataStructureList,
                                m.group(2), m.group(3)));

                        dataTypeMap.put(objectIdentifierMatcher.group(1), new TypeOfData(objectIdentifierMatcher.group(1),
                                objectIdentifierMatcher.group(2),
                                objectIdentifierMatcher.group(3),
                                objectIdentifierMatcher.group(4),
                                tempDataStructureList,
                                m.group(2), m.group(3)));
                    }
                }
            } else {
                dataTypeList.add(new TypeOfData(objectIdentifierMatcher.group(1),
                        objectIdentifierMatcher.group(2),
                        objectIdentifierMatcher.group(3),
                        objectIdentifierMatcher.group(4),
                        tempDataStructureList,
                        objectIdentifierMatcher.group(6)));

                dataTypeMap.put(objectIdentifierMatcher.group(1), new TypeOfData(objectIdentifierMatcher.group(1),
                        objectIdentifierMatcher.group(2),
                        objectIdentifierMatcher.group(3),
                        objectIdentifierMatcher.group(4),
                        tempDataStructureList,
                        objectIdentifierMatcher.group(6)));

            }
            tempDataStructureList.removeAll(tempDataStructureList);
        }
//        System.out.println(dataTypeList.size());


//  aliasy
        objectIdentifierPattern = Pattern.compile(aliasRegex, Pattern.MULTILINE);
        objectIdentifierMatcher = objectIdentifierPattern.matcher(mibFile);

        while(objectIdentifierMatcher.find()) {
            tempDataStructureList.add(new PkgKoderBER.DataStructure(objectIdentifierMatcher.group(2)));
            dataTypeList.add(new TypeOfData(objectIdentifierMatcher.group(1), "context-specific", tempDataStructureList));
            dataTypeMap.put(objectIdentifierMatcher.group(1), new TypeOfData(objectIdentifierMatcher.group(1), "context-specific", tempDataStructureList));
            tempDataStructureList.removeAll(tempDataStructureList);
        }


// sequence
        objectIdentifierPattern = Pattern.compile(firstStepRegex, Pattern.MULTILINE);
        objectIdentifierMatcher = objectIdentifierPattern.matcher(mibFile);


        while (objectIdentifierMatcher.find()) {
//            System.out.println("group 2: " + objectIdentifierMatcher.group(0));
            Pattern secondPattern = Pattern.compile(secondStepRegex, Pattern.MULTILINE);
            Matcher secondMatcher = secondPattern.matcher(objectIdentifierMatcher.group(3));
            while(secondMatcher.find()) {
//                System.out.println(secondMatcher.group(2) + " " + secondMatcher.group(3) + " " + secondMatcher.group(4));
                if(secondMatcher.group(4) == null) {
//                    System.out.println("if");
                    tempDataStructureList.add(new PkgKoderBER.DataStructure(secondMatcher.group(2), secondMatcher.group(3)));
                } else {
//                    System.out.println("else");
                    tempDataStructureList.add(new PkgKoderBER.DataStructure(secondMatcher.group(2), secondMatcher.group(3) + secondMatcher.group(4)));
                }
            }
            dataTypeList.add(new TypeOfData(objectIdentifierMatcher.group(1), "universal", tempDataStructureList));
            dataTypeMap.put(objectIdentifierMatcher.group(1), new TypeOfData(objectIdentifierMatcher.group(1), "universal", tempDataStructureList));
            tempDataStructureList.removeAll(tempDataStructureList);
        }



//            System.out.println(dataTypeList.size());
//            for(sharedClass.TypeOfData x : dataTypeList) {
//                System.out.println(x.name + x.visibility+ x.typeId+ x.keyWord +x.getParentTypes().get(0).name+ x.constrains);
//        }

    }

    private void loadObjectType(String mibFile) {
//        final String globalImportRegex = "([a-zA-Z0-9]+)\\s?(OBJECT-TYPE)\\s*(SYNTAX|SEQUENCE)?\\s*(SEQUENCE\\s*OF)?\\s*([a-zA-Z\\-]*\\s?[a-zA-Z\\-]*)\\s*(\\(?SIZE?\\(?\\s*\\(?[0-9]*\\.?\\.?[0-9]*\\)?\\)?|\\(?[0-9]*\\.\\.[0-9]*\\)?|\\{.*?\\})?\\s*[ACCESS]+\\s*([a-zA-Z\\-]*)\\s*[STATUS]+\\s*([a-zA-Z\\-]*)\\s*[DESCRIPTION]+\\s*(\\\".*?)[\\\"]\\s*\\:?\\:?\\=?\\s*[\\{]\\s*([a-zA-Z0-9\\-]*)\\s*([0-9]*)\\s*[\\}]*";
        final String globalImportRegex = "([a-zA-Z0-9]+)\\s?(OBJECT-TYPE)\\s*(SYNTAX|SEQUENCE)?\\s*(SEQUENCE\\s*OF)?\\s*([a-zA-Z\\-]*\\s?[a-zA-Z\\-]*)\\s*(\\(?SIZE?\\(?\\s*\\(?[0-9]*\\.?\\.?[0-9]*\\)?\\)?|\\(?[0-9]*\\.\\.[0-9]*\\)?|\\{.*?\\})?\\s*[ACCESS]+\\s*([a-zA-Z\\-]*)\\s*[STATUS]+\\s*([a-zA-Z\\-]*)\\s*[DESCRIPTION]+\\s*(\\\".*?)[\\\"]\\s*(INDEX\\s*\\{\\s*[\\w+]*\\s*\\})?\\s*\\:\\:\\=\\s*\\{\\s*([a-zA-Z0-9\\-]*)\\s*([0-9]*)\\s\\}";
//        final String globalImportRegex = "((?<name>\\w+?)\\s+?::=\\s+?\\[(?<reach>(UNIVERSAL|APPLICATION|CONTEXT-SPECIFIC|PRIVATE))\\s(?<value>\\d+)\\]\\s+?(?<type>(IMPLICIT|EXPLICIT))\\s(?<dataType>(INTEGER|OCTET STRING|OBJECT IDENTIFIER|NULL))(?<constraints>.*))+";
        final String firstStepRegex= "([a-zA-Z0-9\\-]*)\\s*\\:\\:\\=\\s*SEQUENCE\\s*\\{\\s*(.*?)\\}";
        final String secondStepRegex=  "(([a-zA-Z0-9\\-]+)\\s*([a-zA-Z0-9\\-\\s]*)(\\([0-9]*\\.\\.[0-9]*\\))?)+\\,?+";
        Pattern objectTypePattern = Pattern.compile(globalImportRegex, Pattern.MULTILINE);
        Matcher objectTypeMatcher = objectTypePattern.matcher(mibFile);
        MyTreeNode<ObjectType> temporaryMyTreeNodeObject; // = new ParseMIB.MyTreeNode<>(new sharedClass.ObjectType());
        TypeOfData temporaryTypeOfData;
//        ParseMIB.MyTreeNode<sharedClass.ObjectType> parentObjectType = new ParseMIB.MyTreeNode<>(new sharedClass.ObjectType());
        while(objectTypeMatcher.find()) {
//            System.out.println("group 0: " + objectTypeMatcher.group(0));
//            if (objectTypeMatcher.group(4) == null) {

                temporaryMyTreeNodeObject = new MyTreeNode<>(new ObjectType(objectTypeMatcher.group(1),
                                                            new TypeOfData(objectTypeMatcher.group(5), objectTypeMatcher.group(6)),
                                                                    objectTypeMatcher.group(7),
                                                                    objectTypeMatcher.group(8),
                                                                    objectTypeMatcher.group(9),
                                                                    objectTypeMatcher.group(1),
                                                                    objectTypeMatcher.group(12)));
            populateTreeStructure(Tree.get(0), temporaryMyTreeNodeObject, objectTypeMatcher.group(11));
//            parentObjectType.getData().present();
//            parentObjectType.addChild(temporaryMyTreeNodeObject);
//            } else {
//                temporaryMyTreeNodeObject = new ParseMIB.MyTreeNode<>(new sharedClass.ObjectType(objectTypeMatcher.group(1),
//                        new sharedClass.TypeOfData(objectTypeMatcher.group(5), objectTypeMatcher.group(6)),
//                        objectTypeMatcher.group(7),
//                        objectTypeMatcher.group(8),
//                        objectTypeMatcher.group(9),
//                        objectTypeMatcher.group(1),
//                        objectTypeMatcher.group(11)));
//            }
        }

//        for(ParseMIB.MyTreeNode<sharedClass.ObjectType> x : Tree) {
//            System.out.println(x.getData().name);
//            for(ParseMIB.MyTreeNode<sharedClass.ObjectType> y : x.getChildren()) {
//                System.out.println("\t" + y.getData().name + " " + y.getData().nrBranch);
//                for(ParseMIB.MyTreeNode<sharedClass.ObjectType> z : y.getChildren()) {
//                    System.out.println("\t\t" + z.getData().name + " " + z.getData().nrBranch);
//                    for(ParseMIB.MyTreeNode<sharedClass.ObjectType> w : z.getChildren()) {
//                        System.out.println("\t\t\t" + w.getData().name + " " + w.getData().nrBranch);
//                        for(ParseMIB.MyTreeNode<sharedClass.ObjectType> e : w.getChildren()) {
//                            System.out.println("\t\t\t\t" + e.getData().name + " " + e.getData().nrBranch);
//                            for(ParseMIB.MyTreeNode<sharedClass.ObjectType> r : e.getChildren()) {
//                                System.out.println("\t\t\t\t\t" + r.getData().name + " " + r.getData().nrBranch);
//                                for(ParseMIB.MyTreeNode<sharedClass.ObjectType> t : r.getChildren()) {
//                                    System.out.println("\t\t\t\t\t\t" + t.getData().name + " " + t.getData().nrBranch);
//                                    for(ParseMIB.MyTreeNode<sharedClass.ObjectType> u : t.getChildren()) {
//                                        System.out.println("\t\t\t\t\t\t\t" + u.getData().name + " " + u.getData().nrBranch);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    private void parseImports(String MibFile) {
        loadImportsFromList(loadImportsFromFile(MibFile));
    }

    public void printTree(MyTreeNode<ObjectType> objectOfTree, String branch, boolean displayDataDetails) {
//        System.out.println(branch + objectOfTree.getData().present());
//          branch = branch + "\t";
        if (displayDataDetails == true) {
            String temp = objectOfTree.getData().returnData(branch);
//            System.out.println(temp);
            System.out.println(branch + temp);

        } else {
            System.out.println(branch + objectOfTree.getData().name + " " + objectOfTree.getData().nrBranch );
        }
        if(!objectOfTree.isLeaf()) {
//            System.out.println("!isLeaf");
            for(MyTreeNode<ObjectType> x : objectOfTree.getChildren()){
                printTree(x,branch + "\t", displayDataDetails);
            }
        }
    }

    private void creatingGuiTree(DefaultMutableTreeNode parent, MyTreeNode<ObjectType> objectOfTree, String oldBranches) {
        if(!objectOfTree.isLeaf()) {
            String childBranches = new String();
            for(MyTreeNode<ObjectType> x : objectOfTree.getChildren()) {
                childBranches = oldBranches + x.getData().nrBranch + "." ;
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(x.getData().name + " " +  childBranches + "(" + x.getData().syntax.name + ")");
//                System.out.println("Parent: " + parent + " " + "child: " + x.getData().name);
                parent.add(child);
                creatingGuiTree(child, x, childBranches);
            }
        }
    }

    private JComponent displayTree(MyTreeNode<ObjectType> objectOfTree) {
            String oldBranches = objectOfTree.getData().nrBranch + ".";
            DefaultMutableTreeNode guiRoot = new DefaultMutableTreeNode(objectOfTree.getData().name + " " + oldBranches + "(" + objectOfTree.getData().syntax.name + ")");
            creatingGuiTree(guiRoot, objectOfTree, oldBranches);
            JTree guiTree = new JTree(guiRoot);
            for( int k = 0; k < guiTree.getRowCount(); k++) {
                guiTree.expandRow(k);
            }
            return new JScrollPane(guiTree);
    }

    public void displayingTreeMethod(boolean GUI){
        if(GUI){
            EventQueue.invokeLater(() -> {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                f.getContentPane().add(new MyTree().displayTree(Tree.get(0)));
                f.setSize(512, 1024);
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            });
        } else {
            printTree(Tree.get(0),"", false);
        }
    }

    public static MyTreeNode<ObjectType> findObjectInTree(MyTreeNode<ObjectType> tree, String pathToObject) {
        MyTreeNode<ObjectType> foundedObject = new MyTreeNode<>(new ObjectType());
        String[] splitedPath = pathToObject.split("\\.");
        int lastIndex = splitedPath.length-1;
        for(int i = 0; i < lastIndex; i++) {
            if (i+1 != lastIndex) {
                if(i == 0) {
                    tree = bfs(tree, splitedPath[i]);
                } else {
                    if(splitedPath[i].equals(splitedPath[i + 1])) {
                        tree = bfsValidateLastReturnedOID(tree, splitedPath[i], false);
                    } else {
                        tree = bfsValidateLastReturnedOID(tree, splitedPath[i], true);
                    }
                }
            } else {
                if(splitedPath[i].equals(splitedPath[i + 1])) {
                    foundedObject = bfsValidateLastReturnedOID(tree, splitedPath[i], false);
                } else {
                    foundedObject = bfsValidateLastReturnedOID(tree, splitedPath[i], true);
                }
//                foundedObject = bfsValidateLastReturnedOID(tree, splitedPath[i]);
            }
        }
//        System.out.println("founded object: " + foundedObject.getData().name);
        return foundedObject;
    }

    public void parseMIB(String nameMibFile) throws IOException {
        dataTypeList.add(new TypeOfData("INTEGER", "4", true));
        dataTypeList.add(new TypeOfData("OCTET STRING", "4", true));
        dataTypeList.add(new TypeOfData("OBJECT IDENTIFIER", null, true));
        dataTypeList.add(new TypeOfData("NULL", null, true));
//        List<PkgKoderBER.DataStructure> list = new ArrayList<>();
//        list.add(new PkgKoderBER.DataStructure(null, "OCTET STRING"));
//        dataTypeList.add(new sharedClass.TypeOfData("PhysAddress","context-specific" ,list, "4"));
//        dataTypeList.add(new sharedClass.TypeOfData("DisplayString","context-specific" ,list , "4"));
//
//        list.removeAll(list);
//        list.add(new PkgKoderBER.DataStructure("internet", "IpAddress"));
//        dataTypeList.add(new sharedClass.TypeOfData("NetworkAddress",  list, "4"));

        MIBfile = loadMIB(nameMibFile);
        parseImports(MIBfile);
        parseTypeOfData(MIBfile);
        loadObjectIdentifiers(MIBfile);
        loadObjectType(MIBfile);


//        System.out.println("===========IMPORT LIST===========");
//        for (ParseMIB.importClass x : importList)
//        {
//            System.out.println(x.sourceFile);
//            for(String y:  x.importedItems) {
//                System.out.println("\t" + y);
//            }
//        }

//        System.out.println("===========LISTA TYPOW===========");
//        for(sharedClass.TypeOfData y : dataTypeList) {
//            y.present();
//        }

//        System.out.println("===========PRZYKLADOWY OBJECT TYPE===========");
//        System.out.println("Object Type: ");
//        Tree.get(0).getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren().get(0).getChildren().get(0).getChildren().get(0).getData().present();

//        System.out.println("===========PRINT TREE===========");
//        printTree(Tree.get(0),"", true);

//        System.out.println("===========FIND OBJECT===========");
//        findObjectInTree(Tree.get(0), "iso.org.dod");
//        findObjectInTree(Tree.get(0), "1.3.6");

//        System.out.println("===========DISPLAY TREE===========");
        displayingTreeMethod(true);


        System.out.println("===========KODER BER===========");
        koderBER.addDataTypeList(dataTypeList);

//        BER();


    }

    public void BER(){

        Scanner scan = new Scanner(System.in);
        System.out.println("KODER BER");
        System.out.println("1 - kodowanie po typie " +
                "2 - kodowanie OID " + "3 - Wyjście");
            int typeOrOID = scan.nextInt();
        while(typeOrOID != 3) {

            switch (typeOrOID) {
                case 1:
                    int k = 0;
//                    for (TypeOfData y : dataTypeList) {
//                        System.out.println("#" + k);
//                        y.present();
//                        k++;
//                    }
                    for (String key : dataTypeMap.keySet()) {
                        System.out.println("#" + k);
                        dataTypeMap.get(key).present();
                        k++;
                    }

                    System.out.println("----------------------");
                    System.out.println("dataTypeList.size(): " + dataTypeList.size());
                    System.out.println("dataTypeMap.size(): " + dataTypeMap.size());
                    System.out.println("----------------------");

                    codedObject.addAll(koderBER.codeFrame());
                    break;

                case 2:
                    MyTreeNode<ObjectType> foundedOID;
                    printTree(Tree.get(0), "", true);
                    System.out.println("Wybierz OID do zakodowania: ");
                    String OIDpath = scan.next();
                    foundedOID = findObjectInTree(Tree.get(0), OIDpath);
//                System.out.println(foundedOID.getData().returnData(""));
                    foundedOID.getData().syntax.present();

                    //todo kodowanie sekwencji znajdujących się w OID
                    koderBER.codeFrame(foundedOID.getData().syntax);

                    //todo kodowanie samego: tagu, dlugosci i sciezki do OID
//                      codedObject.addAll(koderBER.codePathToOID(foundedOID.getData().syntax, OIDpath));
//                      codecObjectList.add(codedObject);
//
                    break;
            }
            System.out.println("1 - kodowanie po typie " +
                    "2 - kodowanie OID " + "3 - Wyjście");
            typeOrOID = scan.nextInt();
        }
    }
}

// todo 1.3.6.1.2.1.3.1
// todo 1.3.6.1.4.1
// todo 1.3.6.1.2.1.8.5.1.13
