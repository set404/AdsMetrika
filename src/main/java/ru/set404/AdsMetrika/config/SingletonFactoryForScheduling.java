package ru.set404.AdsMetrika.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.set404.AdsMetrika.network.ads.ExoClick;
import ru.set404.AdsMetrika.network.ads.TrafficFactory;
import ru.set404.AdsMetrika.network.cpa.Adcombo;
import ru.set404.AdsMetrika.repositories.CredentialsRepository;
import ru.set404.AdsMetrika.scheduled.googlesheets.GoogleAuthorizeConfig;
import ru.set404.AdsMetrika.scheduled.googlesheets.SpreadSheet;
import ru.set404.AdsMetrika.scheduled.telegram.TelegramBot;
import ru.set404.AdsMetrika.services.NetworksService;
import ru.set404.AdsMetrika.services.ScheduledService;
import ru.set404.AdsMetrika.services.SettingsService;

@Component
public class SingletonFactoryForScheduling {
    private final ObjectMapper objectMapper;
    private final CredentialsRepository credentialsRepository;
    private final SettingsService settingsService;
    private final TelegramBot telegramBot;
    @Value("${google.application.name}")
    private String applicationName;
    @Value("${google.credentials.file.path}")
    private String credentialsFilePath;
    @Value("${google.tokens.directory.path}")
    private String tokensDirectoryPath;
    @Value("${google.redirect-uri}")
    private String redirectUri;

    private NetworksService networksService;
    private ScheduledService scheduledService;

    @Autowired
    public SingletonFactoryForScheduling(ObjectMapper objectMapper, CredentialsRepository credentialsRepository, SettingsService settingsService, TelegramBot telegramBot) {
        this.objectMapper = objectMapper;
        this.credentialsRepository = credentialsRepository;
        this.settingsService = settingsService;
        this.telegramBot = telegramBot;
    }

    private TrafficFactory getTrafficFactory() {
        return new TrafficFactory(objectMapper);
    }

    private ExoClick getExoClick() {
        return new ExoClick(objectMapper);
    }

    private Adcombo getAdcombo() {
        return new Adcombo(objectMapper);
    }

    private GoogleAuthorizeConfig getGoogleAuthConfig() {
        GoogleAuthorizeConfig authorizeConfig = new GoogleAuthorizeConfig();
        authorizeConfig.setApplicationName(applicationName);
        authorizeConfig.setRedirectUri(redirectUri);
        authorizeConfig.setCredentialsFilePath(credentialsFilePath);
        authorizeConfig.setTokensDirectoryPath(tokensDirectoryPath);
        return authorizeConfig;
    }

    private SpreadSheet getSpreadSheet() {
        return new SpreadSheet(getGoogleAuthConfig());
    }

    public NetworksService getNetworksServiceSingleton() {
        if (networksService == null)
            networksService = new NetworksService(getExoClick(), getTrafficFactory(), getAdcombo(), credentialsRepository);
        return networksService;
    }

    public ScheduledService getScheduledServiceSingleton() {
        if (scheduledService == null)
            scheduledService = new ScheduledService(getSpreadSheet(), telegramBot, settingsService);
        return scheduledService;
    }
}
