/**
 * 
 */
$(document).ready(function(){
	//Check locale storage 
	var user = JSON.parse(localStorage.getItem('user'));
	console.log(user)
	
	$('#loggedIn').text(user.role+" : " +user.username)
	if(user.role!=0){
		$('#users').hide()
	}
	
	$('#check').text(user.username)
	


	function getAmenities(){
		$.ajax({
			type:"GET",
			url:"rest/amenity",
			contentType:"application/json",
			success:function(amenities){
				console.log(amenities)
				for(let amenity of amenities){
					addAmenity(amenity)
				}
			},
			error:function(){
				alert('error getting amenities')
			}
		})
	}

	function addAmenity(amenity){
		$("#checkboxes").append(`
		<div class="form-check">
				<input type="checkbox" class="form-check-input" id="${amenity.id}" name="amenities" value="${amenity.id}" >
				<label class="form-check-label" for="${amenity.id}">${amenity.name}</label>
		</div>
		`);
	}
})

