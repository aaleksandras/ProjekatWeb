function load(){
	$.ajax({
		url: 'rest/reservation/myReservations',
		type: 'get',
		success: function(res) {
			if(res==null){		
			}else {
				alert('ziv sam');
				for(g of res){
				ispisiRezervacije(g);	
			} 
			}
		}
		
	});
	
	
			
}


function ispisiRezervacije(u){

	let tr = $('<tr></tr>');
	
	let td2 = $('<td></td>');	
	
	let apartmentId = $('<p>ID apartmana '+u.apartmentId+'</p>');
	let startDate = $('<p>Pocetni datum: '+u.startDate.toString()+' </p>');
	let nights = $('<p>Broj noci: '+u.nights.toString()+'</p>');
	let price = $('<p>=Cijena: '+u.price.toString()+'</p>');
	let status = $('<p>=Status: '+u.status+'</p>');
	
	td2.append(apartmentId).append(startDate).append(nights).append(price).append(status);
	
	let aOdustani = $('<a href="">Odustani</a>');
	aOdustani.click(function(event){
		event.preventDefault();
		let id = u.id;
		$.ajax({
			url:'rest/reservation/odustani/'+id,
			type:'PUT',
			success: function(){
				$.get({
					url : 'rest/reservation/myReservations',
					success : function(res) {
						window.location.reload(true);
						for(acc of res){
							ispisiRezervacije(g);	
						}
					}
				});
			}
		});
	});
	
	if(u.status==="CREATED" || u.status==="ACCEPTED"){
		td2.append(aOdustani);
	}
	
	
	
		tr.append(td2);
	
		$('table#guestRes tbody').append(tr);
}


