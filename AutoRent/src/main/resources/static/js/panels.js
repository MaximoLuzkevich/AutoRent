const API_BASE = "/api";
const token = localStorage.getItem("autorent_token");
const usuario = JSON.parse(localStorage.getItem("autorent_usuario") || "null");

const mensaje = document.getElementById("mensaje");
const usuarioResumen = document.getElementById("usuarioResumen");
const buscarAutosForm = document.getElementById("buscarAutosForm");
const autosResultado = document.getElementById("autosResultado");
const autosCantidad = document.getElementById("autosCantidad");
const reservasResultado = document.getElementById("reservasResultado");
const pagosResultado = document.getElementById("pagosResultado");
const pagosPropietarioResultado = document.getElementById("pagosPropietarioResultado");
const misAutosResultado = document.getElementById("misAutosResultado");
const reservasPendientesResultado = document.getElementById("reservasPendientesResultado");
const autoDetalleResultado = document.getElementById("autoDetalleResultado");
const reviewsResultado = document.getElementById("reviewsResultado");
const imagenesResultado = document.getElementById("imagenesResultado");
const reservaDetalleResultado = document.getElementById("reservaDetalleResultado");
const perfilResultado = document.getElementById("perfilResultado");
const propietariosResultado = document.getElementById("propietariosResultado");
const pagosAdminResultado = document.getElementById("pagosAdminResultado");
const pagoForm = document.getElementById("pagoForm");
const reviewForm = document.getElementById("reviewForm");
const perfilForm = document.getElementById("perfilForm");
const filtroReservasForm = document.getElementById("filtroReservasForm");
const filtroMisAutosForm = document.getElementById("filtroMisAutosForm");
const historialAutoForm = document.getElementById("historialAutoForm");
const adminPropietarioForm = document.getElementById("adminPropietarioForm");
const filtroPropietariosForm = document.getElementById("filtroPropietariosForm");
const filtroPagosAdminForm = document.getElementById("filtroPagosAdminForm");

let ultimaBusqueda = null;
let accionPropietarioAdmin = "crear";

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

function headersJson() {
    return {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
    };
}

