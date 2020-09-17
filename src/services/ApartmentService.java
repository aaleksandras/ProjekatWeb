package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.SWITCH;

import beans.Amenity;
import beans.Apartment;
import beans.ApartmentType;
import beans.Comment;
import beans.Reservation;
import beans.ReservationStatus;
import beans.Role;
import beans.User;
import config.PathConfig;


@Path("/apartment")
public class ApartmentService {

	@Context
	ServletContext ctx;

	@Context
	HttpServletRequest request;

	@Path("/test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "TESTT";
	}
	
	@Path("")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addAppartment(Apartment a) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("juhuu");
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		a.setHostId(loggedUser.getId());
		a.setActive(true);

		ArrayList<Apartment> apartments = readApartments();
		System.out.println(a.getCheckIn());
		
		
		apartments.add(a);
		writeApartments(apartments);
		return Response.status(Response.Status.OK).entity(a).build();
	}

	@Path("")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllApartments() throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		if (loggedUser.getRole() != Role.ADMIN)
			return Response.status(Response.Status.FORBIDDEN).build();
		ArrayList<Apartment> apartments = readApartments();
		return Response.status(Response.Status.OK).entity(apartments).build();
	}
	
	@Path("/myAppa")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMyApartments() throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
	
		ArrayList<Apartment> apartments = readApartments();
		ArrayList<Apartment> apartmentss = new ArrayList<Apartment>();
		for (Apartment apartment : apartments) {
			if(apartment.getHostId().equals(loggedUser.getId()) && apartment.isActive()) {
				apartmentss.add(apartment);
			}
		}
		return Response.status(Response.Status.OK).entity(apartmentss).build();
	}
	
	
	
	
	@Path("/getAll")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApartments() throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
	
		ArrayList<Apartment> apartments = readApartments();
		
		ArrayList<Apartment> apartmentss = readApartments();
		for (Apartment apartment : apartments) {
			if(apartment.isActive()) {
				apartmentss.add(apartment);
			}
		}
		
		return Response.status(Response.Status.OK).entity(apartmentss).build();
	}
	
	@PUT
	@Path("/obrisi/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obrsi(@PathParam("id") String id, @Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		
		ArrayList<Apartment> apartments = readApartments();
		Apartment r = null;
		for (Apartment apartment : apartments) {
			if (apartment.getId().equals(id)) {
				r = apartment;
			}
		}
		r.setActive(false);
		writeApartments(apartments);
		return Response.status(200).build();
	}
	
	@Path("/getDates/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDates(@PathParam("id") String id) throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		System.out.println("ziv samm");
		ArrayList<Apartment> apartments = readApartments();
		Apartment pom;
		ArrayList<Date> dates = new ArrayList<Date>();
		Date eee= new Date("2020-2-2");
		dates.add(eee);
		for (Apartment apartment : apartments) {
			if (apartment.getId().equals(id)) {
				dates = apartment.getDates();
			}
		}
		
		for (Date d : dates) {
			System.out.println(d);

		}
		System.out.println(id+"sd");
		return Response.status(Response.Status.OK).entity(dates).build();
	}

	

	@Path("/{id}/comments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getComments(@PathParam("id") String id)
			throws JsonParseException, JsonMappingException, IOException {
		Apartment apartment = getApartmentById(id);
		if (apartment == null)
			return Response.status(Response.Status.NOT_FOUND).build();
		ArrayList<Comment> comments = getCommentByApartmentId(id);
		return Response.status(Response.Status.OK).entity(comments).build();
	}


	@Path("remove/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeApartment(@PathParam("id") String id)
			throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		if (loggedUser.getRole() == Role.GUEST)
			return Response.status(Response.Status.FORBIDDEN).build();
		ArrayList<Apartment> apartments = readApartments();
		for (Apartment a : apartments) {
			if (a.getId().equals(id)) {
				a.setActive(false);
				writeApartments(apartments);
				return Response.status(Response.Status.OK).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	

	@Path("edit/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editApartment(@PathParam("id") String id, Apartment apartment)
			throws JsonParseException, JsonMappingException, IOException {

		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		if (loggedUser.getRole() == Role.GUEST)
			return Response.status(Response.Status.FORBIDDEN).build();
		ArrayList<Apartment> apartments = readApartments();
		for (Apartment a : apartments) {
			if (a.getId().equals(id)) {
				a.setApartmentType(apartment.getApartmentType());
				a.setRooms(apartment.getRooms());
				a.setGuests(apartment.getGuests());
				a.setPrice(apartment.getPrice());
				a.setAmenitiesId(apartment.getAmenitiesId());
				writeApartments(apartments);
				return Response.status(Response.Status.OK).build();
			}
		}

		return Response.status(Response.Status.NOT_FOUND).build();
	}

	



	@Path("/{id}/image")
	@POST
	public Response uploadImage(InputStream in, @HeaderParam("Content-Type") String fileType,
			@HeaderParam("Content-Length") long fileSize, @PathParam("id") String apartmentId) throws IOException {
		String fileName = UUID.randomUUID().toString();
		if (fileType.equals("image/jpeg")) {
			fileName += ".jpg";
		} else {
			fileName += ".png";
		}
		System.out.println(apartmentId);
		java.nio.file.Path BASE_DIR = Paths.get(ctx.getRealPath(".") + PathConfig.APARTMENT_IMAGES);
		
		File directory = new File(ctx.getRealPath(".") + PathConfig.APARTMENT_IMAGES);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
		
		Files.copy(in, BASE_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

		ArrayList<Apartment> apartments = readApartments();
		for (Apartment apartment : apartments) {
			if (apartment.getId().equals(apartmentId)) {
				apartment.getImages().add(fileName);
				writeApartments(apartments);
				return Response.status(Response.Status.OK).entity(in).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@Path("/{id}/image")
	@GET
	@Produces({ "image/jpeg" })
	public FileInputStream getImage(@PathParam("id") String id) throws JsonParseException, JsonMappingException, IOException {
		
		Apartment apartment = getApartmentById(id);
	
		for (String image : apartment.getImages()) {
			FileInputStream fileInputStream = new FileInputStream(
					new File(ctx.getRealPath(".") + PathConfig.APARTMENT_IMAGES + File.separator + image));
			System.out.println(fileInputStream);
			return fileInputStream;
			
		}
		return null;
	}
	
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApartmentByID(@PathParam("id") String id) throws JsonParseException, JsonMappingException, IOException {
		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.UNAUTHORIZED).build();
		Apartment apartment = getApartmentById(id);
		return Response.status(Response.Status.OK).entity(apartment).build();
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

	private ArrayList<Comment> readComments() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File commentsFile = new File(ctx.getRealPath(".") + PathConfig.COMMENTS_FILE);
		boolean created = commentsFile.createNewFile();
		if (created)
			mapper.writeValue(commentsFile, new ArrayList<Comment>());
		return mapper.readValue(commentsFile, new TypeReference<ArrayList<Comment>>() {
		});
	}

	private ArrayList<Comment> getCommentByApartmentId(String apartmentId)
			throws JsonParseException, JsonMappingException, IOException {
		Apartment apartment = getApartmentById(apartmentId);
		ArrayList<Comment> comments = readComments();
		comments = comments.stream()
				.filter(comment -> apartment.getCommentsId().stream().anyMatch(id -> id == comment.getId()))
				.collect(Collectors.toCollection(ArrayList::new));
		return comments;
	}

	private ArrayList<Amenity> readAmenities() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		File amenitiesFile = new File(ctx.getRealPath(".") + PathConfig.AMENITIES_FILE);
		boolean created = amenitiesFile.createNewFile();
		if (created)
			mapper.writeValue(amenitiesFile, new ArrayList<Comment>());
		return mapper.readValue(amenitiesFile, new TypeReference<ArrayList<Amenity>>() {
		});
	}

}
