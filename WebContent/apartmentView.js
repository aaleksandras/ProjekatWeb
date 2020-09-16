function load(){
	$.ajax({
		url: 'rest/apartment/myAppa',
		type: 'get',
		success: function(apartmani) {
			if(apartmani==null){		
			}else {
				alert('ziv sam');
				for(g of apartmani){
				ispisiApartmane(g);	
			} 
			}
		}
		
	});
	
	
			
}


function ispisiApartmane(u){

	let tr = $('<tr></tr>');
	
	let td2 = $('<td></td>');	
	let type = $('<p>Tip apartmana '+u.apartmentType+'</p>');
	let rooms = $('<p>Broj soba: '+u.rooms.toString()+' </p>');
	let guests = $('<p>Broj gostiju: '+u.guests.toString()+'</p>');
	let location = $('<p>=Ulica: '+u.location.address.street+'</p>');
	let location2 = $('<p>=Broj: '+u.location.address.number+'</p>');
	let location1 = $('<p>=Grad: '+u.location.address.city+'</p>');
	td2.append(rooms).append(type).append(guests).append(location).append(location2).append(location1);
	
	
		tr.append(td2);
	
		$('table#apartmani tbody').append(tr);
		
}