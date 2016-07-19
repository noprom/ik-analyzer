<?php

function filter_file($infilename, $outfilename)
{
	`rm -rf $outfilename`;
	$handler = fopen($infilename, "rb");
	while (!feof($handler)) {
		$line = fgets($handler);
		$json = json_decode($line, true);
		$pkg = $json["pkg"];
		$info = $json["desc"];
		$info = preg_replace("/[ \t\n\r]+/", ' ', $info); //匹配所有空格
		$line = $pkg . "--->" . $info . PHP_EOL;
		// $line = iconv("utf-8", "gb2312", $line);
		// $line = mb_convert_encoding($line, "UTF-8", "GBK");
		echo $line;
		file_put_contents($outfilename, $line, FILE_APPEND | LOCK_EX);
	}
}

function start_transfer()
{
	$files = ["app.sgp-gp-01.json", "app.sgp-gp-02.json", "app.sgp-gp-03.json", "app.sgp-gp-04.json", "app.sgp-gp-05.json"];
	foreach ($files as $file) {
		filter_file($dir . $file, $dir . "out." . $file);
	}	
}

function tags_sumup()
{

}

//升级为申请3072M内存
ini_set('memory_limit', '3072M');
$dir = "/tmp/ik/";