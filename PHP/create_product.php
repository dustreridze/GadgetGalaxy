<?php
header("Content-Type: application/json");

$host = "localhost"; 
$username = "root";  
$password = "";      
$database = "smartphone_shop"; 

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Ошибка подключения к базе данных: " . $conn->connect_error]));
}

$data = json_decode(file_get_contents("php://input"), true);


if (isset($data['name'], $data['price'], $data['image_url'])) {
    $name = $conn->real_escape_string($data['name']);
    $price = $conn->real_escape_string($data['price']);
    $image_url = $conn->real_escape_string($data['image_url']);

    
    $query = "INSERT INTO products (name, price, image_url) VALUES ('$name', '$price', '$image_url')";
    if ($conn->query($query) === TRUE) {
        echo json_encode([
            "status" => "success",
            "message" => "Продукт успешно создан",
            "product_id" => $conn->insert_id 
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Ошибка при создании продукта: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "Не все данные были переданы"]);
}

$conn->close();
?>
