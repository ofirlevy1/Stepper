package Flow;

import java.util.HashMap;
import java.util.HashSet;

public class FlowMap {
    // Map from steps final names - to the StepMap object that has them as a source step;
    HashMap<String, HashSet<StepMap>> map;

    public FlowMap() {
        map = new HashMap<String, HashSet<StepMap>>();
    }

    public void addMapping(StepMap stepMap) {
        if(!map.containsKey(stepMap.getSourceStepName())){
            map.put(stepMap.getSourceStepName(), new HashSet<StepMap>());
        }
        map.get(stepMap.getSourceStepName()).add(stepMap);
    }

    public HashSet<StepMap> getMappingsByStep(String stepName) {
        return map.get(stepName);
    }
}
