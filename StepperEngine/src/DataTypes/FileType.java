package DataTypes;


import java.io.File;

public class FileType extends DataType<File>{
    public FileType(String name, boolean isInput) {
        super(name, name.toLowerCase().replace('_',' '), false, Type.FILE, isInput);
    }
    public FileType(File file, boolean isInput){super("File", "File", false, file, Type.FILE, isInput);}

    public FileType(File file, String name, boolean isInput){super(name, name.toLowerCase().replace('_',' '), false, file, Type.FILE, isInput);}

    @Override
    public String getPresentableString() {
        return data.getAbsolutePath();
    }

}
