package space.exploration.spice.service.controller;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import space.exploration.spice.utilities.PositionUtils;
import space.exploration.spice.utilities.TimeUtils;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(path = "/msl")
public class SpiceController {
    public static final String            CLOCK_FORMAT      = "yyyy-MM-dd~HH:mm:ss";
    private             TimeUtils         clockService      = new TimeUtils();
    private             PositionUtils     positionUtils     = new PositionUtils();
    private             DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(CLOCK_FORMAT);
    private             Logger            logger            = LoggerFactory.getLogger(SpiceController.class);

    @GetMapping(path = "/sclk")
    public @ResponseBody
    String getSpaceCraftClock(@RequestParam long timestamp) {

        logger.info("In getSclk - given timestamp = " + timestamp + " clock value = " + dateTimeFormatter.print(timestamp));
        clockService.updateClock(dateTimeFormatter.print(timestamp));

        space.exploration.communications.protocol.spacecraftClock.SpacecraftClock.SclkPacket.Builder sBuilder = space
                .exploration.communications.protocol.spacecraftClock.SpacecraftClock.SclkPacket.newBuilder();
        sBuilder.setMissionDurationMS(TimeUnit.DAYS.toMillis(365l));
        sBuilder.setSclkValue(clockService.getSclkTime());
        sBuilder.setStartTime("2016-01-01~00:00:00");
        sBuilder.setUtcTime(clockService.getUtcTime());
        sBuilder.setClockFile(clockService.getClockFile().getPath());
        sBuilder.setApplicableTimeFrame(clockService.getApplicableTimeFrame());
        sBuilder.setEphemerisTime(clockService.getEphemerisTime());
        sBuilder.setCalendarTime(clockService.getCalendarTime());
        sBuilder.setSol(clockService.getSol());

        return sBuilder.build().toString();
    }

    @GetMapping(path = "/position")
    public @ResponseBody
    String getMslPosition(@RequestParam long timestamp) {
        logger.info("In position - given timestamp = " + timestamp + " clock value = " + dateTimeFormatter.print(timestamp));
        positionUtils.setUtcTime(dateTimeFormatter.print(timestamp));
        return positionUtils.getPositionPacket().toString();
    }
}
