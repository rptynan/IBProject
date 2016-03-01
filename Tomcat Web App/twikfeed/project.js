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
	var eH = $("#editsHeader");
	var eL = $("#editsList");
	var dM = $("#dropdownMenu1");
	// initialise stuff
	var dDdom;
	var trendList = [];
	var pageList = [];
	var tweetList = [];
	var editList = [];
	var cTrend;
	var cPage;
	var cState = "Wikipedia";
	var trendIndex = -1;
	var pageIndex = -1;
	var lTrend;
	var lPage;
	var location = "UK";
	var pageSorting = 1;
	var trendSorting = 1;
	var trendObject = null;
	eD.hide();
	tD.hide();
	
	// Handle the input of custom trends
	$("#form").on('submit', function (e) {
		e.preventDefault();
		// Can't send spaces or # through the request
		var content = $("#input").val().replace(/\s+/g, '');
		content = content.replace("#","");
	
		$.get("TwikfeedServlet?Type=Custom&trend=" + content).done(function(data, textStatus) {
			if (parseInt(data) == 1){
				alert(content + " has been submitted as a custom trend. It will soon appear under the Custom location.");
			}else{
				alert("Sorry, your trend was not successfully submitted.");
			}
		}, "text");
	});
	
	
	// Handle startup or a selection of a new location
	var updateTrends = function(){
		$.get("TwikfeedServlet?Type=Trends&location=" + location + "&sorting=" + trendSorting).done(function(data, textStatus) {
			trendList = $.parseJSON(data);
			tL.empty();
			var i;
			for(i=0; i < trendList.length; i++){
				tL.append("<li><a href=\"#\">" + trendList[i].name + "</a></li>");
			}
		}, "text");
		
	}
	
		var updateTweets = function(){
		// Call to load tweets
		$.get("TwikfeedServlet?Type=Tweets&id="+trendObject.id).done(function(data, textStatus) {
			tweetList = $.parseJSON(data);
			twH.html("<strong>" + trendObject.name + " </strong><small>Tweets</small>");
			twL.empty();
			var i;
			for(i=0; i < tweetList.length; i++){
				twL.append("<li>" + tweetList[i].content + "<br> <small>" + tweetList[i].time + "</small> </li> <br>");
			}
			
			
		}, "text");
	}
	
	// Inner just makes the request to update pages
	var updatePagesInner = function(){
		// Call to load articles
		$.get("TwikfeedServlet?Type=Articles&id="+trendObject.id + "&sorting=" + pageSorting).done(function(data, textStatus) {
			pageList = $.parseJSON(data);
			pL.empty();
			// Set the trend header from the selected trend in the list
			tH.html(cTrend.find("a").html());
			var i;
			for(i=0; i < pageList.length; i++){
				pL.append("<li><a href=\"#\">" + pageList[i].title + "</a></li>");
			}
		}, "text");
	}
	

	
	
	// Handle the selection of a trend and calls UpdatePagesInner
	var updatePages = function(event){
		cTrend = $(event.target).parent();
		if (cTrend[0].nodeName == "LI"){
			pageIndex = -1;
			if(trendIndex >= 0){
				//reset old selected trend
				lTrend.removeClass("active");
				lTrend.html("<a href=\"#\">" + trendList[trendIndex].name + "</a>");
			}
			//Now make a get request to get the list of page name
			trendIndex = cTrend.index();
			trendObject = trendList[trendIndex];
			cTrend.html("<a href=\"#\">"+trendList[trendIndex].name+"<span class=\"sr-only\">(current)</span></a>");
			cTrend.addClass("active");
			// Inner function makes the requests
			updatePagesInner();
			updateTweets();
			lTrend = cTrend;
		}
	}
	// Handle the selection of an article.
	var updatePage = function(event){
		// Get the list element that has been selected.
		cPage = $(event.target).parent();
		
		if (cPage[0].nodeName == "LI"){
			if(pageIndex >= 0){
				// Reset old selected page.
				lPage.removeClass("active");
				lPage.html("<a href=\"#\">" + pageList[pageIndex].title + "</a>");
			}
			pageIndex = cPage.index();
			// Make the selected page in the list highlighted. 
			cPage.html("<a href=\"#\">" + pageList[pageIndex].title + "<span class=\"sr-only\">(current)</span></a>");
			cPage.addClass("active");
			// Change the URL of the iframe
			wF.get(0).src = pageList[pageIndex].url;
			lPage = cPage;
			// Now make call to update the edits
			$.get("TwikfeedServlet?Type=Edits&id="+pageList[pageIndex].id).done(function(data, textStatus) {
				editList = $.parseJSON(data);
				eH.html("<strong>" + pageList[pageIndex].title + " </strong><small>Edit Comments</small>");
				eL.empty();
				var i;
				for(i=0; i < editList.length; i++){
					eL.append("<li>" + editList[i].comment + "<br> <small>" + editList[i].time + "</small> </li> <br>");
				}
				
				
			}, "text");
			
		}

        // Switch to article view if not on it
		if(cState != "Wikipedia"){
			wF.show();
			eD.hide();
			tD.hide();
			cState = "Wikipedia";
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
		location = $(this).html();
		trendIndex = -1;
		updateTrends();
		
	});
	
	//Handlers for the refresh buttons
	$("#trendRefresh").click(function(){
		updateTrends();
	});
	$("#articleRefresh").click(function(){
		if (trendObject !== null){
			updatePagesInner();
			updateTweets();
		}
	});
	
	// Select the sorting method for the articles
	$("#pageRelevance").click(function(){
		pageSorting = 1;
		if (trendObject !== null) updatePagesInner();
	});
	$("#pagePopularity").click(function(){
		pageSorting = 2;
		if (trendObject !== null) updatePagesInner();
	});
	$("#pageRecency").click(function(){
		pageSorting = 3;
		if (trendObject !== null) updatePagesInner();
	});
	$("#pageControversy").click(function(){
		pageSorting = 4;
		if (trendObject !== null) updatePagesInner();
	});
	
	// Select the sorting method for the trends
	$("#trendPopularity").click(function(){
		trendSorting = 1;
		updateTrends();
	});
	$("#trendRecency").click(function(){
		trendSorting = 2;
		updateTrends();
	});
	
	
	// Make it happen
	updateTrends();
	(tL.get(0)).onclick = updatePages;
	(pL.get(0)).onclick = updatePage;
	
});
