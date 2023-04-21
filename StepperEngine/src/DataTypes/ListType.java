package DataTypes;

import java.util.ArrayList;

public class ListType extends  DataType<ArrayList<DataType>>{

    public ListType(String name, boolean isInput) {super(name, name.toLowerCase().replace('_',' '), false, Type.LIST, isInput);}
    public ListType(ArrayList<DataType> list, boolean isInput){super("List", "List", false, list, Type.LIST, isInput);}

    public ListType(ArrayList<DataType> list, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), false, list, Type.LIST, isInput);}

    /**
     * returns a string in the format:
     * 1:'first element', 2:'second element', 3:'third element', .........
     * @return
     */
    @Override
    public String getPresentableString() {
        String prestableString="";
        if(data.isEmpty())
            return "The list is empty";
        for(int i=0;i<data.size();i++)
        {
            prestableString+=(i+1)+":"+data.get(i).getPresentableString()+", ";
        }
        return prestableString.substring(0,prestableString.length()-2);
    }
}
