$(function(){
	$(document).click(function(e) {
		if (!$(e.target).is(".dropdown")&&!$(e.target).is(".caret"))
			$(".dropdown ul").slideUp();
	});
	if($(document).width()>748){
		$(".search").focus(function(){
			$(this).animate({width:"300px"});
		});
		$(".search").blur(function(){
			$(this).animate({width:"174px"});
		});
	}
	
	$(document).on("input", ".int-only", function(){
		var reg = /^[0-9]*$/;
		var _txt = $(this).val();
		if (reg.test(_txt)) {

		} else {
			$(this).val(0);
		}
	});
});
dialog.alert = function(content){
	var d = dialog({
		title : '提示',
		content : content,
		width : '200px',
		okValue : '确定',
		ok : function(){}
	});
	d.showModal();
}
function logout() {
	$.ajax({
		type : "post",
		url : "op/login/logout",
		dataType : 'json',
		error : function() {
			alert("通信错误");
		},
		success : function(data) {
			if (data.success) {
				location.href="/";
			} else {
				var d = dialog({
					content : "登出失败"
				});
				d.showModal();
				setTimeout(function() {
					d.close().remove();
				}, 1000);
			}
		}
	});
}