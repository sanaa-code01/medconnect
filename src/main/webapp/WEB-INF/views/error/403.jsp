<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Accès refusé"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="auth-page d-flex align-items-center">
<div class="container text-center py-5">
    <div class="error-code">403</div>
    <h1 class="h3">Accès refusé</h1>
    <p class="lead text-muted">Vous n'avez pas accès à cette page.</p>
    <c:if test="${not empty requestScope['jakarta.servlet.error.message']}">
        <p class="text-muted"><c:out value="${requestScope['jakarta.servlet.error.message']}"/></p>
    </c:if>
    <a class="btn btn-mc mt-2" href="${ctx}/">Retour à l'accueil</a>
</div>
</body>
</html>
