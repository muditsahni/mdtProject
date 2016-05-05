var chart; // global

//var test = window.location.search.substring(1);

var userid = document.cookie.valueOf("userid").split(";")[0];


var ip1 = document.cookie.valueOf("userid").split(";")[1];
var ip = ip1.split("=")[1];
//var userid = window.location.search.substring(1);
//alert(userid);

$(document).ready(function () {
	
	//doAjaxCall();
	data = [];
	createLiveChart(data);
	weeklyChart();
	monthlyChart();
	requestStats();
	
});
function requestStats() {
	$.ajax({
    url: 'http://'+ip+':47285/MdtProject/MDTProjectController?'+userid+'&view=baseline',
		beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Negotiate");
    },		
		success: function(data) {
				setStats(data);
		},
		cache: false
    });
}

function setStats(data) {	
	console.log(data);
        data= data +'';
	var stats = data.split("@");
	var maxHR = document.getElementById("maxHRValue");
	maxHR.innerHTML = stats[0];
	
	var minHR = document.getElementById("minHRValue");
	minHR.innerHTML = stats[1];
	
	var avgHR = document.getElementById("avgHRValue");
	avgHR.innerHTML = stats[2];
	
	var sdHR = document.getElementById("sdValue");
	sdHR.innerHTML = stats[3];
	
}

function requestData() {
	$.ajax({
    url: 'http://'+ip+':47285/MdtProject/MDTProjectController?'+userid+'&view=livefeed',
		beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Negotiate");
    },		
        success: function(point) {
                                                point = point + '';
						var str = point.split(",");
						var pt = [];
						pt.push(parseInt(str[0]));
						pt.push(parseInt(str[1]));
            var series = chart.series[0],
						shift = series.data.length > 20; // shift if the series is 
                                                 // longer than 20

            // add the point
            chart.series[0].addPoint(pt, true, shift);
            
            // call it again after one second
            setTimeout(requestData, 2000);    
        },
        cache: false
    });
}

function createLiveChart(data) {
		chart = new Highcharts.Chart({
        chart: {
            renderTo: 'containerlive',
            defaultSeriesType: 'spline',
            events: {
                load: requestData
            }
        },
        title: {
            text: 'Live heart Rate'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150,
            maxZoom: 20 * 1000
        },
        yAxis: {
            minPadding: 0.2,
            maxPadding: 0.2,
            title: {
                text: 'Heart Beats',
                margin: 80
            }
        },
        series: [{
            name: 'Life Heart Rate',
            data: []
        }]
    });
}


function weekRequestData() {
	$.ajax({
	 url: 'http://'+ip+':47285/MdtProject/MDTProjectController?'+userid+'&view=weekly',
		beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Negotiate");
    },		
        success: function(data) {
						var try1 = [];
						var pts = [];
						var keys = [];
                                                data = data + '';
						var timeSlots = data.split("@");
						for (var i=0; i<timeSlots.length; i++) {
							var keyValue = timeSlots[i].split("&");
							pts.push(parseInt(keyValue[1]));
							keys.push(keyValue[0]);
						}
            // add the point
            chartWeekly.xAxis[0].setCategories(keys);
					
						for (var j=0; j<keys.length; j++)
							{
								chartWeekly.series[0].addPoint([keys[j],pts[j]], true, false);
							}
						
						
						
        },
        cache: false
    });
}

function monthRequestData() {
	$.ajax({
	 url: 'http://'+ip+':47285/MdtProject/MDTProjectController?'+userid+'&view=monthly',
		beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Negotiate");
    },		
        success: function(data) {
						var try1 = [];
						var pts = [];
						var keys = [];
                                                data = data+'';
						var timeSlots = data.split("@");
						for (var i=0; i<timeSlots.length; i++) {
							var keyValue = timeSlots[i].split("&");
							pts.push(parseInt(keyValue[1]));
							keys.push(keyValue[0]);
						}
            // add the point
            chartMonthly.xAxis[0].setCategories(keys);
					
						for (var j=0; j<keys.length; j++)
							{
								chartMonthly.series[0].addPoint([keys[j],pts[j]], true, false);
							}						
        },
        cache: false
    });
}

function monthlyChart() {
	chartMonthly = new Highcharts.Chart({
        chart: {
            renderTo: 'containermonthly',
            defaultSeriesType: 'spline',
            events: {
                load: monthRequestData
            }
        },
        title: {
            text: 'Monthly Heart Rate'
        },
        subtitle: {
					text: 'Source: Fitbit Dataset',
					x: -20
				},
				xAxis: {
					categories: ['Jan', 'Feb', 'Mar']
				},
				yAxis: {
					title: {
						text: 'Temperature (°C)'
					},
					plotLines: [{
						value: 0,
						width: 1,
						color: '#808080'
					}]
				},
				tooltip: {
					valueSuffix: '°C'
				},
				legend: {
					layout: 'vertical',
					align: 'right',
					verticalAlign: 'middle',
					borderWidth: 0
				},
				series: [{
					name: 'Monthly Heart Rate',
					data: []
				}]
    });
}

function weeklyChart() {
	chartWeekly = new Highcharts.Chart({
        chart: {
            renderTo: 'containerweekly',
            defaultSeriesType: 'spline',
            events: {
                load: weekRequestData
            }
        },
        title: {
            text: 'Weekly Heart Rate'
        },
        subtitle: {
					text: 'Source: Fitbit Dataset',
					x: -20
				},
				xAxis: {
					categories: ['Jan', 'Feb', 'Mar']
				},
				yAxis: {
					title: {
						text: 'Temperature (°C)'
					},
					plotLines: [{
						value: 0,
						width: 1,
						color: '#808080'
					}]
				},
				tooltip: {
					valueSuffix: '°C'
				},
				legend: {
					layout: 'vertical',
					align: 'right',
					verticalAlign: 'middle',
					borderWidth: 0
				},
				series: [{
					name: 'Weekly Heart Rate',
					data: []
				}]
    });
}