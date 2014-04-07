<?php
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: PUT, GET, POST, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');
		
include_once 'db_con.php';


$nameUser  = $_POST["name"];
$nameEmail = $_POST["email"];
$gcmRegID  = $_POST["regId"]; // GCM Registration ID got from device

/**
 * Registering a user device in database
 * Store reg id in users table
 */
if (isset($nameUser) && isset($nameEmail) && isset($gcmRegID)) {
    
	$result    = mysql_query("SELECT gcm_regid from gcm_users WHERE gcm_regid = '$gcmRegID'");
	$NumOfRows = mysql_num_rows($result);
	if ($NumOfRows > 0) 
	{
		// user existed
		echo 'exist';
	} 
	else 
	{
		// user not existed
		$result = mysql_query("INSERT INTO gcm_users(name, email, gcm_regid, created_at) VALUES('$nameUser', '$nameEmail', '$gcmRegID', NOW())");
		
		// check for successful store
		if ($result) 
		{
			echo 'Y';
		} 
		else 
		{
			echo 'N';
		}
	}
} 
else 
{
    echo 'Request without parameter';
}
?>