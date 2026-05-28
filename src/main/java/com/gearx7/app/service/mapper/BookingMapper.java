package com.gearx7.app.service.mapper;

import com.gearx7.app.domain.Booking;
import com.gearx7.app.service.dto.BookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { MachineMapper.class })
public interface BookingMapper {
    //    @Mapping(target = "userId", source = "user.id")
    //    @Mapping(target = "customerName", expression = "java(booking.getUser() != null ? booking.getUser().getFirstName() + \" \" + booking.getUser().getLastName() : null)")
    //    @Mapping(target = "customerPhone", source = "user.phone")
    //    BookingDTO toDto(Booking booking);

    BookingDTO toDto(Booking booking);
}
