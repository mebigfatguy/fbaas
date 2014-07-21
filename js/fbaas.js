
$(document).ready(function() {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	$.getJSON(href + "rest/findbugs/text", function(data) {
		$( 'title' ).append(data.title);
		$( 'h1' ).append(data.title);
		$( 'h4' ).append(data.description);
		$( '#groupId' ).val(data.groupId);
		$( '#artifactId' ).val(data.artifactId);
		$( '#version' ).val(data.version);
		$( '#email' ).val(data.email);
	});
});

function findbugs(groupId, artifactId, version, email) {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	href = href + "rest/findbugs/run/" + groupId + "/" + artifactId + "/" + version + "/" + email;
	$.getJSON(href, function(data) {
	});
}