package com.tahrioussama.hotelservice.service;

import com.tahrioussama.hotelservice.dto.ClientDTO;
import com.tahrioussama.hotelservice.exceptions.ClientNotFoundException;
import com.tahrioussama.hotelservice.exceptions.EmailAlreadyExistsException;

import java.util.List;

public interface IClientService {

    ClientDTO registerClient(ClientDTO clientDTO) throws EmailAlreadyExistsException;
    ClientDTO authenticateClient(String email, String password) throws ClientNotFoundException;
    ClientDTO updateClient(ClientDTO client);
    ClientDTO getClientByMail(String email) throws ClientNotFoundException;
    List<ClientDTO> getAllClients();
    void deleteClientById(Long id) throws ClientNotFoundException;
}
