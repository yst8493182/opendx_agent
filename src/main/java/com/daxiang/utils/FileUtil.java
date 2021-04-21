package com.daxiang.utils;

import javax.validation.constraints.NotNull;
import java.io.*;

public class FileUtil {

    //文件保存的工具函数
    public static void saveFile(String content, @NotNull String filePath) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            fileWriter.write(content);
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取文件的工具函数
    public static String readFile(@NotNull String filePath) {
        FileReader fileReader = null;
        String result = "";
        String res;
        try {
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((res = bufferedReader.readLine()) != null) {
                result += res;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
