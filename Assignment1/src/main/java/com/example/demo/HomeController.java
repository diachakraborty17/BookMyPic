package com.example.demo;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;


@Controller
public class HomeController {
	
	//code not required
	@GetMapping(value="/")
	public ModelAndView renderPage() {
	ModelAndView indexPage = new ModelAndView();
	indexPage.setViewName("index");
	return indexPage;
	}
	
	//getting facebook index page
	@GetMapping(value="/facebook")
	public ModelAndView renderFacebook() {
	ModelAndView facebookIndex = new ModelAndView();
	facebookIndex.setViewName("facebookIndex");
	return facebookIndex;
	}
	
	@PostMapping(value="/facebookRedirect")
	public ModelAndView handleRedirect(
			@RequestParam(name="myId") String myId,
			@RequestParam(name="myName") String myName,
			@RequestParam(name="myFriends") String myFriends,
			@RequestParam(name="myEmail") String myEmail,
			HttpServletRequest req
			) {
		System.out.println(myId+myName+myEmail+myFriends);
		String[] splitted = myFriends.split("/");
		for(int i = 0;i<splitted.length;i++) {
			System.out.println(i+" : " +splitted[i]);
		}
		return new ModelAndView("allProfiles");
	}
	
	@PostMapping(value="/profilePage")
	public ModelAndView uploadToS3(
			@RequestParam("file") MultipartFile image) {
		
		ModelAndView profilePage = new ModelAndView();
		BasicAWSCredentials cred = new BasicAWSCredentials(
				"AKIAJ7EO3MTEBGPCIAOQ",
				"yZb/9i6DPyowCWxXV71l/6krqMNUcqkWe6tfWc0D");
		
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(cred))
				.withRegion(Regions.US_EAST_2)
				.build();
		
		try {
			PutObjectRequest putReq = new PutObjectRequest(
					"seassignment2", 
					 image.getOriginalFilename(), 
					 image.getInputStream(),
					 new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead);
		
			s3client.putObject(putReq);
			String imgSrc = "http://"+"seassignment2"+".s3.amazonaws.com/"+image.getOriginalFilename();
			profilePage.addObject("imgSrc", imgSrc); 
			profilePage.setViewName("profilePage");
			return profilePage;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			profilePage.setViewName("error");
			return profilePage;
			
		}
		
		
	}
	
	
} 
