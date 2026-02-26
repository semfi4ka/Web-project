<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profile</title>
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
            <a href="${pageContext.request.contextPath}/add">Добавить</a>
        </div>
    </div>
</div>

<div class="container">
    <div class="hero">
        <div class="row space">
            <div>
                <h1>Личный кабинет</h1>
                <div class="muted mt8">Здесь позже будут твои коктейли, оценки и комментарии</div>
            </div>
            <span class="badge">${currentUser.username} • ${currentUser.role}</span>
        </div>
    </div>

    <div class="grid mt18">
        <div class="card p20 span6">
            <h2>Мои коктейли</h2>
            <div class="muted mt12">Заглушка: позже выведем список коктейлей, которые ты добавил.</div>
        </div>

        <div class="card p20 span6">
            <h2>Мои оценки</h2>
            <div class="muted mt12">Заглушка: позже выведем историю оценок 1–5★ и комментариев.</div>
        </div>
    </div>
</div>

</body>
</html>