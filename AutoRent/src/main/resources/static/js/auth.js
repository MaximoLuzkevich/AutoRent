const API_BASE = "/api";

const mensaje = document.getElementById("mensaje");
const loginForm = document.getElementById("loginForm");
const registroForm = document.getElementById("registroForm");

function mostrarMensaje(tipo, texto) {
    mensaje.className = `alert alert-${tipo}`;
    mensaje.textContent = texto;
}

function obtenerError(data, fallback) {
    if (!data) {
        return fallback;
    }
    return data.mensaje || data.error || data.message || fallback;
}

function obtenerPanel(usuario) {
    return "cliente-inicio.html";
}

async function enviarJson(url, body) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });

    const text = await response.text();
    let data = null;

    if (text) {
        try {
            data = JSON.parse(text);
        } catch (error) {
            throw new Error("La API devolvio HTML en vez de JSON. Abri el frontend desde http://localhost:8080/login.html y verifica que Spring Boot este levantado.");
        }
    }

    if (!response.ok) {
        throw new Error(obtenerError(data, "No se pudo completar la operacion"));
    }

    return data;
}

if (loginForm) {
    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(loginForm);
        const body = {
            email: formData.get("email"),
            password: formData.get("password")
        };

        try {
            const data = await enviarJson("/usuarios/login", body);
            localStorage.setItem("autorent_token", data.token);
            localStorage.setItem("autorent_usuario", JSON.stringify(data.usuario));
            window.location.href = obtenerPanel(data.usuario);
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}

if (registroForm) {
    registroForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(registroForm);
        const body = {
            nombre: formData.get("nombre"),
            email: formData.get("email"),
            telefono: formData.get("telefono"),
            password: formData.get("password")
        };

        try {
            await enviarJson("/usuarios/registro", body);
            mostrarMensaje("success", "Cuenta creada correctamente.");
            registroForm.reset();
            setTimeout(() => {
                window.location.href = "login.html";
            }, 900);
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}
