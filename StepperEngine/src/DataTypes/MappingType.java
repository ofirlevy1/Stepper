package DataTypes;

public class MappingType extends DataType<Mapping>{

    public MappingType(String name) {
        super(name, "Mapping", false, Type.MAPPING);
    }

    public MappingType(Mapping mapping){super("Mapping", "Mapping", false, mapping, Type.MAPPING);}
    public MappingType(Mapping mapping, String name){super(name, name, false, mapping, Type.MAPPING);}
    @Override
    public String getPresentableString() {
        return "car: "+data.getCar()+"\ncdr: "+data.getCdr();
    }
}
