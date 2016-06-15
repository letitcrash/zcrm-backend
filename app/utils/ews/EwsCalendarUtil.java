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

public class EwsCalendarUtil {

    public List<CalendarItem> findAppointments(ExchangeService service, Timestamp startDate, Timestamp endDate) throws Exception {
        List<CalendarItem> result = new ArrayList<>();

        CalendarFolder cf= CalendarFolder.bind(service, WellKnownFolderName.Calendar);
        FindItemsResults<Appointment> findResults = cf.findAppointments(new CalendarView(startDate, endDate));
        for (Appointment appt : findResults.getItems()) {
            CalendarItem item = new CalendarItem(appt.getSubject(), appt.getBody().toString(),
                                                                                        new Timestamp(appt.getStart().getTime()),
                                                                                        new Timestamp(appt.getEnd().getTime()));
            result.add(item);
        }
        return result;
    }
}
