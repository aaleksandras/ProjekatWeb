package services;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Apartment;
import beans.Gender;
import beans.Reservation;
import beans.ReservationStatus;
import beans.Role;
import beans.User;
import config.PathConfig;

@Path("/reservation")
public class ReservationService {

	@Context
	ServletContext ctx;

	@Context
	HttpServletRequest request;

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllReservations() throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		if (loggedUser.getRole() != Role.ADMIN)
			return Response.status(Response.Status.FORBIDDEN).build();

		ArrayList<Reservation> reservations = readReservations();
		return Response.status(Response.Status.OK).entity(reservations).build();
	}
	

	@PUT
	@Path("/odustani/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response odustani(@PathParam("id") String id, @Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		ArrayList<Reservation> reservations = readReservations();
		Reservation r = null;
		for (Reservation reservation : reservations) {
			if (reservation.getId().equals(id)) {
				r = reservation;
			}
		}
		r.setStatus(ReservationStatus.CANCELED);
		writeReservations(reservations);
		return Response.status(200).build();
	}
	
	@Path("/myReservations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response myReservations() throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
	

		ArrayList<Reservation> reservations = readReservations();
		ArrayList<Reservation> reservations1 = new ArrayList<Reservation>();
		for (Reservation reservation : reservations) {
			if(reservation.getGuestId().equals(loggedUser.getId()))
			{
			reservations1.add(reservation);
			}
		}
		return Response.status(Response.Status.OK).entity(reservations1).build();
	}
	
	@Path("/add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addReservation(Reservation a) throws JsonParseException, JsonMappingException, IOException {
	
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		a.setGuestId(loggedUser.getId());
		a.setStatus(ReservationStatus.CREATED);

		ArrayList<Reservation> reservations = readReservations();
		
		
		reservations.add(a);
		writeReservations(reservations);
		return Response.status(Response.Status.OK).entity(a).build();
	}



	
	//@SuppressWarnings("unlikely-arg-type")
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response searchReservation(@QueryParam("username")String username, @QueryParam("status")String status) throws JsonParseException, JsonMappingException, NoSuchAlgorithmException, IOException {
		ArrayList<Reservation> reservations = readReservations();
	
		if(username != "" && username != null) {
			User user = getUserByusername(username);
			if(user==null) {
				System.out.println("user is null");
			}
			reservations = reservations.stream()
					.filter(reservation -> reservation.getGuestId().equals(user.getId()))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		if(status != "" && status != null) {
			reservations = reservations.stream()
					.filter(reservation -> reservation.getStatus().toString().equals(status))
					.collect(Collectors.toCollection(ArrayList::new));
		}
		return Response.status(Response.Status.OK).entity(reservations).build();
	}
	
	public User getUserByusername(String username)
			throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {
		ArrayList<User> users = readUsers();
		System.out.println("DUZINA: " +  users.size());
		for (User u : users ){
			if (u.getUsername().contentEquals(username))
				return u;
		}
		return null;
	}

	private ArrayList<Reservation> readReservations() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File reservationsFile = new File(ctx.getRealPath(".") + PathConfig.RESERVATIONS_FILE);
		boolean created = reservationsFile.createNewFile();
		if (created)
			mapper.writeValue(reservationsFile, new ArrayList<Reservation>());
		return mapper.readValue(reservationsFile, new TypeReference<ArrayList<Reservation>>() {
		});
	}

	private void writeReservations(ArrayList<Reservation> reservations) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File reservationsFile = new File(ctx.getRealPath(".") + PathConfig.RESERVATIONS_FILE);
		reservationsFile.createNewFile();
		mapper.writeValue(reservationsFile, reservations);
	}

	private Apartment getApartmentById(String id) throws JsonParseException, JsonMappingException, IOException {
		ArrayList<Apartment> apartments = readApartments();
		for (Apartment apartment : apartments) {
			if (apartment.getId().equals(id))
				return apartment;
		}
		return null;
	}

	private ArrayList<Apartment> readApartments() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File apartmentsFile = new File(ctx.getRealPath(".") + PathConfig.APARTMENTS_FILE);
		boolean created = apartmentsFile.createNewFile();
		if (created)
			mapper.writeValue(apartmentsFile, new ArrayList<Apartment>());
		return mapper.readValue(apartmentsFile, new TypeReference<ArrayList<Apartment>>() {
		});
	}

	private void writeApartments(ArrayList<Apartment> apartments) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File apartmentsFile = new File(ctx.getRealPath(".") + PathConfig.APARTMENTS_FILE);
		apartmentsFile.createNewFile();
		mapper.writeValue(apartmentsFile, apartments);
	}

	private ArrayList<Date> readHolidays() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File holidaysFile = new File(ctx.getRealPath(".") + PathConfig.HOLIDAYS_FILE);
		boolean created = holidaysFile.createNewFile();
		if (created)
			mapper.writeValue(holidaysFile, new ArrayList<Date>());
		return mapper.readValue(holidaysFile, new TypeReference<ArrayList<Date>>() {
		});
	}
	
	private ArrayList<User> readUsers() throws IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper();
		File userFile = new File(ctx.getRealPath(".") + PathConfig.USERS_FILE);
		boolean created = userFile.createNewFile();
		if (created) {
			ArrayList<User> admins = new ArrayList<User>();
			admins.add(new User("admin", "admin", "Admin FirstName", "Admin LastName", Gender.MALE, Role.ADMIN));
			mapper.writeValue(userFile, admins);
		}

		return mapper.readValue(userFile, new TypeReference<ArrayList<User>>() {
		});
	}

}
