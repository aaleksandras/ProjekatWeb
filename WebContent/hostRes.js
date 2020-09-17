function load(){
	$.ajax({
		url: 'rest/reservation/hostReservations',
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
	
	let aPrihvati = $('<a href="">Prihvat</a>');
	aPrihvati.click(function(event){
		event.preventDefault();
		let id = u.id;
		$.ajax({
			url:'rest/reservation/prihvati/'+id,
			type:'PUT',
			success: function(){
				$.get({
					url : 'rest/reservation/hostReservations',
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
	
	let aOddbij = $('<a href="">Odbij</a>');
	aOddbij.click(function(event){
		event.preventDefault();
		let id = u.id;
		$.ajax({
			url:'rest/reservation/odbij/'+id,
			type:'PUT',
			success: function(){
				$.get({
					url : 'rest/reservation/hostReservations',
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
	
	let aZavrsena = $('<a href="">Zavrsena</a>');
	aZavrsena.click(function(event){
		event.preventDefault();
		let id = u.id;
		$.ajax({
			url:'rest/reservation/zavrsi/'+id,
			type:'PUT',
			success: function(){
				$.get({
					url : 'rest/reservation/hostReservations',
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
	
	
	
	if(u.status==="CREATED"){
		td2.append(aPrihvati);
	}
	
	if(u.status==="CREATED" || u.status==="ACCEPTED"){
		td2.append(aOddbij);
	}

	
	
	
		tr.append(td2);
	
		$('table#hostRes tbody').append(tr);
}