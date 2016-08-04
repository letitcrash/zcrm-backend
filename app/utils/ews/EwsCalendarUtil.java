package utils.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import models.CalendarItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class EwsCalendarUtil {

    public List<CalendarItem> findAppointments(ExchangeService service, Long startDate, Long endDate) throws Exception {
        List<CalendarItem> result = new ArrayList<>();

        CalendarFolder cf= CalendarFolder.bind(service, WellKnownFolderName.Calendar);
        FindItemsResults<Appointment> findResults = cf.findAppointments(new CalendarView(new Date(TimeUnit.SECONDS.toMillis(startDate)), 
                                                                                         new Date(TimeUnit.SECONDS.toMillis(endDate))));
        for (Appointment appt : findResults.getItems()) {
            CalendarItem item = new CalendarItem(appt.getSubject(), 
                                                 new Timestamp(appt.getStart().getTime()),
                                                 new Timestamp(appt.getEnd().getTime()));
            result.add(item);
        }
        return result;
    }
}
