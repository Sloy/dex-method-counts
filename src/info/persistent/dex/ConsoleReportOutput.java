package info.persistent.dex;

import java.io.PrintStream;

public class ConsoleReportOutput implements ReportOutput{
    PrintStream out;

    public ConsoleReportOutput() {
        this.out = System.out;
    }

    @Override
    public void output(String string) {
        out.print(string);
    }
}
