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
    $apps = [];
    while(!feof($handler)) {
        $line = trim(fgets($handler));
        $line = json_decode($line, true);
        if(empty($line))
            continue;
        $pkg = $line["pkg"];
        $line["tag"] = get_tag_by_pkg($segfile, $pkg);
        $apps[] = $line;
    }
    $apps = json_encode($apps, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    file_put_contents($esfile, $apps);
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

//升级为申请4096M内存
ini_set('memory_limit', '4096M');
$data_dir = "json/";
$gpfile = ["app.sgp-gp-01.tmp.json"];//原始下载下来的gp文件
$segfile = $data_dir . "app-tags-out.txt";//分词结果汇总文件

foreach($gpfile as $gp) {
    gp2es($data_dir . $gp, $segfile, $data_dir . "es." . $gp);
}
