let currentDate = new Date(); // Fecha actual de navegación
let cuidadorasList = [];
let jornadasRegistradas = {}; // Mapa de 'YYYY-MM-DD' -> cuidadoraId

// Cargar cuidadoras al iniciar
async function cargarCuidadoras() {
    try {
        const response = await fetch('/api/cuidadoras');
        cuidadorasList = await response.json();
    } catch (error) {
        console.error("Error al cargar cuidadoras:", error);
    }
}
// Cargar la tasa oficial del Euro al iniciar la página
async function obtenerTasaOficialAutomatica() {
    try {
        // Hacemos una petición rápida al endpoint de cálculo sin parámetros para que devuelva la tasa del BCV
        const response = await fetch('/api/calcular-pago');
        if (response.ok) {
            const datos = await response.json();
            if (datos.length > 0 && datos[0].tasaUtilizada) {
                const tasaOficial = datos[0].tasaUtilizada;
                // Colocamos el valor en el input de la pantalla usando formato con coma para comodidad del usuario
                document.getElementById('tasa-bcv').value = tasaOficial.toFixed(2).replace('.', ',');
                console.log("Tasa del BCV cargada automáticamente: " + tasaOficial);
            }
        }
    } catch (error) {
        console.error("No se pudo pre-cargar la tasa automática:", error);
    }
}
// Cargar jornadas guardadas
async function cargarJornadas() {
    try {
        const response = await fetch('/api/jornadas');
        const jornadas = await response.json();
        jornadasRegistradas = {};
        jornadas.forEach(j => {
            jornadasRegistradas[j.fecha] = j.cuidadoraId;
        });
    } catch (error) {
        console.error("Error al cargar jornadas:", error);
    }
}

// Mostrar un mensaje flotante de guardado
function showToast(message) {
    const toast = document.getElementById('toast-msg');
    toast.textContent = message;
    toast.style.display = 'block';
    setTimeout(() => {
        toast.style.display = 'none';
    }, 2000);
}

// Renderizar el calendario de un mes
async function renderCalendar() {
    await cargarJornadas(); // Actualizar las jornadas en memoria

    const daysContainer = document.getElementById('calendar-days');
    daysContainer.innerHTML = '';

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    // Nombre del mes en español
    const options = { month: 'long', year: 'numeric' };
    document.getElementById('current-month-year').textContent = currentDate.toLocaleDateString('es-ES', options);

    // Primer día del mes (0 = Domingo, 1 = Lunes, etc.)
    const firstDayIndex = new Date(year, month, 1).getDay();
    // Total días en el mes
    const totalDays = new Date(year, month + 1, 0).getDate();

    // Rellenar días vacíos al inicio de la semana
    for (let i = 0; i < firstDayIndex; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('day-cell', 'empty');
        daysContainer.appendChild(emptyCell);
    }

    // Renderizar los días reales del mes
    for (let day = 1; day <= totalDays; day++) {
        const dayCell = document.createElement('div');
        dayCell.classList.add('day-cell');

        // Formatear la fecha como YYYY-MM-DD local
        const monthStr = String(month + 1).padStart(2, '0');
        const dayStr = String(day).padStart(2, '0');
        const dateKey = `${year}-${monthStr}-${dayStr}`;

        // Número de día
        const numLabel = document.createElement('span');
        numLabel.classList.add('day-number');
        numLabel.textContent = day;
        dayCell.appendChild(numLabel);

        // Crear el menú desplegable (Select)
        const select = document.createElement('select');
        select.classList.add('day-select');

        // Opción por defecto (Vacío)
        const defaultOpt = document.createElement('option');
        defaultOpt.value = "";
        defaultOpt.textContent = "— Libre —";
        select.appendChild(defaultOpt);

        // Llenar con cuidadoras cargadas de la API
        cuidadorasList.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            opt.textContent = c.nombre;
            select.appendChild(opt);
        });

        // Seleccionar cuidadora si ya está asignada en esta fecha
        if (jornadasRegistradas[dateKey]) {
            select.value = String(jornadasRegistradas[dateKey]);
            dayCell.style.backgroundColor = '#e8f8f5'; // Pintar suave si trabaja alguien
        }

        // Evento al cambiar cuidadora
        select.addEventListener('change', async (e) => {
            const selectedId = e.target.value;
            if (selectedId === "") {
                // Si selecciona libre, eliminamos la jornada
                try {
                    const res = await fetch(`/api/jornadas/eliminar?fecha=${dateKey}`, { method: 'POST' });
                    if (res.ok) {
                        dayCell.style.backgroundColor = '#fff';
                        showToast("Día marcado como libre");
                    }
                } catch (err) {
                    console.error(err);
                }
            } else {
                // Si selecciona cuidadora, guardamos la jornada
                try {
                    const res = await fetch(`/api/jornadas/asignar?fecha=${dateKey}&cuidadoraId=${selectedId}`, { method: 'POST' });
                    if (res.ok) {
                        dayCell.style.backgroundColor = '#e8f8f5';
                        showToast("Jornada guardada con éxito");
                    }
                } catch (err) {
                    console.error(err);
                }
            }
        });

        dayCell.appendChild(select);
        daysContainer.appendChild(dayCell);
    }
}

// Configuración de botones de navegación del mes
document.getElementById('prev-month').addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar();
});

document.getElementById('next-month').addEventListener('click', () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar();
});

// Botón de cálculo de pagos
document.getElementById('btn-calcular').addEventListener('click', async () => {
    // Obtener el valor de la tasa y formatearla para asegurar que use puntos decimales (.) en vez de comas (,)
    let tasaRaw = document.getElementById('tasa-bcv').value || "46.50";
    let tasaFormateada = tasaRaw.replace(',', '.');

    try {
        const response = await fetch(`/api/calcular-pago?tasaBcv=${tasaFormateada}`);
        if (!response.ok) throw new Error("Error en la respuesta del cálculo");
        const pagos = await response.json();

        // Enlazar con el cuerpo de la tabla de resultados
        const tbody = document.getElementById('results-body');
        tbody.innerHTML = '';

        let granTotalUsd = 0;
        let granTotalBs = 0;

        pagos.forEach(p => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>${p.nombre}</strong></td>
                <td>${p.diasTrabajados} días</td>
                <td>$${p.totalDolares.toFixed(2)}</td>
                <td>Bs. ${p.totalBolivares.toLocaleString('es-VE', {minimumFractionDigits: 2, maximumFractionDigits: 2})}</td>
            `;
            tbody.appendChild(row);
            granTotalUsd += p.totalDolares;
            granTotalBs += p.totalBolivares;
        });

        // Fila de Totales Generales
        const totalRow = document.createElement('tr');
        totalRow.classList.add('total-row');
        totalRow.innerHTML = `
            <td><strong>TOTAL GENERAL</strong></td>
            <td>—</td>
            <td><strong>$${granTotalUsd.toFixed(2)}</strong></td>
            <td><strong>Bs. ${granTotalBs.toLocaleString('es-VE', {minimumFractionDigits: 2, maximumFractionDigits: 2})}</strong></td>
        `;
        tbody.appendChild(totalRow);

        // Asegurar que el contenedor de resultados sea visible
        document.getElementById('results-container').style.display = 'block';
    } catch (error) {
        console.error("Error al calcular pagos:", error);
    }
});
// Inicializar la aplicación
async function init() {
    await cargarCuidadoras();
    await renderCalendar();
    await obtenerTasaOficialAutomatica(); // <-- ¡Llamada agregada aquí!
}

init();