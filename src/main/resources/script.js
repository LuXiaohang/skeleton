const api = "http://ec2-13-58-28-38.us-east-2.compute.amazonaws.com:8080";
//const api = "http://localhost:8080";
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

	function hideform(){
		console.log("i am in hideform");
		$("#addreceiptform").attr("style", "display:none;"); 
		$("#merchant").val("");

	}

