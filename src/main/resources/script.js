$(function(){
	const api = "http://localhost:8080";
	$.getJSON(api+"/receipts", function(receipts){
		for(var i=0; i < receipts.length; i++) {
			var receipt = receipts[i];
			$(`<div class="row contentstyle">
				<div class="col-md-2"></div>
				<div class="col-md-8 receipt" id="${receipt.id}">
				<div class="col-xs-6  col-sm-3"><p>${receipt.created}</p></div>
				<div class="col-xs-6  col-sm-3"><p class="merchant">${receipt.merchantName}</p></div>
				<div class="col-xs-6  col-sm-3"><p class="amount">${receipt.amount}</p></div>
				<div class="col-xs-6  col-sm-3" id="tagInput">
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
		$.ajax({
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			type: 'PUT',
			url: "http://ec2-13-58-28-38.us-east-2.compute.amazonaws.com:8080/tags/"+tag+"?"+id,
			data: JSON.stringify(id),
			success: function(msg,status, jqXHR){
				alert(msg);
				$(a).replaceWith($('<label class="label label-success tagValue" onclick="dissociatetag(this,event,id)">$(tag)</label>'))
			},
			error: function(err){
				alert('Error');
			}
		});
	}
}
function dissociatetag(a,e,id){
	var tag = $(a).val();
	$.ajax({
			headers: { 
				'Accept': 'application/json',
				'Content-Type': 'application/json' 
			},
			type: 'PUT',
			url: "http://ec2-13-58-28-38.us-east-2.compute.amazonaws.com:8080/tags/"+tag,
			data: JSON.stringify(id),
			success: function(msg,status, jqXHR){
				$(a).attr("style", "display:none;");
			},
			error: function(err){
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
			url: "http://ec2-13-58-28-38.us-east-2.compute.amazonaws.com:8080/receipts",
			data: JSON.stringify(data),
			dataType: 'json',
			success: function (msg,status, jqXHR) {
				$("#merchant").val("");
				$("#amount").val("");
				alert(msg);
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

