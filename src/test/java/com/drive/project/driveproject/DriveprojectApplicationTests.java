package com.drive.project.driveproject;

import Controller.HomePageController;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DriveprojectApplicationTests {
	private static final String USER_iDENTIFIER_KEY = "MY_DUMMY_USER";
	private static final HttpTransport HTTP_TRANSPORT = new MockHttpTransport();
	private static final JsonFactory JSON_FACTORY = new MockJsonFactory();
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private Resource credentialsFolder;
	HomePageController home;
	public String homePage() throws IOException {
		boolean isUserAuthenticated = false;



		GoogleClientSecrets secrets = new GoogleClientSecrets();

		GoogleAuthorizationCodeFlow flow =new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();

		Credential credential = flow.loadCredential(USER_iDENTIFIER_KEY);
		if(credential != null){
			boolean tokenValid = credential.refreshToken();
			if(tokenValid){
				isUserAuthenticated = true;
			}
		}
		return isUserAuthenticated?"dashboard.html":"index.html";
	}
	public @ResponseBody
	List<FileItemDTO> listFiles() throws IOException {

		GoogleClientSecrets secrets = new GoogleClientSecrets();

		GoogleAuthorizationCodeFlow flow =new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
		Credential cred = flow.loadCredential(USER_iDENTIFIER_KEY);

		Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("googledrivespringbootexample").build();

		List<FileItemDTO> responseList = new ArrayList<>();

		FileList fileList = drive.files().list().setFields("files(id,name)").execute();
		for (File file: fileList.getFiles()){
			FileItemDTO item = new FileItemDTO();
			item.setId(file.getId());
			item.setName(file.getName());
			responseList.add(item);
		}
		return responseList;
	}
	@Test
	void contextLoads() throws IOException {
		assertThat(Boolean.parseBoolean(homePage()));
//		assertThat(Boolean.parseBoolean(home.saveAuthorizationCode()));

	}


}
