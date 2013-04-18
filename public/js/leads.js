$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list li:nth-child(n+2)').click( function(){
		$('.lead-list li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		loadLead($(this).attr('model-id'));
	});
})

function loadLead(id){
	jsRoutes.controllers.InternalFairyTale.getLead(id).ajax({
		success: function(lead) {
			//Update fields and elements on the page to reflect the selection
			//Common elements
			$('#lead-name').text("Lead: " + lead.name);
			$('#lead-id').val(lead.id);
			$('#lead-image').attr('src', lead.imageFile);
			$('#lead-story').text(lead.story);
			$('#lead-audio').attr('src', lead.audioFile);
			//Picture tab
			
			//Story tab
			
			//Audio tab
			var audio = $('#main-player');
			audio[0].pause();
			audio[0].load();
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	})
}
