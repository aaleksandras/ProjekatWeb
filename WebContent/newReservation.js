var idd = "Volvo";

function load(){
	$.ajax({
		url: 'rest/apartment/getAll',
		type: 'get',
		success: function(apartmani) {
			if(apartmani==null){		
			}else {
				
				dodajApartmane(apartmani);	
			} 
		}
		
	});
	
	
	
	
		
}

function datumi(){
	$.ajax({
		url: 'rest/apartment/getDates',
		type: 'get',
		data: {
			id : app.id
		},
		success: function(apartmani) {
			if(apartmani==null){		
			}else {
				
				dodajDatume(apartmani);	
			} 
		}
		
	});
}

function dodajApartmane(apartmani){
	 var list = apartmani == null ? [] : (apartmani instanceof Array ? apartmani : [ apartmani ]);
		
	 $.each(apartmani, function(index, k) {
		 
			$("#apartmani").append("<option>"+k.id+"</option>");
	 
	 });
}

function dodajDatume(apartmani){
	 var list = apartmani == null ? [] : (apartmani instanceof Array ? apartmani : [ apartmani ]);
		
	 $.each(apartmani, function(index, k) {
		 
			$("#datumi").append("<option>"+k+"</option>");
	 
	 });
}

$("document").ready(function(){
	//Get all apartments types and amenities

	
	  $("#apartmani").on("change", function() {
			app = $('#apartmani').val();
			alert('ds'+app)
		  $.ajax({
				url: 'rest/apartment/getDates'+app,
				type: 'get',
				success: function(apartmani) {
					if(apartmani==null){
					
					}else {
						alert(apartmani)
						alert('ssss')
						dodajDatume(apartmani);	
					} 
				}
				
			});
		  });
	
})


