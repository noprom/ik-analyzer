package com.huntdreams.ik;

import org.json.JSONObject;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * AppsAnalyzer
 * app应用简介以及分词
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/18/16 9:48 PM.
 */
public class AppsAnalyzer {

    // 数据目录
    private String dataPath = "data/app/";
    private String jsonFile = dataPath + "apps.json";
    private String outFileName = dataPath + "out.txt";

    /**
     * 对app进行分词
     *
     * @throws IOException
     */
    public void appAnalyzer() throws IOException {
        InputOutput rw = new InputOutput();

        // 1:从文件 trainFile读入训练集放在String[](its)中
        String[] its = rw.readInput(jsonFile);

        // 2:对训练集预处理之后形成的训练集的词集合放在一个String[]（docs）中
        String[] OutputDocs = new String[its.length];

        // 3:统计词频用
        Map<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String, Integer>>();
        List<String> pkgList = new ArrayList<String>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFileName));
        int i = 0;
        while (i < its.length) {
            String line = its[i];
//            System.out.println(line);
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
//                        + entry.getValue() + ",";
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
        analyzer.appAnalyzer();
    }
}
