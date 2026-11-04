<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Page introuvable"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="auth-page d-flex align-items-center">
<div class="container text-center py-5">
    <div class="error-code">404</div>
    <h1 class="h3">Page introuvable</h1>
    <p class="lead text-muted">La page demandée n'existe pas ou a été déplacée.</p>
    <a class="btn btn-mc mt-2" href="${ctx}/">Retour à l'accueil</a>
</div>
</body>
</html>
