function reinit(json) {
    heatmap.setOptions({ data: []});
    heatmap.setOptions({ data: getPointsNew(json)});
}
    
function reinitMarker(json) {
	heatmap.setOptions({ data: []});
	return setMarkers(json);
}

var map, heatmap;
var circle, rectangle, polygon;
// var infoWindow;

function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 13,
		center: {lat: 37.775, lng: -122.434},
		mapTypeId: 'roadmap'
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
			var infoWindow = new google.maps.InfoWindow();
			infoWindow.setPosition(pos); // infoWindow.setContent('Locatia ta.');
			map.setCenter(pos);
		}, function() {
			handleLocationError(true, infoWindow, map.getCenter());
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false, infoWindow, map.getCenter());
	}
	
	var input = document.getElementById('pac-input');
	var searchBox = new google.maps.places.SearchBox(input);
	map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

	// Bias the SearchBox results towards current map's viewport.
	map.addListener('bounds_changed', function() {
		searchBox.setBounds(map.getBounds());
	});

	//var markers = [];
	// Listen for the event fired when the user selects a prediction and retrieve
	// more details for that place.
	searchBox.addListener('places_changed', function() {
	var places = searchBox.getPlaces();

	if (places.length == 0) {
	return;
	}

	// For each place, get the icon, name and location.
	var bounds = new google.maps.LatLngBounds();
	places.forEach(function(place) {
	if (!place.geometry) {
	console.log("Returned place contains no geometry");
	return;
	}

	if (place.geometry.viewport) {
	// Only geocodes have viewport.
	bounds.union(place.geometry.viewport);
	} else {
	bounds.extend(place.geometry.location);
	}
	});
	map.fitBounds(bounds);
	});

	var drawingManager = new google.maps.drawing.DrawingManager({
	// drawingMode: google.maps.drawing.OverlayType.MARKER,
	drawingControl: true,
	drawingControlOptions: {
	position: google.maps.ControlPosition.TOP_CENTER,
	drawingModes: [google.maps.drawing.OverlayType.RECTANGLE],
	drawingModes: ['circle', 'rectangle'] // all options 'marker', 'circle', 'polygon', 'polyline', 'rectangle'
	},
	circleOptions: {
		fillColor: '#ffff00',
		fillOpacity: 0.1,
		strokeWeight: 5,
		clickable: false,
		editable: true,
		zIndex: 1
	}
	});

	google.maps.event.addListener(drawingManager, 'circlecomplete', onCircleComplete);
	google.maps.event.addListener(drawingManager, 'rectanglecomplete', onRectangleComplete);
	function onCircleComplete(shape) {
        if (shape == null || (!(shape instanceof google.maps.Circle))) return;

        circle = setDrowingToolNull(circle);

        circle = shape;
        console.log('radius', circle.getRadius());
        // console.log('radius', circle.getRadius());
        // console.log('lat', circle.getCenter().lat());
        // console.log('lng', circle.getCenter().lng());
    }

    function onRectangleComplete(shape) {
        if (shape == null || (!(shape instanceof google.maps.Rectangle))) return;

        rectangle = setDrowingToolNull(rectangle);
        console.log("rect " );
        rectangle = shape;
        console.log('ne lat: ',  rectangle.getBounds());
        // console.log('getNorthEast lat: ',  rectangle.getBounds().getNorthEast().lat());
        // console.log('getNorthEast lng: ',  rectangle.getBounds().getNorthEast().lng());
        // console.log('getSouthWest lat: ',  rectangle.getBounds().getSouthWest().lat());
        // console.log('getSouthWest lng: ',  rectangle.getBounds().getSouthWest().lng());
    }

    var setDrowingToolNull = function(shape) {
    	console.log("aici");
    	if (rectangle != null) {
            rectangle.setMap(null);
            rectangle = null;
        }
        if (circle != null) {
            circle.setMap(null);
            circle = null;
        }
        return null;
    }
	drawingManager.setMap(map);
		

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
console.log("chart din c: ",map);



