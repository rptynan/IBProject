$(document).ready(function(){
	
		var dD = $("#dropdown");
		var pH = $("#placeHeading");
		var tL = $("#trendList");
		var tH = $("#trendHeading");
		var pL = $("#pageList");
		var wF = $("#wikiFrame");
		var eD = $("#editsDiv");
		var tD = $("#tweetsDiv");
		var dDdom = dD.get(0);
		var trendList = [];
		var pageList = [];
		var cTrend;
		var cPage;
		var cState = "Wikipedia";
		eD.hide();
		tD.hide();
		
		
		
		
		var updateTrends = function(){
			$.get("TwikfeedServlet", function(data, textStatus) {
			
			alert(data);
		
			trendList = $.parseJSON(data);
			pH.empty();
			tL.empty();
			pH.html(dDdom.options[dDdom.selectedIndex].value + " Trends");
			
			
			
			
		
		
			var i;
			for(i=0; i < trendList.length; i++){
				tL.append("<li>" + trendList[i].name + "</li>");
			}
			}, "text");
			
		}
		
		var updatePages = function(event){
			cTrend = $(event.target);
			if (cTrend[0].nodeName == "LI"){
				pL.empty();
				tH.html(cTrend.html()+ "<br>related Wikipedia pages");
				//Now make a get request to get the list of page name
				pageList = ["Computer Lab", "Doombar", "Marmite"];
				var i;
				for(i=0; i < pageList.length; i++){
					pL.append("<li>" + pageList[i] + "</li>");
				}
			}
		}
		
		var updatePage = function(event){
			cPage = $(event.target);
			if (cPage[0].nodeName == "LI"){
				wF.get(0).src = "https://en.wikipedia.org/wiki/Tetris";
				
			}
		}
		
		
			
		updateTrends();
		dD.change(updateTrends);
		(tL.get(0)).onclick = updatePages;	
		(pL.get(0)).onclick = updatePage;
		
		$("#bTweets").click(function(){
			if(cState != "Tweets"){
				wF.hide();
				eD.hide();
				tD.show();
				cState = "Tweets";
			}
		});
		$("#bEdits").click(function(){
			if(cState != "Edits"){
				wF.hide();
				eD.show();
				tD.hide();
				cState = "Edits";
			}
		});
		$("#bWiki").click(function(){
			if(cState != "Wikipedia"){
				wF.show();
				eD.hide();
				tD.hide();
				cState = "Wikipedia";
			}
		});
	});
		
		
	