package ParseMIB;

import java.util.ArrayList;
import java.util.List;

public class importClass {
    String rawRegexData = null;
    List<String> importedItems = new ArrayList<String>();
    String sourceFile;

    importClass(){

    }

    importClass(String rawData, String sourceFile) {
        this.rawRegexData = rawData;
        this.sourceFile = sourceFile;
    }

    public void addSingleItem(String item) {
        this.importedItems.add(item);
    }

    public String getSourceFile() { return sourceFile; }

    public boolean haveImports() {
        if (this.sourceFile != null) {
            return true;
        } else {
            return false;
        }
    }

}
