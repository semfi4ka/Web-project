<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bartenders</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="topbar">
    <div class="container row space">
        <a class="brand" href="${pageContext.request.contextPath}/welcome">
            <img src="${pageContext.request.contextPath}/images/logo.svg" alt="CocktailHub" class="logo">
            <span class="logotext">CocktailHub</span>
        </a>

        <div class="nav row">
            <a href="${pageContext.request.contextPath}/welcome">Home</a>
            <a href="${pageContext.request.contextPath}/bartenders">Bartenders</a>
            <a href="${pageContext.request.contextPath}/blog">Blog</a>

            <c:if test="${not empty currentUser}">
                <a href="${pageContext.request.contextPath}/add">
                    <c:choose>
                        <c:when test="${currentUser.role == 'CLIENT'}">Offer Cocktail</c:when>
                        <c:otherwise>Add Cocktail</c:otherwise>
                    </c:choose>
                </a>

                <c:if test="${currentUser.role == 'BARTENDER' or currentUser.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/approve">Moderation</a>
                </c:if>

                <c:if test="${currentUser.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/users">Users</a>
                </c:if>

                <a class="btn ghost small" href="${pageContext.request.contextPath}/profile">Account</a>
            </c:if>

            <c:if test="${empty currentUser}">
                <a class="btn ghost small" href="${pageContext.request.contextPath}/login">Log in</a>
            </c:if>
        </div>
    </div>
</div>

<div class="page">
    <div class="hero">
        <div class="row space">
            <div>
                <h1>Bartenders</h1>
                <div class="muted mt8">Explore recipe authors and their cocktail collections.</div>
            </div>
            <span class="badge">${empty bartenders ? 0 : bartenders.size()} total</span>
        </div>
    </div>

    <div class="card p20 mt18">
        <c:if test="${empty bartenders}">
            <div class="muted">No bartenders yet.</div>
        </c:if>

        <c:if test="${not empty bartenders}">
            <table class="table directory-table">
                <tr>
                    <th>Bartender</th>
                    <th>Cocktails</th>
                    <th>Average Rating</th>
                    <th>Joined</th>
                    <th></th>
                </tr>

                <c:forEach var="bartender" items="${bartenders}">
                    <tr>
                        <td>
                            <a class="profile-link directory-name"
                               href="${pageContext.request.contextPath}/profile?userId=${bartender.id}">
                                <c:out value="${bartender.username}"/>
                            </a>
                        </td>
                        <td><span class="badge">${bartender.cocktailCount}</span></td>
                        <td><span class="badge">Rating: ${averageRatings[bartender.id]}</span></td>
                        <td class="small">${createdDates[bartender.id]}</td>
                        <td>
                            <a class="btn secondary small"
                               href="${pageContext.request.contextPath}/profile?userId=${bartender.id}">
                                View profile
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
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
