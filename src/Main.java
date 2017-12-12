import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    private static String path;
    private static final int NUMBER = 10;
    private static final int COL_NAME = 2;
    private static final int COL_TYPE = 3;
    private static final int COL_LENGTH = 4;

    public static void main(String[] args) {
        System.out.println("Start");

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choose a file");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            path = chooser.getSelectedFile().getPath();
            ArrayList<String> fileNames = new ArrayList();
            fileNames = readFiles(path);
            System.out.println("Files' name: " + fileNames);

            for (String fileName : fileNames) {
                String[][] schema = getFileSchema(fileName);

                //sort by the first column (#)
                Arrays.sort(schema, new Comparator<String[]>() {
                    @Override
                    public int compare(String[] o1, String[] o2) {
                        Integer i1 = Integer.parseInt(o1[0]);
                        Integer i2 = Integer.parseInt(o2[0]);
                        return i1.compareTo(i2);
                    }
                });

                produceData(fileName, schema);
            }

        } else {
            System.out.println("No Selection ");
        }
        System.out.println("End");
    }

    private static ArrayList readFiles(String path) {
        ArrayList allFiles = new ArrayList();
        File file = new File(path);

        if (file.isDirectory()) {
            for (String fileName : file.list()) {
                if (fileName.contains(".csv") && !fileName.contains("Demo_"))
                    allFiles.add(fileName);
            }
            return allFiles;
        }
        return null;
    }

    private static String[][] getFileSchema(String fileName) {
        System.out.println("getFileSchema: File name: " + fileName);
        String filePath = path + "\\" + fileName;
        ArrayList<String[]> schema = new ArrayList<>();

        BufferedReader reader = null;
        String[][] schemas = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "BIG5"));
            String str = null;
            while ((str = reader.readLine()) != null) {
                String[] cols = str.split(",");

                schema.add(cols);
            }
            schemas = new String[schema.size() -1][];
            schemas = Arrays.copyOfRange(schema.toArray(schemas), 1, schema.size()) ;
        } catch (Exception e) {
            System.out.println("getFileSchema: " + e.toString());
        }

        return schemas;
    }

    private static void produceData(String fileName, String[][] schemas) {
        BufferedWriter fw = null;

        try {
            File file = new File(path + "\\Demo_" + fileName);
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            //欄位名稱
            for (int i = 0; i < schemas.length; i++) {
                fw.append(schemas[i][COL_NAME]);
                if (i != schemas.length -1) {
                    fw.append(",");
                }
            }
            fw.newLine();

            //資料
            for (int k = 0; k < NUMBER; k++) {
                for (int i = 0; i < schemas.length; i++) {
                    if (schemas[i][COL_NAME].equals("QTYPE")) {
                        fw.append("B");
                    } else if (schemas[i][COL_NAME].equals("SER_NO")) {
                        fw.append(String.valueOf(k + 1));
                    } else if (schemas[i][COL_NAME].contains("CARD")) {
                        fw.append(schemas[i][COL_NAME].substring(4));
                    } else {
                        String str = "";

                        if (schemas[i][COL_TYPE].equals("CHAR")) {
                            int length = (int) (Math.random() * Integer.parseInt(schemas[i][COL_LENGTH])) + 1;
                            for (int j = 0; j < length; j++) {
                                str += (char) ((int) (Math.random() * 26 + 97));
                            }
                        } else {
                            for (int j = 0; j < Integer.parseInt(schemas[i][COL_LENGTH]); j++) {
                                str += String.valueOf((int) (Math.random() * 9));
                                str = str.replaceFirst("0", "");
                            }
                        }
                        fw.append(str);
                    }

                    if (i != schemas.length - 1) {
                        fw.append(",");
                    }
                }
                fw.newLine();
            }
            fw.flush();
        } catch (Exception e) {
            System.out.println("produceData: " + e.toString());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Exception e) {
                    System.out.println("produceData finally: " + e.toString());
                }
            }
        }
    }
}
