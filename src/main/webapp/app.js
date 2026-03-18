/**
 * 
 */
// Initialisation de la carte (centrée sur la France)
const map = L.map('map').setView([46.603354, 1.888334], 6);


L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
}).addTo(map);