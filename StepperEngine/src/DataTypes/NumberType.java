package DataTypes;

public class NumberType extends DataType<Integer> {

    public NumberType(Integer num) {
        super("Number", "Number", true, num);
    }

    @Override
    public String getPresentableString() {
        return data.toString();
    }
}