package Stepper;

/*

Things To Consider:

*


 */


import Flow.Flow;
import Generated.STFlow;
import Generated.STStepper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;

public class Stepper {

    HashSet<Flow> flows;

    // attempting to load from an invalid file should NOT override any data.
    public Stepper(STStepper stStepper) {
        flows = new HashSet<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow()) {
            flows.add(new Flow(stFlow));
        }
    }

}
