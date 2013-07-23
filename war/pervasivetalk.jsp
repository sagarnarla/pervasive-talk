<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="guestbook.PervasiveTalk" %>
<%@ page import="com.opentok.api.API_Config" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<html>
<% 
	String user = request.getParameter("user").toString();
	String url = request.getParameter("url").toString();
	
	 //out.println( String.valueOf(user));
	// out.println( String.valueOf(url));
	 
	PervasiveTalk obj = new PervasiveTalk();
	String sessionid = obj.GetSessionID(url);		
	String opentoken = obj.GetToken(sessionid, user);
		
	//out.println(sessionid);
	//out.println(opentoken);
%>


<head>
<script src="http://static.opentok.com/webrtc/v2.0/js/TB.min.js" ></script>
<script type="text/javascript">

	var sessionid = "<%=sessionid%>";
	var apikey = "<%=API_Config.API_KEY%>";
	var token = "<%=opentoken%>";
	
	//Open in new tab
	//chrome.tabs.create({active:true},function(tab){
    //updateTab(tab.id);
    //});
	
	TB.setLogLevel(TB.DEBUG);
	
	var session = TB.initSession(sessionid);
	
	session.addEventListener('sessionConnected', sessionConnectedHandler);
	session.addEventListener('streamCreated', streamCreatedHandler);
	session.connect(apikey,token);
	
	function sessionConnectedHandler (event) {
		var publisher = TB.initPublisher(apikey, 'myPublisherDiv');
		session.publish(publisher);
	     subscribeToStreams(event.streams);
	     
	}
	
	function streamCreatedHandler(event) {
	    subscribeToStreams(event.streams);
	}

	function subscribeToStreams(streams) {
	    for (var i = 0; i < streams.length; i++) {
	        var stream = streams[i];
	        if (stream.connection.connectionId != session.connection.connectionId) {
	            
	        	var div = document.createElement('div');
	        	div.setAttribute('id', 'stream' + stream.streamId);
	            document.body.appendChild(div);
	            
	        	session.subscribe(stream,div.id);
	            
	        }
	    }	    
	}
	
	

</script>
</head>
  <body>
  </body>
</html>