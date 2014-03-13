DEFINE XPathService edu.tuberlin.dima.taverna_to_pig.udf.XPathFunction();
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

--fits_validation = STREAM image_pathes THROUGH fits_stream AS (image_path:chararray, xml_text:chararray);
fits_validation = STREAM image_pathes THROUGH jhove_stream AS (image_path:chararray, xml_text:chararray);

/* STEP 3 in Workflow */

jhove2 = FOREACH fits_validation GENERATE image_path, XPathService('$xpath_exp3', xml_text) AS node_list;

flatten_jhove2_list = FOREACH jhove2 GENERATE image_path, FLATTEN(node_list) as node;

/* STEP 4 in Workflow */

flatten_jhove_list_filtered = FILTER flatten_jhove2_list BY node == 'Well-Formed and valid';
flatten_jhove_list_filtered = FOREACH flatten_jhove_list_filtered GENERATE image_path as image_path, '$tmp_path' as tmp_path;

opj_compress = STREAM flatten_jhove_list_filtered THROUGH opj_stream AS (image_path:chararray, image_jp2_path:chararray);

/* STEP 5 in Workflow */

jpylyzer_validation = STREAM opj_compress THROUGH jpylyzer_stream AS (image_path:chararray, image_jp2_path:chararray, xml_text:chararray);

/* STEP 6 in Workflow */

jpylyzer = FOREACH jpylyzer_validation GENERATE image_path, image_jp2_path, XPathService('$xpath_exp2', xml_text) AS node_list;

flatten_jpylyzer_list = FOREACH jpylyzer GENERATE image_path, image_jp2_path, FLATTEN(node_list) AS is_valid_jp2;

/* STEP 7 in Workflow */

flatten_jpylyzer_list_filtered = FILTER flatten_jpylyzer_list BY is_valid_jp2 == 'True';
flatten_jpylyzer_list_filtered = FOREACH flatten_jpylyzer_list_filtered GENERATE image_path, image_jp2_path, '$tmp_path' as tmp_path;

opj_decompress = STREAM flatten_jpylyzer_list_filtered THROUGH opj_decompress_stream AS (image_path:chararray, image_jp2_path:chararray, image_tif_path:chararray);

/* Step 8 in Workflow */

opj_decompress_projected = FOREACH opj_decompress GENERATE image_path, image_tif_path, '$tmp_path' as tmp_path;

compared = STREAM opj_decompress_projected THROUGH compare_stream AS (result:chararray, a:chararray);
