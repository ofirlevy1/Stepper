package DataTypes;

public class RelationType extends StepDataType<Relation> {

    public RelationType(Relation relation) {
        super("Relation", "Relation", false, relation);
    }


    /**
     * This returns a presentable string of the format:
     * "<rowNumber" rows: <row1Name>, <row2Name>, ...
     * @return a presentable string
     */
    @Override
    public String getPresentableString() {
        String result = data.getRows() + " rows: ";
        String[] colNames = data.getColumnNames();
        for (String columName : colNames)
            result += columName + ", ";
        return result;
    }
}