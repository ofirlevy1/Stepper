//package Steps;
//
//import DataTypes.*;
//
//import java.util.ArrayList;
//
//public class HTTPCallStep extends Step {
//    // inputs:
//    private StringType resource;
//    private StringType address;
//    private EnumeratorType protocol;
//    private EnumeratorType method;
//    private JsonType requestBody;
//
//    // outputs:
//    private NumberType responseStatusCode;
//    private StringType responseBody;
//
//    public enum Protocols { http, https }
//    public enum Methods {GET, PUT, POST, DELETE}
//
//    public HTTPCallStep() {
//        super("HTTP Caller", false);
//        resource = new StringType("RESOURCE", true);
//        address = new StringType("ADDRESS", true);
//        protocol = new EnumeratorType("PROTOCOL", true, Protocols.class);
//        method = new EnumeratorType("PROTOCOL", true, Methods.class);
//        requestBody = new JsonType("BODY", true);
//        responseStatusCode = new NumberType("CODE", false);
//        responseBody = new StringType("RESPONSE_BODY", false);
//    }
//
//
//    @Override
//    protected void updateStaticTimers() {
//
//    }
//
//    @Override
//    protected void outerRunStepFlow() {
//
//    }
//
//    @Override
//    protected void runStepFlow() throws Exception {
//
//    }
//
//    @Override
//    public void setInputs(DataType... inputs) {
//
//    }
//
//    @Override
//    public void setInputByName(DataType input, String inputName) {
//
//    }
//
//    @Override
//    public ArrayList<DataType> getOutputs(String... outputNames) {
//    }
//
//    @Override
//    public ArrayList<DataType> getAllData() {
//        ArrayList<DataType> allData = new ArrayList<>();
//        allData.add(resource);
//        allData.add(address);
//        allData.add(protocol);
//        allData.add(method);
//        allData.add(requestBody);
//        allData.add(responseStatusCode);
//        allData.add(responseBody);
//        return allData;
//    }
//
//    @Override
//    public void clearDataMembers() {
//
//    }
//}
