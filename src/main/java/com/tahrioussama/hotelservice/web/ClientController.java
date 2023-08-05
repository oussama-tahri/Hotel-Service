package com.tahrioussama.hotelservice.web;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.exceptions.ClientNotFoundException;
import com.tahrioussama.hotelservice.exceptions.EmailAlreadyExistsException;
import com.tahrioussama.hotelservice.service.ClientServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@AllArgsConstructor
@CrossOrigin("*")
public class ClientController {

    private ClientServiceImpl clientService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ClientDTO registerClient(@RequestBody ClientDTO clientDTO) throws EmailAlreadyExistsException {
        return clientService.registerClient(clientDTO);
    }

    @PostMapping("/authenticate")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ClientDTO authenticateClient(@RequestParam String email, @RequestParam String password) throws ClientNotFoundException {
        return clientService.authenticateClient(email, password);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ClientDTO updateClient(@RequestBody ClientDTO clientDTO) {
        return clientService.updateClient(clientDTO);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public ClientDTO getClientByMail(@PathVariable String email) throws ClientNotFoundException {
        return clientService.getClientByMail(email);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteClientById(@PathVariable Long id) throws ClientNotFoundException {
        clientService.deleteClientById(id);
    }
}
