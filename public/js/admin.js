$(function(){
	//Prepare the toggle
	$('#admin-approve-mode-toggle').click(function(){
		if($(this).hasClass('btn-success')) { //On
			$(this).removeClass('btn-success');
			$(this).addClass('btn-danger');
			$(this).text('Skift til editerings mode');
			$('#create').slideUp(400);
			//Select the first lead that needs to be approved
			setTimeout(function(){
				$('#fairytale-administration').slideDown(400);
				setTimeout(function(){
					if(nextUnapprovedLead != -1) {
						$('#approve-lead').fadeIn(100);
						adjustContentPos();
						$('#sortable-leads li[model-priority=' + nextUnapprovedLead + ']').click();
					} else {
						$('#all-approved').fadeIn(100);
					}
				}, 400);
			},400);
		} else { //Off
			$(this).removeClass('btn-danger');
			$(this).addClass('btn-success');
			$(this).text('Skift til admin mode');
			$('#all-approved').fadeOut(100);
			$('#approve-lead').fadeOut(100);
			$('#fairytale-administration').slideUp(400);
			setTimeout(function(){
				$('#create').slideDown(400);
			},400);
		}
	});
	
	//Update with credits
	$('#admin-update-with-credits').click(function(){
		jsRoutes.controllers.InternalFairyTale.updateWithCredits(adminFairyTaleId, $('textarea[name="admin-credits"]').val()).ajax({
			success: function(response) {
				alert("great success");
			},
			error: function(data) {
				alert("An error occured: " + data);
			}
		});
	});
	
	$('#admin-update-info').click(function(){
		jsRoutes.controllers.InternalFairyTale.updateWithInfo(adminFairyTaleId, $('input[name="admin-name"]').val(), $('input[name="admin-due-date"]').val(), $('textarea[name="admin-briefing"]').val()).ajax({
			success: function(response) {
				alert("great success");
			},
			error: function(data) {
				alert("An error occured: " + data);
			}
		});
	});
})