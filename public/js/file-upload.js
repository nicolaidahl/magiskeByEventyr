$(function(){
	var wrapper = $('<div/>').css({height:0,width:0,'overflow':'hidden'});
	var fileInput = $(':file').wrap(wrapper);

	fileInput.change(function(){
		$this = $(this);
		$($this.closest('div').prev('.file')[0]).text($this.val());
	})

	$('.file').click(function(){
		$($(this).next('div').children(':file')[0]).click();
	}).show();
});
