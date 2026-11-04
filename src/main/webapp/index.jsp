<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Accueil"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body>
<nav class="navbar navbar-expand public-nav px-4 py-3">
    <a class="navbar-brand fw-bold text-dark" href="${ctx}/"><i class="bi bi-droplet-fill text-mc me-1"></i>MedConnect</a>
    <div class="ms-auto">
        <c:choose>
            <c:when test="${not empty sessionScope.authUser}">
                <a class="btn btn-mc" href="${ctx}${sessionScope.authUser.role.dashboardPath}">Mon espace</a>
            </c:when>
            <c:otherwise>
                <a class="btn btn-outline-secondary me-2" href="${ctx}/login">Se connecter</a>
                <a class="btn btn-mc" href="${ctx}/register">Créer un compte</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<section class="hero">
    <div class="container">
        <h1>Chaque goutte peut sauver une vie</h1>
        <p class="mt-3">MedConnect met en relation les hôpitaux qui ont besoin de sang et les donneurs compatibles
            près de chez eux. Les hôpitaux publient leurs besoins, les donneurs répondent en un clic.</p>
        <div class="mt-4">
            <a class="btn btn-mc btn-lg me-2" href="${ctx}/register">Devenir donneur</a>
            <a class="btn btn-light btn-lg" href="${ctx}/register">Inscrire mon hôpital</a>
        </div>
    </div>
</section>

<section class="container py-5">
    <h2 class="text-center mb-5">Comment ça marche ?</h2>
    <div class="row g-4 text-center">
        <div class="col-md-4">
            <span class="step-icon"><i class="bi bi-clipboard2-pulse"></i></span>
            <h4 class="mt-3">1. L'hôpital publie</h4>
            <p class="text-muted">Groupe sanguin, nombre de poches et niveau d'urgence : la demande est visible immédiatement.</p>
        </div>
        <div class="col-md-4">
            <span class="step-icon"><i class="bi bi-hand-thumbs-up"></i></span>
            <h4 class="mt-3">2. Le donneur répond</h4>
            <p class="text-muted">Il ne voit que les demandes compatibles avec son groupe et clique sur « Je veux donner ».</p>
        </div>
        <div class="col-md-4">
            <span class="step-icon"><i class="bi bi-check2-circle"></i></span>
            <h4 class="mt-3">3. Le don est enregistré</h4>
            <p class="text-muted">L'hôpital confirme le donneur, enregistre le don, et la demande se clôture quand elle est satisfaite.</p>
        </div>
    </div>
</section>

<footer class="text-center text-muted py-4 border-top">MedConnect · Projet JEE · ENSA Agadir</footer>
</body>
</html>
