
$(document).ready(function() {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	$.getJSON(href + "rest/findbugs/text", function(data) {
		$( 'title' ).append(data.title);
		$( 'h1' ).append(data.title);
		$( 'h4' ).append(data.description);
		$( '#groupId' ).val(data.groupId);
		$( '#artifactId' ).val(data.artifactId);
		$( '#version' ).val(data.version);
		$( '#category' ).val(data.category);
		$( '#type' ).val(data.type);
		$( '#message' ).val(data.message);
		$( '#fieldmethod' ).val(data.fieldMethod);
		$( '#location' ).val(data.location);
	});
});

function findbugs(groupId, artifactId, version) {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	href = href + "rest/findbugs/run/" + encodeURIComponent(groupId.trim()) + "/" + encodeURIComponent(artifactId.trim()) + "/" + encodeURIComponent(version.trim());
	$.getJSON(href, function(data) {
		$( 'h3' ).text(data.status);
		$( '#results' ).addClass('show').removeClass('hide');
		
		$('#bugs tbody > tr').remove();
		jQuery.each( data.bugs, function( i, val ) {
			var row = '<tr>' +
			          '<td>' + val.category + '</td>' + 
			          '<td>' + val.type + '</td>' +
			          '<td>' + val.className + '</td>' +
			          '<td>' + (typeof(val.methodName) === null ? val.fieldName : val.methodName) + '</td>' +
			          '<td>' + (val.lineStart + '-' + val.lineEnd) + '</td>' +
			          '</tr>';
			$('#bugs > tbody:last').append(row);
		});
	});
}