var map, heatmap;
var circle, rectangle, polygon;

function reinit(json) {
	console.log("json mapppp", json);
    heatmap.setOptions({ data: []});
    heatmap.setOptions({ data: getPointsNew(json)});
}
    
function reinitMarker(json) {
	heatmap.setOptions({ data: []});
	return setMarkers(json);
}
// var infoWindow;
function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		zoom: 13,
		center: {lat: 37.775, lng: -122.434},
		mapTypeId: 'roadmap',
		scrollwheel: false
	});
	heatmap = new google.maps.visualization.HeatmapLayer({
		data: getPoints(1, 24),
		map: map
	});
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
	drawingModes: ['circle', 'rectangle', 'polygon'] // all options 'marker', 'circle', 'polygon', 'polyline', 'rectangle'
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
	google.maps.event.addListener(drawingManager, 'polygoncomplete', onPolygonComplete);

	function onCircleComplete(shape) {
        if (shape == null || (!(shape instanceof google.maps.Circle))) return;

        circle = setDrowingToolNull(circle);

        circle = shape;
        console.log('radius', circle.getRadius());
        var lat=circle.getCenter().lat();
        var lng=circle.getCenter().lng();
        // var d = Math.sqrt( (lat-=47.080646)*lat + (lng-=21.9242443)*lng )*6378;
        // console.log("DISTANTA: "+d);
  //       var rad = function(x) {
		//   return x * Math.PI / 180;
		// };

		//   var R = 6378137; // Earth’s mean radius in meter
		//   var dLat = rad(lat - 47.080646);
		//   var dLong = rad(lng - 21.9242443);
		//   var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		//     Math.cos(rad(lat)) * Math.cos(rad(lng)) *
		//     Math.sin(dLong / 2) * Math.sin(dLong / 2);
		//   var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		//   var d = R * c;
		//   console.log("dddddddddddddddddddddu: "+d);

        // console.log('radius', circle.getRadius());340100894.6077962
        // console.log('lat', circle.getCenter().lat());340515740.54884803
        // console.log('lng', circle.getCenter().lng());
        /////////////
        console.log('changed');
    makeAjaxCall();
        /////////////
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

        ////////
        console.log('changed');
    makeAjaxCall();
        ////////
    }

    function onPolygonComplete(shape) {
        if (shape == null || (!(shape instanceof google.maps.Polygon))) return;

        polygon = setDrowingToolNull(polygon);
        console.log("rect " );
        polygon = shape;
        console.log('polygon ne lat: ',  polygon.getPath());
        // console.log('getNorthEast lat: ',  rectangle.getBounds().getNorthEast().lat());
        // console.log('getNorthEast lng: ',  rectangle.getBounds().getNorthEast().lng());
        // console.log('getSouthWest lat: ',  rectangle.getBounds().getSouthWest().lat());
        // console.log('getSouthWest lng: ',  rectangle.getBounds().getSouthWest().lng());
        makeAjaxCall();
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
        if (polygon != null) {
            polygon.setMap(null);
            polygon = null;
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
	console.log("json getPointsNew", json);
	console.log("json rectangle", rectangle);
	console.log("json circle", circle);
	console.log("json polygon", polygon);
	var result = [];
	var geometry;
	if(circle)
		json.forEach(function(pointheatmap){
			if(pointheatmap.lat !=" " && pointheatmap.lng !=" ")
			{
				var GoogleMapPoint = new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng);
				if (google.maps.geometry.spherical.computeDistanceBetween(GoogleMapPoint, circle.getCenter()) <= circle.getRadius()) {
				    console.log('=> is in searchArea');
				    result.push(GoogleMapPoint);
				} else {
				    console.log('=> is NOT in searchArea');
				}
				
			}
			// var rectangleContains = circle.containsLocation(pointheatmap, rectangle);
			console.log("json rectangle 2", rectangle);
			console.log("json circle 2", circle);
			console.log("json polygon 2", polygon);
			// result.push(new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng));
			console.log(pointheatmap);
		})
	var getPolygonBounds= function(polygon) {
			var paths = polygon.getPaths();
			var bounds = new google.maps.LatLngBounds();
			paths.forEach(function(path) {
				var ar = path.getArray();
				for(var i = 0, l = ar.length;i < l; i++) {
					bounds.extend(ar[i]);
				}
			});
			console.log("bounds",bounds);
			return bounds;
		};

	if(polygon) {
		json.forEach(function(pointheatmap){
			if(pointheatmap.lat !=" " && pointheatmap.lng !=" ")
			{
				var GoogleMapPoint = new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng);
				if (google.maps.geometry.poly.containsLocation(GoogleMapPoint, polygon)) {
				    console.log('=> is in searchArea');
				    result.push(GoogleMapPoint);
				} else {
				    console.log('=> is NOT in searchArea');
				}
				
			}
			// var rectangleContains = circle.containsLocation(pointheatmap, rectangle);
			console.log("json rectangle 2", rectangle);
			console.log("json circle 2", circle);
			console.log("json polygon 2", polygon);
			// result.push(new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng));
			console.log(pointheatmap);
		})

	}

	if(rectangle) {
		json.forEach(function(pointheatmap){
			if(pointheatmap.lat !=" " && pointheatmap.lng !=" ")
			{
				var GoogleMapPoint = new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng);
				if (rectangle.getBounds().contains(GoogleMapPoint)) {
				    console.log('=> is in searchArea');
				    result.push(GoogleMapPoint);
				} else {
				    console.log('=> is NOT in searchArea');
				}
				
			}
			// var rectangleContains = circle.containsLocation(pointheatmap, rectangle);
			console.log("json rectangle 2", rectangle);
			console.log("json circle 2", circle);
			console.log("json polygon 2", polygon);
			// result.push(new google.maps.LatLng(pointheatmap.lat, pointheatmap.lng));
			console.log(pointheatmap);
		})
	}
	return result;
}

function setMarkers(json) {
	var markers=[];
	json.forEach(function(pointheatmap) {
		var marker = new google.maps.Marker({
			position: { 
				lat: pointheatmap.lat, 
				lng: pointheatmap.lng 
			},
			map: map
		});
		markers.push(marker);
	});
	return markers;
}
console.log("chart din c: ",map);


