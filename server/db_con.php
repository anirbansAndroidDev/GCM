<?php

$con = mysql_connect("localhost","root","ani325_");
if (!$con)
{
	echo 'Could not connect: ' . mysql_error();
}
mysql_select_db("gcm") or die(mysql_error());

?>