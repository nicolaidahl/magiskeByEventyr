var map = null;
var markers = [];
var directionDisplay;
var directionsService = new google.maps.DirectionsService();

function initialize() {
	var mapOptions = {
		zoom: 6,
    	center: new google.maps.LatLng(55.6576, 12.5888),
    	mapTypeId: google.maps.MapTypeId.ROADMAP,
    	zoomControl: false,
    	scaleControl: false,
    	scrollwheel: false,
    	disableDoubleClickZoom: true,
	}
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	directionsDisplay = new google.maps.DirectionsRenderer();
	directionsDisplay.setMap(map);
	updateMarkers();
}

function clearMarkers() {
	for (var i = 0; i < markers.length; i++) {
        if (markers[i].directions) this.markers[i].directions.setMap(null);
        this.markers[i].setMap(null);
    }
	markers = [];
}

function updateMarkers() {
	clearMarkers();
	var list = $('#sortable-leads > li'); 
	if (list.length == 2) { //Only one element
		var marker = new google.maps.Marker({
		  position: new google.maps.LatLng($($(list)[1]).attr('model-lat'), $($(list)[1]).attr('model-lng')),
		  map: map,
		  title: $($(list)[1]).text()
		});
		markers.push()
	} else if (list.length > 2) {
		var start;
		var end;
		var waypts = [];
		
		verified = [];
		
		$(list).each(function(key, value){
			if($(value).attr('model-id')) { //This is a lead list-item and not the header
				var lat = $(value).attr('model-lat');
				var lng = $(value).attr('model-lng');
				if (lat && lng && lat != "" && lng != "") {
					verified.push(value);
				}
			}
		});
		
		$(verified).each(function(key, value){
			var lat = $(value).attr('model-lat');
			var lng = $(value).attr('model-lng');
			if (key == 0) { 
				start = new google.maps.LatLng(lat, lng);
			} else if (key == verified.length - 1) { //End
				end = new google.maps.LatLng(lat, lng);
			} else { //Waypoint
				waypts.push({
		            location: new google.maps.LatLng(lat, lng),
		            stopover:true
	            });
			}
		});
		
		var request = {
            origin: start,
            destination: end,
            waypoints: waypts,
            optimizeWaypoints: false,
            travelMode: google.maps.DirectionsTravelMode.WALKING
        };
        directionsService.route(request, function(response, status) {
            if (status == google.maps.DirectionsStatus.OK) {
                directionsDisplay.setDirections(response);
                var route = response.routes[0];
            }
        });
	}
}

function fitMapViewPort(latlngs) {
	if (map) {
		if (markers.length > 0) {
			var maxLat = -90; //Lowest possible latitude
			var minLat = 90; //Highest possible latitude
			var maxLng = -180; //Lowest possible longitude
			var minLng = 180; //Highest possible longitude
			for(var i = 0; i < markers.length; i++){
				maxLat = Math.max(maxLat, markers[i].getPostition().lat());
				minLat = Math.min(minLat, markers[i].getPostition().lat());
				maxLng = Math.max(maxLng, markers[i].getPostition().lng());
				minLng = Math.min(minLng, markers[i].getPostition().lng());
			}
			map.fitBounds(new google.maps.LatLngBounds(new google.maps.LatLng(minLat, minLng),new google.maps.LatLng(maxLat, maxLng)));
		} else {
			map.setCenter(new google.maps.LatLng(56, 10)); //Denmark
			map.setZoom(6);
		}
	}
}