<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Connexion"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="auth-page d-flex align-items-center">
<div class="container py-5">
    <div class="auth-card mx-auto">
        <div class="text-center mb-4">
            <a href="${ctx}/" class="h2 text-dark text-decoration-none"><i class="bi bi-droplet-fill text-mc"></i> MedConnect</a>
            <p class="lead mt-2">Connectez-vous à votre espace donneur ou hôpital</p>
        </div>
        <div class="card">
            <div class="card-body p-4">
                <%@ include file="/WEB-INF/views/layout/flash.jspf" %>
                <form method="post" action="${ctx}/login">
                    <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                    <div class="mb-3">
                        <label class="form-label" for="email">E-mail</label>
                        <input class="form-control form-control-lg" type="email" id="email" name="email"
                               value="${fn:escapeXml(param.email)}" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Mot de passe</label>
                        <input class="form-control form-control-lg" type="password" id="password" name="password" required>
                    </div>
                    <button type="submit" class="btn btn-lg btn-mc w-100">Se connecter</button>
                </form>
            </div>
        </div>
        <p class="text-center mt-3">Pas encore de compte ? <a href="${ctx}/register">Créer un compte</a></p>
    </div>
</div>
</body>
</html>
