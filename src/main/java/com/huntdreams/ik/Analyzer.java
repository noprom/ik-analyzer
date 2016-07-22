package com.huntdreams.ik;

import com.huntdreams.ik.util.Constant;
import com.huntdreams.ik.util.DBUtil;
import com.huntdreams.ik.util.ReaderFileListener;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/22/16 2:33 PM.
 */
public class Analyzer extends ReaderFileListener {

    // tag标签列表
    private List<String> tagList;

    private AtomicInteger error = new AtomicInteger(0);

    public Analyzer(String encoding, String tagFile) {
        this.setEncode(encoding);
        this.tagList = new ArrayList<String>();
        File file = new File(tagFile);
        try {
            // 一次性载入内存
            List<String> list = FileUtils.readLines(file, Constant.FILE_ENCODING_UTF8);
            for (String line : list) {
//                System.out.println(line);
                this.tagList.addAll(Arrays.asList(line.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void output(List<String> stringList) {

        for (String line : stringList) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                JSONObject jsonObject = new JSONObject(line);
                String pkg = jsonObject.getString("pkg");
                String desc = jsonObject.getString("desc").replace(" ", "");
                Map<String, Integer> wordMap = new HashMap<String, Integer>();
                IKSegmentation ikSeg = new IKSegmentation(new StringReader(desc), true);

                Lexeme l = null;
                try {
                    while ((l = ikSeg.next()) != null) {
                        //将CJK_NORMAL类的词写入目标文件
                        if (l.getLexemeType() == Lexeme.TYPE_CJK_NORMAL) {
                            //后续在此添加判断此词是否为停用词，若不是则写入目标文件中
                            String text = l.getLexemeText();
                            if (tagList.contains(text)) {
                                Integer count = wordMap.get(text) == null ? 0 : wordMap.get(text);
                                wordMap.put(text, count + 1);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 排序
                List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(wordMap.entrySet());

                Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return (o2.getValue() - o1.getValue());
                    }
                });

                // 输出高频词
                String result = pkg + ":";
                List<String> tags = new ArrayList<String>();
                Integer wordCount = 0;
                for (Map.Entry<String, Integer> entry : list) {
                    String tag = entry.getKey();
                    result += tag + ",";
                    tags.add(tag);
                    wordCount++;
                    if (wordCount >= 20)
                        break;
                }
                result = result.substring(0, result.length() - 1);

                // System.out.println(result);
                jsonObject.put("tag", tags);
                // System.out.println(jsonObject);
                // 保存到数据库
                saveApp(pkg, jsonObject.toString());
            } catch (JSONException e) {
                continue;
            }
        }
    }

    /**
     * 保存到数据库
     *
     * @param pkg  包名
     * @param info 包名对应的信息
     */
    private void saveApp(String pkg, String info) {
        //创建连接
        Connection conn = DBUtil.getConnection();
        //创建SQL执行工具
        QueryRunner qRunner = new QueryRunner();
        //执行SQL插入
        int n = 0;
        try {
            // 转移处理
            info = info.replace("'", "");
            String sql = "set names utf8mb4";
            qRunner.update(conn, sql);
            sql = "insert into apps(package, platform, info) values('" + pkg + "', 'google', '" + info + "')";
//            System.out.println(sql);
            qRunner.update(conn, sql);

            System.out.println("successfully insert " + pkg);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            DbUtils.closeQuietly(conn);
        }
    }
}