async function api(url, options = {}) {
    const response = await fetch(`${API_BASE}${url}`, {
        ...options,
        headers: {
            Authorization: `Bearer ${token}`,
            ...(options.headers || {})
        }
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo completar la accion");
    }

    return data;
}

function formatMoney(valor) {
    return new Intl.NumberFormat("es-AR", {
        style: "currency",
        currency: "ARS"
    }).format(valor || 0);
}

function escapeHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function renderVacio(contenedor, texto) {
    contenedor.innerHTML = `<p class="text-muted mb-0">${texto}</p>`;
}

function renderAutos(autos) {
    if (autosCantidad) {
        autosCantidad.textContent = `${autos.length} resultado(s)`;
    }

    if (autos.length === 0) {
        renderVacio(autosResultado, "No se encontraron autos para esa ciudad y fechas.");
        return;
    }

    autosResultado.innerHTML = autos.map((auto) => `
        <div class="col-md-6 col-xl-4">
            <article class="item-card">
                <div class="auto-thumb" data-auto-thumb="${auto.idAuto}">
                    <span>Sin imagen</span>
                </div>
                <div class="d-flex justify-content-between gap-2">
                    <h3>${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}</h3>
                    <span class="badge text-bg-light">${escapeHtml(auto.categoria)}</span>
                </div>
                <p class="text-muted mb-2">${escapeHtml(auto.ciudad)} ${auto.provincia ? "- " + escapeHtml(auto.provincia) : ""}</p>
                <dl class="item-facts">
                    <div><dt>Precio</dt><dd>${formatMoney(auto.precioDia)} / dia</dd></div>
                    <div><dt>Pasajeros</dt><dd>${auto.capacidadPasajeros}</dd></div>
                    <div><dt>Transmision</dt><dd>${escapeHtml(auto.transmision)}</dd></div>
                </dl>
                <div class="d-grid gap-2">
                    <button class="btn btn-primary btn-sm" data-reservar-auto="${auto.idAuto}">Reservar</button>
                    <button class="btn btn-outline-secondary btn-sm" data-ver-auto="${auto.idAuto}">Ver ficha</button>
                    <button class="btn btn-outline-secondary btn-sm" data-ver-reviews="${auto.idAuto}">Ver reviews</button>
                    <button class="btn btn-outline-secondary btn-sm" data-ver-imagenes="${auto.idAuto}">Ver imagenes</button>
                </div>
            </article>
        </div>
    `).join("");

    cargarMiniaturas(autos);
}

function renderAutoDetalle(auto) {
    if (!autoDetalleResultado) {
        return;
    }

    autoDetalleResultado.innerHTML = `
        <article class="list-item">
            <strong>${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)} ${auto.anio || ""}</strong>
            <span>${escapeHtml(auto.ciudad)}, ${escapeHtml(auto.provincia)} - ${escapeHtml(auto.categoria)}</span>
            <small>Patente: ${escapeHtml(auto.patente)} - Color: ${escapeHtml(auto.color)} - Puertas: ${auto.cantidadPuertas}</small>
            <small>Combustible: ${escapeHtml(auto.combustible)} - Transmision: ${escapeHtml(auto.transmision)}</small>
            <small>Retiro: ${escapeHtml(auto.direccionRetiro)} - Propietario: ${escapeHtml(auto.nombrePropietario)}</small>
            <p class="mb-0">${escapeHtml(auto.descripcion || "Sin descripcion.")}</p>
        </article>
    `;
}

function renderReviews(reviews) {
    if (!reviewsResultado) {
        return;
    }

    if (!reviews || reviews.length === 0) {
        renderVacio(reviewsResultado, "Este auto todavia no tiene reviews.");
        return;
    }

    reviewsResultado.innerHTML = reviews.map((review) => `
        <article class="list-item">
            <strong>Puntaje ${review.puntuacion}/5</strong>
            <span>${escapeHtml(review.nombreCliente || "Cliente")}</span>
            <small>${escapeHtml(review.comentario || "Sin comentario.")}</small>
        </article>
    `).join("");
}

function renderImagenes(imagenes, permiteEliminar = false) {
    if (!imagenesResultado) {
        return;
    }

    if (!imagenes || imagenes.length === 0) {
        renderVacio(imagenesResultado, "Este auto todavia no tiene imagenes.");
        return;
    }

    imagenesResultado.innerHTML = imagenes.map((imagen) => `
        <article class="image-item">
            <img src="${escapeHtml(imagen.urlImagen)}" alt="${escapeHtml(imagen.nombreArchivo)}">
            <div>
                <strong>${escapeHtml(imagen.nombreArchivo)}</strong>
                <small>${imagen.principal ? "Principal" : "Secundaria"} - Auto #${imagen.idAuto}</small>
                ${permiteEliminar ? `<button class="btn btn-outline-danger btn-sm mt-2" data-eliminar-imagen="${imagen.idImagen}" data-id-auto="${imagen.idAuto}">Eliminar</button>` : ""}
            </div>
        </article>
    `).join("");
}

async function cargarMiniaturas(autos) {
    for (const auto of autos) {
        const contenedor = document.querySelector(`[data-auto-thumb="${auto.idAuto}"]`);
        if (!contenedor) {
            continue;
        }

        try {
            const imagen = await api(`/autos/${auto.idAuto}/imagenes/principal`);
            contenedor.innerHTML = `<img src="${escapeHtml(imagen.urlImagen)}" alt="${escapeHtml(imagen.nombreArchivo)}">`;
        } catch (error) {
            contenedor.innerHTML = "<span>Sin imagen</span>";
        }
    }
}

function renderReservaDetalle(reserva) {
    if (!reservaDetalleResultado) {
        return;
    }

    reservaDetalleResultado.innerHTML = `
        <article class="list-item">
            <strong>Reserva #${reserva.idReserva}</strong>
            <span>${escapeHtml(reserva.auto)} - ${escapeHtml(reserva.estado)}</span>
            <small>${reserva.fechaInicio} a ${reserva.fechaFin} - ${formatMoney(reserva.precioTotal)}</small>
            <small>Cliente: ${escapeHtml(reserva.nombreCliente || "Cliente")} - Auto #${reserva.idAuto}</small>
        </article>
    `;
}

function renderLista(contenedor, items, tipo) {
    if (!contenedor) {
        return;
    }

    if (!items || items.length === 0) {
        renderVacio(contenedor, "No hay datos para mostrar.");
        return;
    }

    contenedor.innerHTML = items.map((item) => {
        if (tipo === "reserva") {
            return `
                <article class="list-item">
                    <strong>Reserva #${item.idReserva}</strong>
                    <span>${escapeHtml(item.auto)} - ${escapeHtml(item.estado)}</span>
                    <small>${item.fechaInicio} a ${item.fechaFin} - ${formatMoney(item.precioTotal)}</small>
                    <div class="d-flex gap-2 flex-wrap mt-2">
                        <button class="btn btn-outline-secondary btn-sm" data-ver-reserva="${item.idReserva}">Ver ficha</button>
                        <button class="btn btn-outline-primary btn-sm" data-preparar-pago="${item.idReserva}" data-monto="${item.precioTotal}">Pagar</button>
                        <button class="btn btn-outline-success btn-sm" data-preparar-review="${item.idAuto}">Agregar review</button>
                    </div>
                </article>
            `;
        }

        if (tipo === "pago") {
            return `
                <article class="list-item">
                    <strong>Pago #${item.idPago}</strong>
                    <span>${escapeHtml(item.metodoPago)} - ${escapeHtml(item.estado)}</span>
                    <small>Reserva #${item.idReserva} - ${formatMoney(item.monto)}</small>
                </article>
            `;
        }

        if (tipo === "pagoAdmin") {
            return `
                <article class="list-item">
                    <div class="d-flex justify-content-between gap-2 flex-wrap">
                        <strong>Pago #${item.idPago}</strong>
                        <span>${escapeHtml(item.metodoPago)} - ${escapeHtml(item.estado)}</span>
                    </div>
                    <small>Reserva #${item.idReserva} - ${formatMoney(item.monto)}</small>
                    <div class="d-flex gap-2 flex-wrap mt-2">
                        <button class="btn btn-outline-success btn-sm" data-aprobar-pago="${item.idPago}">Aprobar</button>
                        <button class="btn btn-outline-danger btn-sm" data-rechazar-pago="${item.idPago}">Rechazar</button>
                    </div>
                </article>
            `;
        }

        if (tipo === "propietario") {
            return `
                <article class="list-item">
                    <div class="d-flex justify-content-between gap-2 flex-wrap">
                        <strong>${escapeHtml(item.nombreUsuario)}</strong>
                        <span>${item.activo ? "Activo" : "Inactivo"} - ${item.verificado ? "Verificado" : "Sin verificar"}</span>
                    </div>
                    <span>${escapeHtml(item.emailUsuario)} - ${escapeHtml(item.ciudad)}, ${escapeHtml(item.provincia)}</span>
                    <small>ID usuario: ${item.idUsuario} - DNI: ${escapeHtml(item.dni)} - CUIT: ${escapeHtml(item.cuit)}</small>
                    <div class="d-flex gap-2 flex-wrap mt-2">
                        <button class="btn btn-outline-success btn-sm" data-verificar-propietario="${item.idUsuario}">Verificar</button>
                        <button class="btn btn-outline-danger btn-sm" data-desactivar-propietario="${item.idUsuario}">Dar de baja</button>
                    </div>
                </article>
            `;
        }

        if (tipo === "auto") {
            return `
                <article class="list-item">
                    <strong>${escapeHtml(item.marca)} ${escapeHtml(item.modelo)}</strong>
                    <span>${escapeHtml(item.ciudad)} - ${escapeHtml(item.categoria)}</span>
                    <small>${item.activo ? "Activo" : "Inactivo"} - ${formatMoney(item.precioDia)} por dia</small>
                    <div class="d-flex gap-2 flex-wrap mt-2">
                        <button class="btn btn-outline-secondary btn-sm" data-ver-auto="${item.idAuto}">Ver ficha</button>
                        <button class="btn btn-outline-secondary btn-sm" data-ver-reviews="${item.idAuto}">Ver reviews</button>
                        <button class="btn btn-outline-secondary btn-sm" data-ver-imagenes-admin="${item.idAuto}">Imagenes</button>
                        <button class="btn btn-outline-danger btn-sm" data-baja-auto="${item.idAuto}">Dar de baja</button>
                    </div>
                </article>
            `;
        }

        return `
            <article class="list-item">
                <strong>Reserva #${item.idReserva}</strong>
                <span>${escapeHtml(item.nombreCliente || "Cliente")} - ${escapeHtml(item.estado)}</span>
                <small>${item.fechaInicio} a ${item.fechaFin} - ${escapeHtml(item.auto || "Auto #" + item.idAuto)}</small>
            </article>
        `;
    }).join("");
}

async function buscarAutos(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(buscarAutosForm);
    ultimaBusqueda = {
        ciudad: formData.get("ciudad"),
        fechaInicio: formData.get("fechaInicio"),
        fechaFin: formData.get("fechaFin")
    };

    const params = new URLSearchParams(ultimaBusqueda);
    ["marca", "categoria", "precioMax", "pasajeros", "transmision", "combustible"].forEach((campo) => {
        const valor = formData.get(campo);
        if (valor) {
            params.set(campo, valor);
        }
    });

    try {
        const autos = await api(`/autos/disponibles?${params.toString()}`);
        renderAutos(autos);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function verAuto(idAuto) {
    try {
        const auto = await api(`/autos/${idAuto}`);
        renderAutoDetalle(auto);
        await verImagenes(idAuto);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function verReviews(idAuto) {
    try {
        const reviews = await api(`/reviews/auto/${idAuto}`);
        renderReviews(reviews);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function verImagenes(idAuto, permiteEliminar = false) {
    try {
        const imagenes = await api(`/autos/${idAuto}/imagenes`);
        renderImagenes(imagenes, permiteEliminar);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function eliminarImagen(idAuto, idImagen) {
    try {
        await api(`/autos/${idAuto}/imagenes/${idImagen}`, { method: "DELETE" });
        mostrarMensaje("success", "Imagen eliminada correctamente.");
        await verImagenes(idAuto, true);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function verReserva(idReserva) {
    try {
        const reserva = await api(`/reservas/${idReserva}`);
        renderReservaDetalle(reserva);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function reservarAuto(idAuto) {
    if (!ultimaBusqueda) {
        mostrarMensaje("warning", "Primero busca ciudad y fechas.");
        return;
    }

    try {
        const reserva = await api("/reservas/me", {
            method: "POST",
            headers: headersJson(),
            body: JSON.stringify({
                idAuto,
                fechaInicio: ultimaBusqueda.fechaInicio,
                fechaFin: ultimaBusqueda.fechaFin
            })
        });
        mostrarMensaje("success", `Reserva creada correctamente. ID: ${reserva.idReserva}`);
        await cargarReservas();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarReservas() {
    try {
        renderLista(reservasResultado, await api("/reservas/me"), "reserva");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function filtrarReservas(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(filtroReservasForm);
    const estado = formData.get("estado");
    const desde = formData.get("desde");
    const hasta = formData.get("hasta");

    let ruta = "/reservas/me";
    if (estado) {
        ruta = `/reservas/me/estado/${estado}`;
    } else if (desde && hasta) {
        ruta = `/reservas/me/fechas/${desde}/${hasta}`;
    } else if (desde || hasta) {
        mostrarMensaje("warning", "Para filtrar por fechas completa desde y hasta.");
        return;
    }

    try {
        renderLista(reservasResultado, await api(ruta), "reserva");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function registrarPago(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(pagoForm);
    const body = {
        idReserva: Number(formData.get("idReserva")),
        monto: Number(formData.get("monto")),
        metodoPago: formData.get("metodoPago"),
        titularTarjeta: formData.get("titularTarjeta") || null,
        numeroTarjeta: formData.get("numeroTarjeta") || null,
        vencimientoTarjeta: formData.get("vencimientoTarjeta") || null,
        codigoSeguridad: formData.get("codigoSeguridad") || null
    };

    try {
        const pago = await api("/pagos", {
            method: "POST",
            headers: headersJson(),
            body: JSON.stringify(body)
        });
        mostrarMensaje("success", pago.linkPago
            ? `Pago creado. Link de Mercado Pago: ${pago.linkPago}`
            : "Pago registrado correctamente.");
        pagoForm.reset();
        await cargarPagos();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function crearReview(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(reviewForm);
    const body = {
        idAuto: Number(formData.get("idAuto")),
        puntuacion: Number(formData.get("puntuacion")),
        comentario: formData.get("comentario")
    };

    try {
        await api("/reviews/me", {
            method: "POST",
            headers: headersJson(),
            body: JSON.stringify(body)
        });
        mostrarMensaje("success", "Review agregada correctamente.");
        reviewForm.reset();
        await verReviews(body.idAuto);
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarPerfil() {
    try {
        const perfil = await api("/usuarios/me");
        if (perfilResultado) {
            perfilResultado.innerHTML = `
                <article class="list-item">
                    <strong>${escapeHtml(perfil.nombre)}</strong>
                    <span>${escapeHtml(perfil.email)} - ${escapeHtml(perfil.telefono || "Sin telefono")}</span>
                    <small>${perfil.activo ? "Activo" : "Inactivo"} - Roles: ${(perfil.roles || []).join(", ")}</small>
                </article>
            `;
        }
        if (perfilForm) {
            perfilForm.elements.nombre.value = perfil.nombre || "";
            perfilForm.elements.email.value = perfil.email || "";
            perfilForm.elements.telefono.value = perfil.telefono || "";
        }
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function actualizarPerfil(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(perfilForm);
    const body = {
        nombre: formData.get("nombre"),
        email: formData.get("email"),
        telefono: formData.get("telefono")
    };

    try {
        const perfil = await api("/usuarios/me", {
            method: "PUT",
            headers: headersJson(),
            body: JSON.stringify(body)
        });
        localStorage.setItem("autorent_usuario", JSON.stringify(perfil));
        mostrarMensaje("success", "Perfil actualizado correctamente.");
        await cargarPerfil();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function darDeBajaPerfil() {
    try {
        await api("/usuarios/me", { method: "DELETE" });
        cerrarSesion();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarPagos() {
    try {
        renderLista(pagosResultado, await api("/pagos/me"), "pago");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarPagosPropietario() {
    try {
        renderLista(pagosPropietarioResultado, await api("/pagos/me/propietario"), "pago");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarMisAutos() {
    try {
        renderLista(misAutosResultado, await api("/autos/me"), "auto");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function filtrarMisAutos(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(filtroMisAutosForm);
    const estado = formData.get("estado");
    const categoria = formData.get("categoria");

    let ruta = "/autos/me";
    if (estado) {
        ruta = `/autos/me/estado/${estado}`;
    } else if (categoria) {
        ruta = `/autos/me/categoria/${categoria}`;
    }

    try {
        renderLista(misAutosResultado, await api(ruta), "auto");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function darDeBajaAuto(idAuto) {
    try {
        await api(`/autos/${idAuto}/me`, { method: "DELETE" });
        mostrarMensaje("success", "Auto dado de baja correctamente.");
        await cargarMisAutos();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarHistorialAuto(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(historialAutoForm);
    const idAuto = Number(formData.get("idAuto"));

    try {
        renderLista(reservasPendientesResultado, await api(`/reservas/propietario/${usuario.idUsuario}/auto/${idAuto}`), "pendiente");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarReservasPendientes() {
    try {
        renderLista(reservasPendientesResultado, await api(`/reservas/propietario/${usuario.idUsuario}`), "pendiente");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarPropietarios() {
    try {
        renderLista(propietariosResultado, await api("/propietarios/activos/true"), "propietario");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function filtrarPropietarios(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(filtroPropietariosForm);
    const tipo = formData.get("tipo");
    const valor = String(formData.get("valor") || "").trim();

    const rutas = {
        activos: "/propietarios/activos/true",
        inactivos: "/propietarios/activos/false",
        verificados: "/propietarios/verificados/true",
        sinVerificar: "/propietarios/verificados/false",
        ciudad: `/propietarios/ciudad/${encodeURIComponent(valor)}`,
        provincia: `/propietarios/provincia/${encodeURIComponent(valor)}`,
        nombre: `/propietarios/nombre/${encodeURIComponent(valor)}`
    };

    if (["ciudad", "provincia", "nombre"].includes(tipo) && !valor) {
        mostrarMensaje("warning", "Completa el valor del filtro.");
        return;
    }

    try {
        renderLista(propietariosResultado, await api(rutas[tipo]), "propietario");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

function obtenerDatosPropietarioAdmin() {
    const formData = new FormData(adminPropietarioForm);
    return {
        idUsuario: Number(formData.get("idUsuario")),
        body: {
            dni: formData.get("dni"),
            cuit: formData.get("cuit"),
            direccion: formData.get("direccion"),
            ciudad: formData.get("ciudad"),
            provincia: formData.get("provincia")
        }
    };
}

async function guardarPropietarioAdmin(event) {
    event.preventDefault();
    limpiarMensaje();

    const { idUsuario, body } = obtenerDatosPropietarioAdmin();
    const metodo = accionPropietarioAdmin === "modificar" ? "PUT" : "POST";

    try {
        await api(`/propietarios/${idUsuario}`, {
            method: metodo,
            headers: headersJson(),
            body: JSON.stringify(body)
        });
        mostrarMensaje("success", accionPropietarioAdmin === "modificar"
            ? "Propietario modificado correctamente."
            : "Propietario dado de alta correctamente.");
        adminPropietarioForm.reset();
        await cargarPropietarios();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function verificarPropietario(idUsuario) {
    try {
        await api(`/propietarios/${idUsuario}/verificar`, { method: "PUT" });
        mostrarMensaje("success", "Propietario verificado correctamente.");
        await cargarPropietarios();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function desactivarPropietario(idUsuario) {
    try {
        await api(`/propietarios/${idUsuario}`, { method: "DELETE" });
        mostrarMensaje("success", "Propietario dado de baja correctamente.");
        await cargarPropietarios();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cargarPagosAdmin() {
    try {
        renderLista(pagosAdminResultado, await api("/pagos"), "pagoAdmin");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function filtrarPagosAdmin(event) {
    event.preventDefault();
    limpiarMensaje();

    const formData = new FormData(filtroPagosAdminForm);
    const estado = formData.get("estado");
    const desde = formData.get("desde");
    const hasta = formData.get("hasta");

    let ruta = "/pagos";
    if (estado) {
        ruta = `/pagos/estado/${estado}`;
    } else if (desde && hasta) {
        ruta = `/pagos/fechas/${desde}/${hasta}`;
    } else if (desde || hasta) {
        mostrarMensaje("warning", "Para filtrar por fechas completa desde y hasta.");
        return;
    }

    try {
        renderLista(pagosAdminResultado, await api(ruta), "pagoAdmin");
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

async function cambiarEstadoPago(idPago, accion) {
    try {
        await api(`/pagos/${idPago}/${accion}`, { method: "PUT" });
        mostrarMensaje("success", accion === "aprobar" ? "Pago aprobado correctamente." : "Pago rechazado correctamente.");
        await cargarPagosAdmin();
    } catch (error) {
        mostrarMensaje("danger", error.message);
    }
}

function cerrarSesion() {
    localStorage.removeItem("autorent_token");
    localStorage.removeItem("autorent_usuario");
    window.location.href = "login.html";
}

usuarioResumen.textContent = `${usuario.nombre || usuario.email} (${(usuario.roles || []).join(", ")})`;

if ((usuario.roles || []).includes("PROPIETARIO")) {
    document.getElementById("bloquePropietarioAdmin")?.classList.remove("d-none");
    document.getElementById("bloquePagosPropietarioAdmin")?.classList.remove("d-none");
    document.getElementById("botonPublicarAuto")?.classList.remove("d-none");
    document.getElementById("botonSerPropietario")?.classList.add("d-none");
}

buscarAutosForm?.addEventListener("submit", buscarAutos);
autosResultado?.addEventListener("click", (event) => {
    const boton = event.target.closest("[data-reservar-auto]");
    const verFicha = event.target.closest("[data-ver-auto]");
    const verReview = event.target.closest("[data-ver-reviews]");
    const verImagen = event.target.closest("[data-ver-imagenes]");

    if (boton) {
        reservarAuto(Number(boton.dataset.reservarAuto));
    }

    if (verFicha) {
        verAuto(Number(verFicha.dataset.verAuto));
    }

    if (verReview) {
        verReviews(Number(verReview.dataset.verReviews));
    }

    if (verImagen) {
        verImagenes(Number(verImagen.dataset.verImagenes));
    }
});

document.getElementById("cargarReservas")?.addEventListener("click", cargarReservas);
document.getElementById("cargarPagos")?.addEventListener("click", cargarPagos);
document.getElementById("cargarPagosPropietario")?.addEventListener("click", cargarPagosPropietario);
document.getElementById("cargarPerfil")?.addEventListener("click", cargarPerfil);
document.getElementById("darBajaPerfil")?.addEventListener("click", darDeBajaPerfil);
document.getElementById("cargarMisAutos")?.addEventListener("click", cargarMisAutos);
document.getElementById("cargarReservasPendientes")?.addEventListener("click", cargarReservasPendientes);
document.getElementById("cargarPropietarios")?.addEventListener("click", cargarPropietarios);
document.getElementById("cargarPagosAdmin")?.addEventListener("click", cargarPagosAdmin);
document.getElementById("cerrarSesion")?.addEventListener("click", cerrarSesion);

filtroReservasForm?.addEventListener("submit", filtrarReservas);
filtroMisAutosForm?.addEventListener("submit", filtrarMisAutos);
historialAutoForm?.addEventListener("submit", cargarHistorialAuto);
pagoForm?.addEventListener("submit", registrarPago);
reviewForm?.addEventListener("submit", crearReview);
perfilForm?.addEventListener("submit", actualizarPerfil);
filtroPropietariosForm?.addEventListener("submit", filtrarPropietarios);
filtroPagosAdminForm?.addEventListener("submit", filtrarPagosAdmin);
adminPropietarioForm?.addEventListener("submit", guardarPropietarioAdmin);
adminPropietarioForm?.addEventListener("click", (event) => {
    const boton = event.target.closest("[data-admin-propietario-accion]");
    if (boton) {
        accionPropietarioAdmin = boton.dataset.adminPropietarioAccion;
    }
});

misAutosResultado?.addEventListener("click", (event) => {
    const verFicha = event.target.closest("[data-ver-auto]");
    const verReview = event.target.closest("[data-ver-reviews]");
    const verImagen = event.target.closest("[data-ver-imagenes-admin]");
    const baja = event.target.closest("[data-baja-auto]");

    if (verFicha) {
        verAuto(Number(verFicha.dataset.verAuto));
    }

    if (verReview) {
        verReviews(Number(verReview.dataset.verReviews));
    }

    if (verImagen) {
        verImagenes(Number(verImagen.dataset.verImagenesAdmin), true);
    }

    if (baja) {
        darDeBajaAuto(Number(baja.dataset.bajaAuto));
    }
});

imagenesResultado?.addEventListener("click", (event) => {
    const boton = event.target.closest("[data-eliminar-imagen]");
    if (boton) {
        eliminarImagen(Number(boton.dataset.idAuto), Number(boton.dataset.eliminarImagen));
    }
});

reservasResultado?.addEventListener("click", (event) => {
    const ver = event.target.closest("[data-ver-reserva]");
    const pago = event.target.closest("[data-preparar-pago]");
    const review = event.target.closest("[data-preparar-review]");

    if (ver) {
        verReserva(Number(ver.dataset.verReserva));
    }

    if (pago && pagoForm) {
        pagoForm.elements.idReserva.value = pago.dataset.prepararPago;
        pagoForm.elements.monto.value = pago.dataset.monto;
        pagoForm.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    if (review && reviewForm) {
        reviewForm.elements.idAuto.value = review.dataset.prepararReview;
        reviewForm.scrollIntoView({ behavior: "smooth", block: "center" });
    }
});

propietariosResultado?.addEventListener("click", (event) => {
    const verificar = event.target.closest("[data-verificar-propietario]");
    const desactivar = event.target.closest("[data-desactivar-propietario]");

    if (verificar) {
        verificarPropietario(Number(verificar.dataset.verificarPropietario));
    }

    if (desactivar) {
        desactivarPropietario(Number(desactivar.dataset.desactivarPropietario));
    }
});

pagosAdminResultado?.addEventListener("click", (event) => {
    const aprobar = event.target.closest("[data-aprobar-pago]");
    const rechazar = event.target.closest("[data-rechazar-pago]");

    if (aprobar) {
        cambiarEstadoPago(Number(aprobar.dataset.aprobarPago), "aprobar");
    }

    if (rechazar) {
        cambiarEstadoPago(Number(rechazar.dataset.rechazarPago), "rechazar");
    }
});
