<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Mon profil"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Mon profil</h1>
<div class="row">
    <div class="col-lg-8">
        <div class="card"><div class="card-body">
            <form method="post" action="${ctx}/donneur/profil">
                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="prenom">Prénom</label>
                        <input class="form-control" id="prenom" name="prenom" maxlength="60" value="${fn:escapeXml(donneur.prenom)}" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="nom">Nom</label>
                        <input class="form-control" id="nom" name="nom" maxlength="60" value="${fn:escapeXml(donneur.nom)}" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="email">E-mail</label>
                        <input class="form-control" id="email" value="${fn:escapeXml(donneur.email)}" disabled>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="telephone">Téléphone</label>
                        <input class="form-control" id="telephone" name="telephone" maxlength="20" value="${fn:escapeXml(donneur.telephone)}">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="ville">Ville</label>
                        <input class="form-control" id="ville" name="ville" maxlength="80" value="${fn:escapeXml(donneur.ville)}" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="adresse">Adresse</label>
                        <input class="form-control" id="adresse" name="adresse" maxlength="200" value="${fn:escapeXml(donneur.adresse)}">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="groupeSanguin">Groupe sanguin</label>
                        <select class="form-select" id="groupeSanguin" name="groupeSanguin" required>
                            <c:forEach var="g" items="${groupes}">
                                <option value="${g}" ${donneur.groupeSanguin == g ? 'selected' : ''}>${g.label}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="disponibilite">Disponibilité</label>
                        <select class="form-select" id="disponibilite" name="disponibilite">
                            <c:forEach var="d" items="${disponibilites}">
                                <option value="${d}" ${donneur.disponibilite == d ? 'selected' : ''}>${d.label}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-mc">Enregistrer</button>
            </form>
        </div></div>
    </div>
    <div class="col-lg-4">
        <div class="card"><div class="card-body">
            <h5 class="card-title">Mes dons</h5>
            <p class="mb-1">Dernier don : <strong>${empty donneur.dateDernierDon ? 'aucun' : donneur.dateDernierDonFr}</strong></p>
            <p class="mb-0">Prochain don possible : <strong>${prochaineDate}</strong></p>
            <p class="text-muted small mt-3 mb-0">Un délai minimum de 90 jours est appliqué entre deux dons.</p>
        </div></div>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
