package DataTypes;

public class RelationType extends DataType<Relation> {

    public RelationType(String name) {
        super(name, "Relation", false, Type.RELATION);
    }
    public RelationType(Relation relation) {
        super("Relation", "Relation", false, relation, Type.RELATION);
    }

    public RelationType(Relation relation, String name) {
        super(name, name, false, relation, Type.RELATION);
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