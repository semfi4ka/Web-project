<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Registration</h2>
    <div class="muted mt8">Создай аккаунт и добавляй свои коктейли</div>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <input class="input" type="text" name="username" placeholder="Username" required>
        <input class="input" type="email" name="email" placeholder="Email" required>
        <input class="input" type="password" name="password" placeholder="Password" required>
        <button class="btn" type="submit">Register</button>
    </form>

    <div class="link">
        <a href="login">Already have an account? Log in</a>
    </div>
</div>

</body>
</html>