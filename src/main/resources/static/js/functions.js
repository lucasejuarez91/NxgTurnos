const lang = metaLang.lang === 'es' ? esAr : enGB;
$(document).ready(async function(){
    await initI18n();
    $('#btnLogout').on('click', async function(){
       await logout();
    });
    loadHtmlOnModal(document.getElementById('headerButton'), 'headerModalCreate');
});

async function initI18n() {
    //const lang = navigator.language.startsWith("es") ? "es" : "en"; // Detecta el idioma del navegador
    const response = await fetch(getContextPath() + `/locales/${metaLang.lang}.json`);
    const translations = await response.json();

    i18next.init({
        lng: metaLang.lang,
        resources: {
            [metaLang.lang]: { translation: translations }
        }
    });
}

function loadCombo(element, type){
	if(type === undefined){
		$(element).select2({
            placeholder: i18next.t("select.any.option"),
            allowClear: false,
            theme: "bootstrap-5"
        });
        return;
	}
    fetch(`/${type}/loadCombo`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            // Crea un elemento vacío por defecto
            const defaultOption = {
                id: '', // ID vacío
                text: i18next.t("select.any.option"), // Texto para mostrar como opción por defecto
                description: '' // Descripción vacía
            };

            const formattedData = [defaultOption, ...data.data.map(item => {
                const formattedItem = {
                    id: item.id,
                    text: item.name
                };

                // Agrega todas las propiedades adicionales de manera dinámica
                for (const key in item) {
                    if (item.hasOwnProperty(key) && !['id', 'name'].includes(key)) {
                        formattedItem[key] = item[key]; // Añade las propiedades al objeto
                    }
                }

                return formattedItem;
            })];




            $(element).select2({
                data: formattedData,
                placeholder: i18next.t("select.any.option"),
                allowClear: true,
                containerCssClass: "form-control",
                templateResult: formatState,
                templateSelection: formatState
            });


        })
        .catch((error) => {
            console.error(i18next.t("title.error"), error);
        });
}

function initDataTable(elementId, orderable = [0]){
	table = new DataTable(`#${elementId}`, {
	    ordering: true,
	    destroy: true,
	    responsive: true,
	    language: lang,
        stateSave: true,
        order: orderable
	});
}

function initDataTableGroup(elementId, order, asc, groupColumn){
    table = new DataTable(`#${elementId}`, {
        columnDefs: [{visible: false, targets: groupColumn}, {visible: false, targets: order}],
        order: [[order, asc ? 'asc' : 'desc']],
        scrollCollapse: true,
        scrollY: '50vh',
        displayLength: 25,
        ordering: true,
        destroy: true,
        responsive: true,
        language: lang,
        stateSave: false,
        drawCallback: function () {
            let api = this.api();
            let rows = api.rows({page: 'current'}).nodes();
            let last = null;
            api.column(groupColumn, {page: 'current'})
                .data()
                .each(function (group, i) {
                    if (last !== group) {
                        $(rows)
                            .eq(i)
                            .before(
                                '<tr class="group text-uppercase"><td colspan="8">' +
                                group +
                                '</td></tr>'
                            );
                        last = group;
                    }
                });
        }
    });
}

function callFetch(url, method, body){
	// Hacer una solicitud POST con fetch
    loader();
    return fetch(url, {
	    method: method,  // Especifica el método HTTP
	    headers: {
	        'Content-Type': 'application/json'  // Indica que el cuerpo es JSON
	    },
	    body: JSON.stringify(body)  // Convierte el objeto JS en una cadena JSON
	})
    .then(response => {
        if (!response.ok) {
			modalAlert("error", response.status);
			return false;
            //throw new Error('Error en la solicitud: ' + response.status);
        }
        return response.json();  // Convertir la respuesta a JSON
    })
    .then(data => {
        if (data !== false) {  // Verifica que data no sea el valor de error
            //console.log('Datos enviados:', data);
            loader(false);
            return true;  // Devuelve true si la operación fue exitosa
        }
        loader(false);
        return false;
    })
    .catch(error => {
        console.error(i18next.t("title.error",error));
        loader(false);
        return false;  // Devuelve false si ocurre algún error
    });

}

