package Flow;

import Generated.STContinuation;
import Generated.STContinuationMapping;

import java.util.HashMap;

public class Continuation {
    String targetFlow;
    HashMap<String, String> dataMap;

    public Continuation(STContinuation stContinuation) {
        targetFlow = stContinuation.getTargetFlow();
        dataMap = new HashMap<>();
        for(STContinuationMapping dataMapping : stContinuation.getSTContinuationMapping()) {
            dataMap.put(dataMapping.getSourceData(), dataMapping.getTargetData());
        }
    }

    public boolean hasCustomContinuationDataMappings() {
        return dataMap != null && dataMap.size() > 0;
    }

    public HashMap<String, String> getDataMap() {
        return dataMap;
    }
}
