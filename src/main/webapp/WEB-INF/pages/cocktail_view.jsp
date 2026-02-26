<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cocktail Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="topbar">
    <div class="container row space">
        <a class="brand" href="${pageContext.request.contextPath}/welcome">
            <span class="logo"></span>
            <span>CocktailHub</span>
        </a>

        <div class="nav row">
            <a href="${pageContext.request.contextPath}/welcome">Главная</a>

            <c:if test="${not empty currentUser}">
                <a href="${pageContext.request.contextPath}/add">Добавить</a>
            </c:if>

            <c:choose>
                <c:when test="${not empty currentUser}">
                    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
                        <button class="btn secondary small" type="submit">Logout</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn small">Login</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="container">

    <div class="row space">
        <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">← Back</a>

        <!-- Delete только ADMIN -->
        <c:if test="${not empty currentUser and currentUser.role == 'ADMIN'}">
            <form action="${pageContext.request.contextPath}/delete" method="post" style="margin:0;">
                <input type="hidden" name="id" value="${cocktail.id}">
                <button type="submit" class="btn danger"
                        onclick="return confirm('Delete cocktail?');">
                    Delete
                </button>
            </form>
        </c:if>
    </div>

    <div class="card p20 mt18">

        <h1>${cocktail.name}</h1>
        <div class="muted mt8">${cocktail.description}</div>

        <div class="mt12">
            <span class="badge">Author: ${authorName}</span>
            <span class="badge">Created: ${cocktail.createdAt}</span>
        </div>

        <div class="mt18">
            <h2>Ingredients</h2>
            <ul class="ingredients">
                <c:forEach var="ingredient" items="${ingredients}">
                    <li>${ingredient}</li>
                </c:forEach>
            </ul>
        </div>

    </div>

    <!-- ⭐ Rating + Comment — только авторизованные -->
    <c:if test="${not empty currentUser}">
        <div class="card p20 mt18">
            <h2>Rate & Comment</h2>

            <form method="post" action="${pageContext.request.contextPath}/rate" class="mt12">
                <input type="hidden" name="cocktailId" value="${cocktail.id}">
                <div class="row">
                    <select name="rating" class="select">
                        <option value="1">1 ★</option>
                        <option value="2">2 ★★</option>
                        <option value="3">3 ★★★</option>
                        <option value="4">4 ★★★★</option>
                        <option value="5">5 ★★★★★</option>
                    </select>
                    <button class="btn" type="submit">Submit</button>
                </div>
            </form>

            <form method="post" action="${pageContext.request.contextPath}/comment" class="mt12">
                <input type="hidden" name="cocktailId" value="${cocktail.id}">
                <div class="row">
                    <input class="input" name="text" placeholder="Write comment...">
                    <button class="btn" type="submit">Send</button>
                </div>
            </form>

        </div>
    </c:if>

</div>

</body>
</html>