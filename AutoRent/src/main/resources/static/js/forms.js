const API_BASE = "/api";
const token = localStorage.getItem("autorent_token");
const usuarioGuardado = JSON.parse(localStorage.getItem("autorent_usuario") || "null");
const mensaje = document.getElementById("mensaje");
const propietarioForm = document.getElementById("propietarioForm");
const autoForm = document.getElementById("autoForm");
const imagenForm = document.getElementById("imagenForm");

if (!token || !usuarioGuardado) {
    window.location.href = "login.html";
}

function mostrarMensaje(tipo, texto) {
    mensaje.className = `alert alert-${tipo}`;
    mensaje.textContent = texto;
}

function crearBody(form) {
    const body = {};

    new FormData(form).forEach((valor, clave) => {
        if (valor === "" || clave === "idAuto") {
            return;
        }

        const input = form.elements[clave];
        body[clave] = input?.type === "number" ? Number(valor) : valor;
    });

    return body;
}

function obtenerPanel() {
    return "cliente-inicio.html";
}

async function enviarFormulario(url, body, method = "POST") {
    const response = await fetch(`${API_BASE}${url}`, {
        method,
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(body)
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo guardar");
    }

    return data;
}

async function refrescarUsuario() {
    const response = await fetch(`${API_BASE}/usuarios/me`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    if (!response.ok) {
        return;
    }

    const usuario = await response.json();
    localStorage.setItem("autorent_usuario", JSON.stringify(usuario));
}

if (propietarioForm) {
    propietarioForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        try {
            await enviarFormulario("/propietarios/me", crearBody(propietarioForm));
            await refrescarUsuario();
            mostrarMensaje("success", "Perfil de propietario creado correctamente.");
            setTimeout(() => {
                window.location.href = obtenerPanel();
            }, 900);
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}

if (autoForm) {
    autoForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        try {
            const idAuto = autoForm.elements.idAuto.value;
            const url = idAuto ? `/autos/${idAuto}/me` : "/autos/me";
            const method = idAuto ? "PUT" : "POST";
            const auto = await enviarFormulario(url, crearBody(autoForm), method);
            mostrarMensaje("success", idAuto
                ? `Auto modificado correctamente. ID: ${auto.idAuto}`
                : `Auto publicado correctamente. ID: ${auto.idAuto}`);
            if (imagenForm) {
                imagenForm.elements.idAuto.value = auto.idAuto;
            }
            autoForm.reset();
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}

if (imagenForm) {
    imagenForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const idAuto = imagenForm.elements.idAuto.value;
        const body = {
            nombreArchivo: imagenForm.elements.nombreArchivo.value,
            urlImagen: imagenForm.elements.urlImagen.value,
            principal: imagenForm.elements.principal.checked
        };

        try {
            const imagen = await enviarFormulario(`/autos/${idAuto}/imagenes`, body);
            mostrarMensaje("success", `Imagen agregada correctamente. ID: ${imagen.idImagen}`);
            imagenForm.reset();
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}

document.getElementById("volverPanel")?.setAttribute("href", obtenerPanel());
