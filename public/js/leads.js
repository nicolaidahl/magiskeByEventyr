$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list li:nth-child(n+2)').click( function(){
		$('.lead-list li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		loadLead($(this).attr('model-id'));
	});
	
	//Make leads sortable (JQuery-ui) and attach a stop listener
	$('#sortable-leads').sortable({
    	stop: function(event, ui){
    		var id = ui.item.attr('model-id');
    		$('#sortable-leads li').each(function(key, value){
    			if(id == $(value).attr('model-id')) {
    				//Change priority (zero indexed) to match position (non-zero indexed)
    				setLeadPriority(id, key - 1);
    			}	
    		});
    		updateMarkers();
    	}
    });
	
	$('#yes').click(function (){
		setLeadApproved($($('.lead-id')[0]).val());
	})
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
			
			//Adjust content position
			adjustContentPos();
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	})
}

function setLeadPriority(id, priority){
	jsRoutes.controllers.InternalLead.setLeadPriority(id, priority).ajax({
		success: function(leads) {
			for (var i = 0; i < leads.length; i++) {
			    $('#sortable-leads li[model-id=' + leads[i].id + ']').attr('model-priority', leads[i].priority);
			}
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	})
}

function setLeadApproved(id){
	jsRoutes.controllers.InternalLead.approveLead(id).ajax({
		success: function(response) {
			if (response.nextLeadPriority != -1) {
				$('#sortable-leads li[model-priority=' + response.nextLeadPriority + ']').click();
			} else {
				//Everything approved - do something funky! (Ask to publish?)
			}
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	});
}
