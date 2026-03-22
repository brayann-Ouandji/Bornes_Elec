/**
 * 
 */
var map = L.map('map').setView([46.603354, 1.888334], 6);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap'
}).addTo(map);

var routingControl = null;
var userLatLng = null; // on stocke la position de l'utilisateur pour faciliter le tracé de l'itinéraire

var userIcon = L.icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/64/64113.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

var bornesIcon = L.icon({
    iconUrl: 'images/icone.jpg',
    iconSize: [28, 28],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
})

var markersCluster = L.markerClusterGroup();
map.addLayer(markersCluster);

function afficherStations(stations) {
    markersCluster.clearLayers();
	
	if (routingControl) {
	    map.removeLayer(routingControl);
	    routingControl = null;
	}
	
    stations.forEach(function(station) {
        if (station.lat == null || station.lon == null) return;

        var marker = L.marker([station.lat, station.lon], { icon: bornesIcon });

        // On ajoute les prises disponibles dans un tableau
        var prises = [];
        if (station.prise_ef) prises.push("E/F");
        if (station.prise_t2) prises.push("Type 2");
        if (station.prise_ccs) prises.push("Combo CCS");
        if (station.prise_chademo) prises.push("CHAdeMO");
        var prisesStr = prises.length > 0 ? prises.join(", ") : "Non renseigné";

        // On ajoute le traif soit il est gratuit soit il ne l'est pas
        var tarifStr = station.gratuit
            ? "Gratuit"
            : (station.tarif || "Non renseigné");

        var popupContent =
            "<div style='min-width:220px'>" +
            "<strong style='font-size:14px'>" + (station.nom_station || "Station inconnue") + "</strong><br>" +
            "<span style='color:#666'> " + (station.adresse || "") + (station.commune ? ", " + station.commune : "") + "</span>" +
            "<hr style='margin:6px 0'>" +
            "<b>Puissance :</b> " + (station.puissance != null ? station.puissance + " kW" : "N/A") + "<br>" +
            "<b>Nb de prises :</b> " + (station.nb_prises || "?") + "<br>" +
            "<b>Types :</b> " + prisesStr + "<br>" +
            "<b>Tarif :</b> " + tarifStr + "<br>" +
            "<b>Accès :</b> " + (station.acces || "N/A") + "<br>" +
            "<b>Horaires :</b> " + (station.horaires || "N/A") + "<br>" +
            "<hr style='margin:6px 0'>" +
            "<button onclick='lancerItineraire(" + station.lat + "," + station.lon + ")' " +
            "style='width:100%;padding:6px;background:#2196F3;color:white;border:none;border-radius:4px;cursor:pointer'>" +
            "Y aller</button>" +
            "</div>";

        marker.bindPopup(popupContent, { maxWidth: 280 });
        markersCluster.addLayer(marker);
    });
}

function chargerStations(minPuissance, gratuit) {
    if (minPuissance === undefined) minPuissance = 0;
    if (gratuit === undefined) gratuit = false;
	
	if (routingControl) {
	    map.removeLayer(routingControl);
	    routingControl = null;
	}
	
    var priseEf = document.getElementById("filter-ef").checked;
    var priseT2 = document.getElementById("filter-t2").checked;
    var priseCcs = document.getElementById("filter-ccs").checked;
    var priseChademo = document.getElementById("filter-chademo").checked;
    var acces = document.getElementById("filter-acces").value;
    var paiementCb = document.getElementById("filter-cb").checked;

    var url = "http://127.0.0.1:8080/Bornes_Elec/api/stations"
        + "?minPuissance=" + encodeURIComponent(minPuissance)
        + "&gratuit=" + encodeURIComponent(gratuit)
        + "&priseEf=" + encodeURIComponent(priseEf)
        + "&priseT2=" + encodeURIComponent(priseT2)
        + "&priseCcs=" + encodeURIComponent(priseCcs)
        + "&priseChademo=" + encodeURIComponent(priseChademo)
        + "&acces=" + encodeURIComponent(acces)
        + "&paiementCb=" + encodeURIComponent(paiementCb);

    console.log("URL envoyée =", url);

    fetch(url)
        .then(function(response) {
            if (!response.ok) throw new Error("Erreur HTTP : " + response.status);
            return response.json();
        })
        .then(function(stations) {
            console.log("Stations reçues :", stations);
            afficherStations(stations);
        })
        .catch(function(error) {
            console.error("Erreur :", error);
            alert("Impossible de charger les bornes : " + error.message);
        });
}

