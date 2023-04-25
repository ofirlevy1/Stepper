package Stepper;

/*

Things To Consider:

*


 */


import Flow.*;
import Generated.STFlow;
import Generated.STStepper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class Stepper {

    HashSet<Flow> flows;
    String exceptionString;


    // attempting to load from an invalid file should NOT override any data.
    public Stepper(String xmlFilePath) throws FileNotFoundException, JAXBException{
        STStepper stStepper = deserializeFrom(new FileInputStream(new File(xmlFilePath)));
        flows = new HashSet<>();
        for(STFlow stFlow : stStepper.getSTFlows().getSTFlow()) {
            try {
                flows.add(new Flow(stFlow));
            }
            // input validation exception
            catch (RuntimeException e){
                exceptionString=e.getMessage();
                throw e;
            }
        }
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return getFlowByName(flowName).getFlowDescriptor();
    }

    private Flow getFlowByName(String flowName) {
        for(Flow flow : flows) {
            if(flow.getName().equals(flowName))
                return flow;
        }
        throw new RuntimeException("getFlowByName: flow '" + flowName + " not found");
    }

    private STStepper deserializeFrom(FileInputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("Generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (STStepper) u.unmarshal(in);
    }

    public ArrayList<String> getFlowNames(){
        ArrayList<String> flowNames = new ArrayList<>();
        for(Flow flow : flows)
            flowNames.add(flow.getName());
        return flowNames;
    }

    public ArrayList<FreeInputDescriptor> getFreeInputDescriptorsByFlow(String flowName) {
        return getFlowByName(flowName).getFreeInputsDescriptors();
    }

    public void setFreeInput(String flowName, String freeInputEffectiveName, String dataStr) {
        getFlowByName(flowName).setFreeInput(freeInputEffectiveName, dataStr);
    }

    public boolean areAllMandatoryFreeInputsSet(String flowName) {
        return getFlowByName(flowName).areAllMandatoryFreeInputsSet();
    }

    public void runFlow(String flowName) {
        getFlowByName(flowName).execute();
    }
}
