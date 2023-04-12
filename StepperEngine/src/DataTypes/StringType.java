package DataTypes;

public class StringType extends DataType<String> {

    public StringType(String str) {
        super("String", "String", true, str);
    }

    @Override
    public String getPresentableString() {
        return data;
    }
}
