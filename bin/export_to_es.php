<?php

/**
 * 将谷歌play的原始文件打标签之后转为es所用格式
 *
 * @param $gpfile gp原始文件
 * @param $segfile 分词结果文件
 * @param $esfile es格式文件
 */
function gp2es($gpfile, $segfile, $esfile)
{
    $handler = fopen($gpfile, "rb");
    while(!feof($handler)) {
        $line = fgets($handler);
        $line = json_decode($line, true);
        $pkg = $line["pkg"];
        $line["tag"] = get_tag_by_pkg($segfile, $pkg);
        $line = json_encode($line, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
        file_put_contents($esfile, $line, FILE_APPEND | LOCK_EX);
    }
    fclose($handler);
}

/**
 * 通过包名获得分词之后的tag
 *
 * @param $filename 分词结果文件
 * @param $pkg 应用包名
 * @return 标签名
 */
function get_tag_by_pkg($filename, $pkg)
{
    $handler = fopen($filename, "rb");
    while(!feof($handler)) {
        $line = fgets($handler);
        $arr = explode(":", $line);
        if(count($arr) != 2)
            continue;
        if($pkg == $arr[0])
            return $arr[1];
    }
    fclose($handler);
}