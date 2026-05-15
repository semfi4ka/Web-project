<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cocktail</title>
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
                <a href="${pageContext.request.contextPath}/login" class="btn ghost small">Log in</a>
            </c:if>
        </div>
    </div>
</div>

<div class="container">
    <div class="row space mt22">
        <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">Back</a>

        <c:if test="${not empty currentUser and currentUser.role == 'ADMIN'}">
            <form action="${pageContext.request.contextPath}/delete" method="post" style="margin: 0;">
                <input type="hidden" name="id" value="${cocktail.id}">
                <button type="submit" class="btn danger" onclick="return confirm('Delete cocktail?');">Delete</button>
            </form>
        </c:if>
    </div>

    <div class="card p20 mt18">
        <div class="row detail-layout">
            <c:if test="${not empty cocktail.imagePath}">
                <div class="detail-media-column">
                    <div class="media detail-media">
                        <img class="detail-image" src="${pageContext.request.contextPath}${cocktail.imagePath}" alt="Cocktail photo">
                    </div>
                </div>
            </c:if>

            <div class="detail-content">
                <h1><c:out value="${cocktail.name}"/></h1>
                <div class="muted mt8"><c:out value="${cocktail.description}"/></div>

                <div class="row mt12">
                    <a class="badge profile-link-badge" href="${pageContext.request.contextPath}/profile?userId=${cocktail.author.id}">
                        Author: <c:out value="${authorName}"/>
                    </a>
                    <span class="badge">Created: ${cocktailCreatedAt}</span>
                    <span class="badge">Average: ${avgRating}</span>
                </div>

                <div class="mt18">
                    <h2>Ingredients</h2>
                    <ul class="ingredients">
                        <c:forEach var="ingredient" items="${ingredients}">
                            <li><c:out value="${ingredient}"/></li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${not empty currentUser}">
        <div class="card p20 mt18">
            <div class="row space">
                <div>
                    <h2>Rate and Comment</h2>
                    <div class="muted mt8">Select stars and leave optional feedback.</div>
                </div>

                <c:if test="${not empty myRating}">
                    <span class="badge">Your rating: ${myRating}</span>
                </c:if>
            </div>

            <form id="feedbackForm" method="post" action="${pageContext.request.contextPath}/feedback" class="mt16">
                <input type="hidden" name="cocktailId" value="${cocktail.id}">
                <input type="hidden" id="ratingInput" name="rating" value="${myRating}">

                <div class="rating-line">
                    <div class="stars" id="stars" data-initial="${myRating}">
                        <button type="button" class="star-btn" data-value="1" aria-label="1 star">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27l-5.18 3.04 1.4-5.93-4.62-4 6.08-.52L12 4l2.32 5.86 6.08.52-4.62 4 1.4 5.93z"/></svg>
                        </button>
                        <button type="button" class="star-btn" data-value="2" aria-label="2 stars">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27l-5.18 3.04 1.4-5.93-4.62-4 6.08-.52L12 4l2.32 5.86 6.08.52-4.62 4 1.4 5.93z"/></svg>
                        </button>
                        <button type="button" class="star-btn" data-value="3" aria-label="3 stars">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27l-5.18 3.04 1.4-5.93-4.62-4 6.08-.52L12 4l2.32 5.86 6.08.52-4.62 4 1.4 5.93z"/></svg>
                        </button>
                        <button type="button" class="star-btn" data-value="4" aria-label="4 stars">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27l-5.18 3.04 1.4-5.93-4.62-4 6.08-.52L12 4l2.32 5.86 6.08.52-4.62 4 1.4 5.93z"/></svg>
                        </button>
                        <button type="button" class="star-btn" data-value="5" aria-label="5 stars">
                            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 17.27l-5.18 3.04 1.4-5.93-4.62-4 6.08-.52L12 4l2.32 5.86 6.08.52-4.62 4 1.4 5.93z"/></svg>
                        </button>
                    </div>

                </div>

                <div class="mt16">
                    <label class="small">Comment (optional)</label>
                    <textarea name="text" placeholder="Share your thoughts..."></textarea>
                </div>

                <button class="btn mt12" type="submit">
                    <c:choose>
                        <c:when test="${not empty myRating}">Update rating</c:when>
                        <c:otherwise>Submit rating</c:otherwise>
                    </c:choose>
                </button>

                <div class="small mt12" id="ratingError" style="display: none; color: #8f2018; font-weight: 700;">
                    Please select a rating (1-5).
                </div>
            </form>
        </div>
    </c:if>

    <div class="card p20 mt18" id="cocktail-comments-section">
        <div class="row space">
            <h2>Comments</h2>
            <a class="toolbar-chip toolbar-chip-cycle async-sort-link"
               data-target="#cocktail-comments-section"
               href="${pageContext.request.contextPath}/view?id=${cocktail.id}&commentSort=${nextCommentSort}">
                ${commentSortLabel}
            </a>
        </div>

        <c:if test="${empty comments}">
            <div class="muted mt12">No comments yet.</div>
        </c:if>

        <c:forEach var="comment" items="${comments}">
            <div class="comment mt12">
                <div class="head">
                    <div class="meta-left">
                        <a class="who profile-link" href="${pageContext.request.contextPath}/profile?userId=${comment.author.id}">
                            <c:out value="${comment.author.username}"/>
                        </a>
                        <span class="badge"><c:out value="${comment.author.role}"/></span>

                        <c:if test="${not empty comment.rating}">
                            <span class="badge">Rating: ${comment.rating}</span>
                        </c:if>
                        <c:if test="${empty comment.rating}">
                            <span class="badge">No rating</span>
                        </c:if>
                    </div>

                    <span class="small">${commentDates[comment.id]}</span>
                </div>

                <div class="text"><c:out value="${comment.text}"/></div>
            </div>
        </c:forEach>
    </div>
