var leads = null;
var currentLead = null;

$(function(){
	//Init
	jsRoutes.controllers.Application.getLeads(fairyTaleId).ajax({ //fairyTaleId declared in template
		success: function(response) {
			leads = response;
			moveTo(0);
			var width = $(this).closest('.row-fluid').width();
			if (leads.length > 0) {
				$('#forward').fadeIn();
			} else {
				$('#back').fadeIn();
				$('#forward').fadeIn();
			}
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	});
	//Register button listeners
	$('#back').click(function(){
		if(currentLead != 0) {
			$(this).attr('disabled','disabled');
			if (currentLead == 1) { //Moving to first lead
				var width = $(this).closest('.row-fluid').width();
				$(this).fadeOut(400);
				setTimeout(function(){
					$('#forward').css({marginLeft : width / 100 * 49});
					$('#forward').animate({width: width, marginLeft: 0},400);
					$('#back').removeAttr('disabled');
				}, 450);
			} else {
				$(this).removeAttr('disabled');
			}
			if (currentLead == leads.length -1) { //Moving from last lead
				$('#forward').text('Næste');
			}
			var help = $('#help');
			if(help.is(':visible') && help.find('#help-close').is(':checked')) {
				help.slideUp(400);
				setTimeout(function() {
					moveTo(currentLead - 1);
				}, 400)
			} else {
				moveTo(currentLead - 1);
			}
		} 
	});
	$('#forward').click(function(){
		if ($(this).text() == 'Til forsiden') {
			window.location.replace('http://' + window.location.host);
		} else if(currentLead != leads.length - 1) {
			$(this).attr('disabled','disabled');
			if (currentLead == 0) { //Moving from first lead
				var width = $(this).closest('.row-fluid').width();
				$(this).animate({width: width / 100 * 49, marginLeft: width / 100 * 49}, 400)
				setTimeout(function(){
					$('#back').fadeIn(400);
					$('#forward').css({marginLeft : 0});
					$('#forward').removeAttr('disabled');
				}, 450);
			} else {
				$(this).removeAttr('disabled');
			}
			if (currentLead + 1 == leads.length - 1) { //Moving to last lead
				$(this).text('Til forsiden');
			}
			var help = $('#help');
			if(help.is(':visible') && help.find('#help-close').is(':checked')) {
				help.slideUp(400);
				setTimeout(function() {
					moveTo(currentLead + 1);
				}, 400);
			} else {
				moveTo(currentLead + 1);
			}
		}
	});
	$('.icon-help').click(function() {
		var help = $('#help');
		if(help.is(':visible')) {
			help.slideUp(400);
		} else {
			help.slideDown(400);
			setTimeout(function() {
				if (map) {
					updateDirectionalMarkers();
				} else {
					initializeMap();
				}
			}, 400);
		}
		
	})
});

function moveTo(leadWithPriority){
	$('.lead-image').attr('src', leads[leadWithPriority].imageFile);
	$('.jp-jplayer').jPlayer("setMedia", {
        mp3: leads[leadWithPriority].soundFile
    });
	$('.player-title').text(leads[leadWithPriority].name);
	currentLead = leadWithPriority;
	if($('#help').is(':visible')) {
		if (map) {
			updateDirectionalMarkers();
		} else {
			initializeMap();
		}
	}
}

//Help Map
var map = null;
var directionDisplay;
var directionsService = new google.maps.DirectionsService();

function initializeMap() {
	var mapOptions = {
		zoom: 6,
    	center: new google.maps.LatLng(55.6576, 12.5888),
    	mapTypeId: google.maps.MapTypeId.ROADMAP,
    	zoomControl: false,
    	scaleControl: false,
    	scrollwheel: false,
    	disableDoubleClickZoom: true,
    	streetViewControl: false
	}
	map = new google.maps.Map(document.getElementById("help-map"), mapOptions);
	directionsDisplay = new google.maps.DirectionsRenderer();
	directionsDisplay.setMap(map);
	updateDirectionalMarkers();
	enableGeoLocation();
}

function updateBounds() {
	var latlngbounds = new google.maps.LatLngBounds();
	if(currentMarker) {
		latlngbounds.extend(currentMarker.position);
	}
	if(verified.length > 0) {
		$(verified).each(function(key, lead){
			latlngbounds.extend(new google.maps.LatLng(lead.latitude, lead.longitude));
		})
	}
	map.setCenter(latlngbounds.getCenter());
	map.fitBounds(latlngbounds); 
	var listener = google.maps.event.addListener(map, "idle", function() { 
		if (map.getZoom() > 16) map.setZoom(16); 
		google.maps.event.removeListener(listener); 
	});
}

//Directional markers
var verified = null;
var markers = [];

function clearDirectionalMarkers() {
	for (var i = 0; i < markers.length; i++) {
        if (markers[i].directions) this.markers[i].directions.setMap(null);
        this.markers[i].setMap(null);
    }
	markers = [];
}

function updateDirectionalMarkers() {
	clearDirectionalMarkers();
	
	verified = [];
	if (leads[currentLead].latitude) {
		if (currentLead != 0 && leads[currentLead - 1].latitude) {
			verified.push(leads[currentLead - 1]);
			verified.push(leads[currentLead]);
			$('#help-text').text('Nedenfor kan du se ruten fra sidste ledetråd til nuværende');
		} else {
			verified.push(leads[currentLead]);
			$('#help-text').text('Nedenfor kan du se hvor eventyret begynder');
		}
	} else if (leads[currentLead + 1].latitude) {
		verified.push(leads[currentLead + 1]);
		$('#help-text').text('Nedenfor kan du se hvor eventyret begynder');
	}
	
	if (verified.length == 1) {
		directionsDisplay.setMap(null);
		directionsDisplay = new google.maps.DirectionsRenderer();
		directionsDisplay.setMap(map);
		var latLng = new google.maps.LatLng(verified[0].latitude, verified[0].longitude);
		var marker = new google.maps.Marker({
		  position: latLng,
		  map: map,
		  title: verified[0].name
		});
		markers.push(marker);
	} else if (verified.length > 1) {
		var start;
		var end;
		var waypts = [];
		
		$(verified).each(function(key, lead){
			var lat = lead.latitude;
			var lng = lead.longitude;
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
	        	updateBounds();
	        }
	    });
	}
	updateBounds();
}

