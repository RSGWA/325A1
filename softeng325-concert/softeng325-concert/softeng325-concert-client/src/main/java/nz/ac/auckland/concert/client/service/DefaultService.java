package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import nz.ac.auckland.concert.service.util.Config;
import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.util.TheatreUtility;


public class DefaultService implements ConcertService, ConcertService.NewsItemListener {

	private static Logger _logger = LoggerFactory
			.getLogger(DefaultService.class);
	
	private AtomicLong _reservationIdCounter = new AtomicLong();
	
	// Set of cookie values returned by the Web service.
	private static Set<String> _cookieValues = new HashSet<String>();
	
	private static String CONCERT_SERVICE_URI = "http://localhost:10000/services/concerts";
	private static String USER_SERVICE_URI = "http://localhost:10000/services/users";
	private static String PERFORMER_SERVICE_URI = "http://localhost:10000/services/performers";
	private static String RESERVATION_SERVICE_URI = "http://localhost:10000/services/reservations";
	private static String NEWS_SERVICE_URI = "http://localhost:10000/services/news";
	
	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
	private static final String AWS_SECRET_ACCESS_KEY = "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";

	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";
	
	// Download directory - a directory named "images" in the user's home
	// directory.
	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	private static final String USER_DIRECTORY = System
			.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
			+ FILE_SEPARATOR + "images";
	
	private Client _client = ClientBuilder.newClient();
	
	private NewsItemListener _listener;
;
	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		Builder builder = _client.target(CONCERT_SERVICE_URI).request();
		Response response = null;
		try {
			response = builder.get();
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		List<ConcertDTO> concertList = response.readEntity(new GenericType<List<ConcertDTO>>(){});
		
		Set<ConcertDTO> concertSet = new HashSet<>(concertList);
		response.close();
		return concertSet;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		Builder builder = _client.target(PERFORMER_SERVICE_URI).request();
		Response response = null;
		try {
			response = builder.get();
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		List<PerformerDTO> list = response.readEntity(new GenericType<List<PerformerDTO>>(){});
		Set<PerformerDTO> set = new HashSet<>(list);
		response.close();
		return set;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		Builder builder = _client.target(USER_SERVICE_URI).request();
		Response response = null;
		try {
			response = builder.post(Entity.entity(newUser,MediaType.APPLICATION_XML));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 400: 
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		UserDTO fullUser = (UserDTO) response.getEntity();
		processCookieFromResponse(response);
		response.close();
		return fullUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		Builder builder = _client.target(USER_SERVICE_URI+"/authenticate").request();
		Response response = null;
		try {
			response = builder.post(Entity.entity(user,MediaType.APPLICATION_XML));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 400: 
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		UserDTO fullUser = response.readEntity(UserDTO.class);
		response.close();
		return fullUser;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		
		// Create download directory if it doesn't already exist.
		File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
		downloadDirectory.mkdir();
		
		// Create an AmazonS3 object that represents a connection with the
		// remote S3 service.
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
			AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		AmazonS3 s3 = AmazonS3ClientBuilder
			.standard()
			.withRegion(Regions.AP_SOUTHEAST_2)
			.withCredentials(
				new AWSStaticCredentialsProvider(awsCredentials))
			.build();
		
		String imageName = performer.getImageName();
		
		// Download Image
		download(s3, imageName);
		
		// Read Image from download file
		try {
		    File pathToFile = new File(DOWNLOAD_DIRECTORY+"/"+imageName);
		    Image image = ImageIO.read(pathToFile);
		    if (image == null) {
		    	throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		    }
		    	return image;
		} catch (IOException ex) {
		    throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
	}

	/**
	 * Downloads images in the bucket named AWS_BUCKET.
	 * 
	 * @param s3 the AmazonS3 connection.
	 * 
	 * @param imageNames the named images to download.
	 * 
	 */
	private void download(AmazonS3 s3, String imageName) {
		File f = new File(DOWNLOAD_DIRECTORY);
		TransferManager mgr = TransferManagerBuilder
				.standard()
				.withS3Client(s3)
				.build();
		mgr.download(AWS_BUCKET, imageName, f);
		mgr.shutdownNow();
	}
	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		
		Set<SeatDTO> seats = TheatreUtility.findAvailableSeats(reservationRequest.getNumberOfSeats(), reservationRequest.getSeatType(), new HashSet<SeatDTO>());
		
		ReservationDTO reservation = new ReservationDTO(_reservationIdCounter.incrementAndGet(), reservationRequest, seats);
		
		return reservation;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		Builder builder = _client.target(USER_SERVICE_URI).request();
		Response response = null;
		try {
			response = builder.put(Entity.entity(creditCard,MediaType.APPLICATION_XML));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 400:
		case 401:
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		response.close();
		
	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		_listener = listener;
	}

	@Override
	public void cancelSubscription() {
		_listener = null;
	}
	

	@Override
	public void newsItemReceived(NewsItemDTO newsItem) {
		Builder builder = _client.target(NEWS_SERVICE_URI).request();
		Response response = null;
		try {
			response = builder.put(Entity.entity(newsItem,MediaType.APPLICATION_XML));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		int responseCode = response.getStatus();
		
		switch (responseCode) {
		case 500:
			throw new ServiceException(response.readEntity(String.class));
		}
		response.close();
	}
	
	/**
	 * Method to extract any cookie from a Response object received from the 
	 * Web service. If there is a cookie named username (Config.TOKEN)
	 * it is added to the _cookieValues set, which stores all cookie values for
	 * username received by the Web service. 
	 */
	private void processCookieFromResponse(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();
		
		if(cookies.containsKey(Config.TOKEN)) {
			String cookieValue = cookies.get(Config.TOKEN).getValue();
			_cookieValues.add(cookieValue);
		}
	}
	
}
