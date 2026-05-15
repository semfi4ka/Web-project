<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>CocktailHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

<div class="container welcome-page">
    <div class="ad-banner mt16">
        <img src="${pageContext.request.contextPath}/images/ad-banner.svg" alt="Advertisement">
    </div>

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

    <c:if test="${showFeaturedRows and not empty trendingCocktails}">
        <section class="cocktail-section">
            <div class="section-head">
                <h2>Trending</h2>
                <span class="small">Most 4-5 star ratings this month</span>
            </div>
            <div class="cards cards-row">
                <c:forEach var="cocktail" items="${trendingCocktails}">
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
                                               <rect width='1200' height='700' fill='%23f7f9fb'/>
                                               <rect x='1' y='1' width='1198' height='698' fill='none' stroke='%23d9dfe6' stroke-width='2'/>
                                               <text x='70' y='610' font-family='Verdana' font-size='64' fill='%23111111'>CocktailHub</text>
                                               <text x='70' y='670' font-family='Verdana' font-size='34' fill='%23626a73'>No photo uploaded</text>
                                             </svg>">
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="content">
                                <div class="title"><c:out value="${cocktail.name}"/></div>
                                <div class="desc"><c:out value="${cocktail.description}"/></div>
                                <div class="meta">
                                    <span class="card-rating">
                                        <span class="star-rating" aria-label="Rating ${ratings[cocktail.id]} out of 5">
                                            <c:forEach var="star" items="${ratingStars[cocktail.id]}">
                                                <span class="rating-star ${star}">&#9733;</span>
                                            </c:forEach>
                                        </span>
                                        <span class="rating-number">${ratings[cocktail.id]}</span>
                                    </span>
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <c:if test="${showFeaturedRows and not empty weeklyBestCocktails}">
        <section class="cocktail-section">
            <div class="section-head">
                <h2>Best This Week</h2>
                <span class="small">Highest rated cocktails created this week</span>
            </div>
            <div class="cards cards-row">
                <c:forEach var="cocktail" items="${weeklyBestCocktails}">
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
                                               <rect width='1200' height='700' fill='%23f7f9fb'/>
                                               <rect x='1' y='1' width='1198' height='698' fill='none' stroke='%23d9dfe6' stroke-width='2'/>
                                               <text x='70' y='610' font-family='Verdana' font-size='64' fill='%23111111'>CocktailHub</text>
                                               <text x='70' y='670' font-family='Verdana' font-size='34' fill='%23626a73'>No photo uploaded</text>
                                             </svg>">
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="content">
                                <div class="title"><c:out value="${cocktail.name}"/></div>
                                <div class="desc"><c:out value="${cocktail.description}"/></div>
                                <div class="meta">
                                    <span class="card-rating">
                                        <span class="star-rating" aria-label="Rating ${ratings[cocktail.id]} out of 5">
                                            <c:forEach var="star" items="${ratingStars[cocktail.id]}">
                                                <span class="rating-star ${star}">&#9733;</span>
                                            </c:forEach>
                                        </span>
                                        <span class="rating-number">${ratings[cocktail.id]}</span>
                                    </span>
                                </div>
                            </div>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <section class="cocktail-section">
        <div class="section-head">
            <h2>All Cocktails</h2>
            <span class="small">${empty cocktailList ? 0 : cocktailList.size()} recipes</span>
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
                                           <rect width='1200' height='700' fill='%23f7f9fb'/>
                                           <rect x='1' y='1' width='1198' height='698' fill='none' stroke='%23d9dfe6' stroke-width='2'/>
                                           <text x='70' y='610' font-family='Verdana' font-size='64' fill='%23111111'>CocktailHub</text>
                                           <text x='70' y='670' font-family='Verdana' font-size='34' fill='%23626a73'>No photo uploaded</text>
                                         </svg>">
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="content">
                            <div class="title"><c:out value="${cocktail.name}"/></div>
                            <div class="desc"><c:out value="${cocktail.description}"/></div>

                            <div class="meta">
                                <span class="card-rating">
                                    <span class="star-rating" aria-label="Rating ${ratings[cocktail.id]} out of 5">
                                        <c:forEach var="star" items="${ratingStars[cocktail.id]}">
                                            <span class="rating-star ${star}">&#9733;</span>
                                        </c:forEach>
                                    </span>
                                    <span class="rating-number">${ratings[cocktail.id]}</span>
                                </span>
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </section>
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
