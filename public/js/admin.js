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
			//Parse height from css (as the button is using the box-sizing attribute .height isn't accurate)
			var yesHeight = parseFloat(yes.css('height'));
			$(yes.closest('.row-fluid').prev('.row-fluid')[0]).animate({ paddingBottom: ($('#approve img').height() / 2) - (yesHeight / 2) }, 200);
			
		}, 200);
	}
}