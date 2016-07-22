package com.huntdreams.ik;

import com.huntdreams.ik.util.ReaderFileListener;

import java.util.List;

/**
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/22/16 2:33 PM.
 */
public class Analyzer extends ReaderFileListener {

    public Analyzer(String encoding) {
        this.setEncode(encoding);
    }

    @Override
    public void output(List<String> stringList) throws Exception {
        for (String line : stringList) {
            System.out.println("--->");
        }
    }
}
