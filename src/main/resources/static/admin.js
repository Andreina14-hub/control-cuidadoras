const API_URL = '/api';

document.addEventListener('DOMContentLoaded', () => {
    cargarCuidadoras();
});

// 1. OBTENER TODAS LAS CUIDADORAS
async function cargarCuidadoras() {
    try {
        const response = await fetch(`${API_URL}/cuidadoras`);
        const cuidadoras = await response.json();

        const tabla = document.getElementById('tablaCuidadoras');
        const sinDatos = document.getElementById('sinDatos');
        const contador = document.getElementById('totalCuidadoras');

        tabla.innerHTML = '';
        contador.textContent = `${cuidadoras.length} Activas`;

        if (cuidadoras.length === 0) {
            sinDatos.classList.remove('d-none');
        } else {
            sinDatos.classList.add('d-none');
            cuidadoras.forEach(c => {
                tabla.innerHTML += `
                    <tr>
                        <td class="fw-bold">#${c.id}</td>
                        <td>${c.nombre}</td>
                        <td><span class="badge bg-success">$${c.tarifaPorDia}/h</span></td>
                        <td class="text-end">
                            <button class="btn btn-outline-danger btn-sm rounded-pill" onclick="eliminarCuidadora(${c.id})">
                                <i class="bi bi-trash3-fill"></i> Eliminar
                            </button>
                        </td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Error cargando cuidadoras:", error);
    }
}

// 2. AGREGAR UNA NUEVA CUIDADORA
async function agregarCuidadora(event) {
    event.preventDefault();
    const nombre = document.getElementById('nombre').value;
    const tarifa = parseFloat(document.getElementById('tarifaIndividual').value);

    try {
        const response = await fetch(`${API_URL}/cuidadoras`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nombre: nombre,
                tarifaPorDia: tarifa // <-- Cambiado aquí
            })
        });

        if (response.ok) {
            document.getElementById('formCuidadora').reset();
            document.getElementById('tarifaIndividual').value = "30";
            cargarCuidadoras();
        } else {
            alert("Error al registrar a la cuidadora.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

// 3. ELIMINAR UNA CUIDADORA
async function eliminarCuidadora(id) {
    if (!confirm("¿Seguro que deseas eliminar esta cuidadora?")) return;

    try {
        const response = await fetch(`${API_URL}/cuidadoras/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            cargarCuidadoras();
        } else {
            alert("No se pudo eliminar.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}

// 4. ACTUALIZAR LA TARIFA GLOBAL
async function actualizarTarifa() {
    const nuevaTarifa = parseFloat(document.getElementById('inputTarifa').value);

    try {
        const response = await fetch(`${API_URL}/config/tarifa`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ valor: nuevaTarifa })
        });

        if (response.ok) {
            alert("¡Tarifa de todas las cuidadoras actualizada!");
            cargarCuidadoras();
        } else {
            alert("Error al guardar la tarifa.");
        }
    } catch (error) {
        console.error("Error:", error);
    }
}