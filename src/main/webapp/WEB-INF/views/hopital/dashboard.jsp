<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Tableau de bord"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h1 class="h3 mb-2"><c:out value="${dash.hopital.nomHopital}"/></h1>
    <a href="${ctx}/hopital/demandes" class="btn btn-mc"><i class="bi bi-plus-lg me-1"></i> Publier une demande</a>
</div>

<div class="row">
    <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body">
        <span class="stat-icon"><i class="bi bi-clipboard2-pulse"></i></span>
        <h5 class="card-title text-muted mt-2">Demandes ouvertes</h5><p class="stat-value">${dash.demandesOuvertes}</p>
    </div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body">
        <span class="stat-icon"><i class="bi bi-exclamation-octagon"></i></span>
        <h5 class="card-title text-muted mt-2">Urgentes ou critiques</h5><p class="stat-value">${dash.demandesUrgentes}</p>
    </div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body">
        <span class="stat-icon"><i class="bi bi-person-raised-hand"></i></span>
        <h5 class="card-title text-muted mt-2">Donneurs à traiter</h5><p class="stat-value">${dash.reponsesEnAttente}</p>
    </div></div></div>
    <div class="col-sm-6 col-xl-3"><div class="card stat"><div class="card-body">
        <span class="stat-icon"><i class="bi bi-droplet-half"></i></span>
        <h5 class="card-title text-muted mt-2">Dons ce mois-ci</h5><p class="stat-value">${dash.donsCeMois}</p>
    </div></div></div>
</div>

<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="card-title mb-0">Demandes récentes</h5>
        <a href="${ctx}/hopital/demandes" class="btn btn-sm btn-outline-secondary">Toutes les demandes</a>
    </div>
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>#</th><th>Groupe</th><th>Urgence</th><th>État</th><th>Date</th><th>Donneurs en attente</th><th>Progression</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="v" items="${dash.demandesRecentes}">
                <tr>
                    <td>${v.demande.id}</td>
                    <td><span class="badge badge-groupe">${v.demande.groupeSanguin.label}</span></td>
                    <td><span class="badge bg-${v.demande.urgence.badge}">${v.demande.urgence.label}</span></td>
                    <td><span class="badge bg-${v.demande.etat.badge}">${v.demande.etat.label}</span></td>
                    <td>${v.demande.dateDemandeFr}</td>
                    <td>${v.enAttente}</td>
                    <td><div class="progress"><div class="progress-bar bg-success" style="width: ${v.progression}%"></div></div>
                        <small class="text-muted">${v.donsRecus} / ${v.demande.quantite}</small></td>
                    <td class="text-end"><a class="btn btn-sm btn-outline-secondary" href="${ctx}/hopital/demande?id=${v.demande.id}">Gérer</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty dash.demandesRecentes}">
                <tr><td colspan="8" class="text-center text-muted py-4">Aucune demande publiée pour l'instant.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
