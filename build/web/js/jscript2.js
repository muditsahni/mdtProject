var userid = document.cookie.valueOf("userid").split(";")[0];


var ip1 = document.cookie.valueOf("userid").split(";")[1];
var ip = ip1.split("=")[1];

$(document).ready(function () {	
	//doAjaxCall();
	requestNominees();
        
});

function requestNominees() {
	$.ajax({
    url: 'http://'+ip+':47285/MdtProject/MDTProjectController?'+userid+'&view=nominees',
		beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Negotiate");
    },		
		success: function(data) {
				printNominees(data);
		},
		cache: false
    });
}

function printNominees(data) {
	if (data != null) {
		var nomineeList = data.split("&");
		var list = document.getElementById("nomineeList");
		var htmlString1	= '<li class="list-group-item">'
										+ 	'<a href="">'
										+			'<img src="img/26115.jpg" width="50" height="50">'
										+		'</a>'
										+   '<p class="pull-right">';
		var htmlString2 = '</p><br>'
										+   '<p class="pull-right">';
		var htmlString3 = '</p>'
										+	'<a href="">';
		var htmlString4 = '</a>'
										+	'</li>';
		for (var i = 0; i < nomineeList.length; i++) {
			var info = nomineeList[i].split("#");
			list.innerHTML += htmlString1 + info[1] + htmlString2 + info[2] + htmlString3 + info[0] + htmlString4;
		}	
	}
}

function addNominee(form) {
	console.log(form);
	$.ajax({
		url: 'http://'+ip+':47285/MdtProject/MDTProjectController?',
		type: 'post',
		data: {"name":form[0].value, "emailid":form[1].value, "telno": form[2].value+form[3].value, "userid":userid.split("=")[1]},
		success: function (data, status) {
			console.log("Success!!");
			if(status === 200) {
				printNominees(data);
			}
		}
	});
}
