import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static String PATH = "C:\\Users\\tn955\\Downloads\\88";
    private static int NUMBER = 10;

    public static void main(String[] args) {
        System.out.println("Start");

        ArrayList<String> fileNames = new ArrayList();
        fileNames = readFiles(PATH);
        System.out.println("Files' name: " + fileNames);

        for (String fileName: fileNames){
            ArrayList<String[]> schema = getFileSchema(fileName);
            produceData(fileName, schema);
        }

    }

    private static ArrayList readFiles(String path){
        ArrayList allFiles = new ArrayList();
        File file = new File(path);

        if (file.isDirectory()){
            for (String fileName: file.list()){
                if (fileName.contains(".csv") && !fileName.contains("Demo_"))
                    allFiles.add(fileName);
            }
            return allFiles;
        }
        return null;
    }

    private static ArrayList<String[]> getFileSchema(String fileName){
        System.out.println("getFileSchema: File name: " + fileName);
        String filePath = PATH + "\\" + fileName;
        ArrayList<String[]> schema = new ArrayList<>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "BIG5"));
            String str = null;
            while ((str = reader.readLine()) != null){
                String[] cols = Arrays.copyOfRange(str.split(","), 2, 5) ;
                schema.add(cols);
            }
        }catch (Exception e){
            System.out.println("getFileSchema: " + e.toString());
        }
        return schema;
    }

    private static void produceData(String fileName ,ArrayList<String[]> schemas){
        BufferedWriter fw = null;

        try{
            File file = new File(PATH + "\\Demo_" + fileName);
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            //欄位名稱
            for (int i = 1; i < schemas.size(); i++){
                fw.append(schemas.get(i)[0]);
                if (i != schemas.size()){
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
                        fw.append(String.valueOf(k+1));
                    } else if (schemas.get(i)[0].contains("CARD")) {
                        fw.append(schemas.get(i)[0].substring(4));
                    } else {
                        String str = "";

                        if (schemas.get(i)[1].equals("CHAR")) {
                            System.out.println(schemas.get(i)[2].toString());
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
        }catch (Exception e){
            System.out.println("produceData: " + e.toString());
        }finally {
            if (fw != null){
                try {
                    fw.close();
                }catch (Exception e){
                    System.out.println("produceData finally: " + e.toString());
                }
            }
        }
    }
}
