(() => {
    const API_BASE = "/api";
    const token = localStorage.getItem("autorent_token");
    const usuario = JSON.parse(localStorage.getItem("autorent_usuario") || "null");
    const view = document.body.dataset.view;
    const categorias = ["ECONOMICO", "PREMIUM", "SUV", "ELECTRICO", "UTILITARIO"];
    const fallbackImages = {
        ECONOMICO: "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&w=900&q=80",
        PREMIUM: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=900&q=80",
        SUV: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=900&q=80",
        ELECTRICO: "https://images.unsplash.com/photo-1619767886558-efdc259cde1a?auto=format&fit=crop&w=900&q=80",
        UTILITARIO: "https://images.unsplash.com/photo-1605893477799-b99e3b8b93fe?auto=format&fit=crop&w=900&q=80"
    };

    if (!token || !usuario) {
        window.location.href = "login.html";
        return;
    }

    const $ = (selector) => document.querySelector(selector);
    const $$ = (selector) => [...document.querySelectorAll(selector)];
    const roles = usuario.roles || [];
    const esPropietario = roles.includes("PROPIETARIO");
    const esAdmin = roles.includes("ADMINISTRADOR");

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#039;");
    }

    function formatMoney(value) {
        return new Intl.NumberFormat("es-AR", {
            style: "currency",
            currency: "ARS",
            minimumFractionDigits: 2
        }).format(Number(value || 0));
    }

    function pretty(value) {
        return escapeHtml(String(value ?? "").replaceAll("_", " ").toLowerCase()
            .replace(/\b\w/g, (letter) => letter.toUpperCase()));
    }

    function mensaje(texto, tipo = "info") {
        const box = $("#mensaje");
        if (!box) {
            return;
        }
        box.className = `alert alert-${tipo}`;
        box.textContent = texto;
    }

    function getParam(nombre) {
        return new URLSearchParams(window.location.search).get(nombre);
    }

    async function api(path, options = {}) {
        const headers = {
            Accept: "application/json",
            Authorization: `Bearer ${token}`,
            ...(options.body ? { "Content-Type": "application/json" } : {})
        };

        const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
        const text = await response.text();
        let data = null;

        if (text) {
            try {
                data = JSON.parse(text);
            } catch (error) {
                throw new Error("La respuesta del servidor no fue JSON. Verifica que Spring Boot este levantado.");
            }
        }

        if (!response.ok) {
            throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo completar la operacion");
        }

        return data;
    }

    async function apiMultipart(path, formData, method = "POST") {
        const response = await fetch(`${API_BASE}${path}`, {
            method,
            headers: {
                Accept: "application/json",
                Authorization: `Bearer ${token}`
            },
            body: formData
        });

        const text = await response.text();
        let data = null;

        if (text) {
            try {
                data = JSON.parse(text);
            } catch (error) {
                throw new Error("La respuesta del servidor no fue JSON.");
            }
        }

        if (!response.ok) {
            throw new Error(data?.mensaje || data?.error || data?.message || "No se pudo completar la operacion");
        }

        return data;
    }

    function cerrarSesion() {
        localStorage.removeItem("autorent_token");
        localStorage.removeItem("autorent_usuario");
        window.location.href = "login.html";
    }

    function navLink(href, label, key) {
        const active = view === key
            || (["auto-alta", "propietario-auto-detail"].includes(view) && key === "propietario-autos");
        return `<a class="${active ? "active" : ""}" href="${href}">${label}</a>`;
    }

    function renderNavbar() {
        const contenedor = $("#appNavbar");
        if (!contenedor) {
            return;
        }

        const iniciales = (usuario.nombre || usuario.email || "AR")
            .split(" ")
            .map((parte) => parte[0])
            .join("")
            .slice(0, 2)
            .toUpperCase();

        contenedor.innerHTML = `
            <nav class="site-navbar app-nav">
                <a class="site-brand" href="cliente-inicio.html">
                    <span class="site-logo-mark">AR</span>
                    <span>Auto<span>Rent</span></span>
                </a>
                <div class="site-nav">
                    ${navLink("cliente-inicio.html", "Inicio", "home")}
                    ${navLink("cliente-reservas.html", "Mis reservas", "reservas")}
                    ${navLink("cliente-pagos.html", "Pagos", "pagos")}
                    ${navLink("cliente-perfil.html", "Perfil", "perfil")}
                    ${!esPropietario ? navLink("propietario.html", "Ser propietario", "ser-propietario") : ""}
                    ${esPropietario ? navLink("propietario-autos.html", "Mis autos", "propietario-autos") : ""}
                    ${esPropietario ? navLink("propietario-solicitudes.html", "Solicitudes", "propietario-solicitudes") : ""}
                    ${esPropietario ? navLink("propietario-pagos.html", "Cobros", "propietario-pagos") : ""}
                    ${esPropietario ? navLink("propietario-perfil.html", "Datos propietario", "propietario-perfil") : ""}
                    ${esAdmin ? navLink("admin-propietarios.html", "Propietarios", "admin-propietarios") : ""}
                    ${esAdmin ? navLink("admin-pagos.html", "Pagos admin", "admin-pagos") : ""}
                </div>
                <div class="site-actions">
                    <span class="nav-user"><strong>${escapeHtml(iniciales)}</strong> ${escapeHtml(usuario.nombre || usuario.email)}</span>
                    <button class="btn btn-outline-light btn-sm" id="logoutButton" type="button">Cerrar sesion</button>
                </div>
            </nav>
        `;

        $("#logoutButton")?.addEventListener("click", cerrarSesion);
    }

    function carImage(auto) {
        return fallbackImages[auto.categoria] || fallbackImages.ECONOMICO;
    }

    function debounce(fn, delay = 350) {
        let timeoutId;
        return (...args) => {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => fn(...args), delay);
        };
    }

    async function cargarImagenPrincipal(auto) {
        const img = document.querySelector(`[data-car-image="${auto.idAuto}"]`);
        if (!img) {
            return;
        }
        try {
            const imagenes = await api(`/autos/${auto.idAuto}/imagenes`);
            const imagen = imagenes.find((item) => item.principal) || imagenes[0];
            if (!imagen) {
                img.src = carImage(auto);
                return;
            }
            img.src = imagen.urlImagen;
            img.alt = imagen.nombreArchivo || `${auto.marca} ${auto.modelo}`;
        } catch (error) {
            img.src = carImage(auto);
        }
    }

    function autoCard(auto, fechas = {}, target = "auto-detalle.html", actionLabel = "Ver ficha") {
        const params = new URLSearchParams({ id: auto.idAuto });
        if (fechas.fechaInicio) params.set("fechaInicio", fechas.fechaInicio);
        if (fechas.fechaFin) params.set("fechaFin", fechas.fechaFin);

        return `
            <article class="car-card">
                <img data-car-image="${auto.idAuto}" src="${carImage(auto)}" alt="${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}">
                <div class="car-card-body">
                    <div>
                        <span class="badge-soft">${pretty(auto.categoria)}</span>
                        <h3>${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}</h3>
                        <p>${escapeHtml(auto.ciudad)}${auto.provincia ? `, ${escapeHtml(auto.provincia)}` : ""}</p>
                    </div>
                    <div class="car-specs">
                        <span>${escapeHtml(auto.capacidadPasajeros)} pasajeros</span>
                        <span>${pretty(auto.transmision)}</span>
                    </div>
                    <div class="car-price-row">
                        <strong>${formatMoney(auto.precioDia)} <small>/ dia</small></strong>
                        <a class="btn btn-primary btn-sm" href="${target}?${params.toString()}">${actionLabel}</a>
                    </div>
                </div>
            </article>
        `;
    }

    function renderAutosAgrupados(autos, contenedor, tituloBase = "Explora por categoria", fechas = {}) {
        contenedor.innerHTML = categorias.map((categoria) => {
            const items = autos.filter((auto) => auto.categoria === categoria).slice(0, 4);
            return `
                <section class="fleet-section">
                    <div class="section-heading">
                        <div>
                            <span>${tituloBase}</span>
                            <h2>${pretty(categoria)}</h2>
                        </div>
                    </div>
                    ${items.length
                        ? `<div class="car-row">${items.map((auto) => autoCard(auto, fechas)).join("")}</div>`
                        : `<p class="empty-line">No hay autos publicados en esta categoria por ahora.</p>`}
                </section>
            `;
        }).join("");

        autos.forEach(cargarImagenPrincipal);
    }

    async function initHome() {
        const contenedor = $("#homeCars");
        const resultados = $("#searchResults");
        const form = $("#searchForm");
        configurarAutocompleteCiudad();

        try {
            const autos = await api("/autos");
            renderAutosAgrupados(autos, contenedor);
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            const params = new URLSearchParams();
            ["ciudad", "fechaInicio", "fechaFin", "categoria"].forEach((campo) => {
                const value = formData.get(campo);
                if (value) params.set(campo, value);
            });

            try {
                const autos = await api(`/autos/disponibles?${params.toString()}`);
                resultados.innerHTML = `
                    <section class="fleet-section featured-results">
                        <div class="section-heading">
                            <div>
                                <span>Resultado de busqueda</span>
                                <h2>Autos disponibles</h2>
                            </div>
                        </div>
                        ${autos.length
                            ? `<div class="car-row">${autos.map((auto) => autoCard(auto, {
                                fechaInicio: formData.get("fechaInicio"),
                                fechaFin: formData.get("fechaFin")
                            })).join("")}</div>`
                            : `<p class="empty-line">No encontramos autos libres para esos datos.</p>`}
                    </section>
                `;
                autos.forEach(cargarImagenPrincipal);
                resultados.scrollIntoView({ behavior: "smooth", block: "start" });
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    function configurarAutocompletesLugares(root = document) {
        root.querySelectorAll("input[name='ciudad'], input[name='provincia']").forEach((input) => {
            const form = input.closest("form");
            const ciudadInput = form?.querySelector("input[name='ciudad']");
            const provinciaInput = form?.querySelector("input[name='provincia']");
            configurarAutocompleteLugar(input, ciudadInput, provinciaInput);
        });
    }

    function configurarAutocompleteCiudad() {
        configurarAutocompletesLugares(document);
    }

    function configurarAutocompleteLugar(input, ciudadInput = null, provinciaInput = null) {
        if (!input || input.dataset.geoapifyReady === "true") {
            return;
        }

        input.dataset.geoapifyReady = "true";
        const wrapper = input.parentElement;
        wrapper?.classList.add("autocomplete-wrap");

        let lista = wrapper?.querySelector(".autocomplete-list");
        if (!lista) {
            lista = document.createElement("div");
            lista.className = "autocomplete-list d-none";
            wrapper?.appendChild(lista);
        }

        const buscar = debounce(async () => {
            const texto = input.value.trim();
            if (texto.length < 2) {
                ocultarSugerencias(lista);
                return;
            }

            try {
                const lugares = await api(`/lugares/autocomplete?texto=${encodeURIComponent(texto)}`);
                renderSugerenciasLugar(lista, input, ciudadInput, provinciaInput, lugares);
            } catch (error) {
                ocultarSugerencias(lista);
            }
        });

        input.addEventListener("input", buscar);
        input.addEventListener("blur", () => {
            setTimeout(() => ocultarSugerencias(lista), 160);
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

    async function initAutoDetalle() {
        const id = getParam("id");
        const detalle = $("#autoDetail");
        const reviewsBox = $("#autoReviews");
        const reservaForm = $("#reservaForm");

        if (!id) {
            detalle.innerHTML = `<p class="empty-line">No se indico el auto.</p>`;
            return;
        }

        $("#fechaInicio") && ($("#fechaInicio").value = getParam("fechaInicio") || "");
        $("#fechaFin") && ($("#fechaFin").value = getParam("fechaFin") || "");

        try {
            const [auto, imagenes, reviews] = await Promise.all([
                api(`/autos/${id}`),
                api(`/autos/${id}/imagenes`).catch(() => []),
                api(`/reviews/auto/${id}`).catch(() => [])
            ]);
            const principal = imagenes.find((imagen) => imagen.principal) || imagenes[0];
            detalle.innerHTML = `
                <section class="detail-layout">
                    <div>
                        <img class="detail-main-image" src="${principal?.urlImagen || carImage(auto)}" alt="${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}">
                        <div class="detail-thumbs">
                            ${imagenes.map((imagen) => `<img data-detail-thumb src="${escapeHtml(imagen.urlImagen)}" alt="${escapeHtml(imagen.nombreArchivo)}">`).join("")}
                        </div>
                    </div>
                    <div class="detail-info">
                        <span class="badge-soft">${pretty(auto.categoria)}</span>
                        <h1>${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}</h1>
                        <p>${escapeHtml(auto.descripcion || "Auto publicado en AutoRent.")}</p>
                        <dl class="spec-list">
                            <div><dt>Precio por dia</dt><dd>${formatMoney(auto.precioDia)}</dd></div>
                            <div><dt>Pasajeros</dt><dd>${escapeHtml(auto.capacidadPasajeros)}</dd></div>
                            <div><dt>Puertas</dt><dd>${escapeHtml(auto.cantidadPuertas)}</dd></div>
                            <div><dt>Transmision</dt><dd>${pretty(auto.transmision)}</dd></div>
                            <div><dt>Combustible</dt><dd>${pretty(auto.combustible)}</dd></div>
                            <div><dt>Retiro</dt><dd>${escapeHtml(auto.direccionRetiro)}, ${escapeHtml(auto.ciudad)}</dd></div>
                        </dl>
                    </div>
                </section>
            `;

            renderReviews(reviewsBox, reviews);
            configurarGaleriaDetalle(detalle);
        } catch (error) {
            mensaje(error.message, "danger");
        }

        reservaForm?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(reservaForm);
            try {
                const reserva = await api("/reservas/me", {
                    method: "POST",
                    body: JSON.stringify({
                        idAuto: Number(id),
                        fechaInicio: formData.get("fechaInicio"),
                        fechaFin: formData.get("fechaFin")
                    })
                });
                mensaje(`Reserva creada. Queda pendiente hasta que el propietario la apruebe. ID: ${reserva.idReserva}`, "success");
                setTimeout(() => window.location.href = "cliente-reservas.html", 1000);
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initPropietarioAutoDetalle() {
        if (!esPropietario && !esAdmin) return;
        const id = getParam("id");
        const detalle = $("#ownerAutoDetail");
        const form = $("#ownerAutoForm");
        const imageForm = $("#ownerImageForm");

        if (!id) {
            detalle.innerHTML = `<p class="empty-line">No se indico el auto.</p>`;
            return;
        }

        try {
            const [auto, imagenes, reviews] = await Promise.all([
                api(`/autos/${id}`),
                api(`/autos/${id}/imagenes`).catch(() => []),
                api(`/reviews/auto/${id}`).catch(() => [])
            ]);
            const principal = imagenes.find((imagen) => imagen.principal) || imagenes[0];
            detalle.innerHTML = `
                <section class="detail-layout">
                    <div>
                        <img class="detail-main-image" src="${principal?.urlImagen || carImage(auto)}" alt="${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}">
                        <div class="detail-thumbs">
                            ${imagenes.map((imagen) => `<img data-detail-thumb src="${escapeHtml(imagen.urlImagen)}" alt="${escapeHtml(imagen.nombreArchivo)}">`).join("")}
                        </div>
                    </div>
                    <div class="detail-info">
                        <span class="badge-soft">${auto.activo ? "Publicado" : "Dado de baja"}</span>
                        <h1>${escapeHtml(auto.marca)} ${escapeHtml(auto.modelo)}</h1>
                        <p>${escapeHtml(auto.descripcion || "Sin descripcion cargada.")}</p>
                        <dl class="spec-list">
                            <div><dt>Precio por dia</dt><dd>${formatMoney(auto.precioDia)}</dd></div>
                            <div><dt>Pasajeros</dt><dd>${escapeHtml(auto.capacidadPasajeros)}</dd></div>
                            <div><dt>Transmision</dt><dd>${pretty(auto.transmision)}</dd></div>
                            <div><dt>Categoria</dt><dd>${pretty(auto.categoria)}</dd></div>
                        </dl>
                    </div>
                </section>
            `;

            llenarAutoForm(form, auto);
            const statusButton = $("#toggleOwnerAutoStatus");
            if (statusButton) {
                statusButton.dataset.activo = String(auto.activo);
                statusButton.textContent = auto.activo ? "Dar de baja auto" : "Dar de alta auto";
                statusButton.className = auto.activo ? "btn btn-outline-danger" : "btn btn-outline-success";
            }
            renderOwnerImages(id, imagenes);
            renderReviews($("#ownerAutoReviews"), reviews);
            configurarGaleriaDetalle(detalle);
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            try {
                await api(`/autos/${id}/me`, {
                    method: "PUT",
                    body: JSON.stringify(crearAutoPayload(new FormData(form)))
                });
                mensaje("Auto modificado correctamente.", "success");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        $("#toggleOwnerAutoStatus")?.addEventListener("click", async (event) => {
            const estaActivo = event.currentTarget.dataset.activo === "true";
            try {
                if (estaActivo) {
                    await api(`/autos/${id}/me`, { method: "DELETE" });
                    mensaje("Auto dado de baja correctamente.", "success");
                } else {
                    await api(`/autos/${id}/me/activar`, { method: "PUT" });
                    mensaje("Auto dado de alta correctamente.", "success");
                }
                setTimeout(() => window.location.reload(), 700);
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        imageForm?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const archivos = [...imageForm.elements.file.files];
            if (!archivos.length) {
                mensaje("Selecciona al menos una imagen.", "warning");
                return;
            }

            try {
                for (const [index, file] of archivos.entries()) {
                    const formData = new FormData();
                    formData.append("file", file);
                    formData.append("principal", imageForm.elements.principal.checked && index === 0);
                    await apiMultipart(`/autos/${id}/imagenes/upload`, formData);
                }
                mensaje("Imagenes subidas correctamente.", "success");
                imageForm.reset();
                const imagenes = await api(`/autos/${id}/imagenes`).catch(() => []);
                renderOwnerImages(id, imagenes);
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        document.addEventListener("click", async (event) => {
            const boton = event.target.closest("[data-delete-owner-image]");
            if (!boton) return;

            try {
                await api(`/autos/${id}/imagenes/${boton.dataset.deleteOwnerImage}`, { method: "DELETE" });
                mensaje("Imagen eliminada correctamente.", "success");
                const imagenes = await api(`/autos/${id}/imagenes`).catch(() => []);
                renderOwnerImages(id, imagenes);
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    function renderOwnerImages(idAuto, imagenes) {
        const contenedor = $("#ownerImagesList");
        if (!contenedor) {
            return;
        }

        contenedor.innerHTML = imagenes.length
            ? imagenes.map((imagen) => `
                <div class="image-preview-card">
                    <img src="${escapeHtml(imagen.urlImagen)}" alt="${escapeHtml(imagen.nombreArchivo)}">
                    ${imagen.principal ? `<span class="image-preview-badge"></span>` : ""}
                    <button class="btn btn-light btn-sm image-delete-button" type="button" data-delete-owner-image="${imagen.idImagen}">
                        Eliminar
                    </button>
                </div>
            `).join("")
            : `<p class="empty-line">Este auto todavia no tiene imagenes.</p>`;
    }

    function llenarAutoForm(form, auto) {
        if (!form) return;
        [
            "marca", "modelo", "anio", "patente", "color", "capacidadPasajeros",
            "cantidadPuertas", "transmision", "combustible", "precioDia",
            "descripcion", "ciudad", "provincia", "direccionRetiro", "categoria"
        ].forEach((campo) => {
            if (form.elements[campo]) {
                form.elements[campo].value = auto[campo] ?? "";
            }
        });
    }

    function configurarGaleriaDetalle(root) {
        const main = root?.querySelector(".detail-main-image");
        if (!main) {
            return;
        }

        root.querySelectorAll("[data-detail-thumb]").forEach((thumb) => {
            thumb.addEventListener("click", () => {
                main.src = thumb.src;
                main.alt = thumb.alt;
            });
        });
    }

    function crearAutoPayload(formData) {
        return {
            marca: formData.get("marca"),
            modelo: formData.get("modelo"),
            anio: Number(formData.get("anio")),
            patente: formData.get("patente"),
            color: formData.get("color") || null,
            capacidadPasajeros: Number(formData.get("capacidadPasajeros")),
            cantidadPuertas: Number(formData.get("cantidadPuertas")),
            transmision: formData.get("transmision"),
            combustible: formData.get("combustible"),
            precioDia: Number(formData.get("precioDia")),
            descripcion: formData.get("descripcion") || null,
            ciudad: formData.get("ciudad"),
            provincia: formData.get("provincia") || null,
            direccionRetiro: formData.get("direccionRetiro"),
            categoria: formData.get("categoria")
        };
    }

    function renderReviews(contenedor, reviews) {
        if (!contenedor) {
            return;
        }
        if (!reviews.length) {
            contenedor.innerHTML = "";
            return;
        }
        const visibles = reviews.slice(0, 2);
        const ocultas = reviews.slice(2);
        contenedor.innerHTML = `
            <div class="section-heading">
                <div>
                    <span>Opiniones</span>
                    <h2>Reviews del auto</h2>
                </div>
            </div>
            <div class="review-list">
                ${visibles.map(reviewCard).join("")}
                <div id="moreReviews" class="review-list d-none">${ocultas.map(reviewCard).join("")}</div>
            </div>
            ${ocultas.length ? `<button class="btn btn-outline-primary mt-3" id="showMoreReviews" type="button">Mostrar mas reviews</button>` : ""}
        `;

        $("#showMoreReviews")?.addEventListener("click", () => {
            $("#moreReviews")?.classList.remove("d-none");
            $("#showMoreReviews")?.classList.add("d-none");
        });
    }

    function reviewCard(review) {
        return `
            <article class="review-card">
                <strong>${escapeHtml(review.nombreCliente || "Cliente")}</strong>
                <span class="review-stars">${renderStars(review.puntuacion)}</span>
                <p>${escapeHtml(review.comentario || "Sin comentario.")}</p>
            </article>
        `;
    }

    function renderStars(puntuacion) {
        const valor = Math.max(0, Math.min(5, Number(puntuacion || 0)));
        return `${"&#9733;".repeat(valor)}${"&#9734;".repeat(5 - valor)}`;
    }

    function reservaCard(reserva, modo = "cliente", autosConReview = new Set(), reservasConPago = new Set()) {
        const puedeAprobar = modo === "propietario" && reserva.estado === "PENDIENTE";
        const puedeCancelar = modo === "cliente" && ["PENDIENTE", "CONFIRMADA"].includes(reserva.estado);
        const pagoRegistrado = reservasConPago.has(Number(reserva.idReserva));
        const puedePagar = modo === "cliente" && reserva.estado === "CONFIRMADA" && !pagoRegistrado;
        const reviewPublicada = modo === "cliente" && autosConReview.has(Number(reserva.idAuto));
        const puedeReview = modo === "cliente" && reserva.estado === "FINALIZADA" && !reviewPublicada;
        return `
            <article class="list-card">
                <div>
                    <span class="badge-soft">${pretty(reserva.estado)}</span>
                    <h3>Reserva #${reserva.idReserva}</h3>
                    <p>${escapeHtml(reserva.auto)} - ${escapeHtml(reserva.fechaInicio)} al ${escapeHtml(reserva.fechaFin)}</p>
                    <small>${modo === "propietario" ? `Cliente: ${escapeHtml(reserva.nombreCliente || "")}` : formatMoney(reserva.precioTotal)}</small>
                </div>
                <div class="list-actions">
                    <a class="btn btn-outline-primary btn-sm" href="${modo === "propietario" ? "propietario-auto-detalle.html" : "auto-detalle.html"}?id=${reserva.idAuto}">Ver auto</a>
                    ${puedePagar ? `<a class="btn btn-primary btn-sm" href="cliente-pagos.html?idReserva=${reserva.idReserva}&monto=${reserva.precioTotal}">Pagar</a>` : ""}
                    ${pagoRegistrado && reserva.estado === "CONFIRMADA" ? `<span class="badge-soft">Pago registrado</span>` : ""}
                    ${modo === "cliente" && reserva.estado === "PENDIENTE" ? `<span class="badge-soft">Esperando aprobacion</span>` : ""}
                    ${puedeReview ? `<button class="btn btn-outline-success btn-sm" data-review-auto="${reserva.idAuto}" type="button">Agregar review</button>` : ""}
                    ${reviewPublicada ? `<span class="badge-soft">Review publicada</span>` : ""}
                    ${puedeCancelar ? `<button class="btn btn-outline-danger btn-sm" data-cancelar-reserva="${reserva.idReserva}" type="button">Cancelar</button>` : ""}
                    ${puedeAprobar ? `<button class="btn btn-success btn-sm" data-aprobar-reserva="${reserva.idReserva}" type="button">Aprobar</button>` : ""}
                </div>
            </article>
        `;
    }

    async function initReservas() {
        const contenedor = $("#reservasList");
        const formFiltro = $("#reservasFiltro");
        const reviewForm = $("#reviewForm");
        const reviewSection = $("#reviewSection");
        const reviewAutoLabel = $("#reviewAutoLabel");

        async function cargar(path = "/reservas/me") {
            const [reservas, pagos] = await Promise.all([
                api(path),
                api("/pagos/me").catch(() => [])
            ]);
            const autosConReview = await obtenerAutosConReviewPropia(reservas);
            const reservasConPago = new Set(
                pagos
                        .filter((pago) => pago.estado !== "RECHAZADO")
                        .map((pago) => Number(pago.idReserva))
            );
            contenedor.innerHTML = reservas.length
                ? reservas.map((reserva) => reservaCard(reserva, "cliente", autosConReview, reservasConPago)).join("")
                : `<p class="empty-line">Todavia no tenes reservas cargadas.</p>`;
        }

        async function obtenerAutosConReviewPropia(reservas) {
            const idsAuto = [...new Set(reservas.map((reserva) => Number(reserva.idAuto)).filter(Boolean))];
            const revisados = await Promise.all(idsAuto.map(async (idAuto) => {
                const reviews = await api(`/reviews/auto/${idAuto}`).catch(() => []);
                return reviews.some((review) => Number(review.idCliente) === Number(usuario.idUsuario)) ? idAuto : null;
            }));

            return new Set(revisados.filter(Boolean));
        }

        try {
            await cargar();
        } catch (error) {
            mensaje(error.message, "danger");
        }

        formFiltro?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const estado = new FormData(formFiltro).get("estado");
            try {
                await cargar(estado ? `/reservas/me/estado/${estado}` : "/reservas/me");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        document.addEventListener("click", async (event) => {
            const cancelar = event.target.closest("[data-cancelar-reserva]");
            const review = event.target.closest("[data-review-auto]");
            if (cancelar) {
                try {
                    await api(`/reservas/${cancelar.dataset.cancelarReserva}/cancelar`, { method: "PUT" });
                    mensaje("Reserva cancelada correctamente.", "success");
                    await cargar();
                } catch (error) {
                    mensaje(error.message, "danger");
                }
            }
            if (review && reviewForm) {
                reviewForm.idAuto.value = review.dataset.reviewAuto;
                if (reviewAutoLabel) {
                    reviewAutoLabel.textContent = `Auto #${review.dataset.reviewAuto}`;
                }
                reviewSection?.classList.remove("d-none");
                reviewSection?.scrollIntoView({ behavior: "smooth" });
            }
        });

        reviewForm?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(reviewForm);
            try {
                await api("/reviews/me", {
                    method: "POST",
                    body: JSON.stringify({
                        idAuto: Number(formData.get("idAuto")),
                        puntuacion: Number(formData.get("puntuacion")),
                        comentario: formData.get("comentario")
                    })
                });
                mensaje("Review cargada correctamente.", "success");
                reviewForm.reset();
                reviewSection?.classList.add("d-none");
                await cargar();
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    function pagoCard(pago, modo = "cliente") {
        const puedeGestionar = modo === "admin" && pago.estado === "PENDIENTE";
        return `
            <article class="list-card">
                <div>
                    <span class="badge-soft">${pretty(pago.estado)}</span>
                    <h3>Pago #${pago.idPago}</h3>
                    <p>${pretty(pago.metodoPago)} - Reserva #${pago.idReserva}</p>
                    <small>${formatMoney(pago.monto)}</small>
                    ${pago.linkPago ? `<a href="${escapeHtml(pago.linkPago)}" target="_blank" rel="noreferrer">Abrir Mercado Pago</a>` : ""}
                </div>
                ${puedeGestionar ? `
                    <div class="list-actions">
                        <button class="btn btn-success btn-sm" data-aprobar-pago="${pago.idPago}" type="button">Aprobar</button>
                        <button class="btn btn-outline-danger btn-sm" data-rechazar-pago="${pago.idPago}" type="button">Rechazar</button>
                    </div>
                ` : ""}
            </article>
        `;
    }

    async function initPagos() {
        const contenedor = $("#pagosList");
        const form = $("#pagoForm");
        const metodoPago = $("#metodoPago");
        const cardFields = $("#cardFields");
        $("#idReserva") && ($("#idReserva").value = getParam("idReserva") || "");
        $("#monto") && ($("#monto").value = getParam("monto") || "");

        function actualizarCamposPago() {
            const esTarjeta = metodoPago?.value === "TARJETA";
            cardFields?.classList.toggle("d-none", !esTarjeta);
            ["titularTarjeta", "numeroTarjeta", "vencimientoTarjeta", "codigoSeguridad"].forEach((campo) => {
                if (form?.elements[campo]) {
                    form.elements[campo].required = esTarjeta;
                }
            });
        }

        metodoPago?.addEventListener("change", actualizarCamposPago);
        actualizarCamposPago();

        async function cargar() {
            const pagos = await api("/pagos/me");
            contenedor.innerHTML = pagos.length ? pagos.map((pago) => pagoCard(pago)).join("") : `<p class="empty-line">No tenes pagos registrados.</p>`;
        }

        try {
            await cargar();
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
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
                const pago = await api("/pagos", { method: "POST", body: JSON.stringify(body) });
                if (pago.linkPago) {
                    window.open(pago.linkPago, "_blank", "noopener,noreferrer");
                    mensaje("Se abrio Mercado Pago en una nueva pestaña.", "success");
                    await cargar();
                    return;
                }
                mensaje("Pago registrado correctamente.", "success");
                await cargar();
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initPerfil() {
        const form = $("#perfilForm");
        try {
            const perfil = await api("/usuarios/me");
            form.nombre.value = perfil.nombre || "";
            form.email.value = perfil.email || "";
            form.telefono.value = perfil.telefono || "";
            $("#perfilRoles").textContent = (perfil.roles || []).join(", ");
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(form);
            try {
                const actualizado = await api("/usuarios/me", {
                    method: "PUT",
                    body: JSON.stringify({
                        nombre: formData.get("nombre"),
                        email: formData.get("email"),
                        telefono: formData.get("telefono")
                    })
                });
                localStorage.setItem("autorent_usuario", JSON.stringify(actualizado));
                mensaje("Perfil actualizado correctamente.", "success");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        $("#bajaPerfil")?.addEventListener("click", async () => {
            try {
                await api("/usuarios/me", { method: "DELETE" });
                cerrarSesion();
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initPropietarioAutos() {
        if (!esPropietario && !esAdmin) return;
        const contenedor = $("#ownerCars");
        try {
            const autos = await api("/autos/me");
            contenedor.innerHTML = autos.length
                ? `<div class="car-row">${autos.map((auto) => autoCard(auto, {}, "propietario-auto-detalle.html", "Gestionar")).join("")}</div>`
                : `<p class="empty-line">Todavia no publicaste autos.</p>`;
            autos.forEach(cargarImagenPrincipal);
        } catch (error) {
            mensaje(error.message, "danger");
        }
    }

    async function initPropietarioPerfil() {
        if (!esPropietario && !esAdmin) return;
        const form = $("#propietarioPerfilForm");

        try {
            const perfil = await api("/propietarios/me");
            form.dni.value = perfil.dni || "";
            form.cuit.value = perfil.cuit || "";
            form.direccion.value = perfil.direccion || "";
            form.ciudad.value = perfil.ciudad || "";
            form.provincia.value = perfil.provincia || "";
            $("#propietarioPerfilEstado").textContent = `${perfil.activo ? "Activo" : "Inactivo"} - ${perfil.verificado ? "Verificado" : "Sin verificar"}`;
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const data = new FormData(form);
            try {
                await api("/propietarios/me", {
                    method: "PUT",
                    body: JSON.stringify({
                        dni: data.get("dni"),
                        cuit: data.get("cuit"),
                        direccion: data.get("direccion"),
                        ciudad: data.get("ciudad"),
                        provincia: data.get("provincia")
                    })
                });
                mensaje("Datos de propietario actualizados correctamente.", "success");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initPropietarioSolicitudes() {
        if (!esPropietario && !esAdmin) return;
        const contenedor = $("#ownerRequests");

        async function cargar() {
            const reservas = await api(`/reservas/propietario/${usuario.idUsuario}`);
            contenedor.innerHTML = reservas.length
                ? reservas.map((reserva) => reservaCard(reserva, "propietario")).join("")
                : `<p class="empty-line">No hay solicitudes de reserva para tus autos.</p>`;
        }

        try {
            await cargar();
        } catch (error) {
            mensaje(error.message, "danger");
        }

        document.addEventListener("click", async (event) => {
            const aprobar = event.target.closest("[data-aprobar-reserva]");
            if (!aprobar) return;
            try {
                await api(`/reservas/${aprobar.dataset.aprobarReserva}/confirmar`, { method: "PUT" });
                mensaje("Reserva aprobada correctamente.", "success");
                await cargar();
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initPropietarioPagos() {
        if (!esPropietario && !esAdmin) return;
        const contenedor = $("#ownerPayments");
        try {
            const pagos = await api("/pagos/me/propietario");
            contenedor.innerHTML = pagos.length ? pagos.map((pago) => pagoCard(pago, "propietario")).join("") : `<p class="empty-line">No hay cobros asociados a tus autos.</p>`;
        } catch (error) {
            mensaje(error.message, "danger");
        }
    }

    function propietarioCard(propietario) {
        return `
            <article class="list-card">
                <div>
                    <span class="badge-soft">${propietario.activo ? "Activo" : "Inactivo"}${propietario.verificado ? " - Verificado" : ""}</span>
                    <h3>${escapeHtml(propietario.nombreUsuario)}</h3>
                    <p>${escapeHtml(propietario.emailUsuario)} - ${escapeHtml(propietario.ciudad || "Sin ciudad")}</p>
                    <small>DNI ${escapeHtml(propietario.dni || "-")} - CUIT ${escapeHtml(propietario.cuit || "-")}</small>
                </div>
                <div class="list-actions">
                    <a class="btn btn-primary btn-sm" href="admin-propietario-detalle.html?id=${propietario.idUsuario}">Ver perfil</a>
                </div>
            </article>
        `;
    }

    async function initAdminPropietarios() {
        if (!esAdmin) return;
        const contenedor = $("#adminOwners");
        const filtro = $("#adminOwnersFilter");

        async function cargar(path = "/propietarios/activos/true") {
            const propietarios = await api(path);
            contenedor.innerHTML = propietarios.length ? propietarios.map(propietarioCard).join("") : `<p class="empty-line">No hay propietarios para mostrar.</p>`;
        }

        try {
            await cargar();
        } catch (error) {
            mensaje(error.message, "danger");
        }

        filtro?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const data = new FormData(filtro);
            const tipo = data.get("tipo");
            const valor = data.get("valor");
            const path = valor ? `/propietarios/${tipo}/${encodeURIComponent(valor)}` : "/propietarios/activos/true";
            try {
                await cargar(path);
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initAdminPropietarioDetalle() {
        if (!esAdmin) return;
        const id = getParam("id");
        const form = $("#adminOwnerDetailForm");
        if (!id) return;

        try {
            const propietario = await api(`/propietarios/${id}`);
            $("#ownerTitle").textContent = propietario.nombreUsuario;
            form.dni.value = propietario.dni || "";
            form.cuit.value = propietario.cuit || "";
            form.direccion.value = propietario.direccion || "";
            form.ciudad.value = propietario.ciudad || "";
            form.provincia.value = propietario.provincia || "";
            $("#ownerMeta").textContent = `${propietario.emailUsuario} - ${propietario.activo ? "Activo" : "Inactivo"} - ${propietario.verificado ? "Verificado" : "Sin verificar"}`;
        } catch (error) {
            mensaje(error.message, "danger");
        }

        form?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const data = new FormData(form);
            try {
                await api(`/propietarios/${id}`, {
                    method: "PUT",
                    body: JSON.stringify({
                        dni: data.get("dni"),
                        cuit: data.get("cuit"),
                        direccion: data.get("direccion"),
                        ciudad: data.get("ciudad"),
                        provincia: data.get("provincia")
                    })
                });
                const propietario = await api(`/propietarios/${id}/verificar`, { method: "PUT" });
                $("#ownerMeta").textContent = `${propietario.emailUsuario} - ${propietario.activo ? "Activo" : "Inactivo"} - ${propietario.verificado ? "Verificado" : "Sin verificar"}`;
                mensaje("Propietario actualizado y verificado correctamente.", "success");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        $("#deleteOwner")?.addEventListener("click", async () => {
            try {
                await api(`/propietarios/${id}`, { method: "DELETE" });
                mensaje("Propietario dado de baja.", "success");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    async function initAdminPagos() {
        if (!esAdmin) return;
        const contenedor = $("#adminPayments");
        const filtro = $("#adminPaymentsFilter");

        async function cargar(path = "/pagos") {
            const pagos = await api(path);
            contenedor.innerHTML = pagos.length ? pagos.map((pago) => pagoCard(pago, "admin")).join("") : `<p class="empty-line">No hay pagos para mostrar.</p>`;
        }

        try {
            await cargar();
        } catch (error) {
            mensaje(error.message, "danger");
        }

        filtro?.addEventListener("submit", async (event) => {
            event.preventDefault();
            const estado = new FormData(filtro).get("estado");
            try {
                await cargar(estado ? `/pagos/estado/${estado}` : "/pagos");
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });

        document.addEventListener("click", async (event) => {
            const aprobar = event.target.closest("[data-aprobar-pago]");
            const rechazar = event.target.closest("[data-rechazar-pago]");
            try {
                if (aprobar) {
                    await api(`/pagos/${aprobar.dataset.aprobarPago}/aprobar`, { method: "PUT" });
                    mensaje("Pago aprobado correctamente.", "success");
                    await cargar();
                }
                if (rechazar) {
                    await api(`/pagos/${rechazar.dataset.rechazarPago}/rechazar`, { method: "PUT" });
                    mensaje("Pago rechazado correctamente.", "success");
                    await cargar();
                }
            } catch (error) {
                mensaje(error.message, "danger");
            }
        });
    }

    renderNavbar();
    configurarAutocompletesLugares();

    const initByView = {
        home: initHome,
        "auto-detail": initAutoDetalle,
        reservas: initReservas,
        pagos: initPagos,
        perfil: initPerfil,
        "propietario-autos": initPropietarioAutos,
        "propietario-auto-detail": initPropietarioAutoDetalle,
        "propietario-solicitudes": initPropietarioSolicitudes,
        "propietario-pagos": initPropietarioPagos,
        "propietario-perfil": initPropietarioPerfil,
        "admin-propietarios": initAdminPropietarios,
        "admin-propietario-detalle": initAdminPropietarioDetalle,
        "admin-pagos": initAdminPagos
    };

    initByView[view]?.();
})();
