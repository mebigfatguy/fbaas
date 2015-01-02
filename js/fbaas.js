
$(document).ready(function() {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	$.getJSON(href + "rest/findbugs/text", function(data) {
		$( 'title' ).append(data.title);
		$( 'h1' ).append(data.title);
		$( 'h4' ).append(data.description);
		$( '#groupId' ).val(data.groupId);
		$( '#artifactId' ).val(data.artifactId);
		$( '#version' ).val(data.version);
	});
});

function findbugs(groupId, artifactId, version) {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	href = href + "rest/findbugs/run/" + encodeURIComponent(groupId) + "/" + encodeURIComponent(artifactId) + "/" + encodeURIComponent(version);
	$.getJSON(href, function(data) {
		$( 'h3' ).text(data.status);
		$( '#results' ).addClass('show').removeClass('hide')
	});
}