package DataTypes;

public class DoubleType extends DataType<Double> implements UserFriendly{
    public DoubleType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), Type.DOUBLE, isInput);
    }
    public DoubleType(Double num, boolean isInput) {
        super("Double", "Double", num, Type.DOUBLE, isInput);
    }

    public DoubleType(Double num, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), num, Type.DOUBLE, isInput);}

    @Override
    public String getPresentableString() {
        return getData().toString();
    }

    @Override
    public void setData(String dataStr) {
        setData(Double.parseDouble(dataStr));
    }
}