$(document).ready(function(){
	
	
	// assign the elements shorter names
		var tL = $("#trendList");
		var tH = $("#trendHeading");
		var pL = $("#pageList");
		var wF = $("#wikiFrame");
		var eD = $("#editsDiv");
		var tD = $("#tweetsDiv");
		var twH = $("#tweetsHeader");
		var twL = $("#tweetsList");
		var dM = $("#dropdownMenu1");
		// initialise stuff
		var dDdom;
		var trendList = [];
		var pageList = [];
		var tweetList = [];
		var cTrend;
		var cPage;
		var cState = "Wikipedia";
		var trendIndex = -1;
		var pageIndex = -1;
		var lTrend;
		var lPage;
		eD.hide();
		tD.hide();
		
		
		
		// Handle startup or a selection of a new location
		var updateTrends = function(){
			$.get("TwikfeedServlet?Type=Trends").done(function(data, textStatus) {
			
		
			trendList = $.parseJSON(data);
		
			tL.empty();
			var i;
			for(i=0; i < trendList.length; i++){
				tL.append("<li><a href=\"#\">" + trendList[i].name + "</a></li>");
			}
			}, "text");
			
		}
		// Handle the selection of a trend
		var updatePages = function(event){
			
			cTrend = $(event.target).parent();
			if (cTrend[0].nodeName == "LI"){
				if(trendIndex >= 0){
					//reset old selected trend
					lTrend.removeClass("active");
					lTrend.html("<a href=\"#\">" + trendList[trendIndex].name + "</a>");
				}
				//Now make a get request to get the list of page name
				trendIndex = cTrend.index();
				
				cTrend.html("<a href=\"\">"+trendList[trendIndex].name+"<span class=\"sr-only\">(current)</span></a>");
				cTrend.addClass("active");
				// Call to load articles
				$.get("TwikfeedServlet?Type=Articles&id="+trendList[trendIndex].id).done(function(data, textStatus) {
		
		
					pageList = $.parseJSON(data);
					pL.empty();
					tH.html(cTrend.find("a").html());
			
				
					var i;
					for(i=0; i < pageList.length; i++){
						pL.append("<li><a href=\"#\">" + pageList[i].title + "</a></li>");
					}
				}, "text");
				// Call to load tweets
				$.get("TwikfeedServlet?Type=Tweets&id="+trendList[trendIndex].id).done(function(data, textStatus) {
	
		
					tweetList = $.parseJSON(data);
					twH.html(trendList[trendIndex].name + "Tweets");
					twL.empty();
					var i;
					for(i=0; i < tweetList.length; i++){
						twL.append("<li>" + tweetList[i].content + "<br>" + tweetList[i].time + "</li>");
					}
					
					
				}, "text");
				
				lTrend = cTrend;
			}
		}
		// Handle the selection of an article
		var updatePage = function(event){
			cPage = $(event.target).parent();
			
			if (cPage[0].nodeName == "LI"){
				if(pageIndex >= 0){
					//reset old selected page
					lPage.removeClass("active");
					lPage.html("<a href=\"#\">" + pageList[pageIndex].title + "</a>");
				}
				pageIndex = cPage.index();
	
				cPage.html("<a href=\"\">" + pageList[pageIndex].title + "<span class=\"sr-only\">(current)</span></a>");
				cPage.addClass("active");
			
				wF.get(0).src = pageList[pageIndex].url;
				lPage = cPage;
			}
		}
		// Handle changes between the views
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
		
		// Allow locations to be selected
		$('#dropDown li a').on('click', function(){
			dM.html($(this).html() + "\n<span class=\"caret\"></span>");
		});
		
		// Make it happen
		updateTrends();
		(tL.get(0)).onclick = updatePages;
		(pL.get(0)).onclick = updatePage;
		
	});