document.getElementById("btn-filter").addEventListener("click", function() {
    var minPuissance = document.getElementById("filter-power").value;
    var gratuit = document.getElementById("filter-free").checked;
    chargerStations(minPuissance, gratuit);
});

document.getElementById("btn-search-address").addEventListener("click", function() {
    var query = document.getElementById("search-address").value.trim();
    if (!query) {
        alert("Veuillez entrer une adresse ou une ville.");
        return;
    }

    var url = "https://nominatim.openstreetmap.org/search?format=json&q="
        + encodeURIComponent(query) + "&limit=1&countrycodes=fr";

    fetch(url)
        .then(function(res) { return res.json(); })
        .then(function(results) {
            if (!results || results.length === 0) {
                alert("Adresse introuvable. Essayez avec un nom de ville.");
                return;
            }
            var lat = parseFloat(results[0].lat);
            var lon = parseFloat(results[0].lon);
            map.setView([lat, lon], 13);
        })
        .catch(function(error) {
            alert("Erreur lors de la recherche : " + error.message);
        });
});

document.getElementById("btn-geoloc").addEventListener("click", function() {
    if (!navigator.geolocation) {
        alert("La géolocalisation n'est pas supportée par votre navigateur.");
        return;
    }

    navigator.geolocation.getCurrentPosition(
        function(position) {
            var lat = position.coords.latitude;
            var lon = position.coords.longitude;

            userLatLng = L.latLng(lat, lon);

            map.setView([lat, lon], 13);

            L.marker([lat, lon], { icon: userIcon })
                .addTo(map)
                .bindPopup("Vous êtes ici")
                .openPopup();
        },
        function(error) {
            console.error("Erreur de géolocalisation :", error);
            alert("Impossible de récupérer votre position.");
        }
    );
});

function lancerItineraire(lat, lon) {
    if (!userLatLng) {
        alert("Veuillez d'abord activer la géolocalisation (bouton 'Autour de moi').");
        return;
    }

    // On supprime l'itinéraire précédent
    if (routingControl) {
        map.removeLayer(routingControl);
        routingControl = null;
    }

    var startLon = userLatLng.lng;
    var startLat = userLatLng.lat;

    var url = "https://router.project-osrm.org/route/v1/driving/"
        + startLon + "," + startLat + ";"
        + lon + "," + lat
        + "?overview=full&geometries=geojson";

    console.log("URL OSRM :", url);

    fetch(url)
        .then(function(res) {
            if (!res.ok) throw new Error("Erreur HTTP : " + res.status);
            return res.json();
        })
        .then(function(data) {
            console.log("Réponse OSRM :", data);

            if (!data.routes || data.routes.length === 0) {
                alert("Aucun itinéraire trouvé.");
                return;
            }

            var coords = data.routes[0].geometry.coordinates;

            // OSRM renvoie [lon, lat], Leaflet veut [lat, lon]
            var latlngs = coords.map(function(c) {
                return [c[1], c[0]];
            });

            routingControl = L.polyline(latlngs, {
                color: '#2196F3',
                weight: 5,
                opacity: 0.8
            }).addTo(map);

            map.fitBounds(routingControl.getBounds(), { padding: [40, 40] });
            map.closePopup();

            // Distance et durée
            var distanceKm = (data.routes[0].distance / 1000).toFixed(1);
            var dureMin = Math.round(data.routes[0].duration / 60);
            alert("Itinéraire : " + distanceKm + " km — environ " + dureMin + " min");
        })
        .catch(function(error) {
            console.error("Erreur OSRM :", error);
            alert("Impossible de calculer l'itinéraire : " + error.message);
        });
}

chargerStations();