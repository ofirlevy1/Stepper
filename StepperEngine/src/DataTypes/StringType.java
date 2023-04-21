package DataTypes;

public class StringType extends DataType<String> {

    public StringType(String name) {
        super(name, name.toLowerCase().replace('_',' '), true, Type.STRING);
    }

    public StringType(String str, String name) {super(name, name.toLowerCase().replace('_',' '), true, str, Type.STRING);}

    @Override
    public String getPresentableString() {
        return data;
    }
}
