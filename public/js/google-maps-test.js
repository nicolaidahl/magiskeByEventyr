var map = null;
var markers = [];

function initialize() {
	var mapOptions = {
		zoom: 6,
    	center: new google.maps.LatLng(56, 10),
    	mapTypeId: google.maps.MapTypeId.ROADMAP,
    	//zoomControl: false,
    	//scaleControl: false,
    	//scrollwheel: false,
    	//disableDoubleClickZoom: true,
	}
	map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	
	var marker = new google.maps.Marker({
	  position: map.getCenter(),
	  map: map,
	  title: 'Click to zoom'
	});
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