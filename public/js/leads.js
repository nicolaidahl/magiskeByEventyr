$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list li:nth-child(n+2)').click(function(){
		$('.lead-list li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		
		jsRoutes.controllers.InternalFairyTale.getLead($(this).attr('model-id')).ajax({
			success: function(lead) {
				//Update fields and elements on the page to reflect the selection
				//Name header
				$('#lead-name').text("Lead: " + lead.name);
				//Picture tab
				
				//Story tab
				$('#story-lead-id').val(lead.id);
				
				//Audio tab
				$('#audio-lead-id').val(lead.id);
				$('#audio-lead-picture').attr('src', lead.imageFile);
				$("#audio-lead-audio").attr("src", lead.soundFile);
				var audio = $('#main-player');
				audio[0].pause();
				audio[0].load();
			},
			error: function(data) {
				alert("An error occured: " + data);
			}
		
		})
	});
	
	//Select the first lead in the lead-list if there are any
	var first = $('.lead-list li:nth-child(2)');
	if (first) {
		first.click();
	}
})