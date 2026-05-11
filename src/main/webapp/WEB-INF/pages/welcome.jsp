<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>CocktailHub</title>
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

<div class="container welcome-page">
    <div class="card search-panel mt16">
        <form class="search-form" action="${pageContext.request.contextPath}/welcome" method="get">
            <div class="search-controls">
                <input class="input search-input" type="text" name="q" value="${searchQuery}" placeholder="Search cocktails by name">
                <button class="btn" type="submit">Search</button>
                <c:if test="${not empty searchQuery}">
                    <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">Reset</a>
                </c:if>
            </div>
        </form>
    </div>

    <div class="cards">
        <c:if test="${empty cocktailList}">
            <div class="card p20" style="grid-column: span 12;">
                <div class="muted">
                    <c:choose>
                        <c:when test="${searchPerformed}">No cocktails found for your search.</c:when>
                        <c:otherwise>No cocktails yet.</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>

        <c:forEach var="cocktail" items="${cocktailList}">
            <div class="card cocktail">
                <a href="${pageContext.request.contextPath}/view?id=${cocktail.id}">
                    <div class="media">
                        <c:choose>
                            <c:when test="${not empty cocktail.imagePath}">
                                <img src="${pageContext.request.contextPath}${cocktail.imagePath}" alt="Cocktail photo">
                            </c:when>
                            <c:otherwise>
                                <img alt="Default cocktail" src="data:image/svg+xml;utf8,
                                     <svg xmlns='http://www.w3.org/2000/svg' width='1200' height='700'>
                                       <defs>
                                         <linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>
                                           <stop offset='0' stop-color='%23d97706' stop-opacity='.24'/>
                                           <stop offset='1' stop-color='%230f766e' stop-opacity='.20'/>
                                         </linearGradient>
                                       </defs>
                                       <rect width='1200' height='700' fill='%23f9f3e7'/>
                                       <rect width='1200' height='700' fill='url(%23g)'/>
                                       <circle cx='920' cy='220' r='190' fill='%23ffffff' opacity='.55'/>
                                       <circle cx='960' cy='260' r='190' fill='%23ffffff' opacity='.35'/>
                                       <text x='70' y='610' font-family='Verdana' font-size='64' fill='%231f1d1a' opacity='.68'>CocktailHub</text>
                                       <text x='70' y='670' font-family='Verdana' font-size='34' fill='%231f1d1a' opacity='.48'>No photo uploaded</text>
                                     </svg>">
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="content">
                        <div class="title"><c:out value="${cocktail.name}"/></div>
                        <div class="desc"><c:out value="${cocktail.description}"/></div>

                        <div class="meta">
                            <span class="badge rating-badge">Rating: ${ratings[cocktail.id]}</span>
                            <span class="open-link">View</span>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>
</div>

</body>
</html>
