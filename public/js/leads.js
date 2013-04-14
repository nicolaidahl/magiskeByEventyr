$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list li:nth-child(n+2)').click(function(){
		$('.lead-list li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		
		jsRoutes.controllers.InternalFairyTale.getLead($(this).attr('model-id')).ajax({
			success: function(lead) {
				//Update fields and elements on the page to reflect the selection
				$('#lead-name').text("Lead: " + lead.name);
			},
			error: function(data) {
				alert("An error occured: " + data);
			}
		
		})
	});
	
	//Select the first lead in the lead-list
	$('.lead-list li:nth-child(2)').click();
})