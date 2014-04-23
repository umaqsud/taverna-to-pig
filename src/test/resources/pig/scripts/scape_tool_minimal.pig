DEFINE XPathService edu.tuberlin.dima.taverna_to_pig.udf.XPathFunction();
DEFINE ToMarService edu.tuberlin.dima.taverna_to_pig.udf.ToMarUDF();
DEFINE fits_stream `$fits_stream`;
DEFINE jhove_stream `$jhove_stream`;
DEFINE opj_stream `$opj_stream`;
DEFINE jpylyzer_stream `$jpylyzer_stream`;
DEFINE opj_decompress_stream `$opj_decompress_stream`;
DEFINE compare_stream `$compare_stream`;
DEFINE tmp_dir `$tmp_dir`;

%DECLARE xpath_exp1 '/fits/filestatus/valid';
%DECLARE xpath_exp2 '/jpylyzer/isValidJP2';
%DECLARE xpath_exp3 '/jhove/repInfo/status';
%DECLARE tmp_path '$tmp_dir';

/* STEP 1 in Workflow */

image_pathes = LOAD '$image_pathes' USING PigStorage() AS (image_path: chararray);

/* STEP 2 in Workflow */

fits_validation = STREAM image_pathes THROUGH fits_stream AS (image_path:chararray, xml_text:chararray);
--fits_validation = STREAM image_pathes THROUGH jhove_stream AS (image_path:chararray, xml_text:chararray);

/* STEP 2 with ToMaR */
fits_validation_tomar = FOREACH image_pathes GENERATE image_path as image_path, ToMarService('fits stdxml --input="hdfs:///user/rainer/bild.jpg"');
