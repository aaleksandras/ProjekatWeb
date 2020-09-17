let appId=null;
let apartmentId = null;
let text = null;
let rate = null;


function load(){
	$.ajax({
		url: 'rest/reservation/myReservations',
		type: 'get',
		success: function(res) {
			if(res==null){		
			}else {
			
				for(g of res){
				ispisiRezervacije(g);	
			} 
			}
		}
		
	});
	
	
			
}


function ispisiRezervacije(u){
	appId = u.apartmentId;
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
	
	let forma = $('<form id="komntar" class="form"><label >Komentar:</label><br><input type="text" name="kom" id="kom" required><label >Ocjena:</label><br><input type="number" name="ocj" id="ocj" required> <input type="submit" name="submit" value="Add">  </form>');
	
	let aKomentarisi = $('<a href="">Komentarisi apartman</a>');
	aKomentarisi.click(function(event){
		event.preventDefault();
		let id = u.id;
		td2.append(forma);
		
	});
	
	 
	$('#komntar').submit(function(event){
		event.preventDefault()
			alert('ziv samm formaa')
			
		 apartmentId = $('#apartmani').val()
	 text = $('#kom').val()
		rate = $('#ocj').val()

	
		if(text == "" || rate == "" ){
			alert('Fill fields...')
			return
		}
		

		})
	
	if(u.status==="CREATED" || u.status==="ACCEPTED"){
		td2.append(aOdustani);
	}
	
	if(u.status==="REJECTED" || u.status==="ENDED"){
		td2.append(aKomentarisi);
	}
	
	
	
		tr.append(td2);
	
		$('table#guestRes tbody').append(tr);
}


$("document").ready(function(){

	var user = localStorage.getItem("user");
	
	$.ajax({
		type:"POST",
		url: "rest/comment/add",
		data: JSON.stringify({
			text: text,
			rate: parseInt(rate),
		
			apartmentId: appId,
		
		
		}),
		contentType:"application/json",
		success: function(apartment){
			alert('crated sucessfully..') },
			error: function(error){
				console.log(error)
				alert('Some error occurred...')
			}
		})
	
	 
	
})



