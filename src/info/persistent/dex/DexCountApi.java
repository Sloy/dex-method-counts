/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.persistent.dex;

import com.android.dexdeps.DexData;
import com.android.dexdeps.DexDataException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class DexCountApi {
    private static final String CLASSES_DEX = "classes.dex";

    private NodeFormatter nodeFormatter = new TreeNodeFormatter();
    private ReportOutput reportOutput = new FileReportOutput();

    void generateReport(Config config) {
        try {
            for (String fileName : collectFileNames(config.inputFileNames)) {
                MethodCountNode methodCountTree = countMethodsFromFile(config, fileName);
                String output = nodeFormatter.formatNodeTree(methodCountTree);
                reportOutput.output(output);
            }
        } catch (IOException ioe) {
            if (ioe.getMessage() != null) {
                System.err.println("Failed: " + ioe);
            }
            System.exit(1);
        } catch (DexDataException dde) {
            /* a message was already reported, just bail quietly */
            System.exit(1);
        }
    }

    private MethodCountNode countMethodsFromFile(Config config, String fileName) throws IOException {
        System.out.println("Processing " + fileName);
        RandomAccessFile raf = openInputFile(fileName);

        DexData dexData = new DexData(raf);
        dexData.load();

        DexMethodCounts dexMethodCounts = new DexMethodCounts();
        MethodCountNode methodCountTree = dexMethodCounts.generate(
                dexData, config.includeClasses, config.packageFilter, config.maxDepth, config.filter);

        raf.close();
        return methodCountTree;
    }

    /**
     * Opens an input file, which could be a .dex or a .jar/.apk with a
     * classes.dex inside.  If the latter, we extract the contents to a
     * temporary file.
     *
     * @param fileName the name of the file to open
     */
    RandomAccessFile openInputFile(String fileName) throws IOException {
        RandomAccessFile raf;

        raf = openInputFileAsZip(fileName);
        if (raf == null) {
            File inputFile = new File(fileName);
            raf = new RandomAccessFile(inputFile, "r");
        }

        return raf;
    }

    /**
     * Tries to open an input file as a Zip archive (jar/apk) with a
     * "classes.dex" inside.
     *
     * @param fileName the name of the file to open
     * @return a RandomAccessFile for classes.dex, or null if the input file
     *         is not a zip archive
     * @throws IOException if the file isn't found, or it's a zip and
     *         classes.dex isn't found inside
     */
    RandomAccessFile openInputFileAsZip(String fileName) throws IOException {
        ZipFile zipFile;

        /*
         * Try it as a zip file.
         */
        try {
            zipFile = new ZipFile(fileName);
        } catch (FileNotFoundException fnfe) {
            /* not found, no point in retrying as non-zip */
            System.err.println("Unable to open '" + fileName + "': " +
                fnfe.getMessage());
            throw fnfe;
        } catch (ZipException ze) {
            /* not a zip */
            return null;
        }

        /*
         * We know it's a zip; see if there's anything useful inside.  A
         * failure here results in some type of IOException (of which
         * ZipException is a subclass).
         */
        ZipEntry entry = zipFile.getEntry(CLASSES_DEX);
        if (entry == null) {
            System.err.println("Unable to find '" + CLASSES_DEX +
                "' in '" + fileName + "'");
            zipFile.close();
            throw new ZipException();
        }

        InputStream zis = zipFile.getInputStream(entry);

        /*
         * Create a temp file to hold the DEX data, open it, and delete it
         * to ensure it doesn't hang around if we fail.
         */
        File tempFile = File.createTempFile("dexdeps", ".dex");
        //System.out.println("+++ using temp " + tempFile);
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        tempFile.delete();

        /*
         * Copy all data from input stream to output file.
         */
        byte copyBuf[] = new byte[32768];
        int actual;

        while (true) {
            actual = zis.read(copyBuf);
            if (actual == -1)
                break;

            raf.write(copyBuf, 0, actual);
        }

        zis.close();
        raf.seek(0);

        return raf;
    }

    /**
     * Checks if input files array contain directories and
     * adds it's contents to the file list if so.
     * Otherwise just adds a file to the list.
     *
     * @return a List of file names to process
     */

    private List<String> collectFileNames(String[] inputFileNames) {
        List<String> fileNames = new ArrayList<String>();
        for (String inputFileName : inputFileNames) {
            File file = new File(inputFileName);
            if (file.isDirectory()) {
                String dirPath = file.getAbsolutePath();
                for (String fileInDir: file.list()){
                    fileNames.add(dirPath + File.separator + fileInDir);
                }
            } else {
                fileNames.add(inputFileName);
            }
        }
        return fileNames;
    }

}
