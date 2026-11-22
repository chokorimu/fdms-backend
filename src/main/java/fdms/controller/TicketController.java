package fdms.controller;

import fdms.dto.Dto.CompensationRequest;
import fdms.dto.Dto.RescheduleRequest;
import fdms.dto.Dto.TicketDto;
import fdms.dto.Dto.TicketOptionsDto;
import fdms.model.Role;
import fdms.model.User;
import fdms.service.AuthService;
import fdms.service.TicketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final AuthService auth;
    private final TicketService tickets;

    public TicketController(AuthService auth, TicketService tickets) {
        this.auth = auth;
        this.tickets = tickets;
    }

    /** Daftar tiket milik penumpang yang sedang login. */
    @GetMapping("/my")
    public List<TicketDto> myTickets(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.myTickets(user);
    }

    /** Opsi untuk tiket terdampak: alternatif, status kompensasi, dll. */
    @GetMapping("/{id}/options")
    public TicketOptionsDto options(@PathVariable String id,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.getOptions(user, id);
    }

    /** Pilihan 1: reschedule ke penerbangan alternatif. */
    @PostMapping("/{id}/reschedule")
    public TicketDto reschedule(@PathVariable String id,
                                @RequestBody RescheduleRequest req,
                                @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.reschedule(user, id, req);
    }

    /** Tidak ada alternatif yang cocok: eskalasi ke Admin. */
    @PostMapping("/{id}/escalate")
    public TicketDto escalate(@PathVariable String id,
                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.escalate(user, id);
    }

    /** Pilihan 2: kompensasi (agree = true untuk menyetujui). */
    @PostMapping("/{id}/compensation")
    public TicketDto compensation(@PathVariable String id,
                                  @RequestBody CompensationRequest req,
                                  @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.compensation(user, id, req);
    }

    /** Pilihan 3: tidak memilih. */
    @PostMapping("/{id}/no-choice")
    public TicketDto noChoice(@PathVariable String id,
                              @RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = auth.requireRole(authorization, Role.PENUMPANG);
        return tickets.noChoice(user, id);
    }
}
