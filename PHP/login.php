<?php
header('Content-Type: application/json');


$servername = "localhost";
$username = "root";
$password = "";
$dbname = "smartphone_shop";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["status" => "failure", "message" => "Connection failed: " . $conn->connect_error]));
}

// Получение данных из POST-запроса
$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username'], $data['password'])) {
    echo json_encode(["status" => "failure", "message" => "Некорректные данные"]);
    exit();
}

$username = $data['username'];
$password = $data['password'];

// Проверка данных
$sql = "SELECT id FROM users WHERE username = ? AND password = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $username, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    echo json_encode(["status" => "success", "user_id" => $user['id']]);
} else {
    echo json_encode(["status" => "failure", "message" => "Неверный логин или пароль"]);
}

$stmt->close();
$conn->close();
?>