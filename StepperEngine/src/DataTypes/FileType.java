package DataTypes;


import java.io.File;

public class FileType extends DataType<File>{
    public FileType(String name) {
        super(name, name.toLowerCase().replace('_',' '), false, Type.FILE);
    }
    public FileType(File file){super("File", "File", false, file, Type.FILE);}

    public FileType(File file, String name){super(name, name.toLowerCase().replace('_',' '), false, file, Type.FILE);}

    @Override
    public String getPresentableString() {
        return data.getAbsolutePath();
    }

}
