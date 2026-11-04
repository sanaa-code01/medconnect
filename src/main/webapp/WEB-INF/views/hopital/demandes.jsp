<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Demandes de sang"/>
<c:set var="fGroupe" value="${param.groupeSanguin}"/>
<c:set var="fQuantite" value="${empty param.quantite ? '1' : param.quantite}"/>
<c:set var="fUrgence" value="${empty param.urgence ? 'URGENT' : param.urgence}"/>
<c:set var="fDescription" value="${param.description}"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Demandes de sang</h1>

<div class="card">
    <div class="card-header"><h5 class="card-title mb-0"><i class="bi bi-plus-circle me-1"></i> Publier une nouvelle demande</h5></div>
    <div class="card-body">
        <form method="post" action="${ctx}/hopital/demandes">
            <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
            <%@ include file="/WEB-INF/views/hopital/_demande-fields.jspf" %>
            <button type="submit" class="btn btn-mc">Publier</button>
        </form>
    </div>
</div>

<div class="card">
    <div class="card-header"><h5 class="card-title mb-0">Toutes mes demandes</h5></div>
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>#</th><th>Groupe</th><th>Poches</th><th>Urgence</th><th>État</th><th>Date</th><th>En attente</th><th>Confirmés</th><th>Progression</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="v" items="${demandes}">
                <tr>
                    <td>${v.demande.id}</td>
                    <td><span class="badge badge-groupe">${v.demande.groupeSanguin.label}</span></td>
                    <td>${v.demande.quantite}</td>
                    <td><span class="badge bg-${v.demande.urgence.badge}">${v.demande.urgence.label}</span></td>
                    <td><span class="badge bg-${v.demande.etat.badge}">${v.demande.etat.label}</span></td>
                    <td>${v.demande.dateDemandeFr}</td>
                    <td>${v.enAttente}</td>
                    <td>${v.confirmees}</td>
                    <td><div class="progress"><div class="progress-bar bg-success" style="width: ${v.progression}%"></div></div>
                        <small class="text-muted">${v.donsRecus} / ${v.demande.quantite}</small></td>
                    <td class="text-end text-nowrap">
                        <a class="btn btn-sm btn-outline-secondary" href="${ctx}/hopital/demande?id=${v.demande.id}">Gérer</a>
                        <c:if test="${v.demande.etat == 'OUVERTE'}">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/hopital/demande/modifier?id=${v.demande.id}"><i class="bi bi-pencil"></i></a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty demandes}">
                <tr><td colspan="10" class="text-center text-muted py-4">Aucune demande pour l'instant.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
