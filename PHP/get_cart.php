<?php
header('Content-Type: application/json');

$host = 'localhost';
$dbname = 'smartphone_shop'; 
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

    $stmt = $pdo->prepare("
        SELECT c.id AS cart_id, p.id AS product_id, p.name, p.price, p.image_url 
        FROM cart c 
        INNER JOIN products p ON c.product_id = p.id 
        WHERE c.user_id = ?");
    $stmt->execute([$user_id]);

    $cartItems = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $base_url = "http://10.0.2.2/smartphone_shop/images/";
    foreach ($cartItems as &$item) {
        $item['image_url'] = $base_url . $item['image_url'];
    }

    echo json_encode($cartItems);
} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>
