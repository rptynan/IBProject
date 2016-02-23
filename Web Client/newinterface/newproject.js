$(document).ready(function(){
	
		var dD;
		var pH;
		var tL = $("#trendList");
		var tH = $("#trendHeading");
		var pL;
		var wF = $("#wikiFrame");
		var eD = $("#editsDiv");
		var tD = $("#tweetsDiv");
		var twH;
		var twL;
		var dDdom;
		var trendList = [];
		var pageList = [];
		var tweetList = [];
		var cTrend;
		var cPage;
		var cState = "Wikipedia";
		var trendIndex = -1;
		var lTrend;
		eD.hide();
		tD.hide();
		
		
		
		
		var updateTrends = function(){
			$.get("TwikfeedServlet?Type=Trends").done(function(data, textStatus) {
			
			alert(data);
		
			trendList = $.parseJSON(data);
		
			tL.empty();
			
			
			
			
		
		
			var i;
			for(i=0; i < trendList.length; i++){
				tL.append("<li><a>" + trendList[i].name + "</a></li>");
			}
			}, "text");
			
		}
		
		var updatePages = function(event){
			
			cTrend = $(event.target).parent();
			if (cTrend[0].nodeName == "LI"){
				if(trendIndex >= 0){
					lTrend.removeClass("active");
					lTrend.html("<a>" + trendList[trendIndex].name + "</a>");
				}
				//Now make a get request to get the list of page name
				trendIndex = cTrend.index();
				
				cTrend.html("<a href=\"#\">"+trendList[trendIndex].name+"<span class=\"sr-only\">(current)</span></a>");
				cTrend.addClass("active");
				/*
				$.get("TwikfeedServlet?Type=Articles&id="+trendList[trendIndex].id).done(function(data, textStatus) {
					alert(data);
		
					pageList = $.parseJSON(data);
					pL.empty();
					tH.html(cTrend.html()+ "<br>related Wikipedia pages");
			
				
					var i;
					for(i=0; i < pageList.length; i++){
						pL.append("<li>" + pageList[i].title + "</li>");
					}
				}, "text");
				$.get("TwikfeedServlet?Type=Tweets&id="+trendList[trendIndex].id).done(function(data, textStatus) {
					alert(data);
		
					tweetList = $.parseJSON(data);
					twH.html(trendList[trendIndex].name + "Tweets");
					twL.empty();
					var i;
					for(i=0; i < tweetList.length; i++){
						twL.append("<li>" + tweetList[i].content + "<br>" + tweetList[i].time + "</li>");
					}
					
					
				}, "text");
				*/
				lTrend = cTrend;
			}
		}
		
		var updatePage = function(event){
			cPage = $(event.target);
			
			if (cPage[0].nodeName == "LI"){
				var pageIndex = cPage.index();
				alert(pageIndex);
			
				wF.get(0).src = pageList[pageIndex].url;
				
			}
		}
		updateTrends();
		(tL.get(0)).onclick = updatePages;
		
	});
		
		
	