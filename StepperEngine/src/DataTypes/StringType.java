package DataTypes;

public class StringType extends DataType<String> implements UserFriendly{

    public StringType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), Type.STRING, isInput);
    }

    public StringType(String str, String name, boolean isInput) {super(name, name.toLowerCase().replace('_',' '), str, Type.STRING, isInput);}

    @Override
    public String getPresentableString() {
        return data;
    }

    @Override
    public void setData(String dataStr) {
        this.data = dataStr;
    }
}
