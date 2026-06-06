const API_URL = "http://localhost:8080/api";

const loginForm = document.getElementById("loginForm");
const registroForm = document.getElementById("registroForm");
const resultado = document.getElementById("resultado");

function usuarioActual() {
    const guardado = localStorage.getItem("usuarioAutoRent");
    return guardado ? JSON.parse(guardado) : null;
}

function guardarUsuario(usuario) {
    localStorage.setItem("usuarioAutoRent", JSON.stringify(usuario));
}

function mostrarMensaje(texto, tipo = "success") {
    const mensaje = document.getElementById("mensaje");
    if (!mensaje) return;

    mensaje.className = `alert alert-${tipo}`;
    mensaje.textContent = texto;
}

async function pedirApi(url, metodo = "GET", body = null) {
    const opciones = {
        method: metodo,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (body) {
        opciones.body = JSON.stringify(body);
    }

    const respuesta = await fetch(`${API_URL}${url}`, opciones);
    const texto = await respuesta.text();
    let datos = texto;

    try {
        datos = texto ? JSON.parse(texto) : null;
    } catch (error) {
        datos = texto;
    }

    if (!respuesta.ok) {
        throw new Error(typeof datos === "string" ? datos : JSON.stringify(datos));
    }

    return datos;
}

if (loginForm) {
    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        try {
            const usuario = await pedirApi("/usuarios/login", "POST", {
                email: document.getElementById("email").value,
                password: document.getElementById("password").value
            });

            guardarUsuario(usuario);
            window.location.href = "panel.html";
        } catch (error) {
            mostrarMensaje(error.message || "No se pudo iniciar sesion", "danger");
        }
    });
}

if (registroForm) {
    registroForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        try {
            const usuario = await pedirApi("/usuarios/registro", "POST", {
                nombre: document.getElementById("nombre").value,
                email: document.getElementById("email").value,
                telefono: document.getElementById("telefono").value,
                password: document.getElementById("password").value
            });

            guardarUsuario(usuario);
            window.location.href = "panel.html";
        } catch (error) {
            mostrarMensaje(error.message || "No se pudo registrar", "danger");
        }
    });
}

if (resultado) {
    iniciarPanel();
}

function iniciarPanel() {
    const usuario = usuarioActual();

    if (!usuario) {
        window.location.href = "index.html";
        return;
    }

    document.getElementById("usuarioNombre").textContent = usuario.nombre;
    document.getElementById("usuarioRoles").textContent = ` - Roles: ${(usuario.roles || []).join(", ")}`;

    document.getElementById("cerrarSesion").addEventListener("click", function () {
        localStorage.removeItem("usuarioAutoRent");
        window.location.href = "index.html";
    });

    document.querySelectorAll("[data-current-user-id]").forEach(input => {
        input.value = usuario.idUsuario;
    });

    bloquearPorRol(usuario);

    document.querySelectorAll("[data-api-form]").forEach(form => {
        form.addEventListener("submit", ejecutarFormulario);
    });
}

function bloquearPorRol(usuario) {
    const rolesUsuario = usuario.roles || [];

    document.querySelectorAll("[data-roles]").forEach(contenedor => {
        const rolesPermitidos = contenedor.dataset.roles.split(" ");
        const puedeUsar = rolesPermitidos.some(rol => rolesUsuario.includes(rol));

        if (!puedeUsar) {
            contenedor.classList.add("accion-bloqueada");
            contenedor.querySelectorAll("input, select, button").forEach(control => {
                control.disabled = true;
            });

            const aviso = document.createElement("div");
            aviso.className = "small text-muted mt-2";
            aviso.textContent = "[bloqueado por rol]";
            contenedor.querySelector(".endpoint-card").appendChild(aviso);
        }
    });
}

async function ejecutarFormulario(event) {
    event.preventDefault();

    const form = event.target;
    const metodo = form.dataset.method;
    const url = armarUrl(form);
    const body = armarBody(form);

    try {
        resultado.textContent = "Cargando...";
        const datos = await pedirApi(url, metodo, body);
        resultado.textContent = datos ? JSON.stringify(datos, null, 2) : "Operacion realizada correctamente.";
    } catch (error) {
        resultado.textContent = error.message || "Error al ejecutar la accion.";
    }
}

function armarUrl(form) {
    let url = form.dataset.url;

    form.querySelectorAll("input, select").forEach(input => {
        const valor = encodeURIComponent(input.value.trim());
        url = url.replace(`{${input.name}}`, valor);
    });

    return url;
}

function armarBody(form) {
    const campos = form.querySelectorAll("[data-body]");

    if (!campos.length) {
        return null;
    }

    const body = {};

    campos.forEach(input => {
        if (input.value === "") return;
        body[input.name] = convertirValor(input.name, input.value);
    });

    return body;
}

function convertirValor(nombre, valor) {
    const camposNumericos = [
        "anio",
        "capacidadPasajeros",
        "cantidadPuertas",
        "precioDia",
        "idAuto",
        "idReserva",
        "monto",
        "puntuacion"
    ];

    if (valor === "true") return true;
    if (valor === "false") return false;
    if (camposNumericos.includes(nombre)) return Number(valor);
    return valor;
}
