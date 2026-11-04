<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Inscription"/>
<c:set var="roleChoisi" value="${empty param.role ? 'DONNEUR' : param.role}"/>
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="auth-page">
<div class="container py-5">
    <div class="auth-card mx-auto">
        <div class="text-center mb-4">
            <a href="${ctx}/" class="h2 text-dark text-decoration-none"><i class="bi bi-droplet-fill text-mc"></i> MedConnect</a>
            <p class="lead mt-2">Créez votre compte</p>
        </div>
        <div class="card">
            <div class="card-body p-4">
                <%@ include file="/WEB-INF/views/layout/flash.jspf" %>
                <form method="post" action="${ctx}/register">
                    <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>

                    <div class="mb-3">
                        <label class="form-label d-block">Je suis</label>
                        <div class="btn-group w-100" role="group">
                            <input type="radio" class="btn-check" name="role" id="roleDonneur" value="DONNEUR"
                                   ${roleChoisi == 'DONNEUR' ? 'checked' : ''}>
                            <label class="btn btn-outline-danger" for="roleDonneur"><i class="bi bi-person-heart me-1"></i> Donneur</label>
                            <input type="radio" class="btn-check" name="role" id="roleHopital" value="HOPITAL"
                                   ${roleChoisi == 'HOPITAL' ? 'checked' : ''}>
                            <label class="btn btn-outline-danger" for="roleHopital"><i class="bi bi-hospital me-1"></i> Hôpital</label>
                        </div>
                    </div>

                    <fieldset data-for-role="HOPITAL">
                        <div class="mb-3">
                            <label class="form-label" for="nomHopital">Nom de l'hôpital</label>
                            <input class="form-control" id="nomHopital" name="nomHopital" maxlength="120"
                                   value="${fn:escapeXml(param.nomHopital)}" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="adresse">Adresse</label>
                            <input class="form-control" id="adresse" name="adresse" maxlength="200"
                                   value="${fn:escapeXml(param.adresse)}">
                        </div>
                        <p class="text-muted small mb-2">Responsable du compte :</p>
                    </fieldset>

                    <div class="row">
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="prenom">Prénom</label>
                            <input class="form-control" id="prenom" name="prenom" maxlength="60"
                                   value="${fn:escapeXml(param.prenom)}" required>
                        </div>
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="nom">Nom</label>
                            <input class="form-control" id="nom" name="nom" maxlength="60"
                                   value="${fn:escapeXml(param.nom)}" required>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="email">E-mail</label>
                            <input class="form-control" type="email" id="email" name="email" maxlength="120"
                                   value="${fn:escapeXml(param.email)}" required>
                        </div>
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="telephone">Téléphone</label>
                            <input class="form-control" id="telephone" name="telephone" maxlength="20"
                                   value="${fn:escapeXml(param.telephone)}" placeholder="06 12 34 56 78">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="ville">Ville</label>
                            <input class="form-control" id="ville" name="ville" maxlength="80"
                                   value="${fn:escapeXml(param.ville)}" required>
                        </div>
                        <fieldset class="col-sm-6 mb-3" data-for-role="DONNEUR">
                            <label class="form-label" for="groupeSanguin">Groupe sanguin</label>
                            <select class="form-select" id="groupeSanguin" name="groupeSanguin" required>
                                <option value="">Choisir…</option>
                                <c:forEach var="g" items="${groupes}">
                                    <option value="${g}" ${param.groupeSanguin == g.name() ? 'selected' : ''}>${g.label}</option>
                                </c:forEach>
                            </select>
                        </fieldset>
                    </div>

                    <div class="row">
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="password">Mot de passe</label>
                            <input class="form-control" type="password" id="password" name="password" minlength="8" required>
                            <small class="text-muted">8 caractères minimum, avec une lettre et un chiffre.</small>
                        </div>
                        <div class="col-sm-6 mb-3">
                            <label class="form-label" for="passwordConfirm">Confirmation</label>
                            <input class="form-control" type="password" id="passwordConfirm" name="passwordConfirm" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-lg btn-mc w-100">Créer mon compte</button>
                </form>
            </div>
        </div>
        <p class="text-center mt-3">Déjà inscrit ? <a href="${ctx}/login">Se connecter</a></p>
    </div>
</div>
<script src="${ctx}/assets/js/medconnect.js"></script>
</body>
</html>
