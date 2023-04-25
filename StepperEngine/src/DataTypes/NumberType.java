package DataTypes;

public class NumberType extends DataType<Integer> implements UserFriendly{

    public NumberType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), Type.NUMBER, isInput);
    }
    public NumberType(Integer num, boolean isInput) {
        super("Number", "Number", num, Type.NUMBER, isInput);
    }

    public NumberType(Integer num, String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), num, Type.NUMBER, isInput);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }

    @Override
    public void setData(String dataStr) {
        this.data = Integer.parseInt(dataStr);
    }
}