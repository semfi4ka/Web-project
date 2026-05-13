<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Blog</title>
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
                <h1>Blog</h1>
                <div class="muted mt8">Updates, notes and cocktail stories from the CocktailHub team.</div>
            </div>
            <span class="badge">${empty posts ? 0 : posts.size()} posts</span>
        </div>
    </div>

    <c:if test="${not empty currentUser and currentUser.role == 'ADMIN'}">
        <div class="card p20 mt18">
            <h2>New Publication</h2>
            <form class="blog-form mt16" action="${pageContext.request.contextPath}/blog" method="post">
                <input type="hidden" name="action" value="create">
                <input class="input" type="text" name="title" placeholder="Title" required>
                <textarea name="content" placeholder="Publication text" required></textarea>
                <button class="btn" type="submit">Publish</button>
            </form>
        </div>
    </c:if>

    <c:if test="${empty posts}">
        <div class="card p20 mt18">
            <div class="muted">No blog posts yet.</div>
        </div>
    </c:if>

    <c:forEach var="post" items="${posts}">
        <article class="card p20 mt18 blog-post">
            <div class="row space">
                <div>
                    <h2><c:out value="${post.title}"/></h2>
                    <div class="muted mt8">
                        By
                        <a class="profile-link" href="${pageContext.request.contextPath}/profile?userId=${post.author.id}">
                            <c:out value="${post.author.username}"/>
                        </a>
                        - ${postDates[post.id]}
                    </div>
                </div>
                <span class="badge">${commentsByPost[post.id].size()} comments</span>
            </div>

            <div class="blog-content mt16"><c:out value="${post.content}"/></div>

            <div class="divider"></div>

            <h3>Comments</h3>
            <c:if test="${empty commentsByPost[post.id]}">
                <div class="muted mt12">No comments yet.</div>
            </c:if>

            <c:forEach var="comment" items="${commentsByPost[post.id]}">
                <div class="comment mt12">
                    <div class="head">
                        <div class="meta-left">
                            <a class="who profile-link" href="${pageContext.request.contextPath}/profile?userId=${comment.author.id}">
                                <c:out value="${comment.author.username}"/>
                            </a>
                            <span class="badge"><c:out value="${comment.author.role}"/></span>
                        </div>
                        <span class="small">${commentDates[comment.id]}</span>
                    </div>
                    <div class="text"><c:out value="${comment.text}"/></div>
                </div>
            </c:forEach>

            <c:choose>
                <c:when test="${not empty currentUser}">
                    <form class="blog-comment-form mt16" action="${pageContext.request.contextPath}/blog" method="post">
                        <input type="hidden" name="action" value="comment">
                        <input type="hidden" name="postId" value="${post.id}">
                        <textarea name="text" placeholder="Write a comment..." required></textarea>
                        <button class="btn secondary small" type="submit">Comment</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="muted mt12">Log in to comment.</div>
                </c:otherwise>
            </c:choose>
        </article>
    </c:forEach>
</div>

</body>
</html>
