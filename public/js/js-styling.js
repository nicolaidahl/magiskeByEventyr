function adjustContentPos(){
	if($('#approve')){
		setTimeout(function(){
			//Adjust admin button positions
			var yes = $('#approve #yes');
			//Parse height from css (as the button is using the box-sizing attribute .height isn't accurate)
			var yesHeight = parseFloat(yes.css('height'));
			$(yes.closest('.row-fluid').prev('.row-fluid')[0]).animate({ paddingBottom: ($('#approve img').height() / 2) - (yesHeight / 2) }, 200);
		}, 200);
	}
	setTimeout(function(){
		//Adjust audio position
		var imgHeight = 0;
		$('.lead-image').each(function(idx, value){
			//All images are the same - just take the one that's shown
			imgHeight = Math.max(imgHeight, $(value).height());
		})
		var audio = $('audio');
		//Audio is 30 px high, so reduce the offset by 15px
		audio.animate({ marginTop: (imgHeight / 2) - (15)}, 200);
	}, 200)
}