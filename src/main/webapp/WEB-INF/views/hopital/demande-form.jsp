<%@ include file="/WEB-INF/views/layout/taglibs.jspf" %>
<c:set var="pageTitle" value="Modifier la demande"/>
<c:set var="fGroupe" value="${empty param.groupeSanguin ? demande.groupeSanguin.name() : param.groupeSanguin}"/>
<c:set var="fQuantite" value="${empty param.quantite ? demande.quantite : param.quantite}"/>
<c:set var="fUrgence" value="${empty param.urgence ? demande.urgence.name() : param.urgence}"/>
<c:set var="fDescription" value="${empty param.description ? demande.description : param.description}"/>
<%@ include file="/WEB-INF/views/layout/app-start.jspf" %>
<h1 class="h3 mb-3">Modifier la demande #${demande.id}</h1>
<div class="card"><div class="card-body">
    <form method="post" action="${ctx}/hopital/demande/modifier">
        <%@ include file="/WEB-INF/views/layout/csrf.jspf" %>
        <input type="hidden" name="id" value="${demande.id}">
        <%@ include file="/WEB-INF/views/hopital/_demande-fields.jspf" %>
        <button type="submit" class="btn btn-mc">Enregistrer</button>
        <a href="${ctx}/hopital/demande?id=${demande.id}" class="btn btn-outline-secondary">Annuler</a>
    </form>
</div></div>
<%@ include file="/WEB-INF/views/layout/app-end.jspf" %>
