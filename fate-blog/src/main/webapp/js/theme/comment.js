$(function(){
	$(".up").click(function(){
		var obj = $(this);
		$.ajax({
			type : "post",
			url : "op/theme/comment/vote",
			data : {
				type : obj.attr("data-type"),
				guid : obj.parent().attr("guid"),
				voteType : 1
			},
			dataType : 'json',
			error:function(data){
				if(data.status==404)
					dialog.alert("请求地址不存在");
				else if(data.status==500)
					dialog.alert("系统内部错误");
				else if(data.status==200){
					location.reload();
				}
				else dialog.alert("通信异常");
			},
			success : function(data) {
				if(data.success){
					obj.children(".votes").html(data.up);
					obj.parent().attr("data-original-title","★ "+data.up+" ☆ "+data.down);
				}else{
					dialog.alert(data.message);
				}
			}
		});
	});
	
	$(".down").click(function(){
		var obj = $(this);
		$.ajax({
			type : "post",
			url : "op/theme/comment/vote",
			data : {
				type : obj.attr("data-type"),
				guid : obj.parent().attr("guid"),
				voteType : 2
			},
			dataType : 'json',
			error:function(data){
				if(data.status==404)
					dialog.alert("请求地址不存在");
				else if(data.status==500)
					dialog.alert("系统内部错误");
				else if(data.status==200){
					location.reload();
				}
				else dialog.alert("通信异常");
			},
			success : function(data) {
				if(data.success){
					obj.siblings().children(".votes").html(data.up);
					obj.parent().attr("data-original-title","★ "+data.up+" ☆ "+data.down);
				}else{
					dialog.alert(data.message);
				}
			}
		});
	});
	
	$(".comment-body").hover(function(){
		$(this).children(".comment-meta").children(".pm").toggle();
	})
	
	$(".reply").click(function(){
		$(".respondForm").remove();
		var content = "<div  class='respondForm'>"
					+ "<form action='' method='post' id='respondForm2'>"
					+ "<input type='hidden' name='commentGuid' value='"+$(this).attr("reply")+"' >"
					+ "<input type='hidden' name='toUid' value='"+$(this).attr("to")+"' >"
					+ "<textarea name='commentContent' class='form-control'></textarea>"
					+ "</form>"
					+ "<button class='btn btn-default' onclick=respond('2')>评论</button>"
					+ "</div>";
		$(this).parent().append(content);
		
	});
});
function vote(type,voteType){
	
}