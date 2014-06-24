
$(document).ready(function() {
	var href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
	$.getJSON(href + "rest/findbugs/text", function(data) {
		$( 'title' ).append(data.title);
		$( 'h1' ).append(data.title);
		$( 'h4' ).append(data.description);
		$( '#groupIdTitle' ).append(data.getGroupId());
		$( '#artifactTitle' ).append(data.getArtifactId());
		$( '#version' ).append(data.getVersion());
		$( '#email' ).append(data.getEmail());
	});
});

function findbugs(groupId, artifactId, version, email) {
	
}