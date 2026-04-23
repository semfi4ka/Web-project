<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Welcome Back</h2>
    <div class="muted mt8">Sign in to add, rate and discuss cocktails.</div>

    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null) { %>
    <div class="message error"><%= error %></div>
    <% } %>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input class="input" type="email" name="email" placeholder="Email" required>
        <input class="input" type="password" name="password" placeholder="Password" required>
        <button class="btn" type="submit">Login</button>
    </form>

    <div class="link">
        <a href="${pageContext.request.contextPath}/register">Create account</a>
    </div>
</div>

</body>
</html>
