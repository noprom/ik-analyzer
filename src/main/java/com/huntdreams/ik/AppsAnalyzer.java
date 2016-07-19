package com.huntdreams.ik;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.*;
import java.util.*;

/**
 * AppsAnalyzer
 * app应用简介以及分词
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/18/16 9:48 PM.
 */
public class AppsAnalyzer {

    /**
     * 对app进行分词
     * 将apps.json文件中的文本进行分词,得到分词结果文件analyzer-out.txt
     *
     * @param inFile  输入文件
     * @param outFile 输出文件
     * @throws IOException
     */
    public void appAnalyzer(String inFile, String outFile) throws IOException {
        InputOutput rw = new InputOutput();

        // 1:从文件 trainFile读入训练集放在String[](its)中
        String[] its = rw.readInput(inFile);

        // 2:对训练集预处理之后形成的训练集的词集合放在一个String[]（docs）中
        String[] OutputDocs = new String[its.length];

        // 3:统计词频用
        Map<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
        List<String> pkgList = new ArrayList<String>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        int i = 0;
        while (i < its.length) {
            String line = its[i];
            JSONObject jsonObject = new JSONObject(line);
            String pkg = jsonObject.getString("pkg");
            String desc = jsonObject.getString("desc").replace(" ", "");
            String meta = jsonObject.getJSONArray("meta").toString().replace(" ", "");
            String t = desc + " " + meta;
            String row = pkg + "-->";
            IKSegmentation ikSeg = new IKSegmentation(new StringReader(t), true);

            // 统计
            HashMap pkgMap = map.get(pkg);
            if (pkgMap == null) {
                pkgMap = new HashMap<String, Integer>();
                map.put(pkg, pkgMap);
            }
            Lexeme l = null;
            while ((l = ikSeg.next()) != null) {
                //将CJK_NORMAL类的词写入目标文件
                if (l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL) {
                    //后续在此添加判断此词是否为停用词，若不是则写入目标文件中
                    String text = l.getLexemeText();
                    Integer count = pkgMap.get(text) != null ? (Integer) pkgMap.get(text) : 0;
                    // 统计
                    pkgMap.put(text, count + 1);
                    row += '|' + text;
                }
            }

            // 4:排序
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(pkgMap.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
                }
            });
            map.put(pkg, pkgMap);

            OutputDocs[i] = row;
            i++;

            // 输出高频词
            String result = pkg + ":";
            Integer wordCount = 0;
            for (Map.Entry<String, Integer> entry : list) {
                result += entry.getKey() + ",";
                wordCount++;
                if (wordCount >= 20)
                    break;
            }
            result = result.substring(0, result.length() - 1);
            writer.write(result + "\n");
            System.out.println(result);
        }
        writer.close();
    }

    /**
     * 对标签进一步分词
     *
     * @param inFile  输入文件
     * @param outFile 输出文件
     */
    public void tagAnalyzer(String inFile, String outFile) throws IOException {
        File file = new File(inFile);
        List<String> lines = FileUtils.readLines(file, "UTF-8");
        for (String line : lines) {
            IKSegmentation ikSeg = new IKSegmentation(new StringReader(line), true);
            String row = null;
            Lexeme l = null;
            while ((l = ikSeg.next()) != null) {
                //将CJK_NORMAL类的词写入目标文件
                if (l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL) {
                    //后续在此添加判断此词是否为停用词，若不是则写入目标文件中
                    String text = l.getLexemeText();
                    row += '|' + text;
                }
            }
            System.out.println(row);
        }
    }


    public static void main(String[] args) throws IOException {
        // 数据目录
        String dataPath = "data/app/";
        String jsonFile = dataPath + "apps.json";//原始json文件
        String tagsFile = dataPath + "tags";//聚类标签
        String tagsSegFile = dataPath + "tags-seg";//聚类标签分词
        String analyzerOutFile = dataPath + "analyzer-out.txt";//应用分词输出文件
        String appTagsOutFile = dataPath + "app-tags-out.txt";//应用分词过滤之后的tag

        AppsAnalyzer analyzer = new AppsAnalyzer();
        // 1.得到应用第一步分词结果
        analyzer.appAnalyzer(jsonFile, analyzerOutFile);
        // 2.百度tag分词
        analyzer.tagAnalyzer(tagsFile, tagsSegFile);
        // 3.应用分词再次过滤
    }
}
