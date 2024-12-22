<?php
header('Content-Type: application/json');


$servername = "localhost";
$username = "root";
$password = "";
$dbname = "smartphone_shop";

$conn = new mysqli($servername, $username, $password, $dbname);

// Проверка подключения
if ($conn->connect_error) {
    die(json_encode(["status" => "failure", "message" => "Connection failed: " . $conn->connect_error]));
}


$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['email'], $data['username'], $data['password'])) {
    echo json_encode(["status" => "failure", "message" => "Некорректные данные"]);
    exit();
}

$email = $data['email'];
$username = $data['username'];
$password = $data['password'];

// Проверка на существование пользователя
$checkUser = "SELECT * FROM users WHERE username = ? OR email = ?";
$stmt = $conn->prepare($checkUser);
$stmt->bind_param("ss", $username, $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["status" => "failure", "message" => "Пользователь с таким логином или email уже существует"]);
    $stmt->close();
    $conn->close();
    exit();
}


$insertUser = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
$stmt = $conn->prepare($insertUser);
$stmt->bind_param("sss", $email, $username, $password);

if ($stmt->execute()) {
    
    $user_id = $conn->insert_id;
    echo json_encode(["status" => "success", "message" => "Пользователь успешно зарегистрирован", "user_id" => $user_id]);
} else {
    echo json_encode(["status" => "failure", "message" => "Ошибка регистрации: " . $conn->error]);
}

$stmt->close();
$conn->close();
?>