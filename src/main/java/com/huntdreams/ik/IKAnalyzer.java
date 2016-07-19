package com.huntdreams.ik;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IK分词器
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/18/16 7:17 PM.
 */
public class IKAnalyzer {

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
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(line);
            line = m.replaceAll("");
            System.out.println("----->" + line);
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

    public static void main(String[] args) throws IOException {
        InputOutput rw = new InputOutput();

        //************************训练集的预处理，训练集放在Data目录下
        ////子功能2：训练集的预处理（将训练集进行切词等预处理，形成原始词集合）
        String dir = "data/";
        String trainFile = dir + "InitTrainSet.txt";

        //InputOutput rw=new InputOutput();
        PreProcess p = new PreProcess();

        //1 从文件 trainFile读入训练集放在String[](its)中
        String[] its = rw.readInput(trainFile);

        //2 对训练集预处理之后形成的训练集的词集合放在一个String[]（docs）中
        String[] docs = p.preProcessMain(its);

        //3 将放在String[]中的训练集写入文件
        String trainFileSeg = trainFile.substring(0, trainFile.lastIndexOf(".")) + "Segment.txt";
        rw.writeOutput(docs, trainFileSeg);
        System.out.println("预处理完毕");
    }
}
