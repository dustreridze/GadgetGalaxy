<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");


$servername = "localhost";
$username = "root";
$password = "";
$dbname = "smartphone_shop";

$conn = new mysqli($servername, $username, $password, $dbname);


if ($conn->connect_error) {
    echo json_encode(["status" => "failure", "message" => "Ошибка подключения: " . $conn->connect_error]);
    exit();
}


if (!isset($_POST['user_id'], $_POST['product_id'])) {
    echo json_encode(["status" => "failure", "message" => "Некорректные данные"]);
    exit();
}

$user_id = intval($_POST['user_id']);
$product_id = intval($_POST['product_id']);


$sql = "INSERT INTO cart (user_id, product_id) VALUES (?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ii", $user_id, $product_id);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Товар добавлен в корзину"]);
} else {
    echo json_encode(["status" => "failure", "message" => "Ошибка добавления: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>