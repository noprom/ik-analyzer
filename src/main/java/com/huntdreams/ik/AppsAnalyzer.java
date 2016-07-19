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

    private static final String FILE_ENCODING = "UTF-8";

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
        File file = new File(outFile);
        System.out.println(outFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        List<String> inList = FileUtils.readLines(new File(inFile), FILE_ENCODING);

        // 统计词频用
        Map<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        for (String line : inList) {
            // 替换\t等
            line = line.replaceAll("\\s+", "");
            line = line.replaceAll("\\t+", "");
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
        List<String> lines = FileUtils.readLines(file, FILE_ENCODING);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        for (String line : lines) {
            IKSegmentation ikSeg = new IKSegmentation(new StringReader(line), true);
            String row = line.split(",")[0];
            Lexeme l = null;
            while ((l = ikSeg.next()) != null) {
                //将CJK_NORMAL类的词写入目标文件
                if (l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL) {
                    //后续在此添加判断此词是否为停用词，若不是则写入目标文件中
                    String text = l.getLexemeText();
                    row += ',' + text;
                }
            }
            // 写入文件
            writer.write(row + "\n");
            System.out.println(row);
        }
        writer.close();
    }

    /**
     * 对app分词进行过滤
     *
     * @param appTagFileName    原始app分词结果文件
     * @param filterTagFileName 标签过滤文件
     * @param outFileName       过滤输出结果文件
     */
    public void tagFilter(String appTagFileName, String filterTagFileName, String outFileName) throws IOException {
        // app分词结果
        Map<String, ArrayList<String>> apps = new HashMap<String, ArrayList<String>>();
        // 过滤分词
        Map<String, ArrayList<String>> filterTags = new HashMap<String, ArrayList<String>>();
        // 过滤结果
        Map<String, ArrayList<String>> tagResult = new HashMap<String, ArrayList<String>>();
        Map<String, HashMap<String, Integer>> filterResult = new HashMap<String, HashMap<String, Integer>>();
        // 写结果
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
        File appTagFile = new File(appTagFileName);
        File filterTagFile = new File(filterTagFileName);
        List<String> appTagLines = FileUtils.readLines(appTagFile, FILE_ENCODING);
        List<String> filterTagLines = FileUtils.readLines(filterTagFile, FILE_ENCODING);
        // 填充app
        for (String line : appTagLines) {
            String[] arr = line.split(":");
            ArrayList<String> tags = new ArrayList<String>();
            for (String tag : arr[1].split(",")) {
                tags.add(tag);
            }
            apps.put(arr[0], tags);
        }
        // 填充过滤的分词
        for (String line : filterTagLines) {
            String[] arr = line.split(",");
            ArrayList<String> tags = new ArrayList<String>();
            for (int i = 1; i < arr.length; i++) {
                tags.add(arr[i]);
            }
            filterTags.put(arr[0], tags);
        }
        // 开始过滤
        for (Map.Entry<String, ArrayList<String>> app : apps.entrySet()) {
            ArrayList<String> filteredTag = new ArrayList<String>();
            String appLine = app.getKey() + ":";
            for (String appTag : app.getValue()) {
                for (Map.Entry<String, ArrayList<String>> filter : filterTags.entrySet()) {
                    for (String filterTag : filter.getValue()) {
                        if (appTag.contains(filterTag)) {
                            // 将结果加进该app对应的标签中
                            if (filteredTag.size() == 0)
                                filteredTag.add(filter.getKey());
                            else
                                filteredTag.set(0, filter.getKey());
                            filteredTag.add(filterTag);
                            appLine += filterTag + ",";
                        }
                    }
                }
            }
            appLine = appLine.substring(0, appLine.length() - 1);
            System.out.println(appLine);
            tagResult.put(app.getKey(), filteredTag);
        }
        // 统计结果排序输出
        for (Map.Entry<String, ArrayList<String>> item : tagResult.entrySet()) {
            HashMap<String, Integer> weight = new HashMap<String, Integer>();
            String pkg = item.getKey();
            for (String tag : item.getValue()) {
                Integer count = weight.get(tag) != null ? weight.get(tag) : 0;
                weight.put(tag, count + 1);
            }
            filterResult.put(pkg, weight);

            // 排序
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(weight.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return (o2.getValue() - o1.getValue());
                }
            });

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

    public static void main(String[] args) throws IOException {
        AppsAnalyzer analyzer = new AppsAnalyzer();
        // 数据目录
        String defaultDir = "/tmp/ik/";
        String dataPath = "data/app/";
        if (args.length == 2) {
            dataPath = defaultDir;
        }

        String jsonFile = dataPath + "apps.json";//原始json文件
        String tagsFile = dataPath + "tags";//聚类标签
        String tagsSegFile = dataPath + "tags-seg";//聚类标签分词
        String analyzerOutFile = dataPath + "analyzer-out.txt";//应用分词输出文件
        String appTagsOutFile = dataPath + "app-tags-out.txt";//应用分词过滤之后的tag

        // 1.得到应用第一步分词结果
        analyzer.appAnalyzer(jsonFile, analyzerOutFile);
        // 2.百度tag分词
        analyzer.tagAnalyzer(tagsFile, tagsSegFile);
        // 3.应用分词再次过滤
        analyzer.tagFilter(analyzerOutFile, tagsSegFile, appTagsOutFile);
    }
}
