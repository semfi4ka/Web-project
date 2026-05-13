<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add / Offer Cocktail</title>
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
    <div class="card panel mt22">
        <div class="row space">
            <div>
                <h1>
                    <c:choose>
                        <c:when test="${currentUser.role == 'CLIENT'}">Offer Cocktail</c:when>
                        <c:otherwise>Add Cocktail</c:otherwise>
                    </c:choose>
                </h1>
                <div class="muted mt8">Fill in the details, attach a photo and list ingredients.</div>
            </div>
            <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">Back</a>
        </div>

        <div class="divider"></div>

        <form action="${pageContext.request.contextPath}/add" method="post" enctype="multipart/form-data" style="display: grid; gap: 12px;">
            <label class="small">Name</label>
            <input class="input" type="text" name="name" placeholder="For example, Mojito" required>

            <label class="small">Description</label>
            <textarea name="description" placeholder="Short description..."></textarea>

            <div class="uploader mt12">
                <label class="small">Photo (optional)</label>

                <input id="photoInput" class="file-hidden" type="file" name="photo" accept="image/*">

                <div class="drop">
                    <div>
                        <strong>Upload a photo</strong>
                        <div class="hint">JPG / PNG / WEBP up to 5 MB</div>
                        <div class="mt12">
                            <label for="photoInput" class="btn small">Choose file</label>
                        </div>
                    </div>

                    <div class="preview">
                        <img id="photoPreview" alt="Preview" style="display: none;">
                    </div>
                </div>
            </div>

            <h2 class="mt16">Ingredients</h2>
            <div id="ingredients-container">
                <div class="ingredient-row">
                    <input class="input" type="text" name="ingredientName" placeholder="Ingredient" required>
                    <input class="input" type="text" name="ingredientAmount" placeholder="Amount">
                    <input class="input" type="text" name="ingredientUnit" placeholder="Unit">
                </div>
            </div>

            <button class="btn mt12" type="submit">
                <c:choose>
                    <c:when test="${currentUser.role == 'CLIENT'}">Offer Cocktail</c:when>
                    <c:otherwise>Add Cocktail</c:otherwise>
                </c:choose>
            </button>
        </form>
    </div>
</div>

<script>
    const container = document.getElementById('ingredients-container');
    container.addEventListener('input', () => {
        const lastRow = container.lastElementChild;
        const inputs = lastRow.querySelectorAll('input');

        let anyFilled = false;
        inputs.forEach((input) => {
            if (input.value.trim() !== '') anyFilled = true;
        });

        if (anyFilled) {
            const newRow = lastRow.cloneNode(true);
            newRow.querySelectorAll('input').forEach((input) => {
                input.value = '';
            });
            const nameInput = newRow.querySelector('input[name="ingredientName"]');
            if (nameInput) nameInput.required = false;
            container.appendChild(newRow);
        }
    });

    const photoInput = document.getElementById('photoInput');
    const photoPreview = document.getElementById('photoPreview');

    photoInput.addEventListener('change', () => {
        const file = photoInput.files && photoInput.files[0];
        if (!file) {
            photoPreview.style.display = 'none';
            photoPreview.src = '';
            return;
        }
        const url = URL.createObjectURL(file);
        photoPreview.src = url;
        photoPreview.style.display = 'block';
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
