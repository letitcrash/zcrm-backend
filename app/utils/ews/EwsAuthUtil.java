package utils.ews;

import exceptions.BadCredentialsException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
//import play.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class EwsAuthUtil {
    public static  String DEFAULT_EXCHANGE_SERVER = "https://sd-74609.multimedianordic.no/EWS/Exchange.asmx";

    public ExchangeService checkUserLogin(String login, String password) throws BadCredentialsException {
        try {
            ExchangeService service = createService(login, password);
            Folder.bind(service, WellKnownFolderName.Inbox, PropertySet.IdOnly); //Connection test
            return service;
        } catch (Exception e) {
            BadCredentialsException bce = new BadCredentialsException(e.getMessage(), e.getCause());
            bce.setStackTrace(e.getStackTrace());
            throw bce;
        }
    }

    private ExchangeService createService(String login, String password) throws URISyntaxException {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(login, password);
        service.setCredentials(credentials);
        try {
            service.setUrl(new URI(DEFAULT_EXCHANGE_SERVER));
            return service;
        } catch (URISyntaxException e) {
            //Logger.error("BAD SERVER ADDRESS \n Find more: https://docs.oracle.com/javase/8/docs/api/java/net/URI.html \n "+e.getMessage());
            throw e;
        }
    }
}
