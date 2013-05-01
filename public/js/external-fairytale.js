var leads = null;
var currentLead = null;

$(function(){
	$('#forward').hide();
	$('#back').hide();
	//Init
	jsRoutes.controllers.Application.getLeads(fairyTaleId).ajax({ //fairyTaleId declared in template
		success: function(response) {
			leads = response;
			moveTo(0);
			$('#forward').fadeIn();
			$('#back').fadeIn();
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	});
	//Register button listeners
	$('#back').click(function(){
		if(currentLead != 0) {
			moveTo(currentLead - 1);
		} else {
			alert('Dette er den første ledtråd!');
		}
	});
	$('#forward').click(function(){
		if(currentLead != leads.length - 1) {
			moveTo(currentLead + 1);
		} else {
			alert('Dette er den sidste ledtråd!');
		}
	})
});

function moveTo(leadWithPriority){
	$('.lead-image').attr('src', leads[leadWithPriority].imageFile);
	$('.lead-audio').attr('src', leads[leadWithPriority].soundFile);
	$('.lead-player').each(function(key, value){
		$(value)[0].pause();
		$(value)[0].load();
	});
	currentLead = leadWithPriority;
}