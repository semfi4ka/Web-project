<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Cocktail</title>
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
            <a href="${pageContext.request.contextPath}/profile">Личный кабинет</a>
        </div>
    </div>
</div>

<div class="page">
    <div class="card panel">
        <div class="row space">
            <div>
                <h1>${currentUser.role == 'CLIENT' ? 'Offer a cocktail' : 'Add a cocktail'}</h1>
                <div class="muted mt8">Заполни форму и добавь ингредиенты</div>
            </div>
            <a class="btn secondary" href="${pageContext.request.contextPath}/welcome">← Back</a>
        </div>

        <div class="divider"></div>

        <form action="${pageContext.request.contextPath}/add" method="post" style="display:grid; gap:10px;">
            <label class="small">Name</label>
            <input class="input" type="text" name="name" required>

            <label class="small">Description</label>
            <textarea name="description" placeholder="Short description..."></textarea>

            <h2 class="mt12">Ingredients</h2>
            <div id="ingredients-container">
                <div class="ingredient-row">
                    <input class="input" type="text" name="ingredientName" placeholder="Ingredient Name" required>
                    <input class="input" type="text" name="ingredientAmount" placeholder="Amount">
                    <input class="input" type="text" name="ingredientUnit" placeholder="Unit">
                </div>
            </div>

            <button class="btn mt12" type="submit">
                ${currentUser.role == 'CLIENT' ? 'Offer a cocktail' : 'Add Cocktail'}
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
        inputs.forEach(input => { if (input.value.trim() !== '') anyFilled = true; });

        if (anyFilled) {
            const newRow = lastRow.cloneNode(true);
            newRow.querySelectorAll('input').forEach(i => i.value = '');
            const nameInput = newRow.querySelector('input[name="ingredientName"]');
            if (nameInput) nameInput.required = false;
            container.appendChild(newRow);
        }
    });
</script>

</body>
</html>