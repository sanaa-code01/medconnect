// Petit JavaScript de l'application (fichier séparé : la CSP interdit le JavaScript inline)
document.addEventListener('DOMContentLoaded', function () {
    // Confirmation avant les actions importantes : <form data-confirm="Message">
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!window.confirm(form.dataset.confirm)) {
                e.preventDefault();
            }
        });
    });

    // Inscription : n'affiche (et n'envoie) que les champs du type de compte choisi
    var radios = document.querySelectorAll('input[name="role"]');
    function syncRole() {
        var checked = document.querySelector('input[name="role"]:checked');
        var role = checked ? checked.value : null;
        document.querySelectorAll('[data-for-role]').forEach(function (fs) {
            var visible = fs.dataset.forRole === role;
            fs.hidden = !visible;
            fs.disabled = !visible;
        });
    }
    radios.forEach(function (r) { r.addEventListener('change', syncRole); });
    if (radios.length) { syncRole(); }
});
