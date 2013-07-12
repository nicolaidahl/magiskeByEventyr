var currentAudio = null;
var originalAnchoring = null;
var originalStory = null;

$(function(){
	//Attach listeners to all leads in the lead-list
	$('.lead-list > li:nth-child(n+2)').click( function(e){
		if (e.originalEvent && (e.originalEvent.srcElement.className == 'dropdown-toggle' || e.originalEvent.srcElement.className == 'caret' || e.originalEvent.srcElement.className == 'dropdown' || e.originalEvent.srcElement.localName == 'a')) {
			return true;
		}
		$('.lead-list > li:nth-child(n+2)').removeClass('active');
		$(this).addClass('active');
		loadLead($(this).attr('model-id'));
		
	});
	
	//Attach listeners to all dropdowns
	/*$('.lead-list .dropdown').click(function(e){
		$('.lead-list > li:nth-child(n+2)').removeClass('active');
		$(this).closest('li').addClass('active');
		loadLead($(this).closest('li').attr('model-id'));
	});*/
	
	//Make leads sortable (JQuery-ui) and attach a stop listener
	$('#sortable-leads').sortable({
    	stop: function(event, ui){
    		var id = ui.item.attr('model-id');
    		$('#sortable-leads > li').each(function(key, value){
    			if(id == $(value).attr('model-id')) {
    				//Change priority (zero indexed) to match position (non-zero indexed)
    				setLeadPriority(id, key - 1);
    			}	
    		});
    		updateMarkers();
    	}
    });
	
	$('#yes').click(function (){
		if ($($('.lead-image')[0]).attr('src') != "" && currentAudio != "") {
			setLeadApproved($($('.lead-id')[0]).val());
		} else {
			alert("En ledetråd kan ikke godkendes uden billede og/eller lyd.");
		}
		
	})
})

function loadLead(id){
	jsRoutes.controllers.InternalLead.getLead(id).ajax({
		success: function(lead) {
			//Update fields and elements on the page to reflect the selection
			$('#lead-name').text("Ledetråd: " + lead.name);
			$('.lead-id').val(lead.id);
			$('.lead-image').attr('src', lead.imageFile);
			$('.lead-story').val(lead.story);
			originalStory = lead.story;
			$('.jp-jplayer').jPlayer("setMedia", {
                mp3: lead.soundFile
            });
			currentAudio = lead.soundFile;
			$('#image-anchoring').val(lead.anchoring);
			originalAnchoring = lead.anchoring;
			if(lead.approved) {
				$('#yes').val('Godkendt');
				$('#yes').attr('disabled', true);
				$('#no').val('Fortryd');
			} else {
				$('#yes').val('Ja');
				$('#yes').removeAttr('disabled');
				$('#no').val('Nej');
			}
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
			$('#sortable-leads li[model-id=' + id + ']').append('<div class="icon-approved pull-right" /></div>')
			if (response.nextLeadPriority != -1) {
				$('#sortable-leads > li[model-priority=' + response.nextLeadPriority + ']').click();
			} else {
				$('#approve').fadeOut();
				setTimeout(function(){
					$('#all-approved').fadeIn();
				}, 400)
			}
		},
		error: function(data) {
			alert("An error occured: " + data);
		}
	});
}