//GeoLocation
var currentPosition = null;
var currentMarker = null;
var geoLocationEnabled = false;
var geoLocationTimeouts = 0;

function clearGeoLocationMarker() {
	if(currentMarker) {
		currentMarker.setMap(null);
		currentMarker = null;
	}
}

function enableGeoLocation() {
	//Try W3C Geolocation (Preferred)
	if(navigator.geolocation) {
		clearGeoLocationMarker();
		geoLocationEnabled = true;
		recursiveUpdateOwnPosition();
	} else { // Browser doesn't support Geolocation
		alert("Din browser understøtter ikke geolocation, så din position vil ikke blive vist på kortet.");
	}
}

function disableGeoLocation() {
	geoLocationEnabled = false;
	geoLocationTimeouts = 0;
	clearGeoLocationMarker();
	updateBounds();
}

function recursiveUpdateOwnPosition() {
	if(geoLocationEnabled) {
		navigator.geolocation.getCurrentPosition(function(position) {
			geoLocationTimeouts = 0;
			currentPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
			  if (currentMarker) {
				  currentMarker.setPosition(currentPosition);
			  } else {
				  currentMarker = new google.maps.Marker({
					  position: currentPosition,
					  map: map,
					  icon: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|00FF00'
				  });
			  }
			  updateBounds();
			setTimeout(function(){
				recursiveUpdateOwnPosition();
			}, 10000);
		  }, function() {
			  geoLocationTimeouts++;
			  if(geoLocationTimeouts > 3) {
				  disableGeoLocation();
				  alert("Kunne ikke finde din position. Så den vil ikke blive vist på kortet.");
			  } else {
				  setTimeout(function(){
					  recursiveUpdateOwnPosition();
				  }, 10000);
			  }
		  }, {timeout: 10000});
	}
}