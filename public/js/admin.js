$(function(){
	//Prepare the toggle
	$('#admin-approve-mode-toggle').click(function(){
		if($(this).hasClass('btn-success')) { //On
			$(this).removeClass('btn-success');
			$(this).addClass('btn-danger');
			$(this).text('Skift til editerings mode');
			$('#create').slideUp(400);
			//Select the first lead that needs to be approved
			if(nextUnapprovedLead != -1) {
				setTimeout(function(){
					$('#approve').slideDown(400);
					adjustContentPos();
				},400);
				$('#sortable-leads li[model-priority=' + nextUnapprovedLead + ']').click();
			} else {
				setTimeout(function(){
					$('#all-approved').fadeIn();
				}, 400)
			}
		} else { //Off
			$(this).removeClass('btn-danger');
			$(this).addClass('btn-success');
			$(this).text('Skift til godkendelses mode');
			$('#approve').slideUp(400);
			setTimeout(function(){
				$('#create').slideDown(400);
			},400);
		}
	});
})