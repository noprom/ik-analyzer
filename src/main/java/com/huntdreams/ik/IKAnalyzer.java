package com.huntdreams.ik;

import java.io.IOException;

/**
 * IK分词器
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/18/16 7:17 PM.
 */
public class IKAnalyzer {

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
