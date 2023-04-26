package DataTypes;

import java.util.ArrayList;

public class ListType extends  DataType<ArrayList<DataType>>{

    public ListType(String name, boolean isInput) {super(name, name.toLowerCase().replace('_',' '), Type.LIST, isInput);}
    public ListType(ArrayList<DataType> list, boolean isInput){super("List", "List", list, Type.LIST, isInput);}

    public ListType(ArrayList<DataType> list, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), list, Type.LIST, isInput);}

    /**
     * returns a string in the format:
     * 1:'first element', 2:'second element', 3:'third element', .........
     * @return
     */
    @Override
    public String getPresentableString() {
        String prestableString="";
        if(getData().isEmpty())
            return "The list is empty";
        for(int i=0;i<getData().size();i++)
        {
            prestableString+=(i+1)+":"+getData().get(i).getPresentableString()+", ";
        }
        return prestableString.substring(0,prestableString.length()-2);
    }
}
