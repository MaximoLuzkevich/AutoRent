const API_BASE = "/api";
const token = localStorage.getItem("autorent_token");
const usuarioGuardado = JSON.parse(localStorage.getItem("autorent_usuario") || "null");
const mensaje = document.getElementById("mensaje");
const propietarioForm = document.getElementById("propietarioForm");
const autoForm = document.getElementById("autoForm");
const imagenForm = document.getElementById("imagenForm");
let archivosImagenSeleccionados = [];

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

function debounce(fn, delay = 350) {
    let timeoutId;
    return (...args) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => fn(...args), delay);
    };
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

async function enviarMultipart(url, formData, method = "POST") {
    const response = await fetch(`${API_BASE}${url}`, {
        method,
        headers: {
            Authorization: `Bearer ${token}`
        },
        body: formData
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo guardar");
    }

    return data;
}

async function obtenerJson(url) {
    const response = await fetch(`${API_BASE}${url}`, {
        headers: {
            Accept: "application/json",
            Authorization: `Bearer ${token}`
        }
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo consultar");
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

async function subirImagenesAuto(idAuto, archivos, principalPrimera = false) {
    for (const [index, file] of archivos.entries()) {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("principal", principalPrimera && index === 0);
        await enviarMultipart(`/autos/${idAuto}/imagenes/upload`, formData);
    }
}

function actualizarPreviewImagenes() {
    if (!imagenForm) {
        return;
    }

    const archivos = archivosImagenSeleccionados;
    const estadoVacio = document.getElementById("imageUploadEmpty");
    const preview = document.getElementById("imageUploadPreview");
    const imagenPrincipal = document.getElementById("mainImagePreview");
    const nombrePrincipal = document.getElementById("mainImageName");
    const lista = document.getElementById("otherImagesList");

    if (!estadoVacio || !preview || !imagenPrincipal || !nombrePrincipal || !lista) {
        return;
    }

    if (!archivos.length) {
        estadoVacio.classList.remove("d-none");
        preview.classList.add("d-none");
        imagenPrincipal.removeAttribute("src");
        nombrePrincipal.textContent = "";
        lista.innerHTML = "";
        return;
    }

    const [principal, ...otras] = archivos;
    estadoVacio.classList.add("d-none");
    preview.classList.remove("d-none");
    imagenPrincipal.src = URL.createObjectURL(principal);
    nombrePrincipal.textContent = principal.name;
    lista.innerHTML = otras.length
        ? otras.map((archivo, index) => `
            <li>
                <span>${escapeHtml(archivo.name)}</span>
                <button class="btn btn-outline-danger btn-sm" type="button" data-remove-upload="${index + 1}">
                    Eliminar
                </button>
            </li>
        `).join("")
        : `<li>No seleccionaste imagenes secundarias.</li>`;
}

function sincronizarInputImagenes() {
    if (!imagenForm) {
        return;
    }

    const dataTransfer = new DataTransfer();
    archivosImagenSeleccionados.forEach((archivo) => dataTransfer.items.add(archivo));
    imagenForm.elements.file.files = dataTransfer.files;
}

function cargarArchivosSeleccionados() {
    if (!imagenForm) {
        return;
    }

    archivosImagenSeleccionados = [...imagenForm.elements.file.files];
    actualizarPreviewImagenes();
}

function limpiarArchivosSeleccionados() {
    archivosImagenSeleccionados = [];
    sincronizarInputImagenes();
    actualizarPreviewImagenes();
}

function configurarAutocompletesLugares() {
    document.querySelectorAll("input[name='ciudad'], input[name='provincia']").forEach((input) => {
        if (input.dataset.geoapifyReady === "true") {
            return;
        }

        input.dataset.geoapifyReady = "true";
        const form = input.closest("form");
        const ciudadInput = form?.querySelector("input[name='ciudad']");
        const provinciaInput = form?.querySelector("input[name='provincia']");
        const wrapper = input.parentElement;
        wrapper?.classList.add("autocomplete-wrap");

        const lista = document.createElement("div");
        lista.className = "autocomplete-list d-none";
        wrapper?.appendChild(lista);

        const buscar = debounce(async () => {
            const texto = input.value.trim();
            if (texto.length < 2) {
                ocultarSugerencias(lista);
                return;
            }

            try {
                const lugares = await obtenerJson(`/lugares/autocomplete?texto=${encodeURIComponent(texto)}`);
                renderSugerenciasLugar(lista, input, ciudadInput, provinciaInput, lugares);
            } catch (error) {
                ocultarSugerencias(lista);
            }
        });

        input.addEventListener("input", buscar);
        input.addEventListener("blur", () => {
            setTimeout(() => ocultarSugerencias(lista), 160);
        });
    });
}

function renderSugerenciasLugar(lista, input, ciudadInput, provinciaInput, lugares) {
    if (!lugares.length) {
        ocultarSugerencias(lista);
        return;
    }

    const esProvincia = input.name === "provincia";
    lista.innerHTML = lugares.map((lugar) => `
        <button type="button">
            <strong>${escapeHtml(esProvincia ? (lugar.provincia || lugar.ciudad) : lugar.ciudad)}</strong>
            <span>${escapeHtml(lugar.textoCompleto || lugar.pais || lugar.provincia || "")}</span>
        </button>
    `).join("");
    lista.classList.remove("d-none");

    lista.querySelectorAll("button").forEach((button, index) => {
        button.addEventListener("click", () => {
            const lugar = lugares[index];
            if (esProvincia) {
                input.value = lugar.provincia || lugar.ciudad || "";
                if (ciudadInput && ciudadInput !== input && !ciudadInput.value && lugar.ciudad) {
                    ciudadInput.value = lugar.ciudad;
                }
            } else {
                input.value = lugar.ciudad || "";
            }
            if (provinciaInput && provinciaInput !== input) {
                provinciaInput.value = lugar.provincia || "";
            }
            ocultarSugerencias(lista);
        });
    });
}

function ocultarSugerencias(lista) {
    lista.classList.add("d-none");
    lista.innerHTML = "";
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;")
        .replaceAll("'", "&#039;");
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
            const idAuto = autoForm.elements.idAuto?.value || "";
            const archivos = imagenForm ? archivosImagenSeleccionados : [];
            if (!idAuto && imagenForm && !archivos.length) {
                throw new Error("Para publicar un auto tenes que seleccionar al menos una imagen.");
            }

            const url = idAuto ? `/autos/${idAuto}/me` : "/autos/me";
            const method = idAuto ? "PUT" : "POST";
            const auto = await enviarFormulario(url, crearBody(autoForm), method);
            if (imagenForm) {
                imagenForm.elements.idAuto.value = auto.idAuto;
                if (archivos.length) {
                    await subirImagenesAuto(auto.idAuto, archivos, !idAuto || imagenForm.elements.principal.checked);
                }
            }
            mostrarMensaje("success", idAuto
                ? `Auto modificado correctamente. ID: ${auto.idAuto}`
                : `Auto publicado correctamente. ID: ${auto.idAuto}`);
            autoForm.reset();
            if (imagenForm) {
                imagenForm.reset();
                imagenForm.elements.idAuto.value = auto.idAuto;
                limpiarArchivosSeleccionados();
            }
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });
}

if (imagenForm) {
    imagenForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const idAuto = imagenForm.elements.idAuto.value;
        const archivos = archivosImagenSeleccionados;
        if (!idAuto) {
            mostrarMensaje("warning", "Primero tenes que publicar o indicar un auto.");
            return;
        }
        if (!archivos.length) {
            mostrarMensaje("warning", "Selecciona al menos una imagen.");
            return;
        }

        try {
            await subirImagenesAuto(idAuto, archivos, imagenForm.elements.principal.checked);
            mostrarMensaje("success", "Imagenes subidas correctamente.");
            imagenForm.reset();
            limpiarArchivosSeleccionados();
        } catch (error) {
            mostrarMensaje("danger", error.message);
        }
    });

    imagenForm.elements.file?.addEventListener("change", cargarArchivosSeleccionados);
    imagenForm.addEventListener("reset", () => {
        setTimeout(limpiarArchivosSeleccionados, 0);
    });
    imagenForm.addEventListener("click", (event) => {
        const boton = event.target.closest("[data-remove-upload]");
        if (!boton) {
            return;
        }

        archivosImagenSeleccionados.splice(Number(boton.dataset.removeUpload), 1);
        sincronizarInputImagenes();
        actualizarPreviewImagenes();
    });
}

document.getElementById("volverPanel")?.setAttribute("href", obtenerPanel());
configurarAutocompletesLugares();
