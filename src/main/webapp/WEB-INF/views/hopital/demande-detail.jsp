<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="v" value="${detail.demande}"/>
<c:set var="d" value="${v.demande}"/>
<c:set var="pageTitle" value="Demande #${d.id}"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<div class="d-flex flex-wrap justify-content-between align-items-center mb-3">
    <h1 class="h3 mb-2">Demande #${d.id}
        <span class="badge badge-groupe ms-2">${d.groupeSanguin.label}</span>
        <span class="badge bg-${d.urgence.badge}">${d.urgence.label}</span>
        <span class="badge bg-${d.etat.badge}">${d.etat.label}</span>
    </h1>
    <div class="text-nowrap">
        <c:if test="${d.etat == 'OUVERTE'}">
            <a class="btn btn-outline-primary" href="${ctx}/hopital/demande/modifier?id=${d.id}"><i class="bi bi-pencil me-1"></i>Modifier</a>
            <form method="post" action="${ctx}/hopital/demande/action" class="inline-form"
                  data-confirm="Annuler cette demande ? Les donneurs qui ont répondu seront informés.">
                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                <input type="hidden" name="id" value="${d.id}"><input type="hidden" name="action" value="annuler">
                <button class="btn btn-outline-warning"><i class="bi bi-x-circle me-1"></i>Annuler la demande</button>
            </form>
        </c:if>
        <c:if test="${empty detail.reponses}">
            <form method="post" action="${ctx}/hopital/demande/action" class="inline-form"
                  data-confirm="Supprimer définitivement cette demande ?">
                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                <input type="hidden" name="id" value="${d.id}"><input type="hidden" name="action" value="supprimer">
                <button class="btn btn-outline-danger"><i class="bi bi-trash me-1"></i>Supprimer</button>
            </form>
        </c:if>
    </div>
</div>

<div class="row">
    <div class="col-md-4"><div class="card"><div class="card-body">
        <h5 class="card-title text-muted">Progression</h5>
        <p class="stat-value">${v.donsRecus} / ${d.quantite} <small class="fs-6 text-muted">poches</small></p>
        <div class="progress mt-2"><div class="progress-bar bg-success" style="width: ${v.progression}%"></div></div>
    </div></div></div>
    <div class="col-md-8"><div class="card"><div class="card-body">
        <h5 class="card-title text-muted">Informations</h5>
        <p class="mb-1">Publiée le <strong>${d.dateDemandeFr}</strong></p>
        <p class="mb-0"><c:out value="${empty d.description ? 'Pas de description.' : d.description}"/></p>
    </div></div></div>
</div>

<div class="card">
    <div class="card-header"><h5 class="card-title mb-0">Donneurs qui ont répondu (${fn:length(detail.reponses)})</h5></div>
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>Donneur</th><th>Groupe</th><th>Contact</th><th>Ville</th><th>Réponse le</th><th>Statut</th><th>Actions</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${detail.reponses}">
                <tr>
                    <td><c:out value="${r.donneur.nomComplet}"/></td>
                    <td><span class="badge badge-groupe">${r.donneur.groupeSanguin.label}</span></td>
                    <td class="small"><c:out value="${r.donneur.telephone}"/><br><c:out value="${r.donneur.email}"/></td>
                    <td><c:out value="${r.donneur.ville}"/></td>
                    <td>${r.dateCreationFr}</td>
                    <td><span class="badge bg-${r.statut.badge}">${r.statut.label}</span></td>
                    <td>
                        <c:if test="${r.statut == 'EN_ATTENTE' and d.etat == 'OUVERTE'}">
                            <form method="post" action="${ctx}/hopital/reponse/action" class="inline-form">
                                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                                <input type="hidden" name="id" value="${r.id}"><input type="hidden" name="demandeId" value="${d.id}">
                                <input type="hidden" name="action" value="confirmer">
                                <button class="btn btn-sm btn-success"><i class="bi bi-check-lg"></i> Confirmer</button>
                            </form>
                        </c:if>
                        <c:if test="${r.statut == 'CONFIRMEE'}">
                            <form method="post" action="${ctx}/hopital/reponse/action" class="don-form mb-1">
                                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                                <input type="hidden" name="id" value="${r.id}"><input type="hidden" name="demandeId" value="${d.id}">
                                <input type="hidden" name="action" value="don">
                                <input type="date" class="form-control form-control-sm" name="dateDon" value="${today}" max="${today}" required>
                                <input type="text" class="form-control form-control-sm" name="lieu" maxlength="150" placeholder="Lieu (facultatif)">
                                <button class="btn btn-sm btn-mc"><i class="bi bi-droplet-fill"></i> Don effectué</button>
                            </form>
                        </c:if>
                        <c:if test="${r.statut == 'EN_ATTENTE' or r.statut == 'CONFIRMEE'}">
                            <form method="post" action="${ctx}/hopital/reponse/action" class="inline-form"
                                  data-confirm="Refuser ce donneur ?">
                                <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
                                <input type="hidden" name="id" value="${r.id}"><input type="hidden" name="demandeId" value="${d.id}">
                                <input type="hidden" name="action" value="refuser">
                                <button class="btn btn-sm btn-outline-secondary">Refuser</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty detail.reponses}">
                <tr><td colspan="7" class="text-center text-muted py-4">Aucun donneur n'a encore répondu à cette demande.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<a href="${ctx}/hopital/demandes" class="btn btn-outline-secondary"><i class="bi bi-arrow-left me-1"></i> Retour aux demandes</a>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
