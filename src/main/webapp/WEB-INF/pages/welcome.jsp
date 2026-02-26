<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cocktails</title>
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

            <!-- CLIENT -->
            <c:if test="${not empty currentUser and currentUser.role == 'CLIENT'}">
                <a href="${pageContext.request.contextPath}/add">Добавить</a>
            </c:if>

            <!-- BARTENDER -->
            <c:if test="${not empty currentUser and currentUser.role == 'BARTENDER'}">
                <a href="${pageContext.request.contextPath}/add">Добавить</a>
                <a href="${pageContext.request.contextPath}/approve">Модерация</a>
            </c:if>

            <!-- ADMIN -->
            <c:if test="${not empty currentUser and currentUser.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/add">Добавить</a>
                <a href="${pageContext.request.contextPath}/approve">Модерация</a>
                <a href="${pageContext.request.contextPath}/admin/users">Пользователи</a>
            </c:if>

            <!-- Если гость — кнопка вход -->
            <c:if test="${empty currentUser}">
                <a class="btn small" href="${pageContext.request.contextPath}/login">Вход</a>
            </c:if>

            <!-- Если вошел — кнопка выхода -->
            <c:if test="${not empty currentUser}">
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin:0;">
                    <button class="btn secondary small" type="submit">Выход</button>
                </form>
            </c:if>
        </div>
    </div>
</div>

<div class="container">

    <div class="row space mt12">
        <h2>Все коктейли</h2>
        <div class="small">Открывай карточку, чтобы посмотреть рецепт</div>
    </div>

    <div class="cards">
        <c:if test="${empty cocktailList}">
            <div class="card p20 span12">
                <div class="muted">Пока нет коктейлей.</div>
            </div>
        </c:if>

        <c:forEach var="cocktail" items="${cocktailList}">
            <div class="card cocktail">
                <a href="${pageContext.request.contextPath}/view?id=${cocktail.id}">
                    <div class="cover"></div>
                    <div class="content">
                        <p class="title">${cocktail.name}</p>
                        <div class="small mt8">${cocktail.description}</div>
                        <div class="meta">
                            <span class="badge">Открыть →</span>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>

</div>

</body>
</html>