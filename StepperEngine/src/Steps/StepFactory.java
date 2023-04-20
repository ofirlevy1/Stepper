package Steps;

import java.util.Locale;

public class StepFactory {

    public Step createStep(String stepName) {
        switch (stepName.toLowerCase()) {
            case "collect files in folder":
                return new CollectFilesInFolderStep();
            case "spend some time":
                return new SpendSomeTimeStep();
            case "files deleter":
                return new FilesDeleterStep();
            case "files renamer":
                return new FilesRenamerStep();
            case "files content extractor":
                return new FilesContentExtractorStep();
            case "csv exporter":
                return new CsvExporterStep();
            case "properties exporter":
                return new PropertiesExporterStep();
            case "file dumper":
                return new FileDumperStep();
        }
        return null;
    }
}