function modalAlert(type = "info", message, callback = false){
    Swal.fire({
        icon: type,
        html: message
    }).then((result) => {
        if (result.isConfirmed) {
            if(callback){
                callback();
            }
        }
    });
}

async function logout(){
    const respuesta = await modalConfirmation(i18next.t("title.modal.logout"));
    if (respuesta) {
        window.location.href = getContextPath() + "/logout";
    }
}
function loadHtmlOnModal(element, modalElement = 'modalCreate', isLarge = false, title = ""){
    if(element === null){
        return;
    }
	element.addEventListener('click', function(event) {
		//event.preventDefault();
        loader();
		// Obtén la URL del video desde el atributo data-url del botón
		const url = this.getAttribute('data-url');
		if(isLarge){
			$(`#${modalElement} .modal-dialog`).addClass('modal-lg');
		}
		if(title !== ""){
			$(`#${modalElement} #modalTitle`).text(title);
		}
		// Realiza la llamada fetch para cargar el fragmento Thymeleaf con el video
		fetch(url)
			.then(response => {
				if (!response.ok) {	
                    loader(false);
					throw new Error(i18next.t("cannot.get.modal", response.status));
				}
				return response.text(); // El controlador devuelve una vista Thymeleaf como HTML
			})
			.then(html => {
				// Insertar el HTML cargado en el modal
				document.querySelector(`#${modalElement} .modal-body`).innerHTML = html;

				// Mostrar el modal
				$(`#${modalElement}`).modal('show');
				//loadCombo(slctStatus)
                $(`#${modalElement}`).on('shown.bs.modal', function(){
                    loader();
                    $(`#${modalElement} select`).select2({
                        placeholder: i18next.t("select.any.option"),
                        allowClear: false,
                        theme: "bootstrap-5"
                    });
                    $(`#${modalElement} #status`).val("True").addClass('changed').trigger('change');
                    $(`#${modalElement} input, #${modalElement} select, #${modalElement} textarea`).on('change', function(){
                        $(this).addClass('changed');
                    });
                    loader(false);
                });
                loader(false);
            })
			.catch(error => {
				console.error('Error:', error);
				modalAlert("error", i18next.t("cannot.get.modal", error));
			});
	});
}

function loader(visible = true) {
	if(visible){
        Swal.fire({
            title: i18next.t("title.loading"),
            allowEscapeKey: false,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            }
        });
	} else {
		Swal.close()
	}
}

