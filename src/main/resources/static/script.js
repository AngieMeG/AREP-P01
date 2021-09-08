document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("submit").addEventListener("click", () => {
        var request = "consulta?lugar=" + document.getElementById("inputField").value
        window.location.replace();
    })
});