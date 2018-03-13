package ParseMIB;

import java.util.ArrayList;
import java.util.List;

public class importObjectIdentifier {
     String child = null;
     String rawRegex = null;
     List<String> importParents = new ArrayList<String>();

     importObjectIdentifier(String child, String rawData) {
         this.child = child;
         this.rawRegex = rawData;
     }

}
