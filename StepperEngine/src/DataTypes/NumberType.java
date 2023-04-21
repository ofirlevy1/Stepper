package DataTypes;

public class NumberType extends DataType<Integer> {

    public NumberType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), true, Type.NUMBER, isInput);
    }
    public NumberType(Integer num, boolean isInput) {
        super("Number", "Number", true, num, Type.NUMBER, isInput);
    }

    public NumberType(Integer num, String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), true, num, Type.NUMBER, isInput);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}