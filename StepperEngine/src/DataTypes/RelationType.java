package DataTypes;

public class RelationType extends DataType<Relation> {

    public RelationType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), false, Type.RELATION, isInput);
    }
    public RelationType(Relation relation, boolean isInput) {
        super("Relation", "Relation", false, relation, Type.RELATION, isInput);
    }

    public RelationType(Relation relation, String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), false, relation, Type.RELATION, isInput);
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