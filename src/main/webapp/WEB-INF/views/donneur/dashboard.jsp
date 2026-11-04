<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Tableau de bord"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Bonjour <strong><c:out value="${dash.donneur.prenom}"/></strong></h1>

<c:if test="${empty dash.donneur.groupeSanguin}">
    <div class="alert alert-warning"><div class="alert-message">
        Renseignez votre groupe sanguin dans <a href="${ctx}/donneur/profil">votre profil</a> pour voir les demandes compatibles.
    </div></div>
</c:if>

<div class="row">
    <div class="col-sm-6 col-xl-3">
        <div class="card stat"><div class="card-body">
            <span class="stat-icon"><i class="bi bi-droplet"></i></span>
            <h5 class="card-title text-muted mt-2">Groupe sanguin</h5>
            <p class="stat-value">${empty dash.donneur.groupeSanguin ? '—' : dash.donneur.groupeSanguin.label}</p>
        </div></div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat"><div class="card-body">
            <span class="stat-icon"><i class="bi bi-heart-pulse"></i></span>
            <h5 class="card-title text-muted mt-2">Dons effectués</h5>
            <p class="stat-value">${dash.totalDons}</p>
        </div></div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat"><div class="card-body">
            <span class="stat-icon"><i class="bi bi-calendar-check"></i></span>
            <h5 class="card-title text-muted mt-2">Prochain don possible</h5>
            <p class="stat-value fs-4">
                <c:choose>
                    <c:when test="${dash.eligible}"><span class="text-success">Dès maintenant</span></c:when>
                    <c:otherwise>${dash.prochaineDate}</c:otherwise>
                </c:choose>
            </p>
        </div></div>
    </div>
    <div class="col-sm-6 col-xl-3">
        <div class="card stat"><div class="card-body">
            <span class="stat-icon"><i class="bi bi-hourglass-split"></i></span>
            <h5 class="card-title text-muted mt-2">Réponses en cours</h5>
            <p class="stat-value">${dash.reponsesEnAttente}</p>
        </div></div>
    </div>
</div>

<div class="row">
    <div class="col-xl-7">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Demandes prioritaires pour vous</h5>
                <a href="${ctx}/donneur/annonces" class="btn btn-sm btn-outline-secondary">Tout voir</a>
            </div>
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead><tr><th>Urgence</th><th>Groupe</th><th>Hôpital</th><th>Ville</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach var="a" items="${dash.annoncesPrioritaires}">
                        <tr>
                            <td><span class="badge bg-${a.demande.urgence.badge}">${a.demande.urgence.label}</span></td>
                            <td><span class="badge badge-groupe">${a.demande.groupeSanguin.label}</span></td>
                            <td><c:out value="${a.demande.hopital.nomHopital}"/></td>
                            <td><c:out value="${a.demande.hopital.ville}"/></td>
                            <td class="text-end">
                                <c:if test="${dash.eligible}">
                                    <form method="post" action="${ctx}/donneur/annonces" class="inline-form">
                                        <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                                        <input type="hidden" name="demandeId" value="${a.demande.id}">
                                        <button class="btn btn-sm btn-mc">Je veux donner</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty dash.annoncesPrioritaires}">
                        <tr><td colspan="5" class="text-center text-muted py-4">Aucune nouvelle demande compatible pour le moment.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col-xl-5">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">Mes dernières réponses</h5>
                <a href="${ctx}/donneur/reponses" class="btn btn-sm btn-outline-secondary">Tout voir</a>
            </div>
            <ul class="list-group list-group-flush">
                <c:forEach var="r" items="${dash.dernieresReponses}">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        <span><c:out value="${r.demande.hopital.nomHopital}"/>
                            <small class="text-muted d-block">${r.demande.groupeSanguin.label} · ${r.dateCreationFr}</small></span>
                        <span class="badge bg-${r.statut.badge}">${r.statut.label}</span>
                    </li>
                </c:forEach>
                <c:if test="${empty dash.dernieresReponses}">
                    <li class="list-group-item text-muted text-center py-4">Vous n'avez encore répondu à aucune demande.</li>
                </c:if>
            </ul>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
