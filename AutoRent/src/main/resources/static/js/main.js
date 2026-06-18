const API_URL = "/api";
const token = localStorage.getItem("autorent_token");
const usuario = JSON.parse(localStorage.getItem("autorent_usuario") || "null");

const resultado = document.getElementById("resultado");
const mensaje = document.getElementById("mensaje");
const cerrarSesion = document.getElementById("cerrarSesion");
const usuarioNombre = document.getElementById("usuarioNombre");
const usuarioRoles = document.getElementById("usuarioRoles");

if (!token || !usuario) {
    window.location.href = "login.html";
}

function mostrarMensaje(tipo, texto) {
    mensaje.className = `alert alert-${tipo}`;
    mensaje.textContent = texto;
}

function limpiarMensaje() {
    mensaje.className = "alert d-none";
    mensaje.textContent = "";
}

function mostrarResultado(data) {
    if (data === null || data === undefined || data === "") {
        resultado.textContent = "Operacion realizada correctamente.";
        return;
    }
    resultado.textContent = JSON.stringify(data, null, 2);
}

function obtenerRoles() {
    return usuario?.roles || [];
}

function configurarUsuario() {
    usuarioNombre.textContent = usuario?.nombre || usuario?.email || "Usuario";
    usuarioRoles.textContent = obtenerRoles().join(", ");

    document.querySelectorAll("[data-current-user-id]").forEach((input) => {
        input.value = usuario.idUsuario;
    });
}

function configurarPermisos() {
    const rolesUsuario = obtenerRoles();

    document.querySelectorAll("[data-roles]").forEach((elemento) => {
        const rolesPermitidos = elemento.dataset.roles.split(" ");
        const puedeVer = rolesPermitidos.some((rol) => rolesUsuario.includes(rol));

        if (!puedeVer) {
            elemento.classList.add("d-none");
        }
    });
}

function reemplazarParametros(url, formData) {
    let urlFinal = url;

    for (const [clave, valor] of formData.entries()) {
        urlFinal = urlFinal.replace(`{${clave}}`, encodeURIComponent(valor));
    }

    return urlFinal;
}

function crearBody(form) {
    const body = {};

    form.querySelectorAll("[data-body]").forEach((campo) => {
        if (campo.value !== "") {
            body[campo.name] = campo.type === "number" ? Number(campo.value) : campo.value;
        }
    });

    return body;
}

async function ejecutarFormulario(form) {
    limpiarMensaje();

    const method = form.dataset.method;
    const formData = new FormData(form);
    const url = reemplazarParametros(form.dataset.url, formData);
    const opciones = {
        method,
        headers: {
            Authorization: `Bearer ${token}`
        }
    };

    if (method !== "GET" && method !== "DELETE") {
        opciones.headers["Content-Type"] = "application/json";
        opciones.body = JSON.stringify(crearBody(form));
    }

    const response = await fetch(`${API_URL}${url}`, opciones);
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.mensaje || data?.error || data?.message || "Error al ejecutar la accion");
    }

    mostrarResultado(data);
    mostrarMensaje("success", "Operacion realizada correctamente.");
}

document.querySelectorAll("[data-api-form]").forEach((form) => {
    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        try {
            await ejecutarFormulario(form);
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
});

cerrarSesion.addEventListener("click", async () => {
    try {
        await fetch(`${API_URL}/usuarios/logout`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    } finally {
        localStorage.removeItem("autorent_token");
        localStorage.removeItem("autorent_usuario");
        window.location.href = "login.html";
    }
});

configurarUsuario();
configurarPermisos();
