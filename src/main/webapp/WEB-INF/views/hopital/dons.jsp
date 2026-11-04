<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Dons reçus"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Dons reçus</h1>
<div class="card">
    <div class="table-responsive">
        <table class="table table-hover mb-0">
            <thead><tr><th>Date</th><th>Donneur</th><th>Groupe du donneur</th><th>Demande</th><th>Lieu</th></tr></thead>
            <tbody>
            <c:forEach var="don" items="${dons}">
                <tr>
                    <td>${don.dateDonFr}</td>
                    <td><c:out value="${don.donneur.nomComplet}"/></td>
                    <td><span class="badge badge-groupe">${don.donneur.groupeSanguin.label}</span></td>
                    <td><a href="${ctx}/hopital/demande?id=${don.demande.id}">#${don.demande.id} (${don.demande.groupeSanguin.label})</a></td>
                    <td><c:out value="${don.lieu}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty dons}">
                <tr><td colspan="5" class="text-center text-muted py-4">Aucun don enregistré pour l'instant.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
