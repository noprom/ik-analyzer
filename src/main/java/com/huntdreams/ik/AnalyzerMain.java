package com.huntdreams.ik;

import com.huntdreams.ik.util.Constant;
import com.huntdreams.ik.util.ReadFile;
import com.huntdreams.ik.util.ReadFileThread;
import org.json.JSONObject;

import java.io.*;

/**
 * Analyzer
 * 1.将app应用简介以及分词
 * 2.保存到mysql
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/22/16 1:59 PM.
 */
public class AnalyzerMain {

    // 处理的最大线程个数
    private int maxThread = 50;

    /**
     * 开始处理
     *
     * @param fileName 文件名
     * @param tagFile  过滤用的tag文件
     */
    public void run(String fileName, String tagFile) {
        File file = new File(fileName);
        FileInputStream fis = null;
        try {
            ReadFile readFile = new ReadFile();
            fis = new FileInputStream(file);
            int available = fis.available();
            int maxThreadNum = this.maxThread;
            // 线程粗略开始位置
            int i = available / maxThreadNum;
            for (int j = 0; j < maxThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < maxThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;
                // 具体监听实现
                Analyzer listeners = new Analyzer(Constant.FILE_ENCODING_UTF8, tagFile);
                new ReadFileThread(listeners, startNum, endNum, file.getPath()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        AnalyzerMain analyzer = new AnalyzerMain();
        String jsonFile = "data/apps.json";
        String tagFile = "data/tags.seg";
        if (args.length == 1) {
            jsonFile = args[0];
        }
        analyzer.run(jsonFile, tagFile);
    }
}
