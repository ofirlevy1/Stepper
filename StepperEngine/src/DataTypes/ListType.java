package DataTypes;

import java.util.ArrayList;

public class ListType extends  DataType<ArrayList<DataType>>{

    public ListType(String name) {super(name, "List", false, Type.LIST);}
    public ListType(ArrayList<DataType> list){super("List", "List", false, list, Type.LIST);}

    public ListType(ArrayList<DataType> list, String name){super(name, name, false, list, Type.LIST);}

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
