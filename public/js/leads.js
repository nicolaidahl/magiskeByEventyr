$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list li:nth-child(n+2)').click( function(){
		$('.lead-list li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		loadLead($(this).attr('model-id'));
	});
})

function loadLead(id){
	jsRoutes.controllers.InternalLead.getLead(id).ajax({
		success: function(lead) {
			//Update fields and elements on the page to reflect the selection
			//Common elements
			$('#lead-name').text("Lead: " + lead.name);
			$('.lead-id').val(lead.id);
			$('.lead-image').attr('src', lead.imageFile);
			$('.lead-story').text(lead.story);
			$('.lead-audio').attr('src', lead.soundFile);
			//TODO: Consider going with $('audio')[0].pause()/load() if no other audio tags are introduced then 
			$('.lead-player').each(function(key, value){
				$(value)[0].pause();
				$(value)[0].load();
			});
			//Image tab
			$('#image-anchoring').text(lead.anchoring)
			//Story tab
			
			//Audio tab
			
			//Approve
			adjustContentPos();
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	})
}
