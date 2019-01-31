package com.vattima.lego.sheet.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleSheetsServiceImpl implements GoogleSheetsService {
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

    private final LegoItemSheetProperties legoItemSheetProperties;

    private Credential credential;
    private HttpTransport httpTransport;
    private JsonFactory jsonFactory;

    @PostConstruct
    public void initialize() {
        // Load client secrets.
        InputStream in = LegoItemSheetService.class.getResourceAsStream(legoItemSheetProperties.getClientSecretDir());
        GoogleClientSecrets clientSecrets;
        FileDataStoreFactory dataStoreFactory;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(new File(legoItemSheetProperties.getDataStoreDir()));
            jsonFactory = JacksonFactory.getDefaultInstance();
            clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            httpTransport, jsonFactory, clientSecrets, SCOPES)
                            .setDataStoreFactory(dataStoreFactory)
                            .setAccessType("offline")
                            .build();
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException | GeneralSecurityException e) {
            throw new LegoItemSheetSheetServiceException(e);
        }
    }

    @Override
    public Sheets getSheetsService() {
        // Build a new authorized API client service.
        return new Sheets.Builder(httpTransport, jsonFactory, credential).build();
    }
}
