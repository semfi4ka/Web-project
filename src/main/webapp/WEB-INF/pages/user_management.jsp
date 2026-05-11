<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
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
        </div>
    </div>
</div>

<div class="page">
    <div class="card panel mt22">
        <div class="row space">
            <div>
                <h1>User Management</h1>
                <div class="muted mt8">Promote and demote user roles.</div>
            </div>
            <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">Back</a>
        </div>

        <table class="table">
            <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Cocktails</th>
                <th style="width: 220px;">Actions</th>
            </tr>

            <c:forEach var="user" items="${userList}">
                <tr>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td><span class="badge">${user.role}</span></td>
                    <td>${user.cocktailCount}</td>
                    <td>
                        <div class="row" style="gap: 8px;">
                            <c:if test="${user.role == 'CLIENT'}">
                                <form style="margin: 0;" method="post" action="${pageContext.request.contextPath}/admin/users">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <input type="hidden" name="action" value="promote">
                                    <button class="btn ok small" type="submit">Promote</button>
                                </form>
                            </c:if>

                            <c:if test="${user.role == 'BARTENDER'}">
                                <form style="margin: 0;" method="post" action="${pageContext.request.contextPath}/admin/users">
                                    <input type="hidden" name="userId" value="${user.id}">
                                    <input type="hidden" name="action" value="demote">
                                    <button class="btn danger small" type="submit">Demote</button>
                                </form>
                            </c:if>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

</body>
</html>
