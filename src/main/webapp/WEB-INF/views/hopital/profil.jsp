<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Profil de l'hôpital"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Profil de l'hôpital</h1>
<div class="card"><div class="card-body">
    <form method="post" action="${ctx}/hopital/profil">
        <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label" for="nomHopital">Nom de l'hôpital</label>
                <input class="form-control" id="nomHopital" name="nomHopital" maxlength="120" value="${fn:escapeXml(hopital.nomHopital)}" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label" for="email">E-mail</label>
                <input class="form-control" id="email" value="${fn:escapeXml(hopital.email)}" disabled>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label" for="ville">Ville</label>
                <input class="form-control" id="ville" name="ville" maxlength="80" value="${fn:escapeXml(hopital.ville)}" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label" for="adresse">Adresse</label>
                <input class="form-control" id="adresse" name="adresse" maxlength="200" value="${fn:escapeXml(hopital.adresse)}">
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label" for="prenom">Prénom du responsable</label>
                <input class="form-control" id="prenom" name="prenom" maxlength="60" value="${fn:escapeXml(hopital.prenom)}" required>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label" for="nom">Nom du responsable</label>
                <input class="form-control" id="nom" name="nom" maxlength="60" value="${fn:escapeXml(hopital.nom)}" required>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label" for="telephone">Téléphone (visible par les donneurs confirmés)</label>
                <input class="form-control" id="telephone" name="telephone" maxlength="20" value="${fn:escapeXml(hopital.telephone)}">
            </div>
        </div>
        <button type="submit" class="btn btn-mc">Enregistrer</button>
    </form>
</div></div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
