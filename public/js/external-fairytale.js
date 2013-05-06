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
				$('#forward').text('Fortsæt');
			}
			moveTo(currentLead - 1);
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
			moveTo(currentLead + 1);
		}
	});
});

function moveTo(leadWithPriority){
	$('.lead-image').attr('src', leads[leadWithPriority].imageFile);
	$('.jp-jplayer').jPlayer("setMedia", {
        mp3: leads[leadWithPriority].soundFile
    });
	$('.player-title').text(leads[leadWithPriority].name)
	currentLead = leadWithPriority;
}