<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Demandes de sang"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h1 class="h3 mb-2">Demandes de sang compatibles</h1>
    <div class="btn-group">
        <a href="${ctx}/donneur/annonces" class="btn btn-sm ${maVille ? 'btn-outline-secondary' : 'btn-secondary'}">Toutes les villes</a>
        <a href="${ctx}/donneur/annonces?ville=1" class="btn btn-sm ${maVille ? 'btn-secondary' : 'btn-outline-secondary'}">
            Ma ville (<c:out value="${donneur.ville}"/>)</a>
    </div>
</div>

<c:choose>
    <c:when test="${empty donneur.groupeSanguin}">
        <div class="alert alert-warning"><div class="alert-message">
            Renseignez votre groupe sanguin dans <a href="${ctx}/donneur/profil">votre profil</a>.
        </div></div>
    </c:when>
    <c:otherwise>
        <p class="text-muted">
            Votre groupe <span class="badge badge-groupe">${donneur.groupeSanguin.label}</span> peut donner aux groupes :
            <c:forEach var="g" items="${groupesCompatibles}"><span class="badge bg-light text-dark me-1">${g.label}</span></c:forEach>
        </p>
    </c:otherwise>
</c:choose>

<c:if test="${not eligible}">
    <div class="alert alert-info"><div class="alert-message">
        <i class="bi bi-info-circle me-1"></i> Vous avez donné récemment : vous pourrez de nouveau répondre à partir du <strong>${prochaineDate}</strong>.
    </div></div>
</c:if>
<c:if test="${donneur.disponibilite == 'INDISPONIBLE'}">
    <div class="alert alert-info"><div class="alert-message">
        <i class="bi bi-info-circle me-1"></i> Vous êtes indiqué comme indisponible. Changez votre disponibilité dans <a href="${ctx}/donneur/profil">votre profil</a> pour répondre.
    </div></div>
</c:if>

<div class="card">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>Urgence</th><th>Groupe</th><th>Poches</th><th>Hôpital</th><th>Ville</th><th>Date</th><th>Description</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="a" items="${annonces}">
                <tr>
                    <td><span class="badge bg-${a.demande.urgence.badge}">${a.demande.urgence.label}</span></td>
                    <td><span class="badge badge-groupe">${a.demande.groupeSanguin.label}</span></td>
                    <td>${a.demande.quantite}</td>
                    <td><c:out value="${a.demande.hopital.nomHopital}"/></td>
                    <td><c:out value="${a.demande.hopital.ville}"/>
                        <c:if test="${a.memeVille}"><span class="badge bg-success ms-1">Votre ville</span></c:if></td>
                    <td>${a.demande.dateDemandeFr}</td>
                    <td class="small"><c:out value="${a.demande.description}"/></td>
                    <td class="text-end">
                        <c:choose>
                            <c:when test="${a.dejaRepondu}"><span class="badge bg-secondary">Déjà répondu</span></c:when>
                            <c:when test="${eligible and donneur.disponibilite != 'INDISPONIBLE'}">
                                <form method="post" action="${ctx}/donneur/annonces" class="inline-form"
                                      data-confirm="Confirmer votre proposition de don à cet hôpital ?">
                                    <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                                    <input type="hidden" name="demandeId" value="${a.demande.id}">
                                    <button class="btn btn-sm btn-mc"><i class="bi bi-droplet-fill me-1"></i>Je veux donner</button>
                                </form>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty annonces}">
                <tr><td colspan="8" class="text-center text-muted py-4">Aucune demande compatible pour le moment. Merci de votre engagement !</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
