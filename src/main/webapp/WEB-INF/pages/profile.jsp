<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account</title>
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

            <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
                <button class="btn danger small" type="submit">Log out</button>
            </form>
        </div>
    </div>
</div>

<div class="container">
    <div class="hero">
        <div class="row space">
            <div>
                <h1>${profileUser.username}</h1>
                <div class="muted mt8">Role: <span class="badge">${profileUser.role}</span></div>
            </div>
            <div class="row">
                <span class="badge">Cocktails: ${empty profileCocktails ? 0 : profileCocktails.size()}</span>
                <span class="badge">Average rating: ${profileAvgRating}</span>
                <span class="badge">
                    <c:choose>
                        <c:when test="${isOwnProfile}">Account</c:when>
                        <c:otherwise>Profile</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>
    </div>

    <div class="card p20 mt18">
        <div class="row space">
            <h2>
                <c:choose>
                    <c:when test="${isOwnProfile}">My Cocktails</c:when>
                    <c:otherwise>${profileUser.username}'s Cocktails</c:otherwise>
                </c:choose>
            </h2>
            <span class="badge">${empty profileCocktails ? 0 : profileCocktails.size()} total</span>
        </div>

        <c:if test="${empty profileCocktails}">
            <div class="muted mt12">
                <c:choose>
                    <c:when test="${isOwnProfile}">You have not added any cocktails yet.</c:when>
                    <c:otherwise>This user has not added any cocktails yet.</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <c:if test="${not empty profileCocktails}">
            <table class="table">
                <tr>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th></th>
                </tr>
                <c:forEach var="c" items="${profileCocktails}">
                    <tr>
                        <td><c:out value="${c.name}"/></td>
                        <td><span class="badge"><c:out value="${c.status}"/></span></td>
                        <td class="small">${cocktailDates[c.id]}</td>
                        <td><a class="btn secondary small" href="${pageContext.request.contextPath}/view?id=${c.id}">Open</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>

    <c:if test="${isOwnProfile}">
    <div class="card p20 mt18">
        <div class="row space">
            <h2>My Ratings</h2>
            <span class="badge">${empty myRatings ? 0 : myRatings.size()} total</span>
        </div>

        <c:if test="${empty myRatings}">
            <div class="muted mt12">No ratings yet.</div>
        </c:if>

        <c:if test="${not empty myRatings}">
            <table class="table">
                <tr>
                    <th>Cocktail</th>
                    <th>Rating</th>
                    <th>Updated</th>
                    <th></th>
                </tr>
                <c:forEach var="r" items="${myRatings}">
                    <tr>
                        <td><c:out value="${r.cocktailName}"/></td>
                        <td><span class="badge">Rating: ${r.rating}</span></td>
                        <td class="small">${ratingDates[r.cocktailId]}</td>
                        <td><a class="btn secondary small" href="${pageContext.request.contextPath}/view?id=${r.cocktailId}">Open</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>

    <div class="card p20 mt18">
        <div class="row space">
            <h2>My Comments</h2>
            <span class="badge">${empty myComments ? 0 : myComments.size()} total</span>
        </div>

        <c:if test="${empty myComments}">
            <div class="muted mt12">No comments yet.</div>
        </c:if>

        <c:if test="${not empty myComments}">
            <c:forEach var="cm" items="${myComments}" varStatus="st">
                <div class="comment mt12">
                    <div class="head">
                        <div class="meta-left">
                            <span class="who"><c:out value="${cm.cocktailName}"/></span>
                            <c:if test="${not empty cm.rating}">
                                <span class="badge">Rating: ${cm.rating}</span>
                            </c:if>
                        </div>
                        <span class="small">${profileCommentDates[st.index]}</span>
                    </div>
                    <div class="text"><c:out value="${cm.text}"/></div>
                    <div class="mt12">
                        <a class="btn secondary small" href="${pageContext.request.contextPath}/view?id=${cm.cocktailId}">Open cocktail</a>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
    </c:if>
</div>

</body>
</html>
