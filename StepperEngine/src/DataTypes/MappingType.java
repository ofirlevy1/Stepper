package DataTypes;

public class MappingType extends DataType<Mapping>{

    public MappingType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), false, Type.MAPPING, isInput);
    }

    public MappingType(Mapping mapping, boolean isInput){super("Mapping", "Mapping", false, mapping, Type.MAPPING, isInput);}
    public MappingType(Mapping mapping, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), false, mapping, Type.MAPPING, isInput);}
    @Override
    public String getPresentableString() {
        return "car: "+data.getCar()+"\ncdr: "+data.getCdr();
    }
}
