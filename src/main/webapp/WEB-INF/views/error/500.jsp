<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Erreur interne"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="auth-page d-flex align-items-center">
<div class="container text-center py-5">
    <div class="error-code">500</div>
    <h1 class="h3">Erreur interne</h1>
    <p class="lead text-muted">Une erreur inattendue s'est produite. Réessayez dans un instant.</p>
    <a class="btn btn-mc mt-2" href="${ctx}/">Retour à l'accueil</a>
</div>
</body>
</html>
