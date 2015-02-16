package info.persistent.dex;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileReportOutput implements ReportOutput {

    PrintWriter fileWriter;

    public FileReportOutput() {
    }

    @Override
    public void output(String string) {
        try {
            fileWriter = new PrintWriter("output.txt");
            fileWriter.print(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
}
