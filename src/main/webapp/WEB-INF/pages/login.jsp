<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Welcome Back</h2>
    <div class="muted mt8">Sign in to add, rate and discuss cocktails.</div>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <input class="input" type="email" name="email" placeholder="Email" required>
        <input class="input" type="password" name="password" placeholder="Password" required>
        <button class="btn" type="submit">Login</button>

        <c:if test="${not empty message}">
            <div class="message error">Email or password is incorrect.</div>
        </c:if>
    </form>

    <div class="link">
        <a href="${pageContext.request.contextPath}/register">Create account</a>
    </div>
</div>

<footer class="site-footer">
    <div class="footer-inner">
        <div class="footer-about">
            <a class="footer-brand" href="${pageContext.request.contextPath}/welcome">CocktailHub</a>
            <p>A clean catalog for discovering, publishing and discussing cocktail recipes.</p>
            <span>Simon, 2026</span>
        </div>

        <nav class="footer-links" aria-label="Footer navigation">
            <a href="${pageContext.request.contextPath}/welcome">Home</a>
            <a href="${pageContext.request.contextPath}/bartenders">Bartenders</a>
            <a href="${pageContext.request.contextPath}/blog">Blog</a>
            <c:if test="${not empty currentUser}">
                <a href="${pageContext.request.contextPath}/add">Add Cocktail</a>
                <a href="${pageContext.request.contextPath}/profile">Account</a>
            </c:if>
            <c:if test="${currentUser.role == 'BARTENDER' or currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/approve">Moderation</a>
            </c:if>
            <c:if test="${currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/users">Users</a>
            </c:if>
            <c:if test="${empty currentUser}">
                <a href="${pageContext.request.contextPath}/login">Log in</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </c:if>
        </nav>
    </div>
</footer>

</body>
</html>
