//const api = "http://ec2-13-58-28-38.us-east-2.compute.amazonaws.com:8080";
const api = "http://localhost:8080";
$(function(){
	
	$.getJSON(api+"/receipts", function(receipts){
		for(var i=0; i < receipts.length; i++) {
			var receipt = receipts[i];
			var tagHTML = '';
			k=i+1
			$.each(receipt.tags, function (j, tag) {
				tagHTML += '<p><button class="btn btn-success tagValue" id=\''+k+tag+'\' receiptid=\''+k+'\' tag=\''+tag+'\' onclick="dissociatetag(this,event)">' + tag + '</button></p>';
			})
			$(`<div class="row contentstyle">
				<div class="col-md-2"></div>
				<div class="col-md-8 receipt" id="${receipt.id}">
				<div class="col-xs-6  col-sm-3"><p>${receipt.created}</p></div>
				<div class="col-xs-6  col-sm-3"><p class="merchant">${receipt.merchantName}</p></div>
				<div class="col-xs-6  col-sm-3"><p class="amount">${receipt.amount}</p></div>
				<div class="col-xs-6  col-sm-3" id="tagInput">
				${tagHTML}
				<p><button class="btn btn-info add-tag" onclick="addInputTag(this,event)">ADD+</button></p>
				</div>
				</div>
				</div>`).appendTo($("#receiptList"));


		}
	})
	
})

function takephoto(){
	var video = document.querySelector('video');
	var photo = document.querySelector('img');
	var canvas = document.querySelector('canvas');
	var ctx = canvas.getContext('2d');
	$(".photocanvas").attr("style", "display:block;"); 
	var width = 320;    
	var height = 0;
	height = video.videoHeight / (video.videoWidth/width);
	canvas.width = width;
	canvas.height = height;
	ctx.drawImage(video, 0, 0, width, height);
	var dataURL = canvas.toDataURL("");
	img = dataURL.split(",")[1]
	//console.log(img)
	$.ajax({
		headers: { 
			'Accept': 'application/json',
			'Content-Type': 'text/plain' 
		},
		type: "POST",
		url: api+"/images",
		data: img,
		dataType: 'json',
		success: function (msg,status, jqXHR) {
			console.log(msg);
			var merchant = msg["merchantName"];
			var amount = msg["amount"];
			hidecamera();
			$("#merchant").val(merchant);
			$("#amount").val(amount);
			showform();

		}
	});
}

function addInputTag(a,b){
	//alert($(a).parent().parent().prop("tagName"))
	$('<input type="text" class="form-control tag_input" placeholder="input tag" onkeypress="puttag(this,event)" />').prependTo($(a).parent().parent());
}
function puttag(a,e){
	if(e.keyCode==13){
		var tag = $(a).val();
		var id = $(a).parent().parent().attr("id");
		if($("#"+id+tag).length>0){
			$(a).remove();
			dissociatetag($("#"+id+tag),"hh");
		}
		else{
			$.ajax({
				headers: { 
					'Accept': 'application/json',
					'Content-Type': 'application/json' 
				},
				type: 'PUT',
				url: api+"/tags/"+tag,
				data: JSON.stringify(id),
				success: function(msg){
					$(a).replaceWith($('<p><button class="btn btn-success tagValue" id=\''+id+tag+'\' receiptid=\''+id+'\' tag=\''+tag+'\' onclick="dissociatetag(this,event)">' + tag + '</button></p>'));
				},
				error: function(jqXHR,err){
					alert(jqXHR.responseText);
				}
			});
		}}
	}
	function dissociatetag(a,e){
		var tag = $(a).attr("tag");
		var id = $(a).attr("receiptid");
		b = $(a).parent();
		$.ajax({
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			type: 'PUT',
			url: api+"/tags/"+tag,
			data: JSON.stringify(id),
			success: function(msg,status, jqXHR){
				$(a).remove();
				$(b).remove();
			},
			error: function(jqXHR,err){
				alert('Error');
			}
		});
	}
	function numbertest(){
		$("#amount").val($("#amount").val().replace(/[^0-9.]/g,''));
	}

	function postreceipt(){
		var merchant = $("#merchant").val();
		var amount = $("#amount").val();
		if (merchant==""){
			alert("Merchant Name is required");
		}
		else{
			data={"merchant":merchant,"amount":amount};
			console.log("I am ready to post")
			$.ajax({
				headers: { 
					'Accept': 'application/json',
					'Content-Type': 'application/json' 
				},
				type: "POST",
				url: api+"/receipts",
				data: JSON.stringify(data),
				dataType: 'json',
				success: function (msg,status, jqXHR) {
					$("#merchant").val("");
					$("#amount").val("");
					$(`<div class="row contentstyle">
						<div class="col-md-2"></div>
						<div class="col-md-8 receipt" id='`+msg+`'>
						<div class="col-xs-6  col-sm-3"><p class="merchant">time</p></div>
						<div class="col-xs-6  col-sm-3"><p class="merchant">${merchant}</p></div>
						<div class="col-xs-6  col-sm-3"><p class="amount">${amount}</p></div>
						<div class="col-xs-6  col-sm-3" id="tagInput">
						<p><button class="btn btn-info add-tag" onclick="addInputTag(this,event)">ADD+</button></p>
						</div>
						</div>
						</div>`).appendTo($("#receiptList"));

				//alert(msg);
			}
		});

		}
	}


	function showform(){
		console.log("i am in showform");
		$("#addreceiptform").attr("style", "display:block;"); 
	};

	function showcamera(){
		console.log("i am in showcamera");
		$("#takephoto").attr("style", "display:block;"); 
		if (navigator.mediaDevices === undefined) {
			navigator.mediaDevices = {};
		}
		if (navigator.mediaDevices.getUserMedia === undefined) {
			navigator.mediaDevices.getUserMedia = function(constraints) {
				var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
				if (!getUserMedia) {
					return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
				}
				return new Promise(function(resolve, reject) {
					getUserMedia.call(navigator, constraints, resolve, reject);
				});
			}
		}
		window.URL = (window.URL || window.webkitURL || window.mozURL || window.msURL);
		var mediaOpts = {
			audio: false,
			video: true
		}
		function successFunc(stream) {
			var video = document.querySelector('video');
			if ("srcObject" in video) {
				video.srcObject = stream
			} else {
				video.src = window.URL && window.URL.createObjectURL(stream) || stream
			}
			video.play();
		}
		function errorFunc(err) {
			alert(err.name);
		}

		navigator.getUserMedia(mediaOpts, successFunc, errorFunc);
	};

	function hidecamera(){
		console.log("i am in hidecamera");
		$("#takephoto").attr("style", "display:none;"); 
		$(".photocanvas").attr("style", "display:none;");
		//window.location.reload();
	}
	function hideform(){
		console.log("i am in hideform");
		$("#addreceiptform").attr("style", "display:none;"); 
		$("#merchant").val("");
		$("#amount").val("");

	}

