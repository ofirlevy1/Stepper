package DataTypes;


import java.io.File;

public class FileType extends DataType<File>{

    public FileType(File file){super("File", "File", false, file);}

    @Override
    public String getPresentableString() {
        return data.getAbsolutePath();
    }

}
