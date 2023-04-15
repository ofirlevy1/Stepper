package Steps;

import DataTypes.DataType;
import DataTypes.Relation;
import DataTypes.RelationType;
import DataTypes.StringType;

import java.util.ArrayList;

public class PropertiesExporterStep extends Step{
    private RelationType source;
    public PropertiesExporterStep(RelationType source) {
        super("PROPERTIES_EXPORTER", true);
        this.source = source;
    }

    @Override
    public void execute() {
        try{
            this.runStepFlow();
        } catch (EmptyPropertiesRelationException e) {
            this.outputs.add(new StringType(""));
            this.setStatus(Status.Warning);
            this.setSummaryLine(e.getMessage());
            this.addLog(e.getMessage());
        } catch (Exception e) {
            this.setSummaryLine(e.getMessage());
            this.addLog(e.getMessage());
            this.setStatus(Status.Failure);
        }
    }

    @Override
    protected void runStepFlow() throws Exception {
        Relation relation=(Relation) source.getData();
        String properties="";
        this.addLog("About to process "+relation.getRows()+" lines of data");
        if(relation.getRows()==0&&relation.getCols()==0) throw new EmptyPropertiesRelationException("Properties Relation is empty");
        for(int i=0;i<relation.getRows();i++){
            for(int j=0;j< relation.getCols();j++){
                properties+="row-"+(i+1)+"."+relation.getColumnNames()[j]+"="+relation.get(i,j)+"\n";
            }
        }

        this.setStatus(Status.Success);
        this.addLog("Extracted total of "+(relation.getRows()* relation.getCols())+" properties");
        this.setSummaryLine("Extracted total of "+(relation.getRows()* relation.getCols())+" properties");
        this.outputs.add(new StringType(properties.substring(0,properties.length()-1)));//getting rid of unnecessary new line at end of string
    }

    public class EmptyPropertiesRelationException extends Exception{
        public EmptyPropertiesRelationException(String str){
            super(str);
        }
    }
}
