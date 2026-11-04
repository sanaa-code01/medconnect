<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Historique des dons"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Historique de mes dons</h1>
<div class="card">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>Date</th><th>Hôpital</th><th>Lieu</th><th>Groupe demandé</th></tr></thead>
            <tbody>
            <c:forEach var="d" items="${dons}">
                <tr>
                    <td>${d.dateDonFr}</td>
                    <td><c:out value="${d.demande.hopital.nomHopital}"/></td>
                    <td><c:out value="${d.lieu}"/></td>
                    <td><span class="badge badge-groupe">${d.demande.groupeSanguin.label}</span></td>
                </tr>
            </c:forEach>
            <c:if test="${empty dons}">
                <tr><td colspan="4" class="text-center text-muted py-4">Aucun don enregistré pour le moment.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