</div>

<script>
    const stars = document.getElementById('stars');
    const buttons = stars ? stars.querySelectorAll('.star-btn') : [];
    const ratingInput = document.getElementById('ratingInput');
    const errorBox = document.getElementById('ratingError');
    const form = document.getElementById('feedbackForm');

    function normalizeRating(raw) {
        const normalized = Number(String(raw).replace(',', '.'));
        if (!Number.isFinite(normalized)) return 0;
        return Math.max(0, Math.min(5, Math.round(normalized)));
    }

    function paint(val) {
        const safeValue = normalizeRating(val);

        buttons.forEach((button) => {
            const value = parseInt(button.dataset.value, 10);
            button.classList.toggle('on', value <= safeValue);
        });

    }

    const initial = normalizeRating(stars?.dataset.initial || ratingInput?.value || '0');
    if (ratingInput && initial > 0) ratingInput.value = String(initial);
    paint(initial);

    buttons.forEach((button) => {
        button.addEventListener('click', () => {
            const value = normalizeRating(button.dataset.value);
            ratingInput.value = String(value);
            paint(value);
            if (errorBox) errorBox.style.display = 'none';
        });
    });

    if (form) {
        form.addEventListener('submit', (event) => {
            const value = normalizeRating(ratingInput.value || '0');
            if (!value || value < 1 || value > 5) {
                event.preventDefault();
                if (errorBox) errorBox.style.display = 'block';
            }
        });
    }

    async function replaceSection(link) {
        const targetSelector = link.dataset.target;
        if (!targetSelector) return;

        const currentSection = document.querySelector(targetSelector);
        if (!currentSection) return;

        link.classList.add('is-loading');
        link.setAttribute('aria-busy', 'true');

        try {
            const response = await fetch(link.href, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error('Request failed');
            }

            const html = await response.text();
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const replacement = doc.querySelector(targetSelector);

            if (!replacement) {
                throw new Error('Section not found');
            }

            currentSection.replaceWith(replacement);
            window.history.replaceState({}, '', link.href);
        } catch (error) {
            window.location.href = link.href;
        } finally {
            link.classList.remove('is-loading');
            link.removeAttribute('aria-busy');
        }
    }

    document.addEventListener('click', (event) => {
        const link = event.target.closest('.async-sort-link');
        if (!link) return;

        event.preventDefault();
        replaceSection(link);
    });
</script>

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
