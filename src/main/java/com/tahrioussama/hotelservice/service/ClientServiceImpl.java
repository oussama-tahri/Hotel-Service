package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.dto.RoomDTO;
import com.tahrioussama.hotelservice.entities.Client;
import com.tahrioussama.hotelservice.entities.Room;
import com.tahrioussama.hotelservice.exceptions.ClientNotFoundException;
import com.tahrioussama.hotelservice.exceptions.EmailAlreadyExistsException;
import com.tahrioussama.hotelservice.exceptions.RoomNotFoundException;
import com.tahrioussama.hotelservice.mappers.ClientMapper;
import com.tahrioussama.hotelservice.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ClientServiceImpl implements IClientService {

    private ClientRepository clientRepository;
    private ClientMapper clientMapper;

    @Override
    public ClientDTO registerClient(ClientDTO clientDTO) throws EmailAlreadyExistsException {
        log.info("Saving new Client");
        // Validate email uniqueness before registering the client
        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + clientDTO.getEmail());
        }
        Client client = clientMapper.fromClientDTO(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.fromClient(savedClient);
    }

    @Override
    public ClientDTO authenticateClient(String email, String password) throws ClientNotFoundException {
        // Retrieve the client by email
        Client client = clientRepository.findByEmail(email);

        if (client != null && client.getPassword() != null && client.getPassword().equals(password)) {
            // Convert Client entity to ClientDTO using the ClientMapper
            return clientMapper.fromClient(client);
        } else {
            throw new ClientNotFoundException("Client with email " + email + " not found or invalid credentials.");
        }
    }


    @Override
    public ClientDTO updateClient(ClientDTO clientDTO) {
        log.info("Updating Client");
        Client client = clientMapper.fromClientDTO(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.fromClient(savedClient);
    }

    @Override
    public ClientDTO getClientByMail(String email) throws ClientNotFoundException {
        // Retrieve the Client entity from the database by client email
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            throw new ClientNotFoundException("Client with email " + email + " not found.");
        }

        // Now, we need to check if the retrieved client actually has the same email as requested
        if (!client.getEmail().equals(email)) {
            throw new ClientNotFoundException("Client with email " + email + " not found.");
        }

        // Convert Client entity to ClientDTO using the ClientMapper
        return clientMapper.fromClient(client);
    }

    @Override
    public List<ClientDTO> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        List<ClientDTO> clientDTOS = clients.stream()
                .map(clientMapper::fromClient)
                .collect(Collectors.toList());
        return clientDTOS;
    }

    @Override
    public void deleteClientById(Long id) throws ClientNotFoundException {
        // Check if the client with the given ID exists in the database
        if(!clientRepository.existsById(id)) {
            throw new ClientNotFoundException("Client with ID: "+id+" Not Found!");
        }

        // Retrieve the client from the database by reservation ID
        Optional<Client> OptionalClient = clientRepository.findById(id);

        // Check if the reservation exists
        if (OptionalClient.isEmpty()) {
            throw new ClientNotFoundException("Room with ID: "+id+" Not Found!");
        }

        // Convert the optional client to Client entity
        Client client = OptionalClient.get();

        // Convert the Client entity to ClientDTO using the clientMapper
        ClientDTO clientDTO = clientMapper.fromClient(client);

        clientRepository.delete(client);
    }
}
