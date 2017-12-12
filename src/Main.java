import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static String path;
    private static final int NUMBER = 100000;

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
                ArrayList<String[]> schema = getFileSchema(fileName);
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

    private static ArrayList<String[]> getFileSchema(String fileName) {
        System.out.println("getFileSchema: File name: " + fileName);
        String filePath = path + "\\" + fileName;
        ArrayList<String[]> schema = new ArrayList<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "BIG5"));
            String str = null;
            while ((str = reader.readLine()) != null) {
                String[] cols = Arrays.copyOfRange(str.split(","), 2, 5);
                schema.add(cols);
            }
        } catch (Exception e) {
            System.out.println("getFileSchema: " + e.toString());
        }
        return schema;
    }

    private static void produceData(String fileName, ArrayList<String[]> schemas) {
        BufferedWriter fw = null;

        try {
            File file = new File(path + "\\Demo_" + fileName);
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            //欄位名稱
            for (int i = 1; i < schemas.size(); i++) {
                fw.append(schemas.get(i)[0]);
                if (i != schemas.size()) {
                    fw.append(",");
                }
            }
            fw.newLine();

            //資料
            for (int k = 0; k < NUMBER; k++) {
                for (int i = 1; i < schemas.size(); i++) {
                    if (schemas.get(i)[0].equals("QTYPE")) {
                        fw.append("B");
                    } else if (schemas.get(i)[0].equals("SER_NO")) {
                        fw.append(String.valueOf(k + 1));
                    } else if (schemas.get(i)[0].contains("CARD")) {
                        fw.append(schemas.get(i)[0].substring(4));
                    } else {
                        String str = "";

                        if (schemas.get(i)[1].equals("CHAR")) {
                            int length = (int) (Math.random() * Integer.parseInt(schemas.get(i)[2])) + 1;
                            for (int j = 0; j < length; j++) {
                                str += (char) ((int) (Math.random() * 26 + 97));
                            }
                        } else {
                            for (int j = 0; j < Integer.parseInt(schemas.get(i)[2]); j++) {
                                str += String.valueOf((int) (Math.random() * 9));
                                str = str.replaceFirst("0", "");
                            }
                        }
                        fw.append(str);
                    }

                    if (i != schemas.size() - 1) {
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
