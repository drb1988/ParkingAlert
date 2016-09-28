function reinit(json) {
    heatmap.setOptions({ data: []});
    heatmap.setOptions({ data: getPointsNew(json)});
}
    
function reinitMarker(json) {
	heatmap.setOptions({ data: []});
	return setMarkers(json);
}

var map, heatmap;

function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 13,
		center: {lat: 37.775, lng: -122.434},
		mapTypeId: 'terrain'
	});
	heatmap = new google.maps.visualization.HeatmapLayer({
		data: getPoints(1, 24),
		map: map
	});

	var infoWindow = new google.maps.InfoWindow({map: map});

	// Try HTML5 geolocation.
	if (navigator.geolocation) {
	navigator.geolocation.getCurrentPosition(function(position) {
	  var pos = {
	    lat: position.coords.latitude,
	    lng: position.coords.longitude
	  };

	  infoWindow.setPosition(pos);
	  infoWindow.setContent('Your location.');
	  map.setCenter(pos);
	}, function() {
	  handleLocationError(true, infoWindow, map.getCenter());
	});
	} else {
	// Browser doesn't support Geolocation
	handleLocationError(false, infoWindow, map.getCenter());
	}
}

function changeGradient() {
	var gradient = [
		'rgba(0, 255, 255, 0)',
		'rgba(0, 255, 255, 1)',
		'rgba(0, 191, 255, 1)',
		'rgba(0, 127, 255, 1)',
		'rgba(0, 63, 255, 1)',
		'rgba(0, 0, 255, 1)',
		'rgba(0, 0, 223, 1)',
		'rgba(0, 0, 191, 1)',
		'rgba(0, 0, 159, 1)',
		'rgba(0, 0, 127, 1)',
		'rgba(63, 0, 91, 1)',
		'rgba(127, 0, 63, 1)',
		'rgba(191, 0, 31, 1)',
		'rgba(255, 0, 0, 1)'
	]
	heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);
}

function changeRadius() {
	heatmap.set('radius', heatmap.get('radius') ? null : 20);
}

function changeOpacity() {
	heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);
}

// Heatmap data: 500 Points
function getPoints(startpointvalue, endpointvalue) {
	return []
}

function getPointsNew(json) {
	var result = [];
	json.forEach(function(pointheatmap){
		result.push(new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng));
	})
	return result;
}

function setMarkers(json) {
	var markers=[];
	json.forEach(function(pointheatmap) {
		var marker = new google.maps.Marker({
			position: { lat: pointheatmap.lat, lng: pointheatmap.lng },
			map: map
		});
		markers.push(marker);
	});
	return markers;
}



