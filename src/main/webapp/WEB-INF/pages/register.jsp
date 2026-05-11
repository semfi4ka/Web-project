<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Create Account</h2>
    <div class="muted mt8">Join the community and share your recipes.</div>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <input class="input ${not empty usernameError ? 'input-error has-error' : ''}"
               type="text"
               name="username"
               value="${fn:escapeXml(usernameValue)}"
               placeholder="Username"
               style="${not empty usernameError ? 'border-color: #d34053; background: #fff7f8;' : ''}"
               required>
        <c:if test="${not empty usernameError}">
            <div class="field-error" style="color: #b4233c;">${usernameError}</div>
        </c:if>

        <input class="input ${not empty emailError ? 'input-error has-error' : ''}"
               type="email"
               name="email"
               value="${fn:escapeXml(emailValue)}"
               placeholder="Email"
               style="${not empty emailError ? 'border-color: #d34053; background: #fff7f8;' : ''}"
               required>
        <c:if test="${not empty emailError}">
            <div class="field-error" style="color: #b4233c;">${emailError}</div>
        </c:if>

        <input class="input" type="password" name="password" placeholder="Password" required>
        <button class="btn" type="submit">Register</button>

        <c:if test="${not empty registrationError}">
            <div class="message error" style="color: #b4233c; border-color: #f4c7cf; background: #fff4f6;">${registrationError}</div>
        </c:if>

        <c:if test="${not empty successMessage}">
            <div class="message success" style="color: #146a48; border-color: #bfe3d0; background: #f2fbf6;">${successMessage}</div>
        </c:if>
    </form>

    <div class="link">
        <a href="${pageContext.request.contextPath}/login">Already have an account? Log in</a>
    </div>
</div>

</body>
</html>
