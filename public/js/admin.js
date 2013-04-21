$(function(){
	//Prepare the toggle
	$('#admin-approve-mode-toggle').click(function(){
		if($(this).hasClass('btn-success')) { //On
			$(this).removeClass('btn-success');
			$(this).addClass('btn-danger');
			$(this).text('Skift til editerings mode');
			$('#create').slideUp(400);
			setTimeout(function(){
				$('#approve').slideDown(400);
				adjustContentPos();
			},400);
		} else { //Off
			$(this).removeClass('btn-danger');
			$(this).addClass('btn-success');
			$(this).text('Skift til godkendelses mode');
			$('#approve').slideUp(400);
			setTimeout(function(){
				$('#create').slideDown(400);
			},400);
		}
	})
})

function adjustContentPos(){
	if($('#approve')){
		setTimeout(function(){
			//Adjust audio position
			var audio = $('#approve audio');
			audio.animate({ marginTop: ($('#approve img').height() / 2) - (audio.height() / 2)}, 200);
			//Adjust button position
			//Adjust yes-button position
			var yes = $('#approve #yes');
			yes.animate({ marginTop: ($('#approve img').height() / 2) - (yes.height() / 2)}, 200);
			//Adjust no-button position
			var no = $('#approve #no');
			no.animate({ marginTop: ($('#approve img').height() / 2) - (no.height() / 2)}, 200);
		}, 200);
	}
}