async function updateEntity(entity, id, changes) {
    try {
        const response = await fetch(getContextPath() + `/${entity}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(changes)
        });

        if (!response.ok) {
			modalAlert("error", i18next.t("error", response.statusText));
            //throw new Error(`Error al actualizar: ${response.statusText}`);
        }
        await mixinAlert("success", i18next.t("entity.updated"), reload);
    } catch (error) {
		modalAlert("error", error);
        console.error('Error:', error);
    }
}

async function getChanges(changes = {}){
    //let changes = {};
    let elements = $('.changed');
    if(elements.length > 0){
        elements.each(function() {
            let fieldName = $(this).attr('id') || $(this).attr('name'); // Usa 'id' o 'name' como clave
            let fieldValue = $(this).val(); // Captura el valor del campo

            if (fieldName) {
                //if(Array.isArray(fieldValue)){
                if($(this).attr('type') === 'time'){
                    //let timeValue = document.getElementById("timeInput").value;
                    let today = new Date().toISOString().split("T")[0]; // Obtiene YYYY-MM-DD
                    changes[fieldName] = today + "T" + fieldValue + ":00";
                } else {
                //} else {
                    changes[fieldName] = fieldValue; // Agrega el campo y su valor al objeto cambios
                }
            }
        });
        //await updateEntity("users", userId.value, changes);
    }
    return changes;
}

async function mixinAlert(type, message, callback = false, timer = 1000){
	const Toast = Swal.mixin({
	  toast: true,
	  position: "top-end",
	  showConfirmButton: false,
	  timer: timer,
	  timerProgressBar: true,
	  didOpen: (toast) => {
	    toast.onmouseenter = Swal.stopTimer;
	    toast.onmouseleave = Swal.resumeTimer;
	  }
	});
	Toast.fire({
	  icon: type,
	  title: message
	}).then(() => {
		if(callback){
			callback();
		}
	})
}

function reload() {
  location.reload();
}

async function uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file); // Adjunta el archivo al FormData

    const response = await fetch(getContextPath() + '/file-upload/', {
        method: 'POST',
        body: formData // Enviar el FormData directamente
    });

    if (response.ok) {
        return response.json();
    } else {
        modalAlert("error", i18next.t("title.error.cannot.upload.image"));
    }
}

async function manageEntity(entity, id = '', body = {}, method = 'POST'){
    loader();
    if (Object.keys(body).length === 0) {
        body = await getChanges();
    }

    if (Object.keys(body).length === 0) {
        await mixinAlert("info", i18next.t("nothing.to.do"));
        return;
    }
    callFetch(getContextPath() + `/${entity}${id === '' ? '' : '/' + id}`, `${id === '' ? 'POST' : 'PATCH'}`, body)
        .then(async success => {
            if (success) {
                await mixinAlert("success", id === '' ? i18next.t("entity.created") : i18next.t("entity.updated"), reload);
            }
        })
        .catch(async reason => await mixinAlert("error", reason))
}
async function modalConfirmation(message) {
    const result = await Swal.fire({
        title: i18next.t("modal.confirmation.title", { defaultValue: "Confirmación" }),
        html: message,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: 'primary',
        cancelButtonColor: 'red',
        confirmButtonText: i18next.t("modal.confirmation.yes"),
        cancelButtonText: i18next.t("modal.confirmation.cancel")
    });

    // Retorna `true` si el usuario confirma, de lo contrario `false`
    return result.isConfirmed;
}
function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}

function updateTimestamp() {
    const element = document.getElementById("timestamp");
    if(!element){return}
    const currentTime = new Date();
    element.textContent = currentTime.toLocaleString('es-ES', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

setInterval(updateTimestamp, 1000);  // Actualizar cada 1 segundo

const eventClick = async function (info) {
    let event = info.event;
    if (!event.extendedProps.isLoggedIn) {
        $('#openLoginModal').modal('show');
        document.getElementById('redirectTo').value = window.location.href;
        return;
    }
    let fechaInicio = toLocalISOString(info.event.start); // Convierte a formato ISO 8601
    let fechaFin = info.event.end ? toLocalISOString(info.event.end) : null; // Opcional si existe
    let fechaInicioLbl = moment(fechaInicio).locale(metaLang.lang).format(`DD [${i18next.t("prefix.of")}] MMMM [${i18next.t("prefix.of")}] YYYY [${i18next.t("prefix.to.hour")}] HH:mm`);
    let loungeName = $('#companyLbl').text();
    let professionalName = $('#professionalLbl').text();
    let message = i18next.t("title.modal.request.book", {fechaInicioLbl, loungeName, professionalName});
    let respuesta = await modalConfirmation(message);
    if (!respuesta) {
        return;
    }

    await fetch(getContextPath() + `/appointments/preconfirm`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({start: fechaInicio, end: fechaFin})
    })
        .then(response => response.json())  // Convertir la respuesta a JSON
        .then(async data => {
            if (data.redirectUrl) {
                // Redirigir directamente a la página de confirmación
                //window.location.href = "/booking/confirmation";
                await modalAlert("success", i18next.t("title.book.created"), () => {
                    window.location.href = data.redirectUrl; // Redirecciona solo si el usuario confirma
                });
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            //console.error("Error en la solicitud:", error);
            alert(i18next.t("cannot.get.event", error));
        });

};

function initCalendar(professional, serviceId, calendarElement = 'calendar', slotMinTime, slotMaxTime, evtClick = eventClick){
    var dateLimit = new Date();
    dateLimit.setDate(dateLimit.getDate() + 30);
    var calendarEl = document.getElementById(calendarElement);
    cal = new FullCalendar.Calendar(calendarEl, {
        initialView: window.innerWidth < 768 ? 'timeGridDay' : 'timeGridWeek',
        locale: metaLang.lang,
        firstDay: new Date().getDay(),
        //slotMinTime: slotMinTime || document.getElementById('companyLbl').dataset.minstarttime, // Configurable desde backend
        //slotMaxTime: slotMaxTime || document.getElementById('companyLbl').dataset.maxendtime,   // Configurable desde backend
        validRange: {
            start: new Date(),  // Evita fechas pasadas
            end: dateLimit
        },
        height: '600px',
        aspectRatio: 9,
        contentHeight: 300,
        allDaySlot: false,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: window.innerWidth < 768 ? 'timeGridDay' : 'timeGridWeek,timeGridDay'
        }, // amarillo suave
        nowIndicator: true,
        events: function(fetchInfo, successCallback, failureCallback) {
            // Convertir a formato YYYY-MM-DD sin zona horaria
            let start = fetchInfo.start.toISOString().split("T")[0];
            let end = fetchInfo.end.toISOString().split("T")[0];

            let url = getContextPath() + `/appointments/request/slots/appts?professionalId=${professional}&start=${start}&end=${end}&includeUnavailable=false&serviceId=${serviceId}`;

            //console.log("Llamando a la API:", url);

            fetch(url)
                .then(response => response.json())
                .then(data => {
                    //mixinAlert("success", "Turnos obtenidos con éxito.", false, 3000);
                    // Suponiendo que el backend devuelve `isLoggedIn`
                    let isLoggedIn = data.isLoggedIn;

                    let events = data.events.map(event => ({
                        ...event,
                        extendedProps: { isLoggedIn } // Agregar info extra a cada evento
                    }));

                    successCallback(events);
                })
                .catch(error => "");//mixinAlert("warning", "No se pudo obtener la Agenda del Profesional por Servicio. Verifique otras opciones.", false, 6000));
        },
        eventClick: evtClick
    });
    cal.render();
}

function toLocalISOString(date) {
    let offset = date.getTimezoneOffset() * 60000; // Convierte el offset de minutos a milisegundos
    let localTime = new Date(date.getTime() - offset); // Aplica la diferencia horaria
    return localTime.toISOString().slice(0, 19); // Quita la "Z" final
}

function initLoginModal(){
    document.getElementById("loginForm").addEventListener("submit", function (event) {
        event.preventDefault(); // Evita que el formulario se envíe normalmente

        let formData = new FormData(this);

        fetch(this.action, {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(i18next.t("title.login.error"));
                }
                return response.text();
            })
            .then(data => {
                // Si el login es exitoso, cerrar el modal y recargar eventos
                $('#openLoginModal').modal('hide');
                location.reload(); // Recargar la página para reflejar el estado de autenticación
            })
            .catch(error => {
                // Mostrar mensaje de error sin recargar la página
                let errorBox = document.getElementById("loginError");
                errorBox.classList.remove("d-none");
                errorBox.querySelector(".message").innerText = error.message;
            });
    });
    $('#openLoginModal #linkToRegister').on('click', function(event){
        event.preventDefault();
        $('#openLoginModal').modal('hide');
        $('#openRegisterModal').modal('show');
    });
    $('#openRegisterModal #linkToLogin').on('click', function(event){
        event.preventDefault();
        $('#openRegisterModal').modal('hide');
        $('#openLoginModal').modal('show');
    });
}

function refreshFragment(url, idFragment, method = 'GET') {
    // Realizar una solicitud AJAX para recargar el fragmento
    fetch(url, {
        method: method
    })
    .then(response => response.text()) // Asumiendo que el backend responde con JSON
    .then(async data => {
        $(idFragment).html(data);
    })
    .catch(error => {
        console.error('Error al enviar los datos:', error);
    });
}

function getCurrentUrlParams() {
    return new URLSearchParams(window.location.search);
}

function changeLanguage(lang) {
    const currentParams = getCurrentUrlParams();
    currentParams.set('lang', lang);  // Cambia el parámetro 'lang'
    window.location.href = window.location.pathname + '?' + currentParams.toString();  // Redirige con la nueva URL
}

function toggleBlocks(element) {
    const container = document.getElementById(element);
    if (container.style.display === 'none') {
        container.style.display = 'block';
    } else {
        container.style.display = 'none';
    }
}
