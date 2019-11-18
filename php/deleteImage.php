<?php 
	ini_set('display_errors', 'On');
	ini_set('display_errors', 1);
	error_reporting(E_ALL);
	mb_internal_encoding('UTF-8');
	mb_http_output('UTF-8');
	$server = "galan.ehu.eus";
	$userName = "Xavelez012";
	$pass = "mc3FY6xMZyEV96r2";
	$bd = "Xavelez012_TFG";
	$miArray = [];

	if(isset($_POST["usuario"]) && isset($_POST["contrasena"]) && isset($_POST["url"])){
		$usuario = $_POST["usuario"];
		$contrasena = $_POST["contrasena"];
		$url = 'Img/'.trim($_POST["url"]);

		//Creamos la conexión
		$conn = mysqli_connect($server, $userName, $pass,$bd);
		if (mysqli_connect_errno()) {
		    print_r(json_encode(array("error_code" => 404,
							  	  "error_message" => "Not found")));
		    exit();
		}
		$stmt = $conn->prepare("SELECT COUNT(1) FROM LOGOPEDA WHERE L_USUARIO = ? && L_CONTRASENA = ?");
		$stmt->bind_param("ss", $usuario, $contrasena);
		if ($stmt->execute()) {
			$stmt->bind_result($count);	
			if($stmt->fetch() && $count > 0){
				$stmt->close();
				try{				
					 if (file_exists($url)) {
					    unlink($url);
					    echo 'File '.$url.' has been deleted';
					 } else {
					    print_r(json_encode(array('error_code' => 404,
										'error_message' => 'No existe la imagen')));
					 }
				}catch(Exception $e){
					print_r(json_encode(array('error_code' => 500,
											'error_message' => $e)));
				}
			}else{
				$stmt->close();
		  		print_r(json_encode(array("error_code" => 401,
							  "error_message" => "El usuario debe estar logueado")));
			}			
		}
	}else{
		print_r(json_encode(array("error_code" => 300,
					  "error_message" => "Parametros incorrectos")));
	}
?>