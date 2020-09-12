package services;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Gender;
import beans.Role;
import beans.User;
import config.PathConfig;

@Path("/user")
public class UserService {

	@Context
	ServletContext ctx;

	@Context
	HttpServletRequest request;

	@Path(value = "")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user)
			throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {

		ArrayList<User> users = readUsers();
		int ind = 0;
		for (User u : users) {
			if (u.getUsername().equals(user.getUsername())) {
				ind = 1;
				break;
			}
		}
		if (ind == 0) {
			User u = new User(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
					user.getGender(), user.getRole());
			users.add(u);
			writeUsers(users);
			request.getSession().setAttribute("loggedUser", u);
			return Response.status(Response.Status.OK).entity(u).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) throws IOException, NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(user.getPassword().getBytes());
		String encodedPassword = new String(messageDigest.digest());

		ArrayList<User> users = readUsers();
		for (User u : users) {
			if (u.getUsername().equals(user.getUsername()) && u.getPassword().equals(encodedPassword) && u.isActive()) {
				request.getSession().setAttribute("loggedUser", u);
				return Response.status(Response.Status.OK).entity(u).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@Path("/logout")
	@POST
	public Response logout() {
		request.getSession().invalidate();
		return Response.status(Response.Status.OK).build();
	}
	
	@Path("")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProfile(User user)
			throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(user.getPassword().getBytes());
		String encodedPassword = new String(messageDigest.digest());

		User loggedUser = (User) request.getSession().getAttribute("loggedUser");
		if (loggedUser == null)
			return Response.status(Response.Status.FORBIDDEN).build();
		if (!loggedUser.getUsername().equals(user.getUsername()))
			return Response.status(Response.Status.UNAUTHORIZED).build();

		ArrayList<User> users = readUsers();
		for (User u : users) {
			if (u.getId().equals(user.getId())) {
				u.setPassword(encodedPassword);
				u.setFirstName(user.getFirstName());
				u.setLastName(user.getLastName());
				u.setUsername(user.getUsername());
				writeUsers(users);
				request.getSession().setAttribute("loggedUser", u);
				return Response.status(Response.Status.OK).entity(u).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	private ArrayList<User> readUsers() throws IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(ctx.getRealPath("."));
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

	private void writeUsers(ArrayList<User> users) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		File userFile = new File(ctx.getRealPath(".") + PathConfig.USERS_FILE);
		userFile.createNewFile();
		mapper.writeValue(userFile, users);
	}

}
