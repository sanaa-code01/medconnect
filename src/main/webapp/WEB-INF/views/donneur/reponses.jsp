<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Mes réponses"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Mes réponses aux demandes</h1>
<div class="card">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>Date</th><th>Hôpital</th><th>Ville</th><th>Groupe demandé</th><th>Statut</th><th>Prochaine étape</th></tr></thead>
            <tbody>
            <c:forEach var="r" items="${reponses}">
                <tr>
                    <td>${r.dateCreationFr}</td>
                    <td><c:out value="${r.demande.hopital.nomHopital}"/></td>
                    <td><c:out value="${r.demande.hopital.ville}"/></td>
                    <td><span class="badge badge-groupe">${r.demande.groupeSanguin.label}</span></td>
                    <td><span class="badge bg-${r.statut.badge}">${r.statut.label}</span></td>
                    <td class="small">
                        <c:choose>
                            <c:when test="${r.statut == 'EN_ATTENTE'}">L'hôpital va étudier votre proposition.</c:when>
                            <c:when test="${r.statut == 'CONFIRMEE'}">
                                Vous êtes attendu. Contact : <strong><c:out value="${r.demande.hopital.telephone}"/></strong>
                                <c:if test="${not empty r.demande.hopital.adresse}">· <c:out value="${r.demande.hopital.adresse}"/></c:if>
                            </c:when>
                            <c:when test="${r.statut == 'DON_EFFECTUE'}">Merci pour votre don !</c:when>
                            <c:otherwise>Votre aide n'est plus nécessaire pour cette demande.</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty reponses}">
                <tr><td colspan="6" class="text-center text-muted py-4">
                    Aucune réponse pour l'instant. Consultez les <a href="${ctx}/donneur/annonces">demandes compatibles</a>.
                </td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
