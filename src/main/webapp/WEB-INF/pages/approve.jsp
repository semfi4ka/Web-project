<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Moderation</title>
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
            <a href="${pageContext.request.contextPath}/welcome">Home</a>
            <a href="${pageContext.request.contextPath}/bartenders">Bartenders</a>
            <a href="${pageContext.request.contextPath}/blog">Blog</a>

            <c:if test="${not empty currentUser}">
                <a href="${pageContext.request.contextPath}/add">Add Cocktail</a>

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
    <div class="card panel mt22">
        <div class="row space">
            <div>
                <h1>Cocktail Moderation</h1>
                <div class="muted mt8">Review user-submitted recipes with moderation status.</div>
            </div>
            <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">Back</a>
        </div>

        <div class="divider"></div>

        <c:if test="${empty pendingCocktails}">
            <div class="card p20">
                <div class="muted">No cocktails are waiting for moderation.</div>
            </div>
        </c:if>

        <c:if test="${not empty pendingCocktails}">
            <table class="table">
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Author</th>
                    <th>Created At</th>
                    <th style="width: 220px;">Actions</th>
                </tr>

                <c:forEach var="cocktail" items="${pendingCocktails}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/view?id=${cocktail.id}" style="font-weight: 700; text-decoration: none;">
                                ${cocktail.name}
                            </a>
                        </td>
                        <td>${cocktail.description}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/profile?userId=${cocktail.author.id}" class="profile-link">
                                ${cocktail.author.username}
                            </a>
                        </td>
                        <td>${cocktail.createdAt}</td>
                        <td>
                            <div class="row" style="gap: 8px;">
                                <form action="${pageContext.request.contextPath}/approve" method="post" style="margin: 0;">
                                    <input type="hidden" name="cocktailId" value="${cocktail.id}">
                                    <input type="hidden" name="action" value="approve">
                                    <button class="btn ok small" type="submit">Approve</button>
                                </form>

                                <form action="${pageContext.request.contextPath}/approve" method="post" style="margin: 0;">
                                    <input type="hidden" name="cocktailId" value="${cocktail.id}">
                                    <input type="hidden" name="action" value="reject">
                                    <button class="btn danger small" type="submit">Reject</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>
</div>

</body>
</html>
